package com.juul.koap.test

import com.juul.koap.Message
import com.juul.koap.Message.Code.Method.GET
import com.juul.koap.Message.Code.Response.Content
import com.juul.koap.Message.Option.Observe
import com.juul.koap.Message.Option.UriPath
import com.juul.koap.Message.Option.UriPort
import com.juul.koap.Message.Udp.Type.Acknowledgement
import com.juul.koap.Message.Udp.Type.Confirmable
import com.juul.koap.Registration.Deregister
import com.juul.koap.Registration.Register
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
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import okio.Buffer

class EncoderTest {

    @Test
    fun `UDP message GET without Payload`() {
        val message = Message.Udp(
            type = Confirmable,
            code = GET,
            id = 0xFEED,
            token = 0xCAFE,
            options = listOf(
                UriPath("example")
            ),
            payload = byteArrayOf()
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
            actual = message.encode().toHexString()
        )
    }

    @Test
    fun `TCP message header with max size content length`() {
        val message = Message.Tcp(
            code = GET,
            token = null,
            options = emptyList(),
            payload = byteArrayOf()
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
            actual = buffer.readByteArray().toHexString()
        )
    }

    @Test
    fun `Empty TCP message`() {
        val message = Message.Tcp(
            code = GET,
            token = null,
            options = emptyList(),
            payload = byteArrayOf()
        )

        assertEquals(
            expected = """
                00 # Len: 0, Token length: 0
                01 # Code: 1 (GET)
            """.stripComments(),
            actual = message.encode().toHexString()
        )
    }

    @Test
    fun `TCP message with token value of 1`() {
        val message = Message.Tcp(
            code = GET,
            token = 1,
            options = emptyList(),
            payload = byteArrayOf()
        )

        assertEquals(
            expected = """
                01 # Len: 0, Token length: 1
                01 # Code: 1 (GET)
                01 # Token: 1
            """.stripComments(),
            actual = message.encode().toHexString()
        )
    }

    @Test
    fun `Too long of UriPath throws IllegalArgumentException`() {
        val path = "*".repeat(256) // 255 is maximum allowable UriPath length
        assertFailsWith<IllegalArgumentException> {
            UriPath(path)
        }
    }

    // RFC 7252 Appendix A. Examples
    // Figure 16: Confirmable Request; Piggybacked Response
    @Test
    fun `GET with piggybacked response`() {
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
                UriPath("temperature")
            ),
            payload = byteArrayOf(),
            token = null
        )
        assertEquals(
            expected = """
                40                               # Version 1, Type 0 (Confirmable), Token length: 0
                01                               # Code: 0.01 (GET)
                7D 34                            # Message ID
                BB                               # Delta option: 11 (Uri-Path), Delta length: 11
                74 65 6D 70 65 72 61 74 75 72 65 # "temperature"
            """.stripComments(),
            actual = request.encode().toHexString()
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
            payload = "22.3 C".toByteArray(),
            token = null
        )
        assertEquals(
            expected = """
                60                # Version 1, Type 2 (Acknowledgement), Token length: 0
                45                # Code: 2.05 (Content)
                7D 34             # Message ID
                FF                # Payload marker
                32 32 2E 33 20 43 # "22.3 C"
            """.stripComments(),
            actual = response.encode().toHexString()
        )
    }

    @Test
    fun `Write UriPort Option`() {
        val buffer = Buffer().apply {
            writeOption(UriPort(1234).toFormat(), null)
        }

        assertEquals(
            expected = """
                72    # Option Delta: 7, Option Length: 2
                04 D2 # Option Value: 1234
            """.stripComments(),
            actual = buffer.readByteArray().toHexString()
        )
    }

    @Test
    fun `Write token of value 0`() {
        testWriteToken(
            token = 0,
            expectedSize = 1,
            expectedHex = "00"
        )
    }

    @Test
    fun `Write token of value 255`() {
        testWriteToken(
            token = UBYTE_MAX_VALUE.toLong(),
            expectedSize = 1,
            expectedHex = "FF"
        )
    }

    @Test
    fun `Write token of value 65,535`() {
        testWriteToken(
            token = USHORT_MAX_VALUE.toLong(),
            expectedSize = 2,
            expectedHex = "FF FF"
        )
    }

    @Test
    fun `Write token of value 4,294,967,295`() {
        testWriteToken(
            token = UINT_MAX_VALUE,
            expectedSize = 4,
            expectedHex = "FF FF FF FF"
        )
    }

    @Test
    fun `Write token of value 9,223,372,036,854,775,807`() {
        testWriteToken(
            token = Long.MAX_VALUE,
            expectedSize = 8,
            expectedHex = "7F FF FF FF FF FF FF FF"
        )
    }

    @Test
    fun `Write token of value -1`() {
        testWriteToken(
            token = -1,
            expectedSize = 8,
            expectedHex = "FF FF FF FF FF FF FF FF"
        )
    }

    @Test
    fun `Write token of value -9,223,372,036,854,775,806`() {
        testWriteToken(
            token = Long.MIN_VALUE,
            expectedSize = 8,
            expectedHex = "80 00 00 00 00 00 00 00"
        )
    }

    @Test
    fun `Write Observe Option with value of Register`() {
        testWriteOption(
            option = Observe(Register),
            expected = """
                60 # Option Delta: 6, Option Length: 0 (Option Value of 0 is implied; Register)
            """
        )
    }

    @Test
    fun `Write Observe Option with value of Deregister`() {
        testWriteOption(
            option = Observe(Deregister),
            expected = """
                61 # Option Delta: 6, Option Length: 1
                01 # Option Value: 1 (Deregister)
            """
        )
    }

    @Test
    fun `Write Observe Option with value of 255`() {
        testWriteOption(
            option = Observe(255),
            expected = """
                61 # Option Delta: 6, Option Length: 1
                FF # Option Value: 255
            """
        )
    }

    @Test
    fun `Write Observe Option with value of 16,777,215`() {
        testWriteOption(
            option = Observe(16_777_215),
            expected = """
                63       # Option Delta: 6, Option Length: 3
                FF FF FF # Option Value: 16,777,215
            """
        )
    }

    @Test
    fun `Observe Option with value outside of allowable range throws IllegalArgumentException`() {
        assertFailsWith<IllegalArgumentException> {
            Observe(16_777_216)
        }
    }
}

private fun testWriteToken(
    token: Long,
    expectedSize: Long,
    expectedHex: String
) {
    val buffer = Buffer().apply { writeToken(token) }

    assertEquals(
        expected = expectedSize,
        actual = buffer.size
    )

    assertEquals(
        expected = expectedHex,
        actual = buffer.readByteArray().toHexString()
    )
}

private fun testWriteOption(
    option: Message.Option,
    expected: String
) {
    val buffer = Buffer().apply {
        writeOption(option.toFormat(), preceding = null)
    }

    assertEquals(
        expected = expected.stripComments(),
        actual = buffer.readByteArray().toHexString()
    )
}

private fun String.stripComments() =
    splitToSequence('\n')
        .map { it.substringBefore('#').trim() }
        .filter { it.isNotBlank() }
        .joinToString(" ")
