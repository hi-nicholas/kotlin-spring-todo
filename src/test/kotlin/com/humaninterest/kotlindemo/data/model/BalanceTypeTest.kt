package com.humaninterest.kotlindemo.data.model

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class BalanceTypeTest {
    @Test
    fun givenDebitBalanceTypeAndDebitValue_expectUnchangedSigns() {
        val expectedPositive = 100_000_000L
        val expectedNegative = -100_000_000L
        val actualPositive = BalanceType.DEBIT.debitValue(expectedPositive)
        val actualNegative = BalanceType.DEBIT.debitValue(expectedNegative)

        Assertions.assertEquals(expectedPositive, actualPositive, "(Positive) Debit -> Debit should be unchanged")
        Assertions.assertEquals(expectedNegative, actualNegative, "(Negative) Debit -> Debit should be unchanged")
    }

    @Test
    fun givenDebitBalanceTypeAndCreditValue_expectInvertedSigns() {
        val expectedPositive = 100_000_000L
        val expectedNegative = -100_000_000L
        val actualPositive = BalanceType.DEBIT.creditValue(expectedNegative)
        val actualNegative = BalanceType.DEBIT.creditValue(expectedPositive)

        Assertions.assertEquals(expectedPositive, actualPositive, "(Positive) Debit -> Credit should be flipped")
        Assertions.assertEquals(expectedNegative, actualNegative, "(Negative) Debit -> Credit should be flipped")
    }

    @Test
    fun givenCreditBalanceTypeAndCreditValue_expectUnchangedSigns() {
        val expectedPositive = 100_000_000L
        val expectedNegative = -100_000_000L
        val actualPositive = BalanceType.CREDIT.creditValue(expectedPositive)
        val actualNegative = BalanceType.CREDIT.creditValue(expectedNegative)

        Assertions.assertEquals(expectedPositive, actualPositive, "(Positive) Credit -> Credit should be unchanged")
        Assertions.assertEquals(expectedNegative, actualNegative, "(Negative) Credit -> Credit should be unchanged")
    }

    @Test
    fun givenCreditBalanceTypeAndDebitValue_expectInvertedSigns() {
        val expectedPositive = 100_000_000L
        val expectedNegative = -100_000_000L
        val actualPositive = BalanceType.CREDIT.debitValue(expectedNegative)
        val actualNegative = BalanceType.CREDIT.debitValue(expectedPositive)

        Assertions.assertEquals(expectedPositive, actualPositive, "(Positive) Credit -> Debit should be flipped")
        Assertions.assertEquals(expectedNegative, actualNegative, "(Negative) Credit -> Debit should be flipped")
    }
}
