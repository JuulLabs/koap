package com.juul.koap.multiplatform

expect object ObjectHasherFactory {
    fun createObjectHasher(): ObjectHasher
}

interface ObjectHasher {
    fun hash(vararg values: Any): String
//    fun hash(vararg values: Any): Int
}