package jewelry.dex.util.log

import jewelry.dex.main.DexException
import jewelry.dex.main.debug

private var out = System.out

inline fun String.log() {
    Log.i(this)
}

inline fun String.error() {
    throw DexException(this)
}

object Log {
    fun i(msg: String) {
        if (debug)
            out.println("* [I] $msg")
    }

    fun e(msg: String) {
        if (debug)
            out.println("* [E] $msg")
    }
}
