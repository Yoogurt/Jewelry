package jewelry.dex.os

import jewelry.dex.util.data.toHex
import jewelry.dex.util.memory.PAGE_SIZE
import java.io.InputStream
import java.io.PrintStream

@Deprecated("not implements")
const val PROT_EXEC: Int = 4// not implements
@Deprecated("not implements")
const val PROT_READ: Int = 1// not implements
@Deprecated("not implements")
const val PROT_WRITE: Int = 2// not implements

const val PROT_NONE: Int = 0

const val MAP_FIXED: Int = 16
@Deprecated("")
const val MAP_ANONYMOUS: Int = 32 // not implements , mmap flag
// will carry this flag
// automatic

fun mmap(start: Int, length: Int, flags: Int, fd: InputStream, offset: Long): Int {
    return MemoryMapper.mmap(start, length, (flags or MAP_ANONYMOUS), fd, offset)
}

fun reset() {
    OS.MEMORY = ByteArray(PAGE_SIZE)
    OS.mFlag = intArrayOf(-1)

    System.gc() // collect garbage if necessary
}

fun unmmap(start: Int, size: Int): Int {
    return MemoryMapper.unmmap(start, size)
}

fun dumpMemory(out: PrintStream = System.out, startIndex: Int = 0, endIndex: Int = OS.MEMORY.size) {

    val startIndex = startIndex and (-1 xor 0x10)

    for (i in startIndex until endIndex) {
        if (i % 16 == 0) {
            out.printf("\n%5x : ", i)
        } else if (i % 8 == 0)
            out.print("   ")
        out.print("${OS.MEMORY[i].toHex()} ")
    }
    out.println()
    out.flush()
}

object OS {
    /**
     * don't pull it out , because it will change the reference while running
     * out of space
     */
    var MEMORY = ByteArray(PAGE_SIZE)
    /**
     * don't pull it out , because it will change the reference while running
     * out of space
     */
    var mFlag = intArrayOf(-1)
}