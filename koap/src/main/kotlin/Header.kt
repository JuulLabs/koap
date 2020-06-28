package com.juul.koap

import com.juul.koap.Message.Code
import com.juul.koap.Message.Udp.Type

sealed class Header {

    /** Size (in bytes) of the encoded CoAP header. */
    abstract val size: Int

    abstract val code: Code // 8-bit unsigned integer

    /**
     * Per RFC 7252 2.2. Request/Response Model:
     *
     * > A Token is used to match responses to requests independently from the underlying
     * > messages.
     *
     * A token may be 0-8 bytes, whereas [token] is represented as a [Long]. The following
     * ranges outline the number of bytes that will be occupied when encoded as CoAP.
     *
     * | Range                          | Bytes |
     * |--------------------------------|-------|
     * |         -2^63 .. -1            | 8     |
     * |             0 .. 255           | 1     |
     * |           256 .. 65,635        | 2     |
     * |        65,536 .. 4,294,967,295 | 4     |
     * | 4,294,967,296 .. 2^63-1        | 8     |
     */
    abstract val token: Long?

    data class Udp internal constructor(
        override val size: Int,
        val version: Int,
        val type: Type,
        override val code: Code,
        val messageId: Int, // 16-bit unsigned integer
        override val token: Long?
    ) : Header() {

        override fun toString(): String = "Header.Udp(" +
            "size=$size, " +
            "version=$version, " +
            "type=$type, " +
            "code=$code, " +
            "messageId=${messageId.debugString(Short.SIZE_BYTES)}, " +
            "token=${token.debugString()}" +
            ")"
    }

    data class Tcp internal constructor(
        override val size: Int,

        /** Length (in bytes) of message content (Options + Payload). */
        val length: Long, // 32-bit unsigned integer

        override val code: Code,
        override val token: Long?
    ) : Header() {

        override fun toString(): String = "Header.Tcp(" +
            "size=$size, " +
            "length=$length, " +
            "code=$code, " +
            "token=${token.debugString()}" +
            ")"
    }
}
