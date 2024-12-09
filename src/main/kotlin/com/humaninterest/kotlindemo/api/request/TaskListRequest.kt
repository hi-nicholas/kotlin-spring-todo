package com.humaninterest.kotlindemo.api.request

import com.humaninterest.kotlindemo.api.request.validation.constraint.UniqueListName
import jakarta.validation.constraints.NotBlank
import java.io.Serializable
import java.util.UUID

/**
 * By creating a general interface, we can add validation constraints to it and ensure that
 * all of our Create and Update requests will at bare minimum follow the same constraint checks
 * that are defined at the interface level.
 *
 * This reduces boilerplate and errors between create/update requests.
 */
@UniqueListName
interface TaskListRequest : Serializable {
    @get:NotBlank
    val name: String
    val description: String?
}

data class CreateTaskListRequest(
    override val name: String = "",
    override val description: String? = null,
    // Allow the creation of tasks at the same time we create a task list (if we want to)
    val tasks: ArrayList<String> = arrayListOf(),
) : TaskListRequest {
    companion object {
        @java.io.Serial
        private const val serialVersionUID: Long = 202412061122L
    }
}

data class UpdateTaskListRequest(
    // The ID is required in the request object so that our constraint validators can use it.
    val id: UUID = BLANK_UUID,
    override val name: String = "",
    override val description: String? = null,
) : TaskListRequest {
    companion object {
        @java.io.Serial
        private const val serialVersionUID: Long = 202412061122L

        private val BLANK_UUID: UUID = UUID.randomUUID()
    }
}
