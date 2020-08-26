package com.juul.koap.test

import com.juul.koap.Message
import com.juul.koap.Message.Code.Method.GET
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

@ExperimentalStdlibApi
class DecoderTest {

    @Test
    fun decode_udp_get_with_options() {
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
            payload = "Hello UDP!".encodeToByteArray()
        )

        assertEquals(
            expected = message,
            actual = message.encode().decode()
        )
    }

    @Test
    fun decode_tcp_get_with_options() {
        val message = Message.Tcp(
            code = GET,
            token = 1,
            options = listOf(
                UriHost("http://localhost"),
                UriPort(1234),
                UriPath("/test")
            ),
            payload = "Hello TCP!".encodeToByteArray()
        )

        assertEquals(
            expected = message,
            actual = message.encode().decode()
        )
    }

    @Test
    fun decode_tcp_get_without_options_nor_payload() {
        val message = Message.Tcp(
            code = GET,
            token = 1,
            options = emptyList(),
            payload = byteArrayOf()
        )

        assertEquals(
            expected = message,
            actual = message.encode().decode()
        )
    }

    @Test
    fun decode_tcp_get_without_options_but_with_payload() {
        val message = Message.Tcp(
            code = GET,
            token = 1,
            options = emptyList(),
            payload = "Hello TCP!".encodeToByteArray()
        )

        assertEquals(
            expected = message,
            actual = message.encode().decode()
        )
    }

    @Test
    fun decode_tcp_get_with_options_but_without_payload() {
        val message = Message.Tcp(
            code = GET,
            token = 1,
            options = listOf(
                UriHost("http://localhost"),
                UriPort(1234),
                UriPath("/test")
            ),
            payload = byteArrayOf()
        )

        assertEquals(
            expected = message,
            actual = message.encode().decode()
        )
    }

    @Test
    fun decode_observe_option_with_value_of_16_777_215() {
        testReadOption(
            encoded = """
                63       # Option Delta: 6, Option Length: 3
                FF FF FF # Option Value: 16,777,215
            """,
            expected = Observe(16_777_215)
        )
    }

    @Test
    fun decode_observe_option_with_value_of_register() {
        testReadOption(
            encoded = """
                60 # Option Delta: 6, Option Length: 0 (Option Value of 0 is implied; Register)
            """,
            expected = Observe(Register)
        )
    }

    @Test
    fun decode_observe_option_with_value_of_deregister() {
        testReadOption(
            encoded = """
                61 # Option Delta: 6, Option Length: 1
                01 # Option Value: 1 (Deregister)
            """,
            expected = Observe(Deregister)
        )
    }

    @Test
    fun decoding_tcp_message_does_not_read_beyond_length_specified_in_header() {
        val message = Message.Tcp(
            code = GET,
            token = 0,
            options = emptyList(),
            payload = byteArrayOf(0x01, 0x02, 0x03)
        )
        val extraData = byteArrayOf(0x0A, 0x0B, 0x0C)

        assertEquals(
            expected = message,
            actual = (message.encode() + extraData).decode()
        )
    }

    @Test
    fun can_decode_token_of_length_8() {
        val message = Message.Tcp(
            code = GET,
            token = Long.MAX_VALUE,
            options = emptyList(),
            payload = "Hello TCP!".encodeToByteArray()
        )

        assertEquals(
            expected = message,
            actual = message.encode().decode()
        )
    }
}

private fun testReadOption(
    encoded: String,
    expected: Message.Option
) {
    val option = encoded
        .stripComments()
        .decodeHex()
        .toByteArray()
        .reader()
        .readOption(preceding = null)

    assertEquals(
        expected = expected,
        actual = option
    )
}

private fun String.stripComments() =
    splitToSequence('\n')
        .map { it.substringBefore('#').trim() }
        .filter { it.isNotBlank() }
        .joinToString("")
        .replace(" ", "")
