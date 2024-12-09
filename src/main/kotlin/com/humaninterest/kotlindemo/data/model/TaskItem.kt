package com.humaninterest.kotlindemo.data.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table("task_item")
data class TaskItem(
    @field:Id
    private val id: UUID = UUID.randomUUID(),
    val taskListId: UUID,
    val description: String,
    val sortOrder: Int = TASK_ITEM_DEFAULT_ORDER,
    val isComplete: Boolean = false,
    val createdAt: Long = 0,
    val updatedAt: Long = 0,
    val completedAt: Long? = null,
) : PersistedEntity<UUID> {
    @field:Transient
    @field:org.springframework.data.annotation.Transient
    private var create: Boolean = false

    override fun getId(): UUID = id

    override fun isNew(): Boolean = create

    override fun markForCreate(createdAt: Long): TaskItem {
        return this.copy(createdAt = createdAt, updatedAt = createdAt).also {
            it.create = true
        }
    }

    fun markCompleted(completedAt: Long? = null): TaskItem {
        return this.copy(
            updatedAt = completedAt ?: System.currentTimeMillis(),
            completedAt = completedAt ?: System.currentTimeMillis(),
        )
    }

    companion object {
        const val TASK_ITEM_DEFAULT_ORDER: Int = 9999

        @java.io.Serial
        private const val serialVersionUID: Long = 202412061102L
    }
}
