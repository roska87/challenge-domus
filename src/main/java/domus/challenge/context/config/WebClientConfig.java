package domus.challenge.context.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

    @Value("${app.moviesApiBaseUrl}")
    private String moviesApiBaseUrl;

    @Bean
    public WebClient moviesApiWebClient() {
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(c -> c.defaultCodecs().maxInMemorySize(4 * 1024 * 1024))
                .build();

        HttpClient httpClient = HttpClient.create().wiretap(true);

        return WebClient.builder()
                .baseUrl(moviesApiBaseUrl)
                .exchangeStrategies(strategies)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .filter(logRequest())
                .filter(logResponse())
                .build();
    }

    private ExchangeFilterFunction logRequest() {
        return (request, next) -> {
            String headers = request.headers().entrySet().stream()
                    .map(e -> e.getKey() + "=" + e.getValue())
                    .reduce((a,b) -> a + ", " + b).orElse("");
            org.slf4j.LoggerFactory.getLogger("WEBCLIENT")
                    .info(">> {} {} [{}]", request.method(), request.url(), headers);
            return next.exchange(request);
        };
    }

    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(response -> {
            org.slf4j.LoggerFactory.getLogger("WEBCLIENT")
                    .info("<< status={} headers={}", response.statusCode(), response.headers().asHttpHeaders());
            return Mono.just(response);
        });
    }
}
