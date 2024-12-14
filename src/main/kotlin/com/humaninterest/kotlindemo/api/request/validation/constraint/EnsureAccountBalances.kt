package com.humaninterest.kotlindemo.api.request.validation.constraint

import com.humaninterest.kotlindemo.api.request.validation.validator.EnsureAccountBalancesConstraintValidator
import jakarta.validation.Constraint
import kotlin.reflect.KClass

/**
 * JSR validation constraint annotation that ensures account balances support the requested amount.
 */
@Target(
    AnnotationTarget.FIELD,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.CLASS,
)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [EnsureAccountBalancesConstraintValidator::class])
annotation class EnsureAccountBalances(
    // This message is automatically interpolated from messages.properties.
    val message: String = "{com.humaninterest.insufficientBalance}",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<*>> = [],
)
