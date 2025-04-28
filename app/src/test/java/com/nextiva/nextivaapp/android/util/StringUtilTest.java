/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.util;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import androidx.test.core.app.ApplicationProvider;

import com.nextiva.nextivaapp.android.BaseRobolectricTest;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.db.model.EmailAddress;
import com.nextiva.nextivaapp.android.db.model.PhoneNumber;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

/**
 * Created by adammacdonald on 3/27/18.
 */

@RunWith(RobolectricTestRunner.class)
public class StringUtilTest extends BaseRobolectricTest {

    @Test
    public void getPhoneNumberType_homeTypeIsRosterContact_returnsCorrectValue() {
        assertEquals("Personal Phone", StringUtil.getPhoneNumberTypeLabel(ApplicationProvider.getApplicationContext(), new PhoneNumber(Enums.Contacts.PhoneTypes.HOME_PHONE, "111222333", null), true, false));
    }

    @Test
    public void getPhoneNumberType_homeTypeNotRosterContact_returnsCorrectValue() {
        assertEquals("Home Phone", StringUtil.getPhoneNumberTypeLabel(ApplicationProvider.getApplicationContext(), new PhoneNumber(Enums.Contacts.PhoneTypes.HOME_PHONE, "111222333", null), false, false));
    }

    @Test
    public void getPhoneNumberType_mobileType_returnsCorrectValue() {
        assertEquals("Mobile Phone", StringUtil.getPhoneNumberTypeLabel(ApplicationProvider.getApplicationContext(), new PhoneNumber(Enums.Contacts.PhoneTypes.MOBILE_PHONE, "111222333", null), false, false));
    }

    @Test
    public void getPhoneNumberType_pagerType_returnsCorrectValue() {
        assertEquals("Pager", StringUtil.getPhoneNumberTypeLabel(ApplicationProvider.getApplicationContext(), new PhoneNumber(Enums.Contacts.PhoneTypes.PAGER, "111222333", null), false, false));
    }

    @Test
    public void getPhoneNumberType_homeFaxType_returnsCorrectValue() {
        assertEquals("Home Fax", StringUtil.getPhoneNumberTypeLabel(ApplicationProvider.getApplicationContext(), new PhoneNumber(Enums.Contacts.PhoneTypes.HOME_FAX, "111222333", null), false, false));
    }

    @Test
    public void getPhoneNumberType_workType_returnsCorrectValue() {
        assertEquals("Work Phone", StringUtil.getPhoneNumberTypeLabel(ApplicationProvider.getApplicationContext(), new PhoneNumber(Enums.Contacts.PhoneTypes.WORK_PHONE, "111222333", null), false, false));
    }

    @Test
    public void getPhoneNumberType_workMobileType_returnsCorrectValue() {
        assertEquals("Work Mobile Phone", StringUtil.getPhoneNumberTypeLabel(ApplicationProvider.getApplicationContext(), new PhoneNumber(Enums.Contacts.PhoneTypes.WORK_MOBILE_PHONE, "111222333", null), false, false));
    }

    @Test
    public void getPhoneNumberType_workPagerType_returnsCorrectValue() {
        assertEquals("Work Pager", StringUtil.getPhoneNumberTypeLabel(ApplicationProvider.getApplicationContext(), new PhoneNumber(Enums.Contacts.PhoneTypes.WORK_PAGER, "111222333", null), false, false));
    }

    @Test
    public void getPhoneNumberType_workFaxType_returnsCorrectValue() {
        assertEquals("Work Fax", StringUtil.getPhoneNumberTypeLabel(ApplicationProvider.getApplicationContext(), new PhoneNumber(Enums.Contacts.PhoneTypes.WORK_FAX, "111222333", null), false, false));
    }

    @Test
    public void getPhoneNumberType_otherType_returnsCorrectValue() {
        assertEquals("Other Phone", StringUtil.getPhoneNumberTypeLabel(ApplicationProvider.getApplicationContext(), new PhoneNumber(Enums.Contacts.PhoneTypes.OTHER_PHONE, "111222333", null), false, false));
    }

    @Test
    public void getPhoneNumberType_customTypeWithLabel_returnsCorrectValue() {
        assertEquals("Custom label", StringUtil.getPhoneNumberTypeLabel(ApplicationProvider.getApplicationContext(), new PhoneNumber(Enums.Contacts.PhoneTypes.CUSTOM_PHONE, "111222333", "Custom label"), false, false));
    }

