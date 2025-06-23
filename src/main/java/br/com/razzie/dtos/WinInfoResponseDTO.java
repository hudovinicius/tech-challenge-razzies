package br.com.razzie.dtos;

public record WinInfoResponseDTO(
        String producer,
        Integer interval,
        Integer previousWin,
        Integer followingWin
) {
}
