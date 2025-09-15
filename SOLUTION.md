# Domus Back‑End Developer Challenge — Solution Overview

> Tech stack: **Java 21**, **Spring Boot (WebFlux)**, **Reactor**, **Lombok**, **springdoc-openapi**. Build with **Maven**.

---

## 1) What the API does
Given the public movies API
```
GET https://challenge.iugolabs.com/api/movies/search?page=<pageNumber>
```
this service exposes a single endpoint:

```
GET /api/directors?threshold=X
```
It returns directors who have directed **strictly more** movies than `X` (across all pages), sorted alphabetically:

```json
{
  "directors": ["Christopher Nolan", "Denis Villeneuve"]
}
```

**Edge cases**
- `threshold < 0` → returns `[]` (empty list).
- Missing `threshold` → `400` with a clear error message.
- Non‑numeric `threshold` (e.g. `abc`) → `400` (WebFlux type conversion error mapped to Bad Request).

---

## 2) Architecture & Flow

```
+-------------------+        +-----------------------+        +------------------------+
| DirectorsController| ----> | DirectorsService      | ---->  | MoviesService          |
+-------------------+        +-----------------------+        +------------------------+
                                                             | uses MoviesApiClient   |
                                                             +-----------+------------+
                                                                         |
                                                          +--------------v----------------+
                                                          | MoviesIntegrationConfig       |
                                                          | (@Configuration) provides     |
                                                          | MoviesApiClient via           |
                                                          | WebClient.Builder (HTTP API)  |
                                                          +-------------------------------+
```

- **DirectorsController** parses `threshold` and delegates to the service.
- **DirectorsService** fetches all pages (reactively), counts movies per director using a thread‑safe map, filters by threshold, sorts, returns.
- **MoviesService** implements **smart pagination** (see §3) using **MoviesApiClient**.
- **MoviesApiClient** is an **interface**. In production, it is provided by **MoviesIntegrationConfig** (`@Configuration`), which builds a `WebClient` via Spring's `WebClient.Builder` to call the external API.
- In tests, the HTTP client is replaced with a **stub** of `MoviesApiClient` that serves deterministic in‑memory pages (no network).

---

## 3) Smart pagination (reactive)
1. **Fetch page 1** to read `total_pages`.
2. **Fetch pages 2..N in parallel** (bounded concurrency, e.g. 6) via `Flux.range(...).flatMap(this::fetchPage, 6)`.
3. Emit **page 1 first**, then the rest with `Flux.concat(Mono.just(first), rest)`.

```java
public Flux<ApiMoviesPage> fetchAllPages() {
  return fetchPage(1)
    .flatMapMany(first -> {
      int totalPages = Math.max(first.getTotalPages(), 1);
      Flux<ApiMoviesPage> rest = Flux
        .range(2, Math.max(totalPages - 1, 0))
        .flatMap(this::fetchPage, 6); // parallel, bounded
      return Flux.concat(Mono.just(first), rest); // page 1 first
    });
}
```

---

## 4) Error handling
- **Bad input**: WebFlux throws `ServerWebInputException` for non‑numeric `threshold`. Our `GlobalExceptionHandler` maps this (and other request errors) to **HTTP 400** with a `ProblemDetail` payload.
- **External API errors**: `MoviesApiWebClient` maps 4xx/5xx to a `WebClientResponseException` with the remote status code.
- **Null‑safety in reactive pipelines**: the chosen implementation uses `map(Movie::getDirector)` followed by a `filter(Objects::nonNull)`. This relies on the upstream data not returning `null` directors. If that changes, switch to a null‑safe variant (e.g., use `.handle(...)` or `Mono.justOrEmpty(...)`) to avoid Reactor's "mapper returned a null value" error.

---

## 5) Data model (Jackson mapping)
The external API uses **mixed casing**:
- Movie fields are capitalized (e.g., `"Title"`, `"Director"`).
- Page metadata is snake_case (e.g., `"per_page"`, `"total_pages"`).

DTOs use `@JsonProperty` to map correctly and set `data` to an empty list by default:

```java
@Data
public class Movie {
  @JsonProperty("Title")    private String title;
  @JsonProperty("Year")     private String year;
  @JsonProperty("Director") private String director;
  // ... Rated, Released, Runtime, Genre, Writer, Actors
}

@Data
public class ApiMoviesPage {
  @JsonProperty("page")        private int page;
  @JsonProperty("per_page")    private int perPage;
  @JsonProperty("total")       private int total;
  @JsonProperty("total_pages") private int totalPages;
  @JsonProperty("data")        private List<Movie> data = Collections.emptyList();
}
```

Global case-insensitive mapping is on by default in this project, so minor casing variations in the API won’t break deserialization.

---

## 6) Counting & sorting logic
The current implementation counts movies per director using a thread‑safe `ConcurrentHashMap<String, Integer>` and `merge` (safe even when pages are fetched in parallel):

```java
Map<String, Integer> counts = new ConcurrentHashMap<>();

return moviesService.fetchAllPages()                                  // Flux<ApiMoviesPage>
    .flatMapIterable(p -> p.getData() == null ? List.<Movie>of() : p.getData())
    .map(Movie::getDirector)                                          // map Movie -> director name
    .filter(Objects::nonNull)                                         // discard null directors
    .map(String::trim)                                                // normalize
    .filter(name -> !name.isEmpty())                                  // discard empty after trim
    .doOnNext(director -> counts.merge(director, 1, Integer::sum))    // thread‑safe increment
    .then(Mono.fromCallable(() ->
        counts.entrySet().stream()
              .filter(e -> e.getValue() > threshold)
              .map(Map.Entry::getKey)
              .sorted(String::compareToIgnoreCase)
              .toList()
    ));
```

