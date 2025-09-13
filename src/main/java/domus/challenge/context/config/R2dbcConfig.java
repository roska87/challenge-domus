package domus.challenge.context.config;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.r2dbc.connection.init.CompositeDatabasePopulator;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;

@Configuration
public class R2dbcConfig {

    // schema común
    @Value("${app.db.schema:classpath:db/schema.sql}")
    private Resource schemaResource;

    // por defecto usa data.sql; en test lo sobreescribimos a data-test.sql
    @Value("${app.db.data:classpath:db/data.sql}")
    private Resource dataResource;

    @Bean
    public ConnectionFactoryInitializer initializer(ConnectionFactory connectionFactory) {
        var initializer = new ConnectionFactoryInitializer();
        initializer.setConnectionFactory(connectionFactory);

        var populator = new CompositeDatabasePopulator();
        populator.addPopulators(new ResourceDatabasePopulator(schemaResource));
        populator.addPopulators(new ResourceDatabasePopulator(dataResource));

        initializer.setDatabasePopulator(populator);
        return initializer;
    }

}
