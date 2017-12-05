package jewelry.marik.dex.constant

import jewelry.marik.dex.constant.alais.uint32_t
import jewelry.marik.dex.constant.alais.uint8_t
import jewelry.marik.os.OS
import jewelry.marik.util.data.BytePtr
import jewelry.marik.util.data.Pointer
import jewelry.marik.util.data.pointer
import jewelry.marik.util.data.toUInt32

internal inline fun DecodeUnsignedLeb128(data: Pointer<Pointer<uint8_t>>): uint32_t {
    var ptr = data[0]
    var result: uint32_t = (ptr++)[0].toUInt32()

    if (result > 0x7f) {
        var cur = (ptr++)[0].toUInt32()
        result = (result and 0x7f) or ((cur and 0x7f) shl 7)
        if (cur > 0x7f) {
            cur = (ptr++)[0].toUInt32()
            result = result or ((cur and 0x7f) shl 14)
            if (cur > 0x7f) {
                cur = (ptr++)[0].toUInt32()
                result = result or ((cur and 0x7f) shl 21)
                if (cur > 0x7f) {
                    cur = (ptr++)[0].toUInt32()
                    result = result or (cur shl 28)
                }
            }
        }
    }
    data[0] = ptr
    return result
}

internal inline fun DecodeSignedLeb128(data: Pointer<Pointer<uint8_t>>): Int {
    var ptr = data[0]
    var result: Int = ptr[0].toUInt32()

    if (result <= 0x7f) {
        result = (result shl 25) shr 25
    } else {
        var cur = ptr[0].toUInt32()
        result = (result and 0x7f) or ((cur and 0x7f) shl 7)
        if (cur <= 0x7f) {
            result = (result shl 18) shr 18
        } else {
            cur = ptr[0].toUInt32()
            result = result or ((cur and 0x7f) shl 14)
            if (cur <= 0x7f) {
                result = (result shl 11) shr 11
            } else {
                cur = ptr[0].toUInt32()
                result = result or ((cur and 0x7f) shl 21)
                if (cur <= 0x7f) {
                    result = (result shl 4) shr 4
                } else {
                    cur = ptr[0].toUInt32()
                    result = result or (cur shl 28)
                }
            }
        }
    }
    data[0] = ptr
    return result
}

internal inline fun EncodeUnsignedLeb128(dest: Pointer<uint8_t>, value: uint32_t): Pointer<uint8_t> {
    var value = value
    var ptr = dest
    var cur = 0
    while (value != 0) {
        cur = value and 0x7f
        value = value ushr 7

        if (value > 0)
            cur = cur or 0x80
        ptr[0] = cur.toByte()
        ptr++
    }
    return ptr
}

internal inline fun EncodeSignedLeb128(dest: Pointer<uint8_t>, value: Int): Pointer<uint8_t> {
    if (value > 0)
        return EncodeUnsignedLeb128(dest, value)

    var value = value and 0x7fff_ffff
    var ptr = dest
    var cur = 0
    while (value != 0) {
        cur = value and 0x7f
        value = value ushr 7

        if (value != 0)
            cur = cur or 0x80
        else
            cur = highestMaskAs1(cur)
        ptr[0] = cur.toByte()
        ptr++
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
//    EncodeSignedLeb128(-0x112123, 0)
//
//    println(DecodeSignedLeb128(0).ret)

//    println(highestMaskAs1(0x123).toHex())
    val pointer: Pointer<uint8_t> = BytePtr(0)

//    val p2 = EncodeUnsignedLeb128(pointer, 123456)

//    println(p2.address)

//    val p = pointer.pointer
    println(pointer.address)

    println(DecodeUnsignedLeb128(pointer.pointer))
//    println(p[0].address)
    println(pointer.address)
}