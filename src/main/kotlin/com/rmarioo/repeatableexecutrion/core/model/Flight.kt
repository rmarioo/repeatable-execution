package com.rmarioo.repeatableexecutrion.core.model

import java.math.BigDecimal
import java.time.LocalDateTime

data class Flight(val number: String,
                  val amount: BigDecimal,
                  val departureDate: LocalDateTime,
                  val arrivalDate: LocalDateTime)
