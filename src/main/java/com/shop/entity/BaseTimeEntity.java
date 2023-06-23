package com.shop.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@EntityListeners(value = {AuditingEntityListener.class})//auditing적용위한 어노테이션
@MappedSuperclass//공통매핑정보가 필요할때 사용하는 어노테이션, 부모클래스를 제공받는 자식클래스에게 매핑정보만 제공한다.
@Getter @Setter
public abstract class BaseTimeEntity {
    @CreatedDate//엔티티 생성저장 시간을 자동저장한다.
    @Column(updatable = false)
    private LocalDateTime regTime;

    @LastModifiedDate//엔티티 값변경 시간을 자동저장한다.
    private LocalDateTime updateTime;
}
