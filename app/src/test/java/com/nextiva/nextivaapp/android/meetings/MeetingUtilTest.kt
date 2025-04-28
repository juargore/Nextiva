/*
 * Copyright (c) 2022. Nextiva, inc. to Present.
 * All rights reserved
 */

package com.nextiva.nextivaapp.android.meetings

import com.nextiva.nextivaapp.android.BasePowerMockTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Created by Thaddeus Dannar on 10/12/22.
 */
internal class MeetingUtilTest: BasePowerMockTest() {

    val meetingStandardExpectedInput = "0123456789"
    val meetingNineChar = "123456789"
    val meetingEightChar = "12345678"
    val meetingSevenChar = "1234567"
    val meetingSixChar = "123456"
    val meetingFiveChar = "12345"
    val meetingFourChar = "1234"
    val meetingThreeChar = "123"
    val meetingTwoChar = "12"
    val meetingOneChar = "1"
    val meetingEmpty = ""
    val meetingSpace = " "
    val meetingOnlySpaces = "          "
    val meetingStandardPreformatted = "0123 45 6789"
    val meetingStandardAlphaInvalid = "A123456789"
    val meetingStandardSymbolInvalid1 = "+123456789"
    val meetingStandardSymbolInvalid2 = "0123-45-6789"


    @Before
    fun setUp() {
    }

    @Test
    fun formatMeetingId_correctInput_returnsStandardCorrectValue() {
        assertEquals(
            "0123 45 6789",
            MeetingUtil.formatMeetingId(meetingStandardExpectedInput)
        )
    }

    @Test
    fun formatMeetingId_correctInput_returnsCorrectValueOnOneChar() {
        assertEquals(
            "1",
            MeetingUtil.formatMeetingId(meetingOneChar)
        )
    }

    @Test
    fun formatMeetingId_correctInput_returnsCorrectValueOnTwoChar() {
        assertEquals(
            "12",
            MeetingUtil.formatMeetingId(meetingTwoChar)
        )
    }

    @Test
    fun formatMeetingId_correctInput_returnsCorrectValueOnThreeChar() {
        assertEquals(
            "123",
            MeetingUtil.formatMeetingId(meetingThreeChar)
        )
    }

    @Test
    fun formatMeetingId_correctInput_returnsCorrectValueOnFourChar() {
        assertEquals(
            "1234",
            MeetingUtil.formatMeetingId(meetingFourChar)
        )
    }

    @Test
    fun formatMeetingId_correctInput_returnsCorrectValueOnFiveChar() {
        assertEquals(
            "12345",
            MeetingUtil.formatMeetingId(meetingFiveChar)
        )
    }

    @Test
    fun formatMeetingId_correctInput_returnsCorrectValueOnSixChar() {
        assertEquals(
            "123456",
            MeetingUtil.formatMeetingId(meetingSixChar)
        )
    }

    @Test
    fun formatMeetingId_correctInput_returnsCorrectValueOnSevenChar() {
        assertEquals(
            "1234 56 7",
            MeetingUtil.formatMeetingId(meetingSevenChar)
        )
    }

    @Test
    fun formatMeetingId_correctInput_returnsCorrectValueOnEightChar() {
        assertEquals(
            "1234 56 78",
            MeetingUtil.formatMeetingId(meetingEightChar)
        )
    }

    @Test
    fun formatMeetingId_correctInput_returnsCorrectValueOnNineChar() {
        assertEquals(
            "1234 56 789",
            MeetingUtil.formatMeetingId(meetingNineChar)
        )
    }

    @Test
    fun formatMeetingId_emptyInput_returnsCorrectValueOnZeroChar() {
        assertEquals(
            "",
            MeetingUtil.formatMeetingId(meetingEmpty)
        )
    }

    @Test
    fun formatMeetingId_emptyInput_returnsCorrectValueOnSingleSpaceChar() {
        assertEquals(
            " ",
            MeetingUtil.formatMeetingId(meetingSpace)
        )
    }

    @Test
    fun formatMeetingId_emptyInput_returnsCorrectValueOnTenSpaceChar() {
        assertEquals(
            "          ",
            MeetingUtil.formatMeetingId(meetingOnlySpaces)
        )
    }

    @Test
    fun formatMeetingId_correctInput_returnsCorrectValueOnPreformattedChar() {
        assertEquals(
            "0123 45 6789",
            MeetingUtil.formatMeetingId(meetingStandardPreformatted)
        )
    }

    @Test
    fun formatMeetingId_invalidAlphaInput_returnsInputAlpha() {
        assertEquals(
            "A123456789",
            MeetingUtil.formatMeetingId(meetingStandardAlphaInvalid)
        )
    }

    @Test
    fun formatMeetingId_invalidSymbolInput_returnsInputSymbolOne() {
        assertEquals(
            "+123456789",
            MeetingUtil.formatMeetingId(meetingStandardSymbolInvalid1)
        )
    }

    @Test
    fun formatMeetingId_invalidSymbolInput_returnsInputSymbolTwo() {
        assertEquals(
            "0123-45-6789",
            MeetingUtil.formatMeetingId(meetingStandardSymbolInvalid2)
        )
    }
}