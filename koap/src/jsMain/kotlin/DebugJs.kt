package com.juul.koap

import com.juul.koap.multiplatform.LoggerFactory

val logger = LoggerFactory.createLogger()

actual fun formatString(format: String, vararg args: Any?): String {
    // TODO: Javascript String.format() equivalent
    logger.logMessage("format: $format");
    for ((i, arg) in args.withIndex()) {
        logger.logMessage("Arg $i: $arg")
    }
    return args.contentToString()
}

//TODO:ahobbs call to JS module for toHexList() with JS native bitwise operations?
actual fun Long.toHexList(byteCount: Int): List<String> {
    return emptyList()
}

//= ((byteCount - 1) downTo 0).map { i ->
//    val byte = ((this shr (i * Byte.SIZE_BITS)) and 0xFF).toByte()
//    formatString("%02X", byte)
//}