package com.juul.koap

import okio.BufferedSource

/** 4-bit unsigned integer maximum value. */
private const val UINT4_MAX_VALUE = 15 // 2^4-1

/** 4-bit unsigned integer range. */
internal val UINT4_RANGE = 0..UINT4_MAX_VALUE

/** 8-bit unsigned integer (UByte) maximum value. */
private const val UBYTE_MAX_VALUE = 255 // 2^8-1

/** 8-bit unsigned integer (UByte) range. */
private val UBYTE_RANGE = 0..UBYTE_MAX_VALUE

/** 16-bit unsigned integer (UShort) maximum value. */
private const val USHORT_MAX_VALUE = 65_535 // 2^16-1

/** 16-bit unsigned integer (UShort) range. */
internal val USHORT_RANGE = 0..USHORT_MAX_VALUE

/** 32-bit unsigned integer (UInt) maximum value. */
internal const val UINT_MAX_VALUE = 4_294_967_295 // 2^32-1

/** 32-bit unsigned integer (UInt) range. */
internal val UINT_RANGE = 0..UINT_MAX_VALUE

internal fun Long.fitsInUByte(): Boolean = toInt() in UBYTE_RANGE
internal fun Long.fitsInUShort(): Boolean = toInt() in USHORT_RANGE
internal fun Long.fitsInUInt(): Boolean = this in UINT_RANGE

/**
 * Reads number from [BufferedSource] receiver.
 *
 * @param length in bytes to read.
 * @return value of number.
 */
internal fun BufferedSource.readNumber(length: Int): Long {
    require(length in 1..Long.SIZE_BYTES) { "Unable to read number of length $length" }
    var value = 0L
    for (i in (length - 1) downTo 0) { // Read byte-by-byte in network byte-order.
        val byte = readByte()
        value = value or ((byte.toLong() and 0xFF) shl (i * Byte.SIZE_BITS))
    }
    return value
}
