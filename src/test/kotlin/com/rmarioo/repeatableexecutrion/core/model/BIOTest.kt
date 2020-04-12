package com.rmarioo.repeatableexecutrion.core.model

import arrow.core.Either
import org.junit.jupiter.api.Test

class BIOTest {

    @Test
     fun create() {

        val division: (Int) -> Int = { x: Int -> 10 /x }
      //  BIO.just<Nothing,Int>(5)

        val bioSuccess: BIO<Throwable, Int> = BIO.suspend { division(2) }
        val bioWithError: BIO<Throwable, Int> = BIO.suspend { division(0) }

        val myfunc: () -> Either<Error.SearchNotAllowed, Int> =
            { Either.left(Error.SearchNotAllowed("dd")) }

        val bioWithErrorCustom: BIO<Error.SearchNotAllowed, Int> = BIO { myfunc() }

        println("prima1 di esecuzione")
        printResult(bioSuccess.unsafeRunSynch())
        println("dopo1 di esecuzione")

        println("prima2 di esecuzione")
        printResult(bioWithError.unsafeRunSynch())
        println("dopo2 di esecuzione")



    }

    private fun printResult(result: Either<Throwable, Int>) {
        result.fold(
            { e -> println("errore ${e.message}") },
            { v -> println("risultato $v") })
    }


}
