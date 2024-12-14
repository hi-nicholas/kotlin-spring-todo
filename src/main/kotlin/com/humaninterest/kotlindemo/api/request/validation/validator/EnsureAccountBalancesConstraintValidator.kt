package com.humaninterest.kotlindemo.api.request.validation.validator

import com.humaninterest.kotlindemo.api.request.PostJournalEntryRequest
import com.humaninterest.kotlindemo.api.request.validation.constraint.EnsureAccountBalances
import com.humaninterest.kotlindemo.data.conversion.BigDecimalScaler.scaleToLong
import com.humaninterest.kotlindemo.data.model.BalanceType
import com.humaninterest.kotlindemo.data.model.LedgerAccount
import com.humaninterest.kotlindemo.data.model.LedgerAccountBalance
import com.humaninterest.kotlindemo.data.service.BalanceService
import com.humaninterest.kotlindemo.data.service.LedgerAccountService
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID
import kotlin.math.abs

class EnsureAccountBalancesConstraintValidator : ConstraintValidator<EnsureAccountBalances, PostJournalEntryRequest> {
    // Spring automatically wires in the instance when the constraint validator is initialized.
    @field:Autowired
    lateinit var accountService: LedgerAccountService

    @field:Autowired
    lateinit var balanceService: BalanceService

    private lateinit var message: String

    override fun initialize(constraintAnnotation: EnsureAccountBalances) {
        message = constraintAnnotation.message
    }

    @Suppress("ReturnCount")
    private suspend fun validateAccountBalance(ctx: ConstraintValidatorContext, accountId: UUID, balanceType: BalanceType, amount: Long): Boolean {
        val (acct, bal) =
            getAccountAndBalance(accountId).let {
                it ?: return true
            }

        val adjustedAmount =
            when (balanceType) {
                BalanceType.CREDIT -> acct.balanceType.creditValue(amount)
                BalanceType.DEBIT -> acct.balanceType.debitValue(amount)
            }

        if (acct.balanceType == balanceType && adjustedAmount >= 0L) {
            return true
        }

        val isValid = bal.amount >= abs(adjustedAmount)

        if (!isValid) {
            ctx.buildConstraintViolationWithTemplate(message)
                .addPropertyNode(
                    when (balanceType) {
                        BalanceType.CREDIT -> "creditAccountId"
                        BalanceType.DEBIT -> "debitAccountId"
                    },
                )
                .addConstraintViolation()
        }

        return isValid
    }

    override fun isValid(value: PostJournalEntryRequest?, context: ConstraintValidatorContext): Boolean {
        // If the value is null we will exit early and assume other validators are going to create errors
        // if its required or some such.
        if (value == null || (value.creditAccountId == null && value.debitAccountId == null)) {
            return true
        }

        val scaledAmount = value.amount.scaleToLong()
        val creditValid =
            value.creditAccountId?.let {
                runBlocking {
                    validateAccountBalance(context, it, BalanceType.CREDIT, scaledAmount)
                }
            } != false

        val debitValid =
            value.debitAccountId?.let {
                runBlocking {
                    validateAccountBalance(context, it, BalanceType.DEBIT, scaledAmount)
                }
            } != false

        return if (!creditValid || !debitValid) {
            context.disableDefaultConstraintViolation()
            false
        } else {
            true
        }
    }

    private suspend fun getAccountAndBalance(accountId: UUID): Pair<LedgerAccount, LedgerAccountBalance>? {
        return try {
            withContext(Dispatchers.IO) {
                val account =
                    async {
                        accountService.getById(accountId)
                    }

                val balance =
                    async {
                        balanceService.getByAccountId(accountId)
                    }

                listOf(account, balance).awaitAll()
                Pair(account.await(), balance.await())
            }
        } catch (_: Throwable) {
            null
        }
    }
}
