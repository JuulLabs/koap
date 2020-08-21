var messageTypeSelect = document.getElementById("msgtype");
var udpMessageTypeSelect = document.getElementById("udpmsgtype");
var codeTypeRadioNodeList = document.getElementsByName("radiocode");
var codeTypeResponseSelect = document.getElementById("codetyperesponseselect");
var codeTypeMethodSelect = document.getElementById("codetypemethodselect");
var codeRawClassInput = document.getElementById("coderawclass");
var codeRawDetailInput = document.getElementById("coderawdetail");
var messageIdInput = document.getElementById("msgid");
var tokenInput = document.getElementById("token");
var payloadInput = document.getElementById("payload");
var optionSelect = document.getElementById("optionsselect");
var optionInput = document.getElementById("optioninput");
var encodeButton = document.getElementById("encodebutton");
var decodeButton = document.getElementById("decodebutton");
var decodeTextArea = document.getElementById("decodedtext");
var rawDiv = document.getElementById("coderawdiv");
var methodDiv = document.getElementById("codemethoddiv");
var responseDiv = document.getElementById("coderesponsediv");

hideDiv(rawDiv);
hideDiv(responseDiv);

messageTypeSelect.addEventListener("change", function(evt) {
    if (this.value == "UDP") {
        setupUdpFields(udpMessageTypeSelect, messageIdInput);
    } else if (this.value == "TCP") {
        setupTcpFields(udpMessageTypeSelect, messageIdInput);
    }
});

function onEncodeClick() {
    let messageObj = new Object();

    messageObj.messageType = messageTypeSelect.value;
    messageObj.code = getCodeTypeForSelection(codeTypeRadioNodeList);
    messageObj.token = parseInt(tokenInput.value);

    if (messageTypeSelect.value == "UDP") {
        messageObj.id = parseInt(messageIdInput.value);
        messageObj.type = udpMessageTypeSelect.value;
    }

    messageObj.options = [{
        type: optionSelect.value,
        value: optionInput.value
    }];
    messageObj.payload = payloadInput.value;

    let stringify = JSON.stringify(messageObj);
    console.log(stringify);
    decodeTextArea.value = "";
    decodeTextArea.value = koap.com.juul.koap.encodeFromJson(stringify);
}

function onDecodeTcpClick() {
    let jsonDecode = koap.com.juul.koap.decodeToTcp(decodeTextArea.value);
    updateUiFromJson(jsonDecode);
    console.log(jsonDecode);
}

function onDecodeUdpClick() {
    let jsonDecode = koap.com.juul.koap.decodeToUdp(decodeTextArea.value);
    updateUiFromJson(jsonDecode);
    console.log(jsonDecode);
}

function updateUiFromJson(jsonDecode) {
    let jsonObj = JSON.parse(jsonDecode);
    messageTypeSelect.value = jsonObj.messageType;

    setSelectionForCodeType(jsonObj);

    tokenInput.value = parseInt(jsonObj.token);
    optionSelect.value = jsonObj.options[0].type;
    optionInput.value = jsonObj.options[0].value;
    payloadInput.value = jsonObj.payload;

    if (jsonObj.messageType == "UDP") {
        messageIdInput.value = parseInt(jsonObj.id);
        udpMessageTypeSelect.value = jsonObj.type;
        setupUdpFields(udpMessageTypeSelect, messageIdInput);
    } else if (jsonObj.messageType == "TCP") {
        setupTcpFields(udpMessageTypeSelect, messageIdInput);
    }
}

function setSelectionForCodeType(jsonObj) {
    let codeType = jsonObj.code["#type"];
    if (codeType == "Raw") {
        document.getElementById("Raw").click();
        codeRawClassInput.value = parseInt(jsonObj.code["class"]);
        codeRawDetailInput.value = parseInt(jsonObj.code["detail"]);
    } else {
        let selectOptions = codeTypeMethodSelect.options;
        for (let i = 0; i < selectOptions.length; i++) {
            if (codeType == selectOptions[i].value) {
                document.getElementById("Method").click();
                codeTypeMethodSelect.value = codeType;
                break;
            }
        }

        selectOptions = codeTypeResponseSelect.selectedOptions;
        for (let i = 0; i < selectOptions.length; i++) {
            if (codeType == selectOptions[i].value) {
                document.getElementById("Response").click();
                codeTypeResponseSelect.value = codeType;
                break;
            }
        }
    }
}

function getCodeTypeForSelection(radioNodeList) {
    for (let i = 0; i < radioNodeList.length; i++) {
        if (radioNodeList[i].value == "Response" && radioNodeList[i].checked) {
            return {
                "#type": codeTypeResponseSelect.value
            };
        } else if (radioNodeList[i].value == "Raw" && radioNodeList[i].checked) {
            return {
                "#type": "Raw",
                "class": parseInt(codeRawClassInput.value),
                "detail": parseInt(codeRawDetailInput.value)
            };
        } else if (radioNodeList[i].value == "Method" && radioNodeList[i].checked) {
            return {
                "#type": codeTypeMethodSelect.value
            };
        }
    }
}

function onRadioSelectionChanged(radInput) {
    var rawDiv = document.getElementById("coderawdiv")
    var methodDiv = document.getElementById("codemethoddiv")
    var responseDiv = document.getElementById("coderesponsediv")

    if (radInput.value == "Response") {
        showDiv(responseDiv);
        hideDiv(methodDiv);
        hideDiv(rawDiv);
    } else if (radInput.value == "Raw") {
        hideDiv(responseDiv);
        hideDiv(methodDiv);
        showDiv(rawDiv);
    } else if (radInput.value == "Method") {
        hideDiv(responseDiv);
        showDiv(methodDiv);
        hideDiv(rawDiv);
    }
}

function showDiv(divToShow) {
    divToShow.style.display = "block"
    divToShow.style.visibility = "visible"
}

function hideDiv(divToHide) {
    divToHide.style.display = "none"
}

function setupTcpFields(udpMsgTypeSelect, messageId) {
    udpMsgTypeSelect.disabled = true;
    messageId.disabled = true;
}

function setupUdpFields(udpMsgTypeSelect, messageId) {
    udpMsgTypeSelect.disabled = false;
    messageId.disabled = false;
}
