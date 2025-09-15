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
            // Requirement: negative thresholds => empty list
            return Mono.just(List.of());
        }

        // Concurrent map (concurrency insurance) to keep the count of the movies by director.
        Map<String, Integer> counts = new ConcurrentHashMap<>();

        return moviesService.fetchAllPages()                                                                       // Gets a Flux<ApiMoviesPage> with all the movie pages.
                .flatMapIterable(page -> page.getData() == null ? List.<Movie>of() : page.getData()) // For each page, output each Movie; if data is null, output an empty list.
                .map(Movie::getDirector)                                                                           // Transforms each Movie into the name of its director (String).
                .filter(Objects::nonNull)                                                                          // Discard null directors to avoid NullPointerException.
                .map(String::trim)                                                                                 // Removes spaces at the beginning/end of the name.
                .filter(name -> !name.isEmpty())                                                            // Discard empty names after trim.
                .doOnNext(director -> counts.merge(director, 1, Integer::sum))                        // Increases the director counter (merge: if it does not exist, set it to 1, if it exists, add 1).
                .then(Mono.fromCallable(() ->                                                                     // When the previous flow finishes, it computes the final result in a Mono (encapsulated blocking operation).
                        counts.entrySet().stream()                                                                // Browse the map entries: director -> quantity.
                                .filter(e -> e.getValue() > threshold)                         // Keeps only directors with an amount strictly greater than the threshold.
                                .map(Map.Entry::getKey)                                                           // It remains only with the name of the director.
                                .sorted(String::compareToIgnoreCase)                                              // Sort alphabetically without case sensitivity.
                                .collect(Collectors.toList())                                                     // Collects the result into a List<String>.
                ));
    }

}
