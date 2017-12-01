package jewelry.marik.util.data

object UtilGlobal {
    var DEFAULT_LITTLE_ENDIAN: Boolean = true
}

//---------------------- BYTE ------------------------//

fun Byte.toUInt32(): Int = if (this < 0) this + 256 else toInt()

fun Byte.toHex(): String {
    val internal = toUInt32()

    var result = Integer.toHexString(internal)
    if (result.length < 2)
        result = 0.toString() + result
    else if (result.length > 2)
        result = result.substring(result.length - 2, result.length)
    return result
}


//---------------------- BYTE ARRAY ------------------------//

fun ByteArray.toHex(startIndex: Int = 0, length: Int = size): String {
    val sb = StringBuilder()

    for (m in 0 until length)
        sb.append(this[m + startIndex].toHex()).append(" ")

    return sb.toString().trim { it <= ' ' }
}

inline fun ByteArray.copyTo(target: ByteArray, startIndex: Int = 0, length: Int = target.size) =
        System.arraycopy(this, startIndex, target, 0, length)


fun ByteArray.equals(obj: Int, startIndex: Int = 0, length: Int = size, isLittleEndian: Boolean = UtilGlobal.DEFAULT_LITTLE_ENDIAN): Boolean =
        obj == toUInt32(startIndex, length, isLittleEndian)


inline fun ByteArray.startWith(other: ByteArray): Boolean = partialEquals(other, 0)


fun ByteArray.partialEquals(other: ByteArray, startIndex: Int): Boolean {
    other.forEachIndexed { index, it ->
        if (it != this[index + startIndex])
            return false
    }
    return true
}

fun ByteArray.toUInt32(startIndex: Int = 0, length: Int = 4, isLittleEndian: Boolean = UtilGlobal.DEFAULT_LITTLE_ENDIAN): Int {

    if (isLittleEndian) {
        var actullyValue = 0

        for (m in startIndex until startIndex + length) {
            val wordValue = this[m].toUInt32()

            actullyValue = actullyValue or (wordValue shl ((m - startIndex) shl 3))
        }
        return actullyValue

    } else {
        var actullyValue = 0

        for (m in startIndex until startIndex + length) {
            actullyValue = actullyValue shl 8 or this[m].toUInt32()
        }
        return actullyValue
    }
}

fun ByteArray.equals(obj: Long, startIndex: Int = 0, length: Int = size, isLittleEndian: Boolean = UtilGlobal.DEFAULT_LITTLE_ENDIAN): Boolean {
    return obj == toUInt64(startIndex, length, isLittleEndian)
}

fun ByteArray.toUInt64(startIndex: Int = 0, length: Int = 8, isLittleEndian: Boolean = UtilGlobal.DEFAULT_LITTLE_ENDIAN): Long {

    if (isLittleEndian) {
        var actullyValue = 0L

        for (m in startIndex until startIndex + length) {
            val wordValue = this[m].toUInt32().toLong()
            actullyValue = actullyValue or (wordValue shl ((m - startIndex) shl 3))
        }
        return actullyValue

    } else {
        var actullyValue = 0L

        for (m in startIndex until startIndex + length) {
            actullyValue = actullyValue shl 8 or this[m].toUInt32().toLong()
        }
        return actullyValue
    }

}

fun ByteArray.toInt16(startIndex: Int = 0, length: Int = 2, isLittleEndian: Boolean = UtilGlobal.DEFAULT_LITTLE_ENDIAN): Short =
        toUInt32(startIndex, length).toShort()
//---------------------- SHORT ------------------------//

fun Short.toHex() = toByteArray().toHex()

fun Short.toByteArray(isLittleEndian: Boolean = UtilGlobal.DEFAULT_LITTLE_ENDIAN): ByteArray {
    var `val` = this.toInt()
    val ret = ByteArray(2)
    var startIndex = if (isLittleEndian)
        0 else ret.size - 1
    val step = if (isLittleEndian) 1 else -1

    while (startIndex in 0..(ret.size - 1)) {
        ret[startIndex] = `val`.toByte()
        `val` = `val` shr 8
        startIndex += step
    }
    return ret
}

//---------------------- INT ------------------------//
fun Int.toHex() = toByteArray().toHex()

fun Int.upAlign() = (this + 3) and (-1 xor 3)

fun Int.assertAlignment() {
    if (this and 1 == 1)
        throw AssertionError()
}

fun Int.toByteArray(isLittleEndian: Boolean = UtilGlobal.DEFAULT_LITTLE_ENDIAN): ByteArray {
    var `val` = this
    val ret = ByteArray(4)
    var startIndex = if (isLittleEndian)
        0 else ret.size - 1
    val step = if (isLittleEndian) 1 else -1

    while (startIndex in 0..(ret.size - 1)) {
        ret[startIndex] = `val`.toByte()
        `val` = `val` shr 8
        startIndex += step
    }
    return ret
}

fun Int.isAlignedParam(alignment: Int) = this % alignment == 0

fun Int.compareUnsigned(other: Int): Int {
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
            else {
                if (other < this)
                    -1
                else
                    1
            }
        }
    }
}

fun Int.toUnsigned(): Long =
        if (this < 0) toLong() + (1L shl 32) else toLong()

//---------------------- LONG ------------------------//
fun Long.toHex() = toByteArray().toHex()

fun Long.toByteArray(isLittleEndian: Boolean = UtilGlobal.DEFAULT_LITTLE_ENDIAN): ByteArray {
    var `val` = this
    val ret = ByteArray(8)
    var startIndex = if (isLittleEndian)
        0 else ret.size - 1
    val step = if (isLittleEndian) 1 else -1

    while (startIndex in 0..(ret.size - 1)) {
        ret[startIndex] = `val`.toByte()
        `val` = `val` shr 8
        startIndex += step
    }
    return ret
}

fun main(vararg arg: String) {
//    var test = -122
//    var test2 = -123
//    println(test.compareUnsigned(test2))
    println(8.upAlign())
}
