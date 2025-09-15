package domus.challenge.integration;

import domus.challenge.model.ApiMoviesPage;
import reactor.core.publisher.Mono;

public interface MoviesApiClient {

    Mono<ApiMoviesPage> fetchPage(int page);

}