package domus.challenge.service;

import domus.challenge.model.Movie;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DirectorsService {

    private final MoviesService moviesService;

    public Mono<List<String>> findDirectorsOverThreshold(int threshold) {
        if (threshold < 0) {
            // Requisito: umbrales negativos => lista vacía
            return Mono.just(List.of());
        }

        // Mapa concurrente para contar por director de forma reactiva
        Map<String, Integer> counts = new ConcurrentHashMap<>();

        return moviesService.fetchAllPages()
                .flatMapIterable(page -> page.getData() == null ? List.<Movie>of() : page.getData())
                .map(Movie::getDirector)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(name -> !name.isEmpty())
                .doOnNext(director -> counts.merge(director, 1, Integer::sum))
                .then(Mono.fromCallable(() ->
                        counts.entrySet().stream()
                                .filter(e -> e.getValue() > threshold)
                                .map(Map.Entry::getKey)
                                .sorted(String::compareToIgnoreCase)
                                .collect(Collectors.toList())
                ));
    }

}
