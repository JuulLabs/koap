package com.juul.koap

internal fun Long.requireUShort(): Long {
    require(fitsInUShort()) { "$this is outside allowable range of $USHORT_RANGE" }
    return this
}

internal fun Long.requireUInt(): Long {
    require(fitsInUInt()) { "$this is outside allowable range of $UINT_RANGE" }
    return this
}

internal fun ByteArray.requireLength(range: IntRange): ByteArray {
    require(size in range) { "Length of $size is not within required range of $range" }
    return this
}

internal fun String.requireLength(range: IntRange): String {
    require(length in range) { "Length of $length is not within required range of $range" }
    return this
}

internal fun checkTokenSize(size: Long) {
    require(size in UINT4_RANGE) {
        "Token size of $size is outside allowable range of $UINT4_RANGE"
    }
}

internal fun checkContentSize(length: Long) {
    require(length <= UINT32_MAX_EXTENDED_LENGTH) {
        "Content length $length exceeds maximum allowable of $UINT32_MAX_EXTENDED_LENGTH"
    }
}
