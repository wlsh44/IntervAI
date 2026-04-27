package wlsh.project.intervai.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "잘못된 입력값입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."),

    // Auth
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "존재하지 않는 리프레시 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "올바르지 않은 토큰입니다."),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "닉네임 또는 비밀번호가 일치하지 않습니다."),
    DUPLICATE_NICKNAME(HttpStatus.BAD_REQUEST, "이미 사용 중인 닉네임입니다."),
    INVALID_NICKNAME_LENGTH(HttpStatus.BAD_REQUEST, "닉네임은 4자 이상 8자 이하여야 합니다."),
    INVALID_PASSWORD_LENGTH(HttpStatus.BAD_REQUEST, "비밀번호는 4자 이상 12자 이하여야 합니다."),

    // Profile
    PROFILE_NOT_FOUND(HttpStatus.NOT_FOUND, "프로필을 찾을 수 없습니다."),
    PROFILE_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 프로필이 존재합니다."),
    PROFILE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "본인의 프로필만 접근할 수 있습니다."),

    // Interview
    INVALID_QUESTION_COUNT(HttpStatus.BAD_REQUEST, "질문 개수는 5개 이상 10개 이하여야 합니다."),
    CS_SUBJECT_REQUIRED(HttpStatus.BAD_REQUEST, "CS 면접에는 상세 분야 설정이 필요합니다."),
    PORTFOLIO_LINK_REQUIRED(HttpStatus.BAD_REQUEST, "포트폴리오 면접에는 포트폴리오 링크가 필요합니다."),
    INTERVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "면접을 찾을 수 없습니다."),
    INTERVIEW_ACCESS_DENIED(HttpStatus.FORBIDDEN, "본인의 면접만 접근할 수 있습니다."),

    // Session
    SESSION_NOT_FOUND(HttpStatus.NOT_FOUND, "면접 세션을 찾을 수 없습니다."),
    SESSION_ALREADY_COMPLETED(HttpStatus.BAD_REQUEST, "이미 종료된 면접 세션입니다."),
    SESSION_ACCESS_DENIED(HttpStatus.UNAUTHORIZED, "접근 불가능한 세션입니다."),

    // Question
    QUESTION_COUNT_EXCEEDED(HttpStatus.BAD_REQUEST, "질문 개수를 초과했습니다."),
    QUESTION_NOT_FOUND(HttpStatus.NOT_FOUND, "질문을 찾을 수 없습니다."),
    ALL_QUESTIONS_ANSWERED(HttpStatus.BAD_REQUEST, "모든 질문에 답변을 완료했습니다."),
    QUESTION_NOT_YET_ANSWERED(HttpStatus.BAD_REQUEST, "현재 질문에 아직 답변하지 않았습니다."),

    // Answer
    ANSWER_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 답변이 존재합니다."),
    ANSWER_NOT_FOUND(HttpStatus.NOT_FOUND, "답변을 찾을 수 없습니다."),

    // Feedback
    FOLLOW_UP_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "꼬리 질문은 최대 3개까지 가능합니다."),

    // Report
    REPORT_NOT_FOUND(HttpStatus.NOT_FOUND, "리포트를 찾을 수 없습니다."),
    REPORT_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 리포트가 존재합니다."),
    SESSION_NOT_COMPLETED(HttpStatus.BAD_REQUEST, "종료되지 않은 세션입니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
