package jewelry.dex.util.data

object UtilGlobal {
    var DEFAULT_LITTLE_ENDIAN: Boolean = true
}

//---------------------- BYTE ------------------------//

fun Byte.toInt32(): Int = if (this < 0) this + 256 else toInt()

fun Byte.toHex(): String {
    val internal = toInt32()

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
     obj == toInt32(startIndex, length, isLittleEndian)


inline fun ByteArray.startWith(other: ByteArray): Boolean = partialEquals(other, 0)


fun ByteArray.partialEquals(other: ByteArray, startIndex: Int): Boolean {
    other.forEachIndexed { index, it ->
        if (it != this[index + startIndex])
            return false
    }
    return true
}

fun ByteArray.toInt32(startIndex: Int = 0, length: Int = 4, isLittleEndian: Boolean = UtilGlobal.DEFAULT_LITTLE_ENDIAN): Int {

    if (isLittleEndian) {
        var actullyValue = 0

        for (m in startIndex until startIndex + length) {
            val wordValue = this[m].toInt32()

            actullyValue = actullyValue or (wordValue shl ((m - startIndex) shl 3))
        }
        return actullyValue

    } else {
        var actullyValue = 0

        for (m in startIndex until startIndex + length) {
            actullyValue = actullyValue shl 8 or this[m].toInt32()
        }
        return actullyValue
    }
}

fun ByteArray.equals(obj: Long, startIndex: Int = 0, length: Int = size, isLittleEndian: Boolean = UtilGlobal.DEFAULT_LITTLE_ENDIAN): Boolean {
    return obj == toInt64(startIndex, length, isLittleEndian)
}

fun ByteArray.toInt64(startIndex: Int = 0, length: Int = 8, isLittleEndian: Boolean = UtilGlobal.DEFAULT_LITTLE_ENDIAN): Long {

    if (isLittleEndian) {
        var actullyValue = 0L

        for (m in startIndex until startIndex + length) {
            val wordValue = this[m].toInt32().toLong()
            actullyValue = actullyValue or (wordValue shl ((m - startIndex) shl 3))
        }
        return actullyValue

    } else {
        var actullyValue = 0L

        for (m in startIndex until startIndex + length) {
            actullyValue = actullyValue shl 8 or this[m].toInt32().toLong()
        }
        return actullyValue
    }

}

//---------------------- SHORT ------------------------//

fun ByteArray.toInt16(startIndex: Int = 0, length: Int = 2, isLittleEndian: Boolean = UtilGlobal.DEFAULT_LITTLE_ENDIAN): Short =
        toInt32(startIndex, length).toShort()


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

fun Int.isAlignedParam(alignment: Int): Boolean {
    return this % alignment == 0
}

//---------------------- LONG ------------------------//
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
    var test = byteArrayOf(0x12, 0x34, 0x56, 0x12, 0x22, 0x33)
    var test2 = byteArrayOf(0x12, 0x33, 0x56)
    println(test.startWith(test2))
}
