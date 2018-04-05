// FILE: inlined.kt
// WITH_RUNTIME
// NO_CHECK_LAMBDA_INLINING

import kotlin.coroutines.experimental.*
import kotlin.coroutines.experimental.intrinsics.*

interface I {
    suspend fun bar()
}

class A(val v: String) : I {
    override inline suspend fun bar() {
        log += "before bar($v);"
        foo("1:$v")
        log += "inside bar($v);"
        foo("2:$v")
        log += "after bar($v);"
    }
}

var continuation: () -> Unit = { }
var log = ""
var finished = false

suspend fun <T> foo(v: T): T = suspendCoroutineOrReturn { x ->
    continuation = {
        x.resume(v)
    }
    log += "foo($v);"
    COROUTINE_SUSPENDED
}

// FILE: inlineSite.kt

import kotlin.coroutines.experimental.*
import kotlin.coroutines.experimental.intrinsics.*

fun builder(c: suspend () -> Unit) {
    val continuation = object: Continuation<Unit> {
        override val context: CoroutineContext
            get() = EmptyCoroutineContext

        override fun resume(value: Unit) {
            continuation = { }
            finished = true
        }

        override fun resumeWithException(exception: Throwable) {
            throw exception
        }
    }
    c.startCoroutine(continuation)
}

suspend fun baz() {
    val a = A("A")
    a.bar()

    log += "between bar;"

    val b: I = A("B")
    b.bar()
}

val expectedString =
    "before bar(A);foo(1:A);@;inside bar(A);foo(2:A);@;after bar(A);" +
            "between bar;" +
            "before bar(B);foo(1:B);@;inside bar(B);foo(2:B);@;after bar(B);"

fun box(): String {
    builder {
        baz()
    }

    while (!finished) {
        log += "@;"
        continuation()
    }

    if (log != expectedString) return "fail: $log"

    return "OK"
}