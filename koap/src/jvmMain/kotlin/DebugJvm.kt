package com.juul.koap

actual fun formatString(format: String, vararg args: Any?): String = String.format(format, args)
