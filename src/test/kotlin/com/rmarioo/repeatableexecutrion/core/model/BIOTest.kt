package com.rmarioo.repeatableexecutrion.core.model

import arrow.core.Either
import org.assertj.core.api.Assertions
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
        printResult(bioSuccess.attempt())
        println("dopo1 di esecuzione")

        println("prima2 di esecuzione")
        printResult(bioWithError.attempt())
        println("dopo2 di esecuzione")





    }



    private fun printResult(result: Either<Throwable, Int>) {
        result.fold(
            { e -> println("errore ${e.message}") },
            { v -> println("risultato $v") })
    }

    @Test
    fun referentialTransparency() {

        val `p1 with duplication`               = executeProgram( { `p1 with duplication`() })
        val `p1 refactored`                     = executeProgram( { `p1 refactored`() })

        Assertions.assertThat(`p1 refactored`).isNotEqualTo(`p1 with duplication`)

        val `p2 with duplication`               = executeProgram( { `p2 with duplication`()})
        val `p2 refactored`                     = executeProgram( { `p2 refactored`() })

        Assertions.assertThat(`p2 refactored`).isEqualTo(`p2 with duplication`)
        Assertions.assertThat(`p2 refactored`).isEqualTo(`p1 with duplication`)
    }


    fun `p1 with duplication`(): Int {

        val result1: Int = doSomething(3)
        val result2: Int = doSomething(3)


        val total = result1 + result2;

        return total;
    }

    fun `p1 refactored`(): Int {

        val result: Int = doSomething(3)

        val total = result + result;

        return total;
    }


    fun `p2 with duplication`(): Int {
        val result1 = BIO.suspend { doSomething(3) }
        val result2 = BIO.suspend { doSomething(3) }

        val total = result1 + result2

        return total
    }


    fun `p2 refactored`(): Int {
        val result1 = BIO.suspend { doSomething(3) }

        val total = result1 + result1
        return total
    }

    private fun executeProgram(f: () -> Int): Int {
        resetState()

        return f();
    }
    // doSomething - try to run examples without looking at the implementation
    // but just the signature
    fun doSomething(input: Int): Int {
        if (availableSeats >= input)
        {
            availableSeats= availableSeats -input
            return input
        }
        else return 0

    }

    val A_CONST = 4
    var availableSeats = A_CONST

    private fun resetState() {
        availableSeats = A_CONST
    }

}
