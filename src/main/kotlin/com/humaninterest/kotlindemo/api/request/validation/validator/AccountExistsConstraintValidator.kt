package com.humaninterest.kotlindemo.api.request.validation.validator

import com.humaninterest.kotlindemo.api.request.validation.constraint.AccountExists
import com.humaninterest.kotlindemo.data.service.LedgerAccountService
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID

class AccountExistsConstraintValidator : ConstraintValidator<AccountExists, UUID> {
    // Spring automatically wires in the instance when the constraint validator is initialized.
    @field:Autowired
    lateinit var accountService: LedgerAccountService
    private lateinit var message: String

    override fun initialize(constraintAnnotation: AccountExists) {
        message = constraintAnnotation.message
    }

    override fun isValid(value: UUID?, context: ConstraintValidatorContext): Boolean {
        // If the value is null we will exit early and assume other validators are going to create errors
        // if its required or some such.
        if (value == null) {
            return true
        }

        return runBlocking {
            accountService.exists(value)
        }
    }
}
