package jewelry.dex.util.memory

val PAGE_MASK = (4096L - 1)
val PAGE_SIZE = 4096
val PAGE_SHIFT = 12

inline fun PAGE_START(`val`: Long): Long {
    return `val` and PAGE_MASK.inv()
}

inline fun PAGE_OFFSET(`val`: Long): Long {
    return `val` and PAGE_MASK
}

inline fun PAGE_END(`val`: Long): Long {
    return PAGE_START(`val` + (PAGE_SIZE - 1))
}