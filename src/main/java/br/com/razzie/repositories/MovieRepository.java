package br.com.razzie.repositories;

import br.com.razzie.models.Movie;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface MovieRepository extends CrudRepository<Movie, Long> {

    Optional<Movie> findByTitle(String name);
}
