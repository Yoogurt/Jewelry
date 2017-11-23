package jewelry.dex.main.constant.dex

import jewelry.dex.main.constant.u1Array

object DexHeaderConstant {

    const val kMagicSize = 8
    const val kSha1DigestSize = 20
    const val kDexLittleEndianConstant = 0x12345678
    const val kDexBigEndianConstant = 0x78563412
    const val kDexHeaderSize = 112

    val kDexMagic: u1Array = byteArrayOf('d'.toByte(), 'e'.toByte(), 'x'.toByte(), '\n'.toByte())
    val kDexMagicVersions: Array<ByteArray> = arrayOf(byteArrayOf('0'.toByte(), '3'.toByte(), '5'.toByte(), 0), byteArrayOf('0'.toByte(), '3'.toByte(), '7'.toByte(), 0))

}