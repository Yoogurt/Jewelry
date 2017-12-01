package src.jewelry.marik.dex

import jewelry.dex.util.log.error
import jewelry.marik.dex.MapList
import src.jewelry.marik.dex.constant.DexFile

internal class DexHeader(val partial: DexPartial) : jewelry.dex.main.ineterface.DexBase<DexHeader.Companion.DexHeaderHolder>(partial.begin) {

    internal val begin = partial.begin
    internal val size = partial.size

    private var _map_list: MapList? = null

    internal val map_list: MapList
        get() {
            if (_map_list == null) {
                _map_list = MapList.Companion.create(jewelry.dex.util.data.MemoryReader(begin + holder.map_off))
            }
            return _map_list!!
        }

    override fun onCreateHolder(): src.jewelry.marik.dex.DexHeader.Companion.DexHeaderHolder {
        return src.jewelry.marik.dex.DexHeader.Companion.DexHeaderHolder(this)
    }

    override fun onWriteTo(out: java.io.OutputStream) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        internal class DexHeaderHolder(val header: src.jewelry.marik.dex.DexHeader) : jewelry.dex.main.ineterface.DexBase.Companion.DexBaseMemberHolder() {

            var magic: jewelry.dex.main.constant.u1Array = ByteArray(DexFile.kMagicSize)
                private set
            var checksum: jewelry.dex.main.constant.u4 = 0  // See also location_checksum_
                private set
            var signature: jewelry.dex.main.constant.u1Array = ByteArray(DexFile.kSha1DigestSize)
                private set
            var file_size: jewelry.dex.main.constant.u4 = 0  // size of entire file
                private set
            var header_size: jewelry.dex.main.constant.u4 = 0  // start to start of next section
                private set
            var endian_tag: jewelry.dex.main.constant.u4 = 0
                private set
            var link_size: jewelry.dex.main.constant.u4 = 0  // unused
                private set
            var link_off: jewelry.dex.main.constant.u4 = 0  // unused
                private set
            var map_off: jewelry.dex.main.constant.u4 = 0  // unused
                private set
            var string_ids_size: jewelry.dex.main.constant.u4 = 0  // number of StringIds
                private set
            var string_ids_off: jewelry.dex.main.constant.u4 = 0  // file start of StringIds array
                private set
            var type_ids_size: jewelry.dex.main.constant.u4 = 0  // number of TypeIds, we don't support more than 65535
                private set(value) {
                    if (value > 65535)
                        "type_ids_size larger than 65535".error()
                    field = value
                }
            var type_ids_off: jewelry.dex.main.constant.u4 = 0  // file start of TypeIds array
                private set
            var proto_ids_size: jewelry.dex.main.constant.u4 = 0  // number of ProtoIds, we don't support more than 65535
                private set(value) {
                    if (value > 65535)
                        "proto_ids_size larger than 65535".error()
                    field = value
                }
            var proto_ids_off: jewelry.dex.main.constant.u4 = 0  // file start of ProtoIds array
                private set
            var field_ids_size: jewelry.dex.main.constant.u4 = 0  // number of FieldIds
                private set
            var field_ids_off: jewelry.dex.main.constant.u4 = 0  // file start of FieldIds array
                private set
            var method_ids_size: jewelry.dex.main.constant.u4 = 0  // number of MethodIds
                private set
            var method_ids_off: jewelry.dex.main.constant.u4 = 0  // file start of MethodIds array
                private set
            var class_defs_size: jewelry.dex.main.constant.u4 = 0  // number of ClassDefs
                private set
            var class_defs_off: jewelry.dex.main.constant.u4 = 0  // file start of ClassDef array
                private set
            var data_size: jewelry.dex.main.constant.u4 = 0  // unused
                private set
            var data_off: jewelry.dex.main.constant.u4 = 0  // unused
                private set

            override fun onParse(offset: Int) {

                var reader = jewelry.dex.util.data.MemoryReader(offset)
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

            @Throws(DexException::class)
            override fun onVerify() {
                    DexVerifier(this, header.begin, header.size, header.partial.dex.location).verify(false)
            }
        }
    }
}