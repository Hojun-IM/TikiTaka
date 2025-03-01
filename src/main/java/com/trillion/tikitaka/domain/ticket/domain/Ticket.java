package com.trillion.tikitaka.domain.ticket.domain;

import java.time.LocalDateTime;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.trillion.tikitaka.domain.category.domain.Category;
import com.trillion.tikitaka.domain.member.domain.Member;
import com.trillion.tikitaka.domain.tickettype.domain.TicketType;
import com.trillion.tikitaka.global.common.DeleteBaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "ticket")
@EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted_at IS NULL")
@SQLDelete(sql = "UPDATE tickets SET deleted_at = NOW() WHERE id = ? and version = ?")
public class Ticket extends DeleteBaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Version
	private Long version;

	@Column(nullable = false, length = 150)
	private String title;

	@Lob
	@Column(nullable = false, length = 5000, columnDefinition = "LONGTEXT")
	private String content;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TicketStatus status;

	@Enumerated(EnumType.STRING)
	private TicketPriority priority;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "manager_id")
	private Member manager;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "requester_id")
	private Member requester;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "type_id")
	private TicketType ticketType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "primary_category_id")
	private Category primaryCategory;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "secondary_category_id")
	private Category secondaryCategory;

	@Column(nullable = false)
	private Boolean urgent;

	private LocalDateTime deadline;

	@Builder
	public Ticket(
		String title, String content, TicketStatus status, TicketPriority priority,
		Member manager, Member requester, TicketType ticketType, Category primaryCategory,
		Category secondaryCategory, Boolean urgent, LocalDateTime deadline
	) {
		this.title = title;
		this.content = content;
		this.status = status;
		this.priority = priority;
		this.manager = manager;
		this.requester = requester;
		this.ticketType = ticketType;
		this.primaryCategory = primaryCategory;
		this.secondaryCategory = secondaryCategory;
		this.urgent = urgent;
		this.deadline = deadline;
	}
}
