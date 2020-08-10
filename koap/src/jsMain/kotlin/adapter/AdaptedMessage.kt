package com.juul.koap.adapter

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@ExperimentalStdlibApi
@Serializable
data class AdaptedMessage(
    val messageType: MessageType,
    val type: String = "",
    val code: AdaptedCode,
    val id: Int = 0,
    val token: Long,
    val options: List<AdaptedOptions> = emptyList(),
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
sealed class AdaptedCode {
    sealed class Method : AdaptedCode() {
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

    sealed class Response : AdaptedCode() {
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
    ) : AdaptedCode()
}

@Serializable
@SerialName("options")
data class AdaptedOptions(
    val type: AdaptedOptionsType,
    val value: String = ""
) {
    enum class AdaptedOptionsType {
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
