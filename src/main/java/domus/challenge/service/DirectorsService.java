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

        // Mapa concurrente (seguro ante concurrencia) para llevar el conteo de películas por director.
        Map<String, Integer> counts = new ConcurrentHashMap<>();

        return moviesService.fetchAllPages() // Obtiene un Flux<ApiMoviesPage> con todas las páginas de películas.
                .flatMapIterable(page -> page.getData() == null ? List.<Movie>of() : page.getData()) // Por cada página, emite cada Movie; si data es null, emite lista vacía.
                .map(Movie::getDirector) // Transforma cada Movie en el nombre de su director (String).
                .filter(Objects::nonNull) // Descarta directores nulos para evitar NullPointerException.
                .map(String::trim) // Elimina espacios en blanco al inicio/fin del nombre.
                .filter(name -> !name.isEmpty()) // Descarta nombres vacíos tras el trim.
                .doOnNext(director -> counts.merge(director, 1, Integer::sum)) // Incrementa el contador del director (merge: si no existe pone 1, si existe suma 1).
                .then(Mono.fromCallable(() -> // Cuando el flujo anterior termina, calcula el resultado final en un Mono (operación bloqueante encapsulada).
                        counts.entrySet().stream() // Recorre las entradas del mapa: director -> cantidad.
                                .filter(e -> e.getValue() > threshold) // Mantiene solo los directores con cantidad estrictamente mayor que el umbral (threshold).
                                .map(Map.Entry::getKey) // Se queda solo con el nombre del director.
                                .sorted(String::compareToIgnoreCase) // Ordena alfabéticamente sin diferenciar mayúsculas/minúsculas.
                                .collect(Collectors.toList()) // Recolecta el resultado en una List<String>.
                ));
    }

}
