package com.juul.koap.test

import com.juul.koap.Message.Code.Method.DELETE
import com.juul.koap.Message.Code.Method.GET
import com.juul.koap.Message.Code.Method.POST
import com.juul.koap.Message.Code.Method.PUT
import com.juul.koap.Message.Code.Response.BadGateway
import com.juul.koap.Message.Code.Response.BadOption
import com.juul.koap.Message.Code.Response.BadRequest
import com.juul.koap.Message.Code.Response.Changed
import com.juul.koap.Message.Code.Response.Content
import com.juul.koap.Message.Code.Response.Created
import com.juul.koap.Message.Code.Response.Deleted
import com.juul.koap.Message.Code.Response.Forbidden
import com.juul.koap.Message.Code.Response.GatewayTimeout
import com.juul.koap.Message.Code.Response.InternalServerError
import com.juul.koap.Message.Code.Response.MethodNotAllowed
import com.juul.koap.Message.Code.Response.NotAcceptable
import com.juul.koap.Message.Code.Response.NotFound
import com.juul.koap.Message.Code.Response.NotImplemented
import com.juul.koap.Message.Code.Response.PreconditionFailed
import com.juul.koap.Message.Code.Response.ProxyingNotSupported
import com.juul.koap.Message.Code.Response.RequestEntityTooLarge
import com.juul.koap.Message.Code.Response.ServiceUnavailable
import com.juul.koap.Message.Code.Response.Unauthorized
import com.juul.koap.Message.Code.Response.UnsupportedContentFormat
import com.juul.koap.Message.Code.Response.Valid
import com.juul.koap.Message.Option.Accept
import com.juul.koap.Message.Option.Block
import com.juul.koap.Message.Option.Block1
import com.juul.koap.Message.Option.ContentFormat
import com.juul.koap.Message.Option.ETag
import com.juul.koap.Message.Option.Echo
import com.juul.koap.Message.Option.Edhoc
import com.juul.koap.Message.Option.ExperimentalUse
import com.juul.koap.Message.Option.IfMatch
import com.juul.koap.Message.Option.IfNoneMatch
import com.juul.koap.Message.Option.Oscore
import com.juul.koap.Message.Option.QBlock2
import com.juul.koap.Message.Option.RequestTag
import com.juul.koap.Message.Option.Reserved
import com.juul.koap.Message.Option.Unassigned
import com.juul.koap.Message.Option.UriHost
import com.juul.koap.Message.Option.UriPort
import kotlin.test.Test
import kotlin.test.assertEquals

class MessageTest {

    @Test
    fun optionToString() {
        assertToString(ContentFormat.JSON, "ContentFormat(JSON)")
        assertToString(ContentFormat.CBOR, "ContentFormat(CBOR)")
        assertToString(ContentFormat.PlainText, "ContentFormat(PlainText)")
        assertToString(ContentFormat(20), "ContentFormat(20)")
        assertToString(Accept(ContentFormat.JSON), "Accept(JSON)")
        assertToString(ETag("123".encodeToByteArray()), "ETag(etag=31 32 33)")
        assertToString(IfMatch("123".encodeToByteArray()), "IfMatch(etag=31 32 33)")
        assertToString(IfMatch("".encodeToByteArray()), "IfMatch(etag=)")
        assertToString(Echo("echo".encodeToByteArray()), "Echo(value=65 63 68 6F)")
        assertToString(RequestTag("tag".encodeToByteArray()), "RequestTag(tag=74 61 67)")
        assertToString(Oscore(byteArrayOf(0x09, 0x14)), "Oscore(value=09 14)")
        assertToString(IfNoneMatch, "IfNoneMatch")
        assertToString(Edhoc, "Edhoc")
        assertToString(UriHost("localhost"), "UriHost(uri=localhost)")
        assertToString(UriPort(1234), "UriPort(port=1234)")
        assertToString(Block1(100, true, Block.Size.`64`), "Block1(number=100, more=true, size=64)")
        assertToString(QBlock2(101, false, Block.Size.Bert), "QBlock2(number=101, more=false, size=Bert)")
        assertToString(Unassigned(5674, "abc".encodeToByteArray()), "Unassigned(number=5674, value=61 62 63)")
        assertToString(Reserved(136, "abc".encodeToByteArray()), "Reserved(number=136, value=61 62 63)")
        assertToString(ExperimentalUse(65123, "abc".encodeToByteArray()), "ExperimentalUse(number=65123, value=61 62 63)")
    }

    @Test
    fun methodToString() {
        assertToString(GET, "GET")
        assertToString(POST, "POST")
        assertToString(PUT, "PUT")
        assertToString(DELETE, "DELETE")
    }

    @Test
    fun responseToString() {
        assertToString(Created, "Created")
        assertToString(Deleted, "Deleted")
        assertToString(Valid, "Valid")
        assertToString(Changed, "Changed")
        assertToString(Content, "Content")
        assertToString(BadRequest, "BadRequest")
        assertToString(Unauthorized, "Unauthorized")
        assertToString(BadOption, "BadOption")
        assertToString(Forbidden, "Forbidden")
        assertToString(NotFound, "NotFound")
        assertToString(MethodNotAllowed, "MethodNotAllowed")
        assertToString(NotAcceptable, "NotAcceptable")
        assertToString(PreconditionFailed, "PreconditionFailed")
        assertToString(RequestEntityTooLarge, "RequestEntityTooLarge")
        assertToString(UnsupportedContentFormat, "UnsupportedContentFormat")
        assertToString(InternalServerError, "InternalServerError")
        assertToString(NotImplemented, "NotImplemented")
        assertToString(BadGateway, "BadGateway")
        assertToString(ServiceUnavailable, "ServiceUnavailable")
        assertToString(GatewayTimeout, "GatewayTimeout")
        assertToString(ProxyingNotSupported, "ProxyingNotSupported")
    }
}

private fun assertToString(obj: Any, expected: String) {
    assertEquals(
        actual = obj.toString(),
        expected = expected,
    )
}
