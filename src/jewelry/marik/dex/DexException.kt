package jewelry.marik.dex

class DexException : Exception {
    constructor(msg: String) : super(msg)
    constructor(e: Throwable) : super(e)
}