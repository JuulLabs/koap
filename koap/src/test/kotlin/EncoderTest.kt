package com.juul.koap

import com.juul.koap.Message.Code.Method
import com.juul.koap.Message.Option.Companion.UriPath
import com.juul.koap.Message.Udp.Type.Confirmable
import io.mockk.every
import io.mockk.mockk
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
        val content = mockk<Buffer> {
            every { size } returns UINT32_MAX_EXTENDED_LENGTH
        }

        val buffer = Buffer().apply { writeHeader(message, content) }
        assertEquals(
            expected = """
                F0          # Len of 15 (Reserved) indicating content length >= 65805, Token Length of 0
                FF FF FF FF # Extended Content Length at max allowable
                01          # Code of 1 (GET)
            """.stripComments(),
            actual = buffer.readByteArray().toHexString()
        )
    }

    @Test
    fun `Too long of UriPath throws IllegalArgumentException`() {
        val path = (0..256).joinToString { it.toString() }
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
