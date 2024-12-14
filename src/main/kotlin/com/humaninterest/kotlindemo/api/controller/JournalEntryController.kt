package com.humaninterest.kotlindemo.api.controller

import com.humaninterest.kotlindemo.api.request.PostJournalEntryRequest
import com.humaninterest.kotlindemo.data.conversion.BigDecimalScaler.unscaleToBigDecimal
import com.humaninterest.kotlindemo.data.conversion.toLocalDateTime
import com.humaninterest.kotlindemo.data.service.BalanceService
import com.humaninterest.kotlindemo.data.service.JournalEntryService
import jakarta.validation.Valid
import kotlinx.coroutines.reactor.mono
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

@RestController
@RequestMapping(
    path = ["/journal-entry"],
    produces = [MediaType.APPLICATION_JSON_VALUE],
    consumes = [MediaType.ALL_VALUE],
)
class JournalEntryController(
    private val journalEntryService: JournalEntryService,
    private val balanceService: BalanceService,
) {
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun create(@Valid @RequestBody request: PostJournalEntryRequest) = mono {
        journalEntryService.save(request)
    }

    @Suppress("MagicNumber", "Indentation", "LongMethod")
    @PostMapping(path = ["/test"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun test() = mono {
        val transactions =
            listOf(
                PostJournalEntryRequest(
                    event = "DEPOSIT",
                    type = "ACH",
                    creditAccountId = null,
                    debitAccountId = CUST_DEPOSIT_ACCT_ID,
                    amount = BigDecimal.valueOf(1000.00),
                    transactionDate = LocalDate.now(),
                ),
                PostJournalEntryRequest(
                    event = "CONTRIBUTION",
                    type = "DEPOSIT",
                    creditAccountId = CUST_DEPOSIT_ACCT_ID,
                    debitAccountId = PLAN_DEPOSIT_ACCT_ID,
                    amount = BigDecimal.valueOf(1000.00),
                    transactionDate = LocalDate.now(),
                ),
                PostJournalEntryRequest(
                    event = "CONTRIBUTION",
                    type = "EE_PRETAX",
                    creditAccountId = PLAN_DEPOSIT_ACCT_ID,
                    debitAccountId = PPT_CONTRIB_ACCT_ID,
                    amount = BigDecimal.valueOf(750.00),
                    transactionDate = LocalDate.now(),
                ),
                PostJournalEntryRequest(
                    event = "CONTRIBUTION",
                    type = "MATCH",
                    creditAccountId = PLAN_DEPOSIT_ACCT_ID,
                    debitAccountId = PPT_MATCH_ACCT_ID,
                    amount = BigDecimal.valueOf(250.00),
                    transactionDate = LocalDate.now(),
                ),
            )

        val accountIds = mutableSetOf<UUID>()
        val saved =
            transactions.map { t ->
                journalEntryService.save(t).also { entry ->
                    entry.creditBalance?.let {
                        accountIds.add(it.accountId)
                    }
                    entry.debitBalance?.let {
                        accountIds.add(it.accountId)
                    }
                }
            }

        val balances =
            accountIds.map { accountId ->
                balanceService.getByAccountId(accountId)
            }

        mapOf(
            "transactions" to saved,
            "balances" to
                balances.map {
                    mapOf(
                        "id" to it.id,
                        "accountId" to it.accountId,
                        "accountName" to (acctNames[it.accountId] ?: it.accountId),
                        "amount" to it.amount.unscaleToBigDecimal(),
                        "credits" to it.credits.unscaleToBigDecimal(),
                        "debits" to it.debits.unscaleToBigDecimal(),
                        "lockVersion" to it.lockVersion,
                        "updatedAt" to it.updatedAt.toLocalDateTime(),
                    )
                },
        )
    }

    companion object {
        val CUST_DEPOSIT_ACCT_ID = UUID.fromString("02df5cc6-7b6f-48a3-8a57-a4a4184fba08")
        val PLAN_DEPOSIT_ACCT_ID = UUID.fromString("5dd1fcd6-86ae-4ef1-b088-22687a33f9eb")
        val PPT_CONTRIB_ACCT_ID = UUID.fromString("ee44066d-23b0-42d4-9d8e-a128236ebac5")
        val PPT_MATCH_ACCT_ID = UUID.fromString("3a78e7e8-a8ee-48af-96fd-eb657a96ba0f")
        val acctNames =
            mapOf(
                CUST_DEPOSIT_ACCT_ID to "Matrix Deposit",
                PLAN_DEPOSIT_ACCT_ID to "Plan Deposit",
                PPT_CONTRIB_ACCT_ID to "Participant Pre-Tax Contributions",
                PPT_MATCH_ACCT_ID to "Employer Match Contributions",
            )
    }
}
