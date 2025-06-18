package br.com.movies.services;

import br.com.movies.dtos.MovieRegisterDTO;

public interface MovieService {

    void register(MovieRegisterDTO movie);
}
