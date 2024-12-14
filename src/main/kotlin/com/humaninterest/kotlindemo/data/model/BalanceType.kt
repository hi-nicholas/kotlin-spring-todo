package com.humaninterest.kotlindemo.data.model

enum class BalanceType {
    CREDIT,
    DEBIT,
    ;

    fun debitValue(value: Long): Long {
        return when (this) {
            CREDIT -> value * -1
            DEBIT -> value
        }
    }

    fun creditValue(value: Long): Long {
        return when (this) {
            CREDIT -> value
            DEBIT -> value * -1
        }
    }
}
