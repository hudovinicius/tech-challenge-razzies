package br.com.razzie.runners;

import br.com.razzie.configs.AppProperties;
import br.com.razzie.services.ProcessFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class LoadDataRunner implements CommandLineRunner {

    private final AppProperties appProperties;
    private final ProcessFileService processFileService;

    @Override
    public void run(String... args) throws Exception {
        log.info("LoadDataRunner.run - starting data load...");

        if (!appProperties.getImportOnStartup()) {
            log.warn("LoadDataRunner.run - Import is disabled, skipping data load.");
            return;
        }

        LocalDateTime startTime = LocalDateTime.now();

        processFileService.processFile(appProperties.getFilePath());

        var took = ChronoUnit.MILLIS.between(startTime, LocalDateTime.now());
        log.info("LoadDataRunner.run - ending data load. Took: {} ms", took);
    }
}
