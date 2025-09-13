package domus.challenge.service;

import domus.challenge.model.ApiMoviesPage;
import domus.challenge.model.Movie;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MoviesService {

    private final DatabaseClient client;

    @Value("${app.pagination.per-page:10}")
    private int perPage;

    public Mono<ApiMoviesPage> fetchPage(int page) {
        int pageNumber = Math.max(page, 1);
        int offset = (pageNumber - 1) * perPage;

        Mono<Integer> totalMono = client.sql("SELECT COUNT(*) AS cnt FROM movies")
                .map(row -> row.get("cnt", Number.class).intValue())
                .one();

        Mono<List<Movie>> dataMono = client.sql("""
                        SELECT title, release_year, rated, released, runtime, genre, director, writer, actors
                        FROM movies
                        ORDER BY id
                        LIMIT :limit OFFSET :offset
                        """)
                .bind("limit", perPage)
                .bind("offset", offset)
                .map(row -> {
                    Movie m = new Movie();
                    m.setTitle(row.get("title", String.class));
                    m.setReleaseYear(String.valueOf(row.get("release_year", Integer.class)));
                    m.setRated(row.get("rated", String.class));
                    m.setReleased(row.get("released", String.class));
                    m.setRuntime(row.get("runtime", String.class));
                    m.setGenre(row.get("genre", String.class));
                    m.setDirector(row.get("director", String.class));
                    m.setWriter(row.get("writer", String.class));
                    m.setActors(row.get("actors", String.class));
                    return m;
                })
                .all()
                .collectList();

        return Mono.zip(totalMono, dataMono)
                .map(tuple -> {
                    int total = tuple.getT1();
                    List<Movie> data = tuple.getT2();
                    int totalPages = (int) Math.ceil(total / (double) perPage);

                    ApiMoviesPage p = new ApiMoviesPage();
                    p.setPage(pageNumber);
                    p.setPerPage(perPage);
                    p.setTotal(total);
                    p.setTotalPages(totalPages);
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
}

