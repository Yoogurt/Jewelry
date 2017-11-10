package jewelry.dex.os

import jewelry.dex.util.ByteUtil
import java.io.PrintStream
import java.io.RandomAccessFile

class OS private constructor() {
    /**
     * don't pull it out , because it will change the reference while running
     * out space
     */
    var memory = ByteArray(PAGE_SIZE)
    /**
     * don't pull it out , because it will change the reference while running
     * out space
     */
    var mFlag = intArrayOf(-1)

    fun mmap(start: Int, length: Int, flags: Int, fd: RandomAccessFile?, offset: Long): Int {
        return MemoryMapper.mmap(start, length, (flags or MAP_ANONYMOUS), fd, offset, this)
    }

    fun reset() {
        memory = ByteArray(PAGE_SIZE)
        mFlag = intArrayOf(-1)

        System.gc() // collect garbage if necessary
    }

    fun unmmap(start: Int, size: Int): Int {
        return MemoryMapper.unmmap(start, size, this)
    }

    @JvmOverloads fun dumpMemory(out: PrintStream = System.out, startIndex: Int = 0, endIndex: Int = memory.size) {

        var line = startIndex shr 4
        out.printf("%5s : ", line)

        for (i in startIndex % 16 downTo 1)
            out.print("    ")

        for (i in startIndex..endIndex - 1) {
            out.print(ByteUtil.byte2Hex(memory[i]) + " ")
            if (++line % 16 == 0) {
                out.println()
                out.printf("%5x : ", line)
            } else if (line % 8 == 0)
                out.print("  ")
        }
        out.println()
        out.flush()
    }

    companion object {

        val mainImage = OS()

        fun newImage(): OS {
            return OS()
        }

        var debug = true

        @Deprecated("not implements")
        val PROT_EXEC: Int = 4// not implements
        @Deprecated("not implements")
        val PROT_READ: Int = 1// not implements
        @Deprecated("not implements")
        val PROT_WRITE: Int = 2// not implements
        @Deprecated("not implements")
        val PROT_NONE: Int = 0// not implements

        val MAP_FIXED: Int = 16
        @Deprecated("")
        val MAP_ANONYMOUS: Int = 32 // not implements , mmap flag
        // will carry this flag
        // automatic

        val PAGE_MASK = (4096 - 1).inv().toLong()
        val PAGE_SIZE = 4096
        val PAGE_SHIFT = 12

        fun PAGE_START(`val`: Long): Long {
            return `val` and PAGE_MASK
        }

        fun PAGE_OFFSET(`val`: Long): Long {
            return `val` and PAGE_MASK.inv()
        }

        fun PAGE_END(`val`: Long): Long {
            return PAGE_START(`val` + (PAGE_SIZE - 1))
        }

        @JvmStatic fun main(args: Array<String>) {
            println(PAGE_END(0X8EC8))
        }
    }

}
