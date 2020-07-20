
import com.juul.koap.Message
import com.juul.koap.encode
import com.juul.koap.toHexString
import kotlinx.html.*
import kotlinx.html.dom.append
import kotlinx.html.dom.create
import kotlinx.html.js.table
import kotlinx.html.js.tr
import okio.internal.commonAsUtf8ToByteArray
import org.w3c.dom.Document
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLTableElement
import org.w3c.dom.events.Event
import org.w3c.dom.get
import kotlin.browser.document
import kotlin.browser.window

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

enum class MessageTypes {
    UDP,
    TCP
}

/**
 *
 * Ideally using reflection for Code subclasses (eg: Message.Code::class.nestedClasses) would be better, and more
 * extensible for future additions. That api is not available for Javascript at the moment
 */
@ExperimentalStdlibApi
val codesMap = buildMap<String, List<Message.Code>> {
    put(Message.Code.Response::class.simpleName!!, listOf<Message.Code>(
            Message.Code.Response.Created,
            Message.Code.Response.Deleted,
            Message.Code.Response.Valid,
            Message.Code.Response.Changed,
            Message.Code.Response.Content,
            Message.Code.Response.BadRequest,
            Message.Code.Response.Unauthorized,
            Message.Code.Response.BadOption,
            Message.Code.Response.Forbidden,
            Message.Code.Response.NotFound,
            Message.Code.Response.MethodNotAllowed,
            Message.Code.Response.NotAcceptable,
            Message.Code.Response.PreconditionFailed,
            Message.Code.Response.RequestEntityTooLarge,
            Message.Code.Response.UnsupportedContentFormat,
            Message.Code.Response.InternalServerError,
            Message.Code.Response.NotImplemented,
            Message.Code.Response.BadGateway,
            Message.Code.Response.ServiceUnavailable,
            Message.Code.Response.GatewayTimeout,
            Message.Code.Response.ProxyingNotSupported
    ))
    put(Message.Code.Raw::class.simpleName!!, emptyList())
    put(Message.Code.Method::class.simpleName!!, listOf(
            Message.Code.Method.GET,
            Message.Code.Method.DELETE,
            Message.Code.Method.POST,
            Message.Code.Method.PUT
    ))
}

@ExperimentalStdlibApi
fun main() {
    val inputTable = document.create.table { id = "inputtable" }
    // TODO:ahobbs change up this api to be a little easier to use
    createMessageSelector(inputTable, MessageTypes.values().asList())
    createAndAppendCodeRadioGroup(document, inputTable, codesMap)

//    val domElements = DOMElements(document)
//    domElements.messageType.addEventListener("change") {
//        when(domElements.messageType.selectedIndex) {
//            0 -> setupUdpFields(domElements)
//            1 -> setupTcpFields(domElements)
//        }
//    }
//
//    domElements.encodeButton.addEventListener("click") { event ->
//        handleEncode(domElements, event as Event)
//    }
}

private fun createMessageSelector(table: HTMLTableElement, messageTypes: List<MessageTypes>) {
    table.append.tr {
        th {
            +"Message"
        }
        td {
            select {
                name = "msgtype"
                id = "msgtype"
                messageTypes.forEach { msgType ->
                    option {
                        value = msgType.name.toLowerCase()
                        +msgType.name
                    }
                }
            }
        }
    }

}

private fun createAndAppendCodeRadioGroup(document: Document,
                                          table: HTMLTableElement,
                                          codesMap: Map<String, List<Message.Code>>) {
    document.body!!.append(table.apply {
        append.tr {
            th {
                colSpan = "2"
                +"Select a code"
            }
        }

        append.tr {
            td {
                colSpan = "2"
                codesMap.keys.forEach { key ->
                    input {
                        id = key.toLowerCase()
                        value = key.toLowerCase()
                        type = InputType.radio
                        name = "radiocode"
                    }
                    label {
                        +key
                    }
                }
            }

            append.tr {
            }
        }
    })

    // setup radio button change listeners
    val codeRadGroup = document.getElementsByName("radiocode")
    var i = 0
    while (i < codeRadGroup.length) {
        val input = codeRadGroup[i++] as HTMLInputElement
        input.addEventListener("change", {
            onRadioSelectionChanged(it, input)
        })
    }
}

private fun onRadioSelectionChanged(event: Event, radGroup: HTMLInputElement) {
    when (radGroup.value) {
        Message.Code.Response::class.simpleName!!.toLowerCase() -> {

        }
        Message.Code.Method::class.simpleName!!.toLowerCase() -> {

        }
        Message.Code.Raw::class.simpleName!!.toLowerCase() -> {

        }
    }
    window.alert(radGroup.value)
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

