package src.jewelry.marik.dex

import src.jewelry.marik.dex.constant.kAccInterface
import src.jewelry.marik.dex.constant.kAccValidClassFlags
import src.jewelry.marik.dex.constant.kAccValidInterfaceFlags

internal data class MapItem(val type: jewelry.dex.main.constant.uint16_t, val unused: jewelry.dex.main.constant.uint16_t, val size: jewelry.dex.main.constant.uint32_t, val offset: jewelry.dex.main.constant.uint32_t) {
    companion object {
        const val size: jewelry.dex.main.constant.u4 = 12
        fun create(buffer: jewelry.dex.util.data.MemoryReader) = src.jewelry.marik.dex.MapItem(buffer.uint16_t, buffer.uint16_t, buffer.uint32_t, buffer.uint32_t)
    }
}

internal data class MapList(val size: jewelry.dex.main.constant.uint32_t, val list: Array<src.jewelry.marik.dex.MapItem>) {
    companion object {
        fun create(buffer: jewelry.dex.util.data.MemoryReader): src.jewelry.marik.dex.MapList {
            val size = buffer.uint32_t
            return src.jewelry.marik.dex.MapList(size, Array(size) {
                return@Array MapItem.create(buffer)
            })
        }
    }
}

internal data class StringId(val string_data_off: jewelry.dex.main.constant.uint32_t) {
    companion object {
        const val size = 4

        fun create(buffer: jewelry.dex.util.data.MemoryReader) = src.jewelry.marik.dex.StringId(buffer.uint32_t)
    }
}

internal data class TypeId(val descriptor_idx: jewelry.dex.main.constant.uint32_t) {
    companion object {
        const val size = 4

        fun create(buffer: jewelry.dex.util.data.MemoryReader) = src.jewelry.marik.dex.TypeId(buffer.uint32_t)
    }
}

internal data class FieldId(val class_idx: jewelry.dex.main.constant.uint16_t, val type_idx: jewelry.dex.main.constant.uint16_t, val name_idx: jewelry.dex.main.constant.uint32_t) {
    companion object {
        const val size = 8

        fun create(buffer: jewelry.dex.util.data.MemoryReader) = src.jewelry.marik.dex.FieldId(buffer.uint16_t, buffer.uint16_t, buffer.uint32_t)
    }
}

internal data class MethodId(val class_idx: jewelry.dex.main.constant.uint16_t, val proto_idx: jewelry.dex.main.constant.uint16_t, val name_idx: jewelry.dex.main.constant.uint32_t) {
    companion object {
        const val size = 8

        fun create(buffer: jewelry.dex.util.data.MemoryReader) = src.jewelry.marik.dex.MethodId(buffer.uint16_t, buffer.uint16_t, buffer.uint32_t)
    }
}

internal data class ProtoId(val shorty_idx: jewelry.dex.main.constant.uint32_t, val return_type_idx: jewelry.dex.main.constant.uint16_t, val pad: jewelry.dex.main.constant.uint16_t, val parameters_off: jewelry.dex.main.constant.uint32_t) {
    companion object {
        const val size = 12

        fun create(buffer: jewelry.dex.util.data.MemoryReader) = src.jewelry.marik.dex.ProtoId(buffer.uint32_t, buffer.uint16_t, buffer.uint16_t, buffer.uint32_t)
    }
}

internal data class ClassDef(val class_idx: jewelry.dex.main.constant.uint16_t, val pad1: jewelry.dex.main.constant.uint16_t, val access_flag: jewelry.dex.main.constant.uint32_t, val superclass_idx: jewelry.dex.main.constant.uint16_t, val pad2: jewelry.dex.main.constant.uint16_t, val interfaces_off: jewelry.dex.main.constant.uint32_t, val source_file_idx: jewelry.dex.main.constant.uint32_t, val annotations_off: jewelry.dex.main.constant.uint32_t, val class_data_off: jewelry.dex.main.constant.uint32_t, val static_values_off: jewelry.dex.main.constant.uint32_t) {

