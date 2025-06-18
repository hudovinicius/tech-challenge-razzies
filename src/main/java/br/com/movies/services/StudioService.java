package br.com.movies.services;

import br.com.movies.dtos.StudioRegisterDTO;
import br.com.movies.models.Studio;

import java.util.Set;

public interface StudioService {

    Set<Studio> register(Set<StudioRegisterDTO> studios);

    Studio findOrCreateStudio(StudioRegisterDTO studio);
}
