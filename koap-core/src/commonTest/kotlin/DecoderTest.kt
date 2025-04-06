package com.juul.koap.test

import com.juul.koap.Message
import com.juul.koap.Message.Code.Method.GET
import com.juul.koap.Message.Code.Method.POST
import com.juul.koap.Message.Code.Response.Changed
import com.juul.koap.Message.Option.Block.Size.Bert
import com.juul.koap.Message.Option.Block1
import com.juul.koap.Message.Option.Block2
import com.juul.koap.Message.Option.Echo
import com.juul.koap.Message.Option.Edhoc
import com.juul.koap.Message.Option.ExperimentalUse
import com.juul.koap.Message.Option.HopLimit
import com.juul.koap.Message.Option.NoResponse
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
import com.juul.koap.Message.Option.UriHost
import com.juul.koap.Message.Option.UriPath
import com.juul.koap.Message.Option.UriPort
import com.juul.koap.Message.Udp.Type.Confirmable
import com.juul.koap.blockSizeOf
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
    fun decodeSize1Option() {
        testReadOption(
            encoded = """
                D4 2F       # Option Delta: 60, Option Length: 1
                01 02 03 04 # Option Value: 16909060
            """,
            expected = Size1(16909060),
        )
    }

    @Test
    fun decodeSize2Option() {
        testReadOption(
            encoded = """
                D4 0F       # Option Delta: 28, Option Length: 2
                FF FE FD FC # Option Value: 4294901244
            """,
            expected = Size2(4_294_901_244),
        )
    }

    @Test
    fun decodeBlock1Option() {
        testReadOption(
            encoded = """
                D1 0E # Option Delta: 27, Option Length: 1
                AB    # Option Value: 3<<4 | 0x8 | 3
            """,
            expected = Block1(10, true, blockSizeOf(128)),
        )
    }

    @Test
    fun decodeBlock2Option() {
        testReadOption(
            encoded = """
                D2 0A # Option Delta: 23, Option Length: 2
                AB CD # Option Value: 2748<<4 | 0x8 | 5
            """,
            expected = Block2(2_748, true, blockSizeOf(512)),
        )
    }

    @Test
    fun decodeQBlock1Option() {
        testReadOption(
            encoded = """
                D2 06 # Option Delta: 19, Option Length: 1
                12 34 # Option Value: 291<<4 | 0x0 | 4
            """,
            expected = QBlock1(291, false, blockSizeOf(256)),
        )
    }

    @Test
    fun decodeQBlock2Option() {
        testReadOption(
            encoded = """
                D3 12       # Option Delta: 31, Option Length: 1
                12 34 78    # Option Value: 74567<<4 | 0x8 | 0
            """,
            expected = QBlock2(74_567, true, blockSizeOf(16)),
        )
    }

    @Test
    fun decodeBertBlock1Option() {
        testReadOption(
            encoded = """
                D1 0E # Option Delta: 27, Option Length: 1
                A7    # Option Value: 3<<4 | 0x0 | 7
            """,
            expected = Block1(10, false, Bert),
        )
    }

    @Test
    fun decodeOscoreOptionWithEmptyValue() {
        testReadOption(
            encoded = """
                90 # Option Delta: 9, Option Length: 0
            """,
            expected = Oscore(byteArrayOf(), null, null),
        )
    }

    // Test Vector 4: OSCORE Request, Client
    // https://datatracker.ietf.org/doc/html/rfc8613#appendix-C.4
    @Test
    fun decodeOscoreOptionTestVector4() {
        val unprotectedCoapRequest = "44015d1f00003974396c6f63616c686f737483747631".decodeHex().toByteArray().decode<Message.Udp>()

        val oscoreOptionValue = "0914".decodeHex().toByteArray()
        val ciphertext = "612f1092f1776f1c1668b3825e".decodeHex().toByteArray()
        val protectedCoapRequest = "44025d1f00003974396c6f63616c686f7374620914ff612f1092f1776f1c1668b3825e".decodeHex().toByteArray()

        assertEquals(
            expected = Message.Udp(
                type = unprotectedCoapRequest.type,

                // "the Outer Code SHALL be set to 0.02 (POST) for requests [..]"
                // https://datatracker.ietf.org/doc/html/rfc8613#section-4.2
                code = POST,

                id = unprotectedCoapRequest.id,
                token = unprotectedCoapRequest.token,
                options = listOf(
                    UriHost("localhost"),
                    Oscore(partialIv = byteArrayOf(0x14), kidContext = null, kid = byteArrayOf()),
                ),
                payload = ciphertext,
            ),
            actual = protectedCoapRequest.decode<Message.Udp>(),
        )
        assertEquals(
            actual = Oscore.fromOptionValue(oscoreOptionValue),
            expected = Oscore(partialIv = byteArrayOf(0x14), kidContext = null, kid = byteArrayOf()),
        )
    }

    // Test Vector 5: OSCORE Request, Client
    // https://datatracker.ietf.org/doc/html/rfc8613#appendix-C.5
    @Test
    fun decodeOscoreOptionTestVector5() {
        val unprotectedCoapRequest = "440171c30000b932396c6f63616c686f737483747631".decodeHex().toByteArray().decode<Message.Udp>()

        val oscoreOptionValue = "091400".decodeHex().toByteArray()
        val ciphertext = "4ed339a5a379b0b8bc731fffb0".decodeHex().toByteArray()
        val protectedCoapRequest = "440271c30000b932396c6f63616c686f737463091400ff4ed339a5a379b0b8bc731fffb0".decodeHex().toByteArray()

        assertEquals(
            expected = Message.Udp(
                type = unprotectedCoapRequest.type,

                // "the Outer Code SHALL be set to 0.02 (POST) for requests [..]"
                // https://datatracker.ietf.org/doc/html/rfc8613#section-4.2
                code = POST,

                id = unprotectedCoapRequest.id,
                token = unprotectedCoapRequest.token,
                options = listOf(
                    UriHost("localhost"),
                    Oscore(partialIv = byteArrayOf(0x14), kidContext = null, kid = byteArrayOf(0x00)),
                ),
                payload = ciphertext,
            ),
            actual = protectedCoapRequest.decode<Message.Udp>(),
        )
        assertEquals(
            actual = Oscore.fromOptionValue(oscoreOptionValue),
            expected = Oscore(partialIv = byteArrayOf(0x14), kidContext = null, kid = byteArrayOf(0x00)),
        )
    }

    // Test Vector 6: OSCORE Request, Client
    // https://datatracker.ietf.org/doc/html/rfc8613#appendix-C.6
    @Test
    fun decodeOscoreOptionTestVector6() {
        val unprotectedCoapRequest = "44012f8eef9bbf7a396c6f63616c686f737483747631".decodeHex().toByteArray().decode<Message.Udp>()

        val oscoreOptionValue = "19140837cbf3210017a2d3".decodeHex().toByteArray()
        val ciphertext = "72cd7273fd331ac45cffbe55c3".decodeHex().toByteArray()
        val protectedCoapRequest =
            "44022f8eef9bbf7a396c6f63616c686f73746b19140837cbf3210017a2d3ff72cd7273fd331ac45cffbe55c3".decodeHex().toByteArray()

        assertEquals(
            expected = Message.Udp(
                type = unprotectedCoapRequest.type,

                // "the Outer Code SHALL be set to 0.02 (POST) for requests [..]"
                // https://datatracker.ietf.org/doc/html/rfc8613#section-4.2
                code = POST,

                id = unprotectedCoapRequest.id,
                token = unprotectedCoapRequest.token,
                options = listOf(
                    UriHost("localhost"),
                    Oscore(partialIv = byteArrayOf(0x14), kidContext = "37CBF3210017A2D3".decodeHex().toByteArray(), kid = byteArrayOf()),
                ),
                payload = ciphertext,
            ),
            actual = protectedCoapRequest.decode<Message.Udp>(),
        )
        assertEquals(
            actual = Oscore.fromOptionValue(oscoreOptionValue),
            expected = Oscore(
                partialIv = byteArrayOf(0x14), kidContext = "37CBF3210017A2D3".decodeHex().toByteArray(), kid = byteArrayOf(),
            ),
        )
    }

    // Test Vector 7: OSCORE Response, Server
    // https://datatracker.ietf.org/doc/html/rfc8613#appendix-C.7
    @Test
    fun decodeOscoreOptionTestVector7() {
        val unprotectedCoapResponse = "64455d1f00003974ff48656c6c6f20576f726c6421".decodeHex().toByteArray().decode<Message.Udp>()

        val oscoreOptionValue = "".decodeHex().toByteArray()
        val ciphertext = "dbaad1e9a7e7b2a813d3c31524378303cdafae119106".decodeHex().toByteArray()
        val protectedCoapResponse = "64445d1f0000397490ffdbaad1e9a7e7b2a813d3c31524378303cdafae119106".decodeHex().toByteArray()

        assertEquals(
            expected = Message.Udp(
                type = unprotectedCoapResponse.type,

                // "the Outer Code SHALL be set to [..] 2.04 (Changed) for responses"
                // https://datatracker.ietf.org/doc/html/rfc8613#section-4.2
                code = Changed,

                id = unprotectedCoapResponse.id,
                token = unprotectedCoapResponse.token,
                options = listOf(
                    Oscore(partialIv = byteArrayOf(), kidContext = null, kid = null),
                ),
                payload = ciphertext,
            ),
            actual = protectedCoapResponse.decode<Message.Udp>(),
        )
        assertEquals(
            actual = Oscore.fromOptionValue(oscoreOptionValue),
            expected = Oscore(partialIv = byteArrayOf(), kidContext = null, kid = null),
        )
    }

    // Test Vector 8: OSCORE Response with Partial IV, Server
    // https://datatracker.ietf.org/doc/html/rfc8613#appendix-C.8
    @Test
    fun decodeOscoreOptionTestVector8() {
        val unprotectedCoapResponse = "64455d1f00003974ff48656c6c6f20576f726c6421".decodeHex().toByteArray().decode<Message.Udp>()

        val oscoreOptionValue = "0100".decodeHex().toByteArray()
        val ciphertext = "4d4c13669384b67354b2b6175ff4b8658c666a6cf88e".decodeHex().toByteArray()
        val protectedCoapResponse = "64445d1f00003974920100ff4d4c13669384b67354b2b6175ff4b8658c666a6cf88e".decodeHex().toByteArray()

        assertEquals(
            expected = Message.Udp(
                type = unprotectedCoapResponse.type,

                // "the Outer Code SHALL be set to [..] 2.04 (Changed) for responses"
                // https://datatracker.ietf.org/doc/html/rfc8613#section-4.2
                code = Changed,

                id = unprotectedCoapResponse.id,
                token = unprotectedCoapResponse.token,
                options = listOf(
                    Oscore(partialIv = byteArrayOf(0x00), kidContext = null, kid = null),
                ),
                payload = ciphertext,
            ),
            actual = protectedCoapResponse.decode<Message.Udp>(),
        )
        assertEquals(
            actual = Oscore.fromOptionValue(oscoreOptionValue),
            expected = Oscore(partialIv = byteArrayOf(0x00), kidContext = null, kid = null),
        )
    }

    @Test
    fun decodeEdhocOption() {
        testReadOption(
            encoded = """
                D0 08 # Option Delta: 21, Option Length: 0
            """,
            expected = Edhoc,
        )
    }

    @Test
    fun decodeHopLimit() {
        testReadOption(
            encoded = """
                D1 03 # Option Delta: 16, Option Length: 1
                21    # Option Value: 33
            """,
            expected = HopLimit(33),
        )
    }

    @Test
    fun decodeEcho() {
        testReadOption(
            encoded = """
                D9 EF                      # Option Delta: 252, Option Length: 9
                01 02 03 04 05 06 07 08 09 # Option Value: 1 2 3 4 5 6 7 8 9
            """,
            expected = Echo(byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9)),
        )
    }

    @Test
    fun decodeRequestTag() {
        testReadOption(
            encoded = """
                E5 00 17       # Option Delta: 292, Option Length: 3
                01 02 03 04 05 # Option Value: 1 2 3 4 5
            """,
            expected = RequestTag(byteArrayOf(1, 2, 3, 4, 5)),
        )
    }

    @Test
    fun decodeUnassignedOption() {
        testReadOption(
            encoded = """
                E3 11 27 # Option Delta: 0x1234, Option Length: 3
                01 02 03 # Option Value: 0x01, 0x02, 0x03
            """,
            expected = Unassigned(0x1234, byteArrayOf(0x01, 0x02, 0x03)),
        )
    }

    @Test
    fun decodeReservedOption() {
        testReadOption(
            encoded = """
                D3 7F    # Option Delta: 140, Option Length: 3
                61 62 63 # Option Value: 0x61, 0x62, 0x63
            """,
            expected = Reserved(140, byteArrayOf(0x61, 0x62, 0x63)),
        )
    }

    @Test
    fun decodeExperimentalUseOption() {
        testReadOption(
            encoded = """
                E3 FC DE # Option Delta: 65003, Option Length: 3
                41 42 43 # Option Value: 0x41, 0x42, 0x43
            """,
            expected = ExperimentalUse(65003, byteArrayOf(0x41, 0x42, 0x43)),
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
    fun canDecodeTokenOfLength7() {
        val encoded = """
            47                   # 4 = Version: 1, Type: 0 (Confirmable), 7 = Token Length: 7
            01                   # Code: 0.01 (GET)
            FE ED                # Message ID
            CA FE F0 0D AB CD EF # Token (0xCAFE_F00D_ABCDEF = 57_138_252_270_521_839L)
            B7                   # B = Delta option: 11 (Uri-Path), 7 = Delta length: 7
            65 78 61 6D 70 6C 65 # "example"
        """.trimIndent().stripComments().decodeHex().toByteArray()

        val message = encoded.decode<Message.Udp>()
        assertEquals(
            expected = 57_138_252_270_521_839L,
            actual = message.token,
        )

        // Confirm remainder of message decoded properly.
        val uri = message.options
            .filterIsInstance<UriPath>()
            .single()
            .uri
        assertEquals(
            expected = "example",
            actual = uri,
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
