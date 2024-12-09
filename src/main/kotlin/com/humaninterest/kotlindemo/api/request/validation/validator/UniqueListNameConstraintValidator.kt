package com.humaninterest.kotlindemo.api.request.validation.validator

import com.humaninterest.kotlindemo.api.request.TaskListRequest
import com.humaninterest.kotlindemo.api.request.UpdateTaskListRequest
import com.humaninterest.kotlindemo.api.request.validation.constraint.UniqueListName
import com.humaninterest.kotlindemo.data.service.TaskListService
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired

class UniqueListNameConstraintValidator : ConstraintValidator<UniqueListName, TaskListRequest> {
    // Spring automatically wires in the TaskListService instance when the constraint validator is initialized.
    @field:Autowired
    lateinit var taskListService: TaskListService
    private lateinit var message: String

    override fun initialize(constraintAnnotation: UniqueListName) {
        message = constraintAnnotation.message
    }

    @Suppress("ReturnCount")
    override fun isValid(
        value: TaskListRequest?,
        context: ConstraintValidatorContext,
    ): Boolean {
        // If the value is null or name is blank, we will exit early and assume other validators are going to create
        // errors.
        if (value == null || value.name.isBlank()) {
            return true
        }

        // If we cannot find an existing task list by name, we can exit early.
        val existingByName =
            runBlocking {
                taskListService.findByName(value.name)
            } ?: return true

        val isValid =
            if (value is UpdateTaskListRequest) {
                // If this is an update, then we will check the existing one against the ID that we are updating for.
                value.id == existingByName.id
            } else {
                false
            }

        return isValid.also {
            // Because the validation context is at a class level, we have to tell the framework which field we
            // want to mark as an error.
            if (!it) {
                context.disableDefaultConstraintViolation()
                context.buildConstraintViolationWithTemplate(message)
                    .addPropertyNode("name")
                    .addConstraintViolation()
            }
        }
    }
}
