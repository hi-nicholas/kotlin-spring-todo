package com.humaninterest.kotlindemo.data.service

import com.humaninterest.kotlindemo.api.request.CreateTaskListRequest
import com.humaninterest.kotlindemo.api.request.UpdateTaskListRequest
import com.humaninterest.kotlindemo.api.response.TaskListDTO
import com.humaninterest.kotlindemo.api.response.TaskListDetailsDTO
import com.humaninterest.kotlindemo.data.conversion.toDTO
import com.humaninterest.kotlindemo.data.conversion.toDetailsDTO
import com.humaninterest.kotlindemo.data.model.TaskItem
import com.humaninterest.kotlindemo.data.model.TaskList
import com.humaninterest.kotlindemo.data.repository.TaskItemRepository
import com.humaninterest.kotlindemo.data.repository.TaskListRepository
import com.humaninterest.kotlindemo.spring.NotFoundException
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.springframework.cache.CacheManager
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.Serializable
import java.util.UUID

interface TaskListService {
    suspend fun findByName(name: String): TaskListDTO?

    suspend fun findById(id: UUID): TaskListDTO?

    suspend fun findDetailsById(id: UUID): TaskListDetailsDTO?

    suspend fun create(request: CreateTaskListRequest): TaskListDetailsDTO

    suspend fun update(request: UpdateTaskListRequest): TaskListDTO

    suspend fun delete(id: UUID)
}

@Service
class TaskListServiceImpl(
    private val taskListRepository: TaskListRepository,
    private val taskItemRepository: TaskItemRepository,
    cacheManager: CacheManager,
) : TaskListService {
    private val cache = cacheManager.getCache("TaskList")!!
    private val detailsCache = cacheManager.getCache("TaskListDetails")!!

    override suspend fun findByName(name: String): TaskListDTO? {
        return getOrPutCachedValue(TaskListNameLookupKey(name)) {
            taskListRepository.findFirstByNameIgnoreCase(name)
        }?.toDTO()
    }

    override suspend fun findById(id: UUID): TaskListDTO? {
        return getOrPutCachedValue(id) {
            taskListRepository.findById(id)
        }?.toDTO()
    }

    override suspend fun findDetailsById(id: UUID): TaskListDetailsDTO? {
        return getOrPutDetailsValue(id) {
            val list = taskListRepository.findById(id) ?: return@getOrPutDetailsValue null
            val tasks =
                taskItemRepository.findAllByTaskListId(id).map {
                    it.toDTO()
                }.toList()

            list.toDetailsDTO(tasks)
        }
    }

    @Transactional
    override suspend fun delete(id: UUID) {
        val existing = findById(id) ?: return
        taskListRepository.deleteById(id)
        cache.evict(id)
        cache.evict(TaskListNameLookupKey(existing.name))
        detailsCache.evict(id)
    }

    @Transactional
    override suspend fun create(request: CreateTaskListRequest): TaskListDetailsDTO {
        val taskList =
            TaskList(
                id = UUID.randomUUID(),
                name = request.name.trim(),
                description = request.description?.trim(),
            ).markForCreate()

        val items =
            request.tasks.mapIndexed { index, task ->
                TaskItem(
                    id = UUID.randomUUID(),
                    taskListId = taskList.id,
                    description = task.trim(),
                    sortOrder = index,
                ).markForCreate(taskList.createdAt)
            }

        taskListRepository.save(taskList)
        val savedTasks =
            taskItemRepository.saveAll(items).map {
                it.toDTO()
            }.toList()

        return taskList.toDetailsDTO(savedTasks)
    }

    @Transactional
    override suspend fun update(request: UpdateTaskListRequest): TaskListDTO {
        val existing =
            taskListRepository.findById(request.id) ?: throw NotFoundException("TaskList '${request.id}' not found")
        val updated =
            existing.copy(
                name = request.name.trim(),
                description = request.description?.trim(),
                updatedAt = System.currentTimeMillis(),
            )

        return taskListRepository.save(updated).toDTO().also {
            cache.evict(existing.id)
            cache.evict(TaskListNameLookupKey(existing.name))
            detailsCache.evict(existing.id)
        }
    }

    private suspend fun getOrPutCachedValue(
        key: Serializable,
        block: suspend () -> TaskList?,
    ): TaskList? {
        return cache.get(key) {
            runBlocking {
                block()
            }
        }
    }

    private suspend fun getOrPutDetailsValue(
        key: Serializable,
        block: suspend () -> TaskListDetailsDTO?,
    ): TaskListDetailsDTO? {
        return detailsCache.get(key) {
            runBlocking {
                block()
            }
        }
    }
}

class TaskListNameLookupKey(name: String) : Serializable {
    val name = name.uppercase()

    override fun toString(): String {
        return name
    }

    companion object {
        @java.io.Serial
        private const val serialVersionUID: Long = 202412061500L
    }
}
