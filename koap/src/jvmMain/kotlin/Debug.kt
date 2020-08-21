package com.juul.koap

internal actual fun Byte.toHexString(): String =
    String.format("%02X", this)
