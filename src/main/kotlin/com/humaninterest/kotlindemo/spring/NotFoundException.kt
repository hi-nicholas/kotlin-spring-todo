package com.humaninterest.kotlindemo.spring

class NotFoundException(message: String, cause: Throwable? = null) : RuntimeException(message, cause) {
    companion object {
        @java.io.Serial
        private const val serialVersionUID: Long = 202412061245L
    }
}
