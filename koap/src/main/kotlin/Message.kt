package com.juul.koap

sealed class Message {

    abstract val code: Code
    abstract val token: Long?
    abstract val options: List<Option>
    abstract val payload: ByteArray

    @Suppress("ClassName", "FunctionName") // Names defined to match RFC.
    sealed class Option {

        abstract val number: Int

        data class empty internal constructor(
            override val number: Int
        ) : Option()

        data class opaque internal constructor(
            override val number: Int,
            val value: ByteArray
        ) : Option()

        data class uint internal constructor(
            override val number: Int,
            val value: Long
        ) : Option()

        data class string internal constructor(
            override val number: Int,
            val value: String
        ) : Option()

        /* RFC 7252 5.10. Table 4: Options
         *
         * +-----+----------------+--------+--------+
         * | No. | Name           | Format | Length |
         * +-----+----------------+--------+--------+
         * |   1 | If-Match       | opaque | 0-8    |
         * |   3 | Uri-Host       | string | 1-255  |
         * |   4 | ETag           | opaque | 1-8    |
         * |   5 | If-None-Match  | empty  | 0      |
         * |   7 | Uri-Port       | uint   | 0-2    |
         * |   8 | Location-Path  | string | 0-255  |
         * |  11 | Uri-Path       | string | 0-255  |
         * |  12 | Content-Format | uint   | 0-2    |
         * |  14 | Max-Age        | uint   | 0-4    |
         * |  15 | Uri-Query      | string | 0-255  |
         * |  17 | Accept         | uint   | 0-2    |
         * |  20 | Location-Query | string | 0-255  |
         * |  35 | Proxy-Uri      | string | 1-1034 |
         * |  39 | Proxy-Scheme   | string | 1-255  |
         * |  60 | Size1          | uint   | 0-4    |
         * +-----+----------------+--------+--------+
         */
        companion object {
            fun IfMatch(etag: ByteArray) = opaque(1, etag.requireLength(0..8)) // 5.10.8.1.
            fun UriHost(value: String) = string(3, value.requireLength(1..255))
            fun ETag(value: ByteArray) = opaque(4, value.requireLength(1..8))
            fun IfNoneMatch(value: ByteArray) = empty(5)
            fun UriPort(value: Long) = uint(7, value.requireLength(0..2))
            fun LocationPath(value: String) = string(8, value.requireLength(0..255))
            fun UriPath(value: String) = string(11, value.requireLength(0..255))
            fun ContentFormat(value: Long) = uint(12, value.requireLength(0..2))
            fun MaxAge(value: Long = 60) = uint(14, value.requireLength(0..4))
            fun UriQuery(value: String) = string(15, value.requireLength(0..255))
            fun Accept(value: Long) = uint(17, value.requireLength(0..2))
            fun LocationQuery(value: String) = string(20, value.requireLength(0..255))
            fun ProxyUri(value: String) = string(35, value.requireLength(1..1034))
            fun ProxyScheme(value: String) = string(39, value.requireLength(1..255))
            fun Size1(value: Long) = uint(60, value.requireLength(0..4))

            private fun ByteArray.requireLength(range: IntRange): ByteArray {
                require(size in range) { "Length $size is not within required range $range" }
                return this
            }

            private fun String.requireLength(range: IntRange): String {
                require(length in range) { "Length $length is not within required range $range" }
                return this
            }

            private fun Long.requireLength(range: IntRange): Long {
                require(uint32Size in range) {
                    "Length $uint32Size is not within required range $range"
                }
                return this
            }
        }
    }

    sealed class Code {

        sealed class Method : Code() {
            object GET : Method()
            object POST : Method()
            object PUT : Method()
            object DELETE : Method()
        }

        sealed class Response : Code() {
            object Created : Response()
            object Deleted : Response()
            object Valid : Response()
            object Changed : Response()
            object Content : Response()
            object BadRequest : Response()
            object Unauthorized : Response()
            object BadOption : Response()
            object Forbidden : Response()
            object NotFound : Response()
            object MethodNotAllowed : Response()
            object NotAcceptable : Response()
            object PreconditionFailed : Response()
            object RequestEntityTooLarge : Response()
            object UnsupportedContentFormat : Response()
            object InternalServerError : Response()
            object NotImplemented : Response()
            object BadGateway : Response()
            object ServiceUnavailable : Response()
            object GatewayTimeout : Response()
            object ProxyingNotSupported : Response()
        }
    }

    data class Udp(
        val type: Type,
        override val code: Code, // 8-bit unsigned integer
        val id: Int, // Message ID: 16-bit unsigned integer
        override val token: Long?,
        override val options: List<Option>,
        override val payload: ByteArray
    ) : Message() {

        enum class Type {
            Confirmable,
            NonConfirmable,
            Acknowledgement,
            Reset,
        }
    }

    data class Tcp(
        override val code: Code,
        override val token: Long?,
        override val options: List<Option>,
        override val payload: ByteArray
    ) : Message()
}

/**
 * Determines the smallest number of bytes needed to fit the [Long] receiver as an unsigned 32-bit
 * integer.
 */
@OptIn(ExperimentalStdlibApi::class)
private val Long.uint32Size: Int
    get() = (Long.SIZE_BITS - uint32.countLeadingZeroBits() + 7) / Byte.SIZE_BITS

private val Long.uint32: Long get() = this and 0xFF_FF_FF_FF
