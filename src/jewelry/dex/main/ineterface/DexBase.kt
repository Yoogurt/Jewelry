package jewelry.dex.main.ineterface

import jewelry.dex.main.DexHeader
import java.io.OutputStream
import java.io.RandomAccessFile

internal abstract class DexBase<out Holder> constructor(offset: Int) where Holder : DexBase.Companion.DexBaseMemberHolder {

    protected val pointer = offset

    internal val holder: Holder

    init {
        holder = onCreateHolder()
    }

    /*
    * load menber
    */
    protected abstract fun onCreateHolder(): Holder

    fun parse() {
        holder.onParse(pointer)
        holder.onVerify()
    }

    protected abstract fun onWriteTo(out: OutputStream)

    companion object {
        internal abstract class DexBaseMemberHolder {
            abstract fun onParse(offset : Int)
            abstract fun onVerify()
        }
    }
}