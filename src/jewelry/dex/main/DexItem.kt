package jewelry.dex.main

import jewelry.dex.main.constant.uint16_t
import jewelry.dex.main.constant.uint32_t
import jewelry.dex.util.log.error

const val kMapItemSize = 12
const val kMapListSize = 16

data class MapItem(val type: uint16_t, val unused: uint16_t, val size: uint32_t, val offset: uint32_t)
data class MapList(val size: uint32_t, val list: Array<MapItem>) {
    init {
        if (list.size > 1)
            "MapList require only one MapItem".error()
    }
}

fun main(vararg arg: String) {

}