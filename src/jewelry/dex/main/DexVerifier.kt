package jewelry.dex.main

import jewelry.dex.main.constant.dex.DexHeaderConstant
import jewelry.dex.os.OS
import jewelry.dex.util.data.*
import jewelry.dex.util.log.error
import jewelry.dex.util.log.log
import java.util.zip.Adler32

internal class DexVerifier(holder: DexHeader.Companion.DexHeaderHolder, size: Int) {

    private var holder = holder
    private var size = size

    fun verify() {
        checkSize()
        checkAdler32()
        verifyMagic()
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
        val non_sum_ptr = holder.host.begin + non_sum
        adler.update(OS.MEMORY, non_sum_ptr, holder.file_size - non_sum)
        if (adler.value.toInt() != holder.checksum)
            "header checksum verify fail , expect ${holder.checksum.toByteArray().toHex()} but got ${adler.value.toInt().toByteArray().toHex()}".error()
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
}