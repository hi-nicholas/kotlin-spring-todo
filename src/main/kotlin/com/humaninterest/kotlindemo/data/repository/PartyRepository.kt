package com.humaninterest.kotlindemo.data.repository

import com.humaninterest.kotlindemo.data.model.Party
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.util.UUID

interface PartyRepository : CoroutineCrudRepository<Party, UUID>
