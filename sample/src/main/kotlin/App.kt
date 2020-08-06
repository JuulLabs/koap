import com.juul.koap.Message
import org.w3c.dom.Document
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLSelectElement
import org.w3c.dom.HTMLTextAreaElement
import org.w3c.dom.NodeList
import org.w3c.dom.get
import kotlin.browser.document

class DOMElements(private val document: Document) {
    val messageType: HTMLSelectElement
        get() = document.getElementById("msgtype") as HTMLSelectElement
    val udpMessageType: HTMLSelectElement
        get() = document.getElementById("udpmsgtype") as HTMLSelectElement
    val codeRadioGroup: NodeList
        get() = document.getElementsByName("radiocode")
    val codeRaw: HTMLInputElement
        get() = document.getElementById("codetyperaw") as HTMLInputElement
    val codeResponseDiv: HTMLDivElement
        get() = document.getElementById("coderesponsediv") as HTMLDivElement
    val codeMethodDiv: HTMLDivElement
        get() = document.getElementById("codemethoddiv") as HTMLDivElement
    val codeRawDiv: HTMLDivElement
        get() = document.getElementById("coderawdiv") as HTMLDivElement
    val idInput: HTMLInputElement
        get() = document.getElementById("msgid") as HTMLInputElement
    val token: HTMLInputElement
        get() = document.getElementById("token") as HTMLInputElement
    val options: dynamic = null      // not sure how to handle this just yet
    val payload: HTMLInputElement
        get() = document.getElementById("payload") as HTMLInputElement
    val encodeButton: HTMLButtonElement
        get() = document.getElementById("encodebutton") as HTMLButtonElement
    val decodeButton: HTMLButtonElement
        get() = document.getElementById("decodebutton") as HTMLButtonElement
    val decodeTextArea: HTMLTextAreaElement
        get() = document.getElementById("decodetextarea") as HTMLTextAreaElement
}

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

fun main() {
    val elements = DOMElements(document)

    setupCodeRadioGroup(
        elements.codeRadioGroup,
        mapOf(
            "response" to elements.codeResponseDiv,
            "method" to elements.codeMethodDiv,
            "raw" to elements.codeRawDiv
        )
    )

    with(elements.messageType) {
        addEventListener("change", {
            when (value.toLowerCase()) {
                "udp" -> setupUdpFields(elements)
                "tcp" -> setupTcpFields(elements)
            }
        })
    }

    elements.encodeButton.addEventListener("click", {
        handleEncode(elements)
    })
}

private fun handleEncode(domElements: DOMElements) {
    when (domElements.messageType.value.toLowerCase()) {
        "udp" -> {
            // UDP Type Message.UDP.Type
            // Code     Message.Code
            // ID       hex
            // Token    hex
            // options  emptyList(),
            // payload  string
        }
        "tcp" -> {
            // Code     Message.Code
            // Token    hex
            // options  emptyList(),
            // payload  string
        }
    }
}


private fun setupCodeRadioGroup(radioInput: NodeList,
                                values: Map<String, HTMLDivElement> ) {
    values.values.forEach { div ->
        div.style.display = "none"
    }
    var i = 0
    while (i < radioInput.length) {
        val input = radioInput[i++] as HTMLInputElement
        input.addEventListener("change", {
            onRadioSelectionChanged(input, values)
        })
    }
}

private fun onRadioSelectionChanged(
    radGroup: HTMLInputElement,
    values: Map<String, HTMLDivElement>
) {

    values.keys.forEach {key ->
        if (radGroup.value.toLowerCase() == key) {
            values[key]?.style?.display = "block"
            values[key]?.style?.visibility = "visible"
        } else {
            values[key]?.style?.display = "none"
        }
    }
    when (radGroup.value) {
        Message.Code.Raw::class.simpleName -> {
        }
        else -> {
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