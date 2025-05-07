package santannaf.customer.rest.entrypoint.config

import io.r2dbc.pool.ConnectionPool
import io.r2dbc.pool.ConnectionPoolConfiguration
import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactoryOptions
import jakarta.annotation.PreDestroy
import java.time.Duration.ofSeconds
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.r2dbc.core.DatabaseClient

@Configuration
class ConnectionCustom {
    private var pool: ConnectionPool? = null

    @Bean
    fun pool(): ConnectionPool {
        val connectionFactory = ConnectionFactories.get(
            ConnectionFactoryOptions.builder()
                .option(ConnectionFactoryOptions.DRIVER, "pool")
                .option(ConnectionFactoryOptions.PROTOCOL, "postgresql")
                .option(ConnectionFactoryOptions.HOST, "localhost")
                .option(ConnectionFactoryOptions.PORT, 5432)
                .option(ConnectionFactoryOptions.USER, "rinha")
                .option(ConnectionFactoryOptions.PASSWORD, "rinha")
                .option(ConnectionFactoryOptions.DATABASE, "rinha")
                .build()
        )

        val configuration = ConnectionPoolConfiguration.builder(connectionFactory)
            .validationQuery("select 1")
            .maxIdleTime(ofSeconds(2))
            .initialSize(15)
            .maxSize(15)
            .name("rinha_r2dbc")
            .build()

        pool = ConnectionPool(configuration)
        return pool!!
    }

    @Bean
    fun client(pool: ConnectionPool): DatabaseClient {
        return DatabaseClient.create(pool)
    }

    @PreDestroy
    fun destroy() {
        pool?.let { if (!it.isDisposed) it.dispose() }
    }
}
