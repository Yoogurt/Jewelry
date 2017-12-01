package jewelry.marik.util.data

import jewelry.dex.util.data.*
import jewelry.marik.os.OS
import jewelry.marik.dex.constant.alais.*
import java.util.*

internal class MemoryReader constructor(val source: ByteArray, val startOffset: Int) {
    private val mStack = Stack<uint32_t>()

    private var offset = 0

    constructor(startOffset: Int) : this(OS.MEMORY, startOffset)

    inline val u1: u1
        get() = source[startOffset + offset++]

    inline val u2: u2
        get() {
            try {
                return source.toInt16(startOffset + offset)
            } finally {
                offset += 2
            }
        }

    inline val u4: u4
        get() {
            try {
                return source.toUInt32(startOffset + offset)
            } finally {
                offset += 4
            }
        }

    inline val u8: u8
        get() {
            try {
                return source.toUInt64(startOffset + offset)
            } finally {
                offset += 8
            }
        }

    inline val uint8_t: u1
        get() = u1

    inline val uint16_t: u2
        get() = u2

    inline val uint32_t: u4
        get() = u4

    inline val uint64_t: u8
        get() = u8

    fun copyTo(target: ByteArray, startIndex: Int = 0, length: Int = target.size) =
            (startIndex until startIndex + length).forEach { target[it] = u1 }

    @Synchronized
    fun save(new: uint32_t): MemoryReader {
        mStack.push(offset)
        offset = new
        return this
    }

    @Synchronized
    fun restore(): MemoryReader {
        offset = mStack.pop()
        return this
    }
}

fun main(vararg arg: String) {
    for (i in 0 until 20)
        OS.MEMORY[i] = i.toByte()

    var mr = MemoryReader(2)

    println(mr.u2.toByteArray().toHex())
    println(mr.u2.toByteArray().toHex())
}