package com.juul.koap

import com.juul.koap.Message.Option
import com.juul.koap.Message.Option.Accept
import com.juul.koap.Message.Option.ContentFormat
import com.juul.koap.Message.Option.ETag
import com.juul.koap.Message.Option.Format
import com.juul.koap.Message.Option.Format.empty
import com.juul.koap.Message.Option.Format.opaque
import com.juul.koap.Message.Option.Format.string
import com.juul.koap.Message.Option.Format.uint
import com.juul.koap.Message.Option.IfMatch
import com.juul.koap.Message.Option.IfNoneMatch
import com.juul.koap.Message.Option.LocationPath
import com.juul.koap.Message.Option.LocationQuery
import com.juul.koap.Message.Option.MaxAge
import com.juul.koap.Message.Option.ProxyScheme
import com.juul.koap.Message.Option.ProxyUri
import com.juul.koap.Message.Option.Size1
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
        is UriPort -> uint(7, option.port)
        is LocationPath -> string(8, option.uri)
        is UriPath -> string(11, option.uri)
        is ContentFormat -> uint(12, option.format)
        is MaxAge -> uint(14, option.seconds)
        is UriQuery -> string(15, option.uri)
        is Accept -> uint(17, option.format)
        is LocationQuery -> string(20, option.uri)
        is ProxyUri -> string(35, option.uri)
        is ProxyScheme -> string(39, option.uri)
        is Size1 -> uint(60, option.bytes)
    }
