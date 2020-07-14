package multiplatform

expect object ObjectHasherFactory {
    fun createObjectHasher(): ObjectHasher
}

interface ObjectHasher {
    fun hash(vararg values: Any): Int
}