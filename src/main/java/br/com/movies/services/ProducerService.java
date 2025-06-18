package br.com.movies.services;

import br.com.movies.dtos.ProducerRegisterDTO;
import br.com.movies.models.Producer;

import java.util.Set;

public interface ProducerService {

    Set<Producer> register(Set<ProducerRegisterDTO> producers);

    Producer findOrCreateProducer(ProducerRegisterDTO producer);
}