> **Note:** this version assumes the external API always provides a non‑null `Director` field. If that assumption may not hold, prefer a null‑safe variant that never returns `null` from a `map`, e.g. using `.handle(...)` or `.flatMap(name -> Mono.justOrEmpty(name))` before further processing.

## 7) OpenAPI / Swagger
- Dependency: `org.springdoc:springdoc-openapi-starter-webflux-ui`.
- UI available at: **`/swagger-ui.html`** (default).
- The controller and DTOs are annotated to produce operation and schema docs.

---

## 8) Logging
- Per‑request logs for page fetching (e.g., `Fetched page=1 items=10`).
- `logging.level.domus.challenge=DEBUG` for project code; optional Reactor Netty wiretap in development.
- macOS DNS warning from Netty can be silenced or fixed by adding `netty-resolver-dns-native-macos` with the proper classifier.

---

## 9) Configuration
`src/main/resources/application.properties`
```properties
server.port=8080
app.moviesApiBaseUrl=https://challenge.iugolabs.com
springdoc.api-docs.enabled=true
springdoc.swagger-ui.path=/swagger-ui.html
logging.level.domus.challenge=DEBUG
```

Environment overrides:
- `APP_MOVIES_API_BASE_URL` (env var) or `--app.moviesApiBaseUrl=...` (CLI) override the base URL.

---

## 10) How to run
```bash
# Build & test
./mvnw clean verify

# Run
./mvnw spring-boot:run
# Swagger UI → http://localhost:8080/swagger-ui.html
# Endpoint   → http://localhost:8080/api/directors?threshold=2
```

Example cURL:
```bash
curl -s 'http://localhost:8080/api/directors?threshold=2' | jq
```

---

## 11) Testing strategy
- **Unit/Integration (WebFlux)** with `@SpringBootTest`.
- Replace the production `MoviesApiClient` with a **stub** (no network) via `@TestConfiguration` that **overrides** the bean. Since the production bean is created by `MoviesIntegrationConfig`, marking the stub `@Primary` is enough—no conditional annotations required:

```java
@TestConfiguration
public class MoviesApiClientStubConfig {
  @Bean @Primary MoviesApiClient moviesApiClientStub() {
    return page -> Mono.just(ExampleMoviesData.page(page)); // 5 pages × 10 items
  }
}
```

- Shared seed in `ExampleMoviesData` (50 movies over 5 pages) to produce deterministic results (e.g., only “Woody Allen” exceeds certain thresholds).
- Tests assert: input validation (400), negative threshold behavior, alphabetical sort, pagination coverage.

---

## 12) Notable considerations
- **Null‑safety** in reactive pipelines: never `map` to a nullable value; use `filter/handle` or `Mono.justOrEmpty`.
- **Back‑pressure**: bounded `flatMap` concurrency prevents overwhelming the external API.
- **Thread‑safety**: counting uses `ConcurrentHashMap`.
- **Extensibility**: swapping the external source is trivial—provide another `MoviesApiClient` bean.
- **Resilience** (future work): add timeouts, retries with backoff, circuit‑breaker (e.g., Resilience4j) if needed.
- **Validation**: consider a `@Min(0)` on the query param and Bean Validation messages if schema‑level enforcement is wanted.

---

## 13) Project layout
```
src/main/java/domus/challenge
├── ChallengeApplication.java
├── controller
│   └── DirectorsController.java
├── service
│   ├── MoviesService.java
│   └── DirectorsService.java
├── integration
│   └── MoviesApiClient.java        // interface
├── model
│   ├── ApiMoviesPage.java
│   └── Movie.java
├── exception
│   └── GlobalExceptionHandler.java
└── config
    └── MoviesIntegrationConfig.java // @Configuration that provides MoviesApiClient via WebClient.Builder
```

---

## 14) Troubleshooting
- **400 not returned for `threshold=abc`**: ensure `GlobalExceptionHandler` handles `ServerWebInputException` (WebFlux) as Bad Request.
- **Empty result in runtime but tests pass**: likely Jackson mapping—add `@JsonProperty` on DTOs as above.
- **Reactor NPE: “mapper returned a null value”**: this happens if `map(Movie::getDirector)` receives a movie with `Director = null` (the `filter` runs *after* `map`). Fix by switching to a null‑safe pipeline:
  - Replace `.map(Movie::getDirector).filter(Objects::nonNull)` with `.handle((m, sink) -> { var d = m.getDirector(); if (d != null && !d.isBlank()) sink.next(d.trim()); })`, **or**
  - Use `.map(Movie::getDirector).flatMap(name -> Mono.justOrEmpty(name))` before trimming/filtering.
- **macOS DNS warning**: add `io.netty:netty-resolver-dns-native-macos` with the proper classifier (e.g., `osx-aarch_64`) or set `io.netty.resolver.dns.macos.native=false`.
- **Failed to load ApplicationContext (tests)**: ensure test imports the `MoviesApiClient` stub (`@TestConfiguration`) and that it is marked `@Primary`. This will override the production bean provided by `MoviesIntegrationConfig`.

---

## 15) Notes
This code is intended solely for the Domus challenge demonstration. Replace external dependencies or dataset as needed for production environments.

