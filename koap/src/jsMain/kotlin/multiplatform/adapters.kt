package multiplatform

@JsModule("object-hash")
@JsNonModule
external fun <T> hash(value: T): String
