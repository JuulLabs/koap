package com.juul.koap.test

import com.juul.koap.Message
import com.juul.koap.Message.Code.Method.GET
import com.juul.koap.Message.Option.NoResponse
import com.juul.koap.Message.Option.NoResponse.NotInterestedIn.Response4xx
import com.juul.koap.Message.Option.NoResponse.NotInterestedIn.Response5xx
import com.juul.koap.Message.Option.Observe
import com.juul.koap.Message.Option.Observe.Registration.Deregister
import com.juul.koap.Message.Option.Observe.Registration.Register
import com.juul.koap.Message.Option.UriHost
import com.juul.koap.Message.Option.UriPath
import com.juul.koap.Message.Option.UriPort
import com.juul.koap.Message.Udp.Type.Confirmable
import com.juul.koap.decode
import com.juul.koap.encode
import com.juul.koap.readOption
import com.juul.koap.reader
import okio.ByteString.Companion.decodeHex
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalStdlibApi // for `encodeToByteArray`
class DecoderTest {

    @Test
    fun decodeUdpGetWithOptions() {
        val message = Message.Udp(
            type = Confirmable,
            code = GET,
            id = 0xCAFE,
            token = 1,
            options = listOf(
                UriHost("http://localhost"),
                UriPort(1234),
                UriPath("/test"),
            ),
            payload = "Hello UDP!".encodeToByteArray(),
        )

        assertEquals(
            expected = message,
            actual = message.encode().decode(),
        )
    }

    @Test
    fun decodeTcpGetWithOptions() {
        val message = Message.Tcp(
            code = GET,
            token = 1,
            options = listOf(
                UriHost("http://localhost"),
                UriPort(1234),
                UriPath("/test"),
            ),
            payload = "Hello TCP!".encodeToByteArray(),
        )

        assertEquals(
            expected = message,
            actual = message.encode().decode(),
        )
    }

    @Test
    fun decodeTcpGetWithoutOptionsNorPayload() {
        val message = Message.Tcp(
            code = GET,
            token = 1,
            options = emptyList(),
            payload = byteArrayOf(),
        )

        assertEquals(
            expected = message,
            actual = message.encode().decode(),
        )
    }

    @Test
    fun decodeTcpGetWithoutOptionsButWithPayload() {
        val message = Message.Tcp(
            code = GET,
            token = 1,
            options = emptyList(),
            payload = "Hello TCP!".encodeToByteArray(),
        )

        assertEquals(
            expected = message,
            actual = message.encode().decode(),
        )
    }

    @Test
    fun decodeTcpGetWithOptionsButWithoutPayload() {
        val message = Message.Tcp(
            code = GET,
            token = 1,
            options = listOf(
                UriHost("http://localhost"),
                UriPort(1234),
                UriPath("/test"),
            ),
            payload = byteArrayOf(),
        )

        assertEquals(
            expected = message,
            actual = message.encode().decode(),
        )
    }

    @Test
    fun decodeNoResponseOptionWithValueOf24() {
        testReadOption(
            encoded = """
                D1 F5 # Option Delta: 258, Option Length: 1
                18    # Option Value: 24
            """,
            expected = NoResponse(Response4xx, Response5xx),
        )
    }

    @Test
    fun decodeNoResponseOptionWithEmptyValue() {
        testReadOption(
            encoded = """
                D0 F5 # Option Delta: 258, Option Length: 0
            """,
            expected = NoResponse(),
        )
    }

    @Test
    fun decodeObserveOptionWithValueOf16777215() {
        testReadOption(
            encoded = """
                63       # Option Delta: 6, Option Length: 3
                FF FF FF # Option Value: 16,777,215
            """,
            expected = Observe(16_777_215),
        )
    }

    @Test
    fun decodeObserveOptionWithValueOfRegister() {
        testReadOption(
            encoded = """
                60 # Option Delta: 6, Option Length: 0 (Option Value of 0 is implied; Register)
            """,
            expected = Observe(Register),
        )
    }

    @Test
    fun decodeObserveOptionWithValueOfDeregister() {
        testReadOption(
            encoded = """
                61 # Option Delta: 6, Option Length: 1
                01 # Option Value: 1 (Deregister)
            """,
            expected = Observe(Deregister),
        )
    }

    @Test
    fun decodingTcpMessageDoesNotReadBeyondLengthSpecifiedInHeader() {
        val message = Message.Tcp(
            code = GET,
            token = 0,
            options = emptyList(),
            payload = byteArrayOf(0x01, 0x02, 0x03),
        )
        val extraData = byteArrayOf(0x0A, 0x0B, 0x0C)

        assertEquals(
            expected = message,
            actual = (message.encode() + extraData).decode(),
        )
    }

    @Test
    fun canDecodeTokenOfLength8() {
        val message = Message.Tcp(
            code = GET,
            token = Long.MAX_VALUE,
            options = emptyList(),
            payload = "Hello TCP!".encodeToByteArray(),
        )

        assertEquals(
            expected = message,
            actual = message.encode().decode(),
        )
    }
}

private fun testReadOption(
    encoded: String,
    expected: Message.Option,
) {
    val option = encoded
        .stripComments()
        .decodeHex()
        .toByteArray()
        .reader()
        .readOption(preceding = null)

    assertEquals(
        expected = expected,
        actual = option,
    )
}

private fun String.stripComments() =
    splitToSequence('\n')
        .map { it.substringBefore('#').trim() }
        .filter { it.isNotBlank() }
        .joinToString("")
        .replace(" ", "")
