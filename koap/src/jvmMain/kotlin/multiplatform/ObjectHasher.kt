package com.juul.koap.multiplatform

import java.util.*

actual object ObjectHasherFactory {
    actual fun createObjectHasher(): ObjectHasher = JvmObjectHasher
}

object JvmObjectHasher: ObjectHasher {
    override fun hash(vararg values: Any): String = Objects.hash(values).toString()
//    override fun hash(vararg values: Any): Int = Objects.hash(values)
}
