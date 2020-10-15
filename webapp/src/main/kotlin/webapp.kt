package com.juul.koap

import com.juul.koap.Message.Option.ContentFormat
import com.juul.koap.Message.Option.ContentFormat.Companion.CBOR
import com.juul.koap.Message.Option.ContentFormat.Companion.JSON
import com.juul.koap.Message.Option.ContentFormat.Companion.PlainText
import com.juul.koap.Message.Tcp
import com.juul.koap.Message.Udp
import com.juul.koap.serialization.TcpMessageSerializer
import com.juul.koap.serialization.UdpMessageSerializer
import com.juul.koap.serialization.hex
import kotlinx.serialization.json.Json
import okio.ByteString.Companion.decodeHex

private val json = Json {
    prettyPrint = true
}

/**
 * Outputs result of decoding input [hex] to message (UDP and TCP).
 *
 * The output [String] includes:
 * - Message type
 * - JSON representation of message header
 * - Payload (displayed as either text, JSON, hex)
 *
 * Based on the presence of [ContentFormat] in [Message.options], the [ByteArray] payload will be decoded as follows:
 *
 * | `ContentFormat` | Decoded to...      |
 * |-----------------|--------------------|
 * | [PlainText]     | Plain text         |
 * | [JSON]          | Pretty-print JSON  |
 * | Other           | Hex representation |
 *
 * If a failure occurs while generating output, failure message is included in the final output.
 */
@JsExport
fun decode(hex: String?): String {
    if (hex.isNullOrEmpty()) return ""

    val bytes = try {
        hex.trim().replace(" ", "").decodeHex().toByteArray()
    } catch (t: Throwable) {
        return t.message ?: "Failed to parse hex input"
    }

    val udp = decode<Udp>(bytes)
    val tcp = decode<Tcp>(bytes)

    return if (udp == tcp) {
        udp
    } else {
        buildString {
            appendLine("<h1>UDP</h1>")
            appendLine(udp)
            appendLine()
            appendLine("<h1>TCP</h1>")
            appendLine(tcp)
        }
    }
}

private inline fun <reified T : Message> decode(bytes: ByteArray): String = try {
    parse(bytes.decode<T>())
} catch (t: Throwable) {
    t.message ?: "Failed to parse message"
}

private fun parse(message: Message) = buildString {
    val header = runCatching {
        when (message) {
            is Tcp -> json.encodeToString(TcpMessageSerializer, message)
            is Udp -> json.encodeToString(UdpMessageSerializer, message)
        }
    }.getOrElse { cause -> cause.message ?: "Failed to encode message to JSON" }
    appendLine("<b>Message:</b>")
    appendLine(header)
    appendLine()

    val payload = runCatching {
        when (message.options.lastOrNull { it is ContentFormat }) {
            PlainText -> "<b>Payload:</b>\n${message.payload.decodeToString()}"
            JSON -> "<b>Payload (JSON):</b>\n${message.payload.decodeToString().prettyPrintJson()}"
            CBOR -> "<b>Payload (CBOR):</b>\n${message.payload.hex()}"
            else -> "<b>Payload (Binary):</b>\n${message.payload.hex()}"
        }
    }.getOrElse { cause -> cause.message ?: "Failed to parse payload" }
    appendLine(payload)
}

private fun String.prettyPrintJson(indent: Int = 2): String =
    kotlin.js.JSON.stringify(kotlin.js.JSON.parse(this), null, indent)
