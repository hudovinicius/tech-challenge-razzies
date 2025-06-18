package br.com.movies.services;

import br.com.movies.dtos.ProducerRegisterDTO;
import br.com.movies.models.Producer;
import br.com.movies.repositories.ProducerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProducerServiceImpl implements ProducerService {

    private final ProducerRepository producerRepository;

    @Override
    public Set<Producer> register(Set<ProducerRegisterDTO> producers) {
        return producers.stream()
            .map(this::findOrCreateProducer)
            .collect(java.util.stream.Collectors.toSet());
    }

    @Override
    public Producer findOrCreateProducer(ProducerRegisterDTO producer) {
        log.debug("ProducerServiceImpl.findOrCreateProducer - start - Finding or creating producer: {}", producer.name());

        return producerRepository.findByName(producer.name()).orElseGet(() -> {
            log.debug("ProducerServiceImpl.findOrCreateProducer - Creating new producer: {}", producer.name());
            return producerRepository.save(
                Producer.builder()
                    .name(producer.name())
                    .build()
            );
        });
    }
}
