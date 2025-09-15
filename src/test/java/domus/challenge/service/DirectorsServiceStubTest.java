package domus.challenge.service;

import domus.challenge.config.MoviesApiClientStubConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Import(MoviesApiClientStubConfig.class)
class DirectorsServiceStubTest {

    @Autowired
    DirectorsService directorsService;

    @Test
    void threshold2_onlyWoodyAllen_sorted() {
        StepVerifier.create(directorsService.findDirectorsOverThreshold(2))
                .assertNext(list ->
                        assertThat(list)
                                .containsExactly("Woody Allen"))
                .verifyComplete();
    }

    @Test
    void threshold0_allDirectors_sortedAlphabetically() {
        StepVerifier.create(directorsService.findDirectorsOverThreshold(0))
                .assertNext(list -> {
                    // esperamos 20 directores (ver ExampleMoviesData)
                    assertThat(list).hasSize(20);

                    // lista está ordenada alfabéticamente (case-insensitive)
                    List<String> sorted = new ArrayList<>(list);
                    sorted.sort(String.CASE_INSENSITIVE_ORDER);
                    assertThat(list).isEqualTo(sorted);

                    // chequeos de presencia de algunos nombres representativos
                    assertThat(list).contains(
                            "Alejandro G. Iñárritu", "Alfonso Cuarón", "Christopher Nolan",
                            "Denis Villeneuve", "Greta Gerwig", "Martin Scorsese",
                            "Quentin Tarantino", "Ridley Scott", "Steven Spielberg",
                            "Woody Allen"
                    );
                })
                .verifyComplete();
    }

    @Test
    void negativeThreshold_returnsEmpty() {
        StepVerifier.create(directorsService.findDirectorsOverThreshold(-1))
                .expectNext(List.of())
                .verifyComplete();
    }
}
