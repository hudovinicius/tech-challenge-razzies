package br.com.razzie.services;

import br.com.razzie.dtos.ProducerRegisterDTO;
import br.com.razzie.dtos.WinInfoResponseDTO;
import br.com.razzie.dtos.WinRangeResponseDTO;
import br.com.razzie.exceptions.ErrorCode;
import br.com.razzie.exceptions.ProducerException;
import br.com.razzie.models.Movie;
import br.com.razzie.models.Producer;
import br.com.razzie.repositories.ProducerRepository;
import br.com.razzie.utils.IntervalUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Override
    @Cacheable("winners-rangers")
    public WinRangeResponseDTO findConsecutiveWinners() {

        log.debug("ProducerServiceImpl.findConsecutiveWinners - start - Finding winners ranges");

        try {
            var producers = producerRepository.findProducersWithMultipleWins();

            if (producers == null || producers.isEmpty()) {
                log.warn("ProducerServiceImpl.findConsecutiveWinners - No producers with multiple wins found.");
                return new WinRangeResponseDTO(List.of(), List.of());
            }

            log.debug("ProducerServiceImpl.findConsecutiveWinners - Found {} producers with multiple wins.", producers.size());

            List<WinInfoResponseDTO> winInfoList = producers.parallelStream()
                .map(producer -> {
                    List<Integer> sortedWinYears = producer.getMovies().stream()
                        .filter(Movie::getWinner)
                        .map(Movie::getYear)
                        .sorted()
                        .toList();
                    return IntervalUtil.calculateMinMaxInterval(sortedWinYears, producer.getName());
                })
                .flatMap(List::stream)
                .toList();

            log.debug("ProducerServiceImpl.findConsecutiveWinners - Calculated win intervals for {} producers", winInfoList.size());

            Map<Integer, List<WinInfoResponseDTO>> grouped = winInfoList.stream()
                    .collect(Collectors.groupingBy(WinInfoResponseDTO::interval));

            int min = Collections.min(grouped.keySet());
            int max = Collections.max(grouped.keySet());

            log.debug("ProducerServiceImpl.findConsecutiveWinners - Min interval: {}, Max interval: {}", min, max);

            return new WinRangeResponseDTO(
                    grouped.getOrDefault(min, List.of()),
                    grouped.getOrDefault(max, List.of())
            );

        } catch (Exception ex) {
            log.error("ProducerServiceImpl.findConsecutiveWinners - Error finding winners ranges, message: {}", ex.getMessage(),  ex);
            throw new ProducerException(ex, ErrorCode.FIND_WINNERS_RANGES_ERROR);
        }
    }
}
