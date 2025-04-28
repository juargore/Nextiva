package com.nextiva.nextivaapp.android.models

import com.nextiva.nextivaapp.android.BasePowerMockTest
import org.junit.Assert.assertEquals
import org.junit.Test

class AvatarEventTest : BasePowerMockTest() {

    lateinit var avatarEvent: AvatarEvent
    lateinit var avatarBytes: ByteArray
    lateinit var jid: String

    override fun setup() {
        super.setup()

        avatarBytes = byteArrayOf(2)
        jid = "myJid"

        avatarEvent = AvatarEvent(avatarBytes, jid)
    }

    @Test
    fun getAvatarBytes_returnsCorrectValue() {
        assertEquals(avatarBytes, avatarEvent.avatarBytes)
    }

    @Test
    fun getJid_returnsCorrectValue() {
        assertEquals(jid, avatarEvent.jid)
    }
}