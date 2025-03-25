package com.juul.koap.serialization

import com.juul.koap.Message.Option
import com.juul.koap.Message.Option.IfMatch
import com.juul.koap.Message.Option.UriHost
import com.juul.koap.Message.Option.ETag
import com.juul.koap.Message.Option.IfNoneMatch
import com.juul.koap.Message.Option.UriPort
import com.juul.koap.Message.Option.LocationPath
import com.juul.koap.Message.Option.UriPath
import com.juul.koap.Message.Option.ContentFormat
import com.juul.koap.Message.Option.MaxAge
import com.juul.koap.Message.Option.UriQuery
import com.juul.koap.Message.Option.HopLimit
import com.juul.koap.Message.Option.Accept
import com.juul.koap.Message.Option.LocationQuery
import com.juul.koap.Message.Option.ProxyUri
import com.juul.koap.Message.Option.ProxyScheme
import com.juul.koap.Message.Option.Size1
import com.juul.koap.Message.Option.Echo
import com.juul.koap.Message.Option.NoResponse
import com.juul.koap.Message.Option.NoResponse.NotInterestedIn.Response4xx
import com.juul.koap.Message.Option.NoResponse.NotInterestedIn.Response5xx
import com.juul.koap.Message.Option.RequestTag

import kotlinx.serialization.UseSerializers
import kotlinx.serialization.json.Json
import kotlin.test.assertEquals
import kotlin.test.Test

class SerializerTest {
    @Test
    fun optionSerializesTo() {
        assertOptionSerializesTo(IfMatch("abc".encodeToByteArray()), "If-Match: 616263")
        assertOptionSerializesTo(UriHost("host"), "Uri-Host: host")
        assertOptionSerializesTo(ETag("abc".encodeToByteArray()), "ETag: 616263")
        assertOptionSerializesTo(IfNoneMatch, "If-None-Match")
        assertOptionSerializesTo(UriPort(123), "Uri-Port: 123")
        assertOptionSerializesTo(LocationPath("abc"), "Location-Path: abc")
        assertOptionSerializesTo(UriPath("abc"), "Uri-Path: abc")
        assertOptionSerializesTo(ContentFormat.PlainText, "Content-Format: text/plain; charset=utf-8")
        assertOptionSerializesTo(ContentFormat.JSON, "Content-Format: application/json")
        assertOptionSerializesTo(ContentFormat.CBOR, "Content-Format: application/cbor")
        assertOptionSerializesTo(ContentFormat(20), "Content-Format: 20")
        assertOptionSerializesTo(MaxAge(123), "Max-Age: 123")
        assertOptionSerializesTo(UriQuery("abc"), "Uri-Query: abc")
        assertOptionSerializesTo(HopLimit(123), "Hop-Limit: 123")
        assertOptionSerializesTo(Accept(ContentFormat.JSON), "Accept: application/json")
        assertOptionSerializesTo(LocationQuery("abc"), "Location-Query: abc")
        assertOptionSerializesTo(ProxyUri("abc"), "Proxy-Uri: abc")
        assertOptionSerializesTo(ProxyScheme("abc"), "Proxy-Scheme: abc")
        assertOptionSerializesTo(Size1(123), "Size1: 123")
        assertOptionSerializesTo(Echo("abc".encodeToByteArray()), "Echo: 616263")
        assertOptionSerializesTo(NoResponse(Response4xx, Response5xx), "No-Response: 24")
        assertOptionSerializesTo(RequestTag("xyz".encodeToByteArray()), "Request-Tag: 78797a")
    }
}

private fun assertOptionSerializesTo(opt: Option, expected: String) {
    assertEquals(
        actual = Json.encodeToString(OptionSerializer, opt).trim('"'),
        expected = expected,
    )
}

