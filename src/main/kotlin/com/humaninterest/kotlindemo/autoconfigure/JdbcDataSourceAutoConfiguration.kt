package com.humaninterest.kotlindemo.autoconfigure

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.metadata.DataSourcePoolMetadataProvidersConfiguration
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.jdbc.core.JdbcTemplate
import javax.sql.DataSource

/**
 * NOTE!!!! This isn't how you would _actually_ do this in production.
 * By default, Spring won't configure a standard JDBC connection when R2DBC is activated.
 * It's easier to use JDBC for the stream event consumer example, so we're doing it this way.
 *
 * We have to ensure autoconfiguration after Flyway so we don't mess it up though.
 */
@AutoConfiguration(
    after = [
        R2dbcAutoConfiguration::class,
        DataSourceAutoConfiguration::class,
        FlywayAutoConfiguration::class,
        JdbcTemplateAutoConfiguration::class,
    ],
)
@ConditionalOnMissingBean(DataSource::class)
@EnableConfigurationProperties(DataSourceProperties::class)
@Import(DataSourcePoolMetadataProvidersConfiguration::class)
class JdbcDataSourceAutoConfiguration {
    @Bean
    @Suppress("MagicNumber")
    fun dataSource(properties: DataSourceProperties): DataSource {
        val config = HikariConfig().apply {
            driverClassName = "org.postgresql.Driver"
            jdbcUrl = properties.url
            username = properties.username
            password = properties.password
            isAutoCommit = true
            addDataSourceProperty("reWriteBatchedInserts", "true")
            addDataSourceProperty("stringtype", "unspecified")
            maximumPoolSize = 4
            minimumIdle = 0
        }

        return HikariDataSource(config)
    }

    @Bean
    @ConditionalOnMissingBean(JdbcTemplate::class)
    fun jdbcTemplate(dataSource: DataSource): JdbcTemplate {
        return JdbcTemplate(dataSource)
    }
}
