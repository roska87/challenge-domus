package domus.challenge.component;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class CorrelationIdFilter implements WebFilter {
    private static final String CORRELATION_ID = "X-Correlation-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        String cid = exchange.getRequest().getHeaders().getFirst(CORRELATION_ID);
        if (cid == null || cid.isBlank()) cid = java.util.UUID.randomUUID().toString();
        var req = exchange.mutate().request(
                exchange.getRequest().mutate().header(CORRELATION_ID, cid).build()
        ).build();
        log.debug("CorrelationId={} {} {}", cid,
                req.getRequest().getMethod(), req.getRequest().getURI());
        String finalCid = cid;
        return chain.filter(req)
                .doOnTerminate(() -> log.debug("CorrelationId={} completed", finalCid));
    }
}
