package com.juul.koap.multiplatform

actual object StringFormatterFactory {
    actual fun createMyStringFormatterFactory(): StringFormatter = JsStringFormatter
}

object JsStringFormatter: StringFormatter {
    override fun format(format: String, vararg args: Any?): String {
//        TODO( "Javascript String.format() equivalent")
        return args?.contentToString()
    }
}