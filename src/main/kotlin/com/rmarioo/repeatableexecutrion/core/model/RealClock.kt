package com.rmarioo.repeatableexecutrion.core.model

import com.rmarioo.repeatableexecutrion.core.model.Clock
import java.time.LocalDateTime

class RealClock : Clock {
    override fun currentDateTime(): LocalDateTime {
        return LocalDateTime.now()
    }

}
