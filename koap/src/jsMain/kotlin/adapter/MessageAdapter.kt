package com.juul.koap.adapter

import com.juul.koap.Message
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@ExperimentalStdlibApi
@Serializable
data class MessageAdapter(
    val messageType: String = "",
    val type: String = "",
    val code: CodeAdapter,
    val id: Int = 0,
    val token: Long = 0,
    val options: List<OptionsAdapter> = emptyList(),
    val payload: String
)

@Serializable
@SerialName("code")
data class CodeAdapter(
    val type: CodeAdapterType,
    val value: String
) {
    enum class CodeAdapterType {
        METHOD,
        RESPONSE,
        RAW
    }
}

private fun CodeAdapter.toCode(): Message.Code {
    return when (type) {
        CodeAdapter.CodeAdapterType.METHOD -> {
            when (value) {
                "GET" -> Message.Code.Method.GET
                "POST" -> Message.Code.Method.POST
                "PUT" -> Message.Code.Method.PUT
                "DELETE" -> Message.Code.Method.DELETE
                else -> Message.Code.Method.GET // TODO: wrong
            }
        }
        CodeAdapter.CodeAdapterType.RESPONSE -> Message.Code.Response.Created
        CodeAdapter.CodeAdapterType.RAW -> Message.Code.Raw(0, 2) // TODO: handle RAW better use Json parametric polymorphic deserialization
    }
}

private fun Message.Code.toAdapter(): CodeAdapter {
    return when (this) {
        is Message.Code.Method -> CodeAdapter(
            CodeAdapter.CodeAdapterType.METHOD,
            this::class.simpleName!!
        )
        is Message.Code.Response -> CodeAdapter(
            CodeAdapter.CodeAdapterType.RESPONSE,
            this::class.simpleName!!
        )
        is Message.Code.Raw -> CodeAdapter(
            CodeAdapter.CodeAdapterType.RAW,
            this::class.simpleName!!
        )
    }
}

@Serializable
@SerialName("options")
data class OptionsAdapter(
    val type: OptionsAdapterType,
    val value: String
) {
    enum class OptionsAdapterType {
        UriHost,
        UriPort,
        UriPath,
        UriQuery,
        ProxyUri,
        ProxyScheme,
        ContentFormat,
        Accept,
        MaxAge,
        ETag,
        LocationPath,
        LocationQuery,
        IfMatch,
        IfNoneMatch,
        Size1,
        Observe
    }
}

@ExperimentalStdlibApi
fun OptionsAdapter.toOptions(): Message.Option {
    return when (type) {
        OptionsAdapter.OptionsAdapterType.UriHost -> Message.Option.UriHost(value)
        OptionsAdapter.OptionsAdapterType.UriPort -> Message.Option.UriPort(value.toLong())
        OptionsAdapter.OptionsAdapterType.UriPath -> Message.Option.UriPath(value)
        OptionsAdapter.OptionsAdapterType.UriQuery -> Message.Option.UriQuery(value)
        OptionsAdapter.OptionsAdapterType.ProxyUri -> Message.Option.ProxyUri(value)
        OptionsAdapter.OptionsAdapterType.ProxyScheme -> Message.Option.ProxyScheme(value)
        OptionsAdapter.OptionsAdapterType.ContentFormat -> Message.Option.ContentFormat(value.toLong())
        OptionsAdapter.OptionsAdapterType.Accept -> Message.Option.Accept(value.toLong())
        OptionsAdapter.OptionsAdapterType.MaxAge -> Message.Option.MaxAge(value.toLong())
        OptionsAdapter.OptionsAdapterType.ETag -> Message.Option.ETag(value.encodeToByteArray())
        OptionsAdapter.OptionsAdapterType.LocationPath -> Message.Option.LocationPath(value)
        OptionsAdapter.OptionsAdapterType.LocationQuery -> Message.Option.LocationQuery(value)
        OptionsAdapter.OptionsAdapterType.IfMatch -> Message.Option.IfMatch(value.encodeToByteArray())
        OptionsAdapter.OptionsAdapterType.IfNoneMatch -> Message.Option.IfNoneMatch
        OptionsAdapter.OptionsAdapterType.Size1 -> Message.Option.Size1(value.toLong())
        OptionsAdapter.OptionsAdapterType.Observe -> Message.Option.Observe(value.toLong())
    }
}

