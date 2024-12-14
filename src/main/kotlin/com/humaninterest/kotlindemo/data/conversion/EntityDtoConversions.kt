package com.humaninterest.kotlindemo.data.conversion

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * @receiver Any list to be converted to an ArrayList.
 */
inline fun <reified E> List<E>.toArrayList(): ArrayList<E> {
    return if (this is ArrayList) {
        this
    } else {
        ArrayList(this)
    }
}

/**
 * @receiver A Unix Epoch Milli as a long, to be converted to a UTC date time.
 */
fun Long.toLocalDateTime(): LocalDateTime {
    return LocalDateTime.ofInstant(Instant.ofEpochMilli(this), ZoneId.of("UTC"))
}
