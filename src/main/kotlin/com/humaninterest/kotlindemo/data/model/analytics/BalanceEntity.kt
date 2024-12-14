package com.humaninterest.kotlindemo.data.model.analytics

import com.humaninterest.kotlindemo.api.response.DailyAccountBalance
import com.humaninterest.kotlindemo.api.response.MonthAccountBalance
import com.humaninterest.kotlindemo.api.response.PeriodAccountBalanceDetails
import com.humaninterest.kotlindemo.api.response.QuarterAccountBalance
import com.humaninterest.kotlindemo.api.response.YearlyAccountBalance
import com.humaninterest.kotlindemo.data.conversion.BigDecimalScaler.unscaleToBigDecimal
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.io.Serializable
import java.util.UUID

interface BalanceEntity : Serializable {
    val id: UUID
    val accountId: UUID
    val amount: Long
    val credits: Long
    val debits: Long
    val year: Int

    fun toDTO(): PeriodAccountBalanceDetails
}

@Table("lab_year")
data class BalanceYear(
    @field:Id
    override val id: UUID,
    override val accountId: UUID,
    override val amount: Long,
    override val credits: Long,
    override val debits: Long,
    override val year: Int,
) : BalanceEntity {
    override fun toDTO(): YearlyAccountBalance {
        return YearlyAccountBalance(
            amount = amount.unscaleToBigDecimal(),
            credits = credits.unscaleToBigDecimal(),
            debits = debits.unscaleToBigDecimal(),
            year = year,
        )
    }

    companion object {
        @java.io.Serial
        private const val serialVersionUID: Long = 202412061102L
    }
}

@Table("lab_quarter")
data class BalanceQuarter(
    @field:Id
    override val id: UUID,
    override val accountId: UUID,
    override val amount: Long,
    override val credits: Long,
    override val debits: Long,
    override val year: Int,
    val quarter: Int,
) : BalanceEntity {
    override fun toDTO(): QuarterAccountBalance {
        return QuarterAccountBalance(
            amount = amount.unscaleToBigDecimal(),
            credits = credits.unscaleToBigDecimal(),
            debits = debits.unscaleToBigDecimal(),
            year = year,
            quarter = quarter,
        )
    }

    companion object {
        @java.io.Serial
        private const val serialVersionUID: Long = 202412061102L
    }
}

@Table("lab_month")
data class BalanceMonth(
    @field:Id
    override val id: UUID,
    override val accountId: UUID,
    override val amount: Long,
    override val credits: Long,
    override val debits: Long,
    override val year: Int,
    val month: Int,
) : BalanceEntity {
    override fun toDTO(): MonthAccountBalance {
        return MonthAccountBalance(
            amount = amount.unscaleToBigDecimal(),
            credits = credits.unscaleToBigDecimal(),
            debits = debits.unscaleToBigDecimal(),
            year = year,
            month = month,
        )
    }

    companion object {
        @java.io.Serial
        private const val serialVersionUID: Long = 202412061102L
    }
}

@Table("lab_day")
data class BalanceDay(
    @field:Id
    override val id: UUID,
    override val accountId: UUID,
    override val amount: Long,
    override val credits: Long,
    override val debits: Long,
    override val year: Int,
    val month: Int,
    val day: Int,
) : BalanceEntity {
    override fun toDTO(): DailyAccountBalance {
        return DailyAccountBalance(
            amount = amount.unscaleToBigDecimal(),
            credits = credits.unscaleToBigDecimal(),
            debits = debits.unscaleToBigDecimal(),
            year = year,
            month = month,
            day = day,
        )
    }

    companion object {
        @java.io.Serial
        private const val serialVersionUID: Long = 202412061102L
    }
}
