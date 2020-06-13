package com.juul.koap

/** 4-bit unsigned integer maximum value. */
private const val UINT4_MAX_VALUE = 15 // 2^4-1

/** 4-bit unsigned integer range. */
internal val UINT4_RANGE = 0..UINT4_MAX_VALUE

/** 8-bit unsigned integer (UByte) maximum value. */
private const val UBYTE_MAX_VALUE = 255 // 2^8-1

/** 8-bit unsigned integer (UByte) range. */
internal val UBYTE_RANGE = 0..UBYTE_MAX_VALUE

/** 16-bit unsigned integer (UShort) maximum value. */
private const val USHORT_MAX_VALUE = 65_535 // 2^16-1

/** 16-bit unsigned integer (UShort) range. */
internal val USHORT_RANGE = 0..USHORT_MAX_VALUE

/** 32-bit unsigned integer (UInt) maximum value. */
internal const val UINT_MAX_VALUE = 4_294_967_295 // 2^32-1

/** 32-bit unsigned integer (UInt) range. */
internal val UINT_RANGE = 0..UINT_MAX_VALUE
