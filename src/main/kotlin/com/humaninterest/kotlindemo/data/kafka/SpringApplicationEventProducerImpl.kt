package com.humaninterest.kotlindemo.data.kafka

import com.humaninterest.kotlindemo.api.response.PostedJournalEntryResponse
import org.springframework.context.ApplicationEvent
import org.springframework.context.annotation.Profile
import org.springframework.context.event.ApplicationEventMulticaster
import org.springframework.stereotype.Service
import java.io.Serial

/**
 * When Kafka is disabled, mimic the async event handling with Spring ApplicationEvents.
 */
@Service
@Profile("!kafka")
class SpringApplicationEventProducerImpl(
    private val publisher: ApplicationEventMulticaster,
) : KafkaJournalEntryProducer {

    override fun produce(entry: PostedJournalEntryResponse) {
        publisher.multicastEvent(PostedJournalEntryEvent(entry))
    }
}

class PostedJournalEntryEvent(entry: PostedJournalEntryResponse) : ApplicationEvent(entry) {
    override fun getSource(): PostedJournalEntryResponse {
        return super.getSource() as PostedJournalEntryResponse
    }

    companion object {
        @Serial
        private const val serialVersionUID: Long = -1406105511175734251L
    }
}
