package com.humaninterest.kotlindemo.data.service

import com.humaninterest.kotlindemo.data.model.LedgerAccount
import com.humaninterest.kotlindemo.data.repository.LedgerAccountRepository
import com.humaninterest.kotlindemo.spring.NotFoundException
import kotlinx.coroutines.runBlocking
import org.springframework.cache.CacheManager
import org.springframework.stereotype.Service
import java.io.Serializable
import java.util.UUID

interface LedgerAccountService {
    suspend fun getById(id: UUID): LedgerAccount

    suspend fun getByType(partyId: UUID, type: String): LedgerAccount

    suspend fun exists(id: UUID): Boolean
}

@Service
class LedgerAccountServiceImpl(
    private val accountRepository: LedgerAccountRepository,
    cacheManager: CacheManager,
) : LedgerAccountService {
    private val cache = cacheManager.getCache("LedgerAccount")!!

    override suspend fun exists(id: UUID): Boolean {
        return getOrPutCachedValue(id) {
            accountRepository.findById(id)
        } != null
    }

    override suspend fun getById(id: UUID): LedgerAccount {
        return getOrPutCachedValue(id) {
            accountRepository.findById(id)
        } ?: throw NotFoundException("LedgerAccount '$id' not found")
    }

    override suspend fun getByType(partyId: UUID, type: String): LedgerAccount {
        return getOrPutCachedValue(PartyAccountTypeLookupKey(partyId, type)) {
            accountRepository.findLedgerAccountByPartyIdAndType(partyId, type.uppercase())
        } ?: throw NotFoundException("LedgerAccount type '$type' not found for party $partyId")
    }

    private fun getOrPutCachedValue(key: Serializable, block: suspend () -> LedgerAccount?): LedgerAccount? {
        return cache.get(key) {
            runBlocking {
                block()
            }
        }
    }
}

class PartyAccountTypeLookupKey(partyId: UUID, type: String) : Serializable {
    val partyId = partyId.toString().uppercase()
    val type = type.uppercase()

    override fun toString(): String {
        return "$partyId:$type"
    }

    companion object {
        @java.io.Serial
        private const val serialVersionUID: Long = 202412061500L
    }
}
