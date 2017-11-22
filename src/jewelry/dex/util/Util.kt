package jewelry.dex.util

import jewelry.dex.main.constant.dex.DexConstant
import java.util.jar.JarEntry
import java.util.jar.JarFile

fun isZipFile(file: String): Boolean {
    return try {
        0 < JarFile(file).size()
    } catch (e: Throwable) {
        false
    }
}

fun isDexEntry(entry: JarEntry): Boolean {
    return !entry.isDirectory && entry.name.startsWith(DexConstant.kClassesEntryStart) && entry.name.endsWith(DexConstant.kClassesEntryEnd)
}
