package com.humaninterest.kotlindemo.data.model.analytics

import java.io.Serial
import java.io.Serializable
import java.util.UUID

data class BalanceCacheKey(
    val accountId: UUID,
    val year: Int,
    val month: Int? = null,
    val day: Int? = null,
    val quarter: Int? = null,
) : Serializable {
    override fun toString(): String {
        return listOf(
            accountId.toString(),
            year,
            (quarter?.toString() ?: "x"),
            (month?.toString() ?: "x"),
            (day?.toString() ?: "x"),
        ).joinToString("|")
    }

    companion object {
        @Serial
        private const val serialVersionUID = 1L
    }
}