    @Test
    public void getPhoneNumberType_customTypeNoLabel_returnsCorrectValue() {
        assertEquals("Custom Phone", StringUtil.getPhoneNumberTypeLabel(ApplicationProvider.getApplicationContext(), new PhoneNumber(Enums.Contacts.PhoneTypes.CUSTOM_PHONE, "111222333", null), false, false));
    }

    @Test
    public void getEmailType_emptyType_returnsCorrectValue() {
        assertEquals("Email", StringUtil.getEmailLabel(ApplicationProvider.getApplicationContext(), new EmailAddress(Enums.Contacts.EmailTypes.EMAIL, "address@address.com", null)));
    }

    @Test
    public void getEmailType_homeType_returnsCorrectValue() {
        assertEquals("Home Email", StringUtil.getEmailLabel(ApplicationProvider.getApplicationContext(), new EmailAddress(Enums.Contacts.EmailTypes.HOME_EMAIL, "address@address.com", null)));
    }

    @Test
    public void getEmailType_mobileType_returnsCorrectValue() {
        assertEquals("Mobile Email", StringUtil.getEmailLabel(ApplicationProvider.getApplicationContext(), new EmailAddress(Enums.Contacts.EmailTypes.MOBILE_EMAIL, "address@address.com", null)));
    }

    @Test
    public void getEmailType_workType_returnsCorrectValue() {
        assertEquals("Email Address", StringUtil.getEmailLabel(ApplicationProvider.getApplicationContext(), new EmailAddress(Enums.Contacts.EmailTypes.WORK_EMAIL, "address@address.com", null)));
    }

    @Test
    public void getEmailType_otherType_returnsCorrectValue() {
        assertEquals("Other Email", StringUtil.getEmailLabel(ApplicationProvider.getApplicationContext(), new EmailAddress(Enums.Contacts.EmailTypes.OTHER_EMAIL, "address@address.com", null)));
    }

    @Test
    public void getEmailType_customType_returnsCorrectValue() {
        assertEquals("Custom Email", StringUtil.getEmailLabel(ApplicationProvider.getApplicationContext(), new EmailAddress(Enums.Contacts.EmailTypes.CUSTOM_EMAIL, "address@address.com", null)));
    }

    @Test
    public void getEmailType_unknownType_returnsCorrectValue() {
        assertEquals("Custom label", StringUtil.getEmailLabel(ApplicationProvider.getApplicationContext(), new EmailAddress(Enums.Contacts.EmailTypes.CUSTOM_EMAIL, "address@address.com", "Custom label")));
    }

    @Test
    public void changesMade_sameValues_returnsFalse() {
        assertFalse(StringUtil.changesMade("same", "same"));
    }

    @Test
    public void changesMade_nullValues_returnsFalse() {
        assertFalse(StringUtil.changesMade(null, null));
    }

    @Test
    public void changesMade_blankValues_returnsFalse() {
        assertFalse(StringUtil.changesMade("", ""));
    }

    @Test
    public void changesMade_blankAndNullValues_returnsFalse() {
        assertFalse(StringUtil.changesMade("", null));
    }

    @Test
    public void changesMade_nullAndBlankValues_returnsFalse() {
        assertFalse(StringUtil.changesMade(null, ""));
    }

    @Test
    public void changesMade_nullFirstValue_returnsTrue() {
        assertTrue(StringUtil.changesMade(null, "second"));
    }

    @Test
    public void changesMade_blankFirstValue_returnsTrue() {
        assertTrue(StringUtil.changesMade("", "second"));
    }

    @Test
    public void changesMade_nullSecondValue_returnsTrue() {
        assertTrue(StringUtil.changesMade("first", null));
    }

    @Test
    public void changesMade_blankSecondValue_returnsTrue() {
        assertTrue(StringUtil.changesMade("first", ""));
    }

    @Test
    public void changesMade_differentValues_returnsTrue() {
        assertTrue(StringUtil.changesMade("first", "second"));
    }

    @Test
    public void changesMade_differentCasedValues_returnsTrue() {
        assertTrue(StringUtil.changesMade("same", "Same"));
    }

    @Test
    public void equalsWithNullsAndBlanks_sameValues_returnsTrue() {
        assertTrue(StringUtil.equalsWithNullsAndBlanks("same", "same"));
    }

