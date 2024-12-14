package com.humaninterest.kotlindemo.data.model

import io.r2dbc.postgresql.codec.Json
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDate
import java.util.UUID

@Table("journal_entry")
data class JournalEntry(
    @field:Id
    private val id: UUID = UUID.randomUUID(),
    val event: String,
    val type: String,
    val creditAccountId: UUID? = null,
    val debitAccountId: UUID? = null,
    val amount: Long,
    val transactionDate: LocalDate,
    val metadata: Json = Json.of("{}"),
    val relatedEntryId: UUID? = null,
    val createdAt: Long = 0,
) : PersistedEntity<UUID> {
    @field:Transient
    @field:org.springframework.data.annotation.Transient
    private var create: Boolean = false

    override fun getId(): UUID = id

    override fun isNew(): Boolean = create

    override fun markForCreate(createdAt: Long): JournalEntry {
        return this.copy(createdAt = createdAt).also {
            it.create = true
        }
    }

    companion object {
        @java.io.Serial
        private const val serialVersionUID: Long = 202412061102L
    }
}
