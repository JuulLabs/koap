package com.juul.koap

import com.juul.koap.Message.Code.Method
import com.juul.koap.Message.Option.UriPath
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
            code = Method.GET,
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
            code = Method.GET,
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
}

private fun ByteArray.toHexString(): String = joinToString(" ") { String.format("%02X", it) }

private fun String.stripComments() =
    splitToSequence('\n')
        .map { it.substringBefore('#').trim() }
        .filter { it.isNotBlank() }
        .joinToString(" ")
