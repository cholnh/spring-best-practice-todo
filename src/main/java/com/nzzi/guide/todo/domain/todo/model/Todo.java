package com.nzzi.guide.todo.domain.todo.model;

import com.nzzi.guide.todo.domain._base.Auditable;
import lombok.*;
import lombok.experimental.SuperBuilder;
import javax.persistence.*;

@Entity
@Table(name = "todo_tbl")
@Getter
/*
 * 접근지정자 PROTECTED 이유
 * 기본 생성자의 접근 제한자를 private 으로 걸면,
 * 추후에 Lazy Loading 사용 시 Proxy 에서 생성하지 못하여 Exception 발생
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
/*
 * 상위 부모 객체인 Auditable 의 생성자 빌더를 추가시키기 위함
 */
@SuperBuilder
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
