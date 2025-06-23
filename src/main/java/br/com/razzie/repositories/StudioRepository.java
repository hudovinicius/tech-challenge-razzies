package br.com.razzie.repositories;

import br.com.razzie.models.Studio;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface StudioRepository extends CrudRepository<Studio, Long> {

    Optional<Studio> findByName(String name);
}
