/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.util;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import android.text.TextUtils;

import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.models.CallLogEntry;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;


@RunWith(PowerMockRunner.class)
@PrepareForTest(TextUtils.class)
public class CallUtilTest  {

    private String
            mPhoneNumberOneActual = "+12223334444",
            mPhoneNumberTwoActual = "+1222-333-4444",
            mPhoneNumberThreeActual = "+1(222)-333-4444",
            mPhoneNumberFourActual = "(222)333-4444",
            mPhoneNumberFiveActual = "1111",
            mPhoneNumberSixActual = "2223334444;00,00",
            mPhoneNumberSevenActual = "+1 222 333 4444,00",
            mPhoneNumberEightActual = "+1 222-333-4444,00#",
            mPhoneNumberNineActual = "1111,00#",
            mPhoneNumberTenActual = "1111;00#",
            mPhoneNumberElevenActual = "*007;00#",
            mPhoneNumberTwelveActual = "*007,00#",
            mPhoneNumberThirteenActual = "1111;222;333;444",
            mPhoneNumberFourteenActual = "+()123456789*0#.,-$pw",
            mPhoneNumberFifteenActual = "+0123456789",
            mPhoneNumberSixteenActual = "0123456789",
            mPhoneNumberSeventeenActual = "10123456789",
            mPhoneNumberEighteenActual = "+10123456789",
            mPhoneNumberNineteenActual = "A+10123456789",
            mPhoneNumberTwentyActual = "+B10123456789",
            mPhoneNumberTwentyOneActual = "+1C0123456789",
            mPhoneNumberTwentyTwoActual = "+101234D56789",
            mPhoneNumberTwentyThreeActual = "+10123456789E",
            mPhoneNumberTwentyFourActual = "+1ABCDEFGHIJ",
            mPhoneNumberTwentyFiveActual = "ABCDEFGHIJ",
            mPhoneNumberTwentySixActual = "+ABCDEFGHIJ",
            mPhoneNumberTwentySevenActual = "1ABCDEFGHIJ",
            mPhoneNumberTwentyEightActual = "+1234567890;ABCD",
            mPhoneNumberTwentyNineActual = "123456789;ABCD",
            mSmsShortCodeOne = "12345",
            mSmsShortCodeTwo = "123456",
            mSmsShortCodeThree = "23451",
            mSmsShortCodeFour = "234561",
            mSmsShortCodeFive = "1234",
            mSmsShortCodeSix = "123",
            mSmsShortCodeSeven = "12",
            mSmsShortCodeEight = "1",
            mNoNumber = "",
            mSmsShortCodeNine = "*12345",
            mSmsShortCodeTen = "#12345",
            mSmsShortCodeEleven = "+01234",
            mSmsShortCodeTwelve = "01234",
            mSmsShortCodeThirteen = "ABCDE",
            mSmsShortCodeFourteen = "ABCDEF";

    private static MockedStatic<TextUtils> textUtils;

    @BeforeClass
    public static void global() {
        textUtils = Mockito.mockStatic(TextUtils.class);
        textUtils.when(() -> TextUtils.isEmpty(any(CharSequence.class))).thenAnswer((Answer<Boolean>) invocation -> {
            CharSequence a = (CharSequence) invocation.getArguments()[0];
            return !(a != null && a.length() > 0);
        });

        textUtils.when(() -> TextUtils.isEmpty(null)).thenAnswer((Answer<Boolean>) invocation -> true);
        textUtils.when(() -> TextUtils.equals(any(CharSequence.class), any(CharSequence.class))).thenAnswer(invocation -> {
            CharSequence a = (CharSequence) invocation.getArguments()[0];
            CharSequence b = (CharSequence) invocation.getArguments()[1];
            if (a == b) {
                return true;
            }
            int length;
            if (a != null && b != null && (length = a.length()) == b.length()) {
                if (a instanceof String && b instanceof String) {
                    return a.equals(b);
                } else {
                    for (int i = 0; i < length; i++) {
                        if (a.charAt(i) != b.charAt(i)) {
                            return false;
                        }
                    }
                    return true;
                }
            }
            return false;
        });
    }

    @AfterClass
    public static void tearDown() {
        textUtils.close();
    }

    @Test
    public void getStrippedPhoneNumber_correctInput_returnsCorrectValue() {
        assertEquals("1234567890", CallUtil.getStrippedPhoneNumber(mPhoneNumberFourteenActual));
    }

    @Test
    public void getSearchFormattedPhoneNumber_correctInput_returnsCorrectValue1() {
        assertEquals("2223334444", CallUtil.getSearchFormattedPhoneNumber(mPhoneNumberOneActual));
    }

