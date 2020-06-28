package com.juul.koap

import com.juul.koap.Message.Code.Method.DELETE
import com.juul.koap.Message.Code.Method.GET
import com.juul.koap.Message.Code.Method.POST
import com.juul.koap.Message.Code.Method.PUT
import com.juul.koap.Message.Code.Response.BadGateway
import com.juul.koap.Message.Code.Response.BadOption
import com.juul.koap.Message.Code.Response.BadRequest
import com.juul.koap.Message.Code.Response.Changed
import com.juul.koap.Message.Code.Response.Content
import com.juul.koap.Message.Code.Response.Created
import com.juul.koap.Message.Code.Response.Deleted
import com.juul.koap.Message.Code.Response.Forbidden
import com.juul.koap.Message.Code.Response.GatewayTimeout
import com.juul.koap.Message.Code.Response.InternalServerError
import com.juul.koap.Message.Code.Response.MethodNotAllowed
import com.juul.koap.Message.Code.Response.NotAcceptable
import com.juul.koap.Message.Code.Response.NotFound
import com.juul.koap.Message.Code.Response.NotImplemented
import com.juul.koap.Message.Code.Response.PreconditionFailed
import com.juul.koap.Message.Code.Response.ProxyingNotSupported
import com.juul.koap.Message.Code.Response.RequestEntityTooLarge
import com.juul.koap.Message.Code.Response.ServiceUnavailable
import com.juul.koap.Message.Code.Response.Unauthorized
import com.juul.koap.Message.Code.Response.UnsupportedContentFormat
import com.juul.koap.Message.Code.Response.Valid
import com.juul.koap.Message.Option
import com.juul.koap.Message.Option.Accept
import com.juul.koap.Message.Option.ContentFormat
import com.juul.koap.Message.Option.ETag
import com.juul.koap.Message.Option.Format
import com.juul.koap.Message.Option.IfMatch
import com.juul.koap.Message.Option.IfNoneMatch
import com.juul.koap.Message.Option.LocationPath
import com.juul.koap.Message.Option.LocationQuery
import com.juul.koap.Message.Option.MaxAge
import com.juul.koap.Message.Option.Observe
import com.juul.koap.Message.Option.ProxyScheme
import com.juul.koap.Message.Option.ProxyUri
import com.juul.koap.Message.Option.Size1
import com.juul.koap.Message.Option.UriHost
import com.juul.koap.Message.Option.UriPath
import com.juul.koap.Message.Option.UriPort
import com.juul.koap.Message.Option.UriQuery
import com.juul.koap.Message.Udp.Type.Acknowledgement
import com.juul.koap.Message.Udp.Type.Confirmable
import com.juul.koap.Message.Udp.Type.NonConfirmable
import com.juul.koap.Message.Udp.Type.Reset
import okio.BufferedSource

/**
 * Decodes [ByteArray] receiver to a [Message].
 *
 * To use CoAP UDP (RFC 7252) decoding:
 *
 * ```
 * import com.juul.koap.Message.Udp
 *
 * val data = byteArrayOf(...)
 * val message = data.decode<Udp>()
 * ```
 *
 * To use CoAP TCP (RFC 8323) decoding:
 *
 * ```
 * import com.juul.koap.Message.Tcp
 *
 * val data = byteArrayOf(...)
 * val message = data.decode<Tcp>()
 * ```
 *
 * @see decodeUdp
 * @see decodeTcp
 */
inline fun <reified T : Message> ByteArray.decode(): T =
    when (T::class) {
        Message.Tcp::class -> decodeTcp()
        Message.Udp::class -> decodeUdp()
        else -> error("Unsupported class: ${T::class.java}")
    } as T

/**
 * Decodes [ByteArray] receiver to a [Message.Udp].
 *
 * [Figure 7: Message Format](https://tools.ietf.org/html/rfc7252#section-3) used for [Message.Udp]:
 *
 * ```
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |Ver| T |  TKL  |      Code     |          Message ID           |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |   Token (if any, TKL bytes) ...
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |   Options (if any) ...
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |1 1 1 1 1 1 1 1|    Payload (if any) ...
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * ```
 */
fun ByteArray.decodeUdp(): Message.Udp = decode(decodeUdpHeader())

/**
 * Decodes [ByteArray] receiver to a TCP [Message].
 *
 * [Figure 4: CoAP Frame for Reliable Transports](https://tools.ietf.org/html/rfc8323#section-3.2)
 * used for TCP [Message]:
 *
 * ```
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |  Len  |  TKL  | Extended Length (if any, as chosen by Len) ...
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |      Code     | Token (if any, TKL bytes) ...
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |  Options (if any) ...
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |1 1 1 1 1 1 1 1|    Payload (if any) ...
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * ```
 */
fun ByteArray.decodeTcp(): Message.Tcp = decode(decodeTcpHeader())

/**
 * Decodes [ByteArray] receiver to a [Message.Udp], using the specified [Header.Udp] for reference.
 *
 * Reading will begin after the [Header] (specified by [Header.size]).
 */
fun ByteArray.decode(header: Header.Udp) = decodeWithHeader(header) as Message.Udp

