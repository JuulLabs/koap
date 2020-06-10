package com.juul.koap

import com.juul.koap.Message.Code.Method
import com.juul.koap.Message.Code.Method.GET
import com.juul.koap.Message.Code.Response.Content
import com.juul.koap.Message.Option.UriPath
import com.juul.koap.Message.Udp.Type.Acknowledgement
import com.juul.koap.Message.Udp.Type.Confirmable
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
            id = 0xC4_09,
            token = 0x74_65_73_74,
            options = listOf(
                UriPath("example")
            ),
            payload = byteArrayOf()
        )

        assertEquals(
            expected = """
                44                   # Version 1, Type 0 (Confirmable), Token length: 4
                01                   # Code: 0.01 (GET)
                C4 09                # Message ID
                74 65 73 74          # Token
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
            writeHeader(message, tokenSize = 0, length = UINT32_MAX_EXTENDED_LENGTH)
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
    fun `Write token of value 0`() {
        testWriteToken(
            token = 0,
            expectedSize = 1,
            expectedHex = "00"
        )
    }

    @Test
    fun `Write UByte token of value 255`() {
        testWriteToken(
            token = 255,
            expectedSize = 1,
            expectedHex = "FF"
        )
    }

    @Test
    fun `Write UShort token of value 65,535`() {
        testWriteToken(
            token = 65_535,
            expectedSize = 2,
            expectedHex = "FF FF"
        )
    }

    @Test
    fun `Write UInt token of value 4,294,967,295`() {
        testWriteToken(
            token = 4_294_967_295,
            expectedSize = 4,
            expectedHex = "FF FF FF FF"
        )
    }
}

private fun testWriteToken(
    token: Long,
    expectedSize: Long,
    expectedHex: String
) {
    val buffer = Buffer()

    assertEquals(
        expected = expectedSize,
        actual = buffer.writeToken(token)
    )

    assertEquals(
        expected = expectedHex,
        actual = buffer.readByteArray().toHexString()
    )
}

private fun ByteArray.toHexString(): String = joinToString(" ") { String.format("%02X", it) }

private fun String.stripComments() =
    splitToSequence('\n')
        .map { it.substringBefore('#').trim() }
        .filter { it.isNotBlank() }
        .joinToString(" ")
