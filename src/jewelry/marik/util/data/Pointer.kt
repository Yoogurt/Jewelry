package jewelry.marik.util.data

import jewelry.marik.dex.Struct
import jewelry.marik.dex.constant.alais.uint32_t
import jewelry.marik.dex.constant.alais.uint8_t
import jewelry.marik.os.OS
import jewelry.marik.util.log.error
import kotlin.collections.ArrayList
import kotlin.reflect.KClass

/* void* */
internal abstract class Pointer<Type> constructor(pointer: uint32_t) {
    abstract val size: Int

    var address = pointer
        internal set

    override operator fun equals(other: Any?): Boolean {
        return if ((other == null))
            false
        else {
            if (other !is Pointer<*>)
                if (other !is Number)
                    false
                else
                    address == other.toInt()
            else
                address == other.address
        }
    }

    abstract operator fun plus(value: Int): Pointer<Type>

    abstract operator fun minus(value: Int): Pointer<Type>

    abstract operator fun minus(value: Pointer<Type>): Int

    abstract operator fun inc(): Pointer<Type>

    abstract operator fun dec(): Pointer<Type>

    abstract operator fun get(index: Int): Type

    abstract operator fun set(index: Int, value: Type)

    open operator fun compareTo(other: Pointer<Type>): Int = address.toUnsigned().compareTo(other.address.toUnsigned())

    open operator fun compareTo(other: Number): Int = address.toUnsigned().compareTo(other.toInt())

    override fun toString(): String = "${this::class.simpleName} at $address"

    val const: Pointer<Type>
        get() = ConstValuePointer<Type>(this)
}

internal class ConstValuePointer<Type>(private val source: Pointer<Type>) : Pointer<Type>(source.address) {
    override val size: Int
        get() = source.size

    override operator fun plus(value: Int): Pointer<Type> = source + value

    override operator fun minus(value: Int): Pointer<Type> = source - value

    override operator fun minus(value: Pointer<Type>): Int = source - value

    override fun inc(): Pointer<Type> = source.inc()

    override fun dec(): Pointer<Type> = source.dec()

    override operator fun get(index: Int): Type = source[index]

    override operator fun set(index: Int, value: Type) = throw UnsupportedOperationException()
}

/*  void**  */
internal class SecondaryPointer<Type : Pointer<*>>(protected var out: Type) : Pointer<Type>(0) {
    override fun get(index: Int): Type {
        if (index == 0)
            return out
        else throw ArrayIndexOutOfBoundsException()
    }

    override fun set(index: Int, value: Type) {
        if (index == 0)
            out.address = value.address
        else throw ArrayIndexOutOfBoundsException()
    }

    override operator fun plus(value: Int): Pointer<Type> {
        "".error()
    }

    override operator fun minus(value: Int): Pointer<Type> {
        "".error()
    }

    override fun inc(): Pointer<Type> {
        "inc is not supported".error()
    }

    override fun dec(): Pointer<Type> {
        "dec is not supported".error()
    }

    override operator fun minus(value: Pointer<Type>) = address - value.address

    override fun toString(): String = "${this::class.simpleName} contains { $out }"

    override val size = 4
}

/* Object* , length = 1*/
internal class ObjectPointer<Type>(source: Type) : Pointer<Type>(-1) {
    var source: Type = source
        protected set

    override fun plus(value: Int): Pointer<Type> {
        "plus is not supported".error()
    }

    override fun minus(value: Int): Pointer<Type> {
        "minus is not supported".error()
    }

    override fun minus(value: Pointer<Type>): Int {
        "minus is not supported".error()
    }

    override fun inc(): Pointer<Type> {
        "inc is not supported".error()
    }

    override fun dec(): Pointer<Type> {
        "dec is not supported".error()
    }

    override fun set(index: Int, value: Type) {
        if (index == 0) source = value else throw IndexOutOfBoundsException()
    }

    override val size: Int = 4

    override fun get(index: Int): Type = if (index == 0) source else throw IndexOutOfBoundsException()
}

/* struct[] */
abstract internal class StructPointer<Type : Struct<*>>(pointer: uint32_t, private val clz: KClass<Type>) : Pointer<Type>(pointer) {
    override val size: Int = 4

    override fun get(index: Int): Type {
        "".error()
    }
}

/* uint8_t* */
internal class BytePtr(pointer: uint32_t) : Pointer<Byte>(pointer) {
    override fun get(index: Int): Byte = OS.MEMORY[address + index * size]

    override fun set(index: Int, value: Byte) {
        OS.MEMORY[address + index * size] = value
    }

    override val size: Int = 1

    override operator fun plus(value: Int) = BytePtr(address + size * value)

    override operator fun minus(value: Int) = BytePtr(address - size * value)

    override operator fun minus(value: Pointer<Byte>) = address - value.address