    @Test
    public void equalsWithNullsAndBlanks_nullValues_returnsTrue() {
        assertTrue(StringUtil.equalsWithNullsAndBlanks(null, null));
    }

    @Test
    public void equalsWithNullsAndBlanks_blankValues_returnsTrue() {
        assertTrue(StringUtil.equalsWithNullsAndBlanks("", ""));
    }

    @Test
    public void equalsWithNullsAndBlanks_blankAndNullValues_returnsTrue() {
        assertTrue(StringUtil.equalsWithNullsAndBlanks("", null));
    }

    @Test
    public void equalsWithNullsAndBlanks_nullAndBlankValues_returnsTrue() {
        assertTrue(StringUtil.equalsWithNullsAndBlanks(null, ""));
    }

    @Test
    public void equalsWithNullsAndBlanks_nullFirstValue_returnsFalse() {
        assertFalse(StringUtil.equalsWithNullsAndBlanks(null, "same"));
    }

    @Test
    public void equalsWithNullsAndBlanks_blankFirstValue_returnsFalse() {
        assertFalse(StringUtil.equalsWithNullsAndBlanks("", "same"));
    }

    @Test
    public void equalsWithNullsAndBlanks_nullSecondValue_returnsFalse() {
        assertFalse(StringUtil.equalsWithNullsAndBlanks("same", null));
    }

    @Test
    public void equalsWithNullsAndBlanks_blankSecondValue_returnsFalse() {
        assertFalse(StringUtil.equalsWithNullsAndBlanks("same", ""));
    }

    @Test
    public void equalsWithNullsAndBlanks_differentValues_returnsFalse() {
        assertFalse(StringUtil.equalsWithNullsAndBlanks("first", "second"));
    }

    @Test
    public void equalsWithNullsAndBlanks_differentCasedValues_returnsFalse() {
        assertFalse(StringUtil.equalsWithNullsAndBlanks("same", "Same"));
    }

