package com.humaninterest.kotlindemo.data.repository.analytics

import com.humaninterest.kotlindemo.data.conversion.getQuarter
import com.humaninterest.kotlindemo.data.model.analytics.BalanceCacheKey
import com.humaninterest.kotlindemo.data.model.analytics.BalanceDay
import com.humaninterest.kotlindemo.data.model.analytics.BalanceEntity
import com.humaninterest.kotlindemo.data.model.analytics.BalanceMonth
import com.humaninterest.kotlindemo.data.model.analytics.BalanceQuarter
import com.humaninterest.kotlindemo.data.model.analytics.BalanceYear
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.springframework.cache.CacheManager
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.kotlin.CoroutineSortingRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.UUID

@NoRepositoryBean
interface BalanceAnalyticsRepository {
    suspend fun findAllByAccountIdForDate(accountId: UUID, date: LocalDate): List<BalanceEntity>
}

@Repository
class BalanceAnalyticsRepositoryImpl(
    private val years: BalanceYearRepository,
    private val quarters: BalanceQuarterRepository,
    private val months: BalanceMonthRepository,
    private val days: BalanceDayRepository,
    cacheManager: CacheManager,
) : BalanceAnalyticsRepository {
    private val cache = cacheManager.getCache("BalanceAnalytics")!!

    override suspend fun findAllByAccountIdForDate(accountId: UUID, date: LocalDate): List<BalanceEntity> {
        val year = date.year
        val quarter = date.getQuarter()
        val month = date.monthValue
        val day = date.dayOfMonth

        return withContext(Dispatchers.IO) {
            val y = async {
                getOrCache(BalanceCacheKey(accountId = accountId, year = year)) {
                    years.findByAccountIdAndYear(accountId, year)
                }
            }

            val q = async {
                getOrCache(BalanceCacheKey(accountId = accountId, year = year, quarter = quarter)) {
                    quarters.findByAccountIdAndYearAndQuarter(accountId, year, quarter)
                }
            }

            val m = async {
                getOrCache(BalanceCacheKey(accountId = accountId, year = year, month = month)) {
                    months.findByAccountIdAndYearAndMonth(accountId, year, month)
                }
            }

            val d = async {
                getOrCache(BalanceCacheKey(accountId = accountId, year = year, month = month, day = day)) {
                    days.findByAccountIdAndYearAndMonthAndDay(accountId, year, month, day)
                }
            }

            listOf(y, q, m, d).awaitAll()

            listOfNotNull(y.await(), q.await(), m.await(), d.await())
        }
    }

    private fun getOrCache(key: BalanceCacheKey, block: suspend () -> BalanceEntity?): BalanceEntity? {
        return cache.get(key) {
            runBlocking(Dispatchers.IO) {
                block()
            }
        }
    }
}

interface BalanceYearRepository : CoroutineSortingRepository<BalanceYear, UUID> {
    suspend fun findByAccountIdAndYear(accountId: UUID, year: Int): BalanceYear?
}

interface BalanceQuarterRepository : CoroutineSortingRepository<BalanceQuarter, UUID> {
    suspend fun findByAccountIdAndYearAndQuarter(accountId: UUID, year: Int, quarter: Int): BalanceQuarter?
}

interface BalanceMonthRepository : CoroutineSortingRepository<BalanceMonth, UUID> {
    suspend fun findByAccountIdAndYearAndMonth(accountId: UUID, year: Int, month: Int): BalanceMonth?
}

interface BalanceDayRepository : CoroutineSortingRepository<BalanceDay, UUID> {
    suspend fun findByAccountIdAndYearAndMonthAndDay(accountId: UUID, year: Int, month: Int, day: Int): BalanceDay?
}
