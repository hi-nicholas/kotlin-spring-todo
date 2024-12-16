package com.humaninterest.kotlindemo.test.client

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.humaninterest.kotlindemo.api.request.PostJournalEntryRequest
import com.humaninterest.kotlindemo.api.response.PostedJournalEntryResponse
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient

class ApiTestClient {
    private val webClient: WebClient
    private val om = ObjectMapper().findAndRegisterModules()
        .registerModule(KotlinModule.Builder().enable(KotlinFeature.NullIsSameAsDefault).build())
        .disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)
    init {

        val exchangeStrat = ExchangeStrategies.builder().codecs { cfg ->
            cfg.defaultCodecs().jackson2JsonDecoder(Jackson2JsonDecoder(om))
            cfg.defaultCodecs().jackson2JsonEncoder(Jackson2JsonEncoder(om))
            cfg.defaultCodecs().maxInMemorySize(32 * 1024 * 1024)
        }.build()

        webClient = WebClient.builder().baseUrl("http://localhost:8080")
            .exchangeStrategies(exchangeStrat)
            // .clientConnector(ReactorClientHttpConnector(reactor.netty.http.client.HttpClient.newConnection()))
            .build()
    }

    suspend fun send(body: PostJournalEntryRequest): PostedJournalEntryResponse {
        return webClient.post().uri { uri ->
            uri.path("/journal-entry").build()
        }.bodyValue(body).retrieve().bodyToMono(PostedJournalEntryResponse::class.java).awaitSingle()
    }
}
