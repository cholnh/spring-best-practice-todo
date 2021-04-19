package com.nzzi.guide.todo.domain.todo.model;

import com.nzzi.guide.todo.domain._base.Auditable;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Table(name = "todo_tbl")
@Getter
/*
 * DynamicUpdate
 * null 필드를 update 쿼리에서 제외하는 어노테이션.
 * 또한 업데이트 동시성 문제를 해결.
 * 단점: 성능 오버헤드(엔티티 상태 추적)가 있으므로 고려 필요
 */
@DynamicUpdate
/*
 * 접근지정자 PROTECTED 이유
 * 기본 생성자의 접근 제한자를 private 으로 걸면,
 * 추후에 Lazy Loading 사용 시 Proxy 에서 생성하지 못하여 Exception 발생
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Todo extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    /**
     * 제목
     * 글자수: utf8 기준 / 영문 100자 / 한글 100자
     */
    @Column(name="title", length = 100, nullable = false)
    private String title;

    /**
     * 내용
     * CLOB 매핑
     */
    @Column(name = "contents", nullable = false)
    @Lob
    @Basic(fetch = FetchType.LAZY)
    private String contents;
}
