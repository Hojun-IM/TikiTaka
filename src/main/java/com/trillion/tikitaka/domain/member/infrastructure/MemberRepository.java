package com.trillion.tikitaka.domain.member.infrastructure;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.trillion.tikitaka.domain.member.domain.Member;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public interface MemberRepository extends JpaRepository<Member, Long>, CustomMemberRepository {
	Optional<Member> findByUsername(String username);

	boolean existsByUsername(@NotBlank @Size(max = 30) String username);

	boolean existsByEmail(@Email @NotBlank String email);

	boolean existsByUsernameAndDeletedAtIsNull(@NotBlank @Size(max = 20) String username);

	boolean existsByEmailAndDeletedAtIsNull(@Email @NotBlank String email);
}
