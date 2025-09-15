package domus.challenge.context.config;

import domus.challenge.integration.MoviesApiClient;
import domus.challenge.model.ApiMoviesPage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
@Configuration
public class MoviesIntegrationConfig {

    @Bean
    public MoviesApiClient moviesApiClient(
            WebClient.Builder builder,
            @Value("${app.moviesApiBaseUrl}") String baseUrl) {

        WebClient client = builder.baseUrl(baseUrl).build();

        return page -> {
            int p = Math.max(page, 1);
            return client.get()
                    .uri(uri -> uri.path("/api/movies/search").queryParam("page", p).build())
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, resp ->
                            resp.bodyToMono(String.class).defaultIfEmpty("Client error")
                                    .map(msg -> new WebClientResponseException(
                                            msg, resp.statusCode().value(), resp.statusCode().toString(), null, null, null)))
                    .onStatus(HttpStatusCode::is5xxServerError, resp ->
                            resp.bodyToMono(String.class).defaultIfEmpty("Server error")
                                    .map(msg -> new WebClientResponseException(
                                            msg, resp.statusCode().value(), resp.statusCode().toString(), null, null, null)))
                    .bodyToMono(ApiMoviesPage.class)
                    .doOnNext(pge -> log.info("Fetched page={} items={} from {}",
                            pge.getPage(), pge.getData() == null ? null : pge.getData().size(), baseUrl));
        };
    }
}
