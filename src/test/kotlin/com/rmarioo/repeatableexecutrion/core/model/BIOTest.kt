package com.rmarioo.repeatableexecutrion.core.model

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class BIOTest {

    @Test
    fun referentialTransparency() {

        val p1WithDuplication               = executeProgram { `p1 with duplication`() }
        val p1Refactored                    = executeProgram { `p1 refactored`() }

        Assertions.assertThat(p1Refactored).isNotEqualTo(p1WithDuplication)

        val p2WithDuplication               = executeProgram { `p2 with duplication`()}
        val p2Refactored                    = executeProgram { `p2 refactored`() }

        Assertions.assertThat(p2Refactored).isEqualTo(p2WithDuplication)
        Assertions.assertThat(p2Refactored).isEqualTo(p1WithDuplication)
    }


    private fun `p1 with duplication`(): Int {

        val result1: Int = doSomething(3)
        val result2: Int = doSomething(3)

        return result1 + result2
    }

    private fun `p1 refactored`(): Int {

        val result: Int = doSomething(3)

        return result + result
    }


    private fun `p2 with duplication`(): Int {
        val result1 = BIO.task { doSomething(3) }
        val result2 = BIO.task { doSomething(3) }

        return result1 + result2
    }


    private fun `p2 refactored`(): Int {
        val result1 = BIO.task { doSomething(3) }

        return result1 + result1
    }

    private fun executeProgram(f: () -> Int): Int {
        resetState()

        return f()
    }
    // doSomething - try to run examples without looking at the implementation
    // but just the signature
    private fun doSomething(input: Int): Int {
        return if (availableSeats >= input) {
            availableSeats -= input
            input
        } else 0

    }

    private var availableSeats = A_CONST

    private fun resetState() {
        availableSeats = A_CONST
    }

    companion object {
        const val A_CONST = 4
    }


/*    @Test
    fun create() {

        val division: (Int) -> Int = { x: Int -> 10 /x }

        val bioSuccess: BIO<Throwable, Int> = BIO.task { division(2) }
        val bioWithError: BIO<Throwable, Int> = BIO.task { division(0) }


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
    }*/
}
