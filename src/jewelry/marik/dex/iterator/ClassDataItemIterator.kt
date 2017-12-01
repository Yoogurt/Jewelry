package jewelry.dex.main.constant

import jewelry.dex.main.constant.utils.DecodeUnsignedLeb128
import jewelry.dex.util.CHECK
import jewelry.dex.util.log.warning
import jewelry.dex.util.nullptr
import src.jewelry.marik.dex.Dex
import src.jewelry.marik.dex.constant.kAccFinal
import src.jewelry.marik.dex.constant.kAccNative
import src.jewelry.marik.dex.constant.kAccValidFieldFlags
import src.jewelry.marik.dex.constant.kAccValidMethodFlags

internal open class ClassDataItemIterator(val dexFile: Dex, var ptr_pos: uint32_t) {

    internal var pos: uint32_t = 0
    internal var last_idx: uint32_t = 0
    private val header = ClassDataHeader()
    private val field = ClassDataField()
    private val method = ClassDataMethod()

    val numStaticFields
        get() = header.static_fields_size

    val numInstanceFields
        get() = header.instance_fields_size

    val numDirectMethods
        get() = header.direct_method_size

    val numVirtualMethods
        get() = header.viretual_method_size

    val hasNextStaticField
        get() = pos < endOfStaticFieldsPos() //it's in order

    val hasNextInstanceField
        get() = (pos >= endOfStaticFieldsPos()) and (pos < endOfDirectMethodsPos())

    val hasNextDirectMethod
        get() = (pos >= endOfInstanceFieldsPos()) and (pos < endOfDirectMethodsPos())

    val hasNextVirtualMethod
        get() = (pos >= endOfDirectMethodsPos()) and (pos < endOfVirtualMethodPos())

    val hasNext
        get() = pos < endOfVirtualMethodPos()

    val memberIndex: uint32_t
        get() {
            if (pos < endOfInstanceFieldsPos())
                return last_idx + this.field.field_idx_delta
            else {
                return last_idx + method.method_idx_delta
            }
        }

    val rawMemberAccessFlags: uint32_t
        get() {
            if (pos < endOfInstanceFieldsPos())
                return this.field.access_flags
            else
                return method.access_flags
        }

    val fieldAccessFlags
        get() = rawMemberAccessFlags and kAccValidFieldFlags

    val methodAccessFlags
        get() = rawMemberAccessFlags and kAccValidMethodFlags

    val memberIsNative: Boolean
        get() = rawMemberAccessFlags and kAccNative > 0

    val memberIsFinal: Boolean
        get() = rawMemberAccessFlags and kAccFinal > 0

    val next: Unit
        get() {
            pos++
            if (pos < endOfStaticFieldsPos()) {
                last_idx = memberIndex
                readClassDataField()
            } else if ((pos == endOfStaticFieldsPos()) and (numInstanceFields > 0)) {
                last_idx = 0
                readClassDataField()
            } else if (pos < endOfInstanceFieldsPos()) {
                last_idx = memberIndex
                readClassDataField()
            } else if ((pos == endOfInstanceFieldsPos()) and (numDirectMethods > 0)) {
                last_idx = 0
                readClassDataMethod()
            } else if (pos < endOfDirectMethodsPos()) {
                last_idx = memberIndex
                readClassDataMethod()
            } else if ((pos == endOfDirectMethodsPos()) and (numVirtualMethods > 0)) {
                last_idx = 0
                readClassDataMethod()
            } else if (pos < endOfVirtualMethodPos()) {
                last_idx = memberIndex
                readClassDataMethod()
            } else {
                CHECK(hasNext)
            }
        }

    init {
        readClassDataHeader()
        if (endOfInstanceFieldsPos() > 0) {
            readClassDataField()
        } else if (endOfVirtualMethodPos() > 0) {
            readClassDataMethod()
        }
    }

    private fun readClassDataHeader() {
        CHECK(ptr_pos != nullptr)
        header.static_fields_size = nextUnsignedLeb128()
        header.instance_fields_size = nextUnsignedLeb128()
        header.direct_method_size = nextUnsignedLeb128()
        header.viretual_method_size = nextUnsignedLeb128()
    }

    private fun readClassDataField() {
        field.field_idx_delta = nextUnsignedLeb128()
        field.access_flags = nextUnsignedLeb128()
        if ((last_idx != 0) and (field.field_idx_delta == 0))
            "Duplicate field in ${dexFile.location}".warning()
    }

    private fun readClassDataMethod() {
        method.method_idx_delta = nextUnsignedLeb128()
        method.access_flags = nextUnsignedLeb128()
        method.code_off = nextUnsignedLeb128()
        if ((last_idx != 0) and (method.method_idx_delta == 0))
            "Duplicate method in ${dexFile.location}".warning()
    }

    private fun endOfStaticFieldsPos(): uint32_t {
        return 0
    }

    private fun endOfInstanceFieldsPos(): uint32_t {
        return 0
    }

    private fun endOfDirectMethodsPos(): uint32_t {
        return 0
    }

    private fun endOfVirtualMethodPos(): uint32_t {
        return 0
    }

    private fun nextUnsignedLeb128(): uint32_t {
        val ret = DecodeUnsignedLeb128(ptr_pos)
        ptr_pos = ret.ptr
        return ret.ret
    }

    companion object {
        private class ClassDataHeader {
            var static_fields_size: uint32_t = 0
            var instance_fields_size: uint32_t = 0
            var direct_method_size: uint32_t = 0
            var viretual_method_size: uint32_t = 0
        }

        private class ClassDataField {
            var field_idx_delta: uint32_t = 0
            var access_flags: uint32_t = 0
        }

        private class ClassDataMethod {
            var method_idx_delta: uint32_t = 0
            var access_flags: uint32_t = 0
            var code_off: uint32_t = 0
        }
    }

}