package com.juul.koap

import com.juul.koap.multiplatform.StringFormatterFactory

internal fun ByteArray.toHexString(): String {
    return joinToString(" ") {
        StringFormatterFactory.createMyStringFormatterFactory().format("%02X", it)
    }
}
