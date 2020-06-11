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

