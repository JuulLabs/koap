package com.juul.koap.adapter

import com.juul.koap.Message

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

@ExperimentalStdlibApi
private fun Message.Option.toAdapted(): AdaptedOptions = when (this) {
    is Message.Option.UriHost -> AdaptedOptions(AdaptedOptions.AdaptedOptionsType.UriHost, uri)
    is Message.Option.UriPort -> AdaptedOptions(
        AdaptedOptions.AdaptedOptionsType.UriPort,
        port.toString()
    )
    is Message.Option.UriPath -> AdaptedOptions(AdaptedOptions.AdaptedOptionsType.UriPath, uri)
    is Message.Option.UriQuery -> AdaptedOptions(AdaptedOptions.AdaptedOptionsType.UriQuery, uri)
    is Message.Option.ProxyUri -> AdaptedOptions(AdaptedOptions.AdaptedOptionsType.ProxyUri, uri)
    is Message.Option.ProxyScheme -> AdaptedOptions(
        AdaptedOptions.AdaptedOptionsType.ProxyScheme,
        uri
    )
    is Message.Option.ContentFormat -> AdaptedOptions(
        AdaptedOptions.AdaptedOptionsType.ContentFormat,
        format.toString()
    )
    is Message.Option.Accept -> AdaptedOptions(
        AdaptedOptions.AdaptedOptionsType.Accept,
        format.toString()
    )
    is Message.Option.MaxAge -> AdaptedOptions(
        AdaptedOptions.AdaptedOptionsType.MaxAge,
        seconds.toString()
    )
    is Message.Option.ETag -> AdaptedOptions(
        AdaptedOptions.AdaptedOptionsType.ETag,
        etag.decodeToString()
    )
    is Message.Option.LocationPath -> AdaptedOptions(
        AdaptedOptions.AdaptedOptionsType.LocationPath,
        uri
    )
    is Message.Option.LocationQuery -> AdaptedOptions(
        AdaptedOptions.AdaptedOptionsType.LocationQuery,
        uri
    )
    is Message.Option.IfMatch -> AdaptedOptions(
        AdaptedOptions.AdaptedOptionsType.IfMatch,
        etag.decodeToString()
    )
    is Message.Option.IfNoneMatch -> AdaptedOptions(
        AdaptedOptions.AdaptedOptionsType.IfNoneMatch,
        ""
    )
    is Message.Option.Size1 -> AdaptedOptions(
        AdaptedOptions.AdaptedOptionsType.Size1,
        bytes.toString()
    )
    is Message.Option.Observe -> AdaptedOptions(
        AdaptedOptions.AdaptedOptionsType.Observe,
        value.toString()
    )
    else -> error("error calling Message.Option.toAdapter()")
}

@ExperimentalStdlibApi
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
