package com.humaninterest.kotlindemo.api.request

import com.humaninterest.kotlindemo.api.request.validation.constraint.AccountExists
import com.humaninterest.kotlindemo.api.request.validation.constraint.EnsureAccountBalances
import jakarta.validation.constraints.NotBlank
import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

interface JournalEntryRequest : Serializable {
    val transactionDate: LocalDate
    val amount: BigDecimal
    val relatedEntryId: UUID?
}

@EnsureAccountBalances
data class PostJournalEntryRequest(
    @field:NotBlank
    val event: String = "",
    @field:NotBlank
    val type: String = "",
    @field:AccountExists
    val creditAccountId: UUID? = null,
    @field:AccountExists
    val debitAccountId: UUID? = null,
    override val amount: BigDecimal,
    override val transactionDate: LocalDate,
    override val relatedEntryId: UUID? = null,
) : JournalEntryRequest {
    companion object {
        @java.io.Serial
        private const val serialVersionUID: Long = 202412061122L
    }
}
