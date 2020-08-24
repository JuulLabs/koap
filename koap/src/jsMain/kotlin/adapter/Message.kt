package com.juul.koap.adapter

import com.juul.koap.Message
import com.juul.koap.adapter.Message as AdaptedMessage
import com.juul.koap.adapter.Code as AdaptedCode
import com.juul.koap.adapter.Option as AdaptedOption
import com.juul.koap.adapter.Option.OptionType as AdaptedOptionType


internal fun Message.toAdapted(): AdaptedMessage = when (this) {
    is Message.Tcp -> {
        AdaptedMessage(
            messageType = MessageType.TCP,
            code = code.toAdapted(),
            token = token,
            options = options.map { it.toAdapted() },
            payload = payload.decodeToString()
        )
    }
    is Message.Udp -> {
        AdaptedMessage(
            messageType = MessageType.UDP,
            code = code.toAdapted(),
            type = type.name,
            id = id,
            token = token,
            options = options.map { it.toAdapted() },
            payload = payload.decodeToString()
        )
    }
}

internal fun AdaptedMessage.toMessage(): Message = when (messageType) {
    MessageType.TCP -> Message.Tcp(
        code = code.toCode(),
        token = token,
        options = options.map { it.toOption() },
        payload = payload.encodeToByteArray()
    )
    MessageType.UDP -> Message.Udp(
        type = Message.Udp.Type.valueOf(type),
        code = code.toCode(),
        id = id,
        token = token,
        options = options.map { it.toOption() },
        payload = payload.encodeToByteArray()
    )
}

private fun Message.Code.toAdapted(): AdaptedCode = when (this) {
    is Message.Code.Method.GET -> AdaptedCode.Method.GET
    is Message.Code.Method.POST -> AdaptedCode.Method.POST
    is Message.Code.Method.PUT -> AdaptedCode.Method.PUT
    is Message.Code.Method.DELETE -> AdaptedCode.Method.DELETE
    is Message.Code.Response.Created -> AdaptedCode.Response.Created
    is Message.Code.Response.Deleted -> AdaptedCode.Response.Deleted
    is Message.Code.Response.Valid -> AdaptedCode.Response.Valid
    is Message.Code.Response.Changed -> AdaptedCode.Response.Changed
    is Message.Code.Response.Content -> AdaptedCode.Response.Content
    is Message.Code.Response.BadRequest -> AdaptedCode.Response.BadRequest
    is Message.Code.Response.Unauthorized -> AdaptedCode.Response.Unauthorized
    is Message.Code.Response.BadOption -> AdaptedCode.Response.BadOption
    is Message.Code.Response.Forbidden -> AdaptedCode.Response.Forbidden
    is Message.Code.Response.NotFound -> AdaptedCode.Response.NotFound
    is Message.Code.Response.MethodNotAllowed -> AdaptedCode.Response.MethodNotAllowed
    is Message.Code.Response.NotAcceptable -> AdaptedCode.Response.NotAcceptable
    is Message.Code.Response.PreconditionFailed -> AdaptedCode.Response.PreconditionFailed
    is Message.Code.Response.RequestEntityTooLarge -> AdaptedCode.Response.RequestEntityTooLarge
    is Message.Code.Response.UnsupportedContentFormat -> AdaptedCode.Response.UnsupportedContentFormat
    is Message.Code.Response.InternalServerError -> AdaptedCode.Response.InternalServerError
    is Message.Code.Response.NotImplemented -> AdaptedCode.Response.NotImplemented
    is Message.Code.Response.BadGateway -> AdaptedCode.Response.BadGateway
    is Message.Code.Response.ServiceUnavailable -> AdaptedCode.Response.ServiceUnavailable
    is Message.Code.Response.GatewayTimeout -> AdaptedCode.Response.GatewayTimeout
    is Message.Code.Response.ProxyingNotSupported -> AdaptedCode.Response.ProxyingNotSupported
    is Message.Code.Raw -> AdaptedCode.Raw(`class`, detail)
}

