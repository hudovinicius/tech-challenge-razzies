package br.com.razzie.services;

import br.com.razzie.dtos.MovieRegisterDTO;
import br.com.razzie.models.Movie;
import br.com.razzie.repositories.MovieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final ProducerService producerService;
    private final StudioService studioService;
    private final MovieRepository movieRepository;

    @Override
    @Transactional
    public void register(MovieRegisterDTO movie) {

        log.debug("MovieServiceImpl.register - start - Registering movie: {}", movie.title());

        var producers = producerService.register(movie.producers());
        var studios = studioService.register(movie.studios());

        movieRepository.findByTitle(movie.title()).orElseGet(() -> {
            log.debug("MovieServiceImpl.register - Creating new movie: {}", movie.title());

            return movieRepository.save (
                Movie.builder()
                    .title(movie.title())
                    .year(movie.year())
                    .winner(movie.winner())
                    .studios(studios)
                    .producers(producers)
                    .build()
            );
        });

        log.debug("MovieServiceImpl.register - end - Registering movie: {}", movie.title());

    }
}
