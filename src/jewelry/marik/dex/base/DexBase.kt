package jewelry.dex.main.ineterface

import java.io.OutputStream

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

    fun parse(verify: Boolean) {
        holder.onParse(pointer)
        if (verify)
            holder.onVerify()
    }

    protected abstract fun onWriteTo(out: OutputStream)

    companion object {
        internal abstract class DexBaseMemberHolder {
            abstract fun onParse(offset: Int)
            abstract fun onVerify()
        }
    }
}