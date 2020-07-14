package com.juul.koap

import multiplatform.StringFormatterFactory

internal fun ByteArray.toHexString(): String {
    return joinToString(" ") {
        StringFormatterFactory.createMyStringFormatterFactory().format("%02X", it)
    }
}
