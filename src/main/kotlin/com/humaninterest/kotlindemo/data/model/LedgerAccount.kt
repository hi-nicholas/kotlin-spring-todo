package com.humaninterest.kotlindemo.data.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDate
import java.util.UUID

@Table("ledger_account")
data class LedgerAccount(
    @field:Id
    private val id: UUID = UUID.randomUUID(),
    val parentId: UUID? = null,
    val partyId: UUID,
    val name: String,
    val balanceType: BalanceType,
    val category: String,
    val type: String,
    val effStartDate: LocalDate,
    val effEndDate: LocalDate = PersistedEntity.DEFAULT_END_DATE,
    val metadata: String = "{}",
    val createdAt: Long = 0,
    val updatedAt: Long = 0,
    val deletedAt: Long? = null,
    val isActive: Boolean = true,
) : PersistedEntity<UUID> {
    @field:Transient
    @field:org.springframework.data.annotation.Transient
    private var create: Boolean = false

    override fun getId(): UUID = id

    override fun isNew(): Boolean = create

    override fun markForCreate(createdAt: Long): LedgerAccount {
        return this.copy(createdAt = createdAt, updatedAt = createdAt).also {
            it.create = true
        }
    }

    companion object {
        @java.io.Serial
        private const val serialVersionUID: Long = 202412061102L
    }
}
