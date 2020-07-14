package multiplatform

actual object ObjectHasherFactory {
    actual fun createObjectHasher(): ObjectHasher = JsObjectHasher
}

object JsObjectHasher: ObjectHasher {
    override fun hash(vararg values: Any): Int {
        TODO("Javascript hash function")
    }
}
