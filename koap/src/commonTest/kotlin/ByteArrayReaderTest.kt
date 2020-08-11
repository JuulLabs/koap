package com.juul.koap.test

import com.juul.koap.readNumberOfLength
import com.juul.koap.reader
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ByteArrayReaderTest {

    @Test
    @JsName("readUShortWithValueOf1AtAnOffset")
    fun `readUShort with value of 1, at an offset`() {
        //    offset  0  1
        //           __ __
        val bytes = "00 00 01 00".parseByteArray()
        //              ^^ ^^
        //         byte  0  1

        assertEquals(
            expected = 1, // 00 01
            actual = bytes.reader(startIndex = 1).readUShort()
        )
    }

    /**
     * Java signed short has a range of -32,768..32,767.
     * This test validates that we can read an unsigned short greater than 32,767 (we store it in
     * Java's signed int to prevent overflow).
     */
    @Test
    @JsName("readUShortWithValueThatWontFitInSignedShortAtAnOffset")
    fun `readUShort with value that won't fit in signed short, at an offset`() {
        //    offset  0  1
        //           __ __
        val bytes = "00 FF 00 00".parseByteArray()
        //              ^^ ^^
        //         byte  0  1

        assertEquals(
            expected = 65_280, // FF 00
            actual = bytes.reader(startIndex = 1).readUShort()
        )
    }

    @Test
    @JsName("readNumberOfLengthWithValueThatWontFitInSignedShortAtAnOffset")
    fun `readNumberOfLength with value that won't fit in signed short, at an offset`() {
        //    offset  0  1
        //           __ __
        val bytes = "00 FF 00 00".parseByteArray()
        //              ^^ ^^
        //         byte  0  1

        assertEquals(
            expected = 65_280, // FF 00
            actual = bytes.reader(startIndex = 1).readNumberOfLength(bytes = 2)
        )
    }

    @Test
    @JsName("readLongReadsLongMaxValueAtAnOffset")
    fun `readLong reads Long MAX_VALUE at an offset`() {
        //    offset  0  1  2
        //           __ __ __
        val bytes = "00 00 7F FF FF FF FF FF FF FF 00 00 00 00".parseByteArray()
        //                 ^^ ^^ ^^ ^^ ^^ ^^ ^^ ^^
        //            byte  0  1  2  3  4  5  6  7

        assertEquals(
            expected = Long.MAX_VALUE,
            actual = bytes.reader(startIndex = 2).readLong()
        )
    }

    @Test
    @JsName("exhaustedReturnsFalseUntilAtEndOfByteArray")
    fun `exhausted returns false until at end of ByteArray`() {
        val reader = "01 02 03 04 05".parseByteArray().reader()
        //            ^^ ^^ ^^ ^^ ^^
        //       byte  0  1  2  3  4

        assertFalse { reader.exhausted() }
        reader.readUInt() // Reads 4 bytes, putting us at byte 4.

        assertFalse { reader.exhausted() }
        reader.readUByte() // Reads 1 byte, putting us at end of ByteArray.

        assertTrue { reader.exhausted() }
    }
}

private fun String.parseByteArray() =
    replace(" ", "")
        .chunked(2)
        .map { it.toUpperCase().toInt(16).toByte() }
        .toByteArray()