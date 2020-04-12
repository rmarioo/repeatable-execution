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

    fun myUnsafeRunSync(): Either<E, A> {
       return  runLoop(this)
    }

    private fun <B> runLoop(bio: BIO<E, A>): Either<E, A> {
        return when(bio) {
            is Pure -> Either.Right(bio.a)
            is Suspend -> try {
                            Either.right(bio.f())
                        } catch (e: Exception) {
                            Either.left(bio.fme(e))
                        }
            is SuspendEither -> bio.f()
            is Bind<E,B,A> ->  {
                val either: Either<E, B> =  bio.cont.myUnsafeRunSync()
                val hh: BIO<E, A> = when(either) {
                    is Either.Right -> bio.g(either.b)
                    is Either.Left -> bio
                }
               hh.myUnsafeRunSync()
            }
        }
    }


    internal data class Bind<E, A, B>(val cont: BIO<E, A>, val g: (A) -> BIO<E,B>) : BIO<E,B>
        () {
        override fun unsafeRunSynch(): Either<E, B> {
            throw AssertionError("Unreachable")
        }

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