    @Test
    public void getSearchFormattedPhoneNumber_correctInput_returnsCorrectValue2() {
        assertEquals("2223334444", CallUtil.getSearchFormattedPhoneNumber(mPhoneNumberTwoActual));
    }

    @Test
    public void getSearchFormattedPhoneNumber_correctInput_returnsCorrectValue3() {
        assertEquals("2223334444", CallUtil.getSearchFormattedPhoneNumber(mPhoneNumberThreeActual));
    }

    @Test
    public void getSearchFormattedPhoneNumber_correctInput_returnsCorrectValue4() {
        assertEquals("2223334444", CallUtil.getSearchFormattedPhoneNumber(mPhoneNumberFourActual));
    }

    @Test
    public void getSearchFormattedPhoneNumber_correctInput_returnsCorrectValue5() {
        assertEquals("1111", CallUtil.getSearchFormattedPhoneNumber(mPhoneNumberFiveActual));
    }

    @Test
    public void getSearchFormattedPhoneNumber_correctInput_returnsCorrectValue6() {
        assertEquals("22233344440000", CallUtil.getSearchFormattedPhoneNumber(mPhoneNumberSixActual));
    }

    @Test
    public void getSearchFormattedPhoneNumber_correctInput_returnsCorrectValue7() {
        assertEquals("222333444400", CallUtil.getSearchFormattedPhoneNumber(mPhoneNumberSevenActual));
    }

    @Test
    public void getSearchFormattedPhoneNumber_correctInput_returnsCorrectValue8() {
        assertEquals("222333444400", CallUtil.getSearchFormattedPhoneNumber(mPhoneNumberEightActual));
    }

    @Test
    public void getSearchFormattedPhoneNumber_correctInput_returnsCorrectValue9() {
        assertEquals("111100", CallUtil.getSearchFormattedPhoneNumber(mPhoneNumberNineActual));
    }

    @Test
    public void getSearchFormattedPhoneNumber_correctInput_returnsCorrectValue10() {
        assertEquals("111100", CallUtil.getSearchFormattedPhoneNumber(mPhoneNumberTenActual));
    }

    @Test
    public void getSearchFormattedPhoneNumber_correctInput_returnsCorrectValue11() {
        assertEquals("00700", CallUtil.getSearchFormattedPhoneNumber(mPhoneNumberElevenActual));
    }

    @Test
    public void getSearchFormattedPhoneNumber_correctInput_returnsCorrectValue12() {
        assertEquals("00700", CallUtil.getSearchFormattedPhoneNumber(mPhoneNumberTwelveActual));
    }

    @Test
    public void getSearchFormattedPhoneNumber_correctInput_returnsCorrectValue13() {
        assertEquals("1111222333444", CallUtil.getSearchFormattedPhoneNumber(mPhoneNumberThirteenActual));
    }

    @Test
    public void getSearchFormattedPhoneNumber_noInput_returnsNull() {
        assertEquals("", CallUtil.getSearchFormattedPhoneNumber(""));
    }

    @Test
    public void cleanPhoneNumber_correctInput_returnsCorrectValue1() {
        assertEquals("12223334444", CallUtil.cleanPhoneNumber(mPhoneNumberOneActual));
    }

    @Test
    public void cleanPhoneNumber_correctInput_returnsCorrectValue2() {
        assertEquals("12223334444", CallUtil.cleanPhoneNumber(mPhoneNumberTwoActual));
    }

    @Test
    public void cleanPhoneNumber_correctInput_returnsCorrectValue3() {
        assertEquals("12223334444", CallUtil.cleanPhoneNumber(mPhoneNumberThreeActual));
    }

    @Test
    public void cleanPhoneNumber_correctInput_returnsCorrectValue4() {
        assertEquals("2223334444", CallUtil.cleanPhoneNumber(mPhoneNumberFourActual));
    }

    @Test
    public void cleanPhoneNumber_correctInput_returnsCorrectValue5() {
        assertEquals("1111", CallUtil.cleanPhoneNumber(mPhoneNumberFiveActual));
    }

    @Test
    public void cleanPhoneNumber_correctInput_returnsCorrectValue6() {
        assertEquals("22233344440000", CallUtil.cleanPhoneNumber(mPhoneNumberSixActual));
    }

    @Test
    public void cleanPhoneNumber_correctInput_returnsCorrectValue7() {
        assertEquals("1222333444400", CallUtil.cleanPhoneNumber(mPhoneNumberSevenActual));
    }

    @Test
    public void cleanPhoneNumber_correctInput_returnsCorrectValue8() {
        assertEquals("1222333444400#", CallUtil.cleanPhoneNumber(mPhoneNumberEightActual));
    }

