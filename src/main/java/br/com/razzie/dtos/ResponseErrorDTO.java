package br.com.razzie.dtos;

import java.util.List;

public record ResponseErrorDTO(String code, String message, List<String> details) {
}
