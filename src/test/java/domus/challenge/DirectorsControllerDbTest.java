package domus.challenge;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class DirectorsControllerDbTest {

    @Autowired
    WebTestClient webTestClient;

    @Test
    void thresholdGreaterThan2_returnsVilleneuveAndNolan_sorted() {
        var body = webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/directors")
                        .queryParam("threshold", 2)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.directors").isArray()
                .returnResult()
                .getResponseBody();

        // Validación ligera del contenido
        // Extrae la lista con jsonPath en otra petición para fácil lectura
        webTestClient.get()
                .uri("/api/directors?threshold=2")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.directors[0]").isEqualTo("Christopher Nolan")
                .jsonPath("$.directors[1]").isEqualTo("Denis Villeneuve")
                .jsonPath("$.directors.length()").isEqualTo(2);
        assertThat(body).isNotNull();
    }

    @Test
    void thresholdGreaterThan4_returnsOnlyVilleneuve() {
        webTestClient.get()
                .uri("/api/directors?threshold=4")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.directors.length()").isEqualTo(1)
                .jsonPath("$.directors[0]").isEqualTo("Denis Villeneuve");
    }

    @Test
    void negativeThreshold_returnsEmptyList() {
        webTestClient.get()
                .uri("/api/directors?threshold=-3")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.directors.length()").isEqualTo(0);
    }

    @Test
    void nonNumericThreshold_returns400() {
        webTestClient.get()
                .uri("/api/directors?threshold=abc")
                .exchange()
                .expectStatus().isBadRequest();
    }

}