    @Test
    public void cleanPhoneNumber_correctInput_returnsCorrectValue9() {
        assertEquals("111100#", CallUtil.cleanPhoneNumber(mPhoneNumberNineActual));
    }

    @Test
    public void cleanPhoneNumber_correctInput_returnsCorrectValue10() {
        assertEquals("111100#", CallUtil.cleanPhoneNumber(mPhoneNumberTenActual));
    }

    @Test
    public void cleanPhoneNumber_correctInput_returnsCorrectValue11() {
        assertEquals("*00700#", CallUtil.cleanPhoneNumber(mPhoneNumberElevenActual));
    }

    @Test
    public void cleanPhoneNumber_correctInput_returnsCorrectValue12() {
        assertEquals("*00700#", CallUtil.cleanPhoneNumber(mPhoneNumberTwelveActual));
    }

    @Test
    public void cleanPhoneNumber_correctInput_returnsCorrectValue13() {
        assertEquals("1111222333444", CallUtil.getSearchFormattedPhoneNumber(mPhoneNumberThirteenActual));
    }

    @Test
    public void cleanPhoneNumber_correctInput_returnsCorrectValue14() {
        assertEquals("234567890", CallUtil.getSearchFormattedPhoneNumber(mPhoneNumberFourteenActual));
    }

    @Test
    public void cleanPhoneNumber_correctInput_returnsCorrectValue15() {
        assertEquals("+0123456789", CallUtil.getSearchFormattedPhoneNumber(mPhoneNumberFifteenActual));
    }

    @Test
    public void cleanPhoneNumber_correctInput_returnsCorrectValue16() {
        assertEquals("0123456789", CallUtil.getSearchFormattedPhoneNumber(mPhoneNumberSixteenActual));
    }

    @Test
    public void cleanPhoneNumber_correctInput_returnsCorrectValue17() {
        assertEquals("10123456789", CallUtil.getSearchFormattedPhoneNumber(mPhoneNumberSeventeenActual));
    }

    @Test
    public void cleanPhoneNumber_correctInput_returnsCorrectValue18() {
        assertEquals("0123456789", CallUtil.getSearchFormattedPhoneNumber(mPhoneNumberEighteenActual));
    }

    @Test
    public void cleanPhoneNumber_correctInput_returnsCorrectValue19() {
        assertEquals("0123456789", CallUtil.getSearchFormattedPhoneNumber(mPhoneNumberNineteenActual));
    }

    @Test
    public void cleanPhoneNumber_correctInput_returnsCorrectValue20() {
        assertEquals("0123456789", CallUtil.getSearchFormattedPhoneNumber(mPhoneNumberTwentyActual));
    }

    @Test
    public void cleanPhoneNumber_correctInput_returnsCorrectValue21() {
        assertEquals("0123456789", CallUtil.getSearchFormattedPhoneNumber(mPhoneNumberTwentyOneActual));
    }

    @Test
    public void cleanPhoneNumber_correctInput_returnsCorrectValue22() {
        assertEquals("0123456789", CallUtil.getSearchFormattedPhoneNumber(mPhoneNumberTwentyTwoActual));
    }

    @Test
    public void cleanPhoneNumber_correctInput_returnsCorrectValue23() {
        assertEquals("0123456789", CallUtil.getSearchFormattedPhoneNumber(mPhoneNumberTwentyThreeActual));
    }

    @Test
    public void cleanPhoneNumber_correctInput_returnsCorrectValue24() {
        assertEquals("", CallUtil.getSearchFormattedPhoneNumber(mPhoneNumberTwentyFourActual));
    }

    @Test
    public void cleanPhoneNumber_correctInput_returnsCorrectValue25() {
        assertEquals("", CallUtil.getSearchFormattedPhoneNumber(mPhoneNumberTwentyFiveActual));
    }

    @Test
    public void cleanPhoneNumber_correctInput_returnsCorrectValue26() {
        assertEquals("+", CallUtil.getSearchFormattedPhoneNumber(mPhoneNumberTwentySixActual));
    }

    @Test
    public void cleanPhoneNumber_correctInput_returnsCorrectValue27() {
        assertEquals("1", CallUtil.getSearchFormattedPhoneNumber(mPhoneNumberTwentySevenActual));
    }

    @Test
    public void cleanPhoneNumber_correctInput_returnsCorrectValue28() {
        assertEquals("234567890", CallUtil.getSearchFormattedPhoneNumber(mPhoneNumberTwentyEightActual));
    }

