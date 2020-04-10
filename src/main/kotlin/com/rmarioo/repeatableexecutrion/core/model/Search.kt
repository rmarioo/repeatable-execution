package com.rmarioo.repeatableexecutrion.core.model

import java.time.LocalDateTime

data class Search(
    val departureDate: LocalDateTime,
    val arrivalDate: LocalDateTime,
    val departureAirport: String,
    val arrivalAirport: String,
    val adults: Int,
    val simulateServerError: Boolean
)
