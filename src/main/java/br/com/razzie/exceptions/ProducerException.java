package br.com.razzie.exceptions;

import lombok.Getter;

@Getter
public class ProducerException extends RuntimeException {

    private final ErrorCode errorCode;

    public ProducerException(Throwable e, ErrorCode errorCode) {
        super(e);
        this.errorCode = errorCode;
    }
}
