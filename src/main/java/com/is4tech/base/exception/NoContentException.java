package com.is4tech.base.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NO_CONTENT)
public class NoContentException extends Exception {

    public NoContentException(String message) {
        super(message);
    }
}