    @Test
    public void cleanPhoneNumber_correctInput_returnsCorrectValue29() {
        assertEquals("123456789", CallUtil.getSearchFormattedPhoneNumber(mPhoneNumberTwentyNineActual));
    }

    @Test
    public void cleanPhoneNumber_noInput_returnsNull() {
        assertEquals("", CallUtil.cleanPhoneNumber(""));
    }

    @Test
    public void cleanForTextWatcher_correctInput1_returnsCorrectValue1() {
        assertEquals("+12223334444", CallUtil.cleanForTextWatcher(mPhoneNumberOneActual));
    }

    @Test
    public void cleanForTextWatcher_correctInput2_returnsCorrectValue2() {
        assertEquals("+12223334444", CallUtil.cleanForTextWatcher(mPhoneNumberTwoActual));
    }

    @Test
    public void cleanForTextWatcher_correctInput3_returnsCorrectValue3() {
        assertEquals("+12223334444", CallUtil.cleanForTextWatcher(mPhoneNumberThreeActual));
    }

    @Test
    public void cleanForTextWatcher_correctInput_4returnsCorrectValue4() {
        assertEquals("2223334444", CallUtil.cleanForTextWatcher(mPhoneNumberFourActual));
    }

    @Test
    public void cleanForTextWatcher_correctInput5_returnsCorrectValue5() {
        assertEquals("1111", CallUtil.cleanForTextWatcher(mPhoneNumberFiveActual));
    }

    @Test
    public void cleanForTextWatcher_correctInput6_returnsCorrectValue6() {
        assertEquals("2223334444;00,00", CallUtil.cleanForTextWatcher(mPhoneNumberSixActual));
    }

    @Test
    public void cleanForTextWatcher_correctInput7_returnsCorrectValue7() {
        assertEquals("+12223334444,00", CallUtil.cleanForTextWatcher(mPhoneNumberSevenActual));
    }

    @Test
    public void cleanForTextWatcher_correctInput8_returnsCorrectValue8() {
        assertEquals("+12223334444,00#", CallUtil.cleanForTextWatcher(mPhoneNumberEightActual));
    }

    @Test
    public void cleanForTextWatcher_correctInput9_returnsCorrectValue9() {
        assertEquals("1111,00#", CallUtil.cleanForTextWatcher(mPhoneNumberNineActual));
    }

    @Test
    public void cleanForTextWatcher_correctInput10_returnsCorrectValue10() {
        assertEquals("1111;00#", CallUtil.cleanForTextWatcher(mPhoneNumberTenActual));
    }

    @Test
    public void cleanForTextWatcher_correctInput11_returnsCorrectValue11() {
        assertEquals("*007;00#", CallUtil.cleanForTextWatcher(mPhoneNumberElevenActual));
    }

    @Test
    public void cleanForTextWatcher_correctInput12_returnsCorrectValue12() {
        assertEquals("*007,00#", CallUtil.cleanForTextWatcher(mPhoneNumberTwelveActual));
    }

    @Test
    public void cleanForTextWatcher_correctInput13_returnsCorrectValue13() {
        assertEquals("1111;222;333;444", CallUtil.cleanForTextWatcher(mPhoneNumberThirteenActual));
    }

    @Test
    public void cleanForTextWatcher_correctInput14_returnsCorrectValue14() {
        assertEquals("+123456789*0#.,$pw", CallUtil.cleanForTextWatcher(mPhoneNumberFourteenActual));
    }

    @Test
    public void cleanForTextWatcher_correctInput15_returnsCorrectValue15() {
        assertEquals("+0123456789", CallUtil.cleanForTextWatcher(mPhoneNumberFifteenActual));
    }

    @Test
    public void cleanForTextWatcher_correctInput16_returnsCorrectValue16() {
        assertEquals("0123456789", CallUtil.cleanForTextWatcher(mPhoneNumberSixteenActual));
    }

    @Test
    public void cleanForTextWatcher_correctInput17_returnsCorrectValue17() {
        assertEquals("10123456789", CallUtil.cleanForTextWatcher(mPhoneNumberSeventeenActual));
    }

    @Test
    public void cleanForTextWatcher_correctInput18_returnsCorrectValue18() {
        assertEquals("+10123456789", CallUtil.cleanForTextWatcher(mPhoneNumberEighteenActual));
    }

    @Test
    public void cleanForTextWatcher_correctInput19_returnsCorrectValue20() {
        assertEquals("A+10123456789", CallUtil.cleanForTextWatcher(mPhoneNumberNineteenActual));
    }

    @Test
    public void cleanForTextWatcher_correctInput20_returnsCorrectValue21() {
        assertEquals("+B10123456789", CallUtil.cleanForTextWatcher(mPhoneNumberTwentyActual));
    }

