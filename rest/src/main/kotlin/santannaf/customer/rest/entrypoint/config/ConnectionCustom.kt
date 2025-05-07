package santannaf.customer.rest.entrypoint.config

import io.r2dbc.pool.ConnectionPool
import io.r2dbc.pool.ConnectionPoolConfiguration
import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactoryOptions
import jakarta.annotation.PreDestroy
import java.time.Duration.ofSeconds
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.r2dbc.core.DatabaseClient

@Configuration
class ConnectionCustom(
    @Value("\${datasource.driver}") private val driver: String,
    @Value("\${datasource.protocol}") private val protocol: String,
    @Value("\${datasource.host}") private val host: String,
    @Value("\${datasource.port}") private val port: Int,
    @Value("\${datasource.user}") private val user: String,
    @Value("\${datasource.password}") private val password: String,
    @Value("\${datasource.database}") private val database: String,
    @Value("\${datasource.validationQuery}") private val validationQuery: String,
    @Value("\${datasource.maxIdleTime}") private val maxIdleTime: Long,
    @Value("\${datasource.initialSize}") private val initialSize: Int,
    @Value("\${datasource.maxSize}") private val maxSize: Int,
    @Value("\${datasource.name}") private val name: String
) {
    private var pool: ConnectionPool? = null

    @Bean
    fun pool(): ConnectionPool {
        val connectionFactory = ConnectionFactories.get(
            ConnectionFactoryOptions.builder()
                .option(ConnectionFactoryOptions.DRIVER, driver)
                .option(ConnectionFactoryOptions.PROTOCOL, protocol)
                .option(ConnectionFactoryOptions.HOST, host)
                .option(ConnectionFactoryOptions.PORT, port)
                .option(ConnectionFactoryOptions.USER, user)
                .option(ConnectionFactoryOptions.PASSWORD, password)
                .option(ConnectionFactoryOptions.DATABASE, database)
                .build()
        )

        val configuration = ConnectionPoolConfiguration.builder(connectionFactory)
            .validationQuery(validationQuery)
            .maxIdleTime(ofSeconds(maxIdleTime))
            .initialSize(initialSize)
            .maxSize(maxSize)
            .name(name)
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
