CREATE TABLE movie (
    id BIGINT AUTO_INCREMENT NOT NULL,
    title VARCHAR(255),
    release_year INT,
    winner BOOLEAN,
    created_date TIMESTAMP DEFAULT now(),
    updated_date TIMESTAMP DEFAULT now()
);
ALTER TABLE movie ADD CONSTRAINT pk_movie PRIMARY KEY (id);

CREATE TABLE producer (
    id BIGINT AUTO_INCREMENT NOT NULL,
    name VARCHAR(255) NOT NULL,
    created_date TIMESTAMP DEFAULT now(),
    updated_date TIMESTAMP DEFAULT now()
);
ALTER TABLE producer ADD CONSTRAINT pk_producer PRIMARY KEY (id);

CREATE TABLE studio (
    id BIGINT AUTO_INCREMENT NOT NULL,
    name VARCHAR(255) NOT NULL,
    created_date TIMESTAMP DEFAULT now(),
    updated_date TIMESTAMP DEFAULT now()
);
ALTER TABLE studio ADD CONSTRAINT pk_studio PRIMARY KEY (id);

CREATE TABLE movie_producer (
    movie_id BIGINT NOT NULL,
    producer_id BIGINT NOT NULL
);
ALTER TABLE movie_producer ADD CONSTRAINT pk_movie_producer PRIMARY KEY (movie_id, producer_id);
ALTER TABLE movie_producer ADD CONSTRAINT fk_mp_movie_id FOREIGN KEY (movie_id) REFERENCES movie(id);
ALTER TABLE movie_producer ADD CONSTRAINT fk_mp_producer_id FOREIGN KEY (producer_id) REFERENCES producer(id);

CREATE TABLE movie_studio (
    movie_id BIGINT NOT NULL,
    studio_id BIGINT NOT NULL
);
ALTER TABLE movie_studio ADD CONSTRAINT pk_movie_studio PRIMARY KEY (movie_id, studio_id);
ALTER TABLE movie_studio ADD CONSTRAINT fk_ms_movie_id FOREIGN KEY (movie_id) REFERENCES movie(id);
ALTER TABLE movie_studio ADD CONSTRAINT fk_ms_studio_id FOREIGN KEY (studio_id) REFERENCES studio(id);