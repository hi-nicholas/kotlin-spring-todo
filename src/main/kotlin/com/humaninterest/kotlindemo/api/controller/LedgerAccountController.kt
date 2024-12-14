package com.humaninterest.kotlindemo.api.controller

import com.humaninterest.kotlindemo.data.service.BalanceService
import kotlinx.coroutines.reactor.mono
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.util.UUID

@RestController
@RequestMapping(
    path = ["/account"],
    produces = [MediaType.APPLICATION_JSON_VALUE],
    consumes = [MediaType.ALL_VALUE],
)
class LedgerAccountController(
    private val balanceService: BalanceService,
) {
    @GetMapping("/{accountId}/balance")
    fun getAccountBalance(@PathVariable accountId: UUID) = mono {
        balanceService.getByAccountId(accountId)
    }

    @GetMapping("/{accountId}/balance-details")
    fun getAccountBalanceDetails(@PathVariable accountId: UUID, @RequestParam(required = false) date: LocalDate = LocalDate.now()) = mono {
        balanceService.getDetailsByAccountIdForDate(accountId, date)
    }
}
