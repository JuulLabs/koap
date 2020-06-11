package com.juul.koap

internal fun Long.requireUShort() {
    require(fitsInUShort()) { "$this is outside allowable range of $USHORT_RANGE" }
}

internal fun Long.requireUInt() {
    require(fitsInUInt()) { "$this is outside allowable range of $UINT_RANGE" }
}

internal fun ByteArray.requireLength(range: IntRange) {
    require(size in range) { "Length of $size is not within required range of $range" }
}

internal fun String.requireLength(range: IntRange) {
    require(length in range) { "Length of $length is not within required range of $range" }
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
