package com.trillion.tikitaka.domain.registration.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import com.trillion.tikitaka.domain.registration.domain.Registration;
import com.trillion.tikitaka.domain.registration.domain.RegistrationStatus;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public interface RegistrationRepository extends JpaRepository<Registration, Long>, CustomRegistrationRepository {
	boolean existsByUsernameAndStatus(
		@NotBlank @Size(max = 30) @Pattern(regexp = "^[a-z]{3,10}\\.[a-z]{1,5}$") String username,
		RegistrationStatus status);

	boolean existsByEmailAndStatus(@NotBlank @Email String email, RegistrationStatus status);
}
