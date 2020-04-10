package com.rmarioo.repeatableexecutrion.supplierAdapter

import com.rmarioo.repeatableexecutrion.core.model.Flight
import com.rmarioo.repeatableexecutrion.core.model.Search
import com.rmarioo.repeatableexecutrion.core.port.SupplierRepository
import java.math.BigDecimal

class RealSupplierRepository(private val url: String) :
    SupplierRepository
{
    override fun doSearch(search: Search): List<Flight> =

        if (search.simulateServerError)
            throw RuntimeException("unexpected error happened for $search")
        else
            listOf(
                Flight(
                    "flightABC_1",
                    BigDecimal("100"),
                    search.departureDate,
                    search.arrivalDate
                ),
                Flight(
                    "flightABC_2",
                    BigDecimal("200"),
                    search.departureDate,
                    search.arrivalDate
                )
        )

}
