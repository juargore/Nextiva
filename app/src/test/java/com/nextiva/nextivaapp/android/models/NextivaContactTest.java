/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import android.net.Uri;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;

import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.db.model.Address;
import com.nextiva.nextivaapp.android.db.model.DbPresence;
import com.nextiva.nextivaapp.android.db.model.DbVCard;
import com.nextiva.nextivaapp.android.db.model.EmailAddress;
import com.nextiva.nextivaapp.android.db.model.PhoneNumber;
import com.nextiva.nextivaapp.android.mocks.values.ContactLists;
import com.nextiva.nextivaapp.android.util.CallUtil;
import com.nextiva.nextivaapp.android.util.StringUtil;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by adammacdonald on 3/22/18.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest( {TextUtils.class, Uri.class, StringUtil.class, CallUtil.class, PhoneNumberUtils.class})
public class NextivaContactTest {


    private static MockedStatic<TextUtils> textUtils;
    private static MockedStatic<PhoneNumberUtils> phoneNumberUtils;

    @BeforeClass
    public static void global() {
        phoneNumberUtils = Mockito.mockStatic(PhoneNumberUtils.class);
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

    @Test
    public void getContactType_hasNullValue_returnsTypeNone() {
        NextivaContact nextivaContact = new NextivaContact("userId");

        assertEquals(Enums.Contacts.ContactTypes.NONE, nextivaContact.getContactType().intValue());
    }

    @Test
    public void getContactType_hasValue_returnsCorrectValue() {
        NextivaContact nextivaContact = new NextivaContact("userId");
        nextivaContact.setContactType(Enums.Contacts.ContactTypes.ENTERPRISE);

        assertEquals(Enums.Contacts.ContactTypes.ENTERPRISE, nextivaContact.getContactType().intValue());
    }

    @Test
    public void isFavorite_hasNullValue_returnsFalse() {
        NextivaContact nextivaContact = new NextivaContact("userId");

        assertFalse(nextivaContact.isFavorite());
    }

    @Test
    public void isFavorite_hasValue_returnsCorrectValue() {
        NextivaContact nextivaContact = new NextivaContact("userId");
        nextivaContact.setIsFavorite(true);

        assertTrue(nextivaContact.isFavorite());
    }

    @Test
    public void isRosterContact_hasNullValue_returnsFalse() {
        NextivaContact nextivaContact = new NextivaContact("userId");

        assertFalse(nextivaContact.isRosterContact());
    }

    @Test
    public void getUiName_hasDisplayName_returnsDisplayName() {
        NextivaContact nextivaContact = new NextivaContact("userId");
        nextivaContact.setDisplayName("Display Name");
        nextivaContact.setFirstName("Jim");
        nextivaContact.setLastName("Smith");
        nextivaContact.setCompany("Company");
        nextivaContact.setJid("jim.smith@nextiva.im");
        nextivaContact.setAllPhoneNumbers(new ArrayList<PhoneNumber>() {{
            add(new PhoneNumber(Enums.Contacts.PhoneTypes.HOME_PHONE, "2223334444", null));
            add(new PhoneNumber(Enums.Contacts.PhoneTypes.WORK_EXTENSION, "3333"));
            add(new PhoneNumber(Enums.Contacts.PhoneTypes.CONFERENCE_PHONE, "2223334444", "1111", "0000"));
        }});

        nextivaContact.setEmailAddresses(new ArrayList<EmailAddress>() {{
            add(new EmailAddress(Enums.Contacts.EmailTypes.OTHER_EMAIL, "fake@email.com", null));
        }});

        assertEquals("Display Name", nextivaContact.getUiName());
    }

    @Test
    public void getUiName_hasNoDisplayName_returnsFullName() {
        NextivaContact nextivaContact = new NextivaContact("userId");
        nextivaContact.setDisplayName(null);
        nextivaContact.setFirstName("Jim");
        nextivaContact.setLastName("Smith");
        nextivaContact.setCompany("Company");
        nextivaContact.setJid("jim.smith@nextiva.im");
        nextivaContact.setAllPhoneNumbers(new ArrayList<PhoneNumber>() {{
            add(new PhoneNumber(Enums.Contacts.PhoneTypes.HOME_PHONE, "2223334444", null));
            add(new PhoneNumber(Enums.Contacts.PhoneTypes.WORK_EXTENSION, "3333"));
            add(new PhoneNumber(Enums.Contacts.PhoneTypes.CONFERENCE_PHONE, "2223334444", "1111", "0000"));
        }});
        nextivaContact.setEmailAddresses(new ArrayList<EmailAddress>() {{
            add(new EmailAddress(Enums.Contacts.EmailTypes.OTHER_EMAIL, "fake@email.com", null));
        }});

        assertEquals("Jim Smith", nextivaContact.getUiName());
    }

    @Test
    public void getUiName_hasNoFullName_returnsLastName() {
        NextivaContact nextivaContact = new NextivaContact("userId");
        nextivaContact.setDisplayName(null);
        nextivaContact.setFirstName(null);
        nextivaContact.setLastName("Smith");
        nextivaContact.setCompany("Company");
        nextivaContact.setJid("jim.smith@nextiva.im");
        nextivaContact.setAllPhoneNumbers(new ArrayList<PhoneNumber>() {{
            add(new PhoneNumber(Enums.Contacts.PhoneTypes.HOME_PHONE, "2223334444", null));
            add(new PhoneNumber(Enums.Contacts.PhoneTypes.WORK_EXTENSION, "3333"));
            add(new PhoneNumber(Enums.Contacts.PhoneTypes.CONFERENCE_PHONE, "2223334444", "1111", "0000"));
        }});
        nextivaContact.setEmailAddresses(new ArrayList<EmailAddress>() {{
            add(new EmailAddress(Enums.Contacts.EmailTypes.OTHER_EMAIL, "fake@email.com", null));
        }});

        assertEquals("Smith", nextivaContact.getUiName());
    }

    @Test
    public void getUiName_hasNoLastName_returnsFirstName() {
        NextivaContact nextivaContact = new NextivaContact("userId");
        nextivaContact.setDisplayName(null);
        nextivaContact.setFirstName("Jim");
        nextivaContact.setLastName(null);
        nextivaContact.setCompany("Company");
        nextivaContact.setJid("jim.smith@nextiva.im");
        nextivaContact.setAllPhoneNumbers(new ArrayList<PhoneNumber>() {{
            add(new PhoneNumber(Enums.Contacts.PhoneTypes.HOME_PHONE, "2223334444", null));
            add(new PhoneNumber(Enums.Contacts.PhoneTypes.WORK_EXTENSION, "3333"));
            add(new PhoneNumber(Enums.Contacts.PhoneTypes.CONFERENCE_PHONE, "2223334444", "1111", "0000"));
        }});
        nextivaContact.setEmailAddresses(new ArrayList<EmailAddress>() {{
            add(new EmailAddress(Enums.Contacts.EmailTypes.OTHER_EMAIL, "fake@email.com", null));
        }});

        assertEquals("Jim", nextivaContact.getUiName());
    }

    @Test
    public void getUiName_hasNoFirst_returnsCompany() {
        NextivaContact nextivaContact = new NextivaContact("userId");
        nextivaContact.setDisplayName(null);
        nextivaContact.setFirstName(null);
        nextivaContact.setLastName(null);
        nextivaContact.setCompany("Company");
        nextivaContact.setJid("jim.smith@nextiva.im");
        nextivaContact.setAllPhoneNumbers(new ArrayList<PhoneNumber>() {{
            add(new PhoneNumber(Enums.Contacts.PhoneTypes.HOME_PHONE, "2223334444", null));
            add(new PhoneNumber(Enums.Contacts.PhoneTypes.WORK_EXTENSION, "3333"));
            add(new PhoneNumber(Enums.Contacts.PhoneTypes.CONFERENCE_PHONE, "2223334444", "1111", "0000"));
        }});
        nextivaContact.setEmailAddresses(new ArrayList<EmailAddress>() {{
            add(new EmailAddress(Enums.Contacts.EmailTypes.OTHER_EMAIL, "fake@email.com", null));
        }});

        assertEquals("Company", nextivaContact.getUiName());
    }

    @Test
    public void getUiName_hasNoCompany_returnsJid() {
        NextivaContact nextivaContact = new NextivaContact("userId");
        nextivaContact.setDisplayName(null);
        nextivaContact.setFirstName(null);
        nextivaContact.setLastName(null);
        nextivaContact.setCompany(null);
        nextivaContact.setJid("jim.smith@nextiva.im");
        nextivaContact.setAllPhoneNumbers(new ArrayList<PhoneNumber>() {{
            add(new PhoneNumber(Enums.Contacts.PhoneTypes.HOME_PHONE, "2223334444", null));
            add(new PhoneNumber(Enums.Contacts.PhoneTypes.WORK_EXTENSION, "3333"));
            add(new PhoneNumber(Enums.Contacts.PhoneTypes.CONFERENCE_PHONE, "2223334444", "1111", "0000"));
        }});
        nextivaContact.setEmailAddresses(new ArrayList<EmailAddress>() {{
            add(new EmailAddress(Enums.Contacts.EmailTypes.OTHER_EMAIL, "fake@email.com", null));
        }});

        assertEquals("jim.smith@nextiva.im", nextivaContact.getUiName());
    }

    @Test
    public void getUiName_hasNoJid_returnsFirstPhoneNumber() {
        PowerMockito.when(PhoneNumberUtils.formatNumber("2223334444", Locale.getDefault().getCountry())).thenAnswer(invocation -> "(222) 333-4444");

        NextivaContact nextivaContact = new NextivaContact("userId");
        nextivaContact.setDisplayName(null);
        nextivaContact.setFirstName(null);
        nextivaContact.setLastName(null);
        nextivaContact.setCompany(null);
        nextivaContact.setJid(null);
        nextivaContact.setAllPhoneNumbers(new ArrayList<PhoneNumber>() {{
            add(new PhoneNumber(Enums.Contacts.PhoneTypes.HOME_PHONE, "2223334444", null));
            add(new PhoneNumber(Enums.Contacts.PhoneTypes.WORK_EXTENSION, "3333"));
            add(new PhoneNumber(Enums.Contacts.PhoneTypes.CONFERENCE_PHONE, "2223334444", "1111", "0000"));
        }});
        nextivaContact.setEmailAddresses(new ArrayList<EmailAddress>() {{
            add(new EmailAddress(Enums.Contacts.EmailTypes.OTHER_EMAIL, "fake@email.com", null));
        }});

        assertEquals("(222) 333-4444", nextivaContact.getUiName());
    }

    @Test
    public void getUiName_hasNoPhoneNumbers_returnsFirstExtension() {
        NextivaContact nextivaContact = new NextivaContact("userId");
        nextivaContact.setDisplayName(null);
        nextivaContact.setFirstName(null);
        nextivaContact.setLastName(null);
        nextivaContact.setCompany(null);
        nextivaContact.setJid(null);
        nextivaContact.setAllPhoneNumbers(new ArrayList<PhoneNumber>() {{
            add(new PhoneNumber(Enums.Contacts.PhoneTypes.WORK_EXTENSION, "3333"));
            add(new PhoneNumber(Enums.Contacts.PhoneTypes.CONFERENCE_PHONE, "2223334444", "1111", "0000"));
        }});
        nextivaContact.setEmailAddresses(new ArrayList<EmailAddress>() {{
            add(new EmailAddress(Enums.Contacts.EmailTypes.OTHER_EMAIL, "fake@email.com", null));
        }});

        assertEquals("3333", nextivaContact.getUiName());
    }


    @Test
    public void getUiName_hasNoExtensions_returnsFirstConferenceNumber_2() {
        NextivaContact nextivaContact = new NextivaContact("userId");
        nextivaContact.setDisplayName(null);
        nextivaContact.setFirstName(null);
        nextivaContact.setLastName(null);
        nextivaContact.setCompany(null);
        nextivaContact.setJid(null);
        nextivaContact.setAllPhoneNumbers(new ArrayList<PhoneNumber>() {{
            add(new PhoneNumber(Enums.Contacts.PhoneTypes.CONFERENCE_PHONE, "2223334444", "1111", "0000"));
        }});
        nextivaContact.setEmailAddresses(new ArrayList<EmailAddress>() {{
            add(new EmailAddress(Enums.Contacts.EmailTypes.OTHER_EMAIL, "fake@email.com", null));
        }});

        assertEquals("2223334444,1111#,0000#", nextivaContact.getUiName());
    }

    @Test
    public void getUiName_hasNoConferenceNumbers_returnsFirstEmail() {
        NextivaContact nextivaContact = new NextivaContact("userId");
        nextivaContact.setDisplayName(null);
        nextivaContact.setFirstName(null);
        nextivaContact.setLastName(null);
        nextivaContact.setCompany(null);
        nextivaContact.setJid(null);
        nextivaContact.setAllPhoneNumbers(null);
        nextivaContact.setEmailAddresses(new ArrayList<EmailAddress>() {{
            add(new EmailAddress(Enums.Contacts.EmailTypes.OTHER_EMAIL, "fake@email.com", null));
        }});

        assertEquals("fake@email.com", nextivaContact.getUiName());
    }

    @Test
    public void getUiName_hasNoEmails_returnsNull() {
        NextivaContact nextivaContact = new NextivaContact("userId");
        nextivaContact.setDisplayName(null);
        nextivaContact.setFirstName(null);
        nextivaContact.setLastName(null);
        nextivaContact.setCompany(null);
        nextivaContact.setJid(null);
        nextivaContact.setAllPhoneNumbers(null);
        nextivaContact.setEmailAddresses(null);

        assertNull(nextivaContact.getUiName());
    }

    @Test
    public void getAvatarName_hasDisplayName_returnsDisplayName() {
        NextivaContact nextivaContact = new NextivaContact("userId");
        nextivaContact.setDisplayName("Display Name");
        nextivaContact.setFirstName("Jim");
        nextivaContact.setLastName("Smith");

        assertEquals("Display Name", nextivaContact.getAvatarName());
    }

    @Test
    public void getAvatarName_hasNoDisplayName_returnsFullName() {
        NextivaContact nextivaContact = new NextivaContact("userId");
        nextivaContact.setDisplayName(null);
        nextivaContact.setFirstName("Jim");
        nextivaContact.setLastName("Smith");

        assertEquals("Jim Smith", nextivaContact.getAvatarName());
    }

    @Test
    public void getAvatarName_hasNoFullName_returnsLastName() {
        NextivaContact nextivaContact = new NextivaContact("userId");
        nextivaContact.setDisplayName(null);
        nextivaContact.setFirstName(null);
        nextivaContact.setLastName("Smith");

        assertEquals("Smith", nextivaContact.getAvatarName());
    }

    @Test
    public void getAvatarName_hasNoLastName_returnsFirstName() {
        NextivaContact nextivaContact = new NextivaContact("userId");
        nextivaContact.setDisplayName(null);
        nextivaContact.setFirstName("Jim");
        nextivaContact.setLastName(null);

        assertEquals("Jim", nextivaContact.getAvatarName());
    }

    @Test
    public void getAvatarName_hasNoFirst_returnsNull() {
        NextivaContact nextivaContact = new NextivaContact("userId");
        nextivaContact.setDisplayName(null);
        nextivaContact.setFirstName(null);
        nextivaContact.setLastName(null);

        assertNull(nextivaContact.getAvatarName());
    }

    @Test
    public void getAvatarName_hasNoFullName_returnsC(){
        NextivaContact nextivaContact = new NextivaContact("userId");
        nextivaContact.setFirstName(null);
        nextivaContact.setLastName(null);
        nextivaContact.setCompany("Company");

        assertEquals(Enums.AvatarDisplays.COMPANY, nextivaContact.getAvatarName());
    }

    @Test
    public void getAvatarName_hasNoFirstName_returnsC(){
        NextivaContact nextivaContact = new NextivaContact("userId");
        nextivaContact.setFirstName(null);
        nextivaContact.setLastName("Smith");
        nextivaContact.setCompany("Company");

        assertEquals(Enums.AvatarDisplays.COMPANY, nextivaContact.getAvatarName());
    }

    @Test
    public void getAvatarName_hasNoLastName_returnsC(){
        NextivaContact nextivaContact = new NextivaContact("userId");
        nextivaContact.setFirstName("Jim");
        nextivaContact.setLastName(null);
        nextivaContact.setCompany("Company");

        assertEquals(Enums.AvatarDisplays.COMPANY, nextivaContact.getAvatarName());
    }

    @Test
    public void containsName_hasDisplayName_returnsTrue() {
        NextivaContact nextivaContact = new NextivaContact("userId");
        nextivaContact.setDisplayName("Display Name");
        nextivaContact.setFirstName("Jim");
        nextivaContact.setLastName("Smith");
        nextivaContact.setCompany("Company");
        nextivaContact.setJid("jim.smith@nextiva.im");

        assertTrue(nextivaContact.containsName("Display Name"));
    }

    @Test
    public void containsName_hasFirstAndLastName_returnsTrue() {
        NextivaContact nextivaContact = new NextivaContact("userId");
        nextivaContact.setDisplayName(null);
        nextivaContact.setFirstName("Jim");
        nextivaContact.setLastName("Smith");
        nextivaContact.setCompany("Company");
        nextivaContact.setJid("jim.smith@nextiva.im");

        assertTrue(nextivaContact.containsName("Jim Smith"));
    }

    @Test
    public void containsName_hasFirstName_returnsTrue() {
        NextivaContact nextivaContact = new NextivaContact("userId");
        nextivaContact.setDisplayName(null);
        nextivaContact.setFirstName("Jim");
        nextivaContact.setLastName(null);
        nextivaContact.setCompany("Company");
        nextivaContact.setJid("jim.smith@nextiva.im");

        assertTrue(nextivaContact.containsName("Jim"));
    }

    @Test
    public void containsName_hasLastName_returnsTrue() {
        NextivaContact nextivaContact = new NextivaContact("userId");
        nextivaContact.setDisplayName(null);
        nextivaContact.setFirstName(null);
        nextivaContact.setLastName("Smith");
        nextivaContact.setCompany("Company");
        nextivaContact.setJid("jim.smith@nextiva.im");

        assertTrue(nextivaContact.containsName("Smith"));
    }

    @Test
    public void containsName_hasCompany_returnsTrue() {
        NextivaContact nextivaContact = new NextivaContact("userId");
        nextivaContact.setDisplayName(null);
        nextivaContact.setFirstName(null);
        nextivaContact.setLastName(null);
        nextivaContact.setCompany("Company");
        nextivaContact.setJid("jim.smith@nextiva.im");

        assertTrue(nextivaContact.containsName("Company"));
    }

    @Test
    public void containsName_hasJid_returnsTrue() {
        NextivaContact nextivaContact = new NextivaContact("userId");
        nextivaContact.setDisplayName(null);
        nextivaContact.setFirstName(null);
        nextivaContact.setLastName(null);
        nextivaContact.setCompany(null);
        nextivaContact.setJid("jim.smith@nextiva.im");

        assertTrue(nextivaContact.containsName("jim.smith@nextiva.im"));
    }

    @Test
    public void containsName_hasNoNames_returnsFalse() {
        NextivaContact nextivaContact = new NextivaContact("userId");
        nextivaContact.setDisplayName(null);
        nextivaContact.setFirstName(null);
        nextivaContact.setLastName(null);
        nextivaContact.setCompany(null);
        nextivaContact.setJid(null);

        assertFalse(nextivaContact.containsName("Name"));
    }

    @Test
    public void containsPhoneNumber_hasNoPhoneNumber_returnsFalse() {
        NextivaContact nextivaContact = new NextivaContact("userId");

        assertFalse(nextivaContact.containsPhoneNumber("1231231234"));
    }

    @Test
    public void containsPhoneNumber_hasNoMatchingPhoneNumber_returnsFalse() {
        NextivaContact nextivaContact = new NextivaContact("userId");
        ArrayList<PhoneNumber> phoneNumbers = new ArrayList<>();
        phoneNumbers.add(new PhoneNumber(Enums.Contacts.PhoneTypes.PHONE, "1231231235", null));
        nextivaContact.setPhoneNumbers(phoneNumbers);

        assertFalse(nextivaContact.containsPhoneNumber("1231231234"));
    }

    @Test
    public void containsPhoneNumber_hasPhoneNumber_returnsTrue() {
        NextivaContact nextivaContact = new NextivaContact("userId");
        ArrayList<PhoneNumber> phoneNumbers = new ArrayList<>();
        phoneNumbers.add(new PhoneNumber(Enums.Contacts.PhoneTypes.PHONE, "1231231234", null));
        nextivaContact.setPhoneNumbers(phoneNumbers);

        assertTrue(nextivaContact.containsPhoneNumber("1231231234"));
    }

    @Test
    public void containsExtension_hasNoExtension_returnsFalse() {
        NextivaContact nextivaContact = new NextivaContact("userId");

        assertFalse(nextivaContact.containsExtension("1231"));
    }

    @Test
    public void containsExtension_hasNoMatchingExtension_returnsFalse() {
        NextivaContact nextivaContact = new NextivaContact("userId");
        ArrayList<PhoneNumber> extensions = new ArrayList<>();
        extensions.add(new PhoneNumber(Enums.Contacts.PhoneTypes.WORK_EXTENSION, "1232"));
        nextivaContact.setExtensions(extensions);

        assertFalse(nextivaContact.containsExtension("1231"));
    }

    @Test
    public void containsExtension_hasExtension_returnsTrue() {
        NextivaContact nextivaContact = new NextivaContact("userId");
        ArrayList<PhoneNumber> extensions = new ArrayList<>();
        extensions.add(new PhoneNumber(Enums.Contacts.PhoneTypes.WORK_EXTENSION, "1231"));
        nextivaContact.setExtensions(extensions);

        assertTrue(nextivaContact.containsExtension("1231"));
    }

    @Test
    public void containsConferenceNumber_hasNoNumbers_returnsFalse() {
        NextivaContact nextivaContact = new NextivaContact("userId");

        assertFalse(nextivaContact.containsConferenceNumber());
    }

    @Test
    public void containsConferenceNumber_hasNoMatchingNumbers_returnsFalse() {
        NextivaContact nextivaContact = new NextivaContact("userId");
        nextivaContact.setConferencePhoneNumbers(new ArrayList<PhoneNumber>() {{
            add(new PhoneNumber(Enums.Contacts.PhoneTypes.HOME_PHONE, "1234", null, null));
        }});

        assertFalse(nextivaContact.containsConferenceNumber());
    }

    @Test
    public void containsConferenceNumber_hasMatchingPhoneNumber_returnsTrue() {
        NextivaContact nextivaContact = new NextivaContact("userId");
        nextivaContact.setConferencePhoneNumbers(new ArrayList<PhoneNumber>() {{
            add(new PhoneNumber(Enums.Contacts.PhoneTypes.CONFERENCE_PHONE, "1234", null, null));
        }});

        assertTrue(nextivaContact.containsConferenceNumber());
    }

    @Test
    public void getSubscriptionState_hasValue_returnsCorrectValue() {
        NextivaContact nextivaContact = new NextivaContact("userId");
        nextivaContact.setSubscriptionState(Enums.Contacts.SubscriptionStates.SUBSCRIBED);

        assertEquals(Enums.Contacts.SubscriptionStates.SUBSCRIBED, nextivaContact.getSubscriptionState().intValue());
    }

    @Test
    public void getSubscriptionState_hasNoValue_returnsUnsubscribed() {
        NextivaContact nextivaContact = new NextivaContact("userId");

        assertEquals(Enums.Contacts.SubscriptionStates.UNSUBSCRIBED, nextivaContact.getSubscriptionState().intValue());
    }

    @Test
    public void equals_sameInstance_returnsTrue() {
        NextivaContact nextivaContact1 = new NextivaContact("userId");
        NextivaContact nextivaContact2 = nextivaContact1;

        assertEquals(nextivaContact1, nextivaContact2);
    }

    @Test
    public void equals_differentObjectType_returnsFalse() {
        NextivaContact nextivaContact1 = new NextivaContact("userId");

        assertNotEquals("", nextivaContact1);
    }

    @Test
    public void equals_differentInstances_returnsTrueWhenEqual() {
        DbPresence presence1 = new DbPresence("jid", Enums.Contacts.PresenceStates.AVAILABLE, 127, "", Enums.Contacts.PresenceTypes.NONE);
        DbPresence presence2 = new DbPresence("jid", Enums.Contacts.PresenceStates.AWAY, 0, "", Enums.Contacts.PresenceTypes.NONE);
        DbVCard vCard1 = new DbVCard("jid", new byte[] {2});
        DbVCard vCard2 = new DbVCard("jid2", new byte[] {3});

        ArrayList<PhoneNumber> phoneNumbersList1 = new ArrayList<>();
        phoneNumbersList1.add(new PhoneNumber(Enums.Contacts.PhoneTypes.WORK_PHONE, "1234", "", "", ""));

        ArrayList<PhoneNumber> phoneNumbersList2 = new ArrayList<>();
        phoneNumbersList2.add(new PhoneNumber(Enums.Contacts.PhoneTypes.HOME_PHONE, "9876", "", "", ""));
        phoneNumbersList1.add(new PhoneNumber(Enums.Contacts.PhoneTypes.WORK_EXTENSION, "1234", "", "", ""));
        phoneNumbersList2.add(new PhoneNumber(Enums.Contacts.PhoneTypes.WORK_EXTENSION, "9876", "", "", ""));

        ArrayList<EmailAddress> emailsList1 = new ArrayList<>();
        emailsList1.add(new EmailAddress(Enums.Contacts.EmailTypes.WORK_EMAIL, "fake@email.com", ""));

        ArrayList<EmailAddress> emailsList2 = new ArrayList<>();
        emailsList2.add(new EmailAddress(Enums.Contacts.EmailTypes.HOME_EMAIL, "email@fake.com", ""));

        NextivaContact nextivaContact1 = new NextivaContact("userId");
        nextivaContact1.setContactType(Enums.Contacts.ContactTypes.ENTERPRISE);
        nextivaContact1.setFirstName("Jim");
        nextivaContact1.setLastName("Smith");
        nextivaContact1.setDisplayName("Display Name");
        nextivaContact1.setCompany("Fake Company");
        nextivaContact1.setIsFavorite(true);
        nextivaContact1.setGroupId("Group 1");
        nextivaContact1.setServerUserId("Server ID");
        nextivaContact1.setPresence(presence1);
        nextivaContact1.setVCard(vCard1);
        nextivaContact1.setAllPhoneNumbers(phoneNumbersList1);
        nextivaContact1.setEmailAddresses(emailsList1);
        nextivaContact1.setJid("jim.smith@something.im");
        nextivaContact1.setSubscriptionState(Enums.Contacts.SubscriptionStates.SUBSCRIBED);

        NextivaContact nextivaContact2 = new NextivaContact("userId");
        nextivaContact2.setContactType(Enums.Contacts.ContactTypes.ENTERPRISE);
        nextivaContact2.setFirstName("Jim");
        nextivaContact2.setLastName("Smith");
        nextivaContact2.setDisplayName("Display Name");
        nextivaContact2.setCompany("Fake Company");
        nextivaContact2.setIsFavorite(true);
        nextivaContact2.setGroupId("Group 1");
        nextivaContact2.setServerUserId("Server ID");
        nextivaContact2.setPresence(presence1);
        nextivaContact2.setVCard(vCard1);
        nextivaContact2.setAllPhoneNumbers(phoneNumbersList1);
        nextivaContact2.setEmailAddresses(emailsList1);
        nextivaContact2.setJid("jim.smith@something.im");
        nextivaContact2.setSubscriptionState(Enums.Contacts.SubscriptionStates.SUBSCRIBED);

        assertEquals(nextivaContact1, nextivaContact2);

        nextivaContact2.setUserId("differentUserId");
        assertNotEquals(nextivaContact1, nextivaContact2);
        nextivaContact2.setUserId("userId");
        assertEquals(nextivaContact1, nextivaContact2);

        nextivaContact1.setUserId("");
        nextivaContact2.setUserId(null);
        assertEquals(nextivaContact1, nextivaContact2);
        nextivaContact1.setUserId("userId");
        nextivaContact2.setUserId("userId");
        assertEquals(nextivaContact1, nextivaContact2);

        nextivaContact2.setContactType(Enums.Contacts.ContactTypes.LOCAL);
        assertNotEquals(nextivaContact1, nextivaContact2);
        nextivaContact2.setContactType(Enums.Contacts.ContactTypes.ENTERPRISE);
        assertEquals(nextivaContact1, nextivaContact2);

        nextivaContact2.setFirstName("Keith");
        assertNotEquals(nextivaContact1, nextivaContact2);
        nextivaContact2.setFirstName("Jim");
        assertEquals(nextivaContact1, nextivaContact2);

        nextivaContact1.setFirstName("");
        nextivaContact2.setFirstName(null);
        assertEquals(nextivaContact1, nextivaContact2);
        nextivaContact1.setFirstName("Jim");
        nextivaContact2.setFirstName("Jim");
        assertEquals(nextivaContact1, nextivaContact2);

        nextivaContact2.setLastName("Jones");
        assertNotEquals(nextivaContact1, nextivaContact2);
        nextivaContact2.setLastName("Smith");
        assertEquals(nextivaContact1, nextivaContact2);

        nextivaContact1.setLastName("");
        nextivaContact2.setLastName(null);
        assertEquals(nextivaContact1, nextivaContact2);
        nextivaContact1.setLastName("Smith");
        nextivaContact2.setLastName("Smith");
        assertEquals(nextivaContact1, nextivaContact2);

        nextivaContact2.setDisplayName("Different Name");
        assertNotEquals(nextivaContact1, nextivaContact2);
        nextivaContact2.setDisplayName("Display Name");
        assertEquals(nextivaContact1, nextivaContact2);

        nextivaContact1.setDisplayName("");
        nextivaContact2.setDisplayName(null);
        assertEquals(nextivaContact1, nextivaContact2);
        nextivaContact1.setDisplayName("Display Name");
        nextivaContact2.setDisplayName("Display Name");
        assertEquals(nextivaContact1, nextivaContact2);

        nextivaContact2.setCompany("Real Company");
        assertNotEquals(nextivaContact1, nextivaContact2);
        nextivaContact2.setCompany("Fake Company");
        assertEquals(nextivaContact1, nextivaContact2);

        nextivaContact1.setCompany("");
        nextivaContact2.setCompany(null);
        assertEquals(nextivaContact1, nextivaContact2);
        nextivaContact1.setCompany("Fake Company");
        nextivaContact2.setCompany("Fake Company");
        assertEquals(nextivaContact1, nextivaContact2);

        nextivaContact2.setIsFavorite(false);
        assertNotEquals(nextivaContact1, nextivaContact2);
        nextivaContact2.setIsFavorite(true);
        assertEquals(nextivaContact1, nextivaContact2);

        nextivaContact2.setGroupId("Group 2");
        assertNotEquals(nextivaContact1, nextivaContact2);
        nextivaContact2.setGroupId("Group 1");
        assertEquals(nextivaContact1, nextivaContact2);

        nextivaContact1.setGroupId("");
        nextivaContact2.setGroupId(null);
        assertEquals(nextivaContact1, nextivaContact2);
        nextivaContact1.setGroupId("Group 1");
        nextivaContact2.setGroupId("Group 1");
        assertEquals(nextivaContact1, nextivaContact2);

        nextivaContact2.setServerUserId("Different Server ID");
        assertNotEquals(nextivaContact1, nextivaContact2);
        nextivaContact2.setServerUserId("Server ID");
        assertEquals(nextivaContact1, nextivaContact2);

        nextivaContact1.setServerUserId("");
        nextivaContact2.setServerUserId(null);
        assertEquals(nextivaContact1, nextivaContact2);
        nextivaContact1.setServerUserId("Server ID");
        nextivaContact2.setServerUserId("Server ID");
        assertEquals(nextivaContact1, nextivaContact2);

        nextivaContact2.setPresence(presence2);
        assertNotEquals(nextivaContact1, nextivaContact2);
        nextivaContact2.setPresence(presence1);
        assertEquals(nextivaContact1, nextivaContact2);

        nextivaContact2.setVCard(vCard2);
        assertNotEquals(nextivaContact1, nextivaContact2);
        nextivaContact2.setVCard(vCard1);
        assertEquals(nextivaContact1, nextivaContact2);

        nextivaContact2.setAllPhoneNumbers(phoneNumbersList2);
        assertNotEquals(nextivaContact1, nextivaContact2);
        nextivaContact2.setAllPhoneNumbers(phoneNumbersList1);
        assertEquals(nextivaContact1, nextivaContact2);

        nextivaContact2.setEmailAddresses(emailsList2);
        assertNotEquals(nextivaContact1, nextivaContact2);
        nextivaContact2.setEmailAddresses(emailsList1);
        assertEquals(nextivaContact1, nextivaContact2);

        nextivaContact2.setJid("qwery@poiuyt.im");
        assertNotEquals(nextivaContact1, nextivaContact2);
        nextivaContact2.setJid("jim.smith@something.im");
        assertEquals(nextivaContact1, nextivaContact2);

        nextivaContact1.setJid("");
        nextivaContact2.setJid(null);
        assertEquals(nextivaContact1, nextivaContact2);
        nextivaContact1.setJid("jim.smith@something.im");
        nextivaContact2.setJid("jim.smith@something.im");
        assertEquals(nextivaContact1, nextivaContact2);

        nextivaContact2.setSubscriptionState(Enums.Contacts.SubscriptionStates.UNSUBSCRIBED);
        assertNotEquals(nextivaContact1, nextivaContact2);
        nextivaContact2.setSubscriptionState(Enums.Contacts.SubscriptionStates.SUBSCRIBED);
        assertEquals(nextivaContact1, nextivaContact2);

        nextivaContact1.setPresence(null);
        nextivaContact1.setVCard(null);
        nextivaContact1.setAllPhoneNumbers(null);
        nextivaContact1.setExtensions(null);
        nextivaContact1.setEmailAddresses(null);

        nextivaContact2.setPresence(null);
        nextivaContact2.setVCard(null);
        nextivaContact2.setAllPhoneNumbers(null);
        nextivaContact2.setExtensions(null);
        nextivaContact2.setEmailAddresses(null);

        assertEquals(nextivaContact1, nextivaContact2);

        nextivaContact2.setPresence(presence2);
        assertNotEquals(nextivaContact1, nextivaContact2);
        nextivaContact2.setPresence(null);
        assertEquals(nextivaContact1, nextivaContact2);

        nextivaContact2.setVCard(vCard2);
        assertNotEquals(nextivaContact1, nextivaContact2);
        nextivaContact2.setVCard(null);
        assertEquals(nextivaContact1, nextivaContact2);

        nextivaContact2.setAllPhoneNumbers(phoneNumbersList2);
        assertNotEquals(nextivaContact1, nextivaContact2);
        nextivaContact2.setAllPhoneNumbers(null);
        assertEquals(nextivaContact1, nextivaContact2);

        nextivaContact2.setEmailAddresses(emailsList2);
        assertNotEquals(nextivaContact1, nextivaContact2);
        nextivaContact2.setEmailAddresses(null);
        assertEquals(nextivaContact1, nextivaContact2);
    }

    @Test
    public void copy_contactPassedIn_createdCopiedContact() {
        DbPresence presence1 = new DbPresence("jid", Enums.Contacts.PresenceStates.AVAILABLE, 127, null, Enums.Contacts.PresenceTypes.NONE);
        DbVCard vCard1 = new DbVCard("jid", "HELLO".getBytes());

        ArrayList<Address> addressList1 = new ArrayList<>();
        addressList1.add(new Address("Address Line One", "Address Line Two", "Postal Code", "City", "Region", "Country", "Location", null, null));

        ArrayList<PhoneNumber> phoneNumbersList1 = new ArrayList<>();
        phoneNumbersList1.add(new PhoneNumber(Enums.Contacts.PhoneTypes.WORK_PHONE, "1234", null));
        phoneNumbersList1.add(new PhoneNumber(Enums.Contacts.PhoneTypes.WORK_EXTENSION, "1234"));

        ArrayList<EmailAddress> emailsList1 = new ArrayList<>();
        emailsList1.add(new EmailAddress(Enums.Contacts.EmailTypes.WORK_EMAIL, "fake@email.com", null));

        NextivaContact nextivaContact1 = new NextivaContact("userId");
        nextivaContact1.setContactType(Enums.Contacts.ContactTypes.ENTERPRISE);
        nextivaContact1.setFirstName("Jim");
        nextivaContact1.setLastName("Smith");
        nextivaContact1.setHiraganaFirstName("Jim");
        nextivaContact1.setHiraganaLastName("Smith");
        nextivaContact1.setDisplayName("Display Name");
        nextivaContact1.setCompany("Fake Company");
        nextivaContact1.setIsFavorite(true);
        nextivaContact1.setGroupId("Group 1");
        nextivaContact1.setServerUserId("Server ID");
        nextivaContact1.setPresence(presence1);
        nextivaContact1.setVCard(vCard1);
        nextivaContact1.setAllPhoneNumbers(phoneNumbersList1);
        nextivaContact1.setEmailAddresses(emailsList1);
        nextivaContact1.setJid("jim.smith@something.im");
        nextivaContact1.setSubscriptionState(Enums.Contacts.SubscriptionStates.SUBSCRIBED);
        nextivaContact1.setAddresses(addressList1);

        NextivaContact copiedContact = new NextivaContact(nextivaContact1);

        assertEquals("userId", copiedContact.getUserId());
        assertNotSame(nextivaContact1.getUserId(), copiedContact.getUserId());
        assertEquals(Enums.Contacts.ContactTypes.ENTERPRISE, copiedContact.getContactType().intValue());
        assertEquals("Jim", copiedContact.getFirstName());
        assertNotSame(nextivaContact1.getFirstName(), copiedContact.getFirstName());
        assertEquals("Smith", copiedContact.getLastName());
        assertNotSame(nextivaContact1.getLastName(), copiedContact.getLastName());
        assertEquals("Jim", copiedContact.getHiraganaFirstName());
        assertNotSame(nextivaContact1.getHiraganaFirstName(), copiedContact.getHiraganaFirstName());
        assertEquals("Smith", copiedContact.getHiraganaLastName());
        assertNotSame(nextivaContact1.getHiraganaLastName(), copiedContact.getHiraganaLastName());
        assertEquals("Display Name", copiedContact.getDisplayName());
        assertNotSame(nextivaContact1.getDisplayName(), copiedContact.getDisplayName());
        assertEquals("Fake Company", copiedContact.getCompany());
        assertNotSame(nextivaContact1.getCompany(), copiedContact.getCompany());
        assertTrue(copiedContact.getIsFavoriteRaw());
        assertNotSame(nextivaContact1.getIsFavoriteRaw(), copiedContact.getIsFavoriteRaw());
        assertEquals("Group 1", copiedContact.getGroupId());
        assertNotSame(nextivaContact1.getGroupId(), copiedContact.getGroupId());
        assertEquals("Server ID", copiedContact.getServerUserId());
        assertNotSame(nextivaContact1.getServerUserId(), copiedContact.getServerUserId());
        assertEquals(presence1, copiedContact.getPresence());
        assertNotSame(nextivaContact1.getPresence(), copiedContact.getPresence());

        assertEquals("Address Line One", copiedContact.getAddresses().get(0).getAddressLineOne());
        assertEquals("Address Line Two", copiedContact.getAddresses().get(0).getAddressLineTwo());
        assertEquals("Postal Code", copiedContact.getAddresses().get(0).getPostalCode());
        assertEquals("City", copiedContact.getAddresses().get(0).getCity());
        assertEquals("Region", copiedContact.getAddresses().get(0).getRegion());
        assertEquals("Country", copiedContact.getAddresses().get(0).getCountry());
        assertEquals("Location", copiedContact.getAddresses().get(0).getLocation());
        assertNotSame(nextivaContact1.getAddresses().get(0), copiedContact.getAddresses().get(0));

        assertEquals("jim.smith@something.im", copiedContact.getVCard().getJid());
        assertTrue(equals("HELLO".getBytes(), copiedContact.getVCard().getPhotoData()));
        assertNotSame(nextivaContact1.getVCard(), copiedContact.getVCard());

        for (int i = 0; i < phoneNumbersList1.size(); i++) {
            assertEquals(phoneNumbersList1.get(i).getNumber(), copiedContact.getAllPhoneNumbers().get(i).getNumber());
            assertEquals(phoneNumbersList1.get(i).getType(), copiedContact.getAllPhoneNumbers().get(i).getType());
        }
        assertNotSame(nextivaContact1.getAllPhoneNumbers(), copiedContact.getAllPhoneNumbers());

        for (int i = 0; i < emailsList1.size(); i++) {
            assertEquals(emailsList1.get(i).getAddress(), copiedContact.getEmailAddresses().get(i).getAddress());
            assertEquals(emailsList1.get(i).getType(), copiedContact.getEmailAddresses().get(i).getType());
        }
        assertNotSame(nextivaContact1.getEmailAddresses(), copiedContact.getEmailAddresses());

        assertEquals("jim.smith@something.im", copiedContact.getJid());
        assertNotSame(nextivaContact1.getJid(), copiedContact.getJid());
        assertEquals(Enums.Contacts.SubscriptionStates.SUBSCRIBED, copiedContact.getSubscriptionState().intValue());
    }

    @Test
    public void getAvatarInfo_conferenceType_returnsCorrectAvatarInfo() {
        NextivaContact nextivaContact = new NextivaContact("1");
        nextivaContact.setContactType(Enums.Contacts.ContactTypes.CONFERENCE);

        AvatarInfo avatarInfo = nextivaContact.getAvatarInfo();

        assertEquals(R.drawable.ic_phone, avatarInfo.getIconResId());
    }

    @Test
    public void getAvatarInfo_hasVCard_returnsCorrectAvatarInfo() {
        byte[] avatarBytes = new byte[] {3};

        NextivaContact nextivaContact = new NextivaContact("1");
        nextivaContact.setVCard(new DbVCard("jid", avatarBytes));

        AvatarInfo avatarInfo = nextivaContact.getAvatarInfo();

        assertEquals(avatarBytes, avatarInfo.getPhotoData());
    }

    @Test
    public void getAvatarInfo_hasUiName_returnsCorrectAvatarInfo() {
        NextivaContact nextivaContact = new NextivaContact("1");

        assertNull(nextivaContact.getAvatarInfo().getDisplayName());

        nextivaContact.setFirstName("Bob");
        assertEquals("Bob", nextivaContact.getAvatarInfo().getDisplayName());
        nextivaContact.setFirstName(null);

        nextivaContact.setLastName("Jones");
        assertEquals("Jones", nextivaContact.getAvatarInfo().getDisplayName());

        nextivaContact.setFirstName("Bob");
        assertEquals("Bob Jones", nextivaContact.getAvatarInfo().getDisplayName());

        nextivaContact.setDisplayName("My Display");
        assertEquals("My Display", nextivaContact.getAvatarInfo().getDisplayName());
    }

    @Test
    public void getSearchTextMatch_fullNameMatch_returnsCorrectValue() {
        NextivaContact nextivaContact = new NextivaContact("1");
        String searchTerm = "First L";
        nextivaContact.setFirstName("First");
        nextivaContact.setLastName("Last");

        assertEquals("First Last", nextivaContact.getSearchMatchText(searchTerm.toLowerCase()));
    }

    @Test
    public void getSearchTextMatch_lastNameMatch_returnsCorrectValue() {
        NextivaContact nextivaContact = new NextivaContact("1");
        String searchTerm = "Las";
        nextivaContact.setLastName("Last");

        assertEquals("Last", nextivaContact.getSearchMatchText(searchTerm.toLowerCase()));
    }

    @Test
    public void getSearchTextMatch_firstNameMatch_returnsCorrectValue() {
        NextivaContact nextivaContact = new NextivaContact("1");
        String searchTerm = "Fir";
        nextivaContact.setFirstName("First");

        assertEquals("First", nextivaContact.getSearchMatchText(searchTerm.toLowerCase()));
    }

    @Test
    public void getSearchTextMatch_companyMatch_returnsCorrectValue() {
        NextivaContact nextivaContact = new NextivaContact("1");
        String searchTerm = "Compan";
        nextivaContact.setCompany("Company");

        assertEquals("Company", nextivaContact.getSearchMatchText(searchTerm.toLowerCase()));
    }

    @Test
    public void getSearchTextMatch_jidMatch_returnsCorrectValue() {
        NextivaContact nextivaContact = new NextivaContact("1");
        String searchTerm = "jid@jid";
        nextivaContact.setJid("jid@jid.im");

        assertEquals("jid@jid.im", nextivaContact.getSearchMatchText(searchTerm.toLowerCase()));
    }

    @Test
    public void getSearchTextMatch_phoneNumberMatch_returnsCorrectValue() {
        NextivaContact nextivaContact = new NextivaContact("1");
        ArrayList<PhoneNumber> phoneNumbers = new ArrayList<>();
        phoneNumbers.add(new PhoneNumber(Enums.Contacts.PhoneTypes.WORK_PHONE, "1112223333", null));
        nextivaContact.setPhoneNumbers(phoneNumbers);

        PowerMockito.when(PhoneNumberUtils.formatNumber(eq("1112223333"), any())).thenAnswer((Answer<String>) invocation -> "(111) 222-3333");

        String searchTerm = "1222333";

        assertEquals("(111) 222-3333", nextivaContact.getSearchMatchText(searchTerm.toLowerCase()));
    }

    @Test
    public void getSearchTextMatch_extensionMatch_returnsCorrectValue() {
        NextivaContact nextivaContact = new NextivaContact("1");
        ArrayList<PhoneNumber> extensions = new ArrayList<>();
        extensions.add(new PhoneNumber(Enums.Contacts.PhoneTypes.WORK_EXTENSION, "1111"));
        nextivaContact.setExtensions(extensions);

        String searchTerm = "111";

        assertEquals("1111", nextivaContact.getSearchMatchText(searchTerm.toLowerCase()));
    }

    @Test
    public void getSearchTextMatch_conferenceNumberValueMatch_returnsCorrectValue() {
        NextivaContact nextivaContact = new NextivaContact("1");
        ArrayList<PhoneNumber> conferenceNumbers = new ArrayList<>();
        conferenceNumbers.add(new PhoneNumber(Enums.Contacts.PhoneTypes.CONFERENCE_PHONE, "1112223333", "1234", "5678"));
        nextivaContact.setConferencePhoneNumbers(conferenceNumbers);

        String searchTerm = "1222";

        assertEquals("1112223333,1234#,5678#", nextivaContact.getSearchMatchText(searchTerm.toLowerCase()));
    }

    @Test
    public void getSearchTextMatch_conferenceNumberPinOneMatch_returnsCorrectValue() {
        NextivaContact nextivaContact = new NextivaContact("1");
        ArrayList<PhoneNumber> conferenceNumbers = new ArrayList<>();
        conferenceNumbers.add(new PhoneNumber(Enums.Contacts.PhoneTypes.CONFERENCE_PHONE, "1112223333", "1234", "5678"));
        nextivaContact.setConferencePhoneNumbers(conferenceNumbers);

        String searchTerm = "123";

        assertEquals("1112223333,1234#,5678#", nextivaContact.getSearchMatchText(searchTerm.toLowerCase()));
    }

    @Test
    public void getSearchTextMatch_conferenceNumberPinTwoMatch_returnsCorrectValue() {
        NextivaContact nextivaContact = new NextivaContact("1");
        ArrayList<PhoneNumber> conferenceNumbers = new ArrayList<>();
        conferenceNumbers.add(new PhoneNumber(Enums.Contacts.PhoneTypes.CONFERENCE_PHONE, "1112223333", "1234", "5678"));
        nextivaContact.setConferencePhoneNumbers(conferenceNumbers);

        String searchTerm = "567";

        assertEquals("1112223333,1234#,5678#", nextivaContact.getSearchMatchText(searchTerm.toLowerCase()));
    }

    @Test
    public void getSearchTextMatch_formattedPhoneNumberMatch_returnsCorrectValue() {
        MockedStatic<CallUtil> mocked = Mockito.mockStatic(CallUtil.class);
        mocked.when(() -> CallUtil.getStrippedPhoneNumber("(111) 222-3333")).thenAnswer(
                invocation -> "1112223333");
        NextivaContact nextivaContact = new NextivaContact("1");
        ArrayList<PhoneNumber> phoneNumbers = new ArrayList<>();
        phoneNumbers.add(new PhoneNumber(Enums.Contacts.PhoneTypes.WORK_PHONE, "(111) 222-3333", null));
        nextivaContact.setPhoneNumbers(phoneNumbers);

        PowerMockito.when(PhoneNumberUtils.formatNumber(eq("1112223333"), any())).thenAnswer((Answer<String>) invocation -> "(111) 222-3333");

        String searchTerm = "1222333";

        assertEquals("(111) 222-3333", nextivaContact.getSearchMatchText(searchTerm.toLowerCase()));
        mocked.close();
    }

    @Test
    public void getSearchTextMatch_formattedConferencePhoneNumberValueMatch_returnsCorrectValue() {
        MockedStatic<CallUtil> mocked = Mockito.mockStatic(CallUtil.class);
        mocked.when(() -> CallUtil.getStrippedPhoneNumber("(111) 222-3333,1234#,4321#")).thenAnswer(
                invocation -> "1112223333,1234#,4321#");
        NextivaContact nextivaContact = new NextivaContact("1");
        ArrayList<PhoneNumber> conferenceNumbers = new ArrayList<>();
        conferenceNumbers.add(new PhoneNumber(Enums.Contacts.PhoneTypes.CONFERENCE_PHONE, "(111) 222-3333", "1234", "4321"));
        nextivaContact.setConferencePhoneNumbers(conferenceNumbers);

        String searchTerm = "1222333";

        assertEquals("(111) 222-3333,1234#,4321#", nextivaContact.getSearchMatchText(searchTerm.toLowerCase()));
        mocked.close();
    }

    @Test
    public void getSearchTextMatch_emailMatch_returnsCorrectValue() {
        NextivaContact nextivaContact = new NextivaContact("1");
        ArrayList<EmailAddress> emailAddresses = new ArrayList<>();
        emailAddresses.add(new EmailAddress(Enums.Contacts.EmailTypes.HOME_EMAIL, "email@email.com", null));
        nextivaContact.setEmailAddresses(emailAddresses);

        String searchTerm = "email@em";

        assertEquals("email@email.com", nextivaContact.getSearchMatchText(searchTerm.toLowerCase()));
    }

    @Test
    public void getSearchTextMatch_noMatch_returnsNull() {
        NextivaContact nextivaContact = new NextivaContact("1");
        String searchTerm = "First L";

        assertNull(nextivaContact.getSearchMatchText(searchTerm.toLowerCase()));
    }

    @Test
    public void getAllPhoneNumbersSorted_fullContact_returnsCorrectSort() {
        final NextivaContact nextivaContact = ContactLists.INSTANCE.getNextivaContactTestList().get(2);

        List<PhoneNumber> sortedPhoneNumbersList = nextivaContact.getAllPhoneNumbersSorted();

        assertEquals(Enums.Contacts.PhoneTypes.WORK_PHONE, sortedPhoneNumbersList.get(0).getType());
        assertEquals("1112221113", sortedPhoneNumbersList.get(0).getNumber());
        assertNull(sortedPhoneNumbersList.get(0).getLabel());

        assertEquals(Enums.Contacts.PhoneTypes.WORK_EXTENSION, sortedPhoneNumbersList.get(1).getType());
        assertEquals("1113", sortedPhoneNumbersList.get(1).getNumber());

        assertEquals(Enums.Contacts.PhoneTypes.WORK_EXTENSION, sortedPhoneNumbersList.get(2).getType());
        assertEquals("1114", sortedPhoneNumbersList.get(2).getNumber());

        assertEquals(Enums.Contacts.PhoneTypes.WORK_MOBILE_PHONE, sortedPhoneNumbersList.get(3).getType());
        assertEquals("6667778888", sortedPhoneNumbersList.get(3).getNumber());
        assertNull(sortedPhoneNumbersList.get(3).getLabel());

        assertEquals(Enums.Contacts.PhoneTypes.WORK_PAGER, sortedPhoneNumbersList.get(4).getType());
        assertEquals("7778889999", sortedPhoneNumbersList.get(4).getNumber());
        assertNull(sortedPhoneNumbersList.get(4).getLabel());

        assertEquals(Enums.Contacts.PhoneTypes.WORK_FAX, sortedPhoneNumbersList.get(5).getType());
        assertEquals("8889990000", sortedPhoneNumbersList.get(5).getNumber());
        assertNull(sortedPhoneNumbersList.get(5).getLabel());

        assertEquals(Enums.Contacts.PhoneTypes.HOME_PHONE, sortedPhoneNumbersList.get(6).getType());
        assertEquals("3334445555", sortedPhoneNumbersList.get(6).getNumber());
        assertNull(sortedPhoneNumbersList.get(6).getLabel());

        assertEquals(Enums.Contacts.PhoneTypes.MOBILE_PHONE, sortedPhoneNumbersList.get(7).getType());
        assertEquals("2223334444", sortedPhoneNumbersList.get(7).getNumber());
        assertNull(sortedPhoneNumbersList.get(7).getLabel());

        assertEquals(Enums.Contacts.PhoneTypes.PAGER, sortedPhoneNumbersList.get(8).getType());
        assertEquals("4443332222", sortedPhoneNumbersList.get(8).getNumber());
        assertNull(sortedPhoneNumbersList.get(8).getLabel());

        assertEquals(Enums.Contacts.PhoneTypes.HOME_FAX, sortedPhoneNumbersList.get(9).getType());
        assertEquals("3332221111", sortedPhoneNumbersList.get(9).getNumber());
        assertNull(sortedPhoneNumbersList.get(9).getLabel());

        assertEquals(Enums.Contacts.PhoneTypes.MAIN_PHONE, sortedPhoneNumbersList.get(10).getType());
        assertEquals("2221110000", sortedPhoneNumbersList.get(10).getNumber());
        assertNull(sortedPhoneNumbersList.get(10).getLabel());

        assertEquals(Enums.Contacts.PhoneTypes.CONFERENCE_PHONE, sortedPhoneNumbersList.get(11).getType());
        assertEquals("1233334444", sortedPhoneNumbersList.get(11).getNumber());
        assertEquals("1233334444,123456#,654321#", sortedPhoneNumbersList.get(11).getAssembledPhoneNumber());

        assertEquals(Enums.Contacts.PhoneTypes.CUSTOM_PHONE, sortedPhoneNumbersList.get(12).getType());
        assertEquals("4445556666", sortedPhoneNumbersList.get(12).getNumber());
        assertEquals("My Phone", sortedPhoneNumbersList.get(12).getLabel());

        assertEquals(Enums.Contacts.PhoneTypes.OTHER_PHONE, sortedPhoneNumbersList.get(13).getType());
        assertEquals("5556667777", sortedPhoneNumbersList.get(13).getNumber());
        assertNull(sortedPhoneNumbersList.get(13).getLabel());
    }

    public boolean equals(byte[] a, byte[] a2) {
        if (a == a2) {
            return true;
        }
        if (a == null || a2 == null) {
            return false;
        }

        int length = a.length;
        if (a2.length != length) {
            return false;
        }

        for (int i = 0; i < length; i++) {
            if (a[i] != a2[i]) {
                return false;
            }
        }

        return true;
    }
}
