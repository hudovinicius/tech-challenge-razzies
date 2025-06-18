package br.com.movies.movies.repositories;

import br.com.movies.movies.models.Movie;
import org.springframework.data.repository.CrudRepository;

public interface MovieRepository extends CrudRepository<Movie, Long> {
}