    override operator fun inc() = BytePtr(address + size)

    override operator fun dec() = BytePtr(address - size)

}

internal val Pointer<uint8_t>.string: String
    get() {
        val collection = ArrayList<Byte>(20)

        (address..Int.MAX_VALUE).forEach {
            if (OS.MEMORY[it] != 0.toByte())
                collection.add(OS.MEMORY[it])
            else return String(collection.toByteArray())
        }
        "out of range".error()
    }

internal val BytePtr.string: String
    get() = (this as Pointer<uint8_t>).string

/* uint32_t* */
internal class ShortPtr(pointer: uint32_t) : Pointer<Short>(pointer) {
    override fun get(index: Int): Short = OS.MEMORY.toUInt16(address + index * size)

    override fun set(index: Int, value: Short) {
        value.toByteArray().forEachIndexed { i, it ->
            OS.MEMORY[address + index * size + i] = it
        }
    }

    override val size: Int = 2

    override operator fun plus(value: Int) = ShortPtr(address + size * value)

    override operator fun minus(value: Int) = ShortPtr(address - size * value)

    override operator fun minus(value: Pointer<Short>) = address - value.address

    override operator fun inc() = ShortPtr(address + size)

    override operator fun dec() = ShortPtr(address - size)
}

/* uint32_t* */
internal class IntPtr(pointer: uint32_t) : Pointer<Int>(pointer) {
    override fun get(index: Int): Int = OS.MEMORY.toUInt32(address + index * size)

    override fun set(index: Int, value: Int) {
        value.toByteArray().forEachIndexed { i, it ->
            OS.MEMORY[address + index * size + i] = it
        }
    }

    override val size: Int = 4

    override operator fun plus(value: Int) = IntPtr(address + size * value)

    override operator fun minus(value: Int) = IntPtr(address - size * value)

    override operator fun minus(value: Pointer<Int>) = address - value.address

    override operator fun inc() = IntPtr(address + size)

    override operator fun dec() = IntPtr(address - size)
}

/* uint64_t* */
internal class LongPtr(pointer: uint32_t) : Pointer<Long>(pointer) {
    override fun get(index: Int): Long = OS.MEMORY.toUInt64(address + index * size)

    override fun set(index: Int, value: Long) {
        value.toByteArray().forEachIndexed { i, it ->
            OS.MEMORY[address + index * size + i] = it
        }
    }

    override val size: Int = 4

    override operator fun plus(value: Int) = LongPtr(address + size * value)

    override operator fun minus(value: Int) = LongPtr(address - size * value)

    override operator fun minus(value: Pointer<Long>) = address - value.address

    override operator fun inc() = LongPtr(address + size)

    override operator fun dec() = LongPtr(address - size)
}

/* reinterpret_cast support */
inline internal fun <reified P : Pointer<*>> reinterpret_cast(source: Pointer<*>) = when (P::class) {
    BytePtr::class -> (BytePtr(source.address) as P)
    ShortPtr::class -> (ShortPtr(source.address) as P)
    IntPtr::class -> (IntPtr(source.address) as P)
    LongPtr::class -> (LongPtr(source.address) as P)
    else ->
        "use override class StructPointer instead".error()
}

/* equals with operator '&' */
inline internal val <reified Type> Type.pointer: Pointer<Type>
    get() = when (Type::class) {
        Pointer::class ->
            (SecondaryPointer(this as Pointer<*>) as Pointer<Type>)
        else ->
            ObjectPointer(this)
    }


fun main(vararg arg: String) {
//    (0..40).forEach {
//        OS.MEMORY[it] = it.toByte()
//    }

//    val test = 0.pointer
//
//    test[0] = 123
//
//    println(test)

//    val int_ptr = IntPtr(2)
//    (0..3).forEach {
//        println(int_ptr[it].toHex())
//    }
//    val memitem_ptr = object : StructPointer<MapItem>(0) {
//        override val size: Int
//            get() = MapItem.size
//
//        override fun getValue(buffer: MemoryReader): MapItem =
//                MapItem.create(buffer)
//    }

//    val byte_ptr = BytePtr(0)
//
//    val int_ptr = reinterpret_cast<LongPtr>(byte_ptr)
//
//    println(int_ptr[0].toHex())

//    val test = 32
//    val look = test.pointer.pointer.pointer
//    print(look)

//    testBytePtrString()

//    testBytePtrUTFString()

}

fun testBytePtrString() {
    ('a'..'z').forEachIndexed { i, it ->
        OS.MEMORY[i] = it.toByte()
    }

    println(BytePtr(0).string)
}

fun testBytePtrUTFString() {
    val data = "你好，我好，大家好".toByteArray()

    System.arraycopy(data, 0, OS.MEMORY, 0, data.size)
    println((BytePtr(0)).string)
}