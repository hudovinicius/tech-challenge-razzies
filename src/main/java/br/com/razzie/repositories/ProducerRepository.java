package br.com.razzie.repositories;

import br.com.razzie.models.Producer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ProducerRepository extends CrudRepository<Producer, Long> {

    Optional<Producer> findByName(String name);

    @Query("SELECT p FROM Producer p JOIN p.movies m WHERE m.winner = true GROUP BY p HAVING COUNT(DISTINCT m.id) > 1")
    List<Producer> findProducersWithMultipleWins();
}
