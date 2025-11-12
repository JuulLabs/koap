package com.juul.koap

import com.juul.koap.Message.Option.Oscore

private val PARTIAL_IV_LENGTH_RANGE = 0..5
private const val PARTIAL_IV_LENGTH_MASK = 0x07
private const val KID_FLAG = 0x08
private const val KID_CONTEXT_FLAG = 0x10

data class OscoreParts(
    val partialIv: ByteArray,
    val kidContext: ByteArray?,
    val kid: ByteArray?,
) {
    override fun equals(other: Any?): Boolean =
        this === other ||
            (
                other is OscoreParts &&
                    partialIv.contentEquals(other.partialIv) &&
                    kidContext.contentEquals(other.kidContext) &&
                    kid.contentEquals(other.kid)
            )

    override fun hashCode(): Int {
        var result = partialIv.contentHashCode()
        result = 31 * result + kidContext.contentHashCode()
        result = 31 * result + kid.contentHashCode()
        return result
    }

    override fun toString(): String {
        val args = listOfNotNull(
            if (partialIv.isNotEmpty()) "partialIv=${partialIv.toHexString()}" else null,
            if (kidContext != null) "kidContext=${kidContext.toHexString()}" else null,
            if (kid != null) "kid=${kid.toHexString()}" else null,
        ).joinToString(separator = ", ")
        return "Oscore($args)"
    }
}

/**
 * Creates a binary OSCORE option value from an OscoreParts object.
 *
 * - [RFC 8613](https://tools.ietf.org/html/rfc8613#section-2) 2. The OSCORE Option
 * - [RFC 8613](https://tools.ietf.org/html/rfc8613#section-6.1) 6.1. Encoding of the OSCORE Option Value
 */
internal fun oscoreOptionValue(oscore: OscoreParts): ByteArray {
    val partialIvLength = oscore.partialIv.size
    require(partialIvLength in PARTIAL_IV_LENGTH_RANGE) {
        "Partial IV length of $partialIvLength is outside allowable range of $PARTIAL_IV_LENGTH_RANGE"
    }
    val kidContextLength = if (oscore.kidContext != null) oscore.kidContext.size else 0
    val kidFlagBit = if (oscore.kid != null) KID_FLAG else 0
    val kidContextFlagBit = if (oscore.kidContext != null) KID_CONTEXT_FLAG else 0
    val flagBits = partialIvLength or kidFlagBit or kidContextFlagBit
    if (flagBits == 0) {
        // If the OSCORE flag bits are all zero (0x00), the option value SHALL be empty
        return byteArrayOf()
    }
    var value = byteArrayOf(flagBits.toByte()) + oscore.partialIv
    if (oscore.kidContext != null) {
        value += byteArrayOf(kidContextLength.toByte()) + oscore.kidContext
    }
    if (oscore.kid != null) {
        value += oscore.kid
    }
    return value
}

/**
 * Parses an OscoreParts object from a binary OSCORE option value.
 */
internal fun oscorePartsFromOptionValue(value: ByteArray): OscoreParts {
    // TODO: detect if value does not conform to expected oscore format
    if (value.isEmpty()) {
        return OscoreParts(byteArrayOf(), null, null)
    }
    val flagBits = value[0].toInt()
    val partialIvLength = flagBits and PARTIAL_IV_LENGTH_MASK
    val kidFlag = (flagBits and KID_FLAG) != 0
    val kidContextFlag = (flagBits and KID_CONTEXT_FLAG) != 0
    val partialIv = value.sliceArray(1 until 1 + partialIvLength)
    val kidContextLength = if (kidContextFlag) value[1 + partialIvLength].toInt() else 0
    val kidContext = if (kidContextFlag) {
        value.sliceArray(2 + partialIvLength until 2 + partialIvLength + kidContextLength)
    } else {
        null
    }
    val kidOffset = (if (kidContextFlag) 2 else 1) + partialIvLength + kidContextLength
    val kid = if (kidFlag) value.sliceArray(kidOffset until value.size) else null
    return OscoreParts(partialIv, kidContext, kid)
}
