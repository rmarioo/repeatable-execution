package com.rmarioo.repeatableexecutrion.core.model

import arrow.core.Either
import kotlin.coroutines.EmptyCoroutineContext

sealed class BIO<out E,out A> {
    abstract fun unsafeRunSynch(): Either<E, A>

    class Pure<out E,out A>(val a: A): BIO< E, A>() {
        override fun unsafeRunSynch(): Either<E, A> = Either.right(a)
    }

    class Suspend<out E,out A>(val f: () -> A): BIO<E,A>() {
        override fun unsafeRunSynch(): Either<E, A> {
           TODO()
        }
    }

    companion object {

       operator fun <E,A> invoke(f:  () -> A): BIO<E,A> =
           Suspend(f)

       fun <E,A> just(a: A): BIO<E,A> = Pure(a)
   }


}
