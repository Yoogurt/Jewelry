package brilliant.elf.util

import java.io.FileNotFoundException
import java.io.PrintStream

object Log {

    var out = System.out

    init {
        try {
//            out = PrintStream("C:\\Users\\monitor\\Desktop\\test.dump")
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

    }

    var DEBUG = true

    fun i(msg: String) {
        if (DEBUG)
            out.println("Dex " + msg)
    }

    fun i() {
        if (DEBUG)
            out.println()
    }

    fun e(msg: String) {
        if (DEBUG)
            out.println(msg)
    }

    fun e() {
        if (DEBUG)
            out.println()
    }
}
