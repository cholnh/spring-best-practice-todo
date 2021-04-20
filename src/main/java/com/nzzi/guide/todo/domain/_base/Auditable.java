package com.nzzi.guide.todo.domain._base;

import com.nzzi.guide.todo.global.annotation.BooleanToYNConverter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.LocalDateTime;

@EntityListeners(value = AuditingEntityListener.class)
@MappedSuperclass
@Getter
@SuperBuilder
@NoArgsConstructor
public abstract class Auditable implements Serializable {

    /**
     * 활성화 여부 (Y/N)
     * default: true(Y)
     * 대문자 필수
     */
    @Column(name = "is_active", nullable = false, length = 1, columnDefinition = "VARCHAR(1) DEFAULT 'Y'")
    @Convert(converter = BooleanToYNConverter.class)
    private Boolean isActive;

    /**
     * 등록 날짜
     */
    @Column(name = "created_date", nullable = false, columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @CreationTimestamp
    private LocalDateTime createdDate;

    /**
     * 수정 날짜
     */
    @Column(name = "last_modified_date", nullable = false, columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @UpdateTimestamp
    private LocalDateTime lastModifiedDate;
}
