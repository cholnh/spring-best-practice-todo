package com.nzzi.guide.todo.domain.user.model;

import com.nzzi.guide.todo.domain._base.Auditable;
import com.nzzi.guide.todo.domain._base.Password;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import javax.persistence.*;

@Entity
@Table(name = "user_tbl")
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode(callSuper=false)
public class User extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    /**
     * 아이디
     */
    @Column(name = "username", nullable = false, unique=true, length = 50)
    private String username;

    /**
     * 비밀번호
     * 암호화되서 저장됨 (필수)
     * 글자수: utf8 기준 / 영문 255자 / 한글 255자
     */
    @Column(name = "password", nullable = false, length = 255)
    private Password password;

    /**
     * 유저 실명
     * 글자수: utf8 기준 / 영문 30자 / 한글 30자
     */
    @Column(name = "name", nullable = false, length = 30)
    private String name;

    /**
     * 유저 권한
     *
     * Prefix: "ROLE_"
     * Delimiter: 쉼표(,)
     * Client 기본값: "ROLE_USER"
     * Store Owner 기본값: "ROLE_STORE_OWNER"
     * 예시: ROLE_USER,ROLE_STORE_OWNER
     * 글자수: utf8 기준 / 영문 256자 / 한글 256자
     */
    @Column(name = "authorities", nullable = false, length = 256)
    private String authorities;

    @Builder
    protected User(String username, Password password, String name) {
        this.username = username;
        this.password = password;
        this.name = name;
    }

    @PrePersist
    private void prePersist() {
        if(!isValidAuthorities(authorities)) {
            this.authorities = "ROLE_USER";
        }
    }

    public String[] getAuthorities() {
        return this.authorities.split(",");
    }

    private boolean isValidAuthorities(String authorities) {
        if(authorities == null || authorities.isEmpty()) return false;
        for(String authority : getAuthorities()) {
            if(authority.length() < 6 ||
                    !authority.toUpperCase().substring(0, 5).equals("ROLE_")) {
                return false;
            }
        }
        return true;
    }
}
