package com.nzzi.guide.todo.domain.todo.model;

import com.nzzi.guide.todo.domain._base.Auditable;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;


import javax.persistence.*;

@Entity
@Table(name = "todo_tbl")
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자의 접근 제한자를 private 으로 걸면, 추후에 Lazy Loading 사용 시 Proxy 관련 예외가 발생
@Getter
@EqualsAndHashCode(callSuper=false)
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

    @Builder
    protected Todo(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }
}
