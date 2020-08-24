package com.juul.koap.adapter

import com.juul.koap.Message
import kotlinx.serialization.json.Json
import com.juul.koap.adapter.Message as AdaptedMessage

private val json = Json {
    prettyPrint = true
    classDiscriminator = "#type"
    encodeDefaults = false
}

fun Message.toJson() = json.encodeToString(AdaptedMessage.serializer(), this.toAdapted())

fun String.toMessage() = json.decodeFromString(AdaptedMessage.serializer(), this).toMessage()
