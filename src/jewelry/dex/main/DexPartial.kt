package jewelry.dex.main

class DexPartial(val dex: Dex, val begin: Int, val size: Int) {

    internal lateinit var header: DexHeader

    fun parse() {
        header = DexHeader(this)
        header.parse()
    }
}