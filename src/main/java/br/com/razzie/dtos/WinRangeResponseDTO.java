package br.com.razzie.dtos;

import java.util.List;

public record WinRangeResponseDTO(
    List<WinInfoResponseDTO> min,
    List<WinInfoResponseDTO> max
) {

}
