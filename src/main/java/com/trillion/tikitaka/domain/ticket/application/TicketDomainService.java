package com.trillion.tikitaka.domain.ticket.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.trillion.tikitaka.domain.category.domain.Category;
import com.trillion.tikitaka.domain.category.infrastructure.CategoryRepository;
import com.trillion.tikitaka.domain.member.domain.Member;
import com.trillion.tikitaka.domain.member.infrastructure.MemberRepository;
import com.trillion.tikitaka.domain.ticket.domain.Ticket;
import com.trillion.tikitaka.domain.ticket.domain.TicketStatus;
import com.trillion.tikitaka.domain.ticket.dto.TicketRequestForUser;
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
	public Ticket createTicket(TicketRequestForUser request, CustomUserDetails userDetails) {
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
}
