package com.juul.koap

import com.juul.koap.Message.Option
import com.juul.koap.Message.Option.Accept
import com.juul.koap.Message.Option.Block1
import com.juul.koap.Message.Option.Block2
import com.juul.koap.Message.Option.ContentFormat
import com.juul.koap.Message.Option.ETag
import com.juul.koap.Message.Option.Echo
import com.juul.koap.Message.Option.Edhoc
import com.juul.koap.Message.Option.Format
import com.juul.koap.Message.Option.Format.empty
import com.juul.koap.Message.Option.Format.opaque
import com.juul.koap.Message.Option.Format.string
import com.juul.koap.Message.Option.Format.uint
import com.juul.koap.Message.Option.HopLimit
import com.juul.koap.Message.Option.IfMatch
import com.juul.koap.Message.Option.IfNoneMatch
import com.juul.koap.Message.Option.LocationPath
import com.juul.koap.Message.Option.LocationQuery
import com.juul.koap.Message.Option.MaxAge
import com.juul.koap.Message.Option.NoResponse
import com.juul.koap.Message.Option.Observe
import com.juul.koap.Message.Option.Oscore
import com.juul.koap.Message.Option.ProxyScheme
import com.juul.koap.Message.Option.ProxyUri
import com.juul.koap.Message.Option.QBlock1
import com.juul.koap.Message.Option.QBlock2
import com.juul.koap.Message.Option.RequestTag
import com.juul.koap.Message.Option.Size1
import com.juul.koap.Message.Option.Size2
import com.juul.koap.Message.Option.UnknownOption
import com.juul.koap.Message.Option.UriHost
import com.juul.koap.Message.Option.UriPath
import com.juul.koap.Message.Option.UriPort
import com.juul.koap.Message.Option.UriQuery

/** Converts predefined [Option] receiver to raw [Option.Format]. */
internal fun Option.toFormat(): Format =
    when (val option = this) {
        is Format -> option
        is IfMatch -> opaque(1, option.etag)
        is UriHost -> string(3, option.uri)
        is ETag -> opaque(4, option.etag)
        is IfNoneMatch -> empty(5)
        is Observe -> uint(6, option.value)
        is UriPort -> uint(7, option.port)
        is LocationPath -> string(8, option.uri)
        is Oscore -> opaque(9, option.value)
        is UriPath -> string(11, option.uri)
        is ContentFormat -> uint(12, option.format)
        is MaxAge -> uint(14, option.seconds)
        is UriQuery -> string(15, option.uri)
        is HopLimit -> uint(16, option.hops)
        is Accept -> uint(17, option.format)
        is QBlock1 -> uint(19, option.intValue.toLong())
        is LocationQuery -> string(20, option.uri)
        is Edhoc -> empty(21)
        is Block2 -> uint(23, option.intValue.toLong())
        is Block1 -> uint(27, option.intValue.toLong())
        is Size2 -> uint(28, option.bytes)
        is QBlock2 -> uint(31, option.intValue.toLong())
        is ProxyUri -> string(35, option.uri)
        is ProxyScheme -> string(39, option.uri)
        is Size1 -> uint(60, option.bytes)
        is Echo -> opaque(252, option.value)
        is NoResponse -> uint(258, option.value)
        is RequestTag -> opaque(292, option.tag)
        is UnknownOption -> opaque(option.number, option.value)
    }
