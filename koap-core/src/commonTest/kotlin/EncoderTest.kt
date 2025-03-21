package com.juul.koap.test

import com.juul.koap.Message
import com.juul.koap.Message.Code.Method.GET
import com.juul.koap.Message.Code.Response.Content
import com.juul.koap.Message.Option.Observe
import com.juul.koap.Message.Option.Observe.Registration.Deregister
import com.juul.koap.Message.Option.Observe.Registration.Register
import com.juul.koap.Message.Option.Unknown
import com.juul.koap.Message.Option.UriPath
import com.juul.koap.Message.Option.UriPort
import com.juul.koap.Message.Udp.Type.Acknowledgement
import com.juul.koap.Message.Udp.Type.Confirmable
import com.juul.koap.UBYTE_MAX_VALUE
import com.juul.koap.UINT32_MAX_EXTENDED_LENGTH
import com.juul.koap.UINT_MAX_VALUE
import com.juul.koap.USHORT_MAX_VALUE
import com.juul.koap.encode
import com.juul.koap.toFormat
import com.juul.koap.toHexString
import com.juul.koap.writeHeader
import com.juul.koap.writeOption
import com.juul.koap.writeToken
import okio.Buffer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@ExperimentalStdlibApi // for `encodeToByteArray`
class EncoderTest {

    @Test
    fun udpMessageGetWithoutPayload() {
        val message = Message.Udp(
            type = Confirmable,
            code = GET,
            id = 0xFEED,
            token = 0xCAFE,
            options = listOf(
                UriPath("example"),
            ),
            payload = byteArrayOf(),
        )

        assertEquals(
            expected = """
                42                   # Version 1, Type 0 (Confirmable), Token length: 2
                01                   # Code: 0.01 (GET)
                FE ED                # Message ID
                CA FE                # Token
                B7                   # Delta option: 11 (Uri-Path), Delta length: 7
                65 78 61 6D 70 6C 65 # "example"
            """.stripComments(),
            actual = message.encode().toHexString(),
        )
    }

    @Test
    fun tcpMessageHeaderWithMaxSizeContentLength() {
        val message = Message.Tcp(
            code = GET,
            token = 0,
            options = emptyList(),
            payload = byteArrayOf(),
        )

        val buffer = Buffer().apply {
            writeHeader(message, contentLength = UINT32_MAX_EXTENDED_LENGTH)
        }
        assertEquals(
            expected = """
                F0          # Len: 15 (Reserved; content length >= 65805), Token length: 0
                FF FF FF FF # Extended Content Length: 4,295,033,100 (max allowable)
                01          # Code: 1 (GET)
            """.stripComments(),
            actual = buffer.readByteArray().toHexString(),
        )
    }

    @Test
    fun tcpMessageWith25BytePayload() {
        val message = Message.Tcp(
            code = Content,
            token = 0,
            options = emptyList(),
            payload = (1..25).map { it.toByte() }.toByteArray(),
        )

        assertEquals(
            expected = """
                D0 # Len: 13 (Length defined in Extended Length), Token length: 0
                0D # Extended Length: 13 + 13 = 26
                45 # Code: Content
                FF # Payload marker
                01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F 10 11 12 13 14 15 16 17 18 19 # Payload
            """.stripComments(),
            actual = message.encode().toHexString(),
        )
    }

    @Test
    fun emptyTcpMessage() {
        val message = Message.Tcp(
            code = GET,
            token = 0,
            options = emptyList(),
            payload = byteArrayOf(),
        )

        assertEquals(
            expected = """
                00 # Len: 0, Token length: 0
                01 # Code: 1 (GET)
            """.stripComments(),
            actual = message.encode().toHexString(),
        )
    }

    @Test
    fun tcpMessageWithTokenValueOf1() {
        val message = Message.Tcp(
            code = GET,
            token = 1,
            options = emptyList(),
            payload = byteArrayOf(),
        )

        assertEquals(
            expected = """
                01 # Len: 0, Token length: 1
                01 # Code: 1 (GET)
                01 # Token: 1
            """.stripComments(),
            actual = message.encode().toHexString(),
        )
    }

    @Test
    fun tooLongOfUriPathThrowsIllegalArgumentException() {
        val path = "*".repeat(256) // 255 is maximum allowable UriPath length
        assertFailsWith<IllegalArgumentException> {
            UriPath(path)
        }
    }

