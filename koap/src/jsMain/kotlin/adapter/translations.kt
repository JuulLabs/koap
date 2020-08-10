package com.juul.koap.adapter

import com.juul.koap.Message
import kotlinx.serialization.json.Json

private val json = Json {
    prettyPrint = true
    classDiscriminator = "#type"
    encodeDefaults = false
}

@ExperimentalStdlibApi
fun Message.toJson() = json.stringify(AdaptedMessage.serializer(), this.toAdapted())

@ExperimentalStdlibApi
fun String.toMessage() = json.parse(AdaptedMessage.serializer(), this).toMessage()
