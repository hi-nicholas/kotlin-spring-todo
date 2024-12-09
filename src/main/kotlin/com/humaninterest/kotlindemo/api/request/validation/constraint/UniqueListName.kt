package com.humaninterest.kotlindemo.api.request.validation.constraint

import com.humaninterest.kotlindemo.api.request.validation.validator.UniqueListNameConstraintValidator
import jakarta.validation.Constraint
import kotlin.reflect.KClass

/**
 * JSR validation constraint annotation that ensures all task list names are unique when
 * validating the request.
 */
@Target(
    AnnotationTarget.FIELD,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.CLASS,
)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [UniqueListNameConstraintValidator::class])
annotation class UniqueListName(
    // This message is automatically interpolated from messages.properties.
    val message: String = "{com.humaninterest.unique}",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<*>> = [],
)
