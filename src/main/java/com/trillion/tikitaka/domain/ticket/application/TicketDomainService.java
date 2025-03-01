package com.trillion.tikitaka.domain.ticket.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.trillion.tikitaka.domain.category.domain.Category;
import com.trillion.tikitaka.domain.category.infrastructure.CategoryRepository;
import com.trillion.tikitaka.domain.member.domain.Member;
import com.trillion.tikitaka.domain.member.infrastructure.MemberRepository;
import com.trillion.tikitaka.domain.ticket.domain.Ticket;
import com.trillion.tikitaka.domain.ticket.domain.TicketStatus;
import com.trillion.tikitaka.domain.ticket.dto.TicketRequest;
import com.trillion.tikitaka.domain.ticket.dto.TicketUpdateRequestForManager;
import com.trillion.tikitaka.domain.ticket.dto.TicketUpdateRequestForUser;
import com.trillion.tikitaka.domain.ticket.infrastructure.TicketRepository;
import com.trillion.tikitaka.domain.tickettype.domain.TicketType;
import com.trillion.tikitaka.domain.tickettype.infrastructure.TicketTypeRepository;
import com.trillion.tikitaka.global.exception.BusinessException;
import com.trillion.tikitaka.global.exception.ErrorCode;
import com.trillion.tikitaka.global.security.domain.CustomUserDetails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketDomainService {

	private final TicketRepository ticketRepository;
	private final MemberRepository memberRepository;
	private final TicketTypeRepository ticketTypeRepository;
	private final CategoryRepository categoryRepository;

	@Transactional
	public Ticket createTicket(TicketRequest request, CustomUserDetails userDetails) {
		Member manager = null;
		if (request.getManagerId() != null) {
			manager = memberRepository.findById(request.getManagerId()).orElseThrow(() -> {
				log.error("[티켓 생성 실패] 존재하지 않는 담당자 ID: {}", request.getManagerId());
				return new BusinessException(ErrorCode.MANAGER_NOT_FOUND);
			});
		}

		Member requester = memberRepository.findById(userDetails.getId()).orElseThrow(() -> {
			log.error("[티켓 생성 실패] 존재하지 않는 요청자 ID: {}", userDetails.getId());
			return new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
		});

		TicketType ticketType = ticketTypeRepository.findById(request.getTypeId()).orElseThrow(() -> {
			log.error("[티켓 생성 실패] 존재하지 않는 티켓 유형 ID: {}", request.getTypeId());
			return new BusinessException(ErrorCode.TICKET_TYPE_NOT_FOUND);
		});

		Category primaryCategory = null;
		if (request.getFirstCategoryId() != null) {
			primaryCategory = categoryRepository.findById(request.getFirstCategoryId()).orElseThrow(() -> {
				log.error("[티켓 생성 실패] 존재하지 않는 1차 카테고리 ID: {}", request.getFirstCategoryId());
				return new BusinessException(ErrorCode.CATEGORY_NOT_FOUND);
			});
		}

		Category secondaryCategory = null;
		if (request.getSecondCategoryId() != null) {
			secondaryCategory = categoryRepository.findById(request.getSecondCategoryId()).orElseThrow(() -> {
				log.error("[티켓 생성 실패] 존재하지 않는 2차 카테고리 ID: {}", request.getSecondCategoryId());
				return new BusinessException(ErrorCode.CATEGORY_NOT_FOUND);
			});
		}

		// 1차 2차 카테고리 매칭에 대한 유효성 검증
		if (secondaryCategory != null && !secondaryCategory.getPrimaryCategory().equals(primaryCategory)) {
			log.error("[티켓 생성 실패] 1차 2차 카테고리 매칭 오류");
			throw new BusinessException(ErrorCode.CATEGORY_MISMATCH);
		}

		Ticket ticket = Ticket.builder()
			.title(request.getTitle())
			.content(request.getContent())
			.status(TicketStatus.PENDING)
			.priority(null)
			.manager(manager)
			.requester(requester)
			.ticketType(ticketType)
			.primaryCategory(primaryCategory)
			.secondaryCategory(secondaryCategory)
			.urgent(request.getUrgent() != null ? request.getUrgent() : false)
			.deadline(request.getDeadline())
			.build();

		return ticketRepository.save(ticket);
	}

	@Transactional
	public Ticket updateTicketForManager(Long ticketId, TicketUpdateRequestForManager request) {
		Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(() -> {
			log.error("[티켓 수정 실패] 존재하지 않는 티켓 ID: {}", ticketId);
			return new BusinessException(ErrorCode.TICKET_NOT_FOUND);
		});

		if (request.getManagerId().isPresent()) {
			Long newManagerId = request.getManagerId().orElse(null);
			if (newManagerId == null) {
				ticket.updateManager(null);
			} else {
				Member manager = memberRepository.findById(request.getManagerId().get()).orElseThrow(() -> {
					log.error("[티켓 수정 실패] 존재하지 않는 담당자 ID: {}", request.getManagerId().get());
					return new BusinessException(ErrorCode.MANAGER_NOT_FOUND);
				});
				ticket.updateManager(manager);
			}
		}

		if (request.getStatus() != null) {
			ticket.updateStatus(request.getStatus());
		}

		if (request.getPriority().isPresent()) {
			ticket.updatePriority(request.getPriority().orElse(null));
		}

		if (request.getTypeId() != null) {
			TicketType ticketType = ticketTypeRepository.findById(request.getTypeId()).orElseThrow(() -> {
				log.error("[티켓 수정 실패] 존재하지 않는 티켓 유형 ID: {}", request.getTypeId());
				return new BusinessException(ErrorCode.TICKET_TYPE_NOT_FOUND);
			});
			ticket.updateTicketType(ticketType);
		}

		if (request.getPrimaryCategoryId().isPresent()) {
			Long primaryCategoryId = request.getPrimaryCategoryId().orElse(null);
			if (primaryCategoryId == null) {
				ticket.updatePrimaryCategory(null);
			} else {
				Category primaryCategory = categoryRepository.findById(primaryCategoryId)
					.orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
				ticket.updatePrimaryCategory(primaryCategory);
			}
		}

		if (request.getSecondaryCategoryId().isPresent()) {
			Long secondaryCategoryId = request.getSecondaryCategoryId().orElse(null);
			if (secondaryCategoryId == null) {
				ticket.updateSecondaryCategory(null);
			} else {
				Category secondaryCategory = categoryRepository.findById(secondaryCategoryId)
					.orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
				ticket.updateSecondaryCategory(secondaryCategory);
			}
		}

		// 카테고리 매칭 검증: 2차 카테고리가 있는 경우 반드시 해당 2차의 부모가 1차 카테고리와 일치하는지 확인
		if (ticket.getSecondaryCategory() != null) {
			if (ticket.getPrimaryCategory() == null) {
				log.error("[티켓 수정 실패] 2차 카테고리가 존재하는 경우 1차 카테고리는 필수입니다.");
				throw new BusinessException(ErrorCode.CATEGORY_MISMATCH);
			}
			if (ticket.getSecondaryCategory().getPrimaryCategory() == null
				|| !ticket.getSecondaryCategory().getPrimaryCategory().equals(ticket.getPrimaryCategory())) {
				log.error("[티켓 수정 실패] 1차/2차 카테고리 매칭 오류");
				throw new BusinessException(ErrorCode.CATEGORY_MISMATCH);
			}
		}

		return ticket;
	}

	@Transactional
	public Ticket updateTicketForUser(Long ticketId, TicketUpdateRequestForUser request, Long requesterId) {
		Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(() -> {
			log.error("[티켓 수정 실패] 존재하지 않는 티켓 ID: {}", ticketId);
			return new BusinessException(ErrorCode.TICKET_NOT_FOUND);
		});

		if (!ticket.getRequester().getId().equals(requesterId)) {
			log.error("[티켓 수정 실패] 티켓 수정 권한 없음");
			throw new BusinessException(ErrorCode.UNAUTHORIZED);
		}

		if (request.getTitle() != null) {
			ticket.updateTitle(request.getTitle());
		}

		if (request.getContent() != null) {
			ticket.updateContent(request.getContent());
		}

		if (request.getTypeId() != null) {
			TicketType ticketType = ticketTypeRepository.findById(request.getTypeId()).orElseThrow(() -> {
				log.error("[티켓 수정 실패] 존재하지 않는 티켓 유형 ID: {}", request.getTypeId());
				return new BusinessException(ErrorCode.TICKET_TYPE_NOT_FOUND);
			});
			ticket.updateTicketType(ticketType);
		}

		if (request.getPrimaryCategoryId().isPresent()) {
			Long primaryCategoryId = request.getPrimaryCategoryId().orElse(null);
			if (primaryCategoryId == null) {
				ticket.updatePrimaryCategory(null);
			} else {
				Category primaryCategory = categoryRepository.findById(primaryCategoryId)
					.orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
				ticket.updatePrimaryCategory(primaryCategory);
			}
		}

		if (request.getSecondaryCategoryId().isPresent()) {
			Long secondaryCategoryId = request.getSecondaryCategoryId().orElse(null);
			if (secondaryCategoryId == null) {
				ticket.updateSecondaryCategory(null);
			} else {
				Category secondaryCategory = categoryRepository.findById(secondaryCategoryId)
					.orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
				ticket.updateSecondaryCategory(secondaryCategory);
			}
		}

		// 카테고리 매칭 검증: 2차 카테고리가 있는 경우 반드시 해당 2차의 부모가 1차 카테고리와 일치하는지 확인
		if (ticket.getSecondaryCategory() != null) {
			if (ticket.getPrimaryCategory() == null) {
				log.error("[티켓 수정 실패] 2차 카테고리가 존재하는 경우 1차 카테고리는 필수입니다.");
				throw new BusinessException(ErrorCode.CATEGORY_MISMATCH);
			}
			if (ticket.getSecondaryCategory().getPrimaryCategory() == null
				|| !ticket.getSecondaryCategory().getPrimaryCategory().equals(ticket.getPrimaryCategory())) {
				log.error("[티켓 수정 실패] 1차/2차 카테고리 매칭 오류");
				throw new BusinessException(ErrorCode.CATEGORY_MISMATCH);
			}
		}

		if (request.getUrgent() != null) {
			ticket.updateUrgent(request.getUrgent());
		}

		if (request.getDeadline().isPresent()) {
			ticket.updateDeadline(request.getDeadline().orElse(null));
		}

		return ticket;
	}
}