/**
 * Decodes [ByteArray] receiver to a [Message.Tcp], using the specified [Header.Tcp] for reference.
 *
 * Reading will begin after the [Header] (specified by [Header.size]).
 */
fun ByteArray.decode(header: Header.Tcp) = decodeWithHeader(header) as Message.Tcp

/**
 * Decodes [ByteArray] receiver to a [Message], using the specified [Header] for reference.
 *
 * Reading will begin after the [Header] (specified by [Header.size]).
 */
private fun ByteArray.decodeWithHeader(
    header: Header
): Message = withReader(offset = header.size) {
    // |7 6 5 4 3 2 1 0|7 6 5 4 3 2 1 0|7 6 5 4 3 2 1 0|7 6 5 4 3 2 1 0|
    // +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    // |  Options (if any) ...
    // +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    val options = readOptions()

    // |7 6 5 4 3 2 1 0|7 6 5 4 3 2 1 0|7 6 5 4 3 2 1 0|
    // +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    // |    Payload (if any) ...
    // +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    val payload = readByteArray()

    when (header) {
        is Header.Udp -> Message.Udp(
            header.type,
            header.code,
            header.messageId,
            header.token,
            options,
            payload
        )
        is Header.Tcp -> Message.Tcp(
            header.code,
            header.token,
            options,
            payload
        )
    }
}

/**
 * Decodes only the CoAP UDP (RFC 7252) header of the [ByteArray] receiver.
 *
 * ```
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |Ver| T |  TKL  |      Code     |          Message ID           |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |   Token (if any, TKL bytes) ...
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * ```
 */
fun ByteArray.decodeUdpHeader(): Header.Udp = withReader {
    // |7 6 5 4 3 2 1 0|
    // +-+-+-+-+-+-+-+-+
    // |Ver| T |  TKL  |
    // +-+-+-+-+-+-+-+-+
    val byte = readUByte()
    val ver = (byte shr 6) and 0b11
    check(ver == 1) { "Unsupported version: $ver" }
    val t = (byte shr 4) and 0b11
    val tkl = byte and 0b1111

    // |7 6 5 4 3 2 1 0|
    // +-+-+-+-+-+-+-+-+
    // |      Code     |
    // +-+-+-+-+-+-+-+-+
    val code = readUByte()

    // |7 6 5 4 3 2 1 0|7 6 5 4 3 2 1 0|
    // +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    // |          Message ID           |
    // +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    val id = readUShort()

    // |7 6 5 4 3 2 1 0|7 6 5 4 3 2 1 0|7 6 5 4 3 2 1 0|
    // +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    // | Token (if any, TKL bytes) ...
    // +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    val token = if (tkl != 0) readNumberOfLength(tkl) else null

    Header.Udp(
        size = index,
        version = ver,
        type = t.toType(),
        code = code.toCode(),
        messageId = id,
        token = token
    )
}

/**
 * Decodes only the CoAP TCP (RFC 8323) header of the [ByteArray] receiver.
 *
 * ```
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |  Len  |  TKL  | Extended Length (if any, as chosen by Len) ...
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |      Code     | Token (if any, TKL bytes) ...
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * ```
 */
fun ByteArray.decodeTcpHeader(): Header.Tcp = withReader {
    // |7 6 5 4 3 2 1 0|
    // +-+-+-+-+-+-+-+-+
    // |  Len  |  TKL  |
    // +-+-+-+-+-+-+-+-+
    val byte = readUByte()
    val len = (byte shr 4) and 0b1111
    val tkl = byte and 0b1111

    // |7 6 5 4 3 2 1 0|7 6 5 4 3 2 1 0|7 6 5 4 3 2 1 0|
    // +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    // | Extended Length (if any, as chosen by Len) ...
    // +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    val length = when (len) {
        in 0..12 -> len.toLong()            // No Extended Length
        13 -> (readUByte() + 13).toLong()   //  8-bit unsigned integer
        14 -> (readUShort() + 269).toLong() // 16-bit unsigned integer
        15 -> readUInt() + 65805            // 32-bit unsigned integer
        else -> error("Invalid length $len")
    }

    // |7 6 5 4 3 2 1 0|
    // +-+-+-+-+-+-+-+-+
    // |      Code     |
    // +-+-+-+-+-+-+-+-+
    val code = readUByte()

    // |7 6 5 4 3 2 1 0|7 6 5 4 3 2 1 0|7 6 5 4 3 2 1 0|
    // +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    // | Token (if any, TKL bytes) ...
    // +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    val token = if (tkl != 0) readNumberOfLength(bytes = tkl) else null

    Header.Tcp(
        size = index,
        length = length,
        code = code.toCode(),
        token = token
    )
}

private fun ByteArrayReader.readOptions(): List<Option> {
    val options = mutableListOf<Option>()
    var option: Option? = null
    do {
        option = readOption(option?.toFormat())
        if (option != null) options += option
    } while (option != null)
    return options
}

