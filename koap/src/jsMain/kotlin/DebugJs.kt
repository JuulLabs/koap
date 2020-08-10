package com.juul.koap

private val hexArray = charArrayOf(
    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
)

internal actual fun Byte.toHexString(): String {
    val left = hexArray[(0xF0 and this.toInt()) shr 4]
    val right = hexArray[0x0F and this.toInt()]
    return "$left$right"
}
