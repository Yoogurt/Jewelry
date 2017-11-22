package jewelry.dex.os

import jewelry.dex.util.data.toByteArray
import jewelry.dex.util.data.toHex
import jewelry.dex.util.data.toInt32
import jewelry.dex.util.log.Log
import jewelry.dex.util.memory.PAGE_END
import jewelry.dex.util.memory.PAGE_OFFSET
import jewelry.dex.util.memory.PAGE_SHIFT
import java.io.*

private val debug = false

internal object MemoryMapper {

    /**
     * we mmap a file into byte[]
     */
    fun mmap(start: Int, length: Int, flag: Int, fd: File, offset: Long): Int {
        val raf: InputStream =
                FileInputStream(fd)

        if (PAGE_OFFSET(start.toLong()) > 0 && flag and MAP_FIXED != 0)
            return -1

        try {

            if (flag and MAP_FIXED == 0)
                return mmapNotFix(length, flag, raf, offset)
            else
                return mmapFix(start, length, flag, raf, offset)

        } catch (e: Throwable) {
            throw IllegalStateException()
        } finally {
            raf.close()
        }

    }

    /**
     * we mmap a file into byte[]
     */
    fun mmap(start: Int, length: Int, flag: Int, raf: InputStream, offset: Long): Int {

        if (debug)
            Log.e("mmap(0x" + Integer.toHexString(start) + ",0x" + Integer.toHexString(length) + "," + flag
                    + ",...," + offset + ")")

        if (PAGE_OFFSET(start.toLong()) > 0 && ((flag and MAP_FIXED) != 0))
            return -1

        return if (flag and MAP_FIXED == 0)
            mmapNotFix(length, flag, raf, offset)
        else
            mmapFix(start, length, flag, raf, offset)

    }

    private fun mmapFix(start: Int, length: Int, flag: Int, raf: InputStream, offset: Long): Int {

        if (length < 0)
            throw IllegalArgumentException("length < 0")
        if (PAGE_OFFSET(start.toLong()) != 0.toLong())
            return -1

        val startIndex = (start shr PAGE_SHIFT)
        val endIndex = ((PAGE_END(length.toLong()) shr PAGE_SHIFT) + startIndex).toInt()
        if (debug) {
            Log.e("start $start length $length")
            Log.e("startIndex $startIndex endIndex $endIndex")
        }
        for (i in startIndex until endIndex)
            if (i >= OS.mFlag.size) {
                incMemory(PAGE_END(length.toLong()).toInt())
                return mmapFix(start, length, flag, raf, offset)
            }

        try {
            raf.skip(offset)
            raf.read(OS.MEMORY, start, length)

            val inc_Bit = (PAGE_END(length.toLong()) + start).toInt() shr PAGE_SHIFT
            for (mPtr in (start shr PAGE_SHIFT) until inc_Bit)
                OS.mFlag[mPtr] = flag

        } catch (e: IOException) {
            e.printStackTrace()
            return -1
        }

        return start
    }

    private fun mmapGetFreeAddress(length: Int): Int {

        var blockCount = 0
        var lastSearch = 0
        val test = PAGE_END(length.toLong())
        val needBlockCount = (test shr PAGE_SHIFT).toInt()

        val arrayLength = OS.mFlag.size
        for (i in 0..arrayLength - 1) {
            if (OS.mFlag[i] == -1) {

                if (++blockCount >= needBlockCount) {
                    if (needBlockCount == 1)
                        return i shl PAGE_SHIFT
                    else
                        return lastSearch shl PAGE_SHIFT
                }
                if (lastSearch == -1)
                    lastSearch = i
            } else {
                lastSearch = -1
                blockCount = 0
            }
        }
        return if (blockCount == needBlockCount) lastSearch shl PAGE_SHIFT else -1
    }

    private fun mmapNotFix(length: Int, flag: Int, raf: InputStream, offset: Long): Int {

        var startAddr = mmapGetFreeAddress(length)

        if (startAddr < 0) {
            incMemory(PAGE_END(length.toLong()).toInt())
            startAddr = mmapGetFreeAddress(length)
        }

        if (startAddr < 0)
            throw IllegalArgumentException("startAddress < 0")

        raf.skip(offset)
        raf.read(OS.MEMORY, startAddr, length)

        val inc_Bit = (PAGE_END(length.toLong()) + startAddr).toInt() shr PAGE_SHIFT

        for (mPtr in (startAddr shr PAGE_SHIFT)..inc_Bit - 1)
            OS.mFlag[mPtr] = flag

        return startAddr
    }

    fun unmmap(start: Int, length: Int): Int {

        if (length < 0)
            throw IllegalArgumentException()

        if (PAGE_OFFSET(start.toLong()) > 0)
            return -1

        val blockCount = (PAGE_END(length.toLong()) shr PAGE_SHIFT).toInt()

        val startBlock = start shr PAGE_SHIFT
        var endIndex = startBlock + blockCount

        if (endIndex > OS.mFlag.size)
            endIndex = OS.mFlag.size

        for (i in startBlock until endIndex)
            OS.mFlag[i] = -1

        return 0
    }

    private fun incMemory(size: Int) {
        var size = size
        if (debug)
            Log.e("inc Space : " + size)

        if (PAGE_OFFSET(size.toLong()) > 0)
            throw IllegalArgumentException()

        size += OS.MEMORY.size
        if (size < 0)
            throw OutOfMemoryError()

        val result = ByteArray(size)
        System.arraycopy(OS.MEMORY, 0, result, 0, OS.MEMORY.size)
        val flag = IntArray(size shr PAGE_SHIFT)
        System.arraycopy(OS.mFlag, 0, flag, 0, OS.mFlag.size)

        var start = OS.mFlag.size

        val length = flag.size
        while (start < length) {
            flag[start] = -1
            start++
        }

        OS.MEMORY = result
        OS.mFlag = flag

        if (debug) {
            Log.e("OS.MEMORY = ${OS.MEMORY.size}")
            Log.e("OS.mFlag = ${OS.mFlag.size}")
        }
    }
}