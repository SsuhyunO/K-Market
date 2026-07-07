package org.example.k_market.controller.advice; // TODO: 기존 프로젝트에 이미 있다면 이 파일은 무시하고 기존 것에 병합하세요

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * 이미 프로젝트에 @RestControllerAdvice 로 만든 전역 예외 처리 클래스가 있다면
 * 이 파일은 만들지 말고, 그 클래스에 아래 두 핸들러만 추가하세요.
 *
 * 이게 없으면 login()/withdraw() 등에서 던지는 IllegalArgumentException,
 * IllegalStateException 이 그대로 500 에러로 나가서
 * 프론트에서 "탈퇴한 계정입니다" 같은 메시지를 받아볼 수 없습니다.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleIllegalState(IllegalStateException e) {
        // 탈퇴 회원 로그인 시도, 비로그인 상태에서 마이페이지 접근 등
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", e.getMessage()));
    }
}