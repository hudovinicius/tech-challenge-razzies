package br.com.razzie.controller;

import br.com.razzie.dtos.WinRangeResponseDTO;
import br.com.razzie.services.ProducerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/producers")
@Tag(name = "Producer")
public class ProducerController {

    private final ProducerService producerService;

    @GetMapping(path = "/intervals", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Find producers with the longest and shortest interval between awards")
    public ResponseEntity<WinRangeResponseDTO> findConsecutiveWinners() {
        log.debug("ProducerController.findConsecutiveWinners - start - Fetching winners ranges");
        LocalDateTime startTime = LocalDateTime.now();

        var response = producerService.findConsecutiveWinners();

        var took = ChronoUnit.MILLIS.between(startTime, LocalDateTime.now());
        log.debug("ProducerController.findConsecutiveWinners - end - Fetching winners ranges took: {} ms", took);

        return ResponseEntity.ok(response);
    }
}
