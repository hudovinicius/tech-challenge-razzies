package br.com.movies.services;

import br.com.movies.configs.AppProperties;
import br.com.movies.dtos.MovieRegisterDTO;
import br.com.movies.dtos.ProducerRegisterDTO;
import br.com.movies.dtos.StudioRegisterDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class CsvDataLoaderService implements LoadDataService {

    private final AppProperties appProperties;
    private final ResourceLoader resourceLoader;
    private final MovieService movieService;

    @Override
    public void execute() throws IOException {

        log.info("CsvDataLoaderService.execute - Loading data from CSV files...");

        if (!appProperties.getImportOnStartup()) {
            log.warn("CsvDataLoaderService.execute - Import is disabled, skipping data load.");
            return;
        }

        var resource = resourceLoader.getResource(appProperties.getFilePath());

        try (var reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {

            var lines = reader.lines();

            if (appProperties.getSkipHeader()) {
                lines = lines.skip(1);
                log.debug("CsvDataLoaderService.execute - Header line skipped.");
            }

            lines.forEach(this::processLine);
        }
    }

    private void processLine(String line) {

        log.debug("CsvDataLoaderService.processLine - Processing line:[{}]", line);

        if (StringUtils.isBlank(line)) {
            return;
        }

        var parts = StringUtils.split(line, appProperties.getColumnDelimiter());

        if (parts.length < 3) {
            log.warn("CsvDataLoaderService.processLine - Skipping line due to insufficient data: {}", line);
            return;
        }

        try {

            var winner = parts.length > 4 && Boolean.parseBoolean(parts[4].trim());

            var movie = new MovieRegisterDTO(
                    Integer.parseInt(parts[0].trim()),
                    parts[1].trim(),
                    parseMultipleStudios(parts[2]),
                    parseMultipleProducers(parts[3]),
                    winner
            );

            movieService.register(movie);

        } catch (Exception e) {
            log.error("CsvDataLoaderService.processLine - Error processing line: {}", line, e);
        }
    }

    private Set<ProducerRegisterDTO> parseMultipleProducers(String producers) {
        return Arrays.stream(
                StringUtils.split(normalizeDelimiters(producers), appProperties.getElementDelimiter()))
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .map(ProducerRegisterDTO::new)
                .collect(java.util.stream.Collectors.toSet());
    }

    private Set<StudioRegisterDTO> parseMultipleStudios(String studios) {
        return Arrays.stream(
                StringUtils.split(normalizeDelimiters(studios), appProperties.getElementDelimiter()))
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .map(StudioRegisterDTO::new)
                .collect(java.util.stream.Collectors.toSet());
    }

    private String normalizeDelimiters(String value) {
        if (StringUtils.isBlank(value)) {
            return value;
        }

        return value.replaceAll(appProperties.getRegexElementDelimiter(), appProperties.getElementDelimiter());
    }
}
