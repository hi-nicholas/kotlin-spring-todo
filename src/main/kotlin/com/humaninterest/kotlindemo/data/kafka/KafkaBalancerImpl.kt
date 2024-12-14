package com.humaninterest.kotlindemo.data.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import com.humaninterest.kotlindemo.api.response.PostedJournalEntryResponse
import com.humaninterest.kotlindemo.data.service.LedgerAccountService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.InitializingBean
import org.springframework.cache.CacheManager
import org.springframework.context.annotation.Profile
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
@Profile("kafka")
class KafkaBalancerImpl(
    jdbcTemplate: JdbcTemplate,
    accountService: LedgerAccountService,
    cacheManager: CacheManager,
    private val objectMapper: ObjectMapper,
) : BalancerConsumer(jdbcTemplate, accountService, cacheManager), InitializingBean {
    private val logger = KotlinLogging.logger {}

    override fun afterPropertiesSet() {
        logger.info {
            "Initializing Kafka balance consumer..."
        }
    }

    @KafkaListener(id = "KafkaBalancer", topics = [TOPIC_NAME], groupId = "com.humaninterest", autoStartup = "true")
    fun listen(payload: String) {
        try {
            handlePayload(objectMapper.readValue(payload, PostedJournalEntryResponse::class.java))
        } catch (t: Throwable) {
            logger.error(t) {
                "Exception occurred deserializing Kafka payload"
            }
        }
    }

    companion object {
        const val TOPIC_NAME = "journal_entry"
    }
}
