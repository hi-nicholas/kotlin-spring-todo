package com.humaninterest.kotlindemo.data.repository

import com.humaninterest.kotlindemo.data.model.LedgerAccount
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.util.UUID

interface LedgerAccountRepository : CoroutineCrudRepository<LedgerAccount, UUID> {
    suspend fun findLedgerAccountByPartyIdAndType(partyId: UUID, type: String): LedgerAccount?
}
