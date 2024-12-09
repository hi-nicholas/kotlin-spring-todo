package com.humaninterest.kotlindemo.spring.validation

import jakarta.validation.ValidatorFactory
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.BindException
import org.springframework.validation.beanvalidation.SpringValidatorAdapter
import java.util.concurrent.atomic.AtomicReference

object ValidatorFactoryHelper {
    private val reference: AtomicReference<ValidatorFactory?> = AtomicReference(null)

    fun setValidatorFactory(factory: ValidatorFactory?) {
        reference.set(factory)
    }

    fun getValidatorFactory(): ValidatorFactory {
        return checkNotNull(reference.get()) {
            "ValidatorFactoryHelper not initialized"
        }
    }

    fun validate(obj: Any): BeanPropertyBindingResult {
        val adapter = SpringValidatorAdapter(getValidatorFactory().validator)
        val errors = BeanPropertyBindingResult(obj, obj::class.java.simpleName)
        adapter.validate(obj, errors)

        return errors
    }

    fun <T : Any> validateOrThrow(obj: T): T {
        validate(obj).also {
            if (it.hasErrors()) {
                throw BindException(it)
            }
        }

        return obj
    }
}

fun <T : Any> validate(block: () -> T): T {
    return ValidatorFactoryHelper.validateOrThrow(block())
}