    val javaAccessFlag: jewelry.dex.main.constant.uint32_t
        get() = if ((access_flag and kAccInterface) != 0)
            access_flag and kAccValidInterfaceFlags
        else
            access_flag and kAccValidClassFlags

    companion object {
        const val size = 36

        fun create(buffer: jewelry.dex.util.data.MemoryReader) = src.jewelry.marik.dex.ClassDef(buffer.uint16_t, buffer.uint16_t, buffer.uint32_t, buffer.uint16_t, buffer.uint16_t, buffer.uint32_t, buffer.uint32_t, buffer.uint32_t, buffer.uint32_t, buffer.uint32_t)
    }
}

internal data class TypeItem(val type_idx: jewelry.dex.main.constant.uint16_t) {
    companion object {
        const val size = 2

        fun create(buffer: jewelry.dex.util.data.MemoryReader) = src.jewelry.marik.dex.TypeItem(buffer.uint16_t)
    }
}

internal data class CodeItem(val registers_size: jewelry.dex.main.constant.uint16_t, val ins_size: jewelry.dex.main.constant.uint16_t, val out_size: jewelry.dex.main.constant.uint16_t, val tries_size: jewelry.dex.main.constant.uint16_t, val debug_info_off: jewelry.dex.main.constant.uint32_t, val insns_size_in_code_uints: jewelry.dex.main.constant.uint32_t, val insns: Array<jewelry.dex.main.constant.uint16_t>) {
    companion object {

    }
}

internal data class TryItem(val start_addr: jewelry.dex.main.constant.uint32_t, val insn_count: jewelry.dex.main.constant.uint16_t, val handler_off: jewelry.dex.main.constant.uint16_t) {
    companion object {
        const val size = 8

        fun create(buffer: jewelry.dex.util.data.MemoryReader) = src.jewelry.marik.dex.TryItem(buffer.uint32_t, buffer.uint16_t, buffer.uint16_t)
    }
}

internal data class AnnotationsDirectoryItem(val class_annotations_off: jewelry.dex.main.constant.uint32_t, val fields_size: jewelry.dex.main.constant.uint32_t, val methods_size: jewelry.dex.main.constant.uint32_t, val parameters_size: jewelry.dex.main.constant.uint32_t) {
    companion object {
        const val size = 16

        fun create(buffer: jewelry.dex.util.data.MemoryReader) = src.jewelry.marik.dex.AnnotationsDirectoryItem(buffer.uint32_t, buffer.uint32_t, buffer.uint32_t, buffer.uint32_t)
    }
}

internal data class FieldAnnotationsItem(val field_idx: jewelry.dex.main.constant.uint32_t, val annotations_off: jewelry.dex.main.constant.uint32_t) {
    companion object {
        const val size = 8

        fun create(buffer: jewelry.dex.util.data.MemoryReader) = src.jewelry.marik.dex.FieldAnnotationsItem(buffer.uint32_t, buffer.uint32_t)
    }
}

internal data class MethodAnnotationsItem(val method_idx: jewelry.dex.main.constant.uint32_t, val annotations_off: jewelry.dex.main.constant.uint32_t) {
    companion object {
        const val size = 8

        fun create(buffer: jewelry.dex.util.data.MemoryReader) = src.jewelry.marik.dex.MethodAnnotationsItem(buffer.uint32_t, buffer.uint32_t)
    }
}

internal data class ParameterAnnotationsItem(val method_idx: jewelry.dex.main.constant.uint32_t, val annotations_off: jewelry.dex.main.constant.uint32_t) {
    companion object {
        const val size = 8

        fun create(buffer: jewelry.dex.util.data.MemoryReader) = src.jewelry.marik.dex.ParameterAnnotationsItem(buffer.uint32_t, buffer.uint32_t)
    }
}

internal data class AnnotationSetRefItem(val annotations_off: jewelry.dex.main.constant.uint32_t) {
    companion object {
        const val size = 4

        fun create(buffer: jewelry.dex.util.data.MemoryReader) = src.jewelry.marik.dex.AnnotationSetRefItem(buffer.uint32_t)
    }
}