package com.humaninterest.kotlindemo.data.conversion

import java.math.BigDecimal

/**
 * Utility/helper to scale/unscale BigDecimal and Long.
 */
object BigDecimalScaler {
    fun scale(value: BigDecimal): Long {
        return value.scaleByPowerOfTen(SCALE_POWER).longValueExact()
    }

    fun unscale(value: Long): BigDecimal {
        return if (value != 0L) {
            BigDecimal.valueOf(value).scaleByPowerOfTen(UNSCALE_POWER).let { b ->
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

    const val SCALE_POWER = 8
    const val UNSCALE_POWER = -8
}
