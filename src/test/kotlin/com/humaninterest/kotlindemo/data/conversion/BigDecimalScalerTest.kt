package com.humaninterest.kotlindemo.data.conversion

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class BigDecimalScalerTest {
    @Test
    fun givenValueLessThanOne_expectValidScalingAndUnscaling() {
        val expectedBigDecimal = BigDecimal(".99000000")
        val expectedLong = 99_000_000L
        val input = BigDecimal.valueOf(0.99)

        val scaled = BigDecimalScaler.scale(input)
        val unscaled = BigDecimalScaler.unscale(scaled)

        Assertions.assertEquals(expectedLong, scaled, "Unexpected scaled value")
        Assertions.assertEquals(expectedBigDecimal, unscaled, "Unexpected unscaled value")
    }

    @Test
    fun givenPositiveValue_expectValidScalingAndUnscaling() {
        val expectedBigDecimal = BigDecimal("5.99000000")
        val expectedLong = 599_000_000L
        val input = BigDecimal.valueOf(5.99)

        val scaled = BigDecimalScaler.scale(input)
        val unscaled = BigDecimalScaler.unscale(scaled)

        Assertions.assertEquals(expectedLong, scaled, "Unexpected scaled value")
        Assertions.assertEquals(expectedBigDecimal, unscaled, "Unexpected unscaled value")
    }

    @Test
    fun givenNegativeValue_expectValidScalingAndUnscaling() {
        val expectedBigDecimal = BigDecimal("-5.99000000")
        val expectedLong = -599_000_000L
        val input = BigDecimal.valueOf(-5.99)

        val scaled = BigDecimalScaler.scale(input)
        val unscaled = BigDecimalScaler.unscale(scaled)

        Assertions.assertEquals(expectedLong, scaled, "Unexpected scaled value")
        Assertions.assertEquals(expectedBigDecimal, unscaled, "Unexpected unscaled value")
    }
}
