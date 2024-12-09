package com.humaninterest.kotlindemo.spring.convert

import com.humaninterest.kotlindemo.api.response.TaskListDTO
import com.humaninterest.kotlindemo.data.service.TaskListService
import com.humaninterest.kotlindemo.spring.NotFoundException
import kotlinx.coroutines.runBlocking
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class TaskListDTOConverter(
    private val service: TaskListService,
) : Converter<String, TaskListDTO> {
    override fun convert(source: String): TaskListDTO {
        return if (source.isBlank()) {
            null
        } else {
            try {
                runBlocking {
                    service.findById(UUID.fromString(source))
                }
            } catch (_: Throwable) {
                null
            }
        } ?: throw NotFoundException("TaskList '$source' not found")
    }
}
