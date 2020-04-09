package com.rmarioo.repeatableexecutrion.core

import arrow.core.Either
import arrow.core.Either.Companion.left
import arrow.core.Either.Companion.right
import arrow.core.flatMap
import com.rmarioo.repeatableexecutrion.core.model.Error.DepartureDateIsInThePast
import com.rmarioo.repeatableexecutrion.core.model.Error.GenericError
import com.rmarioo.repeatableexecutrion.core.model.Error.SearchNotAllowed
import com.rmarioo.repeatableexecutrion.core.model.Clock
import com.rmarioo.repeatableexecutrion.core.model.Error
import com.rmarioo.repeatableexecutrion.core.model.Flight
import com.rmarioo.repeatableexecutrion.core.model.Search
import com.rmarioo.repeatableexecutrion.core.port.SupplierRepository
import java.io.PrintWriter
import java.io.StringWriter
import java.time.LocalDateTime

class SearchUseCase(private val clock: Clock,
                    private val supplierRepository: SupplierRepository
) {

    fun doSearch(search: Search): Either<Error, List<Flight>> {

        return  checkDepartureIsInTheFuture(search.departureDate, clock)    .flatMap {
                checkArrivalIsNotInNewYork(search.arrivalAirport) }         .flatMap {

                wrapExceptionToGenericError { supplierRepository.doSearch(search)}
        }
    }

    private fun wrapExceptionToGenericError(searchFunction: () -> List<Flight>): Either<Error, List<Flight>> =
        try {
            right(searchFunction())
        } catch(exception: Exception) {
            left(GenericError(exception))
        }

    private fun exceptionAsString(exception: Exception): String {
        val sw = StringWriter()
        exception.printStackTrace(PrintWriter(sw))
        val exceptionAsString = sw.toString()
        return exceptionAsString
    }

    private fun checkDepartureIsInTheFuture(departureDate: LocalDateTime, clock: Clock):
        Either<DepartureDateIsInThePast, LocalDateTime> =
        if (departureDate.isAfter(clock.currentDateTime())) right(departureDate)
        else left(DepartureDateIsInThePast(departureDate,clock.currentDateTime()))

    private fun checkArrivalIsNotInNewYork(arrivalAirport: String): Either<SearchNotAllowed, String> =
        if (arrivalAirport == "NYC") left(SearchNotAllowed("search in new york is forbidden"))
        else right(arrivalAirport)



}
