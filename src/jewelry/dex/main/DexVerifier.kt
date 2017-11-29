package jewelry.dex.main

import jewelry.dex.main.constant.*
import jewelry.dex.main.constant.dex.DexFile
import jewelry.dex.os.OS
import jewelry.dex.util.data.*
import jewelry.dex.util.log.error
import jewelry.dex.util.log.errorVerify
import jewelry.dex.util.log.log
import java.util.zip.Adler32

internal class DexVerifier(private val holder: DexHeader.Companion.DexHeaderHolder, val begin: Int, val size: Int, val location: String) {

    internal var ptr: u4 = 0

    fun verify(checkAdler: Boolean = true) {
        checkHeader(checkAdler)
        checkMap()
        checkIntraSection()
    }

    /*---------------------------header----------------------------*/

    private fun checkHeader(checkAdler: Boolean = true) {
        verifyMagic()
        checkEndian()
        checkSize()
        if (checkAdler)
            checkAdler32()
        checkHeaderSize()
        checkRemainParams()
    }

    private fun verifyMagic() {
        if (holder.magic.startWith(DexFile.kDexMagic)) {
            if (!DexFile.kDexMagicVersions.any {
                return@any holder.magic.partialEquals(it, 4)
            }) {
                holder.magic.error("Unknown Dex Version")
            }
        } else {
            holder.magic.error("File is not Dex")
        }
    }

    private fun checkSize() {
        if (size != holder.file_size)
            "header size verify fail , expect ${holder.file_size} but got $size".error()
    }

    private fun checkAdler32() {
        var adler = Adler32()
        /*
        * const uint32_t non_sum = sizeof(header_->magic_) + sizeof(header_->checksum_);
        * const uint8_t* non_sum_ptr = reinterpret_cast<const uint8_t*>(header_) + non_sum;
        * adler_checksum = adler32(adler_checksum, non_sum_ptr, expected_size - non_sum);
        * */
        val non_sum = 8 + 4
        val non_sum_ptr = holder.header.begin + non_sum
        adler.update(OS.MEMORY, non_sum_ptr, holder.file_size - non_sum)
        if (adler.value.toInt() != holder.checksum)
            "header checksum verify fail".errorVerify(holder.checksum.toByteArray(), adler.value.toInt().toByteArray())
    }

    private fun checkEndian() {
        when (holder.endian_tag) {
            DexFile.kDexLittleEndianConstant -> {
                "Little Endian Dex File".log()
                UtilGlobal.DEFAULT_LITTLE_ENDIAN = true
            }

            DexFile.kDexBigEndianConstant -> {
                "Big Endian Dex File".log()
                UtilGlobal.DEFAULT_LITTLE_ENDIAN = false
            }
            else ->
                holder.endian_tag.error("Unknown Endian Type")
        }
    }

    private fun checkHeaderSize() {
        if (holder.header_size != DexFile.kDexHeaderSize)
            "Bad header size : ${holder.header_size}".error()
    }

    private fun checkRemainParams() {
        checkValidOffsetAndSize(holder.link_off, holder.link_size, 0, "link")
        checkValidOffsetAndSize(holder.map_off, holder.map_off, 4, "map")
        checkValidOffsetAndSize(holder.string_ids_off, holder.string_ids_size, 4, "string-ids")
        checkValidOffsetAndSize(holder.type_ids_off, holder.type_ids_size, 4, "type-ids")
        checkValidOffsetAndSize(holder.proto_ids_off, holder.proto_ids_size, 4, "proto-ids")
        checkValidOffsetAndSize(holder.field_ids_off, holder.field_ids_size, 4, "field-ids")
        checkValidOffsetAndSize(holder.method_ids_off, holder.method_ids_size, 4, "method-ids")
        checkValidOffsetAndSize(holder.class_defs_off, holder.class_defs_size, 4, "class-defs")
        checkValidOffsetAndSize(holder.data_off, holder.data_size, 0, "data")
    }

