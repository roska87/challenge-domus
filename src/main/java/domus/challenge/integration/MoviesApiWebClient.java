package domus.challenge.integration;

import domus.challenge.model.ApiMoviesPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Slf4j
//@Component
@RequiredArgsConstructor
public class MoviesApiWebClient implements MoviesApiClient {

    private final WebClient moviesApiWebClient;

    @Value("${app.moviesApiBaseUrl}")
    private String baseUrl;

    @Override
    public Mono<ApiMoviesPage> fetchPage(int page) {
        int p = Math.max(page, 1);
        return moviesApiWebClient.get()
                .uri(uri -> uri.path("/api/movies/search").queryParam("page", p).build())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, resp ->
                        resp.bodyToMono(String.class)
                                .defaultIfEmpty("Client error")
                                .map(msg -> new WebClientResponseException(
                                        msg, resp.statusCode().value(),
                                        resp.statusCode().toString(), null, null, null))
                )
                .onStatus(HttpStatusCode::is5xxServerError, resp ->
                        resp.bodyToMono(String.class)
                                .defaultIfEmpty("Server error")
                                .map(msg -> new WebClientResponseException(
                                        msg, resp.statusCode().value(),
                                        resp.statusCode().toString(), null, null, null))
                )
                .bodyToMono(ApiMoviesPage.class)
                .doOnNext(pge -> log.info("Fetched page={} items={} from {}",
                        pge.getPage(), pge.getData() == null ? null : pge.getData().size(), baseUrl));
    }

}