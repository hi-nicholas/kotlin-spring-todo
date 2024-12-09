package com.humaninterest.kotlindemo.data.conversion

import com.humaninterest.kotlindemo.api.response.TaskItemDTO
import com.humaninterest.kotlindemo.api.response.TaskListDTO
import com.humaninterest.kotlindemo.api.response.TaskListDetailsDTO
import com.humaninterest.kotlindemo.data.model.TaskItem
import com.humaninterest.kotlindemo.data.model.TaskList
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * @receiver Any list to be converted to an ArrayList.
 */
inline fun <reified E> List<E>.toArrayList(): ArrayList<E> {
    return if (this is ArrayList) {
        this
    } else {
        ArrayList(this)
    }
}

/**
 * @receiver A Unix Epoch Milli as a long, to be converted to a UTC date time.
 */
fun Long.toLocalDateTime(): LocalDateTime {
    return LocalDateTime.ofInstant(Instant.ofEpochMilli(this), ZoneId.of("UTC"))
}

fun TaskList.toDTO(): TaskListDTO {
    return TaskListDTO(
        id = this.id,
        name = this.name,
        description = this.description,
        createdAt = this.createdAt.toLocalDateTime(),
        updatedAt = this.updatedAt.toLocalDateTime(),
    )
}

fun TaskList.toDetailsDTO(tasks: List<TaskItemDTO>): TaskListDetailsDTO {
    return TaskListDetailsDTO(
        id = this.id,
        name = this.name,
        description = this.description,
        createdAt = this.createdAt.toLocalDateTime(),
        updatedAt = this.updatedAt.toLocalDateTime(),
        tasks = tasks.toArrayList(),
    )
}

fun TaskItem.toDTO(): TaskItemDTO {
    return TaskItemDTO(
        id = this.id,
        taskListId = this.taskListId,
        description = this.description,
        sortOrder = this.sortOrder,
        isComplete = this.isComplete,
        createdAt = this.createdAt.toLocalDateTime(),
        updatedAt = this.updatedAt.toLocalDateTime(),
        completedAt = this.completedAt?.toLocalDateTime(),
    )
}
