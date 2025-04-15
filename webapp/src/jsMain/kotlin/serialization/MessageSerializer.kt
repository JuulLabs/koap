@file:UseSerializers(
    TypeSerializer::class,
    CodeSerializer::class,
    OptionSerializer::class,
)

package com.juul.koap.serialization

import com.juul.koap.Message
import com.juul.koap.Message.Code
import com.juul.koap.Message.Option
import com.juul.koap.Message.Option.ContentFormat.Companion.CBOR
import com.juul.koap.Message.Option.ContentFormat.Companion.EXI
import com.juul.koap.Message.Option.ContentFormat.Companion.JSON
import com.juul.koap.Message.Option.ContentFormat.Companion.LinkFormat
import com.juul.koap.Message.Option.ContentFormat.Companion.OctetStream
import com.juul.koap.Message.Option.ContentFormat.Companion.PlainText
import com.juul.koap.Message.Option.ContentFormat.Companion.XML
import com.juul.koap.Message.Udp.Type
import com.juul.koap.Message.Udp.Type.Acknowledgement
import com.juul.koap.Message.Udp.Type.Confirmable
import com.juul.koap.Message.Udp.Type.NonConfirmable
import com.juul.koap.Message.Udp.Type.Reset
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

private abstract class MessageSurrogate {

    abstract val code: Code
    abstract val token: Long
    abstract val options: List<Option>

    @Serializable
    data class Udp(
        val type: Type,
        override val code: Code,
        val id: Int,
        override val token: Long,
        override val options: List<Option>,
    ) : MessageSurrogate()

    @Serializable
    data class Tcp(
        override val code: Code,
        override val token: Long,
        override val options: List<Option>,
    ) : MessageSurrogate()
}

internal object UdpMessageSerializer : KSerializer<Message.Udp> {

    override val descriptor: SerialDescriptor = MessageSurrogate.Udp.serializer().descriptor

    override fun serialize(
        encoder: Encoder,
        value: Message.Udp
    ) {
        val surrogate = MessageSurrogate.Udp(
            type = value.type,
            code = value.code,
            id = value.id,
            token = value.token,
            options = value.options,
        )

        encoder.encodeSerializableValue(
            serializer = MessageSurrogate.Udp.serializer(),
            value = surrogate
        )
    }

    override fun deserialize(decoder: Decoder): Message.Udp = TODO("Not yet implemented")
}

internal object TcpMessageSerializer : KSerializer<Message.Tcp> {

    override val descriptor: SerialDescriptor = MessageSurrogate.Tcp.serializer().descriptor

    override fun serialize(
        encoder: Encoder,
        value: Message.Tcp
    ) {
        val surrogate = MessageSurrogate.Tcp(
            code = value.code,
            token = value.token,
            options = value.options,
        )

        encoder.encodeSerializableValue(
            serializer = MessageSurrogate.Tcp.serializer(),
            value = surrogate
        )
    }

    override fun deserialize(decoder: Decoder): Message.Tcp = TODO("Not yet implemented")
}

private object TypeSerializer : KSerializer<Type> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Type", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Type) {
        val serialized = when (value) {
            Confirmable -> "Confirmable"
            NonConfirmable -> "NonConfirmable"
            Acknowledgement -> "Acknowledgement"
            Reset -> "Reset"
        }
        encoder.encodeString(serialized)
    }

    override fun deserialize(decoder: Decoder): Type = TODO("Not yet implemented")
}

private object CodeSerializer : KSerializer<Code> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Code", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Code) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): Code = TODO("Not yet implemented")
}

internal object OptionSerializer : KSerializer<Option> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Option", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Option) {
        fun format(format: Long): String = when (format) {
            PlainText.format -> "text/plain; charset=utf-8"
            LinkFormat.format -> "application/link-format"
            XML.format -> "application/xml"
            OctetStream.format -> "application/octet-stream"
            EXI.format -> "application/exi"
            JSON.format -> "application/json"
            CBOR.format -> "application/cbor"
            else -> format.toString()
        }
        val serialized = when (value) {
            is Option.IfMatch -> "If-Match: " + value.etag.toHexString()
            is Option.UriHost -> "Uri-Host: " + value.uri
            is Option.ETag -> "ETag: " + value.etag.toHexString()
            is Option.IfNoneMatch -> "If-None-Match"
            is Option.UriPort -> "Uri-Port: " + value.port
            is Option.LocationPath -> "Location-Path: " + value.uri
            is Option.UriPath -> "Uri-Path: " + value.uri
            is Option.ContentFormat -> "Content-Format: " + format(value.format)
            is Option.MaxAge -> "Max-Age: " + value.seconds
            is Option.UriQuery -> "Uri-Query: " + value.uri
            is Option.HopLimit -> "Hop-Limit: " + value.hops
            is Option.Accept -> "Accept: " + format(value.format)
            is Option.LocationQuery -> "Location-Query: " + value.uri
            is Option.ProxyUri -> "Proxy-Uri: " + value.uri
            is Option.ProxyScheme -> "Proxy-Scheme: " + value.uri
            is Option.Size1 -> "Size1: " + value.bytes
            is Option.Echo -> "Echo: " + value.value.toHexString()
            is Option.NoResponse -> "No-Response: " + value.value
            is Option.RequestTag -> "Request-Tag: " + value.tag.toHexString()
            else -> value.toString()
        }
        encoder.encodeString(serialized)
    }

    override fun deserialize(decoder: Decoder): Option = TODO("Not yet implemented")
}
