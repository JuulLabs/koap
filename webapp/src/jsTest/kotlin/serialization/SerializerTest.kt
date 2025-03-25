@file:UseSerializers(
    OptionSerializer::class,
)

package com.juul.koap.serialization

import com.juul.koap.Message.Option
import com.juul.koap.Message.Option.Accept
import com.juul.koap.Message.Option.ContentFormat
import com.juul.koap.Message.Option.ETag
import com.juul.koap.Message.Option.UriHost
import com.juul.koap.Message.Option.UriPort
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.json.Json
import kotlin.test.assertEquals
import kotlin.test.Test

class SerializerTest {
    @Test
    fun optionSerializesTo() {
        assertOptionSerializesTo(ContentFormat.PlainText, "Content-Format: text/plain; charset=utf-8")
        assertOptionSerializesTo(ContentFormat.JSON, "Content-Format: application/json")
        assertOptionSerializesTo(ContentFormat.CBOR, "Content-Format: application/cbor")
        assertOptionSerializesTo(ContentFormat(20), "Content-Format: ContentFormat(20)")
        assertOptionSerializesTo(Accept(ContentFormat.JSON), "Accept: application/json")
        assertOptionSerializesTo(UriHost("localhost"), "UriHost(uri=localhost)")
        assertOptionSerializesTo(UriPort(1234), "UriPort(port=1234)")
        assertOptionSerializesTo(ETag("123".encodeToByteArray()), "ETag(etag=31 32 33)")
    }
}

private fun assertOptionSerializesTo(opt: Option, expected: String) {
    assertEquals(
        actual = Json.encodeToString(OptionSerializer, opt).trim('"'),
        expected = expected,
    )
}

