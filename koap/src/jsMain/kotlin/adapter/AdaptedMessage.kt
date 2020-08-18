package com.juul.koap.adapter

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@ExperimentalStdlibApi
@Serializable
data class Message (
    val messageType: MessageType,
    val type: String = "",
    val code: Code,
    val id: Int = 0,
    val token: Long,
    val options: List<Option> = emptyList(),
    val payload: String
)

@Serializable
@SerialName("messageType")
enum class MessageType {
    UDP,
    TCP
}

@Serializable
@SerialName("code")
sealed class Code {
    sealed class Method : Code() {
        @Serializable
        @SerialName("GET")
        object GET : Method()

        @Serializable
        @SerialName("POST")
        object POST : Method()

        @Serializable
        @SerialName("PUT")
        object PUT : Method()

        @Serializable
        @SerialName("DELETE")
        object DELETE : Method()
    }

    sealed class Response : Code() {
        @Serializable
        @SerialName("Created")
        object Created : Response()

        @Serializable
        @SerialName("Deleted")
        object Deleted : Response()

        @Serializable
        @SerialName("Valid")
        object Valid : Response()

        @Serializable
        @SerialName("Changed")
        object Changed : Response()

        @Serializable
        @SerialName("Content")
        object Content : Response()

        @Serializable
        @SerialName("BadRequest")
        object BadRequest : Response()

        @Serializable
        @SerialName("Unauthorized")
        object Unauthorized : Response()

        @Serializable
        @SerialName("BadOption")
        object BadOption : Response()

        @Serializable
        @SerialName("Forbidden")
        object Forbidden : Response()

        @Serializable
        @SerialName("NotFound")
        object NotFound : Response()

        @Serializable
        @SerialName("MethodNotAllowed")
        object MethodNotAllowed : Response()

        @Serializable
        @SerialName("NotAcceptable")
        object NotAcceptable : Response()

        @Serializable
        @SerialName("PreconditionFailed")
        object PreconditionFailed : Response()

        @Serializable
        @SerialName("RequestEntityTooLarge")
        object RequestEntityTooLarge : Response()

        @Serializable
        @SerialName("UnsupportedContentFormat")
        object UnsupportedContentFormat : Response()

        @Serializable
        @SerialName("InternalServerError")
        object InternalServerError : Response()

        @Serializable
        @SerialName("NotImplemented")
        object NotImplemented : Response()

        @Serializable
        @SerialName("BadGateway")
        object BadGateway : Response()

        @Serializable
        @SerialName("ServiceUnavailable")
        object ServiceUnavailable : Response()

        @Serializable
        @SerialName("GatewayTimeout")
        object GatewayTimeout : Response()

        @Serializable
        @SerialName("ProxyingNotSupported")
        object ProxyingNotSupported : Response()

    }

    @Serializable
    @SerialName("Raw")
    data class Raw(
        val `class`: Int,
        val detail: Int
    ) : Code()
}

@Serializable
@SerialName("options")
data class Option (
    val type: OptionType,
    val value: String = ""
) {
    enum class OptionType {
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
