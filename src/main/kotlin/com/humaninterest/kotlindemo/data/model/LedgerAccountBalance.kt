package com.humaninterest.kotlindemo.data.model

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDate
import java.util.UUID
import kotlin.math.abs

@Table("ledger_account_balance")
data class LedgerAccountBalance(
    @field:Id
    private val id: UUID = UUID.randomUUID(),
    val accountId: UUID,
    val amount: Long,
    val credits: Long,
    val debits: Long,
    val balanceDate: LocalDate,
    val createdAt: Long = 0,
    val updatedAt: Long = 0,
    @field:Version
    val lockVersion: Long = 0,
) : PersistedEntity<UUID> {
    @field:Transient
    @field:org.springframework.data.annotation.Transient
    private var create: Boolean = false

    override fun getId(): UUID = id

    override fun isNew(): Boolean = create

    override fun markForCreate(createdAt: Long): LedgerAccountBalance {
        return this.copy(createdAt = createdAt, updatedAt = createdAt, lockVersion = System.currentTimeMillis()).also {
            it.create = true
        }
    }

    fun debit(amount: Long): LedgerAccountBalance {
        return this.copy(
            amount = this.amount + amount,
            debits = this.debits + abs(amount),
        )
    }

    fun credit(amount: Long): LedgerAccountBalance {
        return this.copy(
            amount = this.amount + amount,
            credits = this.credits + abs(amount),
        )
    }

    companion object {
        @java.io.Serial
        private const val serialVersionUID: Long = 202412061102L
    }
}
