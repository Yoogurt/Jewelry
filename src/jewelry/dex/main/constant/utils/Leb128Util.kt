package jewelry.dex.main.constant.utils

import jewelry.dex.main.constant.uint32_t
import jewelry.dex.os.OS
import jewelry.dex.util.data.toHex
import jewelry.dex.util.data.toUInt32

internal data class Leb128Result(val ptr: uint32_t, val ret: uint32_t)

internal inline fun DecodeUnsignedLeb128(ptr: uint32_t, buffer: ByteArray = OS.MEMORY): Leb128Result {
    var ptr = ptr
    var result: uint32_t = buffer[ptr++].toUInt32()

    if (result > 0x7f) {
        var cur = buffer[ptr++].toInt()
        result = (result and 0x7f) or ((cur and 0x7f) shl 7)
        if (cur > 0x7f) {
            cur = buffer[ptr++].toInt()
            result = result or ((cur and 0x7f) shl 14)
            if (cur > 0x7f) {
                cur = buffer[ptr++].toInt()
                result = result or ((cur and 0x7f) shl 21)
                if (cur > 0x7f) {
                    cur = buffer[ptr++].toInt()
                    result = result or (cur shl 28)
                }
            }
        }
    }
    return Leb128Result(ptr, result)
}

internal inline fun DecodeSignedLeb128(ptr: uint32_t, buffer: ByteArray = OS.MEMORY): Leb128Result {
    var ptr = ptr
    var result: Int = buffer[ptr++].toUInt32()

    if (result <= 0x7f) {
        result = (result shl 25) shr 25
    } else {
        var cur = buffer[ptr++].toInt()
        result = (result and 0x7f) or ((cur and 0x7f) shl 7)
        if (cur <= 0x7f) {
            result = (result shl 18) shr 18
        } else {
            cur = buffer[ptr++].toInt()
            result = result or ((cur and 0x7f) shl 14)
            if (cur <= 0x7f) {
                result = (result shl 11) shr 11
            } else {
                cur = buffer[ptr++].toInt()
                result = result or ((cur and 0x7f) shl 21)
                if (cur <= 0x7f) {
                    result = (result shl 4) shr 4
                } else {
                    cur = buffer[ptr++].toInt()
                    result = result or (cur shl 28)
                }
            }
        }
    }
    return Leb128Result(ptr, result)
}

internal inline fun EncodeUnsignedLeb128(value: uint32_t, ptr: uint32_t, buffer: ByteArray = OS.MEMORY): uint32_t {
    var value = value
    var ptr = ptr
    var cur = 0
    while (value != 0) {
        cur = value and 0x7f
        value = value ushr 7

        if (value > 0)
            cur = cur or 0x80
        buffer[ptr++] = cur.toByte()
    }
    return ptr
}

internal inline fun EncodeSignedLeb128(value: Int, ptr: uint32_t, buffer: ByteArray = OS.MEMORY): Int {
    var value = value
    var ptr = ptr
    var cur = 0
    while (value != 0) {
        cur = value and 0x7f
        value = value ushr 7

        if (value > 0)
            cur = cur or 0x80
        buffer[ptr++] = cur.toByte()
    }
    return ptr
}

fun uint32_t.compareTo2(other: uint32_t): Int {
    return if (this == other)
        0
    else {
        if (this == 0)
            -1
        if (other == 0)
            1

        if (this > 0) {
            if (other < 0)
                -1
            else {
                if (this > other)
                    1
                else
                    -1
            }
        } else {
            if (other > 0)
                1
            else
                -1
        }
    }
}

fun main(vararg arg: String) {
    EncodeUnsignedLeb128(-0x123456, 0)

    println(DecodeUnsignedLeb128(0).ret.toHex())
}