package com.juul.koap.adapter

import com.juul.koap.Message
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import com.juul.koap.adapter.Message as AdaptedMessage

@UnstableDefault
private val json = Json {
    prettyPrint = true
    classDiscriminator = "#type"
    encodeDefaults = false
}

@ExperimentalStdlibApi
fun Message.toJson() = json.stringify(AdaptedMessage.serializer(), this.toAdapted())

@ExperimentalStdlibApi
fun String.toMessage() = json.parse(AdaptedMessage.serializer(), this).toMessage()
