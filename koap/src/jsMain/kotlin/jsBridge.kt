package com.juul.koap

import com.juul.koap.adapter.toJson
import com.juul.koap.adapter.toMessage

@JsModule("object-hash")
@JsNonModule
external fun <T> hash(value: T): String

@ExperimentalStdlibApi
@JsName("encodeFromJson")
fun encodeFromJson(json: String): String = try {
    json.toMessage().encode().toHexString()
} catch(e: Exception) {
    e.message!!
}

@ExperimentalStdlibApi
@JsName("decodeToUdp")
fun decodeToUdp(encodedHex: String): String {
    val input = encodedHex.replace(" ", "")
    return parseHexBinary(input).decodeUdp().toJson()
}

@ExperimentalStdlibApi
@JsName("decodeToTcp")
fun decodeToTcp(encodedHex: String): String {
    val input = encodedHex.replace(" ", "")
    return parseHexBinary(input).decodeTcp().toJson()
}
