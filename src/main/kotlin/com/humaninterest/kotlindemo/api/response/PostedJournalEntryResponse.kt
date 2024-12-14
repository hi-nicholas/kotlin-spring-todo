package com.humaninterest.kotlindemo.api.response

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
)

data class JournalEntryAccountBalance(
    val accountId: UUID,
    val accountName: String,
    val balance: BigDecimal,
)
