@file:JsExport

package com.juul.koap

import com.juul.koap.Message.Code.Method
import com.juul.koap.Message.Option

/*
    import * as koapModule from '@juullabs/koap/koap-koap'
    const koap = koapModule.com.juul.koap

    let msg = koap.tcpMessage(
        koap.Message.Code.Method.GET,
        66,
        [
            new koap.Message.Option.UriPath('dev'),
            new koap.Message.Option.UriQuery('abd')
        ],
        new Int8Array(0)
    )
    let msgBytes = koap.encode(msg)
 */

public fun tcpMessage(
    method: Method,
    token: Int,
    options: Array<Option>,
    payload: ByteArray
) : Message {
    return Message.Tcp(
        method,
        token.toLong(),
        options.toList(),
        payload
    )
}
