package br.com.razzie.runners;

import br.com.razzie.configs.AppProperties;
import br.com.razzie.dtos.MovieRegisterDTO;
import br.com.razzie.dtos.ProducerRegisterDTO;
import br.com.razzie.dtos.StudioRegisterDTO;
import br.com.razzie.services.MovieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class LoadDataRunner implements CommandLineRunner {

    private final AppProperties appProperties;
    private final ResourceLoader resourceLoader;
    private final MovieService movieService;

    @Override
    public void run(String... args) throws Exception {
        log.info("LoadDataRunner.run - starting data load...");


        if (!appProperties.getImportOnStartup()) {
            log.warn("LoadDataRunner.run - Import is disabled, skipping data load.");
            return;
        }

        LocalDateTime startTime = LocalDateTime.now();
        var resource = resourceLoader.getResource(appProperties.getFilePath());

        try (var reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {

            var lines = reader.lines();

            if (appProperties.getSkipHeader()) {
                lines = lines.skip(1);
                log.debug("LoadDataRunner.run - Header line skipped.");
            }

            lines.forEach(this::processLine);
        }

        var took = ChronoUnit.MILLIS.between(startTime, LocalDateTime.now());
        log.info("LoadDataRunner.run - ending data load. Took: {} ms", took);
    }

    private void processLine(String line) {

        log.debug("LoadDataRunner.processLine - Processing line:[{}]", line);

        if (StringUtils.isBlank(line)) {
            return;
        }

        var parts = StringUtils.split(line, appProperties.getColumnDelimiter());

        if (parts.length < 3) {
            log.warn("LoadDataRunner.processLine - Skipping line due to insufficient data: {}", line);
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
            log.error("LoadDataRunner.processLine - Error processing line: {}", line, e);
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
