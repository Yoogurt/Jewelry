package jewelry.dex.main.constant.dex

import jewelry.dex.main.constant.u1Array

object DexFile {

    const val kMagicSize = 8
    const val kSha1DigestSize = 20
    const val kDexLittleEndianConstant = 0x12345678
    const val kDexBigEndianConstant = 0x78563412
    const val kDexHeaderSize = 112

    val kDexMagic: u1Array = byteArrayOf('d'.toByte(), 'e'.toByte(), 'x'.toByte(), '\n'.toByte())
    val kDexMagicVersions: Array<ByteArray> = arrayOf(byteArrayOf('0'.toByte(), '3'.toByte(), '5'.toByte(), 0), byteArrayOf('0'.toByte(), '3'.toByte(), '7'.toByte(), 0))

    const val kDexTypeHeaderItem = 0x0000
    const val kDexTypeStringIdItem = 0x0001
    const val kDexTypeTypeIdItem = 0x0002
    const val kDexTypeProtoIdItem = 0x0003
    const val kDexTypeFieldIdItem = 0x0004
    const val kDexTypeMethodIdItem = 0x0005
    const val kDexTypeClassDefItem = 0x0006

    const val kDexTypeMapList = 0x1000
    const val kDexTypeTypeList = 0x1001
    const val kDexTypeAnnotationSetRefList = 0x1002
    const val kDexTypeAnnotationSetItem = 0x1003
    const val kDexTypeClassDataItem = 0x2000
    const val kDexTypeCodeItem = 0x2001
    const val kDexTypeStringDataItem = 0x2002
    const val kDexTypeDebugInfoItem = 0x2003
    const val kDexTypeAnnotationItem = 0x2004
    const val kDexTypeEncodedArrayItem = 0x2005
    const val kDexTypeAnnotationsDirectoryItem = 0x2006
}

val kDexVisibilityBuild = 0x00     /* annotation visibility */
val kDexVisibilityRuntime = 0x01
val kDexVisibilitySystem = 0x02

val kDexAnnotationByte = 0x00
val kDexAnnotationShort = 0x02
val kDexAnnotationChar = 0x03
val kDexAnnotationInt = 0x04
val kDexAnnotationLong = 0x06
val kDexAnnotationFloat = 0x10
val kDexAnnotationDouble = 0x11
val kDexAnnotationString = 0x17
val kDexAnnotationType = 0x18
val kDexAnnotationField = 0x19
val kDexAnnotationMethod = 0x1a
val kDexAnnotationEnum = 0x1b
val kDexAnnotationArray = 0x1c
val kDexAnnotationAnnotation = 0x1d
val kDexAnnotationNull = 0x1e
val kDexAnnotationBoolean = 0x1f

val kDexAnnotationValueTypeMask = 0x1f     /* low 5 bits */
val kDexAnnotationValueArgShift = 5


const val kAccPublic = 0x0001  // class, field, method, ic
const val kAccPrivate = 0x0002  // field, method, ic
const val kAccProtected = 0x0004  // field, method, ic
const val kAccStatic = 0x0008  // field, method, ic
const val kAccFinal = 0x0010  // class, field, method, ic
const val kAccSynchronized = 0x0020  // method (only allowed on natives)
const val kAccSuper = 0x0020  // class (not used in dex)
const val kAccVolatile = 0x0040  // field
const val kAccBridge = 0x0040  // method (1.5)
const val kAccTransient = 0x0080  // field
const val kAccVarargs = 0x0080  // method (1.5)
const val kAccNative = 0x0100  // method
const val kAccInterface = 0x0200  // class, ic
const val kAccAbstract = 0x0400  // class, method, ic
const val kAccStrict = 0x0800  // method
const val kAccSynthetic = 0x1000  // class, field, method, ic
const val kAccAnnotation = 0x2000  // class, ic (1.5)
const val kAccEnum = 0x4000  // class, field, ic (1.5)

const val kAccJavaFlagsMask = 0xffff  // bits set from Java sources (low 16)

const val kAccConstructor = 0x00010000  // method (dex only) <(cl)init>
const val kAccDeclaredSynchronized = 0x00020000  // method (dex only)
const val kAccClassIsProxy = 0x00040000  // class  (dex only)
// Used by a method to denote that its execution does not need to go through slow path interpreter.
const val kAccSkipAccessChecks = 0x00080000  // method (dex only)
// Used by a class to denote that the verifier has attempted to check it at least once.
const val kAccVerificationAttempted = 0x00080000  // class (runtime)
const val kAccFastNative = 0x00080000  // method (dex only)
// This is set by the class linker during LinkInterfaceMethods. It is used by a method to represent
// that it was copied from its declaring class into another class. All methods marked kAccMiranda
// and kAccDefaultConflict will have this bit set. Any kAccDefault method contained in the methods_
// array of a concrete class will also have this bit set.
const val kAccCopied = 0x00100000  // method (runtime)
const val kAccMiranda = 0x00200000  // method (dex only)
const val kAccDefault = 0x00400000  // method (runtime)
// This is set by the class linker during LinkInterfaceMethods. Prior to that point we do not know
// if any particular method needs to be a default conflict. Used to figure out at runtime if
// invoking this method will throw an exception.
const val kAccDefaultConflict = 0x00800000  // method (runtime)

// Set by the verifier for a method we do not want the compiler to compile.
const val kAccCompileDontBother = 0x01000000  // method (runtime)

// Set by the verifier for a method that could not be verified to follow structured locking.
const val kAccMustCountLocks = 0x02000000  // method (runtime)

// Special runtime-only flags.
// Interface and all its super-interfaces with default methods have been recursively initialized.
const val kAccRecursivelyInitialized = 0x20000000
// Interface declares some default method.
const val kAccHasDefaultMethod = 0x40000000
// class/ancestor overrides finalize()
const val kAccClassIsFinalizable = 0x80000000


// Valid (meaningful) bits for a field.
const val kAccValidFieldFlags = kAccPublic or kAccPrivate or kAccProtected or
        kAccStatic or kAccFinal or kAccVolatile or kAccTransient or kAccSynthetic or kAccEnum

// Valid (meaningful) bits for a method.
const val kAccValidMethodFlags = kAccPublic or kAccPrivate or kAccProtected or
        kAccStatic or kAccFinal or kAccSynchronized or kAccBridge or kAccVarargs or kAccNative or
        kAccAbstract or kAccStrict or kAccSynthetic or kAccMiranda or kAccConstructor or
        kAccDeclaredSynchronized

// Valid (meaningful) bits for a class (not interface).
// Note 1. These are positive bits. Other bits may have to be zero.
// Note 2. Inner classes can expose more access flags to Java programs. That is handled by libcore.
const val kAccValidClassFlags = kAccPublic or kAccFinal or kAccSuper or
        kAccAbstract or kAccSynthetic or kAccEnum

// Valid (meaningful) bits for an interface.
// Note 1. Annotations are interfaces.
// Note 2. These are positive bits. Other bits may have to be zero.
// Note 3. Inner classes can expose more access flags to Java programs. That is handled by libcore.
const val kAccValidInterfaceFlags = kAccPublic or kAccInterface or
        kAccAbstract or kAccSynthetic or kAccAnnotation
