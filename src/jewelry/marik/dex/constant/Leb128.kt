package jewelry.marik.dex.constant

import jewelry.marik.dex.constant.alais.uint32_t
import jewelry.marik.os.OS
import jewelry.marik.util.data.toUInt32

internal data class Leb128Result(val ptr: uint32_t, val ret: uint32_t)

internal inline fun DecodeUnsignedLeb128(ptr: uint32_t, buffer: ByteArray = OS.MEMORY): Leb128Result {
    var ptr = ptr
    var result: uint32_t = buffer[ptr++].toUInt32()

    if (result > 0x7f) {
        var cur = buffer[ptr++].toUInt32()
        result = (result and 0x7f) or ((cur and 0x7f) shl 7)
        if (cur > 0x7f) {
            cur = buffer[ptr++].toUInt32()
            result = result or ((cur and 0x7f) shl 14)
            if (cur > 0x7f) {
                cur = buffer[ptr++].toUInt32()
                result = result or ((cur and 0x7f) shl 21)
                if (cur > 0x7f) {
                    cur = buffer[ptr++].toUInt32()
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
        var cur = buffer[ptr++].toUInt32()
        result = (result and 0x7f) or ((cur and 0x7f) shl 7)
        if (cur <= 0x7f) {
            result = (result shl 18) shr 18
        } else {
            cur = buffer[ptr++].toUInt32()
            result = result or ((cur and 0x7f) shl 14)
            if (cur <= 0x7f) {
                result = (result shl 11) shr 11
            } else {
                cur = buffer[ptr++].toUInt32()
                result = result or ((cur and 0x7f) shl 21)
                if (cur <= 0x7f) {
                    result = (result shl 4) shr 4
                } else {
                    cur = buffer[ptr++].toUInt32()
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
    if (value > 0)
        return EncodeUnsignedLeb128(value, ptr, buffer)

    var value = value and 0x7fff_ffff
    var ptr = ptr
    var cur = 0
    while (value != 0) {
        cur = value and 0x7f
        value = value ushr 7

        if (value != 0)
            cur = cur or 0x80
        else
            cur = highestMaskAs1(cur)
        buffer[ptr++] = cur.toByte()
    }
    return ptr
}

private inline fun highestMaskAs1(value: Int): Int {
    var start = 0x8000_0000.toInt()
    while (start != 0 && value and start == 0)
        start = start ushr 1
    return start shl 1 or value
}

fun main(vararg arg: String) {
    EncodeSignedLeb128(-0x112123, 0)
//
    println(DecodeSignedLeb128(0).ret)

//    println(highestMaskAs1(0x123).toHex())

}