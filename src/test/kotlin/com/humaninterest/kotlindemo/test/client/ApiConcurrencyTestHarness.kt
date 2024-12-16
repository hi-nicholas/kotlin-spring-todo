package com.humaninterest.kotlindemo.test.client

import com.humaninterest.kotlindemo.api.request.PostJournalEntryRequest
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID
import java.util.concurrent.Executors
import kotlin.math.floor
import kotlin.time.Duration
import kotlin.time.measureTime
import kotlin.time.measureTimedValue

class ApiConcurrencyTestHarness {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val concurrency = args.firstOrNull()?.toIntOrNull() ?: 16
            val workers = Math.floorDiv(concurrency, 2)
            LOGGER.info { "Executing tests with concurrency of $concurrency ($workers workers)" }

            val threadPool = Executors.newFixedThreadPool(concurrency)
            try {
                val totalDuration = measureTime {
                    val jobs = (0..workers).map { idx ->
                        threadPool.submit {
                            runBlocking {
                                postEntries(idx).await()
                            }
                        }
                    }

                    jobs.forEach {
                        it.get()
                    }
                }

                LOGGER.info {
                    "Total duration: $totalDuration"
                }
            } finally {
                threadPool.shutdown()
            }
        }

        private suspend fun postEntries(workerNumber: Int): Deferred<List<Duration>> {
            val client = ApiTestClient()
            return withContext(Dispatchers.IO) {
                async(Dispatchers.IO) {
                    delay(floor((Math.random() * (100 + workerNumber)) + (Math.random() * 100)).toLong())
                    measureTimedValue {
                        TEST_TRANSACTIONS.mapIndexed { idx, tx ->
                            measureTime {
                                postEntry(workerNumber, idx, client, tx)
                            }.also {
                                LOGGER.info { "[$workerNumber:$idx] ${tx.type}:${tx.event}: $it" }
                            }
                        }
                    }.let {
                        LOGGER.info { "Worker $workerNumber completed ${TEST_TRANSACTIONS.size} transactions in ${it.duration}" }
                        it.value
                    }
                }
            }
        }

        private suspend fun postEntry(workerNumber: Int, idx: Int, client: ApiTestClient, payload: PostJournalEntryRequest, retryCount: Int = 0): Duration {
            val (e, d) = measureTimedValue {
                try {
                    client.send(payload)
                    false
                } catch (t: Throwable) {
                    if (retryCount >= 5) {
                        LOGGER.error(t) { "Too many errors retrying..." }
                    }
                    true
                }
            }

            return if (e && retryCount < 5) {
                LOGGER.info { "[$workerNumber:$idx] Retrying (${retryCount + 1}) ${payload.type}:${payload.event}..." }
                delay(floor(10 + (retryCount * 2) + (Math.random() * 100)).toLong())
                return postEntry(workerNumber, idx, client, payload, retryCount + 1) + d
            } else {
                d
            }
        }

        private val LOGGER = KotlinLogging.logger {}
        private val CUST_DEPOSIT_ACCT_ID = UUID.fromString("02df5cc6-7b6f-48a3-8a57-a4a4184fba08")
        private val PLAN_DEPOSIT_ACCT_ID = UUID.fromString("5dd1fcd6-86ae-4ef1-b088-22687a33f9eb")
        private val PPT_CONTRIB_ACCT_ID = UUID.fromString("ee44066d-23b0-42d4-9d8e-a128236ebac5")
        private val PPT_MATCH_ACCT_ID = UUID.fromString("3a78e7e8-a8ee-48af-96fd-eb657a96ba0f")
        private val TEST_TRANSACTIONS =
            listOf(
                PostJournalEntryRequest(
                    event = "DEPOSIT",
                    type = "ACH",
                    creditAccountId = null,
                    debitAccountId = CUST_DEPOSIT_ACCT_ID,
                    amount = BigDecimal.valueOf(1000.00),
                    transactionDate = LocalDate.now(),
                ),
                PostJournalEntryRequest(
                    event = "CONTRIBUTION",
                    type = "DEPOSIT",
                    creditAccountId = CUST_DEPOSIT_ACCT_ID,
                    debitAccountId = PLAN_DEPOSIT_ACCT_ID,
                    amount = BigDecimal.valueOf(1000.00),
                    transactionDate = LocalDate.now(),
                ),
                PostJournalEntryRequest(
                    event = "CONTRIBUTION",
                    type = "EE_PRETAX",
                    creditAccountId = PLAN_DEPOSIT_ACCT_ID,
                    debitAccountId = PPT_CONTRIB_ACCT_ID,
                    amount = BigDecimal.valueOf(750.00),
                    transactionDate = LocalDate.now(),
                ),
                PostJournalEntryRequest(
                    event = "CONTRIBUTION",
                    type = "MATCH",
                    creditAccountId = PLAN_DEPOSIT_ACCT_ID,
                    debitAccountId = PPT_MATCH_ACCT_ID,
                    amount = BigDecimal.valueOf(250.00),
                    transactionDate = LocalDate.now(),
                ),
            )
    }
}
