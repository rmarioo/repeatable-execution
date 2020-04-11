package com.rmarioo.repeatableexecutrion.core.model

import arrow.core.Either
import org.junit.jupiter.api.Test

class BIOTest {

    @Test
     fun create() {

        val division: (Int) -> Int = { x: Int -> 10 /x }
      //  BIO.just<Nothing,Int>(5)

        val bioSuccess = BIO<Error,Int> { division(0) }
        val bioWithError = BIO<Error,Int> { division(0) }

        val either: Either<Error,Int> = bioSuccess.unsafeRunSynch()
    }


}
