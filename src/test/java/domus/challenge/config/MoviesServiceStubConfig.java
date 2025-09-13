package domus.challenge.testsupport;

import domus.challenge.model.ApiMoviesPage;
import domus.challenge.service.MoviesService;
import domus.challenge.support.ExampleMoviesData;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@TestConfiguration
public class MoviesServiceStubConfig {

    @Bean
    @Primary
    MoviesService moviesServiceStub() {
        // Subclase anónima: ignora el WebClient real y devuelve los datos seed.
        return new MoviesService(null) {
            @Override
            public Mono<ApiMoviesPage> fetchPage(int page) {
                return Mono.just(ExampleMoviesData.page(page));
            }

            @Override
            public Flux<ApiMoviesPage> fetchAllPages() {
                return Flux.fromIterable(ExampleMoviesData.pages());
            }
        };
    }
}
