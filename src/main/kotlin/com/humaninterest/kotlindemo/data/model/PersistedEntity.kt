package com.humaninterest.kotlindemo.data.model

import org.springframework.data.domain.Persistable
import java.io.Serializable

interface PersistedEntity<ID : Serializable> : Persistable<ID>, Serializable {
    fun markForCreate(createdAt: Long = System.currentTimeMillis()): PersistedEntity<ID>
}
