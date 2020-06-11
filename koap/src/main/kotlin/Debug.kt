package com.juul.koap

internal fun ByteArray.toHexString(): String = joinToString(" ") { String.format("%02X", it) }
