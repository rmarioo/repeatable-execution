package com.rmarioo.repeatableexecutrion.core.model

import java.time.LocalDateTime

interface Clock {
    fun currentDateTime(): LocalDateTime
}
