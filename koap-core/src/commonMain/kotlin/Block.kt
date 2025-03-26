package com.juul.koap

import com.juul.koap.Message.Option.Block
import kotlin.reflect.KClass

/**
 * Creates a [Block.Size] of specified [bytes] (if allowed).
 *
 * Allowed [bytes]s are: 16, 32, 64, 128, 256, 512, 1024
 *
 * For [BERT](https://datatracker.ietf.org/doc/html/rfc8323#section-6) block size, use [Block.Size.Bert].
 *
 * @throws IllegalArgumentException if specified [bytes] is not one of the allowed sizes.
 */
fun blockSizeOf(bytes: Int): Block.Size = Block.Size.entries.firstOrNull { it.bytes == bytes }
    ?: run {
        val sizes = Block.Size.entries
            .map(Block.Size::bytes)
            .toSet()
            .joinToString(", ")
        throw IllegalArgumentException("Block size of $bytes is invalid, allowed values: $sizes")
    }

internal val Block.intValue: Int
    get() {
        val moreBit = if (more) 1 else 0
        return (number shl 4) or (moreBit shl 3) or size.ordinal
    }

internal inline fun <reified T : Block> blockOf(value: Long): T =
    blockOf(T::class, value)

private fun <T : Block> blockOf(type: KClass<T>, value: Long): T {
    val number = value shr 4
    require(number in BLOCK_NUMBER_RANGE) {
        "Block number $number (from option value of $value) is outside allowable range of $BLOCK_NUMBER_RANGE"
    }
    val more = (value and 0x8L) != 0L
    val size = Block.Size.entries[value.toInt() and 0b111]

    @Suppress("UNCHECKED_CAST")
    return when (type) {
        Message.Option.Block1::class -> Message.Option.Block1(number.toInt(), more, size)
        Message.Option.Block2::class -> Message.Option.Block2(number.toInt(), more, size)
        Message.Option.QBlock1::class -> Message.Option.QBlock1(number.toInt(), more, size)
        Message.Option.QBlock2::class -> Message.Option.QBlock2(number.toInt(), more, size)
        else -> error("Unsupported Block type: ${type.simpleName}")
    } as T
}
