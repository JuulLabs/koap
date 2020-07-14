package multiplatform

actual object StringFormatterFactory {
    actual fun createMyStringFormatterFactory(): StringFormatter = JvmStringFormatter
}

object JvmStringFormatter: StringFormatter {
    override fun format(format: String, vararg args: Any?): String = String.format(format, args)
}