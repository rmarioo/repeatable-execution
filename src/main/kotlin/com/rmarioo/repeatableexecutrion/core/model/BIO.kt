package com.rmarioo.repeatableexecutrion.core.model

import arrow.core.Either
import java.lang.RuntimeException

sealed class BIO<out E, out A> {
    abstract fun attempt(): Either<E, A>

    class Pure<out E, out A>(private val a: A) : BIO<E, A>() {
        override fun attempt(): Either<E, A> = Either.right(a)
    }

    class Suspend<out E, out A>(val f: () -> A, val fme: (Exception) -> E) : BIO<E, A>() {
        override fun attempt(): Either<E, A> {
            return try {
                Either.right(f())
            } catch (e: Exception) {
                Either.left(fme(e))
            }
        }
    }

    class SuspendEither<out E, out A>(val f: () -> Either<E,A>) : BIO<E, A>() {
        override fun attempt(): Either<E, A> = f()
    }

     data class Bind<E, A, B>(val cont: BIO<E, A>, val g: (A) -> BIO<E,B>) : BIO<E,B>
        () {
        override fun attempt(): Either<E, B> {

            return when(val either: Either<E, A> = cont.attempt()) {
                is Either.Right -> g(either.b).attempt()
                is Either.Left -> either
            }
        }

    }

     data class Map<E, A, B>(val cont: BIO<E, A>, val g: (A) -> B) : BIO<E,B>
        () {
        override fun attempt(): Either<E, B> {
            return when(val either: Either<E, A> = cont.attempt()) {
                is Either.Right -> Either.Right(g(either.b))
                is Either.Left -> either
            }
        }

    }

    fun unsafeRunAsync(): A
    {
        return attempt().fold(
            { e: E -> throw RuntimeException("error $e") }, { v -> v })
    }

    companion object {

        operator fun <E, A> invoke(f: () -> A, fme: (Exception) -> E): BIO<E, A> =
            Suspend(f, fme)

        operator fun <E, A> invoke(f: () -> Either<E,A>): BIO<E, A> =
            SuspendEither(f)

        fun <A> task(f: () -> A): BIO<Throwable, A> =
            Suspend(f, { e -> e })

        fun <A> just(a: A): BIO<Nothing, A> = Pure(a)
    }

}



fun <A, B, C> BIO<A, B>.flatMap(other: (B) -> BIO<A, C>): BIO<A, C> =
    BIO.Bind(this,other)

fun <A, B, C> BIO<A, B>.map(other: (B) -> C): BIO<A, C> =
    BIO.Map(this,other)


operator fun <E> BIO<E, Int>.plus(result2: BIO<E, Int>): Int =
    this.flatMap { r1 -> result2.map { r2 -> r1 + r2 } }.unsafeRunAsync()

