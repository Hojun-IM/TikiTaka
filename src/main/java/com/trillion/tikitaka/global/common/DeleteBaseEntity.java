package com.trillion.tikitaka.global.common;

import java.time.LocalDateTime;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

@Getter
@MappedSuperclass
public class DeleteBaseEntity extends BaseEntity {

	private LocalDateTime deletedAt;
}
