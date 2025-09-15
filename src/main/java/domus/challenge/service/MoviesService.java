package domus.challenge.service;

import domus.challenge.integration.MoviesApiClient;
import domus.challenge.model.ApiMoviesPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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
     * "Smart" pagination:
     * - Read page=1 to know total_pages.
     * - Request the rest in parallel with limited concurrency.
     */
    public Flux<ApiMoviesPage> fetchAllPages() {                               // Returns a Flux that emits ALL pages.
        return fetchPage(1)                                                    // 1) Fetch page 1 (Mono<ApiMoviesPage>) to discover total_pages.
                .flatMapMany(first -> {                          // 2) When page 1 arrives, turn the Mono into a Flux built from it.
                    int totalPages = Math.max(first.getTotalPages(), 1);       // 3) Read total pages, guarding with a minimum of 1.
                    Flux<ApiMoviesPage> rest = Flux
                            .range(2, Math.max(totalPages - 1, 0))       // 4) Emit the sequence 2..N (count = totalPages - 1, never negative).
                            .flatMap(this::fetchPage, /* concurrency */ 6);    // 5) For each number, fetch that page in parallel (up to 6 concurrent requests).
                    return Flux.concat(Mono.just(first), rest);                // 6) Return a Flux that emits page 1 first, then the rest.
                });                                                            //    (ensures page 1 is always emitted before others).
    }
}
