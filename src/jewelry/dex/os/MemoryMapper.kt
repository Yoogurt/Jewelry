package jewelry.dex.os

import brilliant.elf.util.Log
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile

internal object MemoryMapper {

    /**
     * we mmap a file into byte[]
     */
    fun mmap(start: Int, length: Int, flag: Int, fd: File?, offset: Long, os: OS): Int {
        var raf: RandomAccessFile? = null
        try {
            raf = RandomAccessFile(fd!!, "r")
        } catch (e: Exception) {
            e.printStackTrace()
            if (fd != null)
                return -1
        }

        if (OS.PAGE_OFFSET(start.toLong()) > 0 && flag and OS.MAP_FIXED != 0)
            return -1

        try {

            if (flag and OS.MAP_FIXED == 0)
                return mmapNotFix(length, flag, raf, offset, os)
            else
                return mmapFix(start, length, flag, raf, offset, os)

        } catch (e: Throwable) {
            throw IllegalStateException()
        } finally {
            if (raf != null)
                try {
                    raf.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

        }

    }

    /**
     * we mmap a file into byte[]
     */
    fun mmap(start: Int, length: Int, flag: Int, raf: RandomAccessFile?, offset: Long, os: OS): Int {

        if (OS.debug)
            Log.e("mmap(0x" + Integer.toHexString(start) + ",0x" + Integer.toHexString(length) + "," + flag
                    + ",...," + offset + ")")

        if (OS.PAGE_OFFSET(start.toLong()) > 0 && ((flag and OS.MAP_FIXED) != 0))
            return -1

        if (flag and OS.MAP_FIXED == 0)
            return mmapNotFix(length, flag, raf, offset, os)
        else
            return mmapFix(start, length, flag, raf, offset, os)

    }

    private fun mmapFix(start: Int, length: Int, flag: Int, raf: RandomAccessFile?, offset: Long, os: OS): Int {

        if (length < 0)
            throw IllegalArgumentException("length < 0")
        if (OS.PAGE_OFFSET(start.toLong()) != 0.toLong())
            return -1

        val startIndex = (start shr OS.PAGE_SHIFT)
        val endIndex = ((OS.PAGE_END(length.toLong()) shr OS.PAGE_SHIFT) + startIndex).toInt()
        if (OS.debug) {
            Log.e("start $start length $length")
            Log.e("startIndex $startIndex endIndex $endIndex")
        }
        for (i in startIndex..endIndex - 1)
            if (i >= os.mFlag.size) {
                incMemory(OS.PAGE_END(length.toLong()).toInt(), os)
                return mmapFix(start, length, flag, raf, offset, os)
            }

        if (raf != null)
            try {
                raf.seek(offset)
                raf.read(os.memory, start, length)

                val inc_Bit = (OS.PAGE_END(length.toLong()) + start).toInt() shr OS.PAGE_SHIFT
                for (mPtr in (start shr OS.PAGE_SHIFT)..inc_Bit - 1)
                    os.mFlag[mPtr] = flag

            } catch (e: IOException) {
                e.printStackTrace()
                return -1
            }

        return start
    }

    private fun mmapGetFreeAddress(length: Int, os: OS): Int {

        var blockCount = 0
        var lastSearch = 0
        val needBlockCount = (OS.PAGE_END(length.toLong()) shr OS.PAGE_SHIFT).toInt()

        val arrayLength = os.mFlag.size
        for (i in 0..arrayLength - 1) {
            if (os.mFlag[i] == -1) {

                if (++blockCount >= needBlockCount) {
                    if (needBlockCount == 1)
                        return i shl OS.PAGE_SHIFT
                    else
                        return lastSearch shl OS.PAGE_SHIFT
                }
                if (lastSearch == -1)
                    lastSearch = i
            } else {
                lastSearch = -1
                blockCount = 0
            }
        }
        return if (blockCount == needBlockCount) lastSearch shl OS.PAGE_SHIFT else -1
    }

    private fun mmapNotFix(length: Int, flag: Int, raf: RandomAccessFile?, offset: Long, os: OS): Int {

        var startAddr = mmapGetFreeAddress(length, os)

        if (startAddr < 0) {
            incMemory(OS.PAGE_END(length.toLong()).toInt(), os)
            startAddr = mmapGetFreeAddress(length, os)
        }

        if (startAddr < 0)
            throw IllegalArgumentException("startAddress < 0")

        if (raf != null)
            try {
                raf.seek(offset)
                raf.read(os.memory, startAddr, length)
            } catch (e: Exception) {
                e.printStackTrace()
                return -1
            }

        val inc_Bit = (OS.PAGE_END(length.toLong()) + startAddr).toInt() shr OS.PAGE_SHIFT

        for (mPtr in (startAddr shr OS.PAGE_SHIFT)..inc_Bit - 1)
            os.mFlag[mPtr] = flag

        return startAddr
    }

    fun unmmap(start: Int, length: Int, os: OS): Int {

        if (length < 0)
            throw IllegalArgumentException()

        if (OS.PAGE_OFFSET(start.toLong()) > 0)
            return -1

        val blockCount = (OS.PAGE_END(length.toLong()) shr OS.PAGE_SHIFT).toInt()

        val startBlock = start shr OS.PAGE_SHIFT
        var endIndex = startBlock + blockCount

        if (endIndex > os.mFlag.size)
            endIndex = os.mFlag.size

        for (i in startBlock..endIndex - 1)
            os.mFlag[i] = -1

        return 0
    }

    private fun incMemory(size: Int, os: OS) {
        var size = size
        if (OS.debug)
            Log.e("inc Space : " + size)

        if (OS.PAGE_OFFSET(size.toLong()) > 0)
            throw IllegalArgumentException()

        size += os.memory.size
        if (size < 0)
            throw OutOfMemoryError()

        val result = ByteArray(size)
        System.arraycopy(os.memory, 0, result, 0, os.memory.size)
        val flag = IntArray(size shr OS.PAGE_SHIFT)
        System.arraycopy(os.mFlag, 0, flag, 0, os.mFlag.size)

        var start = os.mFlag.size

        val length = flag.size
        while (start < length) {
            flag[start] = -1
            start++
        }

        os.memory = result
        os.mFlag = flag

        if (OS.debug) {
            Log.e("os.memory = ${os.memory.size}")
            Log.e("os.mFlag = ${os.mFlag.size}")
        }

    }
}
