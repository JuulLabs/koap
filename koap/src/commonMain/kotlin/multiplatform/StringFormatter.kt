package com.juul.koap.multiplatform

expect object StringFormatterFactory {
    fun createMyStringFormatterFactory(): StringFormatter
}

interface StringFormatter {
    fun format(format: String, vararg args: Any?): String
}