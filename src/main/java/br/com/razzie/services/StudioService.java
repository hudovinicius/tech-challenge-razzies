package br.com.razzie.services;

import br.com.razzie.dtos.StudioRegisterDTO;
import br.com.razzie.models.Studio;

import java.util.Set;

public interface StudioService {

    Set<Studio> register(Set<StudioRegisterDTO> studios);

    Studio findOrCreateStudio(StudioRegisterDTO studio);
}
