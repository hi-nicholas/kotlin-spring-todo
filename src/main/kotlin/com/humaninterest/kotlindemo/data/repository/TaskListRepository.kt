package com.humaninterest.kotlindemo.data.repository

import com.humaninterest.kotlindemo.data.model.TaskList
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.util.UUID

interface TaskListRepository : CoroutineCrudRepository<TaskList, UUID> {
    suspend fun findFirstByNameIgnoreCase(name: String): TaskList?
}
