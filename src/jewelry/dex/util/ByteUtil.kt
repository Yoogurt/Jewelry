package jewelry.dex.util

import java.io.IOException
import java.io.RandomAccessFile
import kotlin.experimental.or

object ByteUtil {

    fun byte2Hex(data: Byte): String {

        val internal: Int

        if (data < 0)
            internal = 256 + data
        else
            internal = data.toInt()

        var result = Integer.toHexString(internal)
        if (result.length < 2)
            result = 0.toString() + result
        else if (result.length > 2)
            result = result.substring(result.length - 2, result.length)
        return result
    }

    fun bytes2Hex(data: ByteArray, startIndex: Int, length: Int): String {

        val sb = StringBuilder()

        for (m in 0..length - 1)
            sb.append(byte2Hex(data[m + startIndex])).append(" ")

        return sb.toString().trim { it <= ' ' }

    }

    fun bytes2Hex(data: ByteArray): String {

        val sb = StringBuilder(data.size shl 1 + 1)

        for (tmp in data)
            sb.append(byte2Hex(tmp)).append(" ")

        return sb.toString().trim { it <= ' ' }

    }

    fun equals(data1: ByteArray, srcStartIndex: Int, data2: ByteArray, desStartIndex: Int, length: Int): Boolean {
        if (data1.size - srcStartIndex < length || data2.size - desStartIndex < length)
            throw RuntimeException("compare fail while comparing data1 and data2 , from data1 position "
                    + srcStartIndex + " to " + length + srcStartIndex + " , data1 total length +" + data1.size + "\n"
                    + "from data2 position " + desStartIndex + " to " + length + srcStartIndex
                    + " , data2 total length +" + data2.size)

        return (0..length - 1).all { data1[it + srcStartIndex] == data2[it + desStartIndex] }
    }

    fun equals(data: ByteArray, startIndex: Int, length: Int, obj: Int, isLittleEndian: Boolean): Boolean {

        val actullyValue = bytes2Int32(data, startIndex, length, isLittleEndian)

        return actullyValue == obj
    }

    fun byte2Int32(data: Byte): Int {
        return if (data < 0) data + 256 else data.toInt()
    }

    fun bytes2Int32(data: ByteArray, startIndex: Int, length: Int, isLittleEndian: Boolean): Int {

        if (isLittleEndian) {

            var actullyValue = 0

            for (m in 0..length - 1) {
                var wordValue = 0
                if (data[m + startIndex] < 0)
                    wordValue = data[m + startIndex] + 256
                else
                    wordValue = data[m + startIndex].toInt()

                actullyValue = actullyValue or (wordValue shl (m shl 3))
            }
            return actullyValue

        } else {

            var actullyValue = 0

            for (m in 0..length - 1) {
                val wordValue: Int
                if (data[m + startIndex] < 0)
                    wordValue = data[m + startIndex] + 256
                else
                    wordValue = data[m + startIndex].toInt()

                actullyValue = actullyValue shl 8 or wordValue
            }
            return actullyValue
        }
    }

    fun byte2Int16(data: ByteArray): Short {
        return bytes2Int16(data, 0, data.size, true)
    }

    fun bytes2Int16(data: ByteArray, startIndex: Int, length: Int, isLittleEndian: Boolean): Short {

        if (isLittleEndian) {

            var actullyValue: Short = 0

            for (m in 0..length - 1) {
                var wordValue = 0
                if (data[m + startIndex] < 0)
                    wordValue = data[m + startIndex] + 256
                else
                    wordValue = data[m + startIndex].toInt()

                actullyValue = actullyValue or (wordValue shl (m shl 3)).toShort()
            }
            return actullyValue

        } else {

            var actullyValue: Short = 0

            for (m in 0..length - 1) {
                val wordValue: Int
                if (data[m + startIndex] < 0)
                    wordValue = data[m + startIndex] + 256
                else
                    wordValue = data[m + startIndex].toInt()

                actullyValue = (actullyValue.toInt() shl 8 or wordValue).toShort()
            }
            return actullyValue
        }
    }

    fun powerOf2(what: Int): Boolean {
        var what = what
        while (what > 0)
            if (what and 1 == 1)
                return false
            else
                what = what ushr 1
        return true
    }

    fun bytes2Int32(data: ByteArray): Int {
        return bytes2Int32(data, 0, data.size, true)
    }

    fun bytes2Int32(data: ByteArray, isLittleEndian: Boolean): Int {
        return bytes2Int32(data, 0, data.size, isLittleEndian)
    }

    fun bytes2Int32(data: ByteArray, length: Int, isLittleEndian: Boolean): Int {
        return bytes2Int32(data, 0, length, isLittleEndian)
    }

