package com.juul.koap

internal fun ByteArray.toHexString(): String = joinToString(" ") { String.format("%02X", it) }

internal fun Int.toHexString(byteCount: Int = Int.SIZE_BYTES): String =
    toLong().toHexString(byteCount)

internal fun Long.toHexString(byteCount: Int = Long.SIZE_BYTES): String {
    val hex = mutableListOf<String>()
    for (i in 0 until byteCount) {
        val byte = ((this shr (i * Byte.SIZE_BITS)) and 0xFF).toByte()
        hex += String.format("%02X", byte)
    }
    hex.reverse()
    return hex.joinToString(" ")
}

internal fun Int?.debugString(byteCount: Int = Int.SIZE_BYTES): String? =
    this?.let { "$it (${it.toHexString(byteCount)})" }

internal fun Long?.debugString(byteCount: Int = Long.SIZE_BYTES): String? =
    this?.let { "$it (${it.toHexString(byteCount)})" }