/**
 * Reads [Option] from [BufferedSource] receiver.
 *
 * 3.1. Option Format (Figure 8: Option Format)
 *
 * ```
 * +---------------+---------------+
 * |  Option Delta | Option Length |   1 byte
 * +---------------+---------------+
 * /         Option Delta          /   0-2 bytes
 * \          (extended)           \
 * +-------------------------------+
 * /         Option Length         /   0-2 bytes
 * \          (extended)           \
 * +-------------------------------+
 * \                               \
 * /         Option Value          /   0 or more bytes
 * \                               \
 * +-------------------------------+
 * ```
 *
 * @return [Option] or `null` if [PAYLOAD_MARKER] was hit or [BufferedSource] receiver is exhausted.
 */
internal fun ByteArrayReader.readOption(preceding: Format?): Option? {
    if (exhausted()) return null

    // +---------------+---------------+
    // |  Option Delta | Option Length |   1 byte
    // +---------------+---------------+
    val byte = readUByte()
    println("Read option at ${index - 1}: ${byteArrayOf(byte.toByte()).toHexString()}")
    if (byte == PAYLOAD_MARKER) return null
    val optionDelta = (byte shr 4) and 0b1111
    val optionLength = byte and 0b1111

    // +---------------+---------------+
    // /         Option Delta          /   0-2 bytes
    // \          (extended)           \
    // +-------------------------------+
    val delta = when (optionDelta) {
        in 0..12 -> optionDelta  // No Extended Delta
        13 -> readUByte() + 13   //  8-bit unsigned integer
        14 -> readUShort() + 269 // 16-bit unsigned integer
        else -> error("Invalid option delta $optionDelta")
    }

    // +-------------------------------+
    // /         Option Length         /   0-2 bytes
    // \          (extended)           \
    // +-------------------------------+
    val length = when (optionLength) {
        in 0..12 -> optionLength // No Extended Length
        13 -> readUByte() + 13   //  8-bit unsigned integer
        14 -> readUShort() + 269 // 16-bit unsigned integer
        else -> error("Invalid option length $optionLength")
    }

    return when (val number = (preceding?.number ?: 0) + delta) {
        1 -> IfMatch(readByteArray(length))
        3 -> UriHost(readUtf8(length))
        4 -> ETag(readByteArray(length))
        5 -> IfNoneMatch
        6 -> Observe(readNumberOfLength(length))
        7 -> UriPort(readNumberOfLength(length))
        8 -> LocationPath(readUtf8(length))
        11 -> UriPath(readUtf8(length))
        12 -> ContentFormat(readNumberOfLength(length))
        14 -> MaxAge(readNumberOfLength(length))
        15 -> UriQuery(readUtf8(length))
        17 -> Accept(readNumberOfLength(length))
        20 -> LocationQuery(readUtf8(length))
        35 -> ProxyUri(readUtf8(length))
        39 -> ProxyScheme(readUtf8(length))
        60 -> Size1(readNumberOfLength(length))
        else -> error("Unsupported option number $number")
    }.also { println("option: $it") }
}

// Type (T): 2-bit unsigned integer
// https://tools.ietf.org/html/rfc7252#section-3
private fun Int.toType(): Message.Udp.Type = when (this) {
    0 -> Confirmable
    1 -> NonConfirmable
    2 -> Acknowledgement
    3 -> Reset
    else -> error("Unknown message type: $this")
}

private fun Int.toCode(): Message.Code = when (this) {
    1 -> GET    // 0.01
    2 -> POST   // 0.02
    3 -> PUT    // 0.03
    4 -> DELETE // 0.04

    // RFC 7252: 12.1.2. Response Codes
    65 -> Created                   // 2.01
    66 -> Deleted                   // 2.02
    67 -> Valid                     // 2.03
    68 -> Changed                   // 2.04
    69 -> Content                   // 2.05
    128 -> BadRequest               // 4.00
    129 -> Unauthorized             // 4.01
    130 -> BadOption                // 4.02
    131 -> Forbidden                // 4.03
    132 -> NotFound                 // 4.04
    133 -> MethodNotAllowed         // 4.05
    134 -> NotAcceptable            // 4.06
    140 -> PreconditionFailed       // 4.12
    141 -> RequestEntityTooLarge    // 4.13
    143 -> UnsupportedContentFormat // 4.15
    160 -> InternalServerError      // 5.00
    161 -> NotImplemented           // 5.01
    162 -> BadGateway               // 5.02
    163 -> ServiceUnavailable       // 5.03
    164 -> GatewayTimeout           // 5.04
    165 -> ProxyingNotSupported     // 5.05

    else -> {
        val `class` = (this shr 5) and 0b111
        val detail = this and 0b11111
        Message.Code.Raw(`class`, detail)
    }
}

/**
 * Reads specified number of [bytes] from [ByteArrayReader] receiver to acquire a number.
 *
 * @param bytes (count) to read from [ByteArrayReader] to build number
 * @return value of number
 */
internal fun ByteArrayReader.readNumberOfLength(
    bytes: Int
): Long = when (bytes) {
    0 -> 0L
    1 -> readUByte().toLong()
    2 -> readUShort().toLong()
    3 -> readUInt24().toLong()
    4 -> readUInt()
    else -> throw IllegalArgumentException("Unsupported number length of $bytes bytes")
}
