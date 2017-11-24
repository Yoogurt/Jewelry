package jewelry.dex.main

import jewelry.dex.main.constant.u4
import jewelry.dex.main.constant.uint16_t
import jewelry.dex.main.constant.uint32_t
import jewelry.dex.os.OS
import jewelry.dex.util.data.MemoryReader
import jewelry.dex.util.data.toByteArray
import jewelry.dex.util.data.toHex
import jewelry.dex.util.log.error

const val kMapItemSize = 12
const val kMapListSize = 16

internal data class MapItem(val type: uint16_t, val unused: uint16_t, val size: uint32_t, val offset: uint32_t) {
    companion object {
        fun create(buffer: MemoryReader): MapItem = MapItem(buffer.uint16_t, buffer.uint16_t, buffer.uint32_t, buffer.uint32_t)
    }
}

internal data class MapList(val size: uint32_t, val list: Array<MapItem>) {
    companion object {
        fun create(buffer: MemoryReader): MapList {
            val size = buffer.uint32_t
            return MapList(size, Array(size) {
                return@Array MapItem.create(buffer)
            })
        }
    }
}
