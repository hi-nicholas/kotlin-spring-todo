@file:Suppress("unused")

package com.humaninterest.kotlindemo.api.response

import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

data class AccountBalanceAnalyticsDTO(
    val accountId: UUID,
    val date: LocalDate,
    val amount: BigDecimal,
    val balances: ArrayList<PeriodAccountBalanceDetails>,
) : Serializable {
    companion object {
        @java.io.Serial
        private const val serialVersionUID: Long = 202412061102L
    }
}

enum class PeriodType {
    YEAR,
    QUARTER,
    MONTH,
    DAY,
}

interface PeriodAccountBalanceDetails : Serializable, Comparable<PeriodAccountBalanceDetails> {
    val periodType: PeriodType
    val amount: BigDecimal
    val credits: BigDecimal
    val debits: BigDecimal
    val year: Int
}

abstract class BasePeriodAccountBalanceDetails(
    override val amount: BigDecimal,
    override val credits: BigDecimal,
    override val debits: BigDecimal,
    override val year: Int,
) : PeriodAccountBalanceDetails {
    override fun compareTo(other: PeriodAccountBalanceDetails): Int {
        if (this.periodType != other.periodType) {
            return this.periodType.compareTo(other.periodType)
        }

        if (this.year != other.year) {
            return this.year.compareTo(other.year)
        }

        return 0
    }

    companion object {
        @java.io.Serial
        private const val serialVersionUID: Long = 1L
    }
}

class YearlyAccountBalance(
    amount: BigDecimal,
    credits: BigDecimal,
    debits: BigDecimal,
    year: Int,
) : BasePeriodAccountBalanceDetails(amount, credits, debits, year) {
    override val periodType: PeriodType = PeriodType.YEAR

    companion object {
        @java.io.Serial
        private const val serialVersionUID: Long = 202412061102L
    }
}

class QuarterAccountBalance(
    amount: BigDecimal,
    credits: BigDecimal,
    debits: BigDecimal,
    year: Int,
    val quarter: Int,
) : BasePeriodAccountBalanceDetails(amount, credits, debits, year) {
    override val periodType: PeriodType = PeriodType.QUARTER

    companion object {
        @java.io.Serial
        private const val serialVersionUID: Long = 202412061102L
    }
}

class MonthAccountBalance(
    amount: BigDecimal,
    credits: BigDecimal,
    debits: BigDecimal,
    year: Int,
    val month: Int,
) : BasePeriodAccountBalanceDetails(amount, credits, debits, year) {
    override val periodType: PeriodType = PeriodType.MONTH

    companion object {
        @java.io.Serial
        private const val serialVersionUID: Long = 202412061102L
    }
}

class DailyAccountBalance(
    amount: BigDecimal,
    credits: BigDecimal,
    debits: BigDecimal,
    year: Int,
    val month: Int,
    val day: Int,
) : BasePeriodAccountBalanceDetails(amount, credits, debits, year) {
    override val periodType: PeriodType = PeriodType.DAY

    companion object {
        @java.io.Serial
        private const val serialVersionUID: Long = 202412061102L
    }
}
