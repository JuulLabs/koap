package com.juul.koap

// Borrowed from HexConverter.kt in the kotlinx.serialization library
internal fun parseHexBinary(hex: String): ByteArray {
    val len = hex.length
    require(len % 2 == 0) { "Hex binary string must be even length" }
    val bytes = ByteArray(len / 2)
    var i = 0

    while (i < len) {
        val h = hexToInt(hex[i])
        val l = hexToInt(hex[i + 1])
        require(!(h == -1 || l == -1)) { "Invalid hex chars: ${hex[i]}${hex[i + 1]}" }

        bytes[i / 2] = ((h shl 4) + l).toByte()
        i += 2
    }

    return bytes
}

// Borrowed from HexConverter.kt in the kotlinx.serialization library
private fun hexToInt(character: Char): Int = when (character) {
    in '0'..'9' -> character - '0'
    in 'A'..'F' -> character - 'A' + 10
    in 'a'..'f' -> character - 'a' + 10
    else -> -1
}
