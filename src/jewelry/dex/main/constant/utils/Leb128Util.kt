package jewelry.dex.main.constant.utils

import jewelry.dex.main.constant.uint32_t
import jewelry.dex.os.OS

inline fun DecodeUnsignedLeb128(ptr: uint32_t, buffer: ByteArray = OS.MEMORY): uint32_t {
    var ptr = ptr
    var result: Int = buffer[ptr++].toInt()
    if (result > 0x7f) {
        val cur = buffer[ptr++].toInt()
        result = (result and 0x7f) or ((cur and 0x7f) shl 7)
    }
    return result
}