package domus.challenge.service;

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

    private final WebClient moviesApiWebClient;

    public Mono<ApiMoviesPage> fetchPage(int page) {
        int p = Math.max(page, 1);
        log.info("Fetching page {} to discover total_pages", p);
        return moviesApiWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/movies/search")
                        .queryParam("page", p)
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, resp ->
                        resp.bodyToMono(String.class)
                                .defaultIfEmpty("Client error")
                                .map(msg -> new WebClientResponseException(
                                        msg, resp.statusCode().value(),
                                        resp.statusCode().toString(),
                                        null, null, null))
                )
                .onStatus(HttpStatusCode::is5xxServerError, resp ->
                        resp.bodyToMono(String.class)
                                .defaultIfEmpty("Server error")
                                .map(msg -> new WebClientResponseException(
                                        msg, resp.statusCode().value(),
                                        resp.statusCode().toString(),
                                        null, null, null))
                )
                .bodyToMono(ApiMoviesPage.class)
                .doOnNext(amp -> log.info("Fetched page={} items={}", amp.getPage(),
                        amp.getData() == null ? null : amp.getData().size()));
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
