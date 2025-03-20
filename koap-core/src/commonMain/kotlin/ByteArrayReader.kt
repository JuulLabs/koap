package com.juul.koap

import okio.ByteString.Companion.toByteString

internal inline fun <T> ByteArray.withReader(
    startIndex: Int = 0,
    endIndex: Int = size,
    action: ByteArrayReader.() -> T,
): T = action.invoke(ByteArrayReader(this, startIndex, endIndex))

internal fun ByteArray.reader(
    startIndex: Int = 0,
    endIndex: Int = size,
): ByteArrayReader = ByteArrayReader(this, startIndex, endIndex)

/**
 * All numbers are read in network byte-order (Big endian).
 *
 * @param startIndex the start of the range (inclusive), must be in `0..bytes.size`
 * @param endIndex the end of the range (exclusive), must be in `startIndex..bytes.size`
 */
internal class ByteArrayReader(
    private val bytes: ByteArray,
    startIndex: Int = 0,
    private val endIndex: Int = bytes.size,
) {

    var index = startIndex

    fun exhausted(): Boolean = index >= endIndex

    private fun checkIndex() {
        if (index >= endIndex) {
            throw IndexOutOfBoundsException(
                "Cannot read when index is at or beyond endIndex (index=$index, endIndex=$endIndex)",
            )
        }
    }

    private fun checkLength(length: Int) {
        check(index + length <= endIndex) {
            "Cannot read byte range $index..${index + length} as it spans beyond endIndex of $endIndex"
        }
    }

    /** Reads 1-byte to acquire an 8-bit unsigned int. */
    fun readUByte(): Int {
        checkIndex()
        return bytes[index++].toInt() and 0xFF
    }

    /** Reads 2-bytes to acquire a 16-bit unsigned int. */
    fun readUShort(): Int {
        checkIndex()
        return ((bytes[index++].toInt() and 0xFF) shl 8) or readUByte()
    }

    /** Reads 3-bytes to acquire a 24-bit unsigned int. */
    fun readUInt24(): Int {
        checkIndex()
        return ((bytes[index++].toInt() and 0xFF) shl 16) or readUShort()
    }

    /** Reads 4-bytes to acquire a 32-bit unsigned int. */
    fun readUInt(): Long {
        checkIndex()
        return ((bytes[index++].toLong() and 0xFF) shl 24) or readUInt24().toLong()
    }

    /** Reads [n]-bytes to acquire a 64-bit signed int. */
    fun readNLong(n: Int): Long {
        checkLength(n)
        var result = 0L
        for (i in 0 until n) {
            result = (result shl 8) or (bytes[index++].toLong() and 0xFF)
        }
        return result
    }

    /** Reads 8-bytes to acquire a 64-bit signed int. */
    fun readLong(): Long {
        checkIndex()
        return ((bytes[index++].toLong() and 0xFF) shl 56) or
            ((bytes[index++].toLong() and 0xFF) shl 48) or
            ((bytes[index++].toLong() and 0xFF) shl 40) or
            ((bytes[index++].toLong() and 0xFF) shl 32) or
            readUInt()
    }

    /** Reads bytes from [index] (inclusive) to [endIndex] (exclusive). */
    fun readByteArray(): ByteArray {
        val copy = bytes.copyOfRange(index, endIndex)
        index = endIndex
        return copy
    }

    fun readByteArray(length: Int): ByteArray {
        checkLength(length)
        val copy = bytes.copyOfRange(index, index + length)
        index += length
        return copy
    }

    fun readUtf8(length: Int): String {
        checkLength(length)
        val utf8 = bytes.toByteString(index, length).utf8()
        index += length
        return utf8
    }
}
