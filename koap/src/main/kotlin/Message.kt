package com.juul.koap

sealed class Message {

    abstract val code: Code
    abstract val token: Long?
    abstract val options: List<Option>
    abstract val payload: ByteArray

    @Suppress("ClassName") // Names defined to match RFC.
    sealed class Option {

        /** RFC 7252 3.1. Option Format */
        sealed class Format : Option() {

            abstract val number: Int

            data class empty constructor(
                override val number: Int
            ) : Format()

            data class opaque constructor(
                override val number: Int,
                val value: ByteArray
            ) : Format() {

                override fun equals(other: Any?): Boolean {
                    if (this === other) return true
                    if (javaClass != other?.javaClass) return false
                    other as opaque
                    if (number != other.number) return false
                    if (!value.contentEquals(other.value)) return false
                    return true
                }

                override fun hashCode(): Int {
                    var result = number
                    result = 31 * result + value.contentHashCode()
                    return result
                }
            }

            data class uint constructor(
                override val number: Int,
                val value: Long
            ) : Format()

            data class string constructor(
                override val number: Int,
                val value: String
            ) : Format()
        }

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

        /** RFC 7252 5.10.1. Uri-Host, Uri-Port, Uri-Path, and Uri-Query */
        data class UriHost(val uri: String) : Option() {
            init {
                uri.requireLength(1..255)
            }
        }

        /** RFC 7252 5.10.1. Uri-Host, Uri-Port, Uri-Path, and Uri-Query */
        data class UriPort(val port: Long) : Option() {
            init {
                port.requireUShort()
            }
        }

        /**
         * RFC 7252 5.10.1. Uri-Host, Uri-Port, Uri-Path, and Uri-Query
         *
         * > The Uri-Path and Uri-Query Option can contain any character sequence. No percent-
         * > encoding is performed. The value of a Uri-Path Option MUST NOT be "." or ".." (as the
         * > request URI must be resolved before parsing it into options).
         */
        data class UriPath(val uri: String) : Option() {
            init {
                require(uri != "." && uri != "..") { "Uri-Path must not be \".\" or \"..\"" }
                uri.requireLength(0..255)
            }
        }

        /**
         * RFC 7252 5.10.1. Uri-Host, Uri-Port, Uri-Path, and Uri-Query
         *
         * @see UriPath
         */
        data class UriQuery(val uri: String) : Option() {
            init {
                require(uri != "." && uri != "..") { "Uri-Query must not be \".\" or \"..\"" }
                uri.requireLength(0..255)
            }
        }

        /** RFC 7252 5.10.2. Proxy-Uri and Proxy-Scheme */
        data class ProxyUri(val uri: String) : Option() {
            init {
                uri.requireLength(1..1034)
            }
        }

        /** RFC 7252 5.10.2. Proxy-Uri and Proxy-Scheme */
        data class ProxyScheme(val uri: String) : Option() {
            init {
                uri.requireLength(1..255)
            }
        }

        /** RFC 7252 5.10.3. Content-Format */
        data class ContentFormat(val format: Long) : Option() {
            init {
                format.requireUShort()
            }
        }

        /** RFC 7252 5.10.4. Accept */
        data class Accept(val format: Long) : Option() {
            init {
                format.requireUShort()
            }
        }

        /** RFC 7252 5.10.5. Max-Age */
        data class MaxAge(val seconds: Long) : Option() {
            init {
                require(seconds.fitsInUInt()) { // ~136.1 years
                    "Max-Age of $seconds seconds is outside of allowable range"
                }
            }
        }

        /** RFC 7252 5.10.6. ETag */
        data class ETag(val etag: ByteArray) : Option() {
            init {
                etag.requireLength(1..8)
            }

            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false
                other as ETag
                if (!etag.contentEquals(other.etag)) return false
                return true
            }

            override fun hashCode(): Int = etag.contentHashCode()
        }

        /** RFC 7252 5.10.7. Location-Path and Location-Query */
        data class LocationPath(val uri: String) : Option() {
            init {
                uri.requireLength(0..255)
            }
        }

        /** RFC 7252 5.10.7. Location-Path and Location-Query */
        data class LocationQuery(val uri: String) : Option() {
            init {
                uri.requireLength(0..255)
            }
        }

        /** RFC 7252 5.10.8.1. If-Match */
        data class IfMatch(val etag: ByteArray) : Option() {
            init {
                etag.requireLength(0..8)
            }

            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false
                other as IfMatch
                if (!etag.contentEquals(other.etag)) return false
                return true
            }

            override fun hashCode(): Int {
                return etag.contentHashCode()
            }
        }

        /** RFC 7252 5.10.8.2. If-None-Match */
        object IfNoneMatch : Option()

        /** RFC 7252 5.10.9. Size1 Option */
        data class Size1(val bytes: Long) : Option() {
            init {
                bytes.requireUInt()
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

        data class Raw(
            val `class`: Int,
            val detail: Int
        ) : Code()
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

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            other as Udp
            if (type != other.type) return false
            if (code != other.code) return false
            if (id != other.id) return false
            if (token != other.token) return false
            if (options != other.options) return false
            if (!payload.contentEquals(other.payload)) return false
            return true
        }

        override fun hashCode(): Int {
            var result = type.hashCode()
            result = 31 * result + code.hashCode()
            result = 31 * result + id
            result = 31 * result + (token?.hashCode() ?: 0)
            result = 31 * result + options.hashCode()
            result = 31 * result + payload.contentHashCode()
            return result
        }
    }

    data class Tcp(
        override val code: Code,
        override val token: Long?,
        override val options: List<Option>,
        override val payload: ByteArray
    ) : Message() {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            other as Tcp
            if (code != other.code) return false
            if (token != other.token) return false
            if (options != other.options) return false
            if (!payload.contentEquals(other.payload)) return false
            return true
        }

        override fun hashCode(): Int {
            var result = code.hashCode()
            result = 31 * result + (token?.hashCode() ?: 0)
            result = 31 * result + options.hashCode()
            result = 31 * result + payload.contentHashCode()
            return result
        }
    }
}