    @Test
    public void cleanForTextWatcher_correctInput21_returnsCorrectValue22() {
        assertEquals("+1C0123456789", CallUtil.cleanForTextWatcher(mPhoneNumberTwentyOneActual));
    }

    @Test
    public void cleanForTextWatcher_correctInput22_returnsCorrectValue23() {
        assertEquals("+101234D56789", CallUtil.cleanForTextWatcher(mPhoneNumberTwentyTwoActual));
    }

    @Test
    public void cleanForTextWatcher_correctInput23_returnsCorrectValue24() {
        assertEquals("+10123456789E", CallUtil.cleanForTextWatcher(mPhoneNumberTwentyThreeActual));
    }

    @Test
    public void cleanForTextWatcher_correctInput24_returnsCorrectValue25() {
        assertEquals("+1ABCDEFGHIJ", CallUtil.cleanForTextWatcher(mPhoneNumberTwentyFourActual));
    }

    @Test
    public void cleanForTextWatcher_correctInput25_returnsCorrectValue26() {
        assertEquals("ABCDEFGHIJ", CallUtil.cleanForTextWatcher(mPhoneNumberTwentyFiveActual));
    }

    @Test
    public void cleanForTextWatcher_correctInput26_returnsCorrectValue27() {
        assertEquals("+ABCDEFGHIJ", CallUtil.cleanForTextWatcher(mPhoneNumberTwentySixActual));
    }

    @Test
    public void cleanForTextWatcher_correctInput27_returnsCorrectValue28() {
        assertEquals("1ABCDEFGHIJ", CallUtil.cleanForTextWatcher(mPhoneNumberTwentySevenActual));
    }

    @Test
    public void cleanForTextWatcher_correctInput28_returnsCorrectValue29() {
        assertEquals("+1234567890;ABCD", CallUtil.cleanForTextWatcher(mPhoneNumberTwentyEightActual));
    }

    @Test
    public void cleanForTextWatcher_correctInput29_returnsCorrectValue30() {
        assertEquals("123456789;ABCD", CallUtil.cleanForTextWatcher(mPhoneNumberTwentyNineActual));
    }

    @Test
    public void cleanForTextWatcher_emptyStringInput_returnsEmptyString() {
        assertEquals("", CallUtil.cleanForTextWatcher(""));
    }

    @Test
    public void cleanForTextWatcher_nullInput_returnsEmptyString() {
        assertEquals("", CallUtil.cleanForTextWatcher(null));
    }

    @Test
    public void sortCallLogEntries_sortsCorrectly() {
        ArrayList<CallLogEntry> callLogEntries = new ArrayList<>();

        CallLogEntry entryOne = new CallLogEntry("111", "Ben Anas", 1523661568L, "1", "1112223333", Enums.Calls.CallTypes.MISSED, null, null, Enums.Contacts.PresenceStates.NONE, -128, "", 0, "jid@jid.im", Enums.Contacts.PresenceTypes.AVAILABLE,123, "");
        CallLogEntry entryTwo = new CallLogEntry("123", "Carrie Okaey", 1523914557L, "1", "1112223333", Enums.Calls.CallTypes.MISSED, null, null, Enums.Contacts.PresenceStates.NONE, -128, "", 0, "jid@jid.im", Enums.Contacts.PresenceTypes.AVAILABLE,123, "");
        CallLogEntry entryThree = new CallLogEntry("222", "Lynn Guini", 1523896864L, "1", "1112223333", Enums.Calls.CallTypes.MISSED, null, null, Enums.Contacts.PresenceStates.NONE, -128, "", 0, "jid@jid.im", Enums.Contacts.PresenceTypes.AVAILABLE,123, "");
        CallLogEntry entryFour = new CallLogEntry("234", "Vinny Gurr", 1523915116L, "1", "1112223333", Enums.Calls.CallTypes.MISSED, null, null, Enums.Contacts.PresenceStates.NONE, -128, "", 0, "jid@jid.im", Enums.Contacts.PresenceTypes.AVAILABLE,123, "");

        callLogEntries.add(entryOne);
        callLogEntries.add(entryTwo);
        callLogEntries.add(entryThree);
        callLogEntries.add(entryFour);

        CallUtil.sortCallLogEntries(callLogEntries);

        assertEquals(entryFour, callLogEntries.get(0));
        assertEquals(entryTwo, callLogEntries.get(1));
        assertEquals(entryThree, callLogEntries.get(2));
        assertEquals(entryOne, callLogEntries.get(3));
    }

