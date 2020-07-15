
import com.juul.koap.Message
import com.juul.koap.Message.Code.Method.GET
import com.juul.koap.Message.Udp.Type.Confirmable
import com.juul.koap.decode
import com.juul.koap.encode
import okio.internal.commonAsUtf8ToByteArray
import kotlin.browser.document

fun main() {
//    console.log("testhash" + hash("testhash"))
    val message = Message.Udp(
        type = Confirmable,
        code = GET,
        id = 0xFEED,
        token = 0xCAFE,
        options = listOf(
                Message.Option.UriPath("example")
        ),
        payload = "First message".commonAsUtf8ToByteArray()
    ).encode()
    val decoded = message.decode<Message.Udp>()
    document.getElementById("body_text")?.innerHTML = decoded.payload.fold("") {
        str, byte -> str + byte.toChar()
    }
}