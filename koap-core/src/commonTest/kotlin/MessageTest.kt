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
import com.juul.koap.Message.Option.Observe
import com.juul.koap.Message.Option.Observe.Registration.Deregister
import com.juul.koap.Message.Option.Observe.Registration.Register
import kotlin.test.Test
import kotlin.test.assertEquals

class MessageTest {

    @Test
    fun optionToString() {
        assertToString(Observe(Register), "Observe(value=0/Register)")
        assertToString(Observe(Deregister), "Observe(value=1/Deregister)")
        assertToString(Observe(1234567), "Observe(value=1234567)")
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
