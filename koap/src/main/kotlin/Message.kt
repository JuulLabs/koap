package com.juul.koap

import java.util.Objects

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
private val IF_MATCH_SIZE_RANGE = 0..8
private val URI_HOST_LENGTH_RANGE = 1..255
private val ETAG_SIZE_RANGE = 1..8
private val URI_PORT_RANGE = USHORT_RANGE
private val LOCATION_PATH_LENGTH_RANGE = 0..255
private val URI_PATH_LENGTH_RANGE = 0..255
private val CONTENT_FORMAT_RANGE = USHORT_RANGE
private val MAX_AGE_RANGE = UINT_RANGE
private val URI_QUERY_LENGTH_RANGE = 0..255
private val ACCEPT_RANGE = USHORT_RANGE
private val LOCATION_QUERY_LENGTH_RANGE = 0..255
private val PROXY_URI_LENGTH_RANGE = 1..1034
private val PROXY_SCHEME_LENGTH_RANGE = 1..255
private val SIZE1_RANGE = UINT_RANGE

sealed class Message {

    abstract val code: Code

    /**
     * Per RFC 7252 2.2. Request/Response Model:
     *
     * > A Token is used to match responses to requests independently from the underlying messages.
     *
     * A token may be 0-8 bytes, whereas [token] is represented as a [Long]. The following ranges
     * outline the number of bytes that will be occupied when encoded as CoAP.
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

    abstract val options: List<Option>
    abstract val payload: ByteArray

    @Suppress("ClassName") // Names defined to match RFC.
    sealed class Option {

        /** RFC 7252 3.1. Option Format */
        sealed class Format : Option() {

            abstract val number: Int

            data class empty(
                override val number: Int
            ) : Format()

            data class opaque(
                override val number: Int,
                val value: ByteArray
            ) : Format() {

                override fun equals(other: Any?): Boolean =
                    this === other ||
                        (other is opaque && number == other.number && value.contentEquals(other.value))

                override fun hashCode(): Int = Objects.hash(number, value.contentHashCode())
            }

            data class uint(
                override val number: Int,
                val value: Long
            ) : Format()

            data class string(
                override val number: Int,
                val value: String
            ) : Format()
        }

        /** RFC 7252 5.10.1. Uri-Host, Uri-Port, Uri-Path, and Uri-Query */
        data class UriHost(val uri: String) : Option() {
            init {
                require(uri.length in URI_HOST_LENGTH_RANGE) {
                    "Uri-Host length of ${uri.length} is outside allowable range of $URI_HOST_LENGTH_RANGE"
                }
            }
        }

        /** RFC 7252 5.10.1. Uri-Host, Uri-Port, Uri-Path, and Uri-Query */
        data class UriPort(val port: Long) : Option() {
            init {
                require(port in URI_PORT_RANGE) {
                    "Uri-Port value of $port is outside allowable range of $URI_PORT_RANGE"
                }
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
                require(uri.length in URI_PATH_LENGTH_RANGE) {
                    "Uri-Path length of ${uri.length} is outside allowable range of $URI_PATH_LENGTH_RANGE"
                }
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
                require(uri.length in URI_QUERY_LENGTH_RANGE) {
                    "Uri-Query length of ${uri.length} is outside allowable range of $URI_QUERY_LENGTH_RANGE"
                }
            }
        }

        /** RFC 7252 5.10.2. Proxy-Uri and Proxy-Scheme */
        data class ProxyUri(val uri: String) : Option() {
            init {
                require(uri.length in PROXY_URI_LENGTH_RANGE) {
                    "Proxy-Uri length of ${uri.length} is outside allowable range of $PROXY_URI_LENGTH_RANGE"
                }
            }
        }

        /** RFC 7252 5.10.2. Proxy-Uri and Proxy-Scheme */
        data class ProxyScheme(val uri: String) : Option() {
            init {
                require(uri.length in PROXY_SCHEME_LENGTH_RANGE) {
                    "Proxy-Scheme length of ${uri.length} is outside allowable range of $PROXY_SCHEME_LENGTH_RANGE"
                }
            }
        }

        /** RFC 7252 5.10.3. Content-Format */
        data class ContentFormat(val format: Long) : Option() {
            init {
                require(format in CONTENT_FORMAT_RANGE) {
                    "Content-Format of $format is outside allowable range of $CONTENT_FORMAT_RANGE"
                }
            }
        }

