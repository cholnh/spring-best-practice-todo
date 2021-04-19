/**
 * user package
 * 회원가입된 유저.
 * - id/pw 방식으로 인증.
 * - roll: user, admin
 * - password try count 최대 5회.
 * - password try lock 시, email 인증 필요.
 * - phone number 는 country code, number 구성.
 */
package com.nzzi.guide.todo.domain.user;