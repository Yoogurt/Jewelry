package jewelry.dex.main

class DexPartial(val begin: Int, val size: Int) {

    internal lateinit var mHeader: DexHeader

    fun parse() {
        mHeader = DexHeader(this)
        mHeader.parse()
    }
}