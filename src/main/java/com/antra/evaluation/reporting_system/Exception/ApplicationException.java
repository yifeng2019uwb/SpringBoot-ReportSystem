package com.antra.evaluation.reporting_system.Exception;

import org.springframework.http.HttpStatus;

public class ApplicationException extends RuntimeException {

    private HttpStatus status;

    public ApplicationException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
    }

    public ApplicationException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

}
