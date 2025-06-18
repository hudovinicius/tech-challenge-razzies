package br.com.movies.repositories;

import br.com.movies.models.Movie;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface MovieRepository extends CrudRepository<Movie, Long> {

    Optional<Movie> findByTitle(String name);
}
