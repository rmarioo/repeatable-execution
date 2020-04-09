package com.rmarioo.repeatableexecutrion.core.model

import java.time.LocalDateTime

sealed class Error {
  class DepartureDateIsInThePast(val departureDate: LocalDateTime,val currentDateTime: LocalDateTime) : Error()
  class SearchNotAllowed(val cause: String) : Error()
  class GenericError(val exception: Throwable) : Error()
}
