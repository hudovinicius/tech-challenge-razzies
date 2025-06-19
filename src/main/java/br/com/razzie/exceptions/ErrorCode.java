package br.com.razzie.exceptions;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ErrorCode {
    INTERNAL_SERVER_ERROR("100", HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor"),
    FIND_WINNERS_RANGES_ERROR("200", HttpStatus.BAD_REQUEST, "Erro ao processar os ranges de vencedores"),;

    private final String code;
    private final HttpStatus httpStatus;
    private final String message;
}
