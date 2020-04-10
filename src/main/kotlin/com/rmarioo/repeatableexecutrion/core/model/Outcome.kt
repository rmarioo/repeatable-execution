package com.rmarioo.repeatableexecutrion.core.model

import arrow.core.Either

class Outcome<out A,out B>( val delegate: Either<out A,out B>,val log: String) {


    inline fun <C> fold(ifLeft: (A) -> C, ifRight: (B) -> C): Pair<C,String> = when (this
        .delegate) {
        is Either.Right -> ifRight(this.delegate.b) to this.log
        is Either.Left -> ifLeft(this.delegate.a) to this.log
    }
}