    // RFC 7252 Appendix A. Examples
    // Figure 16: Confirmable Request; Piggybacked Response
    @Test
    fun getWithPiggybackedResponse() {
        //   Header: GET (T=CON, Code=0.01, MID=0x7d34)
        // Uri-Path: "temperature"
        //
        //  0                   1                   2                   3
        //  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
        // +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
        // | 1 | 0 |   0   |     GET=1     |          MID=0x7d34           |
        // +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
        // |  11   |  11   |      "temperature" (11 B) ...
        // +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

        val request = Message.Udp(
            type = Confirmable,
            code = GET,
            id = 0x7d34,
            options = listOf(
                UriPath("temperature"),
            ),
            payload = byteArrayOf(),
            token = 0,
        )
        assertEquals(
            expected = """
                40                               # Version 1, Type 0 (Confirmable), Token length: 0
                01                               # Code: 0.01 (GET)
                7D 34                            # Message ID
                BB                               # Delta option: 11 (Uri-Path), Delta length: 11
                74 65 6D 70 65 72 61 74 75 72 65 # "temperature"
            """.stripComments(),
            actual = request.encode().toHexString(),
        )

        //  Header: 2.05 Content (T=ACK, Code=2.05, MID=0x7d34)
        // Payload: "22.3 C"
        //
        //  0                   1                   2                   3
        //  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
        // +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
        // | 1 | 2 |   0   |    2.05=69    |          MID=0x7d34           |
        // +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
        // |1 1 1 1 1 1 1 1|      "22.3 C" (6 B) ...
        // +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

        val response = Message.Udp(
            type = Acknowledgement,
            code = Content, // 2.05 (69)
            id = 0x7d34,
            options = emptyList(),
            payload = "22.3 C".encodeToByteArray(),
            token = 0,
        )
        assertEquals(
            expected = """
                60                # Version 1, Type 2 (Acknowledgement), Token length: 0
                45                # Code: 2.05 (Content)
                7D 34             # Message ID
                FF                # Payload marker
                32 32 2E 33 20 43 # "22.3 C"
            """.stripComments(),
            actual = response.encode().toHexString(),
        )
    }

    @Test
    fun writeUriPortOption() {
        val buffer = Buffer().apply {
            writeOption(UriPort(1234).toFormat(), null)
        }

        assertEquals(
            expected = """
                72    # Option Delta: 7, Option Length: 2
                04 D2 # Option Value: 1234
            """.stripComments(),
            actual = buffer.readByteArray().toHexString(),
        )
    }

    @Test
    fun writeTokenOfValue0() {
        testWriteToken(
            token = 0,
            expectedSize = 0,
            expectedHex = "",
        )
    }

    @Test
    fun writeTokenOfValue255() {
        testWriteToken(
            token = UBYTE_MAX_VALUE.toLong(),
            expectedSize = 1,
            expectedHex = "FF",
        )
    }

    @Test
    fun writeTokenOfValue65535() {
        testWriteToken(
            token = USHORT_MAX_VALUE.toLong(),
            expectedSize = 2,
            expectedHex = "FF FF",
        )
    }

    @Test
    fun writeTokenOfValue4294967295() {
        testWriteToken(
            token = UINT_MAX_VALUE,
            expectedSize = 4,
            expectedHex = "FF FF FF FF",
        )
    }

    @Test
    fun writeTokenOfValue9223372036854775807() {
        testWriteToken(
            token = Long.MAX_VALUE,
            expectedSize = 8,
            expectedHex = "7F FF FF FF FF FF FF FF",
        )
    }

    @Test
    fun writeTokenOfValueNegative1() {
        testWriteToken(
            token = -1,
            expectedSize = 8,
            expectedHex = "FF FF FF FF FF FF FF FF",
        )
    }

    @Test
    fun writeTokenOfValueNegative9223372036854775806() {
        testWriteToken(
            token = Long.MIN_VALUE,
            expectedSize = 8,
            expectedHex = "80 00 00 00 00 00 00 00",
        )
    }

    @Test
    fun writeObserveOptionWithValueOfRegister() {
        testWriteOption(
            option = Observe(Register),
            expected = """
                60 # Option Delta: 6, Option Length: 0 (Option Value of 0 is implied; Register)
            """,
        )
    }

    @Test
    fun writeObserveOptionWithValueOfDeregister() {
        testWriteOption(
            option = Observe(Deregister),
            expected = """
                61 # Option Delta: 6, Option Length: 1
                01 # Option Value: 1 (Deregister)
            """,
        )
    }

    @Test
    fun writeObserveOptionWithValueOf255() {
        testWriteOption(
            option = Observe(255),
            expected = """
                61 # Option Delta: 6, Option Length: 1
                FF # Option Value: 255
            """,
        )
    }

    @Test
    fun writeObserveOptionWithValueOf16777215() {
        testWriteOption(
            option = Observe(16_777_215),
            expected = """
                63       # Option Delta: 6, Option Length: 3
                FF FF FF # Option Value: 16,777,215
            """,
        )
    }

    @Test
    fun observeOptionWithValueOutsideOfAllowableRangeThrowsIllegalArgumentException() {
        assertFailsWith<IllegalArgumentException> {
            Observe(16_777_216)
        }
    }

    @Test
    fun writeUnknownOption() {
        testWriteOption(
            option = Unknown(0x4321, byteArrayOf(0x04, 0x03, 0x02, 0x01)),
            expected = """
                E4 42 14    # Option Delta: 0x4321, Option Length: 3
                04 03 02 01 # Option Value: 0x04, 0x03, 0x02, 0x01
            """,
        )
    }
}

private fun testWriteToken(
    token: Long,
    expectedSize: Long,
    expectedHex: String,
) {
    val buffer = Buffer().apply { writeToken(token) }

    assertEquals(
        expected = expectedSize,
        actual = buffer.size,
    )

    assertEquals(
        expected = expectedHex,
        actual = buffer.readByteArray().toHexString(),
    )
}

private fun testWriteOption(
    option: Message.Option,
    expected: String,
) {
    val buffer = Buffer().apply {
        writeOption(option.toFormat(), preceding = null)
    }

    assertEquals(
        expected = expected.stripComments(),
        actual = buffer.readByteArray().toHexString(),
    )
}

private fun String.stripComments() =
    splitToSequence('\n')
        .map { it.substringBefore('#').trim() }
        .filter { it.isNotBlank() }
        .joinToString(" ")
