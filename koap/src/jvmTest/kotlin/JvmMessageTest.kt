package com.juul.koap.test

import com.juul.koap.Message
import com.juul.koap.Message.Option.ETag
import com.juul.koap.Message.Option.Format.opaque
import com.juul.koap.Message.Option.IfMatch
import kotlin.test.Test
import nl.jqno.equalsverifier.EqualsVerifier

class JvmMessageTest {

    @Test
    fun `Verify equals of opaque`() {
        verifyEquals<opaque>()
    }

    @Test
    fun `Verify equals of ETag`() {
        verifyEquals<ETag>()
    }

    @Test
    fun `Verify equals of IfMatch`() {
        verifyEquals<IfMatch>()
    }

    @Test
    fun `Verify equals of Udp Message`() {
        verifyEquals<Message.Udp>()
    }

    @Test
    fun `Verify equals of Tcp Message`() {
        verifyEquals<Message.Tcp>()
    }
}

/**
 * > [EqualsVerifier] can be used in unit tests to verify whether the contract for the `equals` and
 * > `hashCode` methods in a class is met.
 */
private inline fun <reified T> verifyEquals() {
    EqualsVerifier.forClass(T::class.java).verify()
}
