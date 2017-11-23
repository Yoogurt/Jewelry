package jewelry.dex.main

import jewelry.dex.main.constant.dex.DexHeaderConstant
import jewelry.dex.main.constant.u1Array
import jewelry.dex.main.constant.u4
import jewelry.dex.main.ineterface.DexBase
import jewelry.dex.os.OS
import jewelry.dex.util.data.*
import java.io.OutputStream

internal class DexHeader(val partial: DexPartial) : DexBase<DexHeader.Companion.DexHeaderHolder>(partial.begin) {

    internal val begin = partial.begin
    internal val size = partial.size

    override fun onCreateHolder(): DexHeaderHolder {
        return DexHeaderHolder(this)
    }

    override fun onWriteTo(out: OutputStream) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        internal class DexHeaderHolder(val header: DexHeader) : DexBase.Companion.DexBaseMemberHolder() {

            var magic: u1Array = ByteArray(DexHeaderConstant.kMagicSize)
                private set
            var checksum: u4 = 0  // See also location_checksum_
                private set
            var signature: u1Array = ByteArray(DexHeaderConstant.kSha1DigestSize)
                private set
            var file_size: u4 = 0  // size of entire file
                private set
            var header_size: u4 = 0  // start to start of next section
                private set
            var endian_tag: u4 = 0
                private set
            var link_size: u4 = 0  // unused
                private set
            var link_off: u4 = 0  // unused
                private set
            var map_off: u4 = 0  // unused
                private set
            var string_ids_size: u4 = 0  // number of StringIds
                private set
            var string_ids_off: u4 = 0  // file start of StringIds array
                private set
            var type_ids_size: u4 = 0  // number of TypeIds, we don't support more than 65535
                private set
            var type_ids_off: u4 = 0  // file start of TypeIds array
                private set
            var proto_ids_size: u4 = 0  // number of ProtoIds, we don't support more than 65535
                private set
            var proto_ids_off: u4 = 0  // file start of ProtoIds array
                private set
            var field_ids_size: u4 = 0  // number of FieldIds
                private set
            var field_ids_off: u4 = 0  // file start of FieldIds array
                private set
            var method_ids_size: u4 = 0  // number of MethodIds
                private set
            var method_ids_off: u4 = 0  // file start of MethodIds array
                private set
            var class_defs_size: u4 = 0  // number of ClassDefs
                private set
            var class_defs_off: u4 = 0  // file start of ClassDef array
                private set
            var data_size: u4 = 0  // unused
                private set
            var data_off: u4 = 0  // unused
                private set

            override fun onParse(offset: Int) {
                val memory = OS.MEMORY
                memory.copyTo(magic, offset)
                checksum = memory.toInt32(offset + 8)
                memory.copyTo(signature, offset + 12)
                file_size = memory.toInt32(offset + 32)
                header_size = memory.toInt32(offset + 36)
                endian_tag = memory.toInt32(offset + 40)
                link_size = memory.toInt32(offset + 44)
                link_off = memory.toInt32(offset + 48)
                map_off = memory.toInt32(offset + 52)
                string_ids_size = memory.toInt32(offset + 56)
                string_ids_off = memory.toInt32(offset + 60)
                type_ids_size = memory.toInt32(offset + 64)
                type_ids_off = memory.toInt32(offset + 68)
                proto_ids_size = memory.toInt32(offset + 72)
                proto_ids_off = memory.toInt32(offset + 76)
                field_ids_size = memory.toInt32(offset + 80)
                field_ids_off = memory.toInt32(offset + 84)
                method_ids_size = memory.toInt32(offset + 88)
                method_ids_off = memory.toInt32(offset + 92)
                class_defs_size = memory.toInt32(offset + 96)
                class_defs_off = memory.toInt32(offset + 100)
                data_size = memory.toInt32(offset + 104)
                data_off = memory.toInt32(offset + 108)
            }

            override fun onVerify() {
                DexVerifier(this, header.begin, header.size, header.partial.dex.location).verify(false)
            }
        }
    }
}