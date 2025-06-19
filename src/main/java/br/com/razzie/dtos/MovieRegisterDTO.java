package br.com.razzie.dtos;

import java.util.Set;

public record MovieRegisterDTO(
        Integer year,
        String title,
        Set<StudioRegisterDTO> studios,
        Set<ProducerRegisterDTO> producers,
        Boolean winner
) {

}
