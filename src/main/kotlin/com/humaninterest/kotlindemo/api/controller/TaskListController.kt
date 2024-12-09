package com.humaninterest.kotlindemo.api.controller

import com.humaninterest.kotlindemo.api.request.CreateTaskListRequest
import com.humaninterest.kotlindemo.api.request.UpdateTaskListRequest
import com.humaninterest.kotlindemo.api.response.TaskListDTO
import com.humaninterest.kotlindemo.api.response.TaskListDetailsDTO
import com.humaninterest.kotlindemo.data.service.TaskListService
import com.humaninterest.kotlindemo.spring.validation.validate
import jakarta.validation.Valid
import kotlinx.coroutines.reactor.mono
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(
    path = ["/task-list"],
    produces = [MediaType.APPLICATION_JSON_VALUE],
    consumes = [MediaType.ALL_VALUE],
)
class TaskListController(
    private val taskListService: TaskListService,
) {
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun create(
        @Valid @RequestBody request: CreateTaskListRequest,
    ) = mono {
        taskListService.create(request)
    }

    @PutMapping(path = ["/{taskList}"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun update(
        @PathVariable taskList: TaskListDTO,
        @RequestBody request: UpdateTaskListRequest,
    ) = mono {
        taskListService.update(
            validate {
                request.copy(id = taskList.id)
            },
        )
    }

    @GetMapping(path = ["/{taskList}"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun get(
        @PathVariable taskList: TaskListDTO,
    ) = mono<TaskListDetailsDTO?> {
        taskListService.findDetailsById(taskList.id)
    }
}
