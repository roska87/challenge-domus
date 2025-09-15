package domus.challenge.controller;

import domus.challenge.context.Constants;
import domus.challenge.testsupport.MoviesServiceStubConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(MoviesServiceStubConfig.class)
class DirectorsControllerStubTest {

    @Autowired
    WebTestClient webTestClient;

    private static final String PATH =
            String.join("", Constants.API_PATH, Constants.SERVICE_DIRECTORS);

    @Test
    void threshold2_onlyWoodyAllen() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(PATH)
                        .queryParam("threshold", 2).build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.directors.length()").isEqualTo(1)
                .jsonPath("$.directors[0]").isEqualTo("Woody Allen");
    }

    @Test
    void nonNumericThreshold_returns400() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(PATH)
                        .queryParam("threshold", "abc")
                        .build())
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void threshold0_returnsAllSortedAlphabetically() {
        // Con ExampleMoviesData hay 20 directores distintos (> 0 películas)
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(PATH)
                        .queryParam("threshold", 0)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.directors.length()").isEqualTo(20)
                // chequeos de orden (principio/fin) para validar sorting
                .jsonPath("$.directors[0]").isEqualTo("Alejandro G. Iñárritu")
                .jsonPath("$.directors[19]").isEqualTo("Woody Allen");
    }

    @Test
    void negativeThreshold_returnsEmptyList() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(PATH)
                        .queryParam("threshold", -5)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.directors.length()").isEqualTo(0);
    }
}
