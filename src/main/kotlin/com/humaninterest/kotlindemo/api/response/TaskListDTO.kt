package com.humaninterest.kotlindemo.api.response

import java.io.Serializable
import java.time.LocalDateTime
import java.util.UUID

data class TaskListDTO(
    val id: UUID,
    val name: String,
    val description: String? = null,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) : Serializable {
    companion object {
        @java.io.Serial
        private const val serialVersionUID: Long = 202412061122L
    }
}

data class TaskListDetailsDTO(
    val id: UUID,
    val name: String,
    val description: String? = null,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val tasks: ArrayList<TaskItemDTO> = arrayListOf(),
) : Serializable {
    companion object {
        @java.io.Serial
        private const val serialVersionUID: Long = 202412061122L
    }
}

data class TaskItemDTO(
    val id: UUID,
    val taskListId: UUID,
    val description: String,
    val sortOrder: Int,
    val isComplete: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val completedAt: LocalDateTime? = null,
) : Serializable {
    companion object {
        @java.io.Serial
        private const val serialVersionUID: Long = 202412061122L
    }
}
