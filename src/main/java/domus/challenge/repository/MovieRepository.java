package domus.challenge.repository;

import domus.challenge.model.MovieEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface MovieRepository extends ReactiveCrudRepository<MovieEntity, Long> {
    Flux<MovieEntity> findAll();
}