    @Test
    public void separatePhoneNumberFromDTMFTones_correctInput_returnsCorrectValue1() {
        String phoneNumberOneExpected = "12223334444";
        assertEquals(phoneNumberOneExpected, CallUtil.separatePhoneNumberFromDTMFTones(mPhoneNumberOneActual)[0]);
    }

    @Test
    public void separatePhoneNumberFromDTMFTones_correctInput_returnsCorrectValue2() {
        String phoneNumberExpected = "12223334444";
        assertEquals(phoneNumberExpected, CallUtil.separatePhoneNumberFromDTMFTones(mPhoneNumberTwoActual)[0]);
    }

    @Test
    public void separatePhoneNumberFromDTMFTones_correctInput_returnsCorrectValue3() {
        String phoneNumberExpected = "12223334444";
        assertEquals(phoneNumberExpected, CallUtil.separatePhoneNumberFromDTMFTones(mPhoneNumberThreeActual)[0]);
    }

    @Test
    public void separatePhoneNumberFromDTMFTones_correctInput_returnsCorrectValue4() {
        String phoneNumberExpected = "2223334444";
        assertEquals(phoneNumberExpected, CallUtil.separatePhoneNumberFromDTMFTones(mPhoneNumberFourActual)[0]);
    }

    @Test
    public void separatePhoneNumberFromDTMFTones_correctInput_returnsCorrectValue5() {
        String phoneNumberExpected = "1111";
        assertEquals(phoneNumberExpected, CallUtil.separatePhoneNumberFromDTMFTones(mPhoneNumberFiveActual)[0]);
    }

    @Test
    public void separatePhoneNumberFromDTMFTones_correctInput_returnsCorrectValue6() {
        String phoneNumberExpected = "2223334444",
                phoneNumberExpected1 = "00,00";

        assertEquals(phoneNumberExpected, CallUtil.separatePhoneNumberFromDTMFTones(mPhoneNumberSixActual)[0]);
        assertEquals(phoneNumberExpected1, CallUtil.separatePhoneNumberFromDTMFTones(mPhoneNumberSixActual)[1]);
    }

    @Test
    public void separatePhoneNumberFromDTMFTones_correctInput_returnsCorrectValue7() {
        String phoneNumberExpected = "12223334444",
                phoneNumberExpected1 = "00";

        assertEquals(phoneNumberExpected, CallUtil.separatePhoneNumberFromDTMFTones(mPhoneNumberSevenActual)[0]);
        assertEquals(phoneNumberExpected1, CallUtil.separatePhoneNumberFromDTMFTones(mPhoneNumberSevenActual)[1]);
    }

    @Test
    public void separatePhoneNumberFromDTMFTones_correctInput_returnsCorrectValue8() {
        String phoneNumberExpected = "12223334444",
                phoneNumberExpected1 = "00#";

        assertEquals(phoneNumberExpected, CallUtil.separatePhoneNumberFromDTMFTones(mPhoneNumberEightActual)[0]);
        assertEquals(phoneNumberExpected1, CallUtil.separatePhoneNumberFromDTMFTones(mPhoneNumberEightActual)[1]);
    }

    @Test
    public void separatePhoneNumberFromDTMFTones_correctInput_returnsCorrectValue9() {
        String phoneNumberExpected = "1111",
                phoneNumberExpected1 = "00#";

        assertEquals(phoneNumberExpected, CallUtil.separatePhoneNumberFromDTMFTones(mPhoneNumberNineActual)[0]);
        assertEquals(phoneNumberExpected1, CallUtil.separatePhoneNumberFromDTMFTones(mPhoneNumberNineActual)[1]);
    }

    @Test
    public void separatePhoneNumberFromDTMFTones_correctInput_returnsCorrectValue10() {
        String phoneNumberExpected = "1111",
                phoneNumberExpected1 = "00#";

        assertEquals(phoneNumberExpected, CallUtil.separatePhoneNumberFromDTMFTones(mPhoneNumberTenActual)[0]);
        assertEquals(phoneNumberExpected1, CallUtil.separatePhoneNumberFromDTMFTones(mPhoneNumberTenActual)[1]);
    }

    @Test
    public void separatePhoneNumberFromDTMFTones_correctInput_returnsCorrectValue11() {
        String phoneNumberExpected = "*007",
                phoneNumberExpected1 = "00#";

        assertEquals(phoneNumberExpected, CallUtil.separatePhoneNumberFromDTMFTones(mPhoneNumberElevenActual)[0]);
        assertEquals(phoneNumberExpected1, CallUtil.separatePhoneNumberFromDTMFTones(mPhoneNumberElevenActual)[1]);
    }