    fun equals(data: ByteArray, startIndex: Int, length: Int, obj: Long, isLittleEndian: Boolean): Boolean {

        val actullyValue = bytes2Int64(data, startIndex, length, isLittleEndian)

        return actullyValue == obj
    }

    @JvmOverloads fun compare(src: ByteArray, obj: ByteArray, isLittleEndian: Boolean = true): Int {
        var src = src
        var obj = obj

        var isExchange = false

        if (src.size < obj.size) {
            val tmp = src
            src = obj
            obj = tmp
            isExchange = true
        }

        if (isLittleEndian) {

            var srcIndex = src.size - 1
            while (srcIndex > obj.size - 1) {
                if (src[srcIndex].toInt() != 0)
                    return if (isExchange) -1 else 1
                srcIndex--
            }

            while (srcIndex > -1) {
                if (src[srcIndex] != obj[srcIndex])
                    return if (byte2Int32(src[srcIndex]) > byte2Int32(obj[srcIndex]))
                        if (isExchange) -1 else 1
                    else
                        if (isExchange) 1 else -1
                srcIndex--
            }

        } else {

            var srcIndex = 0
            while (srcIndex < src.size - obj.size) {
                if (src[srcIndex].toInt() != 0)
                    return if (isExchange) -1 else 1
                srcIndex++
            }

            var objIndex = 0
            while (objIndex < obj.size) {
                if (src[srcIndex] != obj[srcIndex])
                    return if (byte2Int32(src[srcIndex]) > byte2Int32(obj[srcIndex]))
                        if (isExchange) -1 else 1
                    else
                        if (isExchange) 1 else -1
                objIndex++
                srcIndex++
            }

        }

        return 0

    }

    fun bytes2Int64(data: ByteArray, startIndex: Int, length: Int, isLittleEndian: Boolean): Long {

        if (length > 8)
            throw IllegalArgumentException("cann't parse data , because it's too long")

        if (isLittleEndian) {

            var actullyValue: Long = 0

            for (m in 0 until length) {
                var wordValue = 0
                if (data[m + startIndex] < 0)
                    wordValue = data[m + startIndex] + 256
                else
                    wordValue = data[m + startIndex].toInt()

                actullyValue = actullyValue or (wordValue shl (m shl 3)).toLong()
            }
            return actullyValue

        } else {

            var actullyValue = 0

            for (m in 0 until length) {
                val wordValue: Int
                if (data[m + startIndex] < 0)
                    wordValue = data[m + startIndex] + 256
                else
                    wordValue = data[m + startIndex].toInt()

                actullyValue = actullyValue shl 8 or wordValue
            }
            return actullyValue.toLong()
        }

    }

    @JvmOverloads fun bytes2Int64(data: ByteArray, isLittleEndian: Boolean = true): Long {
        return bytes2Int64(data, 0, data.size, isLittleEndian)
    }

    fun bytes2Int64(data: ByteArray, length: Int): Long {
        return bytes2Int64(data, 0, length, true)
    }

    fun bytes2Int64(data: ByteArray, length: Int, isLittleEndian: Boolean): Long {
        return bytes2Int32(data, 0, length, isLittleEndian).toLong()
    }

    fun decHexSizeFormat32(size: ByteArray, isLittleEndian: Boolean): String {
        return bytes2Int32(size, isLittleEndian).toString() + "(0x" + bytes2Hex(size) + ")" + "B"
    }

    fun hexDecSizeFormat32(size: ByteArray, isLittleEndian: Boolean): String {
        return "0x" + bytes2Hex(size) + "(" + bytes2Int32(size, isLittleEndian) + ")" + "B"
    }

    fun assertAlign(align: Int) {
        if (align and 1 == 1)
            throw AssertionError()
    }

    /**
     * @return little endian
     */
    fun int2bytes(`val`: Int): ByteArray {
        var `val` = `val`
        val ret = ByteArray(4)
        var i = 0
        while (i < 4) {
            ret[i] = `val`.toByte()
            i++
            `val` = `val` shr 8
        }
        return ret
    }

    fun short2bytes(`val`: Short): ByteArray {
        var `val`: Int = `val`.toInt()
        val ret = ByteArray(2)
        var i = 0
        while (i < 2) {
            ret[i] = `val`.toByte()
            i++
            `val` = `val` shr 8
        }
        return ret
    }

    @Throws(IOException::class)
    fun getStringFromBytes(raf: RandomAccessFile): String {

        val sb = StringBuilder()

        var read: Int = raf.read()
        while (read > 0) {
            sb.append(read.toChar())
            read = raf.read()
        }

        return sb.toString()
    }
}