    @Test
    public void redactApiUrl_redactsCorrectly() {
        final String[] inputValues = new String[] {
                "https://fakeapi.com/AuthenticationService/user/authenticate?token=9f1451b8-8b8d-4047-865c-194c40d1f14b",
                "https://fakeapi.com/AuthenticationService/users/9f1451b8-8b8d-4047-865c-194c40d1f14b",
                "https://fakeapi.com/dms/bc/mobile/mobile-config.xml",
                "https://fakeapi.com/gateway/v2/registration/user@jid.im/dcd873f9-6f12-4d37-8bec-3ecc60aed1e6",
                "https://fakeapi.com/gateway/v2/msg/history/dcd873f9-6f12-4d37-8bec-3ecc60aed1e6/0",
                "https://fakeapi.com/gateway/v2/msg/history/dcd873f9-6f12-4d37-8bec-3ecc60aed1e6/1532018429130",
                "https://fakeapi.com/v2.0/user/user@jid.im/services",
                "https://fakeapi.com/v2.0/user/user@jid.im/services/broadworksanywhere",
                "https://fakeapi.com/v2.0/user/user@jid.im/services/broadworksanywhere/location/4445556666",
                "https://fakeapi.com/v2.0/user/user@jid.im/services/broadworksanywhere/location/4445556666;3333",
                "https://fakeapi.com/v2.0/user/user@jid.im/directories/enterprise?start=1500&sortColumn=firstName/i&results=50",
                "https://fakeapi.com/v2.0/user/user@jid.im/directories/enterprise?impId=searc@jid.im/i",
                "https://fakeapi.com/v2.0/user/user@jid.im/directories/enterprise?impId=searc@jid.im%2Fi",
                "https://fakeapi.com/v2.0/user/user%40jid.im/directories/enterprise?impId=searc@jid.im%2Fi",
                "https://fakeapi.com/v2.0/user/user%40jid.im/directories/enterprise?impId=searc%40jid.im%2Fi",
                "https://fakeapi.com/v2.0/user/user@jid.im/directories/enterprise?number=2223334444",
                "https://fakeapi.com/v2.0/user/user@jid.im/directories/enterprise?number=+2223334444",
                "https://fakeapi.com/v2.0/user/user@jid.im/directories/enterprise?number=+2223334444,3333",
                "https://fakeapi.com/v2.0/user/user@jid.im/directories/enterprise?extension=2345",
                "https://fakeapi.com/v2.0/user/user@jid.im/directories/enterprise?extension=23456",
                "https://fakeapi.com/v2.0/user/user@jid.im/directories/enterprise?name=Jim Smith/i",
                "https://fakeapi.com/v2.0/user/user@jid.im/directories/enterprise?name=Bono/i",
                "https://fakeapi.com/v2.0/user/user@jid.im/directories/enterprise?name=Jim Smith%2Fi",
                "https://fakeapi.com/v2.0/user/user@jid.im/directories/enterprise?name=Bono%2Fi",
                "https://fakeapi.com/v2.0/user/user@jid.im/calls/new?address=3334445555",
                "https://fakeapi.com/v2.0/user/user@jid.im/calls/imrn?callingPartyAddress=3334445555&calledPartyAddress=7778889999",
                "https://fakeapi.com/v2.0/user/user%40jid.im/calls/imrn?callingPartyAddress=3334445555&calledPartyAddress=7778889999",
                "https://fakeapi.com/v2.0/user/user@jid.im/calls/imrn?callingPartyAddress=3334445555@nextiva.com&calledPartyAddress=1234567988@nextiva.com",
                "https://fakeapi.com/v2.0/user/user%40jid.im/calls/imrn?callingPartyAddress=3334445555@nextiva.com&calledPartyAddress=7778889999@nextiva.com"
        };
        final String[] expectedOutputValues = new String[] {
                "https://fakeapi.com/AuthenticationService/user/authenticate?token=UDID",
                "https://fakeapi.com/AuthenticationService/users/UDID",
                "https://fakeapi.com/dms/bc/mobile/mobile-config.xml",
                "https://fakeapi.com/gateway/v2/registration/USER_EMAIL/UDID",
                "https://fakeapi.com/gateway/v2/msg/history/UDID/0",
                "https://fakeapi.com/gateway/v2/msg/history/UDID/PHONE_NUMBER",
                "https://fakeapi.com/v2.0/user/USER_EMAIL/services",
                "https://fakeapi.com/v2.0/user/USER_EMAIL/services/broadworksanywhere",
                "https://fakeapi.com/v2.0/user/USER_EMAIL/services/broadworksanywhere/location/PHONE_NUMBER",
                "https://fakeapi.com/v2.0/user/USER_EMAIL/services/broadworksanywhere/location/PHONE_NUMBER",
                "https://fakeapi.com/v2.0/user/USER_EMAIL/directories/enterprise?start=1500&sortColumn=firstName/i&results=50",
                "https://fakeapi.com/v2.0/user/USER_EMAIL/directories/enterprise?impId=IMP_ID/i",
                "https://fakeapi.com/v2.0/user/USER_EMAIL/directories/enterprise?impId=IMP_ID%2Fi",
                "https://fakeapi.com/v2.0/user/USER_EMAIL/directories/enterprise?impId=IMP_ID%2Fi",
                "https://fakeapi.com/v2.0/user/USER_EMAIL/directories/enterprise?impId=IMP_ID%2Fi",
                "https://fakeapi.com/v2.0/user/USER_EMAIL/directories/enterprise?number=PHONE_NUMBER",
                "https://fakeapi.com/v2.0/user/USER_EMAIL/directories/enterprise?number=PHONE_NUMBER",
                "https://fakeapi.com/v2.0/user/USER_EMAIL/directories/enterprise?number=PHONE_NUMBER",
                "https://fakeapi.com/v2.0/user/USER_EMAIL/directories/enterprise?extension=PHONE_NUMBER",
                "https://fakeapi.com/v2.0/user/USER_EMAIL/directories/enterprise?extension=PHONE_NUMBER",
                "https://fakeapi.com/v2.0/user/USER_EMAIL/directories/enterprise?name=NAME/i",
                "https://fakeapi.com/v2.0/user/USER_EMAIL/directories/enterprise?name=NAME/i",
                "https://fakeapi.com/v2.0/user/USER_EMAIL/directories/enterprise?name=NAME%2Fi",
                "https://fakeapi.com/v2.0/user/USER_EMAIL/directories/enterprise?name=NAME%2Fi",
                "https://fakeapi.com/v2.0/user/USER_EMAIL/calls/new?address=PHONE_NUMBER",
                "https://fakeapi.com/v2.0/user/USER_EMAIL/calls/imrn?callingPartyAddress=PHONE_NUMBER&calledPartyAddress=PHONE_NUMBER",
                "https://fakeapi.com/v2.0/user/USER_EMAIL/calls/imrn?callingPartyAddress=PHONE_NUMBER&calledPartyAddress=PHONE_NUMBER",
                "https://fakeapi.com/v2.0/user/USER_EMAIL/calls/imrn?callingPartyAddress=PHONE_NUMBER@nextiva.com&calledPartyAddress=PHONE_NUMBER@nextiva.com",
                "https://fakeapi.com/v2.0/user/USER_EMAIL/calls/imrn?callingPartyAddress=PHONE_NUMBER@nextiva.com&calledPartyAddress=PHONE_NUMBER@nextiva.com"
        };

        for (int i = 0; i < inputValues.length; i++) {
            assertEquals(expectedOutputValues[i], StringUtil.redactApiUrl(inputValues[i]));
        }
    }