    private fun checkValidOffsetAndSize(offset: u4, size: u4, alignment: u4, label: String) {
        if (size == 0 && offset != 0)
            "Offset($offset) should be zero when size is zero for $label".error()

        if (holder.header.size < offset)
            "Offset($offset) should be within file size($size) for $label".error()

        if (alignment != 0 && !offset.isAlignedParam(alignment))
            "Offset($offset) should be aligned by $alignment for $label".error()
    }

    /*---------------------------map----------------------------*/
    private fun checkMap() {
        val start = begin + holder.map_off
        val map = holder.header.map_list
        checkListSize(start, 1, MapItem.size, "maplist content")

        var items = map.list

        val count = map.size
        var last_offset = 0
        var data_item_count = 0
        var data_items_left = holder.data_size
        var used_bits = 0

        checkListSize(start + 4, count, MapItem.size, "map size")

        items.forEachIndexed { index, item ->
            if (last_offset >= item.offset && index != 0)
                "Out of order map item: $last_offset then ${item.offset}".error()

            if (item.offset >= holder.file_size)
                "Map item after end of file: ${item.offset}, size ${holder.file_size}".error()


            if (isDataSectionType(item.type.toInt())) {
                val icount = item.size
                if (icount > data_items_left)
                    "Too many items in data section: ${data_item_count + icount}".error()

                data_items_left -= icount
                data_item_count += icount
            }

            val bit = mapTypeToBitMask(item.type.toInt())
            if (bit == 0)
                "Unknown map section type ${item.type}".error()

            if (used_bits and bit != 0)
                "Duplicate map section of type ${item.type}".error()

            used_bits = used_bits or bit
            last_offset = item.offset
        }

        if (used_bits and mapTypeToBitMask(DexFile.kDexTypeHeaderItem) == 0)
            "Map is missing header entry".error()

        if (used_bits and mapTypeToBitMask(DexFile.kDexTypeMapList) == 0)
            "Map is missing map_list entry".error()

        if ((used_bits and mapTypeToBitMask(DexFile.kDexTypeStringIdItem) == 0) and ((holder.string_ids_off != 0) or (holder.string_ids_size != 0)))
            "Map is missing string_ids entry".error()

        if ((used_bits and mapTypeToBitMask(DexFile.kDexTypeTypeIdItem) == 0) and ((holder.type_ids_off != 0) or (holder.type_ids_size != 0)))
            "Map is missing type_ids entry".error()

        if ((used_bits and mapTypeToBitMask(DexFile.kDexTypeProtoIdItem) == 0) and ((holder.proto_ids_off != 0) or (holder.proto_ids_size != 0)))
            "Map is missing proto_ids entry".error()

        if ((used_bits and mapTypeToBitMask(DexFile.kDexTypeFieldIdItem) == 0) and ((holder.field_ids_off != 0) or (holder.field_ids_size != 0)))
            "Map is missing field_ids entry".error()

        if ((used_bits and mapTypeToBitMask(DexFile.kDexTypeMethodIdItem) == 0) and ((holder.method_ids_off != 0) or (holder.method_ids_size != 0)))
            "Map is missing method_ids entry".error()

        if ((used_bits and mapTypeToBitMask(DexFile.kDexTypeClassDefItem) == 0) and ((holder.class_defs_off != 0) or (holder.class_defs_size != 0)))
            "Map is missing class_defs entry".error()
    }

    private fun checkListSize(start: u4, count: u4, elem_size: u4, label: String) {
        if (elem_size == 0)
            "elem_size can not be zero for $label".error()

        val range_start = start.toLong()
        val file_start = holder.header.begin.toLong()

        val max: u8 = 0xff_ff_ff_ff // max unsigned int
        val available_bytes_till_end_of_mem = max - start
        val max_count = available_bytes_till_end_of_mem / elem_size

        if (max_count < count)
            "Overflow in range for $label: ${range_start - file_start} for $count@$elem_size".error()
    }

    private fun checkList(element_size: size_t, label: String, ptr: u4): u4 {
        var ptr = ptr
        checkListSize(ptr, 1, 4, label)

        val count = OS.MEMORY.toInt32(ptr)
        if (count > 0)
            checkListSize(ptr + 4, count, element_size, label)

        ptr += 4 + count + element_size
        return ptr
    }

