package com.rmarioo.repeatableexecutrion.core.port

import com.rmarioo.repeatableexecutrion.core.model.Flight
import com.rmarioo.repeatableexecutrion.core.model.Search

interface SupplierRepository {
    fun doSearch(search: Search): List<Flight>
}
