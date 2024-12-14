package com.humaninterest.kotlindemo.autoconfigure

import org.springframework.beans.factory.InitializingBean
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.context.event.ApplicationEventMulticaster
import org.springframework.context.event.SimpleApplicationEventMulticaster
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor
import java.util.concurrent.Executors

@AutoConfiguration
@EnableAsync
class AsyncAutoConfiguration(private val multicaster: ApplicationEventMulticaster) : InitializingBean {
    @Suppress("MagicNumber")
    override fun afterPropertiesSet() {
        // This allows the event multicaster listeners to be triggered asynchronously.
        if (multicaster is SimpleApplicationEventMulticaster) {
            multicaster.setTaskExecutor(ConcurrentTaskExecutor(Executors.newFixedThreadPool(4)))
        }
    }
}
