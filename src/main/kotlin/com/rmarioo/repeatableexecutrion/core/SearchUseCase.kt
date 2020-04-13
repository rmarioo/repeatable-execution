package com.rmarioo.repeatableexecutrion.core

import arrow.core.Either.Companion.left
import arrow.core.Either.Companion.right
import com.rmarioo.repeatableexecutrion.core.model.BIO
import com.rmarioo.repeatableexecutrion.core.model.Clock
import com.rmarioo.repeatableexecutrion.core.model.Error
import com.rmarioo.repeatableexecutrion.core.model.Error.DepartureDateIsInThePast
import com.rmarioo.repeatableexecutrion.core.model.Error.GenericError
import com.rmarioo.repeatableexecutrion.core.model.Error.SearchNotAllowed
import com.rmarioo.repeatableexecutrion.core.model.Flight
import com.rmarioo.repeatableexecutrion.core.model.Search
import com.rmarioo.repeatableexecutrion.core.model.flatMap
import com.rmarioo.repeatableexecutrion.core.port.SupplierRepository
import java.time.LocalDateTime

class SearchUseCase(private val clock: Clock,
                    private val supplierRepository: SupplierRepository
) {

    fun doSearch(search: Search): BIO<Error, List<Flight>> {

        return  checkDepartureIsInTheFuture(search.departureDate, clock)    .flatMap {
                checkArrivalIsNotInNewYork(search.arrivalAirport) }         .flatMap {

                wrapExceptionToGenericError { supplierRepository.doSearch(search)}
        }
    }

    private fun wrapExceptionToGenericError(searchFunction: () -> List<Flight>): BIO<GenericError,
        List<Flight>> {
        return BIO( {searchFunction()},{ GenericError(it) })
    }


    private fun checkDepartureIsInTheFuture(departureDate: LocalDateTime, clock: Clock):
        BIO<DepartureDateIsInThePast, LocalDateTime> =
        if (departureDate.isAfter(clock.currentDateTime())) BIO{ right(departureDate)}
        else BIO { left(DepartureDateIsInThePast(departureDate,clock.currentDateTime()))}

    private fun checkArrivalIsNotInNewYork(arrivalAirport: String): BIO<SearchNotAllowed, String> =
        if (arrivalAirport == "NYC") BIO{left(SearchNotAllowed("search in new york is forbidden"))}
        else BIO{right(arrivalAirport)}

}
