package com.juul.koap.adapter

import com.juul.koap.Message

@ExperimentalStdlibApi
private fun AdaptedOptions.toOptions(): Message.Option = when (type) {
        AdaptedOptions.AdaptedOptionsType.UriHost -> Message.Option.UriHost(value)
        AdaptedOptions.AdaptedOptionsType.UriPort -> Message.Option.UriPort(value.toLong())
        AdaptedOptions.AdaptedOptionsType.UriPath -> Message.Option.UriPath(value)
        AdaptedOptions.AdaptedOptionsType.UriQuery -> Message.Option.UriQuery(value)
        AdaptedOptions.AdaptedOptionsType.ProxyUri -> Message.Option.ProxyUri(value)
        AdaptedOptions.AdaptedOptionsType.ProxyScheme -> Message.Option.ProxyScheme(value)
        AdaptedOptions.AdaptedOptionsType.ContentFormat -> Message.Option.ContentFormat(value.toLong())
        AdaptedOptions.AdaptedOptionsType.Accept -> Message.Option.Accept(value.toLong())
        AdaptedOptions.AdaptedOptionsType.MaxAge -> Message.Option.MaxAge(value.toLong())
        AdaptedOptions.AdaptedOptionsType.ETag -> Message.Option.ETag(value.encodeToByteArray())
        AdaptedOptions.AdaptedOptionsType.LocationPath -> Message.Option.LocationPath(value)
        AdaptedOptions.AdaptedOptionsType.LocationQuery -> Message.Option.LocationQuery(value)
        AdaptedOptions.AdaptedOptionsType.IfMatch -> Message.Option.IfMatch(value.encodeToByteArray())
        AdaptedOptions.AdaptedOptionsType.IfNoneMatch -> Message.Option.IfNoneMatch
        AdaptedOptions.AdaptedOptionsType.Size1 -> Message.Option.Size1(value.toLong())
        AdaptedOptions.AdaptedOptionsType.Observe -> Message.Option.Observe(value.toLong())
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

@ExperimentalStdlibApi
internal fun AdaptedMessage.toMessage(): Message = when (messageType) {
        MessageType.TCP -> Message.Tcp(
            code = code.toCode(),
            token = token,
            options = options.map { it.toOptions() },
            payload = payload.encodeToByteArray()
        )
        MessageType.UDP -> Message.Udp(
            type = Message.Udp.Type.valueOf(type),
            code = code.toCode(),
            id = id,
            token = token,
            options = options.map { it.toOptions() },
            payload = payload.encodeToByteArray()
        )
    }
