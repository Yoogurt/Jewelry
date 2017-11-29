package jewelry.dex.main.constant

import jewelry.dex.main.constant.dex.DexFile
import jewelry.dex.util.data.MemoryReader

internal open class ClassDataItemIterator(val dexFile: DexFile, val ptr_pos: uint32_t) {

    internal var pos: uint32_t = 0
    internal var last_idx: uint32_t = 0
    private var header: ClassDataHeader = ClassDataHeader()

    init {
        readClassDataHeader()
        if (endOfInstanceFieldsPos() > 0) {
            readClassDataField()
        } else if (endOfVIrtualMethodPos() > 0) {
            readClassDataMethod()
        }
    }

    private fun readClassDataHeader() {

    }

    private fun readClassDataField() {

    }

    private fun readClassDataMethod() {


    }

    private fun endOfInstanceFieldsPos(): Int {
        return 0
    }

    private fun endOfVIrtualMethodPos(): Int {
        return 0
    }

    companion object {
        private class ClassDataHeader {
            var static_fields_size: uint32_t = 0
            var instance_fields_size: uint32_t = 0
            var direct_method_size: uint32_t = 0
            var viretual_method_size: uint32_t = 0
        }
    }

}