    @Test
    public void redactApiUrl_redactsSipMessageCorrectly() {
        final String inputValues =
                "SIP Port Sip Service  onSendingSignaling: REGISTER sip:prod.voipdnsservers.com SIP/2.0" +
                        "Via: SIP/2.0/TCP 192.168.0.169:5060;branch=z9hG4bK-524287-1---38f41463c500d63e;rport" + "" +
                        "Max-Forwards: 70" +
                        "Contact: <sip:ebconnect36402036_5431_btbc_mb@192.168.0.169:5060;transport=tcp>;+sip.instance=\"<urn:uuid:095B9491-453C-F863-C986-267B9FBD9873>\";reg-id=1" +
                        "To: <sip:ebconnect36402036_5431_btbc_mb@prod.voipdnsservers.com>" +
                        "From: <sip:ebconnect36402036_5431_btbc_mb@prod.voipdnsservers.com>;tag=f74a2d62" +
                        "Call-ID: 8Am0BZ7UvumCS5kZxE3UAQ.." +
                        "CSeq: 1 REGISTER" +
                        "Expires: 45" +
                        "Allow: INVITE, ACK, CANCEL, OPTIONS, BYE, REFER, NOTIFY, MESSAGE, REGISTER, SUBSCRIBE, INFO, PUBLISH" +
                        "Supported: replaces, answermode, eventlist, park-info, outbound, path" +
                        "User-Agent: PortSIP - Toro Nightly (SNAPSHOT-20230731, Android, 33)" +
                        "Allow-Events: hold, talk, conference, dialog, park-info" +
                        "Content-Length: 0";
        final String expectedOutputValues =
                "SIP Port Sip Service  onSendingSignaling: REGISTER sip:USER_EMAIL@192.168.0.169:5060;transport=tcp>;+sip.instance=\"<urn:uuid:095B9491-453C-F863-C986-267B9FBD9873>\";reg-id=1" +
                        "To: <sip:USER_EMAIL@prod.voipdnsservers.com>" +
                        "From: <sip:USER_EMAIL@prod.voipdnsservers.com>;tag=f74a2d62" +
                        "Call-ID: 8Am0BZ7UvumCS5kZxE3UAQ.." +
                        "CSeq: 1 REGISTER" +
                        "Expires: 45" +
                        "Allow: INVITE, ACK, CANCEL, OPTIONS, BYE, REFER, NOTIFY, MESSAGE, REGISTER, SUBSCRIBE, INFO, PUBLISH" +
                        "Supported: replaces, answermode, eventlist, park-info, outbound, path" +
                        "User-Agent: PortSIP - Toro Nightly (SNAPSHOT-20230731, Android, 33)" +
                        "Allow-Events: hold, talk, conference, dialog, park-info" +
                        "Content-Length: 0";

        assertEquals(expectedOutputValues, StringUtil.redactApiUrl(inputValues));
    }
    @Test
    public void redactApiUrl_redactsSipRegister() {
        final String inputValues =
                "REGISTER sip:prod.voipdnsservers.com SIP/2.0" +
                        "Via: SIP/2.0/TCP 192.168.0.169:5060;branch=z9hG4bK-524287-1---5520885612d3da4e" +
                        "Max-Forwards: 70" +
                        "Contact: <sip:ebconnect36402036_5431_btbc_mb@192.168.0.169:5060;transport=tcp>;+sip.instance=\"<urn:uuid:095B9491-453C-F863-C986-267B9FBD9873>\";reg-id=1" +
                        "To: <sip:ebconnect36402036_5431_btbc_mb@prod.voipdnsservers.com>" +
                        "From: <sip:ebconnect36402036_5431_btbc_mb@prod.voipdnsservers.com>;tag=f74a2d62" +
                        "Call-ID: 8Am0BZ7UvumCS5kZxE3UAQ.." +
                        "CSeq: 2 REGISTER" +
                        "Expires: 45" +
                        "Allow: INVITE, ACK, CANCEL, OPTIONS, BYE, REFER, NOTIFY, MESSAGE, REGISTER, SUBSCRIBE, INFO, PUBLISH" +
                        "Supported: replaces, answermode, eventlist, park-info, outbound, path" +
                        "User-Agent: PortSIP - Toro Nightly (SNAPSHOT-20230731, Android, 33)" +
                        "Authorization: Digest username=\"EFE9B291B050C\",realm=\"nextiva.com\",nonce=\"BroadWorksXlkrld3t1T4qibd9BW\",uri=\"sip:prod.voipdnsservers.com\",response=\"9802107e60fa4b9a7d1811b46cccb1c1\",cnonce=\"9cd6d9f0789abc48bb911ca4d611b673\",nc=00000001,qop=auth,algorithm=MD5" +
                        "Allow-Events: hold, talk, conference, dialog, park-info" +
                        "Content-Length: 0";
        final String expectedOutputValues =
                "REGISTER sip:USER_EMAIL@192.168.0.169:5060;transport=tcp>;+sip.instance=\"<urn:uuid:095B9491-453C-F863-C986-267B9FBD9873>\";reg-id=1" +
                "To: <sip:USER_EMAIL@prod.voipdnsservers.com>" +
                "From: <sip:USER_EMAIL@prod.voipdnsservers.com>;tag=f74a2d62" +
                "Call-ID: 8Am0BZ7UvumCS5kZxE3UAQ.." +
                "CSeq: 2 REGISTER" +
                "Expires: 45" +
                "Allow: INVITE, ACK, CANCEL, OPTIONS, BYE, REFER, NOTIFY, MESSAGE, REGISTER, SUBSCRIBE, INFO, PUBLISH" +
                "Supported: replaces, answermode, eventlist, park-info, outbound, path" +
                "User-Agent: PortSIP - Toro Nightly (SNAPSHOT-20230731, Android, 33)" +
                "Authorization: Digest username=\"EFE9B291B050C\",realm=\"nextiva.com\",nonce=\"BroadWorksXlkrld3t1T4qibd9BW\",uri=\"sip:prod.voipdnsservers.com\",response=\"9802107e60fa4b9a7d1811b46cccb1c1\",cnonce=\"9cd6d9f0789abc48bb911ca4d611b673\",nc=00000001,qop=auth,algorithm=MD5" +
                "Allow-Events: hold, talk, conference, dialog, park-info" +
                "Content-Length: 0";

        assertEquals(expectedOutputValues, StringUtil.redactApiUrl(inputValues));
    }
    @Test
    public void redactApiUrl_redactsSipMessageOkCorrectly() {
        final String inputValues =
                "SIP/2.0 200 OK" +
                        "Via: SIP/2.0/TCP 192.168.0.169:5060;received=68.3.12.18;branch=z9hG4bK-524287-1---5520885612d3da4e;rport=47859" +
                        "Contact: <sip:ebconnect36402036_5431_btbc_mb@192.168.0.169:5060;transport=tcp>;expires=90;q=0.5" +
                        "To: <sip:ebconnect36402036_5431_btbc_mb@prod.voipdnsservers.com>;tag=2080385087-1690851562969" +
                        "From: <sip:ebconnect36402036_5431_btbc_mb@prod.voipdnsservers.com>;tag=f74a2d62" +
                        "Call-ID: 8Am0BZ7UvumCS5kZxE3UAQ.." +
                        "CSeq: 2 REGISTER" +
                        "Allow-Events: call-info, line-seize, dialog, message-summary, as-feature-event, x-broadworks-hoteling, x-broadworks-call-center-status, conference" +
                        "Content-Length: 0";
        final String expectedOutputValues =
                "SIP/2.0 200 OK" +
                        "Via: SIP/2.0/TCP 192.168.0.169:5060;received=68.3.12.18;branch=z9hG4bK-524287-1---5520885612d3da4e;rport=47859" +
                        "Contact: <sip:USER_EMAIL@192.168.0.169:5060;transport=tcp>;expires=90;q=0.5" +
                        "To: <sip:USER_EMAIL@prod.voipdnsservers.com>;tag=2080385087-1690851562969" +
                        "From: <sip:USER_EMAIL@prod.voipdnsservers.com>;tag=f74a2d62" +
                        "Call-ID: 8Am0BZ7UvumCS5kZxE3UAQ.." +
                        "CSeq: 2 REGISTER" +
                        "Allow-Events: call-info, line-seize, dialog, message-summary, as-feature-event, x-broadworks-hoteling, x-broadworks-call-center-status, conference" +
                        "Content-Length: 0";

        assertEquals(expectedOutputValues, StringUtil.redactApiUrl(inputValues));
    }
    @Test
    public void redactApiUrl_redactsSipMessageInviteCorrectly() {
        final String inputValues =
                "INVITE sip:9999@prod.voipdnsservers.com SIP/2.0" +
                        "Via: SIP/2.0/TCP 192.168.0.169:5060;branch=z9hG4bK-524287-1---6fe7970010ba9b7b" +
                        "Max-Forwards: 70" +
                        "Contact: <sip:ebconnect36402036_5431_btbc_mb@192.168.0.169:5060;ob;transport=tcp>;+sip.instance=\"<urn:uuid:095B9491-453C-F863-C986-267B9FBD9873>\"" +
                        "To: <sip:9999@prod.voipdnsservers.com>" +
                        "From: <sip:ebconnect36402036_5431_btbc_mb@prod.voipdnsservers.com>;tag=cce55e32" +
                        "Call-ID: d-e1uIPcC80ry_mTjrXI9Q.." +
                        "CSeq: 1 INVITE" +
                        "Allow: INVITE, ACK, CANCEL, OPTIONS, BYE, REFER, NOTIFY, MESSAGE, REGISTER, SUBSCRIBE, INFO, PUBLISH, PRACK, UPDATE" +
                        "Content-Type: application/sdp" +
                        "Supported: replaces, answermode, eventlist, park-info, outbound, path, 100rel" +
                        "User-Agent: PortSIP - Toro Nightly (SNAPSHOT-20230731, Android, 33)" +
                        "Allow-Events: hold, talk, conference, dialog, park-info" +
                        "Content-Length: 361" +
                        "" +
                        "v=0" +
                        "o=- 1690851563 1 IN IP4 192.168.0.169" +
                        "s=ps" +
                        "c=IN IP4 192.168.0.169" +
                        "t=0 0" +
                        "m=audio 5060 RTP/AVP 9 18 0 8 105 101" +
                        "a=rtpmap:9 G722/8000" +
                        "a=rtpmap:18 G729/8000" +
                        "a=fmtp:18 annexb=no" +
                        "a=rtpmap:0 PCMU/8000" +
                        "a=rtpmap:8 PCMA/8000" +
                        "a=rtpmap:105 opus/48000/2" +
                        "a=fmtp:105 useinbandfec=1" +
                        "a=rtpmap:101 telephone-event/8000" +
                        "a=fmtp:101 0-16" +
                        "a=mid:audio" +
                        "a=sendrecv";
        final String expectedOutputValues =
                "INVITE sip:USER_EMAIL@prod.voipdnsservers.com SIP/2.0" +
                        "Via: SIP/2.0/USER_EMAIL/sdpSupported: replaces, answermode, eventlist, park-info, outbound, path, 100rel" +
                        "User-Agent: PortSIP - Toro Nightly (SNAPSHOT-20230731, Android, 33)" +
                        "Allow-Events: hold, talk, conference, dialog, park-info" +
                        "Content-Length: 361" +
                        "" +
                        "v=0" +
                        "o=- 1690851563 1 IN IP4 192.168.0.169" +
                        "s=ps" +
                        "c=IN IP4 192.168.0.169" +
                        "t=0 0" +
                        "m=audio 5060 RTP/AVP 9 18 0 8 105 101" +
                        "a=rtpmap:9 G722/8000" +
                        "a=rtpmap:18 G729/8000" +
                        "a=fmtp:18 annexb=no" +
                        "a=rtpmap:0 PCMU/8000" +
                        "a=rtpmap:8 PCMA/8000" +
                        "a=rtpmap:105 opus/48000/2" +
                        "a=fmtp:105 useinbandfec=1" +
                        "a=rtpmap:101 telephone-event/8000" +
                        "a=fmtp:101 0-16" +
                        "a=mid:audio" +
                        "a=sendrecv";

        assertEquals(expectedOutputValues, StringUtil.redactApiUrl(inputValues));
    }
    @Test
    public void redactApiUrl_redactsSipMessageRingingCorrectly() {
        final String inputValues =
                "SIP/2.0 180 Ringing" +
                        "Via: SIP/2.0/TCP 192.168.0.169:5060;received=68.3.12.18;branch=z9hG4bK-524287-1---6fe7970010ba9b7b;rport=47859" +
                        "Contact: <sip:208.89.109.80:5062;transport=tcp>" +
                        "To: <sip:9999@prod.voipdnsservers.com>;tag=1933723123-1690851563558" +
                        "From: <sip:ebconnect36402036_5431_btbc_mb@prod.voipdnsservers.com>;tag=cce55e32" +
                        "Call-ID: d-e1uIPcC80ry_mTjrXI9Q.." +
                        "CSeq: 1 INVITE" +
                        "Allow: ACK, BYE, CANCEL, INFO, INVITE, OPTIONS, PRACK, REFER, NOTIFY, UPDATE" +
                        "Call-Info: <sip:10.101.112.16>;appearance-index=1" +
                        "Privacy: none" +
                        "P-Asserted-Identity: \"Voice Portal Voice Portal\"<sip:9999@nextiva.com;user=phone>" +
                        "X-BroadWorks-Correlation-Info: 70b99faa-9ade-4908-a9ed-68382d2fcea3" +
                        "Content-Length: 0";
        final String expectedOutputValues =
                "SIP/2.0 180 Ringing" +
                        "Via: SIP/2.0/TCP 192.168.0.169:5060;received=68.3.12.18;branch=z9hG4bK-524287-1---6fe7970010ba9b7b;rport=47859" +
                        "Contact: <sip:USER_EMAIL@prod.voipdnsservers.com>;tag=1933723123-1690851563558" +
                        "From: <sip:USER_EMAIL@prod.voipdnsservers.com>;tag=cce55e32" +
                        "Call-ID: d-e1uIPcC80ry_mTjrXI9Q.." +
                        "CSeq: 1 INVITE" +
                        "Allow: ACK, BYE, CANCEL, INFO, INVITE, OPTIONS, PRACK, REFER, NOTIFY, UPDATE" +
                        "Call-Info: <sip:USER_EMAIL@nextiva.com;user=phone>" +
                        "X-BroadWorks-Correlation-Info: UDID" +
                        "Content-Length: 0";

        assertEquals(expectedOutputValues, StringUtil.redactApiUrl(inputValues));
    }

