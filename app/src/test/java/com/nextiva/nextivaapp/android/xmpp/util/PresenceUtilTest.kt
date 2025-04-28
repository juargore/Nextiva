package com.nextiva.nextivaapp.android.xmpp.util

import com.nextiva.nextivaapp.android.BaseRobolectricTest
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.model.DbPresence
import org.jivesoftware.smack.packet.Presence
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PresenceUtilTest : BaseRobolectricTest() {

    @Test
    fun nextivaPresenceToSmackPresence_typeAvailableStateAvailable_returnsCorrectPresence() {
        val nextivaPresence = DbPresence("test.test@jid.im", Enums.Contacts.PresenceStates.AVAILABLE, 0, "status", Enums.Contacts.PresenceTypes.AVAILABLE)
        val smackPresence = PresenceUtil.dbPresenceToSmackPresence(nextivaPresence)

        assertEquals(Presence.Type.available, smackPresence.type)
        assertEquals(Presence.Mode.available, smackPresence.mode)
        assertEquals(0, smackPresence.priority)
        assertEquals("status", smackPresence.status)
    }

    @Test
    fun nextivaPresenceToSmackPresence_typeUnAvailableStateAvailable_returnsCorrectPresence() {
        val nextivaPresence = DbPresence("test.test@jid.im", Enums.Contacts.PresenceStates.AVAILABLE, 0, "status", Enums.Contacts.PresenceTypes.UNAVAILABLE)
        val smackPresence = PresenceUtil.dbPresenceToSmackPresence(nextivaPresence)

        assertEquals(Presence.Type.unavailable, smackPresence.type)
        assertEquals(Presence.Mode.available, smackPresence.mode)
        assertEquals(0, smackPresence.priority)
        assertEquals("status", smackPresence.status)
    }

    @Test
    fun nextivaPresenceToSmackPresence_typeAvailableStateAway_returnsCorrectPresence() {
        val nextivaPresence = DbPresence("test.test@jid.im", Enums.Contacts.PresenceStates.AWAY, 0, "status", Enums.Contacts.PresenceTypes.AVAILABLE)
        val smackPresence = PresenceUtil.dbPresenceToSmackPresence(nextivaPresence)

        assertEquals(Presence.Type.available, smackPresence.type)
        assertEquals(Presence.Mode.away, smackPresence.mode)
        assertEquals(0, smackPresence.priority)
        assertEquals("status", smackPresence.status)
    }

    @Test
    fun nextivaPresenceToSmackPresence_typeAvailableStateBusy_returnsCorrectPresence() {
        val nextivaPresence = DbPresence("test.test@jid.im", Enums.Contacts.PresenceStates.BUSY, 0, "status", Enums.Contacts.PresenceTypes.AVAILABLE)
        val smackPresence = PresenceUtil.dbPresenceToSmackPresence(nextivaPresence)

        assertEquals(Presence.Type.available, smackPresence.type)
        assertEquals(Presence.Mode.dnd, smackPresence.mode)
        assertEquals(0, smackPresence.priority)
        assertEquals("status", smackPresence.status)
    }

    @Test
    fun nextivaPresenceToSmackPresence_typeAvailableStateUnavailable_returnsCorrectPresence() {
        val nextivaPresence = DbPresence("test.test@jid.im", Enums.Contacts.PresenceStates.OFFLINE, 0, "status", Enums.Contacts.PresenceTypes.AVAILABLE)
        val smackPresence = PresenceUtil.dbPresenceToSmackPresence(nextivaPresence)

        assertEquals(Presence.Type.available, smackPresence.type)
        assertEquals(Presence.Mode.xa, smackPresence.mode)
        assertEquals(0, smackPresence.priority)
        assertEquals("status", smackPresence.status)
    }

    @Test
    fun getPendingPresence_returnsCorrectPresence() {
        val nextivaPresence = PresenceUtil.getPendingPresence("test@test.im")

        assertEquals("test@test.im", nextivaPresence.jid)
        assertEquals(Enums.Contacts.PresenceStates.PENDING, nextivaPresence.state)
        assertEquals(Enums.Contacts.PresenceTypes.UNAVAILABLE, nextivaPresence.type)
    }

    @Test
    fun toSmackPresence_unavailableType_setsUnavailablePresence() {
        val nextivaPresence = DbPresence(
                null, Enums.Contacts.PresenceStates.NONE, -128, null,
                Enums.Contacts.PresenceTypes.UNAVAILABLE)

        assertEquals(Presence.Type.unavailable, PresenceUtil.dbPresenceToSmackPresence(nextivaPresence).type)
    }

    @Test
    fun toSmackPresence_regularType_setsAvailablePresence() {
        val nextivaPresence = DbPresence(
                null, Enums.Contacts.PresenceStates.NONE, -128, null,
                Enums.Contacts.PresenceTypes.AVAILABLE)

        assertEquals(Presence.Type.available, PresenceUtil.dbPresenceToSmackPresence(nextivaPresence).type)
    }

    @Test
    fun toSmackPresence_awayPresence_setsAwayMode() {
        val nextivaPresence = DbPresence(
                null,
                Enums.Contacts.PresenceStates.AWAY, -128, null, Enums.Contacts.PresenceTypes.NONE)

        assertEquals(Presence.Mode.away, PresenceUtil.dbPresenceToSmackPresence(nextivaPresence).mode)
    }

    @Test
    fun toSmackPresence_busyPresence_setsDndMode() {
        val nextivaPresence = DbPresence(
                null,
                Enums.Contacts.PresenceStates.BUSY, -128, null, Enums.Contacts.PresenceTypes.NONE)

        assertEquals(Presence.Mode.dnd, PresenceUtil.dbPresenceToSmackPresence(nextivaPresence).mode)
    }

    @Test
    fun toSmackPresence_noPresence_defaultsToAvailableMode() {
        val nextivaPresence = DbPresence(
                null,
                Enums.Contacts.PresenceStates.NONE, -128, null, Enums.Contacts.PresenceTypes.NONE)

        assertEquals(Presence.Mode.available, PresenceUtil.dbPresenceToSmackPresence(nextivaPresence).mode)
    }

    @Test
    fun toSmackPresence_availablePresence_setsAvailableMode() {
        val nextivaPresence = DbPresence(
                null,
                Enums.Contacts.PresenceStates.AVAILABLE, -128, null, Enums.Contacts.PresenceTypes.NONE)

        assertEquals(Presence.Mode.available, PresenceUtil.dbPresenceToSmackPresence(nextivaPresence).mode)
    }

    @Test
    fun toSmackPresence_mobilePresence_setsAvailableMode() {
        val nextivaPresence = DbPresence(
                null,
                Enums.Contacts.PresenceStates.AVAILABLE, -128, null, Enums.Contacts.PresenceTypes.NONE)

        assertEquals(Presence.Mode.available, PresenceUtil.dbPresenceToSmackPresence(nextivaPresence).mode)
    }

    @Test
    fun toSmackPresence_hasStatus_setsStatus() {
        val nextivaPresence = DbPresence(
                null, Enums.Contacts.PresenceStates.NONE, -128,
                "My Status", Enums.Contacts.PresenceTypes.NONE)

        assertEquals("My Status", PresenceUtil.dbPresenceToSmackPresence(nextivaPresence).status)
    }

    @Test
    fun toSmackPresence_hasNoStatus_doesNotSetStatus() {
        val nextivaPresence = DbPresence(
                null, Enums.Contacts.PresenceStates.NONE, -128, null, Enums.Contacts.PresenceTypes.NONE)

        assertNull(PresenceUtil.dbPresenceToSmackPresence(nextivaPresence).status)
    }

    @Test
    fun toSmackPresence_hasPriority_setsPriority() {
        val nextivaPresence = DbPresence(
                null, Enums.Contacts.PresenceStates.NONE,
                100, null, Enums.Contacts.PresenceTypes.NONE)

        assertEquals(100, PresenceUtil.dbPresenceToSmackPresence(nextivaPresence).priority)
    }

    @Test
    fun getPendingPresence_returnsPendingPresence() {
        val jid = "myjid@domain.im"
        val expectedPresence = DbPresence(jid, Enums.Contacts.PresenceStates.PENDING, -128, null, Enums.Contacts.PresenceTypes.UNAVAILABLE)
        val actualPresence = PresenceUtil.getPendingPresence(jid)

        assertEquals(expectedPresence, actualPresence)
    }
}