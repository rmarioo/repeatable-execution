package com.rmarioo.repeatableexecutrion.rest

import arrow.core.Either
import com.rmarioo.repeatableexecutrion.core.model.Error
import com.rmarioo.repeatableexecutrion.core.model.Error.DepartureDateIsInThePast
import com.rmarioo.repeatableexecutrion.core.model.Error.GenericError
import com.rmarioo.repeatableexecutrion.core.model.Error.SearchNotAllowed
import com.rmarioo.repeatableexecutrion.core.model.Flight
import com.rmarioo.repeatableexecutrion.core.model.Search
import com.rmarioo.repeatableexecutrion.core.SearchUseCase
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.badRequest
import org.springframework.http.ResponseEntity.ok
import org.springframework.http.ResponseEntity.status
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.io.PrintWriter
import java.io.StringWriter


@RestController
class SearchController(val searchUseCase: SearchUseCase) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @PostMapping("/search")
    fun doSearch(@RequestBody searchRequest: SearchRequest): ResponseEntity<*> {

        val result: Either<Error, List<Flight>> = searchUseCase.doSearch(toDomain(searchRequest));

        return result.fold(
            { error ->
                logger.error("error happened with request $searchRequest error: ${error.toLogMessage()}")
                error.toResponseEntity()
            },
            { flights -> ok(flights) })
    }

    private fun Error.toResponseEntity(): ResponseEntity<String> {
        val errorToLog = this.toLogMessage()
        return when (this) {
            is DepartureDateIsInThePast -> badRequest().body(errorToLog)
            is SearchNotAllowed -> badRequest().body(errorToLog)
            is GenericError -> status(500).body(errorToLog)
        }
    }

    fun Error.toLogMessage(): String {

        return when (this) {
            is DepartureDateIsInThePast -> """departureDate   ${departureDate} is in the past 
                              current time is ${currentDateTime}"""
            is SearchNotAllowed -> "search not allowed ${cause} "
            is GenericError -> """generic error ${exception.message} happened for request ${exceptionToString(exception)}t"""
        }

    }

    fun exceptionToString(e: Throwable): String {
        val sw = StringWriter()
        e.printStackTrace(PrintWriter(sw))
        return sw.toString();
    }


    private fun toDomain(searchRequest: SearchRequest): Search {
        return Search(
            departureDate = searchRequest.departureDate,
            arrivalDate = searchRequest.arrivalDate,
            departureAirport = searchRequest.departureAirport,
            arrivalAirport = searchRequest.arrivalAirport,
            adults = searchRequest.adults,
            simulateServerError = searchRequest.simulateServerError ?: false
        )
    }
}
