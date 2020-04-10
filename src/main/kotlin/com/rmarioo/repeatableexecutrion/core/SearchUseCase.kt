package com.rmarioo.repeatableexecutrion.core

import arrow.core.Either
import arrow.core.Either.Companion.left
import arrow.core.Either.Companion.right
import com.rmarioo.repeatableexecutrion.core.model.Clock
import com.rmarioo.repeatableexecutrion.core.model.Error
import com.rmarioo.repeatableexecutrion.core.model.Error.DepartureDateIsInThePast
import com.rmarioo.repeatableexecutrion.core.model.Error.GenericError
import com.rmarioo.repeatableexecutrion.core.model.Error.SearchNotAllowed
import com.rmarioo.repeatableexecutrion.core.model.Flight
import com.rmarioo.repeatableexecutrion.core.model.Outcome
import com.rmarioo.repeatableexecutrion.core.model.Search
import com.rmarioo.repeatableexecutrion.core.port.SupplierRepository
import java.time.LocalDateTime

class SearchUseCase(private val clock: Clock,
                    private val supplierRepository: SupplierRepository
) {

    fun doSearch(search: Search): Outcome<Error, List<Flight>> {

        return  checkDepartureIsInTheFuture(search.departureDate, clock)    .flatMap {
                checkArrivalIsNotInNewYork(search.arrivalAirport) }         .flatMap {

                doSearchOnSupplier(search)
        }
    }

    private fun doSearchOnSupplier(search: Search) =
        try {
            Outcome(right(supplierRepository.doSearch(search)),"supplier called with $search")
        } catch(exception: Exception) {
            Outcome(left(GenericError(exception)),"wrapExceptionToGenericError")
        }

    private fun checkDepartureIsInTheFuture(departureDate: LocalDateTime, clock: Clock):
        Outcome<DepartureDateIsInThePast, LocalDateTime> =
        if (departureDate.isAfter(clock.currentDateTime())) Outcome(right(departureDate),"checkDepartureIsInTheFuture")
        else Outcome(left(DepartureDateIsInThePast(departureDate,clock.currentDateTime())),"checkDepartureIsInTheFuture")

    private fun checkArrivalIsNotInNewYork(arrivalAirport: String): Outcome<SearchNotAllowed, String> =
        if (arrivalAirport == "NYC") Outcome(left(SearchNotAllowed("search in new york is " +
            "forbidden")),"checkArrivalIsNotInNewYork")
        else Outcome(right(arrivalAirport),"checkArrivalIsNotInNewYork")



}

fun <A, B, C> Outcome<A, B>.flatMap(f: (B) -> Outcome<A, C>): Outcome<A, C> =
    let { a: Outcome<A, B> ->
        when (val thisEither = a.delegate) {
            is Either.Right -> {
                val flatMappedEither: Outcome<A, C> = f(thisEither.b)
                Outcome(flatMappedEither.delegate,"${a.log} ${flatMappedEither.log}")
            }
            is Either.Left -> Outcome(thisEither,a.log)
        }
    }

