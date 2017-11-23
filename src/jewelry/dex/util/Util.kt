package jewelry.dex.util

import jewelry.dex.main.constant.dex.DexConstant
import jewelry.dex.main.constant.u4
import jewelry.dex.os.OS
import java.util.jar.JarEntry
import java.util.jar.JarFile

fun isZipFile(file: String): Boolean {
    return try {
        0 < JarFile(file).size()
    } catch (e: Throwable) {
        false
    }
}

fun isDexEntry(entry: JarEntry): Boolean {
    return !entry.isDirectory && entry.name.startsWith(DexConstant.kClassesEntryStart) && entry.name.endsWith(DexConstant.kClassesEntryEnd)
}

interface Offsetor<T> {

    val size: u4

    operator fun get(offset: u4): T
    operator fun set(index: u4, value: T)

    operator fun iterator(): Iterator<T>
}

class OffsetorProvider<T>(val parent: Offsetor<T>, val start: u4) : Offsetor<T> {
    override val size = parent.size - start

    init {
        if (start < 0)
            throw ArrayIndexOutOfBoundsException("start should be positive")
    }

    override fun get(offset: u4): T {
        return parent[this.start + offset]
    }

    override fun set(index: u4, value: T) {
        parent[this.start + index] = value
    }

    override fun iterator(): Iterator<T> {
        return object : Iterator<T> {
            var offset = 0
            override fun next(): T {
                return parent[this.offset++ + this@OffsetorProvider.start]
            }

            override fun hasNext(): Boolean {
                return offset < size
            }
        }
    }

}

class ByteArrayOffsetor(val parent: ByteArray, val start: u4) : Offsetor<Byte> {

    override val size = parent.size - start

    init {
        if (start < 0)
            throw ArrayIndexOutOfBoundsException("start should be positive")
    }

    override operator fun get(offset: u4): Byte {
        return parent[this.start + offset]
    }

    override operator fun set(index: u4, value: Byte) {
        parent[this.start + index] = value
    }

    override operator fun iterator(): Iterator<Byte> {
        return object : ByteIterator() {
            var offset = 0

            override fun hasNext(): Boolean {
                return offset < size
            }

            override fun nextByte(): Byte {
                return parent[this.offset++ + this@ByteArrayOffsetor.start]
            }
        }
    }
}

fun reinterpret_cast(source: ByteArray, start: u4): ByteArrayOffsetor {
    return ByteArrayOffsetor(source, start)
}

fun reinterpret_cast(start: u4): ByteArrayOffsetor {
    return reinterpret_cast(OS.MEMORY, start)
}

fun main(vararg arg: String) {
    OS.MEMORY[3] = 12
    OS.MEMORY[4] = 13
    OS.MEMORY[5] = 14

    var it = reinterpret_cast(1)
    var it2 = OffsetorProvider<Byte>(it, 2).iterator()
    for (i in 0 until 8)
        println(it2.next())
}
