package multiplatform

import com.juul.koap.adapter.CodeAdapter
import com.juul.koap.adapter.MessageAdapter
import com.juul.koap.adapter.OptionsAdapter
import com.juul.koap.adapter.toJson
import com.juul.koap.adapter.toMessage

@JsModule("object-hash")
@JsNonModule
external fun <T> hash(value: T): String

@ExperimentalStdlibApi
@JsName("testAdapter")
fun outputAdapter() {
    val message = MessageAdapter(
        "UDP",
        "Confirmable",
        CodeAdapter(CodeAdapter.CodeAdapterType.METHOD, "GET"),
        1234,
        5678,
        listOf(
            OptionsAdapter(OptionsAdapter.OptionsAdapterType.IfNoneMatch, "")
        ),
        payload = "testPayload"
    ).toMessage()?.toJson()

    console.log("got message $message")
}