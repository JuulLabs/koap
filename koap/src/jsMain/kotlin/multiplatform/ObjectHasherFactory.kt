package com.juul.koap.multiplatform

actual object ObjectHasherFactory {
    actual fun createObjectHasher(): ObjectHasher = JsObjectHasher
}

object JsObjectHasher: ObjectHasher {
    override fun hash(vararg values: Any): String {
//    override fun hash(vararg values: Any): Int {
        println("values: $values")
        return multiplatform.hash(values)
    }
}
