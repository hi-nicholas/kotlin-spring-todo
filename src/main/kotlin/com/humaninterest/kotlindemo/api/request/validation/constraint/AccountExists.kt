package com.humaninterest.kotlindemo.api.request.validation.constraint

import com.humaninterest.kotlindemo.api.request.validation.validator.AccountExistsConstraintValidator
import jakarta.validation.Constraint
import kotlin.reflect.KClass

/**
 * JSR validation constraint annotation that ensures an account exists with a given UUID.
 */
@Target(
    AnnotationTarget.FIELD,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.CLASS,
)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [AccountExistsConstraintValidator::class])
annotation class AccountExists(
    // This message is automatically interpolated from messages.properties.
    val message: String = "{com.humaninterest.doesNotExist}",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<*>> = [],
)
