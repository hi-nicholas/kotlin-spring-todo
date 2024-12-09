package com.humaninterest.kotlindemo.data.repository

import com.humaninterest.kotlindemo.data.model.TaskItem
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Sort
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.kotlin.CoroutineSortingRepository
import java.util.UUID

interface TaskItemRepository : CoroutineCrudRepository<TaskItem, UUID>, CoroutineSortingRepository<TaskItem, UUID> {
    fun findAllByTaskListId(
        id: UUID,
        sort: Sort = Sort.by("taskListId", "sortOrder", "createdAt"),
    ): Flow<TaskItem>

    @Modifying
    @Query(
        "UPDATE task_item SET sort_order = sort_order + 1 WHERE task_list_id = :taskListId AND " +
            "sort_order >= :sortOrder AND NOT(id = :taskItemId)",
    )
    suspend fun reSortItems(
        taskListId: UUID,
        taskItemId: UUID,
        sortOrder: Int,
    )

    @Modifying
    @Query(
        "UPDATE task_item SET is_complete = TRUE, completed_at = :completedAt, " +
            "updated_at = :completedAt WHERE id = :id",
    )
    suspend fun markItemComplete(
        id: UUID,
        completedAt: Long = System.currentTimeMillis(),
    )
}
