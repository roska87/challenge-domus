package domus.challenge.context.config;

import com.fasterxml.jackson.databind.MapperFeature;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {
    @Bean
    Jackson2ObjectMapperBuilderCustomizer caseInsensitive() {
        return b -> b.featuresToEnable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
    }
}