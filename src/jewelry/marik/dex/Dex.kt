package jewelry.marik.dex

import jewelry.marik.os.PROT_NONE
import jewelry.marik.os.mmap
import jewelry.marik.util.data.BytePtr
import jewelry.marik.util.isDexEntry
import jewelry.marik.util.isZipFile
import jewelry.marik.util.log.error
import jewelry.marik.util.log.log
import java.util.*
import java.util.jar.JarFile

const val debug = true

class Dex private constructor(filePath: String) {

    internal val location = filePath

    internal val mDexPartial = LinkedList<DexPartial>()

    private fun parse(): Dex {
        if (mDexPartial.size < 1)
            "no dex found in ${location}".error()
        mDexPartial.forEach {
            it.parse()
        }
        return this
    }

    companion object {
        private val sOpenDexes: MutableList<Dex> = LinkedList<Dex>()

        @Synchronized
        fun open(filePath: String): Dex {
            return if (isZipFile(filePath)) {
                openZip(filePath)
            } else {
                openFile(filePath)
            }
        }

        private fun openZip(filePath: String): Dex {
            val dex = Dex(filePath)
            val jarFile = JarFile(filePath)
            jarFile.entries().iterator().forEach {
                if (isDexEntry(it)) {
                    if (it.size > Int.MAX_VALUE)
                        throw DexException("${it.name} is larger than 2G")

                    val mmapAddress = mmap(0, it.size.toInt(), PROT_NONE, jarFile.getInputStream(it), 0)

                    dex.mDexPartial.add(DexPartial(dex, BytePtr(mmapAddress), it.size.toInt()))

                    if (debug)
                        "${it.name} mmap at $mmapAddress".log()
                }
            }
            sOpenDexes.add(dex)
            return dex.parse()
        }

        private fun openFile(filePath: String): Dex {
            try {
                var dex = Dex(filePath)
                sOpenDexes.add(dex)
                return dex.parse()
            } catch (e: java.io.IOException) {
                throw DexException(e)
            }
        }
    }

}

fun main(vararg arg: String) {
    val dex = Dex.open("/home/marik/Desktop/base.apk")
}