    @Test
    public void separatePhoneNumberFromDTMFTones_correctInput_returnsCorrectValue12() {
        String phoneNumberExpected = "*007",
                phoneNumberExpected1 = "00#";

        assertEquals(phoneNumberExpected, CallUtil.separatePhoneNumberFromDTMFTones(mPhoneNumberTwelveActual)[0]);
        assertEquals(phoneNumberExpected1, CallUtil.separatePhoneNumberFromDTMFTones(mPhoneNumberTwelveActual)[1]);
    }

    @Test
    public void separatePhoneNumberFromDTMFTones_correctInput_returnsCorrectValue13() {
        String phoneNumberExpected = "1111",
                phoneNumberExpected1 = "222;333;444";

        assertEquals(phoneNumberExpected, CallUtil.separatePhoneNumberFromDTMFTones(mPhoneNumberThirteenActual)[0]);
        assertEquals(phoneNumberExpected1, CallUtil.separatePhoneNumberFromDTMFTones(mPhoneNumberThirteenActual)[1]);
    }

    @Test
    public void separatePhoneNumberFromDTMFTones_correctInput_returnsCorrectValue14() {
        String phoneNumberExpected = "123456789*0#",
                phoneNumberExpected1 = "";

        assertEquals(phoneNumberExpected, CallUtil.separatePhoneNumberFromDTMFTones(mPhoneNumberFourteenActual)[0]);
        assertEquals(phoneNumberExpected1, CallUtil.separatePhoneNumberFromDTMFTones(mPhoneNumberFourteenActual)[1]);
    }

    @Test
    public void isValidSMSNumber_returnsFalse1() {
        assertFalse(CallUtil.isValidSMSNumber(mSmsShortCodeOne));
    }

    @Test
    public void isValidSMSNumber_returnsFalse2() {
        assertFalse(CallUtil.isValidSMSNumber(mSmsShortCodeTwo));
    }

    @Test
    public void isValidSMSNumber_returnsTrue3() {
        assertTrue(CallUtil.isValidSMSNumber(mSmsShortCodeThree));
    }

    @Test
    public void isValidSMSNumber_returnsTrue4(){
        assertTrue(CallUtil.isValidSMSNumber(mSmsShortCodeFour));
    }

    @Test
    public void isValidSMSNumber_returnsFalse5() {
        assertFalse(CallUtil.isValidSMSNumber(mSmsShortCodeFive));
    }

    @Test
    public void isValidSMSNumber_returnsFalse6() {
        assertFalse(CallUtil.isValidSMSNumber(mSmsShortCodeSix));
    }

    @Test
    public void isValidSMSNumber_returnsFalse7() {
        assertFalse(CallUtil.isValidSMSNumber(mSmsShortCodeSeven));
    }

    @Test
    public void isValidSMSNumber_returnsFalse8() {
        assertFalse(CallUtil.isValidSMSNumber(mSmsShortCodeEight));
    }

    @Test
    public void isValidSMSNumber__returnsFalse9() {
        assertFalse(CallUtil.isValidSMSNumber(mSmsShortCodeNine));
    }

    @Test
    public void isValidSMSNumber_returnsFalse10() {
        assertFalse(CallUtil.isValidSMSNumber(mSmsShortCodeTen));
    }

    @Test
    public void isValidSMSNumber_returnsTrue11() {
        assertTrue(CallUtil.isValidSMSNumber(mPhoneNumberOneActual));
    }

    @Test
    public void isValidSMSNumber_returnsTrue12() {
        assertTrue(CallUtil.isValidSMSNumber(mPhoneNumberTwoActual));
    }

    @Test
    public void isValidSMSNumber_returnsTrue13() {
        assertTrue(CallUtil.isValidSMSNumber(mPhoneNumberThreeActual));
    }

    @Test
    public void isValidSMSNumber_returnsTrue14() {
        assertTrue(CallUtil.isValidSMSNumber(mPhoneNumberFourActual));
    }

    @Test
    public void isValidSMSNumber_returnsFalse15() {
        assertFalse(CallUtil.isValidSMSNumber(mPhoneNumberFiveActual));
    }

    @Test
    public void isValidSMSNumber_returnsFalse16() {
        assertFalse(CallUtil.isValidSMSNumber(mPhoneNumberSixActual));
    }

    @Test
    public void isValidSMSNumber_returnsFalse17() {
        assertFalse(CallUtil.isValidSMSNumber(mPhoneNumberSevenActual));
    }

    @Test
    public void isValidSMSNumber_returnsFalse18() {
        assertFalse(CallUtil.isValidSMSNumber(mPhoneNumberEightActual));
    }

    @Test
    public void isValidSMSNumber_returnsFalse19() {
        assertFalse(CallUtil.isValidSMSNumber(mPhoneNumberNineActual));
    }

