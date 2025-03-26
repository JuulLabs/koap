package com.juul.koap

import kotlin.reflect.KClass

internal val Message.Option.Block.intValue: Int
    get() {
        val moreBit = if (more) 1 else 0
        return (number shl 4) or (moreBit shl 3) or size.size
    }

internal inline fun <reified T : Message.Option.Block> blockOf(value: Long): T =
    blockOf(T::class, value)

private fun <T : Message.Option.Block> blockOf(type: KClass<T>, value: Long): T {
    val number = value shr 4
    require(number in BLOCK_NUMBER_RANGE) {
        "Block number $number (from option value of $value) is outside allowable range of $BLOCK_NUMBER_RANGE"
    }
    val more = (value and 0x8L) != 0L
    val size = (value and 0b111).toInt().let {
        Block.Size.entries[it]
    }

    @Suppress("UNCHECKED_CAST")
    return when (type) {
        Message.Option.Block1::class -> Message.Option.Block1(number.toInt(), more, size)
        Message.Option.Block2::class -> Message.Option.Block2(number.toInt(), more, size)
        Message.Option.QBlock1::class -> Message.Option.QBlock1(number.toInt(), more, size)
        Message.Option.QBlock2::class -> Message.Option.QBlock2(number.toInt(), more, size)
        else -> error("Unsupported Block type: ${type.simpleName}")
    } as T
}
