package com.juul.koap

/**
 * Indicates the CoAP version number.
 * 2-bit unsigned integer
 * https://tools.ietf.org/html/rfc7252#section-3
 */
internal const val COAP_VERSION = 1

internal const val PAYLOAD_MARKER = 0xFF

internal const val UINT32_MAX_EXTENDED_LENGTH = UINT_MAX_VALUE + 65805L