    /*---------------------------intraSection----------------------------*/

    private fun checkIntraSection() {
        val map = holder.header.map_list
        val items = map.list

        var offset = 0
        ptr = begin

        // Check the items listed in the map.
        items.forEachIndexed { count, item ->

            val section_offset = item.offset
            val section_count = item.size
            val type = item.type

            // Check for padding and overlap between items.
            checkPadding(offset, section_offset)
            if (offset > section_offset)
                "Section overlap or out-of-order map: ${offset.toHex()}, ${section_offset.toHex()}".error()

            when (type.toInt()) {
                DexFile.kDexTypeHeaderItem -> {
                    if (section_count != 1)
                        "Multiple header items".error()
                    if (section_offset != 0)
                        "Header at ${section_offset.toHex()}, not at start of file".error()

                    ptr = begin + holder.header_size
                    offset = holder.header_size
                }

                DexFile.kDexTypeStringIdItem,
                DexFile.kDexTypeTypeIdItem,
                DexFile.kDexTypeProtoIdItem,
                DexFile.kDexTypeFieldIdItem,
                DexFile.kDexTypeMethodIdItem,
                DexFile.kDexTypeClassDefItem -> {
                    checkIntraIdSection(section_offset, section_count, type)
                }
            }
        }
    }

    private fun checkPadding(offset: size_t, aligned_offset: uint32_t) {
        var offset = offset
        if (offset < aligned_offset) {
            checkListSize(begin + offset, aligned_offset - offset, 1, "section")

            while (offset < aligned_offset) {
                if (OS.MEMORY[ptr] != 0.toByte())
                    "Non-zero padding ${OS.MEMORY[ptr].toHex()} before section start at ${offset.toByteArray().toHex()}".error()

                ptr++
                offset++
            }
        }
    }

    private fun checkIntraIdSection(offset: size_t, count: uint32_t, type: uint16_t) {
        var expected_offset = 0
        var expected_size = 0

        when (type.toInt()) {
            DexFile.kDexTypeStringIdItem -> {
                expected_offset = holder.string_ids_off
                expected_size = holder.string_ids_size
            }
            DexFile.kDexTypeTypeIdItem -> {
                expected_offset = holder.type_ids_off
                expected_size = holder.type_ids_size
            }
            DexFile.kDexTypeProtoIdItem -> {
                expected_offset = holder.proto_ids_off
                expected_size = holder.proto_ids_size
            }
            DexFile.kDexTypeFieldIdItem -> {
                expected_offset = holder.field_ids_off
                expected_size = holder.field_ids_size
            }
            DexFile.kDexTypeMethodIdItem -> {
                expected_offset = holder.method_ids_off
                expected_size = holder.method_ids_size
            }
            DexFile.kDexTypeClassDefItem -> {
                expected_offset = holder.class_defs_off
                expected_size = holder.class_defs_size
            }
            else -> "Bad type for id section: $type".error()
        }

        // Check that the offset and size are what were expected from the header.
        if (offset != expected_offset)
            "Bad offset for section: got ${offset.toHex()}, expected ${expected_offset.toHex()}".error()

        if (count != expected_size)
            "Bad size for section: got $count, expected $expected_size".error()

        checkIntraSectionIterate(offset, count, type)
    }

