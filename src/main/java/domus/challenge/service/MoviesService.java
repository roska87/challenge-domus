package domus.challenge.service;

import domus.challenge.integration.MoviesApiClient;
import domus.challenge.model.ApiMoviesPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class MoviesService {

    private final MoviesApiClient moviesApiClient;

    public Mono<ApiMoviesPage> fetchPage(int page) {
        return moviesApiClient.fetchPage(page);
    }

    /**
     * Paginación "inteligente":
     * - Lee page=1 para conocer total_pages.
     * - Solicita el resto en paralelo con concurrencia limitada.
     */
    public Flux<ApiMoviesPage> fetchAllPages() {
        return fetchPage(1)
                .flatMapMany(first -> {
                    int totalPages = Math.max(first.getTotalPages(), 1);
                    Flux<ApiMoviesPage> rest = Flux
                            .range(2, Math.max(totalPages - 1, 0))
                            .flatMap(this::fetchPage, /* concurrency */ 6);
                    return Flux.concat(Mono.just(first), rest);
                });
    }
}
