package jewelry.dex.main

import jewelry.dex.main.constant.dex.DexFile
import jewelry.dex.main.constant.u1Array
import jewelry.dex.main.constant.u4
import jewelry.dex.main.ineterface.DexBase
import jewelry.dex.util.data.*
import java.io.OutputStream

internal class DexHeader(val partial: DexPartial) : DexBase<DexHeader.Companion.DexHeaderHolder>(partial.begin) {

    internal val begin = partial.begin
    internal val size = partial.size

    private var _map_list: MapList? = null

    internal val map_list: MapList
        get() {
            if (_map_list == null) {
                _map_list = MapList.create(MemoryReader(begin + holder.map_off))
            }
            return _map_list!!
        }

    override fun onCreateHolder(): DexHeaderHolder {
        return DexHeaderHolder(this)
    }

    override fun onWriteTo(out: OutputStream) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        internal class DexHeaderHolder(val header: DexHeader) : DexBase.Companion.DexBaseMemberHolder() {

            var magic: u1Array = ByteArray(DexFile.kMagicSize)
                private set
            var checksum: u4 = 0  // See also location_checksum_
                private set
            var signature: u1Array = ByteArray(DexFile.kSha1DigestSize)
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

                var reader = MemoryReader(offset)
                reader.copyTo(magic)
                checksum = reader.u4
                reader.copyTo(signature)
                file_size = reader.u4
                header_size = reader.u4
                endian_tag = reader.u4
                link_size = reader.u4
                link_off = reader.u4
                map_off = reader.u4
                string_ids_size = reader.u4
                string_ids_off = reader.u4
                type_ids_size = reader.u4
                type_ids_off = reader.u4
                proto_ids_size = reader.u4
                proto_ids_off = reader.u4
                field_ids_size = reader.u4
                field_ids_off = reader.u4
                method_ids_size = reader.u4
                method_ids_off = reader.u4
                class_defs_size = reader.u4
                class_defs_off = reader.u4
                data_size = reader.u4
                data_off = reader.u4
            }

            override fun onVerify() {
                DexVerifier(this, header.begin, header.size, header.partial.dex.location).verify(false)
            }
        }
    }
}