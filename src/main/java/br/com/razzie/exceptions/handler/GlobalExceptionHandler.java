package br.com.razzie.exceptions.handler;

import br.com.razzie.dtos.ResponseErrorDTO;
import br.com.razzie.exceptions.ErrorCode;
import br.com.razzie.exceptions.ProducerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Collections;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseErrorDTO> handleGenericException(Exception ex) {
        log.error("GlobalExceptionHandler.handleGenericException - error - message: {}", ex.getMessage(), ex);

        ErrorCode code = ErrorCode.INTERNAL_SERVER_ERROR;

        return ResponseEntity
                .status(code.getHttpStatus())
                .body(new ResponseErrorDTO(code.getCode(), code.getMessage(), Collections.singletonList(ex.getMessage())));
    }

    @ExceptionHandler(ProducerException.class)
    public ResponseEntity<ResponseErrorDTO> handleCompanyException(ProducerException ex) {
        return ResponseEntity
                .status(ex.getErrorCode().getHttpStatus())
                .body(new ResponseErrorDTO(ex.getErrorCode().getCode(), ex.getErrorCode().getMessage(), Collections.singletonList(ex.getMessage())));
    }
}
