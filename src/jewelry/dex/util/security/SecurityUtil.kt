package jewelry.dex.util.security

import jewelry.dex.os.OS
import jewelry.dex.util.data.toHex
import java.security.MessageDigest

object SecurityUtil {

    fun getSha1Sum(startIndex: Int = 0, length: Int): ByteArray {
        var messageDigest = MessageDigest.getInstance("sha-1")
        messageDigest.update(OS.MEMORY, startIndex, length)
        return messageDigest.digest()
    }
}

fun main(vararg arg: String) {

    OS.MEMORY[0] = 'a'.toByte()
    SecurityUtil.getSha1Sum(0, 1).forEach { print(" ${it.toHex()}") }

}