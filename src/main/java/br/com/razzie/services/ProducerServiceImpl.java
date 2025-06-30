package br.com.razzie.services;

import br.com.razzie.dtos.ProducerRegisterDTO;
import br.com.razzie.dtos.WinInfoResponseDTO;
import br.com.razzie.dtos.WinRangeResponseDTO;
import br.com.razzie.exceptions.ErrorCode;
import br.com.razzie.exceptions.ProducerException;
import br.com.razzie.models.Movie;
import br.com.razzie.models.Producer;
import br.com.razzie.repositories.ProducerRepository;
import br.com.razzie.utils.MinMaxInterval;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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

            List<WinInfoResponseDTO> minList = new ArrayList<>();
            List<WinInfoResponseDTO> maxList = new ArrayList<>();
            MinMaxInterval currentMinMax = new MinMaxInterval();

            producers.forEach(producer -> {
                List<Integer> sortedWinYears = producer.getMovies().stream()
                        .filter(Movie::getWinner)
                        .map(Movie::getYear)
                        .sorted()
                        .toList();
                calculateMinMaxInterval(sortedWinYears, producer.getName(), minList, maxList, currentMinMax);
            });

            return new WinRangeResponseDTO(minList, maxList);

        } catch (Exception ex) {
            log.error("ProducerServiceImpl.findConsecutiveWinners - Error finding winners ranges, message: {}", ex.getMessage(),  ex);
            throw new ProducerException(ex, ErrorCode.FIND_WINNERS_RANGES_ERROR);
        }
    }

    private void calculateMinMaxInterval(List<Integer> sortedWinYears, String producerName, List<WinInfoResponseDTO> minList, List<WinInfoResponseDTO> maxList, MinMaxInterval currentMinMax) {
        for (int i = 1; i < sortedWinYears.size(); i++) {
            int prev = sortedWinYears.get(i - 1);
            int curr = sortedWinYears.get(i);
            int diff = curr - prev;

            log.debug("ProducerServiceImpl.calculateMinMaxInterval - Comparing years: {} and {} - Difference: {}, currentMin: {}, currentMax: {}", prev, curr, diff, currentMinMax.getMin(), currentMinMax.getMax());

            if (currentMinMax.lessThanMin(diff)) {
                log.debug("ProducerServiceImpl.calculateMinMaxInterval - Found new min interval: {} ({} - {})", diff, curr, prev);

                minList.clear();
                minList.add(new WinInfoResponseDTO(producerName, diff, prev, curr));
                currentMinMax.update(diff);

            } else if (currentMinMax.equalsMin(diff)) {
                log.debug("ProducerServiceImpl.calculateMinMaxInterval - Found equal min interval: {} ({} - {})", diff, curr, prev);
                minList.add(new WinInfoResponseDTO(producerName, diff, prev, curr));
            }

            if (currentMinMax.greaterThanMax(diff)) {
                log.debug("ProducerServiceImpl.calculateMinMaxInterval - Found new max interval: {} ({} - {})", diff, curr, prev);

                maxList.clear();
                maxList.add(new WinInfoResponseDTO(producerName, diff, prev, curr));
                currentMinMax.update(diff);

            } else if (currentMinMax.equalsMax(diff)) {
                log.debug("ProducerServiceImpl.calculateMinMaxInterval - Found equal max interval: {} ({} - {})", diff, curr, prev);
                maxList.add(new WinInfoResponseDTO(producerName, diff, prev, curr));
            }
        }
    }
}
