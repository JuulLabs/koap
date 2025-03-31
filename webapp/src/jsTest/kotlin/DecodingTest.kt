package com.juul.koap

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise
import okio.ByteString.Companion.decodeHex
import kotlin.test.Test
import kotlin.test.assertEquals

class DecodingTest {

    @Test
    fun coapWithJsonPayload() = GlobalScope.promise {
        /* Message.Udp(
         *     type = Confirmable,
         *     code = GET,
         *     id = 0xFEED,
         *     token = 0xCAFE,
         *     options = listOf(
         *         UriPath("example"),
         *         ContentFormat.JSON,
         *     ),
         *     payload = {"example": 123}
         * )
         */
        val input = "42 01 FE ED CA FE B7 65 78 61 6D 70 6C 65 11 32 FF 7B 22 65 78 61 6D 70 6C 65 22 3A 20 31 32 33 7D"
            .replace(" ", "")
            .decodeHex()
            .toByteArray()

        assertEquals(
            expected = """
                <b>Message:</b>
                {
                  "type": "Confirmable",
                  "code": "GET",
                  "id": 65261,
                  "token": 51966,
                  "options": [
                    "UriPath(uri=example)",
                    "Content-Format: application/json"
                  ]
                }

                <b>Payload (JSON):</b>
                {
                  "example": 123
                }
            """.trimIndent(),
            actual = decode<Message.Udp>(input).trim(),
        )

        assertEquals(
            expected = "Unsupported number length of 10 bytes",
            actual = decode<Message.Tcp>(input),
        )
    }

    @Test
    fun coapWithCborPayload() = GlobalScope.promise {
        /* Message.Udp(
         *     type = Confirmable,
         *     code = GET,
         *     id = 0xFEED,
         *     token = 0xCAFE,
         *     options = listOf(
         *         UriPath("example"),
         *         ContentFormat.CBOR,
         *     ),
         *     payload = <CBOR>,
         * )
         *
         * CBOR payload:
         * BF           # map(*)
         *   61        # text(1)
         *      61     # "a"
         *   63        # text(3)
         *      313233 # "123"
         *   61        # text(1)
         *      62     # "b"
         *   63        # text(3)
         *      393837 # "987"
         *   FF        # primitive(*)
         */
        val input = "42 01 FE ED CA FE B7 65 78 61 6D 70 6C 65 11 3C FF BF 61 61 63 31 32 33 61 62 63 39 38 37 FF"
            .replace(" ", "")
            .decodeHex()
            .toByteArray()

        assertEquals(
            expected = "Unsupported number length of 10 bytes",
            actual = decode<Message.Tcp>(input),
        )

        assertEquals(
            expected = """
                <b>Message:</b>
                {
                  "type": "Confirmable",
                  "code": "GET",
                  "id": 65261,
                  "token": 51966,
                  "options": [
                    "UriPath(uri=example)",
                    "Content-Format: application/cbor"
                  ]
                }

                <b>Payload (CBOR):</b>
                {_ "a": "123", "b": "987"}
            """.trimIndent(),
            actual = decode<Message.Udp>(input).trim(),
        )
    }

    @Test
    fun coapWithUnassignedOption() = GlobalScope.promise {
        /* Message.Udp(
         *     type = Confirmable,
         *     code = GET,
         *     id = 0xFEED,
         *     token = 0xCAFE,
         *     options = listOf(
         *         ContentFormat.CBOR,
         *         Unassigned(0x1234, byteArrayOf(0x01, 0x02, 0xDE)),
         *     ),
         *     payload = <CBOR>,
         * )
         *
         * CBOR payload:
         * 83          # array(3)
         *   61        # text(1)
         *      61     # "a"
         *   63        # text(3)
         *      313233 # "123"
         *   61        # text(1)
         *      62     # "b"
         */
        val input = "42 01 FE ED CA FE C1 3C E3 11 1B 01 02 DE FF 83 61 61 63 31 32 33 61 62"
            .replace(" ", "")
            .decodeHex()
            .toByteArray()

        assertEquals(
            expected = "Unsupported number length of 10 bytes",
            actual = decode<Message.Tcp>(input),
        )

        assertEquals(
            expected = """
                <b>Message:</b>
                {
                  "type": "Confirmable",
                  "code": "GET",
                  "id": 65261,
                  "token": 51966,
                  "options": [
                    "Content-Format: application/cbor",
                    "Unassigned(number=4660, value=01 02 DE)"
                  ]
                }

                <b>Payload (CBOR):</b>
                ["a", "123", "b"]
            """.trimIndent(),
            actual = decode<Message.Udp>(input).trim(),
        )
    }

    @Test
    fun coapWithReservedOption() = GlobalScope.promise {
        /* Message.Udp(
         *     type = Confirmable,
         *     code = GET,
         *     id = 0xFEED,
         *     token = 0xCAFE,
         *     options = listOf(
         *         ContentFormat.CBOR,
         *         Reserved(132, byteArrayOf(0x01, 0x02, 0xDE)),
         *     ),
         *     payload = <CBOR>,
         * )
         *
         * CBOR payload:
         * 83          # array(3)
         *   61        # text(1)
         *      61     # "a"
         *   63        # text(3)
         *      313233 # "123"
         *   61        # text(1)
         *      62     # "b"
         */
        val input = "42 01 FE ED CA FE C1 3C D3 6B 01 02 DE FF 83 61 61 63 31 32 33 61 62"
            .replace(" ", "")
            .decodeHex()
            .toByteArray()

        assertEquals(
            expected = "Unsupported number length of 10 bytes",
            actual = decode<Message.Tcp>(input),
        )

        assertEquals(
            expected = """
                <b>Message:</b>
                {
                  "type": "Confirmable",
                  "code": "GET",
                  "id": 65261,
                  "token": 51966,
                  "options": [
                    "Content-Format: application/cbor",
                    "Reserved(number=132, value=01 02 DE)"
                  ]
                }

                <b>Payload (CBOR):</b>
                ["a", "123", "b"]
            """.trimIndent(),
            actual = decode<Message.Udp>(input).trim(),
        )
    }

    @Test
    fun coapWithExperimentalUseOption() = GlobalScope.promise {
        /* Message.Udp(
         *     type = Confirmable,
         *     code = GET,
         *     id = 0xFEED,
         *     token = 0xCAFE,
         *     options = listOf(
         *         ContentFormat.CBOR,
         *         ExperimentalUse(65011, byteArrayOf(0x01, 0x02, 0xDE)),
         *     ),
         *     payload = <CBOR>,
         * )
         *
         * CBOR payload:
         * 83          # array(3)
         *   61        # text(1)
         *      61     # "a"
         *   63        # text(3)
         *      313233 # "123"
         *   61        # text(1)
         *      62     # "b"
         */
        val input = "42 01 FE ED CA FE C1 3C E3 FC DA 01 02 DE FF 83 61 61 63 31 32 33 61 62"
            .replace(" ", "")
            .decodeHex()
            .toByteArray()

        assertEquals(
            expected = "Unsupported number length of 10 bytes",
            actual = decode<Message.Tcp>(input),
        )

        assertEquals(
            expected = """
                <b>Message:</b>
                {
                  "type": "Confirmable",
                  "code": "GET",
                  "id": 65261,
                  "token": 51966,
                  "options": [
                    "Content-Format: application/cbor",
                    "ExperimentalUse(number=65011, value=01 02 DE)"
                  ]
                }

                <b>Payload (CBOR):</b>
                ["a", "123", "b"]
            """.trimIndent(),
            actual = decode<Message.Udp>(input).trim(),
        )
    }
}
