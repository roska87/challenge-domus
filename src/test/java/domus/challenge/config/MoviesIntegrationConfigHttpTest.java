package domus.challenge.config;

import domus.challenge.integration.MoviesApiClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.springframework.http.MediaType;

import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.test.StepVerifier;

import java.io.IOException;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ContextConfiguration(initializers = MoviesIntegrationConfigHttpTest.Initializer.class)
class MoviesIntegrationConfigHttpTest {

    static MockWebServer server;

    @Autowired
    MoviesApiClient api; // Bean provided by MoviesIntegrationConfig

    @BeforeAll
    static void start() throws IOException {
        server = new MockWebServer();
        server.start();
    }

    @AfterAll
    static void stop() throws IOException {
        server.shutdown();
    }

    // Reset the server before each test, preserving the port
    @BeforeEach
    void resetServer() throws IOException {
        int port = server.getPort();
        server.shutdown();                // shut down the server and clear status/queues
        server = new MockWebServer();     // fresh instance
        server.start(port);               // same port => baseUrl is still valid
    }

    /** Inject app.moviesApiBaseUrl with the mock server URL before the context loads */
    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override public void initialize(ConfigurableApplicationContext ctx) {
            // Ensure server started and set base URL withiut trailing slash
            try {
                if (server == null) { server = new MockWebServer(); server.start(); }
            } catch (IOException e) { throw new RuntimeException(e); }
            String baseUrl = "http://localhost:" + server.getPort();
            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                    ctx, "app.moviesApiBaseUrl=" + baseUrl
            );
        }
    }

    @Test
    void fetchPage_success_mapsJson_and_hitsCorrectPath() throws InterruptedException, IOException {
        // Arrange: enqueue page=1 with 2 movies
        String body = """
      {
        "page": 1,
        "per_page": 2,
        "total": 4,
        "total_pages": 2,
        "data": [
          { "Title":"Movie A","Year":"2012","Rated":"PG","Released":"2012-01-01","Runtime":"100","Genre":"Drama","Director":"Woody Allen","Writer":"","Actors":"" },
          { "Title":"Movie B","Year":"2013","Rated":"PG","Released":"2013-01-01","Runtime":"90","Genre":"Drama","Director":"Martin Scorsese","Writer":"","Actors":"" }
        ]
      }
      """;

        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setBody(body));

        // Act + Assert: API call maps fields
        StepVerifier.create(api.fetchPage(1))
                .assertNext(p -> {
                    assertThat(p.getPage()).isEqualTo(1);
                    assertThat(p.getPerPage()).isEqualTo(2);
                    assertThat(p.getTotal()).isEqualTo(4);
                    assertThat(p.getTotalPages()).isEqualTo(2);
                    assertThat(p.getData()).hasSize(2);
                    assertThat(p.getData().get(0).getTitle()).isEqualTo("Movie A");
                    assertThat(p.getData().get(0).getDirector()).isEqualTo("Woody Allen");
                })
                .verifyComplete();

        // Verify HTTP request details
        var recorded = server.takeRequest();
        assertThat(recorded.getMethod()).isEqualTo("GET");
        assertThat(recorded.getPath()).isEqualTo("/api/movies/search?page=1");
    }

    @Test
    void fetchPage_4xx_throws_WebClientResponseException() {
        server.enqueue(new MockResponse()
                .setResponseCode(404)
                .setHeader("Content-Type", MediaType.TEXT_PLAIN_VALUE)
                .setBody("Not Found"));

        StepVerifier.create(api.fetchPage(99))
                .expectErrorSatisfies(err -> {
                    assertThat(err).isInstanceOf(WebClientResponseException.class);
                    var wcre = (WebClientResponseException) err;
                    assertThat(wcre.getStatusCode().value()).isEqualTo(404);
                    assertThat(wcre.getStatusText()).contains("404 NOT_FOUND");
                })
                .verify();
    }

    @Test
    void fetchPage_5xx_throws_WebClientResponseException() {
        server.enqueue(new MockResponse()
                .setResponseCode(502)
                .setHeader("Content-Type", MediaType.TEXT_PLAIN_VALUE)
                .setBody("Bad Gateway"));

        StepVerifier.create(api.fetchPage(1))
                .expectErrorSatisfies(err -> {
                    assertThat(err).isInstanceOf(org.springframework.web.reactive.function.client.WebClientResponseException.class);
                    var wcre = (org.springframework.web.reactive.function.client.WebClientResponseException) err;
                    assertThat(wcre.getStatusCode().value()).isEqualTo(502);
                })
                .verify();
    }
}
