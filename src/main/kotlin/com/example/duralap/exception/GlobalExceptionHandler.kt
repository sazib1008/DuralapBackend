package com.example.duralap.exception

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.context.request.WebRequest

@RestControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    /**
     * Handle business logic/validation failures explicitly thrown by the Domain/Service layers.
     */
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(
        ex: IllegalArgumentException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        val path = (request as? ServletWebRequest)?.request?.requestURI ?: ""
        
        logger.warn("Domain Exception at \$path: \${ex.message}")

        val errorResponse = ErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            error = HttpStatus.BAD_REQUEST.reasonPhrase,
            message = ex.message ?: "Invalid request parameters",
            path = path
        )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    /**
     * Handle missing entities or domain objects.
     */
    @ExceptionHandler(NoSuchElementException::class)
    fun handleNoSuchElementException(
        ex: NoSuchElementException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        val path = (request as? ServletWebRequest)?.request?.requestURI ?: ""

        val errorResponse = ErrorResponse(
            status = HttpStatus.NOT_FOUND.value(),
            error = HttpStatus.NOT_FOUND.reasonPhrase,
            message = ex.message ?: "Resource not found",
            path = path
        )
        return ResponseEntity(errorResponse, HttpStatus.NOT_FOUND)
    }

    /**
     * Handle Jakarta/Hibernate validation annotation failures (@Valid).
     * This iterates through missing fields and constructs a readable user message.
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(
        ex: MethodArgumentNotValidException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        val path = (request as? ServletWebRequest)?.request?.requestURI ?: ""

        val errors = ex.bindingResult.fieldErrors.joinToString("; ") { "\${it.field}: \${it.defaultMessage}" }
        logger.warn("Validation Exception at \$path -> \$errors")

        val errorResponse = ErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            error = "Validation Failed",
            message = errors,
            path = path
        )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    /**
     * Catch-All for unhandled Internal Server Errors (SQL failures, Kafka timeouts).
     * Prevents dumping full StackTraces onto the API response.
     */
    @ExceptionHandler(Exception::class)
    fun handleAllUncaughtException(
        ex: Exception,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        val path = (request as? ServletWebRequest)?.request?.requestURI ?: ""

        // Strictly log the stacktrace internally to the ELK stack / console, don't expose it to HTTP
        logger.error("Unhandled Exception at \$path", ex)

        val errorResponse = ErrorResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            error = HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase,
            message = "An unexpected error occurred. Please try again later.",
            path = path
        )
        return ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
