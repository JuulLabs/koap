package com.juul.koap

internal inline fun <T> ByteArray.withReader(
    offset: Int = 0,
    action: ByteArrayReader.() -> T
): T = action.invoke(ByteArrayReader(this, offset))

internal fun ByteArray.reader(
    offset: Int = 0
): ByteArrayReader = ByteArrayReader(this, offset)

/** All numbers are read in network byte-order (Big endian). */
internal class ByteArrayReader(
    private val bytes: ByteArray,
    offset: Int = 0
) {

    var index = offset

    fun exhausted(): Boolean = index >= bytes.size

    /** Reads 1-byte to acquire an 8-bit unsigned int. */
    fun readUByte(): Int = bytes[index++].toInt() and 0xFF

    /** Reads 2-bytes in [ByteArray] receiver to acquire a 16-bit unsigned int. */
    fun readUShort(): Int = ((bytes[index++].toInt() and 0xFF) shl 8) or readUByte()

    /** Reads 3-bytes in [ByteArray] receiver to acquire a 24-bit unsigned int. */
    fun readUInt24(): Int = ((bytes[index++].toInt() and 0xFF) shl 16) or readUShort()

    /** Reads 4-bytes in [ByteArray] receiver to acquire a 32-bit unsigned int. */
    fun readUInt(): Long = ((bytes[index++].toLong() and 0xFF) shl 24) or readUInt24().toLong()

    /** Reads 8-bytes in [ByteArray] receiver to acquire a 64-bit signed int. */
    fun readLong(): Long =
        ((bytes[index++].toLong() and 0xFF) shl 56) or
            ((bytes[index++].toLong() and 0xFF) shl 48) or
            ((bytes[index++].toLong() and 0xFF) shl 40) or
            ((bytes[index++].toLong() and 0xFF) shl 32) or
            readUInt()

    fun readByteArray(): ByteArray {
        val copy = bytes.copyOfRange(index, bytes.size)
        index = bytes.size
        return copy
    }

    fun readByteArray(length: Int): ByteArray {
        val copy = bytes.copyOfRange(index, index + length)
        index += length
        return copy
    }

    fun readUtf8(length: Int): String = readByteArray(length).toString(Charsets.UTF_8)
}
