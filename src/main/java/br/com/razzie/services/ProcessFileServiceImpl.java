package br.com.razzie.services;

import br.com.razzie.configs.AppProperties;
import br.com.razzie.dtos.MovieRegisterDTO;
import br.com.razzie.dtos.ProducerRegisterDTO;
import br.com.razzie.dtos.StudioRegisterDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProcessFileServiceImpl implements ProcessFileService {

    private final AppProperties appProperties;
    private final ResourceLoader resourceLoader;
    private final MovieService movieService;

    @Override
    public void processFile(String filePath) throws Exception {
        log.debug("ProcessFileServiceImpl.processFile - starting data load...");

        var resource = resourceLoader.getResource(filePath);

        try (var reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {

            var lines = reader.lines();

            if (appProperties.getSkipHeader()) {
                lines = lines.skip(1);
                log.debug("LoadDataRunner.run - Header line skipped.");
            }

            lines.forEach(this::processLine);
        }

        log.debug("ProcessFileServiceImpl.processFile - ending data load.");
    }

    private void processLine(String line) {

        log.debug("ProcessFileServiceImpl.processLine - Processing line:[{}]", line);

        if (StringUtils.isBlank(line)) {
            return;
        }

        var parts = StringUtils.split(line, appProperties.getColumnDelimiter());

        if (parts.length < 3) {
            log.warn("ProcessFileServiceImpl.processLine - Skipping line due to insufficient data: {}", line);
            return;
        }

        try {
            var winner = parts.length > 4 && StringUtils.equalsIgnoreCase(parts[4], appProperties.getWinnerValue());

            var movie = new MovieRegisterDTO(
                    Integer.parseInt(parts[0].trim()),
                    parts[1].trim(),
                    parseMultipleStudios(parts[2]),
                    parseMultipleProducers(parts[3]),
                    winner
            );

            movieService.register(movie);

        } catch (Exception e) {
            log.error("ProcessFileServiceImpl.processLine - Error processing line: {}", line, e);
        }
    }

    private Set<ProducerRegisterDTO> parseMultipleProducers(String producers) {

        var normalizedProducers = normalizeElementDelimiters(producers);

        return Arrays.stream(StringUtils.split(normalizedProducers, appProperties.getElementDelimiter()))
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .map(ProducerRegisterDTO::new)
                .collect(java.util.stream.Collectors.toSet());
    }

    private Set<StudioRegisterDTO> parseMultipleStudios(String studios) {

        var normalizedStudios = normalizeElementDelimiters(studios);

        return Arrays.stream(
                        StringUtils.split(normalizedStudios, appProperties.getElementDelimiter()))
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .map(StudioRegisterDTO::new)
                .collect(java.util.stream.Collectors.toSet());
    }

    private String normalizeElementDelimiters(String value) {
        return value.replaceAll(appProperties.getRegexElementDelimiter(), appProperties.getElementDelimiter());
    }
}
