package com.humaninterest.kotlindemo.data.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table("task_list")
data class TaskList(
    @field:Id
    private val id: UUID = UUID.randomUUID(),
    val name: String,
    val description: String? = null,
    val createdAt: Long = 0,
    val updatedAt: Long = 0,
) : PersistedEntity<UUID> {
    @field:Transient
    @field:org.springframework.data.annotation.Transient
    private var create: Boolean = false

    override fun getId(): UUID = id

    override fun isNew(): Boolean = create

    override fun markForCreate(createdAt: Long): TaskList {
        return this.copy(createdAt = createdAt, updatedAt = createdAt).also {
            it.create = true
        }
    }

    companion object {
        @java.io.Serial
        private const val serialVersionUID: Long = 202412061102L
    }
}
