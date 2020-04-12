package com.rmarioo.repeatableexecutrion.core.model

import arrow.core.Either

sealed class BIO<out E, out A> {
    abstract fun unsafeRunSynch(): Either<E, A>

    class Pure<out E, out A>(val a: A) : BIO<E, A>() {
        override fun unsafeRunSynch(): Either<E, A> = Either.right(a)
    }

    class Suspend<out E, out A>(val f: () -> A, val fme: (Exception) -> E) : BIO<E, A>() {
        override fun unsafeRunSynch(): Either<E, A> {
            return try {
                Either.right(f())
            } catch (e: Exception) {
                Either.left(fme(e))
            }
        }
    }

    class SuspendEither<out E, out A>(val f: () -> Either<E,A>) : BIO<E, A>() {
        override fun unsafeRunSynch(): Either<E, A> = f()
    }

    companion object {

        operator fun <E, A> invoke(f: () -> A, fme: (Exception) -> E): BIO<E, A> =
            Suspend(f, fme)

        operator fun <E, A> invoke(f: () -> Either<E,A>): BIO<E, A> =
            SuspendEither(f)

        fun <A> suspend(f: () -> A): BIO<Throwable, A> =
            Suspend(f, { e -> e })

        fun <A> just(a: A): BIO<Nothing, A> = Pure(a)
    }


}

class GenericError
