package com.juul.koap

import okio.BufferedSink

actual fun BufferedSink.writeOptions(options: List<Message.Option>) {
    val optionsCast = options as Array<Message.Option>
    var sorted = mutableListOf<Message.Option.Format>()
    var j = 0
    while (j < optionsCast.size) {
        sorted.add(optionsCast[j++].toFormat())
    }
    sorted = sorted.sortedBy { it.number }.toMutableList()

    for (i in sorted.indices) {
        val preceding = if (i == 0) null else sorted[i - 1]
        buffer.writeOption(sorted[i], preceding)
    }
}
