package com.humaninterest.kotlindemo.data.service

import com.humaninterest.kotlindemo.api.request.PostJournalEntryRequest
import com.humaninterest.kotlindemo.api.response.JournalEntryAccountBalance
import com.humaninterest.kotlindemo.api.response.PostedJournalEntryResponse
import com.humaninterest.kotlindemo.data.conversion.BigDecimalScaler.scaleToLong
import com.humaninterest.kotlindemo.data.conversion.BigDecimalScaler.unscaleToBigDecimal
import com.humaninterest.kotlindemo.data.model.BalanceType
import com.humaninterest.kotlindemo.data.model.JournalEntry
import com.humaninterest.kotlindemo.data.model.LedgerAccount
import com.humaninterest.kotlindemo.data.model.LedgerAccountBalance
import com.humaninterest.kotlindemo.data.repository.JournalEntryRepository
import io.r2dbc.postgresql.codec.Json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.stereotype.Service
import org.springframework.transaction.ReactiveTransactionManager
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait
import java.util.UUID

interface JournalEntryService {
    suspend fun save(request: PostJournalEntryRequest): PostedJournalEntryResponse
}

@Service
class JournalEntryServiceImpl(
    private val txManager: ReactiveTransactionManager,
    private val accountService: LedgerAccountService,
    private val balanceService: BalanceService,
    private val journalEntryRepository: JournalEntryRepository,
) : JournalEntryService {
    override suspend fun save(request: PostJournalEntryRequest): PostedJournalEntryResponse {
        val scaledAmount = request.amount.scaleToLong()
        val debitBalance =
            request.debitAccountId?.let {
                adjustBalance(it, BalanceType.DEBIT, scaledAmount)
            }

        val creditBalance =
            request.creditAccountId?.let {
                adjustBalance(it, BalanceType.CREDIT, scaledAmount)
            }

        return updateBalancesAndSave(listOfNotNull(debitBalance, creditBalance)) {
            journalEntryRepository.save(
                JournalEntry(
                    event = request.event.uppercase(),
                    type = request.type.uppercase(),
                    creditAccountId = creditBalance?.accountId,
                    debitAccountId = debitBalance?.accountId,
                    amount = scaledAmount,
                    transactionDate = request.transactionDate,
                    metadata = Json.of("{}"),
                    relatedEntryId = request.relatedEntryId,
                ).markForCreate(System.currentTimeMillis()),
            )
        }.let { entry ->
            PostedJournalEntryResponse(
                id = entry.id,
                event = entry.event,
                type = entry.type,
                amount = request.amount,
                transactionDate = entry.transactionDate,
                relatedEntryId = entry.relatedEntryId,
                creditBalance = creditBalance?.let {
                    JournalEntryAccountBalance(
                        accountId = it.accountId,
                        accountName = accountService.getById(it.accountId).name,
                        balance = it.amount.unscaleToBigDecimal(),
                    )
                },
                debitBalance = debitBalance?.let {
                    JournalEntryAccountBalance(
                        accountId = it.accountId,
                        accountName = accountService.getById(it.accountId).name,
                        balance = it.amount.unscaleToBigDecimal(),
                    )
                },
            )
        }
    }

    private suspend fun adjustBalance(accountId: UUID, balanceType: BalanceType, amount: Long): LedgerAccountBalance {
        val (acct, bal) = getAccountAndBalance(accountId)
        return when (balanceType) {
            BalanceType.CREDIT -> {
                bal.credit(acct.balanceType.creditValue(amount))
            }

            BalanceType.DEBIT -> {
                bal.debit(acct.balanceType.debitValue(amount))
            }
        }
    }

    private suspend fun getAccountAndBalance(accountId: UUID): Pair<LedgerAccount, LedgerAccountBalance> {
        return withContext(Dispatchers.IO) {
            val account =
                async {
                    accountService.getById(accountId)
                }

            val balance =
                async {
                    balanceService.getByAccountId(accountId)
                }

            listOf(account, balance).awaitAll()
            Pair(account.await(), balance.await())
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private suspend fun updateBalancesAndSave(balances: List<LedgerAccountBalance>, block: suspend () -> JournalEntry): JournalEntry {
        val txOp = TransactionalOperator.create(txManager)

        return txOp.executeAndAwait { tx ->
            try {
                try {
                    balances.forEach { b ->
                        balanceService.save(b)
                    }
                    block()
                } catch (e: OptimisticLockingFailureException) {
                    tx.setRollbackOnly()
                    throw e
                } catch (t: Throwable) {
                    tx.setRollbackOnly()
                    throw t
                }
            } finally {
                if (tx.isRollbackOnly) {
                    balanceService.evictAll(balances)
                }
            }
        }
    }
}
