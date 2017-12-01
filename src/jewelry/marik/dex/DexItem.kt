package jewelry.marik.dex

import jewelry.dex.main.constant.u4
import jewelry.dex.main.constant.uint16_t
import jewelry.dex.main.constant.uint32_t
import src.jewelry.marik.dex.constant.kAccInterface
import src.jewelry.marik.dex.constant.kAccValidClassFlags
import src.jewelry.marik.dex.constant.kAccValidInterfaceFlags

internal data class MapItem(val type: uint16_t, val unused: uint16_t, val size: uint32_t, val offset: uint32_t) {
    companion object {
        const val size: u4 = 12
        fun create(buffer: jewelry.dex.util.data.MemoryReader) = MapItem(buffer.uint16_t, buffer.uint16_t, buffer.uint32_t, buffer.uint32_t)
    }
}

internal data class MapList(val size: uint32_t, val list: Array<MapItem>) {
    companion object {
        fun create(buffer: jewelry.dex.util.data.MemoryReader): MapList {
            val size = buffer.uint32_t
            return MapList(size, Array(size) {
                return@Array MapItem.create(buffer)
            })
        }
    }
}

internal data class StringId(val string_data_off: uint32_t) {
    companion object {
        const val size = 4

        fun create(buffer: jewelry.dex.util.data.MemoryReader) = StringId(buffer.uint32_t)
    }
}

internal data class TypeId(val descriptor_idx: uint32_t) {
    companion object {
        const val size = 4

        fun create(buffer: jewelry.dex.util.data.MemoryReader) = TypeId(buffer.uint32_t)
    }
}

internal data class FieldId(val class_idx: uint16_t, val type_idx: uint16_t, val name_idx: uint32_t) {
    companion object {
        const val size = 8

        fun create(buffer: jewelry.dex.util.data.MemoryReader) = FieldId(buffer.uint16_t, buffer.uint16_t, buffer.uint32_t)
    }
}

internal data class MethodId(val class_idx: uint16_t, val proto_idx: uint16_t, val name_idx: uint32_t) {
    companion object {
        const val size = 8

        fun create(buffer: jewelry.dex.util.data.MemoryReader) = MethodId(buffer.uint16_t, buffer.uint16_t, buffer.uint32_t)
    }
}

internal data class ProtoId(val shorty_idx: uint32_t, val return_type_idx: uint16_t, val pad: uint16_t, val parameters_off: uint32_t) {
    companion object {
        const val size = 12

        fun create(buffer: jewelry.dex.util.data.MemoryReader) = ProtoId(buffer.uint32_t, buffer.uint16_t, buffer.uint16_t, buffer.uint32_t)
    }
}

internal data class ClassDef(val class_idx: uint16_t, val pad1: uint16_t, val access_flag: uint32_t, val superclass_idx: uint16_t, val pad2: uint16_t, val interfaces_off: uint32_t, val source_file_idx: uint32_t, val annotations_off: uint32_t, val class_data_off: uint32_t, val static_values_off: uint32_t) {

    val javaAccessFlag: uint32_t
        get() = if ((access_flag and kAccInterface) != 0)
            access_flag and kAccValidInterfaceFlags
        else
            access_flag and kAccValidClassFlags

    companion object {
        const val size = 32

        fun create(buffer: jewelry.dex.util.data.MemoryReader) = ClassDef(buffer.uint16_t, buffer.uint16_t, buffer.uint32_t, buffer.uint16_t, buffer.uint16_t, buffer.uint32_t, buffer.uint32_t, buffer.uint32_t, buffer.uint32_t, buffer.uint32_t)
    }
}

internal data class TypeItem(val type_idx: uint16_t) {
    companion object {
        const val size = 2

        fun create(buffer: jewelry.dex.util.data.MemoryReader) = TypeItem(buffer.uint16_t)
    }
}

internal data class CodeItem(val registers_size: uint16_t, val ins_size: uint16_t, val out_size: uint16_t, val tries_size: uint16_t, val debug_info_off: uint32_t, val insns_size_in_code_uints: uint32_t, val insns: Array<uint16_t>) {
    companion object {

    }
}

internal data class TryItem(val start_addr: uint32_t, val insn_count: uint16_t, val handler_off: uint16_t) {
    companion object {
        const val size = 8

        fun create(buffer: jewelry.dex.util.data.MemoryReader) = TryItem(buffer.uint32_t, buffer.uint16_t, buffer.uint16_t)
    }
}

internal data class AnnotationsDirectoryItem(val class_annotations_off: uint32_t, val fields_size: uint32_t, val methods_size: uint32_t, val parameters_size: uint32_t) {
    companion object {
        const val size = 16

        fun create(buffer: jewelry.dex.util.data.MemoryReader) = AnnotationsDirectoryItem(buffer.uint32_t, buffer.uint32_t, buffer.uint32_t, buffer.uint32_t)
    }
}

internal data class FieldAnnotationsItem(val field_idx: uint32_t, val annotations_off: uint32_t) {
    companion object {
        const val size = 8

        fun create(buffer: jewelry.dex.util.data.MemoryReader) = FieldAnnotationsItem(buffer.uint32_t, buffer.uint32_t)
    }
}

internal data class MethodAnnotationsItem(val method_idx: uint32_t, val annotations_off: uint32_t) {
    companion object {
        const val size = 8

        fun create(buffer: jewelry.dex.util.data.MemoryReader) = MethodAnnotationsItem(buffer.uint32_t, buffer.uint32_t)
    }
}

internal data class ParameterAnnotationsItem(val method_idx: uint32_t, val annotations_off: uint32_t) {
    companion object {
        const val size = 8

        fun create(buffer: jewelry.dex.util.data.MemoryReader) = ParameterAnnotationsItem(buffer.uint32_t, buffer.uint32_t)
    }
}

internal data class AnnotationSetRefItem(val annotations_off: uint32_t) {
    companion object {
        const val size = 4

        fun create(buffer: jewelry.dex.util.data.MemoryReader) = AnnotationSetRefItem(buffer.uint32_t)
    }
}