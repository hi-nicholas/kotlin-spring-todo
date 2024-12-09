package com.humaninterest.kotlindemo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.util.TimeZone

@SpringBootApplication
class KotlinDemoApplication

fun main(args: Array<String>) {
    // Always set app timezone to UTC
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    @Suppress("SpreadOperator")
    runApplication<KotlinDemoApplication>(*args)
}
