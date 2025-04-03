package com.juul.koap.test

import com.juul.koap.Message
import com.juul.koap.Message.Code.Method.GET
import com.juul.koap.Message.Code.Response.Content
import com.juul.koap.Message.Option.Block.Size.Bert
import com.juul.koap.Message.Option.Block1
import com.juul.koap.Message.Option.Block2
import com.juul.koap.Message.Option.Echo
import com.juul.koap.Message.Option.Edhoc
import com.juul.koap.Message.Option.ExperimentalUse
import com.juul.koap.Message.Option.HopLimit
import com.juul.koap.Message.Option.NoResponse
import com.juul.koap.Message.Option.NoResponse.NotInterestedIn.Response2xx
import com.juul.koap.Message.Option.NoResponse.NotInterestedIn.Response4xx
import com.juul.koap.Message.Option.NoResponse.NotInterestedIn.Response5xx
import com.juul.koap.Message.Option.Observe
import com.juul.koap.Message.Option.Observe.Registration.Deregister
import com.juul.koap.Message.Option.Observe.Registration.Register
import com.juul.koap.Message.Option.Oscore
import com.juul.koap.Message.Option.QBlock1
import com.juul.koap.Message.Option.QBlock2
import com.juul.koap.Message.Option.RequestTag
import com.juul.koap.Message.Option.Reserved
import com.juul.koap.Message.Option.Size1
import com.juul.koap.Message.Option.Size2
import com.juul.koap.Message.Option.Unassigned
import com.juul.koap.Message.Option.UriPath
import com.juul.koap.Message.Option.UriPort
import com.juul.koap.Message.Udp.Type.Acknowledgement
import com.juul.koap.Message.Udp.Type.Confirmable
import com.juul.koap.UBYTE_MAX_VALUE
import com.juul.koap.UINT32_MAX_EXTENDED_LENGTH
import com.juul.koap.UINT_MAX_VALUE
import com.juul.koap.USHORT_MAX_VALUE
import com.juul.koap.blockSizeOf
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
    fun writeNoResponse2xx4xx5xxOption() {
        testWriteOption(
            option = NoResponse(Response2xx, Response4xx, Response5xx),
            expected = """
                D1 F5 # Option Delta: 258, Option Length: 1
                1A    # Option Value: 26
            """,
        )
    }

    @Test
    fun writeNoResponseEmptyOption() {
        testWriteOption(
            option = NoResponse(),
            expected = """
                D0 F5 # Option Delta: 258, Option Length: 0 (Option Value of 0 is implied)
            """,
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
    fun writeOscoreOptionWithEmptyValue() {
        testWriteOption(
            option = Oscore(byteArrayOf()),
            expected = """
                90 # Option Delta: 9, Option Length: 0
            """,
        )
    }

    @Test
    fun writeOscoreOptionTestVector4() {
        // https://datatracker.ietf.org/doc/html/rfc8613#appendix-C.4 Test Vector 4
        testWriteOption(
            option = Oscore(byteArrayOf(0x09, 0x14)),
            expected = """
                92    # Option Delta: 9, Option Length: 2
                09 14 # Option Value: 09 14
            """,
        )
    }

    @Test
    fun writeEdhocOption() {
        testWriteOption(
            option = Edhoc,
            expected = """
                D0 08 # Option Delta: 21, Option Length: 0
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
    fun writeSize1Option() {
        testWriteOption(
            option = Size1(255),
            expected = """
                D1 2F # Option Delta: 60, Option Length: 1
                FF    # Option Value: 255
            """,
        )
    }

    @Test
    fun writeSize2Option() {
        testWriteOption(
            option = Size2(1337),
            expected = """
                D2 0F # Option Delta: 28, Option Length: 2
                05 39 # Option Value: 1337
            """,
        )
    }

    @Test
    fun writeBlock1Option() {
        testWriteOption(
            option = Block1(3, true, blockSizeOf(128)),
            expected = """
                D1 0E # Option Delta: 27, Option Length: 1
                3B    # Option Value: 3<<4 | 0x8 | 3
            """,
        )
    }

    @Test
    fun writeBlock2Option() {
        testWriteOption(
            option = Block2(17, true, blockSizeOf(1024)),
            expected = """
                D2 0A # Option Delta: 23, Option Length: 2
                01 1E # Option Value: 17<<4 | 0x8 | 6
            """,
        )
    }

    @Test
    fun writeQBlock1Option() {
        testWriteOption(
            option = QBlock1(170, false, blockSizeOf(256)),
            expected = """
                D2 06 # Option Delta: 19, Option Length: 1
                0A A4 # Option Value: 170<<4 | 0x0 | 4
            """,
        )
    }

    @Test
    fun writeQBlock2Option() {
        testWriteOption(
            option = QBlock2(2, false, blockSizeOf(512)),
            expected = """
                D1 12 # Option Delta: 31, Option Length: 1
                25    # Option Value: 2<<4 | 0x0 | 5
            """,
        )
    }

    @Test
    fun writeBlock1BertOption() {
        testWriteOption(
            option = Block1(1_048_575, false, Bert),
            expected = """
                D3 0E    # Option Delta: 27, Option Length: 1
                FF FF F7 # Option Value: 1048575<<4 | 0x0 | 7
            """,
        )
    }

    @Test
    fun blockOptionWithValueOutsideOfAllowableRangeThrowsIllegalArgumentException() {
        assertFailsWith<IllegalArgumentException> {
            Block1(0xF_FF_FF + 1, false, blockSizeOf(16))
        }
    }

    @Test
    fun writeHopLimit() {
        testWriteOption(
            option = HopLimit(17),
            expected = """
                D1 03 # Option Delta: 16, Option Length: 1
                11    # Option Value: 17
            """,
        )
    }

    @Test
    fun writeEcho() {
        testWriteOption(
            option = Echo(byteArrayOf(1, 2, 3, 4, 5, 6, 7)),
            expected = """
                D7 EF                # Option Delta: 252, Option Length: 7
                01 02 03 04 05 06 07 # Option Value: 1 2 3 4 5 6 7
            """,
        )
    }

    @Test
    fun writeRequestTag() {
        testWriteOption(
            option = RequestTag(byteArrayOf(1, 2, 3)),
            expected = """
                E3 00 17 # Option Delta: 292, Option Length: 3
                01 02 03 # Option Value: 1 2 3
            """,
        )
    }

    @Test
    fun writeUnassignedOption() {
        testWriteOption(
            option = Unassigned(0x4321, byteArrayOf(0x04, 0x03, 0x02, 0x01)),
            expected = """
                E4 42 14    # Option Delta: 0x4321, Option Length: 4
                04 03 02 01 # Option Value: 0x04, 0x03, 0x02, 0x01
            """,
        )
    }

    @Test
    fun writeReservedOption() {
        testWriteOption(
            option = Reserved(136, byteArrayOf(0x34, 0x33, 0x32, 0x31)),
            expected = """
                D4 7B       # Option Delta: 136, Option Length: 4
                34 33 32 31 # Option Value: 0x34, 0x33, 0x32, 0x31
            """,
        )
    }

    @Test
    fun writeExperimentalUseOption() {
        testWriteOption(
            option = ExperimentalUse(65007, byteArrayOf(0x24, 0x23, 0x22, 0x21)),
            expected = """
                E4 FC E2    # Option Delta: 65007, Option Length: 4
                24 23 22 21 # Option Value: 0x24, 0x23, 0x22, 0x21
            """,
        )
    }

    @Test
    fun experimentalUseOptionWithNumberOutsideExperimentalRangeThrowsIllegalArgumentException() {
        assertFailsWith<IllegalArgumentException> {
            ExperimentalUse(64999, byteArrayOf())
        }
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
