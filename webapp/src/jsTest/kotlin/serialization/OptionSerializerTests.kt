package com.juul.koap.serialization

import com.juul.koap.Message.Option
import com.juul.koap.Message.Option.Accept
import com.juul.koap.Message.Option.ContentFormat
import com.juul.koap.Message.Option.ETag
import com.juul.koap.Message.Option.Echo
import com.juul.koap.Message.Option.HopLimit
import com.juul.koap.Message.Option.IfMatch
import com.juul.koap.Message.Option.IfNoneMatch
import com.juul.koap.Message.Option.LocationPath
import com.juul.koap.Message.Option.LocationQuery
import com.juul.koap.Message.Option.MaxAge
import com.juul.koap.Message.Option.NoResponse
import com.juul.koap.Message.Option.NoResponse.NotInterestedIn.Response4xx
import com.juul.koap.Message.Option.NoResponse.NotInterestedIn.Response5xx
import com.juul.koap.Message.Option.ProxyScheme
import com.juul.koap.Message.Option.ProxyUri
import com.juul.koap.Message.Option.RequestTag
import com.juul.koap.Message.Option.Size1
import com.juul.koap.Message.Option.UriHost
import com.juul.koap.Message.Option.UriPath
import com.juul.koap.Message.Option.UriPort
import com.juul.koap.Message.Option.UriQuery
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

@Suppress("TestFunctionName")
class OptionSerializerTests {

    @Test
    fun IfMatch() {
        assertEquals(
            expected = "If-Match: 616263".quoted(),
            actual = IfMatch("abc".encodeToByteArray()).serialize(),
        )
    }

    @Test
    fun UriHost() {
        assertEquals(
            expected = "Uri-Host: host".quoted(),
            actual = UriHost("host").serialize(),
        )
    }

    @Test
    fun ETag() {
        assertEquals(
            expected = "ETag: 616263".quoted(),
            actual = ETag("abc".encodeToByteArray()).serialize(),
        )
    }

    @Test
    fun IfNoneMatch() {
        assertEquals(
            expected = "If-None-Match".quoted(),
            actual = IfNoneMatch.serialize(),
        )
    }

    @Test
    fun UriPort() {
        assertEquals(
            expected = "Uri-Port: 123".quoted(),
            actual = UriPort(123).serialize(),
        )
    }

    @Test
    fun LocationPath() {
        assertEquals(
            expected = "Location-Path: abc".quoted(),
            actual = LocationPath("abc").serialize(),
        )
    }

    @Test
    fun UriPath() {
        assertEquals(
            expected = "Uri-Path: abc".quoted(),
            actual = UriPath("abc").serialize(),
        )
    }

    @Test
    fun ContentFormat() {
        assertEquals(
            expected = "Content-Format: text/plain; charset=utf-8".quoted(),
            actual = ContentFormat.PlainText.serialize(),
        )
        assertEquals(
            expected = "Content-Format: application/json".quoted(),
            actual = ContentFormat.JSON.serialize(),
        )
        assertEquals(
            expected = "Content-Format: application/cbor".quoted(),
            actual = ContentFormat.CBOR.serialize(),
        )
        assertEquals(
            expected = "Content-Format: 20".quoted(),
            actual = ContentFormat(20).serialize(),
        )
    }

    @Test
    fun MaxAge() {
        assertEquals(
            expected = "Max-Age: 123".quoted(),
            actual = MaxAge(123).serialize(),
        )
    }

    @Test
    fun UriQuery() {
        assertEquals(
            expected = "Uri-Query: abc".quoted(),
            actual = UriQuery("abc").serialize(),
        )
    }

    @Test
    fun HopLimit() {
        assertEquals(
            expected = "Hop-Limit: 123".quoted(),
            actual = HopLimit(123).serialize(),
        )
    }

    @Test
    fun Accept() {
        assertEquals(
            expected = "Accept: application/json".quoted(),
            actual = Accept(ContentFormat.JSON).serialize(),
        )
    }

    @Test
    fun LocationQuery() {
        assertEquals(
            expected = "Location-Query: abc".quoted(),
            actual = LocationQuery("abc").serialize(),
        )
    }

    @Test
    fun ProxyUri() {
        assertEquals(
            expected = "Proxy-Uri: abc".quoted(),
            actual = ProxyUri("abc").serialize(),
        )
    }

    @Test
    fun ProxyScheme() {
        assertEquals(
            expected = "Proxy-Scheme: abc".quoted(),
            actual = ProxyScheme("abc").serialize(),
        )
    }

    @Test
    fun Size1() {
        assertEquals(
            expected = "Size1: 123".quoted(),
            actual = Size1(123).serialize(),
        )
    }

    @Test
    fun Echo() {
        assertEquals(
            expected = "Echo: 616263".quoted(),
            actual = Echo("abc".encodeToByteArray()).serialize(),
        )
    }

    @Test
    fun NoResponse() {
        assertEquals(
            expected = "No-Response: 24".quoted(),
            actual = NoResponse(Response4xx, Response5xx).serialize(),
        )
    }

    @Test
    fun RequestTag() {
        assertEquals(
            expected = "Request-Tag: 78797a".quoted(),
            actual = RequestTag("xyz".encodeToByteArray()).serialize(),
        )
    }
}

private fun String.quoted() = "\"$this\""
private fun Option.serialize() = Json.encodeToString(OptionSerializer, this)