@ExperimentalStdlibApi
fun Message.Option.toAdapter(): OptionsAdapter {
    return when (this) {
        is Message.Option.UriHost -> OptionsAdapter(OptionsAdapter.OptionsAdapterType.UriHost, uri)
        is Message.Option.UriPort -> OptionsAdapter(OptionsAdapter.OptionsAdapterType.UriPort, port.toString())
        is Message.Option.UriPath -> OptionsAdapter(OptionsAdapter.OptionsAdapterType.UriPath, uri)
        is Message.Option.UriQuery -> OptionsAdapter(OptionsAdapter.OptionsAdapterType.UriQuery, uri)
        is Message.Option.ProxyUri -> OptionsAdapter(OptionsAdapter.OptionsAdapterType.ProxyUri, uri)
        is Message.Option.ProxyScheme -> OptionsAdapter(OptionsAdapter.OptionsAdapterType.ProxyScheme, uri)
        is Message.Option.ContentFormat -> OptionsAdapter(OptionsAdapter.OptionsAdapterType.ContentFormat, format.toString())
        is Message.Option.Accept -> OptionsAdapter(OptionsAdapter.OptionsAdapterType.Accept, format.toString())
        is Message.Option.MaxAge -> OptionsAdapter(OptionsAdapter.OptionsAdapterType.MaxAge, seconds.toString())
        is Message.Option.ETag -> OptionsAdapter(OptionsAdapter.OptionsAdapterType.ETag, etag.decodeToString())
        is Message.Option.LocationPath -> OptionsAdapter(OptionsAdapter.OptionsAdapterType.LocationPath, uri)
        is Message.Option.LocationQuery -> OptionsAdapter(OptionsAdapter.OptionsAdapterType.LocationQuery, uri)
        is Message.Option.IfMatch -> OptionsAdapter(OptionsAdapter.OptionsAdapterType.IfMatch, etag.decodeToString())
        is Message.Option.IfNoneMatch -> OptionsAdapter(OptionsAdapter.OptionsAdapterType.IfNoneMatch, "")
        is Message.Option.Size1 -> OptionsAdapter(OptionsAdapter.OptionsAdapterType.Size1, bytes.toString())
        is Message.Option.Observe -> OptionsAdapter(OptionsAdapter.OptionsAdapterType.Observe, value.toString())
        else -> error("error calling Message.Option.toAdapter()")
    }
}

private val json = Json {
    prettyPrint = true
}

@ExperimentalStdlibApi
fun Message.toJson() = json.stringify(MessageAdapter.serializer(), this.toAdapter())

@ExperimentalStdlibApi
private fun Message.toAdapter(): MessageAdapter {
    return when (this) {
        is Message.Tcp -> {
            MessageAdapter(
                messageType = "TCP",
                code = code.toAdapter(),
                token = token,
                options = options.map { it.toAdapter() },
                payload = payload.decodeToString()
            )
        }
        is Message.Udp -> {
            MessageAdapter(
                messageType = "UDP",
                code = code.toAdapter(),
                type = type.name,
                id = id,
                token = token,
                options = options.map { it.toAdapter() },
                payload = payload.decodeToString()
            )
        }
    }
}

@ExperimentalStdlibApi
fun String.toMessage() = json.parse(MessageAdapter.serializer(), this).toMessage()

@ExperimentalStdlibApi
fun MessageAdapter.toMessage(): Message? {
    return when (messageType) {
        "TCP" -> Message.Tcp(
            code = code.toCode(),
            token = token,
            options = options.map { it.toOptions() },
            payload = payload.encodeToByteArray()
        )
        "UDP" -> Message.Udp(
            type = Message.Udp.Type.valueOf(type),
            code = code.toCode(),
            id = id,
            token = token,
            options = options.map { it.toOptions() },
            payload = payload.encodeToByteArray()
        )
        else -> null // TODO: fix this, it's wrong
    }
}

//{
//    "#type": "UDP",
//    "type": "Confirmable",
//    "code": {
//    "#type": "GET"
//},
//    "id": 65261,
//    "token": 51966,
//    "options": [
//    {
//        "#type": "UriPath",
//        "uri": "example"
//    }
//    ],
//    "payload": [
//    ]
//}