
import com.juul.koap.Message
import com.juul.koap.encode
import com.juul.koap.toHexString
import okio.internal.commonAsUtf8ToByteArray
import org.w3c.dom.Document
import org.w3c.dom.events.Event
import kotlin.browser.document

/*
Message.Tcp(
    code = GET,
    token = 1,
    options = emptyList(),
    payload = "Hello TCP!".toByteArray()
)

Message.Udp(
    type = Confirmable,
    code = GET,
    id = 0xFEED,
    token = 0xCAFE,
    options = listOf(
        Message.Option.UriPath("example")
    ),
    payload = "First message".commonAsUtf8ToByteArray()
)

Stuff to address
    - Formatting of encoded message to hex
    - error handling
    - is DOMElements object really that useful?
    - Build controls dynamically from values from messages (allowing to to be more extensible)
    - is commonAsUtf8ToByteArray() okay to use?
 */

class DOMElements(document: Document) {
    val messageType: dynamic = document.getElementById("msgtype")            // select
    val type: dynamic = document.getElementById("type")                      // select
    val idInput: dynamic = document.getElementById("idinput")                // input
    val token: dynamic = document.getElementById("token")                    // input
    val options: dynamic = null      // not sure how to handle this just yet
    val payload: dynamic = document.getElementById("payload")                // input
    val encodeButton: dynamic = document.getElementById("encodebutton")      // button
    val decodeButton: dynamic = document.getElementById("decodebutton")      // button
    val decodeTextarea: dynamic = document.getElementById("decodetextarea")  // textarea
}

fun main() {

    val domElements = DOMElements(document)

    domElements.messageType.addEventListener("change") {
        when(domElements.messageType.selectedIndex) {
            0 -> setupUdpFields(domElements)
            1 -> setupTcpFields(domElements)
        }
    }

    domElements.encodeButton.addEventListener("click") { event ->
        handleEncode(domElements, event as Event)
    }
}

private fun handleEncode(domElements: DOMElements,
                         event: Event) {
    console.log("handleEncode: ${domElements.messageType.selectedIndex}")

    // TODO use real values for messages
    val message = when (domElements.messageType.selectedIndex) {
        0 -> buildMessageUdp(Message.Udp.Type.Confirmable, Message.Code.Method.GET, 0xFEED, 0xCAFE, listOf(Message.Option.UriPath("example")), "First message".commonAsUtf8ToByteArray())
        1 -> buildMessageTcp(Message.Code.Method.GET, 1, emptyList(), "Hello TCP!".commonAsUtf8ToByteArray())
        else -> null
    }

    console.log("with message $message")
    domElements.decodeTextarea.value = message?.encode()?.toHexString() ?: "Did not build a message"
}

private fun setupTcpFields(elements: DOMElements) {
    elements.type.disabled = true
    elements.idInput.disabled = true
}

private fun setupUdpFields(elements: DOMElements) {
    elements.type.disabled = false
    elements.idInput.disabled = false
}

private fun buildMessageUdp(type: Message.Udp.Type,
                            code: Message.Code.Method,
                            id: Int,
                            token: Long,
                            options: List<Message.Option> = emptyList(),
                            payload: ByteArray): Message {
    return Message.Udp(
        type = type,
        code = code,
        id = id,
        token = token,
        options = options,
        payload = payload
    )
}

private fun buildMessageTcp(code: Message.Code.Method,
                            token: Long,
                            options: List<Message.Option> = emptyList(),
                            payload: ByteArray): Message {
    return Message.Tcp(
        code = code,
        token = token,
        options = options,
        payload = payload
    )
}

