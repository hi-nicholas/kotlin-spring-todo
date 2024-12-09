package com.humaninterest.kotlindemo.api.controller.advice

import com.humaninterest.kotlindemo.spring.NotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import reactor.core.publisher.Mono

// This intercepts exceptions thrown and turns them into the appropriate HTTP responses.
@RestControllerAdvice
class NotFoundControllerAdvice {
    @ExceptionHandler(NotFoundException::class)
    fun handle(ex: NotFoundException): Mono<ResponseEntity<ProblemDetail>> {
        return Mono.just(
            ResponseEntity.of(
                ProblemDetail.forStatusAndDetail(
                    HttpStatus.NOT_FOUND,
                    ex.message ?: "Not Found",
                ),
            ).build(),
        )
    }
}
