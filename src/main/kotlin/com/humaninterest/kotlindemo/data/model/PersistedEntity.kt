package com.humaninterest.kotlindemo.data.model

import org.springframework.data.domain.Persistable
import java.io.Serializable
import java.time.LocalDate

interface PersistedEntity<ID : Serializable> : Persistable<ID>, Serializable {
    fun markForCreate(createdAt: Long = System.currentTimeMillis()): PersistedEntity<ID>

    companion object {
        @JvmStatic
        val DEFAULT_END_DATE: LocalDate = LocalDate.of(9999, 12, 31)
    }
}
