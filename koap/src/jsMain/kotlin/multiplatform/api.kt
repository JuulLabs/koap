package com.juul.koap.multiplatform

import com.juul.koap.adapter.toJson
import com.juul.koap.adapter.toMessage
import com.juul.koap.decodeTcp
import com.juul.koap.decodeUdp
import com.juul.koap.encode
import com.juul.koap.parseHexBinary
import com.juul.koap.toHexString

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
fun decodeToUdp(encodedHex: String) {
    val input = encodedHex.replace(" ", "")
    parseHexBinary(input).decodeUdp().toJson()
}

@ExperimentalStdlibApi
@JsName("decodeToTcp")
fun decodeToTcp(encodedHex: String) {
    val input = encodedHex.replace(" ", "")
    parseHexBinary(input).decodeTcp().toJson()
}
