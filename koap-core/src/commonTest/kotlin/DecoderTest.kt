package com.juul.koap.test

import com.juul.koap.Message
import com.juul.koap.Message.Code.Method.GET
import com.juul.koap.Message.Code.Method.PUT
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

    @Test
    fun canDecodeTokenOfAnyLength() {
        fun emptyPut(token: Long) = Message.Udp(
            type = Confirmable,
            code = PUT,
            id = 0x1415,
            token = token,
            options = emptyList(),
            payload = byteArrayOf(),
        )
        testReadUdp("40031415", emptyPut(0x0))
        testReadUdp("4103141592", emptyPut(0x92))
        testReadUdp("420314159265", emptyPut(0x9265))
        testReadUdp("43031415926535", emptyPut(0x926535))
        testReadUdp("4403141592653589", emptyPut(0x92653589))
        testReadUdp("450314159265358979", emptyPut(0x9265358979))
        testReadUdp("46031415926535897932", emptyPut(0x926535897932))
        testReadUdp("4703141592653589793238", emptyPut(0x92653589793238))
        testReadUdp("480314159265358979323846", emptyPut(-0x6d9aca7686cdc7ba))
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

private fun testReadUdp(
    encoded: String,
    expected: Message.Udp,
) {
    val msg = encoded
        .stripComments()
        .decodeHex()
        .toByteArray()
        .decode<Message.Udp>()

    assertEquals(
        expected = expected,
        actual = msg,
    )
}

private fun String.stripComments() =
    splitToSequence('\n')
        .map { it.substringBefore('#').trim() }
        .filter { it.isNotBlank() }
        .joinToString("")
        .replace(" ", "")