    @Test
    public void isValidSMSNumber_returnsFalse20() {
        assertFalse(CallUtil.isValidSMSNumber(mPhoneNumberTenActual));
    }

    @Test
    public void isValidSMSNumber_returnsFalse21() {
        assertFalse(CallUtil.isValidSMSNumber(mPhoneNumberElevenActual));
    }

    @Test
    public void isValidSMSNumber_returnsFalse22() {
        assertFalse(CallUtil.isValidSMSNumber(mPhoneNumberTwelveActual));
    }

    @Test
    public void isValidSMSNumber_returnsFalse23() {
        assertFalse(CallUtil.isValidSMSNumber(mPhoneNumberThirteenActual));
    }

    @Test
    public void isValidSMSNumber_returnsFalse24() {
        assertFalse(CallUtil.isValidSMSNumber(mPhoneNumberFourteenActual));
    }

    @Test
    public void isValidSMSNumber_returnsFalse25() {
        assertFalse(CallUtil.isValidSMSNumber(mPhoneNumberFifteenActual));
    }

    @Test
    public void isValidSMSNumber_returnsFalse26() {
        assertFalse(CallUtil.isValidSMSNumber(mPhoneNumberSixteenActual));
    }

    @Test
    public void isValidSMSNumber_returnsFalse27() {
        assertFalse(CallUtil.isValidSMSNumber(mPhoneNumberSeventeenActual));
    }

    @Test
    public void isValidSMSNumber_returnsFalse28() {
        assertFalse(CallUtil.isValidSMSNumber(mPhoneNumberEighteenActual));
    }

    @Test
    public void isValidSMSNumber_returnsFalse29() {
        assertFalse(CallUtil.isValidSMSNumber(mSmsShortCodeEleven));
    }

    @Test
    public void isValidSMSNumber_returnsFalse30() {
        assertFalse(CallUtil.isValidSMSNumber(mSmsShortCodeTwelve));
    }

    @Test
    public void isValidSMSNumber_returnsFalse31() {
        assertFalse(CallUtil.isValidSMSNumber(mNoNumber));
    }

    @Test
    public void isValidSMSNumber_returnsFalse32() {
        assertFalse(CallUtil.isValidSMSNumber(mPhoneNumberNineteenActual));
    }

    @Test
    public void isValidSMSNumber_returnsFalse33() {
        assertFalse(CallUtil.isValidSMSNumber(mPhoneNumberTwentyActual));
    }

    @Test
    public void isValidSMSNumber_returnsFalse34() {
        assertFalse(CallUtil.isValidSMSNumber(mPhoneNumberTwentyOneActual));
    }

    @Test
    public void isValidSMSNumber_returnsFalse35() {
        assertFalse(CallUtil.isValidSMSNumber(mPhoneNumberTwentyTwoActual));
    }

    @Test
    public void isValidSMSNumber_returnsFalse36() {
        assertFalse(CallUtil.isValidSMSNumber(mPhoneNumberTwentyTwoActual));
    }

    @Test
    public void isValidSMSNumber_returnsFalse37() {
        assertFalse(CallUtil.isValidSMSNumber(mPhoneNumberTwentyThreeActual));
    }

    @Test
    public void isValidSMSNumber_returnsFalse38() {
        assertFalse(CallUtil.isValidSMSNumber(mPhoneNumberTwentyFourActual));
    }

    @Test
    public void isValidSMSNumber_returnsFalse39() {
        assertFalse(CallUtil.isValidSMSNumber(mPhoneNumberTwentyFiveActual));
    }


    @Test
    public void isValidSMSNumber_returnsFalse40() {
        assertFalse(CallUtil.isValidSMSNumber(mPhoneNumberTwentySixActual));
    }


    @Test
    public void isValidSMSNumber_returnsFalse41() {
        assertFalse(CallUtil.isValidSMSNumber(mPhoneNumberTwentySevenActual));
    }


    @Test
    public void isValidSMSNumber_returnsFalse42() {
        assertFalse(CallUtil.isValidSMSNumber(mPhoneNumberTwentyEightActual));
    }


    @Test
    public void isValidSMSNumber_returnsFalse43() {
        assertFalse(CallUtil.isValidSMSNumber(mPhoneNumberTwentyNineActual));
    }

    @Test
    public void isValidSMSNumber_returnsFalse44() {
        assertFalse(CallUtil.isValidSMSNumber(mSmsShortCodeThirteen));
    }

    @Test
    public void isValidSMSNumber_returnsFalse45() {
        assertFalse(CallUtil.isValidSMSNumber(mSmsShortCodeFourteen));
    }


}
