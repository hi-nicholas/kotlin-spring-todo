package com.humaninterest.kotlindemo.data.kafka

import com.humaninterest.kotlindemo.data.service.LedgerAccountService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.InitializingBean
import org.springframework.cache.CacheManager
import org.springframework.context.annotation.Profile
import org.springframework.context.event.EventListener
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

/**
 * Balance consumer implementation to handle Spring ApplicationEvents
 * which get thrown when the 'kafka' profile is disabled.
 */
@Service
@Profile("!kafka")
class SpringApplicationEventBalanceConsumer(
    jdbcTemplate: JdbcTemplate,
    accountService: LedgerAccountService,
    cacheManager: CacheManager,
) : BalancerConsumer(jdbcTemplate, accountService, cacheManager), InitializingBean {
    private val logger = KotlinLogging.logger {}

    override fun afterPropertiesSet() {
        logger.info {
            "Initializing Spring Application Event balance consumer..."
        }
    }

    @EventListener(PostedJournalEntryEvent::class)
    @Async
    fun handleEvent(event: PostedJournalEntryEvent) {
        try {
            handlePayload(event.source)
        } catch (t: Throwable) {
            logger.error(t) {
                "Exception occurred handling PostedJournalEntryEvent"
            }
        }
    }
}
