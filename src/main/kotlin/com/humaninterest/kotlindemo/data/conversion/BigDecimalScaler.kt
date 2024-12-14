package com.humaninterest.kotlindemo.data.conversion

import java.math.BigDecimal

@Suppress("MagicNumber")
object BigDecimalScaler {
    fun scale(value: BigDecimal): Long {
        return value.scaleByPowerOfTen(8).longValueExact()
    }

    fun unscale(value: Long): BigDecimal {
        return if (value != 0L) {
            BigDecimal.valueOf(value).scaleByPowerOfTen(-8).let { b ->
                if (b.compareTo(BigDecimal.ZERO) == 0) {
                    BigDecimal.ZERO
                } else {
                    b
                }
            }
        } else {
            BigDecimal.ZERO
        }
    }

    fun BigDecimal.scaleToLong(): Long {
        return scale(this)
    }

    fun Long.unscaleToBigDecimal(): BigDecimal {
        return unscale(this)
    }
}
