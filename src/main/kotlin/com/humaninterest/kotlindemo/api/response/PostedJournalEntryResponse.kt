package com.humaninterest.kotlindemo.api.response

import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

data class PostedJournalEntryResponse(
    val id: UUID,
    val event: String,
    val type: String,
    val amount: BigDecimal,
    val transactionDate: LocalDate,
    val creditBalance: JournalEntryAccountBalance?,
    val debitBalance: JournalEntryAccountBalance?,
    val relatedEntryId: UUID?,
) : Serializable {
    companion object {
        @java.io.Serial
        private const val serialVersionUID: Long = 202412061102L
    }
}

data class JournalEntryAccountBalance(
    val accountId: UUID,
    val accountName: String,
    val balance: BigDecimal,
) : Serializable {
    companion object {
        @java.io.Serial
        private const val serialVersionUID: Long = 202412061102L
    }
}
