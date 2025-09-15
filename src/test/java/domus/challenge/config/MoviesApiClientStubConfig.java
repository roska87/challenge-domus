package domus.challenge.config;

import domus.challenge.integration.MoviesApiClient;
import domus.challenge.model.ApiMoviesPage;
import domus.challenge.support.ExampleMoviesData;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

@TestConfiguration
public class MoviesApiClientStubConfig {
    @Bean @Primary
    MoviesApiClient moviesApiClientStub() {
        return page -> Mono.just(ExampleMoviesData.page(page));
    }
}