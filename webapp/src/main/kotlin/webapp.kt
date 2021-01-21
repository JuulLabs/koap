package com.juul.koap

import cbor.decodeFirstSync
import cbor.diagnose
import com.juul.koap.Message.Option.Accept
import com.juul.koap.Message.Option.ContentFormat
import com.juul.koap.Message.Tcp
import com.juul.koap.Message.Udp
import com.juul.koap.serialization.TcpMessageSerializer
import com.juul.koap.serialization.UdpMessageSerializer
import com.juul.koap.serialization.hex
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.await
import kotlinx.coroutines.promise
import kotlinx.serialization.json.Json
import okio.ByteString.Companion.decodeHex
import kotlin.js.Promise

private val json = Json {
    prettyPrint = true
    prettyPrintIndent = "  "
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
 * | `ContentFormat`      | Decoded to...      |
 * |----------------------|--------------------|
 * | [Encoding.PlainText] | Plain text         |
 * | [Encoding.JSON]      | Pretty-print JSON  |
 * | Other                | Hex representation |
 *
 * If a failure occurs while generating output, failure message is included in the final output.
 */
@JsExport
fun decode(hex: String?): Promise<String> = GlobalScope.promise {
    if (hex.isNullOrEmpty()) return@promise ""

    val bytes = try {
        val stripped = hex.replace(" ", "").replace("\n", "")
        console.info("Decoding: $stripped")
        stripped.decodeHex().toByteArray()
    } catch (t: Throwable) {
        console.error(t)
        return@promise t.message ?: "Failed to parse hex input"
    }

    val udp = decode<Udp>(bytes)
    val tcp = decode<Tcp>(bytes)

    if (udp == tcp) {
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

private suspend inline fun <reified T : Message> decode(bytes: ByteArray): String = try {
    parse(bytes.decode<T>())
} catch (t: Throwable) {
    console.error(t)
    t.message ?: "Failed to parse message"
}

private suspend fun parse(message: Message) = buildString {
    val header = runCatching {
        when (message) {
            is Tcp -> json.encodeToString(TcpMessageSerializer, message)
            is Udp -> json.encodeToString(UdpMessageSerializer, message)
        }
    }.getOrElse { cause ->
        console.error(cause)
        cause.message ?: "Failed to encode message to JSON"
    }
    appendLine("<b>Message:</b>")
    appendLine(header)
    appendLine()

    val encoding = message.detectEncoding()
    when (encoding) {
        Encoding.PlainText -> appendLine("<b>Payload:</b>")
        Encoding.JSON -> appendLine("<b>Payload (JSON):</b>")
        Encoding.CBOR -> appendLine("<b>Payload (CBOR):</b>")
        else -> appendLine("<b>Payload (Binary):</b>")
    }

    val payload = runCatching {
        when (encoding) {
            Encoding.PlainText -> message.payload.decodeToString()
            Encoding.JSON -> message.payload.decodeToString().prettyPrintJson()
            Encoding.CBOR -> message.payload.hex(separator = "").decodeCbor().await()
            else -> message.payload.hex()
        }
    }.getOrElse { cause ->
        console.error(cause)
        val hex = message.payload.hex()
        val description = cause.message ?: "Failed to parse payload"
        "$hex\n$description"
    }
    appendLine(payload)
}

private fun String.prettyPrintJson(indent: Int = 2): String =
    JSON.stringify(JSON.parse(this), null, indent)

private fun String.decodeCbor(): Promise<String> {
    val options = object {
        val encoding = "hex"
    }
    return diagnose(this, options)
}

enum class Encoding { PlainText, JSON, CBOR }

private fun Message.detectEncoding(): Encoding? {
    val format = (options.lastOrNull { it is ContentFormat } as? ContentFormat)?.format
        ?: (options.lastOrNull { it is Accept } as? Accept)?.format

    return when (format) {
        0L -> Encoding.PlainText
        50L -> Encoding.JSON
        60L -> Encoding.CBOR
        else -> null
    }
}
