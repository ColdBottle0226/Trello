package pjt.trello.common;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    DELETED_ACCOUNT(HttpStatus.BAD_REQUEST, "삭제된 아이디입니다."),
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당하는 아이디는 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }

    public String getMessage() {
        return this.message;
    }

    private ErrorCode(final HttpStatus httpStatus, final String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
