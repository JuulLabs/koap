import com.juul.koap.Message
import kotlinx.html.InputType
import kotlinx.html.br
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.dom.append
import kotlinx.html.dom.create
import kotlinx.html.id
import kotlinx.html.input
import kotlinx.html.js.table
import kotlinx.html.js.tr
import kotlinx.html.label
import kotlinx.html.option
import kotlinx.html.select
import kotlinx.html.td
import kotlinx.html.textArea
import kotlinx.html.th
import org.w3c.dom.ChildNode
import org.w3c.dom.Document
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLSelectElement
import org.w3c.dom.HTMLTableElement
import org.w3c.dom.HTMLTextAreaElement
import org.w3c.dom.NodeList
import org.w3c.dom.asList
import org.w3c.dom.get
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
    - is commonAsUtf8ToByteArray() okay to use?
 */

class DOMElements(private val document: Document) {
    val messageType: HTMLSelectElement
        get() = document.getElementById("msgtype") as HTMLSelectElement             // select
    val udpMessageType: HTMLSelectElement
        get() = document.getElementById("udpmsgtype") as HTMLSelectElement             // select
    val codeRadioGroup: NodeList
        get() = document.getElementsByName("radiocode")
    val codeRaw: HTMLInputElement
        get() = document.getElementById("codetyperaw") as HTMLInputElement
    val codeSelect: HTMLSelectElement
        get() = document.getElementById("codetypeselect") as HTMLSelectElement
    val idInput: HTMLInputElement
        get() = document.getElementById("msgid") as HTMLInputElement                    // input
    val token: HTMLInputElement
        get() = document.getElementById("token") as HTMLInputElement                // input
    val options: dynamic = null      // not sure how to handle this just yet
    val payload: HTMLInputElement
        get() = document.getElementById("payload") as HTMLInputElement                  // input
    val encodeButton: HTMLButtonElement
        get() = document.getElementById("encodebutton") as HTMLButtonElement      // button
    val decodeButton: HTMLButtonElement
        get() = document.getElementById("decodebutton") as HTMLButtonElement      // button
    val decodeTextArea: HTMLTextAreaElement
        get() = document.getElementById("decodetextarea") as HTMLTextAreaElement  // textarea
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
    put(
        Message.Code.Response::class.simpleName!!, listOf<Message.Code>(
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
        )
    )
    put(Message.Code.Raw::class.simpleName!!, emptyList())
    put(
        Message.Code.Method::class.simpleName!!, listOf(
            Message.Code.Method.GET,
            Message.Code.Method.DELETE,
            Message.Code.Method.POST,
            Message.Code.Method.PUT
        )
    )
}

@ExperimentalStdlibApi
fun main() {
    val leftCol: HTMLDivElement = document.getElementById("leftcol") as HTMLDivElement
    val rightCol: HTMLDivElement = document.getElementById("rightcol") as HTMLDivElement

    // Left column setup
    val inputTable = document.create.table { id = "inputtable" }
    leftCol.append(inputTable)
    val domElements = DOMElements(document)
    inputTable.appendSelector(
        "msgtype",
        "Message",
        MessageTypes.values().map { it.name.capitalize() })
        .appendSelector(
            "udpmsgtype",
            "UDP Type",
            Message.Udp.Type.values().map { it.name.capitalize() })
        .appendCodeRadioGroup(document, codesMap)
        .appendInput("msgid", "ID")
        .appendInput("token")
        .appendInput("options")
        .appendInput("payload")
        .appendButton("encodebutton", "Encode -->")

    with(domElements.messageType) {
        addEventListener("change", {
            when (value.toLowerCase()) {
                MessageTypes.UDP.name.toLowerCase() -> setupUdpFields(domElements)
                MessageTypes.TCP.name.toLowerCase() -> setupTcpFields(domElements)
            }
        })
    }

    // right column setup
    rightCol.append {
        textArea {
            id = "decodedtext"
        }
        br {}
        button {
            id = "decodebutton"
            name = "decodebutton"
            +"<-- Decode"
        }
    }

    domElements.encodeButton.addEventListener("click", {
        handleEncode(domElements)
    })
}

