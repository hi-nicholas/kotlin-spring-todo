package com.humaninterest.kotlindemo.autoconfigure

import com.humaninterest.kotlindemo.spring.validation.ValidatorFactoryHelper
import jakarta.validation.ValidatorFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration

/**
 * AutoConfiguration to intercept the ValidatorFactory bean and bind it to the
 * singleton ValidatorFactoryHelper class.
 */
@AutoConfiguration(after = [ValidationAutoConfiguration::class])
class ValidatorFactoryHelperAutoConfiguration(private val validatorFactory: ValidatorFactory) : InitializingBean {
    override fun afterPropertiesSet() {
        ValidatorFactoryHelper.setValidatorFactory(validatorFactory)
    }
}