    @Test
    public void getStringBetween_correctNumber() {
        assertEquals("5555555555", StringUtil.getStringBetween("sip:5555555555@nextiva.com" , "sip:", "@"));
    }
    @Test
    public void getStringBetween_correctNumber2() {
        assertEquals("555-555-5555", StringUtil.getStringBetween("sip:555-555-5555@nextiva.com" , "sip:", "@"));
    }
    @Test
    public void getStringBetween_correctAddress() {
        assertEquals("jmicheals", StringUtil.getStringBetween("sip:jmicheals@nextiva.com" , "sip:", "@"));
    }
    @Test
    public void getStringBetween_allEmpty() {
        assertEquals("", StringUtil.getStringBetween("" , "",""));
    }
    @Test
    public void getStringBetween_emptyInput() {
        assertEquals("", StringUtil.getStringBetween("" , "sip:", "@"));
    }
    @Test
    public void getStringBetween_emptyBeginning() {
        assertEquals("", StringUtil.getStringBetween("sip:test@test.com" , "", "@"));

    }
    @Test
    public void getStringBetween_emptyEnding() {
        assertEquals("", StringUtil.getStringBetween("sip:test@test.test" , "sip:", ""));

    }
    @Test
    public void getStringBetween_missingBeginningInInput() {
        assertEquals("", StringUtil.getStringBetween("test@test.test" , "sip:", "@"));
    }
    @Test
    public void getStringBetween_missingEndingInInput() {
        assertEquals("", StringUtil.getStringBetween("test@test.test" , "sip:", "#"));
    }
    @Test
    public void getStringBetween_phoneNumberWithoutSipOrDomain() {
        assertEquals("", StringUtil.getStringBetween("555-555-5555" , "sip:", "@"));
    }
    @Test
    public void getStringBetween_phoneNumberWithoutADomain() {
        assertEquals("", StringUtil.getStringBetween("sip:555-555-5555" , "sip:", "@"));
    }
}
