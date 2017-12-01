package jewelry.marik.util.log

import jewelry.marik.util.data.toByteArray
import jewelry.marik.util.data.toHex
import jewelry.marik.dex.DexException
import jewelry.marik.dex.debug

private var out = System.out

// String Support

inline fun String.log() {
    Log.i(this)
}

inline fun String.warning() {
    Log.w(this)
}

inline fun String.error(): Nothing {
    throw DexException(this)
}

inline fun String.error(out: ByteArray): Nothing {
    "$this ${out.toHex()}".error()
}

//inline fun String.error(action: () -> Unit) {
//    action()
//    error()
//}

inline fun String.errorVerify(expect: ByteArray, got: ByteArray): Nothing {
    "$this , expect ${expect.toHex()} , but got ${got.toHex()}".error()
}

// ByteArray Support
inline fun ByteArray.log(msg: String) {
    "$msg ${toHex()}".log()
}

inline fun ByteArray.error(msg: String): Nothing {
    "$msg ${toHex()}".error()
}

inline fun ByteArray.errorVerify(expect: ByteArray, msg: String): Nothing {
    msg.errorVerify(expect, this)
}

inline fun Number.log(msg: String) = when (this) {
    is Byte -> "$msg ${toHex()} ($this)".log()
    is Short -> "$msg ${this.toByteArray().toHex()} ($this)".log()
    is Int -> "$msg ${this.toByteArray().toHex()} ($this)".log()
    is Long -> "$msg ${this.toByteArray().toHex()} ($this)".log()
    else ->
        "$msg".log()
}

inline fun Number.error(msg: String): Nothing = when (this) {
    is Byte -> "$msg ${toHex()}".error()
    is Short -> "$msg ${this.toByteArray().toHex()}".error()
    is Int -> "$msg ${this.toByteArray().toHex()}".error()
    is Long -> "$msg ${this.toByteArray().toHex()}".error()
    else ->
        "$msg".error()
}

object Log {
    fun i(msg: String) {
        if (debug)
            out.println("[I] $msg")
    }

    fun w(msg: String) {
        if (debug)
            out.println("[W] $msg")
    }

    fun e(msg: String) {
        if (debug)
            out.println("[E] $msg")
    }
}
