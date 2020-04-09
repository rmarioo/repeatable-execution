package com.rmarioo.repeatableexecutrion.rest

import java.time.LocalDateTime

data class SearchRequest(
    val departureDate: LocalDateTime,
    val arrivalDate: LocalDateTime,
    val departureAirport: String,
    val arrivalAirport: String,
    val adults: Int
)
