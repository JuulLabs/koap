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
import kotlin.test.Test
import kotlin.test.assertEquals

class MessageTest {

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

    @Test
    fun messageCodeToInt_validCode_correctOutputInt() {
        assertEquals(GET.toInt(), 1)
        assertEquals(POST.toInt(), 2)
        assertEquals(PUT.toInt(), 3)
        assertEquals(DELETE.toInt(), 4)
        assertEquals(Created.toInt(), 65)
        assertEquals(Deleted.toInt(), 66)
        assertEquals(Valid.toInt(), 67)
        assertEquals(Changed.toInt(), 68)
        assertEquals(Content.toInt(), 69)
        assertEquals(BadRequest.toInt(), 128)
        assertEquals(Unauthorized.toInt(), 129)
        assertEquals(BadOption.toInt(), 130)
        assertEquals(Forbidden.toInt(), 131)
        assertEquals(NotFound.toInt(), 132)
        assertEquals(MethodNotAllowed.toInt(), 133)
        assertEquals(NotAcceptable.toInt(), 134)
        assertEquals(PreconditionFailed.toInt(), 140)
        assertEquals(RequestEntityTooLarge.toInt(), 141)
        assertEquals(UnsupportedContentFormat.toInt(), 143)
        assertEquals(InternalServerError.toInt(), 160)
        assertEquals(NotImplemented.toInt(), 161)
        assertEquals(BadGateway.toInt(), 162)
        assertEquals(ServiceUnavailable.toInt(), 163)
        assertEquals(GatewayTimeout.toInt(), 164)
        assertEquals(ProxyingNotSupported.toInt(), 165)
    }
}

private fun assertToString(obj: Any, expected: String) {
    assertEquals(
        actual = obj.toString(),
        expected = expected
    )
}
