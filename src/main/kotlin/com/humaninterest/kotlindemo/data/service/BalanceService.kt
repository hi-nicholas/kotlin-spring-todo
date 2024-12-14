package com.humaninterest.kotlindemo.data.service

import com.humaninterest.kotlindemo.data.conversion.BigDecimalScaler.scaleToLong
import com.humaninterest.kotlindemo.data.model.LedgerAccountBalance
import com.humaninterest.kotlindemo.data.repository.LedgerAccountBalanceRepository
import kotlinx.coroutines.runBlocking
import org.springframework.cache.CacheManager
import org.springframework.stereotype.Service
import java.io.Serial
import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

interface BalanceService {
    suspend fun getByAccountId(accountId: UUID): LedgerAccountBalance

    suspend fun create(accountId: UUID, balance: BigDecimal = BigDecimal.ZERO): LedgerAccountBalance

    suspend fun save(balance: LedgerAccountBalance): LedgerAccountBalance

    fun evictAll(balances: Collection<LedgerAccountBalance>)
}

@Service
class BalanceServiceImpl(
    private val balanceRepository: LedgerAccountBalanceRepository,
    cacheManager: CacheManager,
) : BalanceService {
    private val cache = cacheManager.getCache("LedgerAccountBalance")!!

    override suspend fun getByAccountId(accountId: UUID): LedgerAccountBalance {
        return getOrPutCachedValue(BalanceLookupKey(accountId)) {
            balanceRepository.findFirstByAccountId(accountId)
        } ?: create(accountId)
    }

    override suspend fun save(balance: LedgerAccountBalance): LedgerAccountBalance {
        return saveBalance(BalanceLookupKey(balance.accountId)) {
            balanceRepository.save(
                balance.copy(
                    balanceDate = LocalDate.now(),
                    updatedAt = System.currentTimeMillis(),
                ),
            )
        }
    }

    override fun evictAll(balances: Collection<LedgerAccountBalance>) {
        balances.distinctBy { it.accountId }.map { balance ->
            BalanceLookupKey(balance.accountId)
        }.forEach {
            cache.evict(it)
        }
    }

    override suspend fun create(accountId: UUID, balance: BigDecimal): LedgerAccountBalance {
        return saveBalance(BalanceLookupKey(accountId)) {
            balanceRepository.save(
                LedgerAccountBalance(
                    accountId = accountId,
                    amount = balance.scaleToLong(),
                    credits = 0L,
                    debits = 0L,
                    balanceDate = LocalDate.now(),
                ).markForCreate(System.currentTimeMillis()),
            )
        }
    }

    private fun getOrPutCachedValue(key: Serializable, block: suspend () -> LedgerAccountBalance?): LedgerAccountBalance? {
        return cache.get(key) {
            runBlocking {
                block()
            }
        }
    }

    private suspend fun saveBalance(key: Serializable, block: suspend () -> LedgerAccountBalance): LedgerAccountBalance {
        return block().also {
            cache.put(key, it)
        }
    }
}

class BalanceLookupKey(val accountId: UUID) : Serializable {
    override fun toString(): String {
        return accountId.toString().uppercase()
    }

    companion object {
        @Serial
        private const val serialVersionUID: Long = 202412061500L
    }
}
