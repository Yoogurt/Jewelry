package jewelry.dex.util.data

import jewelry.dex.main.constant.u1
import jewelry.dex.main.constant.u2
import jewelry.dex.main.constant.u4
import jewelry.dex.main.constant.u8
import jewelry.dex.os.OS

internal class MemoryReader constructor(val source: ByteArray, val startOffset: Int) {
    var offset = 0

    constructor(startOffset: Int) : this(OS.MEMORY, startOffset)

    val u1: u1
        get() {
            return source[startOffset + offset++]
        }

    val u2: u2
        get() {
            try {
                return source.toInt16(startOffset + offset)
            } finally {
                offset += 2
            }
        }

    val u4: u4
        get() {
            try {
                return source.toInt32(startOffset + offset)
            } finally {
                offset += 4
            }
        }

    val u8: u8
        get() {
            try {
                return source.toInt64(startOffset + offset)
            } finally {
                offset += 8
            }
        }

    val uint8_t: u1
        get() = u1

    val uint16_t: u2
        get() = u2

    val uint32_t: u4
        get() = u4

    val uint648_t: u8
        get() = u8

}

fun main(vararg arg: String) {
    for (i in 0 until 20)
        OS.MEMORY[i] = i.toByte()

    var mr = MemoryReader(2)

    println(mr.u2.toByteArray().toHex())
    println(mr.u2.toByteArray().toHex())
}