package domus.challenge.service;

import domus.challenge.model.ApiMoviesPage;
import domus.challenge.model.Movie;
import domus.challenge.model.MovieEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MoviesService {

    private final R2dbcEntityTemplate template;

    @Value("${app.pagination.per-page:10}")
    private int perPage;

    public Mono<ApiMoviesPage> fetchPage(int page) {
        int pageNumber = Math.max(page, 1);
        long offset = (long) (pageNumber - 1) * perPage;

        Mono<Long> totalMono = template.count(Query.empty(), MovieEntity.class);

        Mono<List<Movie>> dataMono = template.select(
                        Query.empty()
                                .sort(Sort.by(Sort.Order.asc("id")))
                                .limit(perPage)
                                .offset(offset),
                        MovieEntity.class)
                .map(this::toDto)
                .collectList();

        return Mono.zip(totalMono, dataMono)
                .map(tuple -> {
                    long total = tuple.getT1();
                    List<Movie> data = tuple.getT2();
                    int totalPages = (int) Math.ceil(total / (double) perPage);

                    ApiMoviesPage p = new ApiMoviesPage();
                    p.setPage(pageNumber);
                    p.setPerPage(perPage);
                    p.setTotal(Math.toIntExact(total));
                    p.setTotalPages(Math.max(totalPages, 1));
                    p.setData(data);
                    return p;
                });
    }

    /**
     * “Paginación inteligente” local:
     * lee page=1 para conocer totalPages y solicita el resto en paralelo.
     */
    public Flux<ApiMoviesPage> fetchAllPages() {
        return fetchPage(1).flatMapMany(first -> {
            int totalPages = Math.max(first.getTotalPages(), 1);
            Flux<ApiMoviesPage> rest = Flux
                    .range(2, Math.max(totalPages - 1, 0))
                    .flatMap(this::fetchPage, /* concurrency */ 4);
            return Flux.concat(Mono.just(first), rest);
        });
    }

    private Movie toDto(MovieEntity e) {
        Movie m = new Movie();
        m.setTitle(e.getTitle());
        if (e.getReleaseYear() != null) {
            m.setReleaseYear(String.valueOf(e.getReleaseYear()));
        }
        m.setRated(e.getRated());
        m.setReleased(e.getReleased());
        m.setRuntime(e.getRuntime());
        m.setGenre(e.getGenre());
        m.setDirector(e.getDirector());
        m.setWriter(e.getWriter());
        m.setActors(e.getActors());
        return m;
    }
}