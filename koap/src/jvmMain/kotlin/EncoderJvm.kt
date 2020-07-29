package com.juul.koap

import okio.BufferedSink

actual fun BufferedSink.writeOptions(options: List<Message.Option>) {
    val sorted = options.map(Message.Option::toFormat).sortedBy(Message.Option.Format::number)
    for (i in sorted.indices) {
        val preceding = if (i == 0) null else sorted[i - 1]
        buffer.writeOption(sorted[i], preceding)
    }
}
