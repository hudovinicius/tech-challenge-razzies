package br.com.razzie.services;

import br.com.razzie.dtos.ProducerRegisterDTO;
import br.com.razzie.dtos.WinRangeResponseDTO;
import br.com.razzie.models.Producer;

import java.util.Set;

public interface ProducerService {

    Set<Producer> register(Set<ProducerRegisterDTO> producers);

    Producer findOrCreateProducer(ProducerRegisterDTO producer);

    WinRangeResponseDTO findConsecutiveWinners();
}