private fun handleEncode(domElements: DOMElements) {
    when (domElements.messageType.value) {
        MessageTypes.UDP.name -> {

            // UDP Type Message.UDP.Type
            // Code     Message.Code
            // ID       hex
            // Token    hex
            // options  emptyList(),
            // payload  string
        }
        MessageTypes.TCP.name -> {
            // Code     Message.Code
            // Token    hex
            // options  emptyList(),
            // payload  string
        }
    }
}

private fun HTMLTableElement.appendButton(
    idText: String,
    displayText: String = idText.capitalize()
): HTMLTableElement {
    append {
        tr {
            td {
                colSpan = "2"
                button {
                    id = idText
                    name = idText
                    +displayText
                }
            }
        }
    }
    return this
}

private fun HTMLTableElement.appendInput(
    idText: String,
    displayText: String = idText.capitalize()
): HTMLTableElement {
    append {
        tr {
            th {
                +displayText
            }
            td {
                input {
                    id = idText
                    type = InputType.text
                }
            }
        }
    }
    return this
}

private fun HTMLTableElement.appendSelector(
    idText: String,
    displayText: String = idText.capitalize(),
    selectorValues: List<String>
): HTMLTableElement {
    append.tr {
        th {
            +displayText
        }
        td {
            select {
                id = idText
                name = idText
                selectorValues.forEachIndexed { index, displayValue ->
                    option {
                        selected = index == 0
                        value = displayValue
                        +displayValue
                    }
                }
            }
        }
    }
    return this
}

private fun HTMLTableElement.appendCodeRadioGroup(
    document: Document,
    codesMap: Map<String, List<Message.Code>>
): HTMLTableElement {
    append {
        tr {
            th {
                colSpan = "2"
                +"Select a code"
            }
        }
        tr {
            td {
                colSpan = "2"
                codesMap.keys.forEach { key ->
                    input {
                        id = key
                        value = key
                        type = InputType.radio
                        name = "radiocode"
                    }
                    label {
                        +key
                    }
                }
            }
        }
        tr {
            td {
                colSpan = "2"
                div {
                    id = "codetypediv"
                }
            }
        }
    }

    // setup radio button change listeners
    val codeRadGroup = document.getElementsByName("radiocode")
    val codeTypeDiv = document.getElementById("codetypediv") as HTMLDivElement
    var i = 0
    while (i < codeRadGroup.length) {
        val input = codeRadGroup[i++] as HTMLInputElement
        input.addEventListener("change", {
            onRadioSelectionChanged(input, codeTypeDiv, codesMap)
        })
    }

    return this
}

private fun onRadioSelectionChanged(
    radGroup: HTMLInputElement,
    codeTypeDiv: HTMLDivElement,
    codesMap: Map<String, List<Message.Code>>
) {
    codeTypeDiv.childNodes.asList().mapNotNull { it as? ChildNode }.forEach {
        it.remove()
    }

    when (radGroup.value) {
        Message.Code.Raw::class.simpleName -> {
            codeTypeDiv.append {
                input {
                    id = "codetyperaw"
                    type = InputType.text
                }
            }
        }
        else -> {
            codeTypeDiv.append {
                select {
                    id = "codetypeselect"
                    codesMap[radGroup.value]?.forEach { code ->
                        option {
                            value = code::class.simpleName!!
                            +code::class.simpleName!!
                        }
                    }
                }
            }
        }
    }
}

private fun setupTcpFields(elements: DOMElements) {
    console.log("setupTcpFieldds")
    elements.udpMessageType.disabled = true
    elements.idInput.disabled = true
}

private fun setupUdpFields(elements: DOMElements) {
    console.log("setupUdpFields")
    elements.udpMessageType.disabled = false
    elements.idInput.disabled = false
}
