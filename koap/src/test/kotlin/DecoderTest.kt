package com.juul.koap.test

import com.juul.koap.Message
import com.juul.koap.Message.Code.Method.GET
import com.juul.koap.Message.Option.UriHost
import com.juul.koap.Message.Option.UriPath
import com.juul.koap.Message.Option.UriPort
import com.juul.koap.Message.Udp.Type.Confirmable
import com.juul.koap.decode
import com.juul.koap.encode
import kotlin.test.Test
import kotlin.test.assertEquals

class DecoderTest {

    @Test
    fun `Decode Udp GET with Options`() {
        val message = Message.Udp(
            type = Confirmable,
            code = GET,
            id = 0xCAFE,
            token = 1,
            options = listOf(
                UriHost("http://localhost"),
                UriPort(1234),
                UriPath("/test")
            ),
            payload = "Hello UDP!".toByteArray()
        )

        assertEquals(
            expected = message,
            actual = message.encode().decode()
        )
    }

    @Test
    fun `Decode Tcp GET with Options`() {
        val message = Message.Tcp(
            code = GET,
            token = 1,
            options = listOf(
                UriHost("http://localhost"),
                UriPort(1234),
                UriPath("/test")
            ),
            payload = "Hello TCP!".toByteArray()
        )

        assertEquals(
            expected = message,
            actual = message.encode().decode()
        )
    }
}
