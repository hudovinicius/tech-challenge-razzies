package br.com.movies.services;

import br.com.movies.dtos.StudioRegisterDTO;
import br.com.movies.models.Studio;
import br.com.movies.repositories.StudioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class StudioServiceImpl implements StudioService {

    private final StudioRepository studioRepository;

    @Override
    public Set<Studio> register(Set<StudioRegisterDTO> studios) {
        return studios.stream()
                .map(this::findOrCreateStudio)
                .collect(java.util.stream.Collectors.toSet());
    }

    @Override
    public Studio findOrCreateStudio(StudioRegisterDTO studio) {
        log.debug("StudioServiceImpl.findOrCreateStudio - start - Finding or creating producer: {}", studio.name());

        return studioRepository.findByName(studio.name()).orElseGet(() -> {
            log.debug("StudioServiceImpl.findOrCreateStudio - Creating new studio: {}", studio.name());
            return studioRepository.save(
                Studio.builder()
                    .name(studio.name())
                    .build()
            );
        });
    }
}