        /** RFC 7252 5.10.4. Accept */
        data class Accept(val format: Long) : Option() {
            init {
                require(format in ACCEPT_RANGE) {
                    "Accept format of $format is outside allowable range of $ACCEPT_RANGE"
                }
            }
        }

        /** RFC 7252 5.10.5. Max-Age */
        data class MaxAge(val seconds: Long) : Option() {
            init {
                require(seconds in MAX_AGE_RANGE) { // ~136.1 years
                    "Max-Age of $seconds seconds is outside of allowable range of $MAX_AGE_RANGE"
                }
            }
        }

        /** RFC 7252 5.10.6. ETag */
        data class ETag(val etag: ByteArray) : Option() {
            init {
                require(etag.size in ETAG_SIZE_RANGE) {
                    "ETag length of ${etag.size} is outside allowable range of $ETAG_SIZE_RANGE"
                }
            }

            override fun equals(other: Any?): Boolean =
                this === other || (other is ETag && etag.contentEquals(other.etag))

            override fun hashCode(): Int = etag.contentHashCode()
        }

        /** RFC 7252 5.10.7. Location-Path and Location-Query */
        data class LocationPath(val uri: String) : Option() {
            init {
                require(uri.length in LOCATION_PATH_LENGTH_RANGE) {
                    "Location-Path length of ${uri.length} is outside allowable range of $LOCATION_PATH_LENGTH_RANGE"
                }
            }
        }

        /** RFC 7252 5.10.7. Location-Path and Location-Query */
        data class LocationQuery(val uri: String) : Option() {
            init {
                require(uri.length in LOCATION_QUERY_LENGTH_RANGE) {
                    "Location-Query length of ${uri.length} is outside allowable range of $LOCATION_QUERY_LENGTH_RANGE"
                }
            }
        }

        /** RFC 7252 5.10.8.1. If-Match */
        data class IfMatch(val etag: ByteArray) : Option() {
            init {
                require(etag.size in IF_MATCH_SIZE_RANGE) {
                    "If-Match length of ${etag.size} is outside allowable range of $IF_MATCH_SIZE_RANGE"
                }
            }

            override fun equals(other: Any?): Boolean =
                this === other || (other is IfMatch && etag.contentEquals(other.etag))

            override fun hashCode(): Int = etag.contentHashCode()
        }

        /** RFC 7252 5.10.8.2. If-None-Match */
        object IfNoneMatch : Option()

        /** RFC 7252 5.10.9. Size1 Option */
        data class Size1(val bytes: Long) : Option() {
            init {
                require(bytes in SIZE1_RANGE) {
                    "Size1 of $bytes is outside allowable range of $SIZE1_RANGE"
                }
            }
        }
    }

    sealed class Code {

        sealed class Method : Code() {
            object GET : Method()
            object POST : Method()
            object PUT : Method()
            object DELETE : Method()

            override fun toString(): String = javaClass.simpleName
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

            override fun toString(): String = javaClass.simpleName
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

        override fun equals(other: Any?): Boolean =
            this === other ||
                (other is Udp &&
                    type == other.type &&
                    code == other.code &&
                    id == other.id &&
                    token == other.token &&
                    options == other.options &&
                    payload.contentEquals(other.payload))

        override fun hashCode(): Int =
            Objects.hash(type, code, id, token, options, payload.contentHashCode())

        override fun toString(): String = "Message.Udp(" +
            "type=$type, " +
            "code=$code, " +
            "id=$id (0x${Integer.toHexString(id).toUpperCase()}), " +
            "token=$token, " +
            "options=$options, " +
            "payload=${payload.toHexString()}" +
            ")"
    }

    data class Tcp(
        override val code: Code,
        override val token: Long?,
        override val options: List<Option>,
        override val payload: ByteArray
    ) : Message() {

        override fun equals(other: Any?): Boolean =
            this === other ||
                (other is Tcp &&
                    code == other.code &&
                    token == other.token &&
                    options == other.options &&
                    payload.contentEquals(other.payload))

        override fun hashCode(): Int =
            Objects.hash(code, token, options, payload.contentHashCode())

        override fun toString(): String = "Message.Tcp(" +
            "code=$code, " +
            "token=$token, " +
            "options=$options, " +
            "payload=${payload.toHexString()}" +
            ")"
    }
}
