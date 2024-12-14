package com.humaninterest.kotlindemo.data.repository

import com.humaninterest.kotlindemo.data.model.JournalEntry
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.util.UUID

interface JournalEntryRepository : CoroutineCrudRepository<JournalEntry, UUID>
