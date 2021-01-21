@file:JsModule("cbor")
@file:JsNonModule

package cbor

import kotlin.js.Promise

internal external fun decodeFirstSync(input: String, options: dynamic): dynamic
internal external fun diagnose(input: String, options: dynamic): Promise<String>
