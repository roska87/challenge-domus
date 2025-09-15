package domus.challenge.service;

import domus.challenge.model.ApiMoviesPage;
import domus.challenge.testsupport.MoviesServiceStubConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Import(MoviesServiceStubConfig.class)
class MoviesServiceStubTest {

    @Autowired
    MoviesService moviesService;

    @Test
    void fetchAllPages_returnsFivePagesWithTenPerPage() {
        Flux<ApiMoviesPage> all = moviesService.fetchAllPages();

        StepVerifier.create(all.collectList())
                .assertNext(pages -> {
                    assertThat(pages).hasSize(5);
                    for (int i = 0; i < pages.size(); i++) {
                        var p = pages.get(i);
                        assertThat(p.getPage()).isEqualTo(i + 1);
                        assertThat(p.getPerPage()).isEqualTo(10);
                        assertThat(p.getTotal()).isEqualTo(50);
                        assertThat(p.getTotalPages()).isEqualTo(5);
                        assertThat(p.getData()).hasSize(10);
                    }
                })
                .verifyComplete();
    }
}
