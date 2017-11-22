package jewelry.dex.main

class DexException : Exception {
    constructor() : super()
    constructor(msg: String) : super(msg)
    constructor(e: Throwable) : super(e)
}