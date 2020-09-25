package io.btc.lsp.handler

/**
 * This file exists so we can read it in the LSP server and make assertions on it
 */
class FooBar {
    fun fooBar() {
        println("This is just random foo within some more random foo")
    }
}

class Quux {
    init {
        FooBar().fooBar()
    }

}

fun aNonFooYetFoo() {
    println("This is still a foo; yet it is independent of any other foos. Which makes it feel special.")
}

object SingletonFoo {
    fun whyDoIExist() {
        println("The eternal question")
        answerToLifeUniverseAndEverything()
    }

    fun answerToLifeUniverseAndEverything(): Int {
        return 42
    }
}