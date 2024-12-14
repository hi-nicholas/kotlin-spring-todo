package com.humaninterest.kotlindemo.data.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import com.humaninterest.kotlindemo.api.response.PostedJournalEntryResponse
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.annotation.Profile
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

interface KafkaJournalEntryProducer {
    fun produce(entry: PostedJournalEntryResponse)
}

@Service
@Profile("kafka")
class KafkaJournalEntryProducerImpl(
    private val template: KafkaTemplate<String, String>,
    private val objectMapper: ObjectMapper,
) : KafkaJournalEntryProducer {
    private val logger = KotlinLogging.logger {}

    override fun produce(entry: PostedJournalEntryResponse) {
        val future = template.send(TOPIC_NAME, entry.id.toString(), objectMapper.writeValueAsString(entry))
        future.whenComplete { _, t ->
            if (t != null) {
                logger.error(t) {
                    "Exception occurred writing journal entry '${entry.id}' to topic"
                }
            } else {
                logger.trace {
                    "Wrote ${entry.id} to topic"
                }
            }
        }
    }

    companion object {
        const val TOPIC_NAME = "journal_entry"
    }
}
