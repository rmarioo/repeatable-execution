package com.rmarioo.repeatableexecutrion.rest

import arrow.core.Either
import com.rmarioo.repeatableexecutrion.core.model.Error
import com.rmarioo.repeatableexecutrion.core.model.Error.DepartureDateIsInThePast
import com.rmarioo.repeatableexecutrion.core.model.Error.GenericError
import com.rmarioo.repeatableexecutrion.core.model.Error.SearchNotAllowed
import com.rmarioo.repeatableexecutrion.core.model.Flight
import com.rmarioo.repeatableexecutrion.core.model.Search
import com.rmarioo.repeatableexecutrion.core.SearchUseCase
import com.rmarioo.repeatableexecutrion.core.model.Outcome
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.badRequest
import org.springframework.http.ResponseEntity.ok
import org.springframework.http.ResponseEntity.status
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController


@RestController
class SearchController(val searchUseCase: SearchUseCase) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @PostMapping("/search")
    fun doSearch(@RequestBody searchRequest: SearchRequest): ResponseEntity<*> {

        val result: Outcome<Error, List<Flight>> = searchUseCase.doSearch(toDomain(searchRequest));

        val fold: Pair<ResponseEntity<out Any>, String> = result.fold(
            { error -> logAndHandleError(error, searchRequest) },
            { flights: List<Flight> -> ok(flights) })

        logger.info("aaaaaa ${fold.second}" )
        return fold.first;
    }

    private fun logAndHandleError(error: Error, searchRequest: SearchRequest) =
        when (error) {
            is DepartureDateIsInThePast ->
                logBadRequest("""departureDate   ${error.departureDate} is in the past 
                              current time is ${error.currentDateTime} 
                              request $searchRequest""".trimMargin())
            is SearchNotAllowed ->
                logBadRequest("search not allowed ${error.cause} for request $searchRequest")

            is GenericError ->
                logGenericError("generic error ${error.exception.message} happened for request $searchRequest", error)

        }

    private fun logGenericError(message: String, error: GenericError): ResponseEntity<String> {
        logger.error(message, error.exception)
        return status(500).body("$message")
    }

    private fun logBadRequest(message: String): ResponseEntity<String> {
        logger.error(message)
        return badRequest().body(message)
    }


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

