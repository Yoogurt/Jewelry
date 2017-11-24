package jewelry.dex.main.constant.dex

import jewelry.dex.main.constant.u1Array

object DexFile {

    const val kMagicSize = 8
    const val kSha1DigestSize = 20
    const val kDexLittleEndianConstant = 0x12345678
    const val kDexBigEndianConstant = 0x78563412
    const val kDexHeaderSize = 112

    val kDexMagic: u1Array = byteArrayOf('d'.toByte(), 'e'.toByte(), 'x'.toByte(), '\n'.toByte())
    val kDexMagicVersions: Array<ByteArray> = arrayOf(byteArrayOf('0'.toByte(), '3'.toByte(), '5'.toByte(), 0), byteArrayOf('0'.toByte(), '3'.toByte(), '7'.toByte(), 0))

    const val kDexTypeHeaderItem = 0x0000
    const val kDexTypeStringIdItem = 0x0001
    const val kDexTypeTypeIdItem = 0x0002
    const val kDexTypeProtoIdItem = 0x0003
    const val kDexTypeFieldIdItem = 0x0004
    const val kDexTypeMethodIdItem = 0x0005
    const val kDexTypeClassDefItem = 0x0006

    const val kDexTypeMapList = 0x1000
    const val kDexTypeTypeList = 0x1001
    const val kDexTypeAnnotationSetRefList = 0x1002
    const val kDexTypeAnnotationSetItem = 0x1003
    const val kDexTypeClassDataItem = 0x2000
    const val kDexTypeCodeItem = 0x2001
    const val kDexTypeStringDataItem = 0x2002
    const val kDexTypeDebugInfoItem = 0x2003
    const val kDexTypeAnnotationItem = 0x2004
    const val kDexTypeEncodedArrayItem = 0x2005
    const val kDexTypeAnnotationsDirectoryItem = 0x2006
}