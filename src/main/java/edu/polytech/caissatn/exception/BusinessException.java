package edu.polytech.caissatn.exception;

import org.springframework.web.ErrorResponseException;

public class BusinessException extends ErrorResponseException {

    private final BusinessExceptionReason reason;

    /**
     * Constructeur avec raison.
     *
     * @param reason la raison de l'exception
     */
    public BusinessException(final BusinessExceptionReason reason) {
        super(reason.getStatusCode(), reason.getBody(), null);
        this.reason = reason;
    }

    /**
     * Retourne la raison de l'exception métier.
     *
     * @return la raison
     */
    public BusinessExceptionReason getReason() {
        return this.reason;
    }
}
