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
        assertStringify(GET, "GET")
        assertStringify(POST, "POST")
        assertStringify(PUT, "PUT")
        assertStringify(DELETE, "DELETE")
    }

    @Test
    fun responseToString() {
        assertStringify(Created, "Created")
        assertStringify(Deleted, "Deleted")
        assertStringify(Valid, "Valid")
        assertStringify(Changed, "Changed")
        assertStringify(Content, "Content")
        assertStringify(BadRequest, "BadRequest")
        assertStringify(Unauthorized, "Unauthorized")
        assertStringify(BadOption, "BadOption")
        assertStringify(Forbidden, "Forbidden")
        assertStringify(NotFound, "NotFound")
        assertStringify(MethodNotAllowed, "MethodNotAllowed")
        assertStringify(NotAcceptable, "NotAcceptable")
        assertStringify(PreconditionFailed, "PreconditionFailed")
        assertStringify(RequestEntityTooLarge, "RequestEntityTooLarge")
        assertStringify(UnsupportedContentFormat, "UnsupportedContentFormat")
        assertStringify(InternalServerError, "InternalServerError")
        assertStringify(NotImplemented, "NotImplemented")
        assertStringify(BadGateway, "BadGateway")
        assertStringify(ServiceUnavailable, "ServiceUnavailable")
        assertStringify(GatewayTimeout, "GatewayTimeout")
        assertStringify(ProxyingNotSupported, "ProxyingNotSupported")
    }
}

private fun assertStringify(obj: Any, expected: String) {
    assertEquals(
        actual = obj.toString(),
        expected = expected
    )
}
