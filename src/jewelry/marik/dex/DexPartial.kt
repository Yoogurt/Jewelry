package jewelry.marik.dex

import jewelry.marik.util.data.BytePtr

internal class DexPartial(val dex: Dex, val begin: BytePtr, val size: Int) {

    internal lateinit var header: DexHeader

    fun parse() {
        header = DexHeader(this)
        header.parse(true)
    }
}