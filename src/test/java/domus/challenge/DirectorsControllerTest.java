package domus.challenge;

import domus.challenge.support.MockApiPages;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = DirectorsControllerTest.Initializer.class)
public class DirectorsControllerTest {

    static MockWebServer mockWebServer;

    @Autowired
    WebTestClient webTestClient;

    @BeforeAll
    static void setup() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext ctx) {
            // Inyectamos baseUrl del MockWebServer
            TestPropertyValues.of(
                    "app.moviesApiBaseUrl=http://localhost:" + getPort()
            ).applyTo(ctx.getEnvironment());
        }
        private static int getPort() {
            try {
                if (mockWebServer == null) {
                    mockWebServer = new MockWebServer();
                    mockWebServer.start();
                }
                return mockWebServer.getPort();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Test
    void returnsSortedDirectorsOverThreshold() {
        // page=1 dice total_pages=3
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(MockApiPages.page1(3))
                .setHeader("Content-Type", "application/json"));

        // page=2
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(MockApiPages.pageN(2))
                .setHeader("Content-Type", "application/json"));

        // page=3
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(MockApiPages.pageN(3))
                .setHeader("Content-Type", "application/json"));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/directors")
                        .queryParam("threshold", 2)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.directors").isArray()
                .jsonPath("$.directors").value(list -> {
                    @SuppressWarnings("unchecked")
                    List<String> directors = (List<String>) list;
                    // Woody Allen (3), Martin Scorsese (2), Nolan(1)
                    Assertions.assertEquals(List.of("Woody Allen"), directors);
                });
    }

    @Test
    void negativeThresholdReturnsEmpty() {
        // Aunque no llamemos a la API, devolvemos lista vacía por regla.
        webTestClient.get()
                .uri("/api/directors?threshold=-5")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.directors.length()").isEqualTo(0);
    }

    @Test
    void nonNumericThresholdReturns400() {
        webTestClient.get()
                .uri("/api/directors?threshold=abc")
                .exchange()
                .expectStatus().isBadRequest();
    }

}