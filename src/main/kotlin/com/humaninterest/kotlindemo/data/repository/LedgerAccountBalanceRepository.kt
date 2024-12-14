package com.humaninterest.kotlindemo.data.repository

import com.humaninterest.kotlindemo.data.model.LedgerAccountBalance
import org.springframework.data.domain.Sort
import org.springframework.data.domain.Sort.Order
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.util.UUID

interface LedgerAccountBalanceRepository : CoroutineCrudRepository<LedgerAccountBalance, UUID> {
    suspend fun findFirstByAccountId(accountId: UUID, sort: Sort = Sort.by(Order.desc("balanceDate"))): LedgerAccountBalance?
}
