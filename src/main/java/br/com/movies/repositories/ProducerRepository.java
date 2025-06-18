package br.com.movies.repositories;

import br.com.movies.models.Producer;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ProducerRepository extends CrudRepository<Producer, Long> {

    Optional<Producer> findByName(String name);
}
