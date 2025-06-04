package edu.polytech.caissatn.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponse;

/**
 * Defines the business exception reasons.
 */
public enum BusinessExceptionReason implements ErrorResponse {
    USER_ALREADY_HAS_COMPANY(HttpStatus.FORBIDDEN, "User is already has a company"),
    NOT_OPENED_REGISTER(HttpStatus.FORBIDDEN, "You can save transactions only to an open register"),
    REGISTER_ALREADY_OPENED(HttpStatus.FORBIDDEN, "There is already an opened register."),
    NO_REGISTER_IS_OPENED(HttpStatus.FORBIDDEN, "There is no opened register.");

    private final HttpStatusCode status;
    private final ProblemDetail body;

    BusinessExceptionReason(HttpStatusCode status, String detail) {
        this.status = status;
        this.body = ProblemDetail.forStatusAndDetail(status, detail);
    }

    @Override
    public HttpStatusCode getStatusCode() {
        return status;
    }

    @Override
    public ProblemDetail getBody() {
        return body;
    }
}