package jewelry.dex.main

import jewelry.dex.main.constant.dex.DexHeaderConstant
import jewelry.dex.main.constant.u4
import jewelry.dex.main.constant.u8
import jewelry.dex.os.OS
import jewelry.dex.util.data.*
import jewelry.dex.util.log.error
import jewelry.dex.util.log.errorVerify
import jewelry.dex.util.log.log
import jewelry.dex.util.reinterpret_cast
import java.util.zip.Adler32

internal class DexVerifier(private val holder: DexHeader.Companion.DexHeaderHolder, val begin: Int, val size: Int, val location: String) {

    fun verify(checkAdler: Boolean = true) {
        checkHeader(checkAdler)
        checkMap()
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
        if (holder.magic.startWith(DexHeaderConstant.kDexMagic)) {
            if (!DexHeaderConstant.kDexMagicVersions.any {
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
            DexHeaderConstant.kDexLittleEndianConstant -> {
                "Little Endian Dex File".log()
                UtilGlobal.DEFAULT_LITTLE_ENDIAN = true
            }

            DexHeaderConstant.kDexBigEndianConstant -> {
                "Big Endian Dex File".log()
                UtilGlobal.DEFAULT_LITTLE_ENDIAN = false
            }
            else ->
                holder.endian_tag.error("Unknown Endian Type")
        }
    }

    private fun checkHeaderSize() {
        if (holder.header_size != DexHeaderConstant.kDexHeaderSize)
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
        val map = reinterpret_cast(begin + holder.map_off)
        checkListSize(map.start, 1, kMapListSize, "maplist content")


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

}

