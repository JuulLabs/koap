package com.juul.koap

import java.util.Objects

actual fun createHash(vararg values: Any): String = Objects.hash(values).toString()
