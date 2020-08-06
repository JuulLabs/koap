package com.juul.koap.multiplatform

actual fun shiftRight(num: Int, bits: Int): Int = js("num << bits") as Int