    private fun checkIntraSectionIterate(offset: size_t, section_count: uint32_t, type: uint16_t) {
        // Get the right alignment mask for the type of section.
        var alignment_mask: size_t =
                when (type.toInt()) {
                    DexFile.kDexTypeClassDataItem,
                    DexFile.kDexTypeStringDataItem,
                    DexFile.kDexTypeDebugInfoItem,
                    DexFile.kDexTypeAnnotationItem,
                    DexFile.kDexTypeEncodedArrayItem ->
                        1 - 1;
                    else ->
                        4 - 1;
                }

        // Iterate through the items in the section.
        (0 until section_count).forEach {
            var aligned_offset = (offset + alignment_mask) and alignment_mask.inv()

            // Check the padding between items.
            checkPadding(offset, aligned_offset)

            when (type.toInt()) {
                DexFile.kDexTypeStringIdItem -> {
                    checkListSize(ptr, 1, StringId.size, "string_ids")
                    ptr += StringId.size
                }
                DexFile.kDexTypeTypeIdItem -> {
                    checkListSize(ptr, 1, TypeId.size, "type_ids")
                    ptr += TypeId.size
                }
                DexFile.kDexTypeProtoIdItem -> {
                    checkListSize(ptr, 1, ProtoId.size, "proto_ids")
                    ptr += ProtoId.size
                }
                DexFile.kDexTypeFieldIdItem -> {
                    checkListSize(ptr, 1, FieldId.size, "field_ids")
                    ptr += FieldId.size
                }
                DexFile.kDexTypeMethodIdItem -> {
                    checkListSize(ptr, 1, MethodId.size, "method_ids")
                    ptr += MethodId.size
                }
                DexFile.kDexTypeClassDefItem -> {
                    checkListSize(ptr, 1, ClassDef.size, "class_defs")
                    ptr += ClassDef.size
                }
                DexFile.kDexTypeTypeList -> {
                    ptr = checkList(TypeItem.size, "type_list", ptr)
                }
                DexFile.kDexTypeAnnotationSetRefList -> {
                    ptr = checkList(AnnotationSetRefItem.size, "annotation_set_ref_list", ptr)
                }
                DexFile.kDexTypeAnnotationSetItem -> {
                    ptr = checkList(4, "annotation_Set_item", ptr)
                }
                DexFile.kDexTypeClassDataItem -> {

                }
            }
        }
    }

    private fun checkIntraClassDataItem() {
        var have_class = false
        var class_type_index: uint16_t
        var class_access_flag: uint32_t
    }

    private fun <kStatic> checkIntraClassDataItemFields() where kStatic : Boolean {
    }

    /*---------------------------interSection----------------------------*/
    companion object {
        private fun mapTypeToBitMask(map_type: uint32_t): uint32_t =
                when (map_type) {
                    DexFile.kDexTypeHeaderItem -> 1 shl 0
                    DexFile.kDexTypeStringIdItem -> 1 shl 1
                    DexFile.kDexTypeTypeIdItem -> 1 shl 2
                    DexFile.kDexTypeProtoIdItem -> 1 shl 3
                    DexFile.kDexTypeFieldIdItem -> 1 shl 4
                    DexFile.kDexTypeMethodIdItem -> 1 shl 5
                    DexFile.kDexTypeClassDefItem -> 1 shl 6
                    DexFile.kDexTypeMapList -> 1 shl 7
                    DexFile.kDexTypeTypeList -> 1 shl 8
                    DexFile.kDexTypeAnnotationSetRefList -> 1 shl 9
                    DexFile.kDexTypeAnnotationSetItem -> 1 shl 10
                    DexFile.kDexTypeClassDataItem -> 1 shl 11
                    DexFile.kDexTypeCodeItem -> 1 shl 12
                    DexFile.kDexTypeStringDataItem -> 1 shl 13
                    DexFile.kDexTypeDebugInfoItem -> 1 shl 14
                    DexFile.kDexTypeAnnotationItem -> 1 shl 15
                    DexFile.kDexTypeEncodedArrayItem -> 1 shl 16
                    DexFile.kDexTypeAnnotationsDirectoryItem -> 1 shl 17
                    else -> 0
                }

        private fun isDataSectionType(map_type: u4): Boolean =
                when (map_type) {
                    DexFile.kDexTypeHeaderItem, DexFile.kDexTypeStringIdItem, DexFile.kDexTypeTypeIdItem, DexFile.kDexTypeProtoIdItem, DexFile.kDexTypeFieldIdItem, DexFile.kDexTypeMethodIdItem, DexFile.kDexTypeClassDefItem ->
                        false
                    else -> true
                }
    }

}