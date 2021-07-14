// ktlint-disable indent
// todo: Disable above rule only on effected line when https://github.com/pinterest/ktlint/issues/631 is fixed.

package com.juul.koap

import com.juul.koap.Message.Option.Observe.Registration.Deregister
import com.juul.koap.Message.Option.Observe.Registration.Register

/* RFC 7252 5.10. Table 4: Options
 * RFC 7641 2. The Observe Option (No. 6)
 *
 * +-----+----------------+--------+--------+
 * | No. | Name           | Format | Length |
 * +-----+----------------+--------+--------+
 * |   1 | If-Match       | opaque | 0-8    |
 * |   3 | Uri-Host       | string | 1-255  |
 * |   4 | ETag           | opaque | 1-8    |
 * |   5 | If-None-Match  | empty  | 0      |
 * |   6 | Observe        | uint   | 0-3    |
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
private val OBSERVE_RANGE = 0..16_777_215 // 3-byte unsigned int

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
     * |             1 .. 255           | 1     |
     * |           256 .. 65,635        | 2     |
     * |        65,536 .. 4,294,967,295 | 4     |
     * | 4,294,967,296 .. 2^63-1        | 8     |
     *
     * _A token of value `0` will occupy `0` bytes._
     */
    abstract val token: Long

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

                override fun hashCode(): Int {
                    var result = number
                    result = 31 * result + value.contentHashCode()
                    return result
                }
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

            /** RFC 7252 12.3. CoAP Content-Formats Registry */
            /* ktlint-disable no-multi-spaces */
            companion object {
                val PlainText = ContentFormat(0)    // text/plain; charset=utf-8
                val LinkFormat = ContentFormat(40)  // application/link-format
                val XML = ContentFormat(41)         // application/xml
                val OctetStream = ContentFormat(42) // application/octet-stream
                val EXI = ContentFormat(47)         // application/exi
                val JSON = ContentFormat(50)        // application/json

                /** RFC 7049 7.4. CoAP Content-Format */
                val CBOR = ContentFormat(60)        // application/cbor
            }
            /* ktlint-enable no-multi-spaces */
        }

        /** RFC 7252 5.10.4. Accept */
        data class Accept(val format: Long) : Option() {

            constructor(format: ContentFormat) : this(format.format)

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

        /** [RFC 7641 2. The Observe Option](https://tools.ietf.org/html/rfc7641#section-2) */
        data class Observe(val value: Long) : Option() {

            /**
             * Per [RFC 7641 2. The Observe Option](https://tools.ietf.org/html/rfc7641#section-2):
             *
             * > When included in a GET request, the Observe Option extends the GET method so it
             * > does not only retrieve a current representation of the target resource, but also
             * > requests the server to add or remove an entry in the list of observers of the
             * > resource depending on the option value. The list entry consists of the client
             * > endpoint and the token specified by the client in the request. Possible values are:
             *
             * - `0` (register) adds the entry to the list, if not present;
             * - `1` (deregister) removes the entry from the list, if present.
             */
            sealed class Registration {
                object Register : Registration()
                object Deregister : Registration()
            }

            /**
             * Constructs an [Observe] to be included in a GET request.
             *
             * @see Registration
             */
            constructor(action: Registration) : this(
                when (action) {
                    Register -> 0L
                    Deregister -> 1L
                }
            )

            init {
                require(value in OBSERVE_RANGE) {
                    "Observe value of $value is outside allowable range of $OBSERVE_RANGE"
                }
            }
        }
    }

    /**
     * Per "RFC 7252 3. Message Format", **Code** is an:
     *
     * > 8-bit unsigned integer, split into a 3-bit class (most significant bits) and a 5-bit detail
     * > (least significant bits), documented as "c.dd" where "c" is a digit from 0 to 7 for the
     * > 3-bit subfield and "dd" are two digits from 00 to 31 for the 5-bit subfield.
     */
    sealed class Code {

        abstract val `class`: Int
        abstract val detail: Int

        /** RFC 7252: 12.1.1. Method Codes */
        sealed class Method(
            override val `class`: Int,
            override val detail: Int
        ) : Code() {
            /* ktlint-disable no-multi-spaces */
            object GET : Method(`class` = 0, detail = 1)    // 0.01
            object POST : Method(`class` = 0, detail = 2)   // 0.02
            object PUT : Method(`class` = 0, detail = 3)    // 0.03
            object DELETE : Method(`class` = 0, detail = 4) // 0.04
            /* ktlint-enable no-multi-spaces */

            override fun toString(): String = this::class.simpleName!!
        }

        /** RFC 7252: 12.1.2. Response Codes */
        sealed class Response(
            override val `class`: Int,
            override val detail: Int
        ) : Code() {
            /* ktlint-disable no-multi-spaces */
            object Created : Response(`class` = 2, detail = 1)                   // 2.01
            object Deleted : Response(`class` = 2, detail = 2)                   // 2.02
            object Valid : Response(`class` = 2, detail = 3)                     // 2.03
            object Changed : Response(`class` = 2, detail = 4)                   // 2.04
            object Content : Response(`class` = 2, detail = 5)                   // 2.05
            object BadRequest : Response(`class` = 4, detail = 0)                // 4.00
            object Unauthorized : Response(`class` = 4, detail = 1)              // 4.01
            object BadOption : Response(`class` = 4, detail = 2)                 // 4.02
            object Forbidden : Response(`class` = 4, detail = 3)                 // 4.03
            object NotFound : Response(`class` = 4, detail = 4)                  // 4.04
            object MethodNotAllowed : Response(`class` = 4, detail = 5)          // 4.05
            object NotAcceptable : Response(`class` = 4, detail = 6)             // 4.06
            object PreconditionFailed : Response(`class` = 4, detail = 12)       // 4.12
            object RequestEntityTooLarge : Response(`class` = 4, detail = 13)    // 4.13
            object UnsupportedContentFormat : Response(`class` = 4, detail = 15) // 4.15
            object InternalServerError : Response(`class` = 5, detail = 0)       // 5.00
            object NotImplemented : Response(`class` = 5, detail = 1)            // 5.01
            object BadGateway : Response(`class` = 5, detail = 2)                // 5.02
            object ServiceUnavailable : Response(`class` = 5, detail = 3)        // 5.03
            object GatewayTimeout : Response(`class` = 5, detail = 4)            // 5.04
            object ProxyingNotSupported : Response(`class` = 5, detail = 5)      // 5.05
            /* ktlint-enable no-multi-spaces */

            override fun toString(): String = this::class.simpleName!!
        }

        data class Raw(
            /** Allowable range is `0..7`. */
            override val `class`: Int,

            /** Allowable range is `0..31`. */
            override val detail: Int
        ) : Code()
    }

    data class Udp(
        val type: Type,
        override val code: Code,

        /** Allowable range is `0..65,535`. */
        val id: Int,

        override val token: Long,
        override val options: List<Option>,
        override val payload: ByteArray
    ) : Message() {

        sealed class Type {
            object Confirmable : Type()
            object NonConfirmable : Type()
            object Acknowledgement : Type()
            object Reset : Type()
        }

        override fun equals(other: Any?): Boolean =
            this === other ||
                (other is Udp && // ktlint-disable indent
                    type == other.type &&
                    code == other.code &&
                    id == other.id &&
                    token == other.token &&
                    options == other.options &&
                    payload.contentEquals(other.payload))

        override fun hashCode(): Int {
            var result = type.hashCode()
            result = 31 * result + code.hashCode()
            result = 31 * result + id
            result = 31 * result + token.hashCode()
            result = 31 * result + options.hashCode()
            result = 31 * result + payload.contentHashCode()
            return result
        }

        override fun toString(): String = "Message.Udp(" +
            "type=$type, " +
            "code=$code, " +
            "id=${id.debugString(Short.SIZE_BYTES)}, " +
            "token=${token.debugTokenString()}, " +
            "options=$options, " +
            "payload=${payload.toHexString()}" +
            ")"
    }

    data class Tcp(
        override val code: Code,
        override val token: Long,
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

        override fun hashCode(): Int {
            var result = code.hashCode()
            result = 31 * result + token.hashCode()
            result = 31 * result + options.hashCode()
            result = 31 * result + payload.contentHashCode()
            return result
        }

        override fun toString(): String = "Message.Tcp(" +
            "code=$code, " +
            "token=${token.debugTokenString()}, " +
            "options=$options, " +
            "payload=${payload.toHexString()}" +
            ")"
    }
}

val Message.Code.Response.isSuccess: Boolean get() = `class` == 2
val Message.Code.Response.isError: Boolean get() = `class` == 4 || `class` == 5
