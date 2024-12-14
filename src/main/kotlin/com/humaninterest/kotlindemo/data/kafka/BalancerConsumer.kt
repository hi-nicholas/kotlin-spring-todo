package com.humaninterest.kotlindemo.data.kafka

import com.humaninterest.kotlindemo.api.response.PostedJournalEntryResponse
import com.humaninterest.kotlindemo.data.conversion.BigDecimalScaler.scaleToLong
import com.humaninterest.kotlindemo.data.conversion.getQuarter
import com.humaninterest.kotlindemo.data.model.BalanceType
import com.humaninterest.kotlindemo.data.model.analytics.BalanceCacheKey
import com.humaninterest.kotlindemo.data.service.LedgerAccountService
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.PreDestroy
import kotlinx.coroutines.runBlocking
import org.springframework.cache.CacheManager
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.io.Closeable
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID
import java.util.concurrent.Executors
import kotlin.math.abs
import kotlin.time.measureTime

/**
 * Base class to handle the logic for consuming journal entry events and re-calculating
 * period account balances for it.
 */
abstract class BalancerConsumer(
    jdbcTemplate: JdbcTemplate,
    protected val accountService: LedgerAccountService,
    cacheManager: CacheManager,
) : Closeable {
    private val jdbcTemplate = NamedParameterJdbcTemplate(jdbcTemplate)

    @Suppress("MagicNumber")
    private val threadPool = Executors.newFixedThreadPool(6)
    private val cache = cacheManager.getCache("BalanceAnalytics")!!

    protected fun handlePayload(payload: PostedJournalEntryResponse) {
        // Use the thread pool to update these concurrently.
        // Since we're using JDBC, coroutines won't really help us.

        payload.debitBalance?.let { balance ->
            threadPool.execute {
                val duration = measureTime {
                    process(BalanceType.DEBIT, payload.transactionDate, balance.accountId, payload.amount)
                }
                LOGGER.debug {
                    "${balance.accountId} - DEBIT duration: $duration"
                }
            }
        }

        payload.creditBalance?.let { balance ->
            threadPool.execute {
                val duration = measureTime {
                    process(BalanceType.CREDIT, payload.transactionDate, balance.accountId, payload.amount)
                }
                LOGGER.debug {
                    "${balance.accountId} - CREDIT duration: $duration"
                }
            }
        }
    }

    @PreDestroy
    override fun close() {
        threadPool.shutdown()
    }

    private fun process(type: BalanceType, date: LocalDate, accountId: UUID, amount: BigDecimal) {
        val account = runBlocking { accountService.getById(accountId) }

        val scaledAmount = when (type) {
            BalanceType.DEBIT -> account.balanceType.debitValue(amount.scaleToLong())
            BalanceType.CREDIT -> account.balanceType.creditValue(amount.scaleToLong())
        }

        val (creditAmount, debitAmount) = when (type) {
            BalanceType.CREDIT -> Pair(abs(scaledAmount), 0L)
            BalanceType.DEBIT -> Pair(0L, abs(scaledAmount))
        }

        val context = BalanceUpdateContext(
            amount = scaledAmount,
            creditAmount = creditAmount,
            debitAmount = debitAmount,
            accountId = account.id,
            year = date.year,
            quarter = date.getQuarter(),
            month = date.monthValue,
            day = date.dayOfMonth,
        )

        // NOTE: There should be a platform transaction manager to bridge these into a single tx.
        // However, in this example I didn't feel like fully booting up JDBC support in the auto-config
        // so we don't have one. As such the JDBC DataSource is set to auto commit and we're just roughing it.
        // We wouldn't want that in production.

        try {
            measureTime {
                updateYear(context)
                updateQuarter(context)
                updateMonth(context)
                updateDay(context)
            }.also { duration ->
                LOGGER.debug {
                    "JDBC balance updates for '$accountId': $duration"
                }
            }
        } catch (t: Throwable) {
            LOGGER.error(t) {
                "Exception updating balances for context $context"
            }
        }
    }

    private fun updateYear(context: BalanceUpdateContext) {
        jdbcTemplate.update(
            "INSERT INTO lab_year (account_id, amount, credits, debits, year) " +
                "VALUES (:accountId, :amount, :credits, :debits, :year) " +
                "ON CONFLICT(account_id, year) DO UPDATE SET amount = lab_year.amount + :amount, " +
                "credits = lab_year.credits + :credits, debits = lab_year.debits + :debits",
            MapSqlParameterSource(context.toBaseParams()),
        )
        cache.evict(BalanceCacheKey(context.accountId, context.year))
    }

    private fun updateQuarter(context: BalanceUpdateContext) {
        jdbcTemplate.update(
            "INSERT INTO lab_quarter (account_id, amount, credits, debits, year, quarter) VALUES (:accountId, :amount, :credits, :debits, :year, :quarter) " +
                "ON CONFLICT(account_id, year, quarter) DO UPDATE SET amount = lab_quarter.amount + :amount, " +
                "credits = lab_quarter.credits + :credits, debits = lab_quarter.debits + :debits",
            MapSqlParameterSource(context.toBaseParams().plus("quarter" to context.quarter)),
        )
        cache.evict(BalanceCacheKey(accountId = context.accountId, year = context.year, quarter = context.quarter))
    }

    private fun updateMonth(context: BalanceUpdateContext) {
        jdbcTemplate.update(
            "INSERT INTO lab_month (account_id, amount, credits, debits, year, month) VALUES (:accountId, :amount, :credits, :debits, :year, :month) " +
                "ON CONFLICT(account_id, year, month) DO UPDATE SET amount = lab_month.amount + :amount, " +
                "credits = lab_month.credits + :credits, debits = lab_month.debits + :debits",
            MapSqlParameterSource(context.toBaseParams().plus("month" to context.month)),
        )
        cache.evict(BalanceCacheKey(accountId = context.accountId, year = context.year, month = context.month))
    }

    private fun updateDay(context: BalanceUpdateContext) {
        jdbcTemplate.update(
            "INSERT INTO lab_day (account_id, amount, credits, debits, year, month, day) VALUES (:accountId, :amount, :credits, :debits, :year, :month, :day) " +
                "ON CONFLICT(account_id, year, month, day) DO UPDATE SET amount = lab_day.amount + :amount, " +
                "credits = lab_day.credits + :credits, debits = lab_day.debits + :debits",
            MapSqlParameterSource(context.toBaseParams().plus(mapOf("month" to context.month, "day" to context.day))),
        )
        cache.evict(BalanceCacheKey(accountId = context.accountId, year = context.year, month = context.month, day = context.day))
    }

    companion object {
        private val LOGGER = KotlinLogging.logger {}
    }
}

/**
 * Parameter context for the query handling to shorten method signatures.
 */
private data class BalanceUpdateContext(
    val amount: Long,
    val creditAmount: Long,
    val debitAmount: Long,
    val accountId: UUID,
    val year: Int,
    val quarter: Int,
    val month: Int,
    val day: Int,
) {
    fun toBaseParams(): Map<String, Any> {
        return mapOf(
            "accountId" to accountId,
            "amount" to amount,
            "credits" to creditAmount,
            "debits" to debitAmount,
            "year" to year,
        )
    }
}