private fun AdaptedCode.toCode(): Message.Code = when (this) {
    is AdaptedCode.Method.GET -> Message.Code.Method.GET
    is AdaptedCode.Method.POST -> Message.Code.Method.POST
    is AdaptedCode.Method.PUT -> Message.Code.Method.PUT
    is AdaptedCode.Method.DELETE -> Message.Code.Method.DELETE
    is AdaptedCode.Response.Created -> Message.Code.Response.Created
    is AdaptedCode.Response.Deleted -> Message.Code.Response.Deleted
    is AdaptedCode.Response.Valid -> Message.Code.Response.Valid
    is AdaptedCode.Response.Changed -> Message.Code.Response.Changed
    is AdaptedCode.Response.Content -> Message.Code.Response.Content
    is AdaptedCode.Response.BadRequest -> Message.Code.Response.BadRequest
    is AdaptedCode.Response.Unauthorized -> Message.Code.Response.Unauthorized
    is AdaptedCode.Response.BadOption -> Message.Code.Response.BadOption
    is AdaptedCode.Response.Forbidden -> Message.Code.Response.Forbidden
    is AdaptedCode.Response.NotFound -> Message.Code.Response.NotFound
    is AdaptedCode.Response.MethodNotAllowed -> Message.Code.Response.MethodNotAllowed
    is AdaptedCode.Response.NotAcceptable -> Message.Code.Response.NotAcceptable
    is AdaptedCode.Response.PreconditionFailed -> Message.Code.Response.PreconditionFailed
    is AdaptedCode.Response.RequestEntityTooLarge -> Message.Code.Response.RequestEntityTooLarge
    is AdaptedCode.Response.UnsupportedContentFormat -> Message.Code.Response.UnsupportedContentFormat
    is AdaptedCode.Response.InternalServerError -> Message.Code.Response.InternalServerError
    is AdaptedCode.Response.NotImplemented -> Message.Code.Response.NotImplemented
    is AdaptedCode.Response.BadGateway -> Message.Code.Response.BadGateway
    is AdaptedCode.Response.ServiceUnavailable -> Message.Code.Response.ServiceUnavailable
    is AdaptedCode.Response.GatewayTimeout -> Message.Code.Response.GatewayTimeout
    is AdaptedCode.Response.ProxyingNotSupported -> Message.Code.Response.ProxyingNotSupported
    is AdaptedCode.Raw -> Message.Code.Raw(`class`, detail)
}

private fun Message.Option.toAdapted(): AdaptedOption = when (this) {
    is Message.Option.UriHost -> AdaptedOption(AdaptedOptionType.UriHost, uri)
    is Message.Option.UriPort -> AdaptedOption(
        AdaptedOptionType.UriPort,
        port.toString()
    )
    is Message.Option.UriPath -> AdaptedOption(AdaptedOptionType.UriPath, uri)
    is Message.Option.UriQuery -> AdaptedOption(AdaptedOptionType.UriQuery, uri)
    is Message.Option.ProxyUri -> AdaptedOption(AdaptedOptionType.ProxyUri, uri)
    is Message.Option.ProxyScheme -> AdaptedOption(
        AdaptedOptionType.ProxyScheme,
        uri
    )
    is Message.Option.ContentFormat -> AdaptedOption(
        AdaptedOptionType.ContentFormat,
        format.toString()
    )
    is Message.Option.Accept -> AdaptedOption(
        AdaptedOptionType.Accept,
        format.toString()
    )
    is Message.Option.MaxAge -> AdaptedOption(
        AdaptedOptionType.MaxAge,
        seconds.toString()
    )
    is Message.Option.ETag -> AdaptedOption(
        AdaptedOptionType.ETag,
        etag.decodeToString()
    )
    is Message.Option.LocationPath -> AdaptedOption(
        AdaptedOptionType.LocationPath,
        uri
    )
    is Message.Option.LocationQuery -> AdaptedOption(
        AdaptedOptionType.LocationQuery,
        uri
    )
    is Message.Option.IfMatch -> AdaptedOption(
        AdaptedOptionType.IfMatch,
        etag.decodeToString()
    )
    is Message.Option.IfNoneMatch -> AdaptedOption(
        AdaptedOptionType.IfNoneMatch,
        ""
    )
    is Message.Option.Size1 -> AdaptedOption(
        AdaptedOptionType.Size1,
        bytes.toString()
    )
    is Message.Option.Observe -> AdaptedOption(
        AdaptedOptionType.Observe,
        value.toString()
    )
    else -> error("error calling Message.Option.toAdapter()")
}

private fun AdaptedOption.toOption(): Message.Option = when (type) {
    AdaptedOptionType.UriHost -> Message.Option.UriHost(value)
    AdaptedOptionType.UriPort -> Message.Option.UriPort(value.toLong())
    AdaptedOptionType.UriPath -> Message.Option.UriPath(value)
    AdaptedOptionType.UriQuery -> Message.Option.UriQuery(value)
    AdaptedOptionType.ProxyUri -> Message.Option.ProxyUri(value)
    AdaptedOptionType.ProxyScheme -> Message.Option.ProxyScheme(value)
    AdaptedOptionType.ContentFormat -> Message.Option.ContentFormat(value.toLong())
    AdaptedOptionType.Accept -> Message.Option.Accept(value.toLong())
    AdaptedOptionType.MaxAge -> Message.Option.MaxAge(value.toLong())
    AdaptedOptionType.ETag -> Message.Option.ETag(value.encodeToByteArray())
    AdaptedOptionType.LocationPath -> Message.Option.LocationPath(value)
    AdaptedOptionType.LocationQuery -> Message.Option.LocationQuery(value)
    AdaptedOptionType.IfMatch -> Message.Option.IfMatch(value.encodeToByteArray())
    AdaptedOptionType.IfNoneMatch -> Message.Option.IfNoneMatch
    AdaptedOptionType.Size1 -> Message.Option.Size1(value.toLong())
    AdaptedOptionType.Observe -> Message.Option.Observe(value.toLong())
}
