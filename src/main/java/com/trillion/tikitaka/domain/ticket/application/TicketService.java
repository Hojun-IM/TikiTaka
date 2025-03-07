package com.trillion.tikitaka.domain.ticket.application;

import java.util.List;

import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.trillion.tikitaka.domain.category.domain.Category;
import com.trillion.tikitaka.domain.category.domain.CategoryDomainService;
import com.trillion.tikitaka.domain.category.infrastructure.CategoryRepository;
import com.trillion.tikitaka.domain.member.domain.Member;
import com.trillion.tikitaka.domain.member.domain.Role;
import com.trillion.tikitaka.domain.member.infrastructure.MemberRepository;
import com.trillion.tikitaka.domain.ticket.domain.Ticket;
import com.trillion.tikitaka.domain.ticket.domain.TicketPriority;
import com.trillion.tikitaka.domain.ticket.domain.TicketStatus;
import com.trillion.tikitaka.domain.ticket.dto.TicketListResponseForManager;
import com.trillion.tikitaka.domain.ticket.dto.TicketListResponseForUser;
import com.trillion.tikitaka.domain.ticket.dto.TicketRequest;
import com.trillion.tikitaka.domain.ticket.dto.TicketResponse;
import com.trillion.tikitaka.domain.ticket.dto.TicketUpdateRequestForManager;
import com.trillion.tikitaka.domain.ticket.dto.TicketUpdateRequestForUser;
import com.trillion.tikitaka.domain.ticket.infrastructure.TicketRepository;
import com.trillion.tikitaka.domain.ticket.util.TicketFilter;
import com.trillion.tikitaka.domain.tickettype.domain.TicketType;
import com.trillion.tikitaka.domain.tickettype.infrastructure.TicketTypeRepository;
import com.trillion.tikitaka.global.common.RestPage;
import com.trillion.tikitaka.global.exception.BusinessException;
import com.trillion.tikitaka.global.exception.ErrorCode;
import com.trillion.tikitaka.global.security.domain.CustomUserDetails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TicketService {

	private final CategoryDomainService categoryDomainService;
	private final TicketDomainService ticketDomainService;
	private final MemberRepository memberRepository;
	private final TicketTypeRepository ticketTypeRepository;
	private final CategoryRepository categoryRepository;
	private final TicketRepository ticketRepository;

	@Transactional
	public Long createTicket(TicketRequest request, List<MultipartFile> files, CustomUserDetails userDetails) {
		log.info("[티켓 생성 요청] 제목: {}", request.getTitle());

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

		categoryDomainService.validateHierarchy(primaryCategory, secondaryCategory);

		Ticket ticket = ticketDomainService.createTicket(
			request.getTitle(), request.getContent(), manager, requester, ticketType, primaryCategory,
			secondaryCategory, request.getUrgent(), request.getDeadline()
		);
		ticketRepository.save(ticket);

		if (files != null && !files.isEmpty()) {
			log.info("[티켓 첨부파일 저장]");
			// TODO: 파일 저장 로직
			// fileService.uploadFiles(ticket.getId(), files);
		}

		return ticket.getId();
	}

	public TicketResponse getTicket(Long ticketId, CustomUserDetails userDetails) {
		log.info("[티켓 조회 요청] 티켓 ID: {}", ticketId);
		Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(() -> {
			log.error("[티켓 조회 실패] 존재하지 않는 티켓 ID: {}", ticketId);
			return new BusinessException(ErrorCode.TICKET_NOT_FOUND);
		});

		if (userDetails.getMember().getRole() == Role.USER) {
			if (!ticket.getRequester().getId().equals(userDetails.getId())) {
				log.error("[티켓 조회 실패] 권한 없음");
				throw new BusinessException(ErrorCode.ACCESS_DENIED);
			}
		}

		return ticketRepository.getTicket(ticketId);
	}

	@Cacheable(value = "ticketsForManager", keyGenerator = "ticketFilterKeyGeneratorForManager")
	public RestPage<TicketListResponseForManager> getTicketsForManager(TicketFilter filter) {
		log.info("[담당자 티켓 목록 조회 요청] 필터: {}", filter);
		return new RestPage<>(ticketRepository.getTicketsForManager(filter));
	}

	@Cacheable(value = "ticketsForUser", keyGenerator = "ticketFilterKeyGeneratorForUser")
	public RestPage<TicketListResponseForUser> getTicketsForUser(TicketFilter filter, CustomUserDetails userDetails) {
		log.info("[사용자 티켓 목록 조회 요청] 필터: {}", filter);
		return new RestPage<>(ticketRepository.getTicketsForUser(filter, userDetails.getId()));
	}

	@Transactional
	@CacheEvict(value = "ticketsForManager", allEntries = true)
	public Long updateTicketForManager(Long ticketId, TicketUpdateRequestForManager request) {
		log.info("[티켓 수정 요청] 티켓 ID: {}", ticketId);

		Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(() -> {
			log.error("[티켓 수정 실패] 존재하지 않는 티켓 ID: {}", ticketId);
			return new BusinessException(ErrorCode.TICKET_NOT_FOUND);
		});

		Member newManager = resolveManager(ticket, request.getManagerId());

		TicketStatus newStatus = request.getStatus();

		TicketPriority newPriority = request.getPriority().isPresent()
			? request.getPriority().get()
			: ticket.getPriority();

		TicketType newTicketType = resolveTicketType(ticket, request.getTypeId());

		Category newPrimaryCategory = resolveCategory(ticket.getPrimaryCategory(),
			request.getPrimaryCategoryId(), categoryRepository);
		Category newSecondaryCategory = resolveCategory(ticket.getSecondaryCategory(),
			request.getSecondaryCategoryId(), categoryRepository);

		if (request.getPrimaryCategoryId().isPresent() || request.getSecondaryCategoryId().isPresent()) {
			categoryDomainService.validateHierarchy(newPrimaryCategory, newSecondaryCategory);
		}

		ticketDomainService.updateTicketForManager(
			ticket, newStatus, newPriority, newManager, newTicketType, newPrimaryCategory, newSecondaryCategory
		);

		return ticket.getId();
	}

	@Transactional
	@CacheEvict(value = "ticketsForUser", allEntries = true)
	public Long updateTicketForUser(
		Long ticketId, TicketUpdateRequestForUser request, CustomUserDetails userDetails
	) {
		log.info("[티켓 수정 요청] 티켓 ID: {}", ticketId);

		Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(() -> {
			log.error("[티켓 수정 실패] 존재하지 않는 티켓 ID: {}", ticketId);
			return new BusinessException(ErrorCode.TICKET_NOT_FOUND);
		});

		if (!ticket.getRequester().getId().equals(userDetails.getId())) {
			log.error("[티켓 수정 실패] 권한 없음");
			throw new BusinessException(ErrorCode.ACCESS_DENIED);
		}

		TicketType newTicketType = resolveTicketType(ticket, request.getTypeId());

		Category newPrimaryCategory = resolveCategory(ticket.getPrimaryCategory(),
			request.getPrimaryCategoryId(), categoryRepository);
		Category newSecondaryCategory = resolveCategory(ticket.getSecondaryCategory(),
			request.getSecondaryCategoryId(), categoryRepository);

		if (request.getPrimaryCategoryId().isPresent() || request.getSecondaryCategoryId().isPresent()) {
			categoryDomainService.validateHierarchy(newPrimaryCategory, newSecondaryCategory);
		}

		ticketDomainService.updateTicketForUser(
			ticket, request.getTitle(), request.getContent(), newTicketType, newPrimaryCategory,
			newSecondaryCategory, request.getUrgent(), request.getDeadline().orElse(null)
		);
		return ticket.getId();
	}

	@Transactional
	public void deleteTicket(Long ticketId, CustomUserDetails userDetails) {
		log.info("[티켓 삭제 요청] 티켓 ID: {}", ticketId);

		Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(() -> {
			log.error("[티켓 삭제 실패] 존재하지 않는 티켓 ID: {}", ticketId);
			return new BusinessException(ErrorCode.TICKET_NOT_FOUND);
		});

		if (!ticket.getRequester().getId().equals(userDetails.getId())) {
			log.error("[티켓 삭제 실패] 티켓 삭제 권한 없음");
			throw new BusinessException(ErrorCode.UNAUTHORIZED);
		}

		if (ticket.getStatus() != TicketStatus.PENDING) {
			log.error("[티켓 삭제 실패] 처리 대기 중인 티켓만 삭제할 수 있습니다.");
			throw new BusinessException(ErrorCode.TICKET_STATUS_NOT_PENDING);
		}

		ticketRepository.delete(ticket);
	}

	private Member resolveManager(Ticket ticket, JsonNullable<Long> managerId) {
		// 값이 입력되지 않은 경우 그대로 반환 (변경 없음)
		if (!managerId.isPresent()) {
			return ticket.getManager();
		}

		// 값이 null로 입력된 경우 null 반환 (해제)
		Long newManagerId = managerId.get();
		if (newManagerId == null) {
			return null;
		}

		// null이 아닌 값이 입력된 경우 DB 조회
		return memberRepository.findByIdAndRole(newManagerId, Role.MANAGER).orElseThrow(() -> {
			log.error("[티켓 수정 실패] 존재하지 않는 담당자 ID: {}", newManagerId);
			return new BusinessException(ErrorCode.MANAGER_NOT_FOUND);
		});
	}

	private TicketType resolveTicketType(Ticket ticket, Long typeId) {
		if (typeId == null) {
			return ticket.getTicketType();
		}

		return ticketTypeRepository.findById(typeId).orElseThrow(() -> {
			log.error("[티켓 수정 실패] 존재하지 않는 티켓 유형 ID: {}", typeId);
			return new BusinessException(ErrorCode.TICKET_TYPE_NOT_FOUND);
		});
	}

	private Category resolveCategory(
		Category currentCategory, JsonNullable<Long> newCategoryId, CategoryRepository categoryRepository
	) {
		if (!newCategoryId.isPresent()) {
			return currentCategory;
		}

		Long catIdVal = newCategoryId.get();
		if (catIdVal == null) {
			return null;
		}

		return categoryRepository.findById(catIdVal).orElseThrow(() -> {
			log.error("[티켓 수정 실패] 존재하지 않는 카테고리 ID: {}", catIdVal);
			return new BusinessException(ErrorCode.CATEGORY_NOT_FOUND);
		});
	}
}
