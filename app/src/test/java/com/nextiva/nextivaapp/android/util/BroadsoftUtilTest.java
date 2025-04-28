/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.util;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.nextiva.nextivaapp.android.BaseRobolectricTest;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.managers.interfaces.ConfigManager;
import com.nextiva.nextivaapp.android.managers.interfaces.NetManager;
import com.nextiva.nextivaapp.android.models.CallLogEntry;
import com.nextiva.nextivaapp.android.models.ChatConversation;
import com.nextiva.nextivaapp.android.models.NextivaAnywhereLocation;
import com.nextiva.nextivaapp.android.models.NextivaContact;
import com.nextiva.nextivaapp.android.models.Service;
import com.nextiva.nextivaapp.android.models.ServiceSettings;
import com.nextiva.nextivaapp.android.models.SimultaneousRingLocation;
import com.nextiva.nextivaapp.android.models.UserDetails;
import com.nextiva.nextivaapp.android.models.net.broadsoft.BroadsoftProfileAdditionalDetails;
import com.nextiva.nextivaapp.android.models.net.broadsoft.BroadsoftProfileDetails;
import com.nextiva.nextivaapp.android.models.net.broadsoft.BroadsoftProfileResponse;
import com.nextiva.nextivaapp.android.models.net.broadsoft.BroadsoftServiceSettingsResponse;
import com.nextiva.nextivaapp.android.models.net.broadsoft.BroadsoftServicesResponse;
import com.nextiva.nextivaapp.android.models.net.broadsoft.calllogs.BroadsoftAllCallLogsResponse;
import com.nextiva.nextivaapp.android.models.net.broadsoft.calllogs.BroadsoftCallLogEntry;
import com.nextiva.nextivaapp.android.models.net.broadsoft.enterprisecontacts.BroadsoftEnterpriseAdditionalDetails;
import com.nextiva.nextivaapp.android.models.net.broadsoft.enterprisecontacts.BroadsoftEnterpriseDirectoryDetails;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.BroadsoftMobileConfigResponse;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.BroadsoftMobileConfigServices;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.BroadsoftServicesSupplementaryServices;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.supplementaryservices.BroadsoftSupplementaryServicesXsi;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.supplementaryservices.BroadsoftSupplementaryServicesXsiBroadworksAnywhere;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.settings.BroadsoftMobileConfigGeneralSetting;
import com.nextiva.nextivaapp.android.models.net.broadsoft.services.BroadsoftBaseServiceSettings;
import com.nextiva.nextivaapp.android.models.net.broadsoft.services.BroadsoftBroadWorksAnywhereLocation;
import com.nextiva.nextivaapp.android.models.net.broadsoft.services.BroadsoftBroadWorksAnywhereLocationBody;
import com.nextiva.nextivaapp.android.models.net.broadsoft.services.BroadsoftBroadWorksAnywhereServiceSettings;
import com.nextiva.nextivaapp.android.models.net.broadsoft.services.BroadsoftCallForwardingAlwaysServiceSettings;
import com.nextiva.nextivaapp.android.models.net.broadsoft.services.BroadsoftCallForwardingBusyServiceSettings;
import com.nextiva.nextivaapp.android.models.net.broadsoft.services.BroadsoftCallForwardingNoAnswerServiceSettings;
import com.nextiva.nextivaapp.android.models.net.broadsoft.services.BroadsoftCallForwardingNotReachableServiceSettings;
import com.nextiva.nextivaapp.android.models.net.broadsoft.services.BroadsoftCallingIdDeliveryBlockingServiceSettings;
import com.nextiva.nextivaapp.android.models.net.broadsoft.services.BroadsoftDoNotDisturbServiceSettings;
import com.nextiva.nextivaapp.android.models.net.broadsoft.services.BroadsoftRemoteOfficeServiceSettings;
import com.nextiva.nextivaapp.android.models.net.broadsoft.services.BroadsoftService;
import com.nextiva.nextivaapp.android.models.net.broadsoft.services.BroadsoftSimultaneousRingLocation;
import com.nextiva.nextivaapp.android.models.net.broadsoft.services.BroadsoftSimultaneousRingPersonalServiceSettings;
import com.nextiva.nextivaapp.android.models.net.broadsoft.ums.BroadsoftUmsChatMessage;
import com.nextiva.nextivaapp.android.models.net.broadsoft.ums.BroadsoftUmsChatMessagesResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by adammacdonald on 3/27/18.
 */

@RunWith(RobolectricTestRunner.class)
public class BroadsoftUtilTest extends BaseRobolectricTest {

    @Mock
    private ConfigManager mConfigManager;
    @Mock
    private NetManager mNetManager;

    @Override
    public void setup() throws IOException {
        super.setup();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getUserDetails_incompleteInput_returnsCorrectValues() {
        BroadsoftProfileResponse broadsoftProfileResponse = new BroadsoftProfileResponse();

        UserDetails userDetails = BroadsoftUtil.getUserDetails(broadsoftProfileResponse);

        assertNull(userDetails);
    }

    @Test
    public void getUserDetails_partialIncompleteInput_returnsCorrectValues() {
        BroadsoftProfileDetails details = new BroadsoftProfileDetails();
        BroadsoftProfileAdditionalDetails additionalDetails = new BroadsoftProfileAdditionalDetails();

        BroadsoftProfileResponse broadsoftProfileResponse = new BroadsoftProfileResponse(details, additionalDetails);

        UserDetails userDetails = BroadsoftUtil.getUserDetails(broadsoftProfileResponse);

        assertNotNull(userDetails);
        assertNull(userDetails.getFirstName());
        assertNull(userDetails.getLastName());
        assertNull(userDetails.getEmail());
        assertNull(userDetails.getImpId());
    }

    @Test
    public void getUserDetails_completeInput_returnsCorrectValues() {
        BroadsoftProfileDetails details = new BroadsoftProfileDetails("Jim", "Smith");
        BroadsoftProfileAdditionalDetails additionalDetails = new BroadsoftProfileAdditionalDetails("fake@email.com", "jim.smith@fake.im");

        BroadsoftProfileResponse broadsoftProfileResponse = new BroadsoftProfileResponse(details, additionalDetails);

        UserDetails userDetails = BroadsoftUtil.getUserDetails(broadsoftProfileResponse);

        assertNotNull(userDetails);
        assertEquals("Jim", userDetails.getFirstName());
        assertEquals("Smith", userDetails.getLastName());
        assertEquals("fake@email.com", userDetails.getEmail());
        assertEquals("jim.smith@fake.im", userDetails.getImpId());
    }

    @Test
    public void getServices_incompleteInput_returnsNull() {
        BroadsoftServicesResponse broadsoftServicesResponse = new BroadsoftServicesResponse(null);
        assertNull(BroadsoftUtil.getServices(broadsoftServicesResponse));

        broadsoftServicesResponse = new BroadsoftServicesResponse(new ArrayList<>());
        assertNull(BroadsoftUtil.getServices(broadsoftServicesResponse));
    }

    @Test
    public void getServices_completeInput_returnsCorrectValues() {
        ArrayList<BroadsoftService> servicesList = new ArrayList<BroadsoftService>() {{
            add(new BroadsoftService("name1", "uri1"));
            add(new BroadsoftService(null, "uri2"));
            add(new BroadsoftService("name3", null));
            add(new BroadsoftService(null, null));
            add(null);
        }};

        BroadsoftServicesResponse broadsoftServicesResponse = new BroadsoftServicesResponse(servicesList);

        Service[] services = BroadsoftUtil.getServices(broadsoftServicesResponse);

        assertEquals(4, services.length);
        assertEquals("name1", services[0].getType());
        assertEquals("uri1", services[0].getUri());
        assertNull(services[1].getType());
        assertEquals("uri2", services[1].getUri());
        assertEquals("name3", services[2].getType());
        assertNull(services[2].getUri());
        assertNull(services[3].getType());
        assertNull(services[3].getUri());
    }

    @Test
    public void getServiceSettings_incompleteInput_returnsCorrectValues() {
        BroadsoftServiceSettingsResponse broadsoftServiceSettingsResponse = new BroadsoftServiceSettingsResponse();

        ServiceSettings serviceSettings = BroadsoftUtil.getServiceSettings(Enums.Service.TYPE_REMOTE_OFFICE, "someUri", broadsoftServiceSettingsResponse);

        assertNotNull(serviceSettings);
        assertEquals(Enums.Service.TYPE_REMOTE_OFFICE, serviceSettings.getType());
        assertEquals("someUri", serviceSettings.getUri());
        assertNull(serviceSettings.getActiveRaw());
        assertNull(serviceSettings.getRingSplashEnabledRaw());
        assertNull(serviceSettings.getNumberOfRings());
        assertNull(serviceSettings.getRemoteOfficeNumber());
        assertNull(serviceSettings.getForwardToPhoneNumber());
        assertNull(serviceSettings.getAlertAllLocationsForClickToDialCallsRaw());
        assertNull(serviceSettings.getAlertAllLocationsForGroupPagingCallsRaw());
        assertNull(serviceSettings.getNextivaAnywhereLocationsList());
    }

    @Test
    public void getServiceSettings_completeInput_returnsCorrectValues() {
        ArrayList<BroadsoftBroadWorksAnywhereLocation> locationsList = new ArrayList<BroadsoftBroadWorksAnywhereLocation>() {{
            add(new BroadsoftBroadWorksAnywhereLocation("1111", "description1", true, false, false, false));
            add(new BroadsoftBroadWorksAnywhereLocation("2222", "description2", false, true, false, false));
            add(new BroadsoftBroadWorksAnywhereLocation("3333", "description3", false, false, true, false));
            add(new BroadsoftBroadWorksAnywhereLocation("4444", "description4", false, false, false, true));
            add(new BroadsoftBroadWorksAnywhereLocation("5555", null, false, false, false, false));
        }};

        ArrayList<BroadsoftSimultaneousRingLocation> simultaneousRingLocationsList = new ArrayList<BroadsoftSimultaneousRingLocation>() {{
            add(new BroadsoftSimultaneousRingLocation("1111", null));
            add(new BroadsoftSimultaneousRingLocation("2222", true));
            add(new BroadsoftSimultaneousRingLocation("3333", false));
            add(new BroadsoftSimultaneousRingLocation(null, true));
            add(new BroadsoftSimultaneousRingLocation(null, false));
            add(new BroadsoftSimultaneousRingLocation(null, null));
        }};

        BroadsoftServiceSettingsResponse broadsoftServiceSettingsResponse = new BroadsoftServiceSettingsResponse(false, true, 100, "1234", "0987", false, true, locationsList, "Ring for all Incoming Calls", simultaneousRingLocationsList);

        ServiceSettings serviceSettings = BroadsoftUtil.getServiceSettings(Enums.Service.TYPE_REMOTE_OFFICE, "someUri", broadsoftServiceSettingsResponse);

        assertNotNull(serviceSettings);
        assertEquals(Enums.Service.TYPE_REMOTE_OFFICE, serviceSettings.getType());
        assertEquals("someUri", serviceSettings.getUri());
        assertFalse(serviceSettings.getActiveRaw());
        assertTrue(serviceSettings.getRingSplashEnabledRaw());
        assertEquals(Integer.valueOf(100), serviceSettings.getNumberOfRings());
        assertEquals("1234", serviceSettings.getRemoteOfficeNumber());
        assertEquals("0987", serviceSettings.getForwardToPhoneNumber());
        assertFalse(serviceSettings.getAlertAllLocationsForClickToDialCalls());
        assertTrue(serviceSettings.getAlertAllLocationsForGroupPagingCalls());

        assertEquals(5, serviceSettings.getNextivaAnywhereLocationsList().size());

        assertEquals("1111", serviceSettings.getNextivaAnywhereLocationsList().get(0).getPhoneNumber());
        assertEquals("description1", serviceSettings.getNextivaAnywhereLocationsList().get(0).getDescription());
        assertTrue(serviceSettings.getNextivaAnywhereLocationsList().get(0).getActive());
        assertFalse(serviceSettings.getNextivaAnywhereLocationsList().get(0).getCallControlEnabled());
        assertFalse(serviceSettings.getNextivaAnywhereLocationsList().get(0).getPreventDivertingCalls());
        assertFalse(serviceSettings.getNextivaAnywhereLocationsList().get(0).getAnswerConfirmationRequired());

        assertEquals("2222", serviceSettings.getNextivaAnywhereLocationsList().get(1).getPhoneNumber());
        assertEquals("description2", serviceSettings.getNextivaAnywhereLocationsList().get(1).getDescription());
        assertFalse(serviceSettings.getNextivaAnywhereLocationsList().get(1).getActive());
        assertTrue(serviceSettings.getNextivaAnywhereLocationsList().get(1).getCallControlEnabled());
        assertFalse(serviceSettings.getNextivaAnywhereLocationsList().get(1).getPreventDivertingCalls());
        assertFalse(serviceSettings.getNextivaAnywhereLocationsList().get(1).getAnswerConfirmationRequired());

        assertEquals("3333", serviceSettings.getNextivaAnywhereLocationsList().get(2).getPhoneNumber());
        assertEquals("description3", serviceSettings.getNextivaAnywhereLocationsList().get(2).getDescription());
        assertFalse(serviceSettings.getNextivaAnywhereLocationsList().get(2).getActive());
        assertFalse(serviceSettings.getNextivaAnywhereLocationsList().get(2).getCallControlEnabled());
        assertTrue(serviceSettings.getNextivaAnywhereLocationsList().get(2).getPreventDivertingCalls());
        assertFalse(serviceSettings.getNextivaAnywhereLocationsList().get(2).getAnswerConfirmationRequired());

        assertEquals("4444", serviceSettings.getNextivaAnywhereLocationsList().get(3).getPhoneNumber());
        assertEquals("description4", serviceSettings.getNextivaAnywhereLocationsList().get(3).getDescription());
        assertFalse(serviceSettings.getNextivaAnywhereLocationsList().get(3).getActive());
        assertFalse(serviceSettings.getNextivaAnywhereLocationsList().get(3).getCallControlEnabled());
        assertFalse(serviceSettings.getNextivaAnywhereLocationsList().get(3).getPreventDivertingCalls());
        assertTrue(serviceSettings.getNextivaAnywhereLocationsList().get(3).getAnswerConfirmationRequired());

        assertEquals("5555", serviceSettings.getNextivaAnywhereLocationsList().get(4).getPhoneNumber());
        assertNull(serviceSettings.getNextivaAnywhereLocationsList().get(4).getDescription());
        assertFalse(serviceSettings.getNextivaAnywhereLocationsList().get(4).getActive());
        assertFalse(serviceSettings.getNextivaAnywhereLocationsList().get(4).getCallControlEnabled());
        assertFalse(serviceSettings.getNextivaAnywhereLocationsList().get(4).getPreventDivertingCalls());
        assertFalse(serviceSettings.getNextivaAnywhereLocationsList().get(4).getAnswerConfirmationRequired());

        //TODO
    }

    @Test
    public void getBroadsoftBaseServiceSettings_emptyInput_returnsNull() {
        ServiceSettings serviceSettings = new ServiceSettings("", "", null, null, null, null, null, null, null, null, null, null);

        assertNull(BroadsoftUtil.getBroadsoftBaseServiceSettings(serviceSettings));
    }

    @Test
    public void getBroadsoftBaseServiceSettings_invalidInput_returnsNull() {
        ServiceSettings serviceSettings = new ServiceSettings("FAKE SETTING", "", null, null, null, null, null, null, null, null, null, null);

        assertNull(BroadsoftUtil.getBroadsoftBaseServiceSettings(serviceSettings));
    }

    @Test
    public void getBroadsoftBaseServiceSettings_doNotDisturbSettingsInput_returnsCorrectValues() {
        ServiceSettings serviceSettings = new ServiceSettings(Enums.Service.TYPE_DO_NOT_DISTURB, "", true, false, null, null, null, null, null, null, null, null);

        BroadsoftBaseServiceSettings broadsoftBaseServiceSettings = BroadsoftUtil.getBroadsoftBaseServiceSettings(serviceSettings);

        assertThat(broadsoftBaseServiceSettings, instanceOf(BroadsoftDoNotDisturbServiceSettings.class));
        assertTrue(((BroadsoftDoNotDisturbServiceSettings) broadsoftBaseServiceSettings).getActive());
        assertFalse(((BroadsoftDoNotDisturbServiceSettings) broadsoftBaseServiceSettings).getRingSplash());
    }

    @Test
    public void getBroadsoftBaseServiceSettings_doNotDisturbSettingsNullValuesInput_returnsCorrectValues() {
        ServiceSettings serviceSettings = new ServiceSettings(Enums.Service.TYPE_DO_NOT_DISTURB, "", null, null, null, null, null, null, null, null, null, null);

        BroadsoftBaseServiceSettings broadsoftBaseServiceSettings = BroadsoftUtil.getBroadsoftBaseServiceSettings(serviceSettings);

        assertThat(broadsoftBaseServiceSettings, instanceOf(BroadsoftDoNotDisturbServiceSettings.class));
        assertNull(((BroadsoftDoNotDisturbServiceSettings) broadsoftBaseServiceSettings).getActive());
        assertNull(((BroadsoftDoNotDisturbServiceSettings) broadsoftBaseServiceSettings).getRingSplash());
    }

    @Test
    public void getBroadsoftBaseServiceSettings_callForwardingAlwaysSettingsInput_returnsCorrectValues() {
        ServiceSettings serviceSettings = new ServiceSettings(Enums.Service.TYPE_CALL_FORWARDING_ALWAYS, "", true, false, null, null, "1234", null, null, null, null, null);

        BroadsoftBaseServiceSettings broadsoftBaseServiceSettings = BroadsoftUtil.getBroadsoftBaseServiceSettings(serviceSettings);

        assertThat(broadsoftBaseServiceSettings, instanceOf(BroadsoftCallForwardingAlwaysServiceSettings.class));
        assertTrue(((BroadsoftCallForwardingAlwaysServiceSettings) broadsoftBaseServiceSettings).getActive());
        assertFalse(((BroadsoftCallForwardingAlwaysServiceSettings) broadsoftBaseServiceSettings).getRingSplash());
        assertEquals("1234", ((BroadsoftCallForwardingAlwaysServiceSettings) broadsoftBaseServiceSettings).getForwardToPhoneNumber());
    }

    @Test
    public void getBroadsoftBaseServiceSettings_callForwardingAlwaysSettingsNullValuesInput_returnsCorrectValues() {
        ServiceSettings serviceSettings = new ServiceSettings(Enums.Service.TYPE_CALL_FORWARDING_ALWAYS, "", null, null, null, null, null, null, null, null, null, null);

        BroadsoftBaseServiceSettings broadsoftBaseServiceSettings = BroadsoftUtil.getBroadsoftBaseServiceSettings(serviceSettings);

        assertThat(broadsoftBaseServiceSettings, instanceOf(BroadsoftCallForwardingAlwaysServiceSettings.class));
        assertNull(((BroadsoftCallForwardingAlwaysServiceSettings) broadsoftBaseServiceSettings).getActive());
        assertNull(((BroadsoftCallForwardingAlwaysServiceSettings) broadsoftBaseServiceSettings).getRingSplash());
        assertNull(((BroadsoftCallForwardingAlwaysServiceSettings) broadsoftBaseServiceSettings).getForwardToPhoneNumber());
    }

    @Test
    public void getBroadsoftBaseServiceSettings_callForwardingWhenBusySettingsInput_returnsCorrectValues() {
        ServiceSettings serviceSettings = new ServiceSettings(Enums.Service.TYPE_CALL_FORWARDING_BUSY, "", true, null, null, null, "1234", null, null, null, null, null);

        BroadsoftBaseServiceSettings broadsoftBaseServiceSettings = BroadsoftUtil.getBroadsoftBaseServiceSettings(serviceSettings);

        assertThat(broadsoftBaseServiceSettings, instanceOf(BroadsoftCallForwardingBusyServiceSettings.class));
        assertTrue(((BroadsoftCallForwardingBusyServiceSettings) broadsoftBaseServiceSettings).getActive());
        assertEquals("1234", ((BroadsoftCallForwardingBusyServiceSettings) broadsoftBaseServiceSettings).getForwardToPhoneNumber());
    }

    @Test
    public void getBroadsoftBaseServiceSettings_callForwardingWhenBusySettingsNullValuesInput_returnsCorrectValues() {
        ServiceSettings serviceSettings = new ServiceSettings(Enums.Service.TYPE_CALL_FORWARDING_BUSY, "", null, null, null, null, null, null, null, null, null, null);

        BroadsoftBaseServiceSettings broadsoftBaseServiceSettings = BroadsoftUtil.getBroadsoftBaseServiceSettings(serviceSettings);

        assertThat(broadsoftBaseServiceSettings, instanceOf(BroadsoftCallForwardingBusyServiceSettings.class));
        assertNull(((BroadsoftCallForwardingBusyServiceSettings) broadsoftBaseServiceSettings).getActive());
        assertNull(((BroadsoftCallForwardingBusyServiceSettings) broadsoftBaseServiceSettings).getForwardToPhoneNumber());
    }

    @Test
    public void getBroadsoftBaseServiceSettings_callForwardingNoAnswerSettingsInput_returnsCorrectValues() {
        ServiceSettings serviceSettings = new ServiceSettings(Enums.Service.TYPE_CALL_FORWARDING_NO_ANSWER, "", true, null, 14, null, "1234", null, null, null, null, null);

        BroadsoftBaseServiceSettings broadsoftBaseServiceSettings = BroadsoftUtil.getBroadsoftBaseServiceSettings(serviceSettings);

        assertThat(broadsoftBaseServiceSettings, instanceOf(BroadsoftCallForwardingNoAnswerServiceSettings.class));
        assertTrue(((BroadsoftCallForwardingNoAnswerServiceSettings) broadsoftBaseServiceSettings).getActive());
        assertEquals(Integer.valueOf(14), ((BroadsoftCallForwardingNoAnswerServiceSettings) broadsoftBaseServiceSettings).getNumberOfRings());
        assertEquals("1234", ((BroadsoftCallForwardingNoAnswerServiceSettings) broadsoftBaseServiceSettings).getForwardToPhoneNumber());
    }

    @Test
    public void getBroadsoftBaseServiceSettings_callForwardingNoAnswerSettingsNullValuesInput_returnsCorrectValues() {
        ServiceSettings serviceSettings = new ServiceSettings(Enums.Service.TYPE_CALL_FORWARDING_NO_ANSWER, "", null, null, null, null, null, null, null, null, null, null);

        BroadsoftBaseServiceSettings broadsoftBaseServiceSettings = BroadsoftUtil.getBroadsoftBaseServiceSettings(serviceSettings);

        assertThat(broadsoftBaseServiceSettings, instanceOf(BroadsoftCallForwardingNoAnswerServiceSettings.class));
        assertNull(((BroadsoftCallForwardingNoAnswerServiceSettings) broadsoftBaseServiceSettings).getActive());
        assertNull(((BroadsoftCallForwardingNoAnswerServiceSettings) broadsoftBaseServiceSettings).getNumberOfRings());
        assertNull(((BroadsoftCallForwardingNoAnswerServiceSettings) broadsoftBaseServiceSettings).getForwardToPhoneNumber());
    }

    @Test
    public void getBroadsoftBaseServiceSettings_callForwardingNotReachableSettingsInput_returnsCorrectValues() {
        ServiceSettings serviceSettings = new ServiceSettings(Enums.Service.TYPE_CALL_FORWARDING_NOT_REACHABLE, "", true, null, null, null, "1234", null, null, null, null, null);

        BroadsoftBaseServiceSettings broadsoftBaseServiceSettings = BroadsoftUtil.getBroadsoftBaseServiceSettings(serviceSettings);

        assertThat(broadsoftBaseServiceSettings, instanceOf(BroadsoftCallForwardingNotReachableServiceSettings.class));
        assertTrue(((BroadsoftCallForwardingNotReachableServiceSettings) broadsoftBaseServiceSettings).getActive());
        assertEquals("1234", ((BroadsoftCallForwardingNotReachableServiceSettings) broadsoftBaseServiceSettings).getForwardToPhoneNumber());
    }

    @Test
    public void getBroadsoftBaseServiceSettings_callForwardingNotReachableSettingsNullValuesInput_returnsCorrectValues() {
        ServiceSettings serviceSettings = new ServiceSettings(Enums.Service.TYPE_CALL_FORWARDING_NOT_REACHABLE, "", null, null, null, null, null, null, null, null, null, null);

        BroadsoftBaseServiceSettings broadsoftBaseServiceSettings = BroadsoftUtil.getBroadsoftBaseServiceSettings(serviceSettings);

        assertThat(broadsoftBaseServiceSettings, instanceOf(BroadsoftCallForwardingNotReachableServiceSettings.class));
        assertNull(((BroadsoftCallForwardingNotReachableServiceSettings) broadsoftBaseServiceSettings).getActive());
        assertNull(((BroadsoftCallForwardingNotReachableServiceSettings) broadsoftBaseServiceSettings).getForwardToPhoneNumber());
    }

    @Test
    public void getBroadsoftBaseServiceSettings_remoteOfficeSettingsInput_returnsCorrectValues() {
        ServiceSettings serviceSettings = new ServiceSettings(Enums.Service.TYPE_REMOTE_OFFICE, "", true, null, null, "0987", null, null, null, null, null, null);

        BroadsoftBaseServiceSettings broadsoftBaseServiceSettings = BroadsoftUtil.getBroadsoftBaseServiceSettings(serviceSettings);

        assertThat(broadsoftBaseServiceSettings, instanceOf(BroadsoftRemoteOfficeServiceSettings.class));
        assertTrue(((BroadsoftRemoteOfficeServiceSettings) broadsoftBaseServiceSettings).getActive());
        assertEquals("0987", ((BroadsoftRemoteOfficeServiceSettings) broadsoftBaseServiceSettings).getRemoteOfficeNumber());
    }

    @Test
    public void getBroadsoftBaseServiceSettings_remoteOfficeSettingsNullValuesInput_returnsCorrectValues() {
        ServiceSettings serviceSettings = new ServiceSettings(Enums.Service.TYPE_REMOTE_OFFICE, "", null, null, null, null, null, null, null, null, null, null);

        BroadsoftBaseServiceSettings broadsoftBaseServiceSettings = BroadsoftUtil.getBroadsoftBaseServiceSettings(serviceSettings);

        assertThat(broadsoftBaseServiceSettings, instanceOf(BroadsoftRemoteOfficeServiceSettings.class));
        assertNull(((BroadsoftRemoteOfficeServiceSettings) broadsoftBaseServiceSettings).getActive());
        assertNull(((BroadsoftRemoteOfficeServiceSettings) broadsoftBaseServiceSettings).getRemoteOfficeNumber());
    }

    @Test
    public void getBroadsoftBaseServiceSettings_callerIdBlockingSettingsInput_returnsCorrectValues() {
        ServiceSettings serviceSettings = new ServiceSettings(Enums.Service.TYPE_CALLING_LINE_ID_DELIVERY_BLOCKING, "", true, null, null, null, null, null, null, null, null, null);

        BroadsoftBaseServiceSettings broadsoftBaseServiceSettings = BroadsoftUtil.getBroadsoftBaseServiceSettings(serviceSettings);

        assertThat(broadsoftBaseServiceSettings, instanceOf(BroadsoftCallingIdDeliveryBlockingServiceSettings.class));
        assertTrue(((BroadsoftCallingIdDeliveryBlockingServiceSettings) broadsoftBaseServiceSettings).getActive());
    }

    @Test
    public void getBroadsoftBaseServiceSettings_callerIdBlockingSettingsNullValuesInput_returnsCorrectValues() {
        ServiceSettings serviceSettings = new ServiceSettings(Enums.Service.TYPE_CALLING_LINE_ID_DELIVERY_BLOCKING, "", null, null, null, null, null, null, null, null, null, null);

        BroadsoftBaseServiceSettings broadsoftBaseServiceSettings = BroadsoftUtil.getBroadsoftBaseServiceSettings(serviceSettings);

        assertThat(broadsoftBaseServiceSettings, instanceOf(BroadsoftCallingIdDeliveryBlockingServiceSettings.class));
        assertNull(((BroadsoftCallingIdDeliveryBlockingServiceSettings) broadsoftBaseServiceSettings).getActive());
    }

    @Test
    public void getBroadsoftBaseServiceSettings_broadworksAnywhereSettingsInput_returnsCorrectValues() {
        ArrayList<NextivaAnywhereLocation> locationsList = new ArrayList<NextivaAnywhereLocation>() {{
            add(new NextivaAnywhereLocation("1111", "description1", true, false, false, false));
            add(new NextivaAnywhereLocation("2222", "description2", false, true, false, false));
            add(new NextivaAnywhereLocation("3333", "description3", false, false, true, false));
            add(new NextivaAnywhereLocation("4444", "description4", false, false, false, true));
            add(new NextivaAnywhereLocation("5555", null, false, false, false, false));
        }};

        ServiceSettings serviceSettings = new ServiceSettings(Enums.Service.TYPE_BROADWORKS_ANYWHERE, "", null, null, null, null, null, true, false, locationsList, null, null);

        BroadsoftBaseServiceSettings broadsoftBaseServiceSettings = BroadsoftUtil.getBroadsoftBaseServiceSettings(serviceSettings);

        assertThat(broadsoftBaseServiceSettings, instanceOf(BroadsoftBroadWorksAnywhereServiceSettings.class));
        assertTrue(((BroadsoftBroadWorksAnywhereServiceSettings) broadsoftBaseServiceSettings).getAlertForClickToDialCalls());
        assertFalse(((BroadsoftBroadWorksAnywhereServiceSettings) broadsoftBaseServiceSettings).getAlertForGroupPagingCalls());

        assertEquals(5, ((BroadsoftBroadWorksAnywhereServiceSettings) broadsoftBaseServiceSettings).getBroadsoftBroadWorksAnywhereLocationsList().size());

        assertEquals("1111", ((BroadsoftBroadWorksAnywhereServiceSettings) broadsoftBaseServiceSettings).getBroadsoftBroadWorksAnywhereLocationsList().get(0).getPhoneNumber());
        assertEquals("description1", ((BroadsoftBroadWorksAnywhereServiceSettings) broadsoftBaseServiceSettings).getBroadsoftBroadWorksAnywhereLocationsList().get(0).getDescription());
        assertTrue(((BroadsoftBroadWorksAnywhereServiceSettings) broadsoftBaseServiceSettings).getBroadsoftBroadWorksAnywhereLocationsList().get(0).getActive());
        assertFalse(((BroadsoftBroadWorksAnywhereServiceSettings) broadsoftBaseServiceSettings).getBroadsoftBroadWorksAnywhereLocationsList().get(0).getCallControlEnabled());
        assertFalse(((BroadsoftBroadWorksAnywhereServiceSettings) broadsoftBaseServiceSettings).getBroadsoftBroadWorksAnywhereLocationsList().get(0).getPreventDivertingCalls());
        assertFalse(((BroadsoftBroadWorksAnywhereServiceSettings) broadsoftBaseServiceSettings).getBroadsoftBroadWorksAnywhereLocationsList().get(0).getAnswerConfirmationRequired());

        assertEquals("2222", ((BroadsoftBroadWorksAnywhereServiceSettings) broadsoftBaseServiceSettings).getBroadsoftBroadWorksAnywhereLocationsList().get(1).getPhoneNumber());
        assertEquals("description2", ((BroadsoftBroadWorksAnywhereServiceSettings) broadsoftBaseServiceSettings).getBroadsoftBroadWorksAnywhereLocationsList().get(1).getDescription());
        assertFalse(((BroadsoftBroadWorksAnywhereServiceSettings) broadsoftBaseServiceSettings).getBroadsoftBroadWorksAnywhereLocationsList().get(1).getActive());
        assertTrue(((BroadsoftBroadWorksAnywhereServiceSettings) broadsoftBaseServiceSettings).getBroadsoftBroadWorksAnywhereLocationsList().get(1).getCallControlEnabled());
        assertFalse(((BroadsoftBroadWorksAnywhereServiceSettings) broadsoftBaseServiceSettings).getBroadsoftBroadWorksAnywhereLocationsList().get(1).getPreventDivertingCalls());
        assertFalse(((BroadsoftBroadWorksAnywhereServiceSettings) broadsoftBaseServiceSettings).getBroadsoftBroadWorksAnywhereLocationsList().get(1).getAnswerConfirmationRequired());

        assertEquals("3333", ((BroadsoftBroadWorksAnywhereServiceSettings) broadsoftBaseServiceSettings).getBroadsoftBroadWorksAnywhereLocationsList().get(2).getPhoneNumber());
        assertEquals("description3", ((BroadsoftBroadWorksAnywhereServiceSettings) broadsoftBaseServiceSettings).getBroadsoftBroadWorksAnywhereLocationsList().get(2).getDescription());
        assertFalse(((BroadsoftBroadWorksAnywhereServiceSettings) broadsoftBaseServiceSettings).getBroadsoftBroadWorksAnywhereLocationsList().get(2).getActive());
        assertFalse(((BroadsoftBroadWorksAnywhereServiceSettings) broadsoftBaseServiceSettings).getBroadsoftBroadWorksAnywhereLocationsList().get(2).getCallControlEnabled());
        assertTrue(((BroadsoftBroadWorksAnywhereServiceSettings) broadsoftBaseServiceSettings).getBroadsoftBroadWorksAnywhereLocationsList().get(2).getPreventDivertingCalls());
        assertFalse(((BroadsoftBroadWorksAnywhereServiceSettings) broadsoftBaseServiceSettings).getBroadsoftBroadWorksAnywhereLocationsList().get(2).getAnswerConfirmationRequired());

        assertEquals("4444", ((BroadsoftBroadWorksAnywhereServiceSettings) broadsoftBaseServiceSettings).getBroadsoftBroadWorksAnywhereLocationsList().get(3).getPhoneNumber());
        assertEquals("description4", ((BroadsoftBroadWorksAnywhereServiceSettings) broadsoftBaseServiceSettings).getBroadsoftBroadWorksAnywhereLocationsList().get(3).getDescription());
        assertFalse(((BroadsoftBroadWorksAnywhereServiceSettings) broadsoftBaseServiceSettings).getBroadsoftBroadWorksAnywhereLocationsList().get(3).getActive());
        assertFalse(((BroadsoftBroadWorksAnywhereServiceSettings) broadsoftBaseServiceSettings).getBroadsoftBroadWorksAnywhereLocationsList().get(3).getCallControlEnabled());
        assertFalse(((BroadsoftBroadWorksAnywhereServiceSettings) broadsoftBaseServiceSettings).getBroadsoftBroadWorksAnywhereLocationsList().get(3).getPreventDivertingCalls());
        assertTrue(((BroadsoftBroadWorksAnywhereServiceSettings) broadsoftBaseServiceSettings).getBroadsoftBroadWorksAnywhereLocationsList().get(3).getAnswerConfirmationRequired());

        assertEquals("5555", ((BroadsoftBroadWorksAnywhereServiceSettings) broadsoftBaseServiceSettings).getBroadsoftBroadWorksAnywhereLocationsList().get(4).getPhoneNumber());
        assertNull(((BroadsoftBroadWorksAnywhereServiceSettings) broadsoftBaseServiceSettings).getBroadsoftBroadWorksAnywhereLocationsList().get(4).getDescription());
        assertFalse(((BroadsoftBroadWorksAnywhereServiceSettings) broadsoftBaseServiceSettings).getBroadsoftBroadWorksAnywhereLocationsList().get(4).getActive());
        assertFalse(((BroadsoftBroadWorksAnywhereServiceSettings) broadsoftBaseServiceSettings).getBroadsoftBroadWorksAnywhereLocationsList().get(4).getCallControlEnabled());
        assertFalse(((BroadsoftBroadWorksAnywhereServiceSettings) broadsoftBaseServiceSettings).getBroadsoftBroadWorksAnywhereLocationsList().get(4).getPreventDivertingCalls());
        assertFalse(((BroadsoftBroadWorksAnywhereServiceSettings) broadsoftBaseServiceSettings).getBroadsoftBroadWorksAnywhereLocationsList().get(4).getAnswerConfirmationRequired());
    }

    @Test
    public void getBroadsoftBaseServiceSettings_broadworksAnywhereSettingsNullValuesInput_returnsCorrectValues() {
        ServiceSettings serviceSettings = new ServiceSettings(Enums.Service.TYPE_BROADWORKS_ANYWHERE, "", null, null, null, null, null, null, null, null, null, null);

        BroadsoftBaseServiceSettings broadsoftBaseServiceSettings = BroadsoftUtil.getBroadsoftBaseServiceSettings(serviceSettings);

        assertThat(broadsoftBaseServiceSettings, instanceOf(BroadsoftBroadWorksAnywhereServiceSettings.class));
        assertNull(((BroadsoftBroadWorksAnywhereServiceSettings) broadsoftBaseServiceSettings).getAlertForClickToDialCalls());
        assertNull(((BroadsoftBroadWorksAnywhereServiceSettings) broadsoftBaseServiceSettings).getAlertForGroupPagingCalls());
        assertNull(((BroadsoftBroadWorksAnywhereServiceSettings) broadsoftBaseServiceSettings).getBroadsoftBroadWorksAnywhereLocationsList());
    }

    @Test
    public void getBroadsoftBaseServiceSettings_simultaneousRingSettingsInput_ringIncomingCalls_returnsCorrectValues() {
        ArrayList<SimultaneousRingLocation> simultaneousRingLocationsList = new ArrayList<SimultaneousRingLocation>() {{
            add(new SimultaneousRingLocation("1111", null));
            add(new SimultaneousRingLocation("2222", true));
            add(new SimultaneousRingLocation("3333", false));
            add(new SimultaneousRingLocation(null, true));
            add(new SimultaneousRingLocation(null, false));
            add(new SimultaneousRingLocation(null, null));
        }};

        ServiceSettings serviceSettings = new ServiceSettings(Enums.Service.TYPE_SIMULTANEOUS_RING_PERSONAL, "", null, null, null, null, null, null, null, null, false, simultaneousRingLocationsList);

        BroadsoftBaseServiceSettings broadsoftBaseServiceSettings = BroadsoftUtil.getBroadsoftBaseServiceSettings(serviceSettings);

        assertThat(broadsoftBaseServiceSettings, instanceOf(BroadsoftSimultaneousRingPersonalServiceSettings.class));
        assertEquals("Ring for all Incoming Calls", ((BroadsoftSimultaneousRingPersonalServiceSettings) broadsoftBaseServiceSettings).getIncomingCalls());

        assertEquals(6, ((BroadsoftSimultaneousRingPersonalServiceSettings) broadsoftBaseServiceSettings).getSimultaneousRingLocations().getSimultaneousRingLocations().size());

        BroadsoftSimultaneousRingLocation location1 = ((BroadsoftSimultaneousRingPersonalServiceSettings) broadsoftBaseServiceSettings).getSimultaneousRingLocations().getSimultaneousRingLocations().get(0);
        assertEquals("1111", location1.getAddress());
        assertNull(location1.getAnswerConfirmationRequired());

        BroadsoftSimultaneousRingLocation location2 = ((BroadsoftSimultaneousRingPersonalServiceSettings) broadsoftBaseServiceSettings).getSimultaneousRingLocations().getSimultaneousRingLocations().get(1);
        assertEquals("2222", location2.getAddress());
        assertTrue(location2.getAnswerConfirmationRequired());

        BroadsoftSimultaneousRingLocation location3 = ((BroadsoftSimultaneousRingPersonalServiceSettings) broadsoftBaseServiceSettings).getSimultaneousRingLocations().getSimultaneousRingLocations().get(2);
        assertEquals("3333", location3.getAddress());
        assertFalse(location3.getAnswerConfirmationRequired());

        BroadsoftSimultaneousRingLocation location4 = ((BroadsoftSimultaneousRingPersonalServiceSettings) broadsoftBaseServiceSettings).getSimultaneousRingLocations().getSimultaneousRingLocations().get(3);
        assertNull(location4.getAddress());
        assertTrue(location4.getAnswerConfirmationRequired());

        BroadsoftSimultaneousRingLocation location5 = ((BroadsoftSimultaneousRingPersonalServiceSettings) broadsoftBaseServiceSettings).getSimultaneousRingLocations().getSimultaneousRingLocations().get(4);
        assertNull(location5.getAddress());
        assertFalse(location5.getAnswerConfirmationRequired());

        BroadsoftSimultaneousRingLocation location6 = ((BroadsoftSimultaneousRingPersonalServiceSettings) broadsoftBaseServiceSettings).getSimultaneousRingLocations().getSimultaneousRingLocations().get(5);
        assertNull(location6.getAddress());
        assertNull(location6.getAnswerConfirmationRequired());
    }

    @Test
    public void getBroadsoftBaseServiceSettings_simultaneousRingSettingsInput_dontRingIncomingCalls_returnsCorrectValues() {
        ArrayList<SimultaneousRingLocation> simultaneousRingLocationsList = new ArrayList<SimultaneousRingLocation>() {{
            add(new SimultaneousRingLocation("1111", null));
            add(new SimultaneousRingLocation("2222", true));
            add(new SimultaneousRingLocation("3333", false));
            add(new SimultaneousRingLocation(null, true));
            add(new SimultaneousRingLocation(null, false));
            add(new SimultaneousRingLocation(null, null));
        }};

        ServiceSettings serviceSettings = new ServiceSettings(Enums.Service.TYPE_SIMULTANEOUS_RING_PERSONAL, "", null, null, null, null, null, null, null, null, true, simultaneousRingLocationsList);

        BroadsoftBaseServiceSettings broadsoftBaseServiceSettings = BroadsoftUtil.getBroadsoftBaseServiceSettings(serviceSettings);

        assertThat(broadsoftBaseServiceSettings, instanceOf(BroadsoftSimultaneousRingPersonalServiceSettings.class));
        assertEquals("Do not Ring if on a Call", ((BroadsoftSimultaneousRingPersonalServiceSettings) broadsoftBaseServiceSettings).getIncomingCalls());

        assertEquals(6, ((BroadsoftSimultaneousRingPersonalServiceSettings) broadsoftBaseServiceSettings).getSimultaneousRingLocations().getSimultaneousRingLocations().size());

        BroadsoftSimultaneousRingLocation location1 = ((BroadsoftSimultaneousRingPersonalServiceSettings) broadsoftBaseServiceSettings).getSimultaneousRingLocations().getSimultaneousRingLocations().get(0);
        assertEquals("1111", location1.getAddress());
        assertNull(location1.getAnswerConfirmationRequired());

        BroadsoftSimultaneousRingLocation location2 = ((BroadsoftSimultaneousRingPersonalServiceSettings) broadsoftBaseServiceSettings).getSimultaneousRingLocations().getSimultaneousRingLocations().get(1);
        assertEquals("2222", location2.getAddress());
        assertTrue(location2.getAnswerConfirmationRequired());

        BroadsoftSimultaneousRingLocation location3 = ((BroadsoftSimultaneousRingPersonalServiceSettings) broadsoftBaseServiceSettings).getSimultaneousRingLocations().getSimultaneousRingLocations().get(2);
        assertEquals("3333", location3.getAddress());
        assertFalse(location3.getAnswerConfirmationRequired());

        BroadsoftSimultaneousRingLocation location4 = ((BroadsoftSimultaneousRingPersonalServiceSettings) broadsoftBaseServiceSettings).getSimultaneousRingLocations().getSimultaneousRingLocations().get(3);
        assertNull(location4.getAddress());
        assertTrue(location4.getAnswerConfirmationRequired());

        BroadsoftSimultaneousRingLocation location5 = ((BroadsoftSimultaneousRingPersonalServiceSettings) broadsoftBaseServiceSettings).getSimultaneousRingLocations().getSimultaneousRingLocations().get(4);
        assertNull(location5.getAddress());
        assertFalse(location5.getAnswerConfirmationRequired());

        BroadsoftSimultaneousRingLocation location6 = ((BroadsoftSimultaneousRingPersonalServiceSettings) broadsoftBaseServiceSettings).getSimultaneousRingLocations().getSimultaneousRingLocations().get(5);
        assertNull(location6.getAddress());
        assertNull(location6.getAnswerConfirmationRequired());
    }

    @Test
    public void getBroadsoftBaseServiceSettings_simultaneousRingSettingsNullValuesInput_returnsCorrectValues() {
        ServiceSettings serviceSettings = new ServiceSettings(Enums.Service.TYPE_SIMULTANEOUS_RING_PERSONAL, "", null, null, null, null, null, null, null, null, null, null);

        BroadsoftBaseServiceSettings broadsoftBaseServiceSettings = BroadsoftUtil.getBroadsoftBaseServiceSettings(serviceSettings);

        assertThat(broadsoftBaseServiceSettings, instanceOf(BroadsoftSimultaneousRingPersonalServiceSettings.class));
        assertNull(((BroadsoftSimultaneousRingPersonalServiceSettings) broadsoftBaseServiceSettings).getIncomingCalls());
        assertNull(((BroadsoftSimultaneousRingPersonalServiceSettings) broadsoftBaseServiceSettings).getSimultaneousRingLocations().getSimultaneousRingLocations());
    }

    @Test
    public void getNextivaAnywhereLocation_completeInput_returnsCorrectValues() {
        BroadsoftBroadWorksAnywhereLocationBody broadsoftBroadWorksAnywhereLocationBody = new BroadsoftBroadWorksAnywhereLocationBody("0987", "description", true, false, true, false);

        NextivaAnywhereLocation nextivaAnywhereLocation = BroadsoftUtil.getNextivaAnywhereLocation(broadsoftBroadWorksAnywhereLocationBody, "description");

        assertEquals("0987", nextivaAnywhereLocation.getPhoneNumber());
        assertEquals("description", nextivaAnywhereLocation.getDescription());
        assertTrue(nextivaAnywhereLocation.getActiveRaw());
        assertFalse(nextivaAnywhereLocation.getCallControlEnabledRaw());
        assertTrue(nextivaAnywhereLocation.getPreventDivertingCallsRaw());
        assertFalse(nextivaAnywhereLocation.getAnswerConfirmationRequiredRaw());
    }

    @Test
    public void getNextivaAnywhereLocation_nullValuesInput_returnsCorrectValues() {
        BroadsoftBroadWorksAnywhereLocationBody broadsoftBroadWorksAnywhereLocationBody = new BroadsoftBroadWorksAnywhereLocationBody(null, "unusedDescription", null, null, null, null);

        NextivaAnywhereLocation nextivaAnywhereLocation = BroadsoftUtil.getNextivaAnywhereLocation(broadsoftBroadWorksAnywhereLocationBody, null);

        assertEquals("", nextivaAnywhereLocation.getPhoneNumber());
        assertNull(nextivaAnywhereLocation.getDescription());
        assertNull(nextivaAnywhereLocation.getActiveRaw());
        assertNull(nextivaAnywhereLocation.getCallControlEnabledRaw());
        assertNull(nextivaAnywhereLocation.getPreventDivertingCallsRaw());
        assertNull(nextivaAnywhereLocation.getAnswerConfirmationRequiredRaw());
    }

    @Test
    public void getSimultaneousRingLocation_completeInput_returnsCorrectValues() {
        BroadsoftSimultaneousRingLocation broadsoftSimultaneousRingLocation = new BroadsoftSimultaneousRingLocation("0987", true);

        SimultaneousRingLocation simultaneousRingLocation = BroadsoftUtil.getSimultaneousRingLocation(broadsoftSimultaneousRingLocation);

        assertEquals("0987", simultaneousRingLocation.getPhoneNumber());
        assertTrue(simultaneousRingLocation.getAnswerConfirmationRequiredRaw());
    }

    @Test
    public void getSimultaneousRingLocation_nullValuesInput_returnsCorrectValues() {
        BroadsoftSimultaneousRingLocation broadsoftSimultaneousRingLocation = new BroadsoftSimultaneousRingLocation(null, null);

        SimultaneousRingLocation simultaneousRingLocation = BroadsoftUtil.getSimultaneousRingLocation(broadsoftSimultaneousRingLocation);

        assertNull(simultaneousRingLocation.getPhoneNumber());
        assertNull(simultaneousRingLocation.getAnswerConfirmationRequiredRaw());
    }

    @Test
    public void getNextivaContact_validInput_returnsCorrectValues() {
        BroadsoftEnterpriseAdditionalDetails additionalDetails = new BroadsoftEnterpriseAdditionalDetails("jim@smith.com", "jim.smith@fake.im", "2132132132");

        BroadsoftEnterpriseDirectoryDetails directoryDetails = new BroadsoftEnterpriseDirectoryDetails(
                "userId",
                "Jim",
                "Smith",
                "123456789",
                "0987",
                additionalDetails);

        NextivaContact contact = BroadsoftUtil.getNextivaContact(directoryDetails);

        assertEquals("userId", contact.getUserId());
        assertEquals(Enums.Contacts.ContactTypes.ENTERPRISE, contact.getContactType().intValue());
        assertEquals("Jim", contact.getFirstName());
        assertEquals("Smith", contact.getLastName());
        assertEquals("jim.smith@fake.im", contact.getJid());
        assertEquals(1, contact.getEmailAddresses().size());
        assertEquals(Enums.Contacts.EmailTypes.WORK_EMAIL, contact.getEmailAddresses().get(0).getType());
        assertEquals("jim@smith.com", contact.getEmailAddresses().get(0).getAddress());
        assertEquals(2, contact.getPhoneNumbers().size());
        assertEquals(Enums.Contacts.PhoneTypes.MOBILE_PHONE, contact.getPhoneNumbers().get(0).getType());
        assertEquals("2132132132", contact.getPhoneNumbers().get(0).getNumber());
        assertEquals(Enums.Contacts.PhoneTypes.WORK_PHONE, contact.getPhoneNumbers().get(1).getType());
        assertEquals("123456789", contact.getPhoneNumbers().get(1).getNumber());
        assertEquals(1, contact.getExtensions().size());
        assertEquals(Enums.Contacts.PhoneTypes.WORK_EXTENSION, contact.getExtensions().get(0).getType());
        assertEquals("0987", contact.getExtensions().get(0).getNumber());
    }

    @Test
    public void getNextivaContact_nullInput_returnsNull() {
        assertNull(BroadsoftUtil.getNextivaContact(null));
    }

    @Test
    public void getNextivaContact_invalidInput_returnsNull() {
        BroadsoftEnterpriseDirectoryDetails directoryDetails = new BroadsoftEnterpriseDirectoryDetails(null, null, null, null, null, null);

        assertNull(BroadsoftUtil.getNextivaContact(directoryDetails));
    }

    @Test
    public void getCallLogEntries_validInput_returnsCorrectValues() {
        ArrayList<BroadsoftCallLogEntry> missedCallsList = new ArrayList<BroadsoftCallLogEntry>() {{
            add(null);
            add(new BroadsoftCallLogEntry());
            add(new BroadsoftCallLogEntry("missed0", "Null Caller", null, "1", null));
            add(new BroadsoftCallLogEntry(null, null, "2018-01-02T13:14:15.123-07:00", null, "1112223333"));
            add(new BroadsoftCallLogEntry("missed1", "Missed Caller", "2018-02-03T14:15:16.456-07:00", "1", "2223334444"));
        }};

        ArrayList<BroadsoftCallLogEntry> placedCallsList = new ArrayList<BroadsoftCallLogEntry>() {{
            add(null);
            add(new BroadsoftCallLogEntry());
            add(new BroadsoftCallLogEntry("placed0", "Null Caller", null, "1", null));
            add(new BroadsoftCallLogEntry(null, null, "2018-03-04T15:16:17.789-07:00", null, "3334445555"));
            add(new BroadsoftCallLogEntry("placed1", "Placed Caller", "2018-04-05T16:17:18.012-07:00", "2", "4445556666"));
        }};

        ArrayList<BroadsoftCallLogEntry> receivedCallsList = new ArrayList<BroadsoftCallLogEntry>() {{
            add(null);
            add(new BroadsoftCallLogEntry());
            add(new BroadsoftCallLogEntry("received0", "Null Caller", null, "1", null));
            add(new BroadsoftCallLogEntry(null, null, "2018-05-06T17:18:19.345-07:00", null, "5556667777"));
            add(new BroadsoftCallLogEntry("received1", "Received Caller", "2018-06-07T18:19:20.345-07:00", "3", "6667778888"));
        }};

        BroadsoftAllCallLogsResponse callLogsResponse = new BroadsoftAllCallLogsResponse(placedCallsList, missedCallsList, receivedCallsList);
        ArrayList<CallLogEntry> callLogEntriesList = BroadsoftUtil.getCallLogEntries(callLogsResponse);
        assertEquals(6, callLogEntriesList.size());

        CallLogEntry callLogEntry1 = callLogEntriesList.get(0);
        assertEquals("received1", callLogEntry1.getCallLogId());
        assertEquals("Received Caller", callLogEntry1.getDisplayName());
        assertEquals(1528420760L, callLogEntry1.getCallInstant().getEpochSecond());
        assertEquals("3", callLogEntry1.getCountryCode());
        assertEquals("6667778888", callLogEntry1.getPhoneNumber());
        assertEquals(Enums.Calls.CallTypes.RECEIVED, callLogEntry1.getCallType());

        CallLogEntry callLogEntry2 = callLogEntriesList.get(1);
        assertNull(callLogEntry2.getCallLogId());
        assertNull(callLogEntry2.getDisplayName());
        assertEquals(1525652299L, callLogEntry2.getCallInstant().getEpochSecond());
        assertNull(callLogEntry2.getCountryCode());
        assertEquals("5556667777", callLogEntry2.getPhoneNumber());
        assertEquals(Enums.Calls.CallTypes.RECEIVED, callLogEntry2.getCallType());

        CallLogEntry callLogEntry3 = callLogEntriesList.get(2);
        assertEquals("placed1", callLogEntry3.getCallLogId());
        assertEquals("Placed Caller", callLogEntry3.getDisplayName());
        assertEquals(1522970238L, callLogEntry3.getCallInstant().getEpochSecond());
        assertEquals("2", callLogEntry3.getCountryCode());
        assertEquals("4445556666", callLogEntry3.getPhoneNumber());
        assertEquals(Enums.Calls.CallTypes.PLACED, callLogEntry3.getCallType());

        CallLogEntry callLogEntry4 = callLogEntriesList.get(3);
        assertNull(callLogEntry4.getCallLogId());
        assertNull(callLogEntry4.getDisplayName());
        assertEquals(1520201777L, callLogEntry4.getCallInstant().getEpochSecond());
        assertNull(callLogEntry4.getCountryCode());
        assertEquals("3334445555", callLogEntry4.getPhoneNumber());
        assertEquals(Enums.Calls.CallTypes.PLACED, callLogEntry4.getCallType());

        CallLogEntry callLogEntry5 = callLogEntriesList.get(4);
        assertEquals("missed1", callLogEntry5.getCallLogId());
        assertEquals("Missed Caller", callLogEntry5.getDisplayName());
        assertEquals(1517692516L, callLogEntry5.getCallInstant().getEpochSecond());
        assertEquals("1", callLogEntry5.getCountryCode());
        assertEquals("2223334444", callLogEntry5.getPhoneNumber());
        assertEquals(Enums.Calls.CallTypes.MISSED, callLogEntry5.getCallType());

        CallLogEntry callLogEntry6 = callLogEntriesList.get(5);
        assertNull(callLogEntry6.getCallLogId());
        assertNull(callLogEntry6.getDisplayName());
        assertEquals(1514924055L, callLogEntry6.getCallInstant().getEpochSecond());
        assertNull(callLogEntry6.getCountryCode());
        assertEquals("1112223333", callLogEntry6.getPhoneNumber());
        assertEquals(Enums.Calls.CallTypes.MISSED, callLogEntry6.getCallType());
    }

    @Test
    public void getCallLogEntries_invalidInput_returnsEmptyArray() {
        BroadsoftAllCallLogsResponse callLogsResponse = new BroadsoftAllCallLogsResponse();

        assertEquals(0, BroadsoftUtil.getCallLogEntries(callLogsResponse).size());
    }

    @Test
    public void processChatConversations_validInput_processesValues() {
        BroadsoftUmsChatMessage[] chatMessages = new BroadsoftUmsChatMessage[] {
                new BroadsoftUmsChatMessage(),
                new BroadsoftUmsChatMessage(null, null, null, null, null, null, null, null, null, "", "", "", null),
                new BroadsoftUmsChatMessage(null, "user1", null, null, null, null, null, null, null, "", "", "", null),
                new BroadsoftUmsChatMessage(null, "user2", "user5", null, null, null, null, null, null, "", "", "", null),
                new BroadsoftUmsChatMessage(null, "user3", "user4", Enums.Chats.ConversationTypes.GROUP_ALERT, null, null, false, null, null, "", "", "", null),
                new BroadsoftUmsChatMessage(null, "user4", "user3", Enums.Chats.ConversationTypes.GROUP_ALERT, "body04", null, false, null, null, "", "", "", null),
                new BroadsoftUmsChatMessage(null, "user5", "user2", Enums.Chats.ConversationTypes.CHAT, "body05", null, false, null, 1522273911427L, "", "", "", null),
                new BroadsoftUmsChatMessage("message06", "user1", "user2", Enums.Chats.ConversationTypes.CHAT, "body06", null, false, true, 1522273811427L, "", "", "", null),
                new BroadsoftUmsChatMessage("message07", "user2", "user1", Enums.Chats.ConversationTypes.CHAT, "body07", null, true, null, 1522273711427L, "", "", "", null),
                new BroadsoftUmsChatMessage("message08", "user1", "user2", Enums.Chats.ConversationTypes.CHAT, "body08", null, false, true, 1522273611427L, "", "", "", null),
                new BroadsoftUmsChatMessage("message09", "user3", "user4", Enums.Chats.ConversationTypes.CHAT, "body09", null, false, null, 1522273511427L, "", "", "", null),
                new BroadsoftUmsChatMessage("message10", "user4", "user5", Enums.Chats.ConversationTypes.CHAT, "body10", null, true, true, 1522273411427L, "", "", "", null),
                new BroadsoftUmsChatMessage("message11", "user5", "user4", Enums.Chats.ConversationTypes.CHAT, "body11", null, false, false, 1522273311427L, "", "", "", null),
                new BroadsoftUmsChatMessage("message12", "user6", "user7", Enums.Chats.ConversationTypes.GROUP_CHAT, "body12", null, true, true, 1522273211427L, "", "", "", null),
                new BroadsoftUmsChatMessage("message13", "user6", "user7", Enums.Chats.ConversationTypes.GROUP_CHAT, "body13", null, true, null, 1522273111427L, "", "Guest First", "", null),
                new BroadsoftUmsChatMessage("message14", "user7", "user6", Enums.Chats.ConversationTypes.GROUP_CHAT, "body14", null, false, true, 1522273011427L, "user6", "", "Guest Last", null),
                new BroadsoftUmsChatMessage("message15", "user8", "user9", Enums.Chats.ConversationTypes.GROUP_CHAT, "body15", null, false, null, 1522272911427L, "user9", "", "", null),
                new BroadsoftUmsChatMessage("message16", "user8", "user9", Enums.Chats.ConversationTypes.GROUP_CHAT, "body16", null, false, true, 1522272811427L, "user9", "", "", null),
                new BroadsoftUmsChatMessage("message17", "user9", "user8", Enums.Chats.ConversationTypes.GROUP_CHAT, "body17", null, true, false, 1522272711427L, "", "Guest First", "Guest Last", null),
                new BroadsoftUmsChatMessage("message18", "user8", "user9", Enums.Chats.ConversationTypes.GROUP_CHAT, "body18", null, false, true, 1522272611427L, "user9", "", "", null),
        };

        BroadsoftUmsChatMessagesResponse broadsoftUmsChatMessagesResponse = new BroadsoftUmsChatMessagesResponse(chatMessages);
        HashMap<String, ChatConversation> chatConversationHashMap = new HashMap<>();

        BroadsoftUtil.processChatConversations(broadsoftUmsChatMessagesResponse, chatConversationHashMap);

        assertEquals(4, chatConversationHashMap.size());

        ChatConversation user2Conversation = chatConversationHashMap.get("user2");
        assertNotNull(user2Conversation);
        assertEquals(4, user2Conversation.getChatMessagesList().size());
        assertNull(user2Conversation.getChatMessagesList().get(0).getMessageId());
        assertEquals("user5", user2Conversation.getChatMessagesList().get(0).getTo());
        assertEquals("user2", user2Conversation.getChatMessagesList().get(0).getFrom());
        assertEquals(Enums.Chats.ConversationTypes.CHAT, user2Conversation.getChatMessagesList().get(0).getType());
        assertEquals("body05", user2Conversation.getChatMessagesList().get(0).getBody());
        assertFalse(user2Conversation.getChatMessagesList().get(0).isSender());
        assertFalse(user2Conversation.getChatMessagesList().get(0).isRead());
        assertEquals((Long) 1522273911427L, user2Conversation.getChatMessagesList().get(0).getTimestamp());
        assertEquals("", user2Conversation.getChatMessagesList().get(0).getParticipant());
        assertEquals("", user2Conversation.getChatMessagesList().get(0).getGuestFirstName());
        assertEquals("", user2Conversation.getChatMessagesList().get(0).getGuestLastName());

        assertEquals("message06", user2Conversation.getChatMessagesList().get(1).getMessageId());
        assertEquals("user1", user2Conversation.getChatMessagesList().get(1).getTo());
        assertEquals("user2", user2Conversation.getChatMessagesList().get(1).getFrom());
        assertEquals(Enums.Chats.ConversationTypes.CHAT, user2Conversation.getChatMessagesList().get(1).getType());
        assertEquals("body06", user2Conversation.getChatMessagesList().get(1).getBody());
        assertFalse(user2Conversation.getChatMessagesList().get(1).isSender());
        assertTrue(user2Conversation.getChatMessagesList().get(1).isRead());
        assertEquals((Long) 1522273811427L, user2Conversation.getChatMessagesList().get(1).getTimestamp());
        assertEquals("", user2Conversation.getChatMessagesList().get(1).getParticipant());
        assertEquals("", user2Conversation.getChatMessagesList().get(1).getGuestFirstName());
        assertEquals("", user2Conversation.getChatMessagesList().get(1).getGuestLastName());

        assertEquals("message07", user2Conversation.getChatMessagesList().get(2).getMessageId());
        assertEquals("user2", user2Conversation.getChatMessagesList().get(2).getTo());
        assertEquals("user1", user2Conversation.getChatMessagesList().get(2).getFrom());
        assertEquals(Enums.Chats.ConversationTypes.CHAT, user2Conversation.getChatMessagesList().get(2).getType());
        assertEquals("body07", user2Conversation.getChatMessagesList().get(2).getBody());
        assertTrue(user2Conversation.getChatMessagesList().get(2).isSender());
        assertFalse(user2Conversation.getChatMessagesList().get(2).isRead());
        assertEquals((Long) 1522273711427L, user2Conversation.getChatMessagesList().get(2).getTimestamp());
        assertEquals("", user2Conversation.getChatMessagesList().get(2).getParticipant());
        assertEquals("", user2Conversation.getChatMessagesList().get(2).getGuestFirstName());
        assertEquals("", user2Conversation.getChatMessagesList().get(2).getGuestLastName());

        assertEquals("message08", user2Conversation.getChatMessagesList().get(3).getMessageId());
        assertEquals("user1", user2Conversation.getChatMessagesList().get(3).getTo());
        assertEquals("user2", user2Conversation.getChatMessagesList().get(3).getFrom());
        assertEquals(Enums.Chats.ConversationTypes.CHAT, user2Conversation.getChatMessagesList().get(3).getType());
        assertEquals("body08", user2Conversation.getChatMessagesList().get(3).getBody());
        assertFalse(user2Conversation.getChatMessagesList().get(3).isSender());
        assertTrue(user2Conversation.getChatMessagesList().get(3).isRead());
        assertEquals((Long) 1522273611427L, user2Conversation.getChatMessagesList().get(3).getTimestamp());
        assertEquals("", user2Conversation.getChatMessagesList().get(3).getParticipant());
        assertEquals("", user2Conversation.getChatMessagesList().get(3).getGuestFirstName());
        assertEquals("", user2Conversation.getChatMessagesList().get(3).getGuestLastName());

        ChatConversation user4Conversation = chatConversationHashMap.get("user4");
        assertNotNull(user4Conversation);
        assertEquals(3, user4Conversation.getChatMessagesList().size());
        assertEquals("message09", user4Conversation.getChatMessagesList().get(0).getMessageId());
        assertEquals("user3", user4Conversation.getChatMessagesList().get(0).getTo());
        assertEquals("user4", user4Conversation.getChatMessagesList().get(0).getFrom());
        assertEquals(Enums.Chats.ConversationTypes.CHAT, user4Conversation.getChatMessagesList().get(0).getType());
        assertEquals("body09", user4Conversation.getChatMessagesList().get(0).getBody());
        assertFalse(user4Conversation.getChatMessagesList().get(0).isSender());
        assertFalse(user4Conversation.getChatMessagesList().get(0).isRead());
        assertEquals((Long) 1522273511427L, user4Conversation.getChatMessagesList().get(0).getTimestamp());
        assertEquals("", user4Conversation.getChatMessagesList().get(0).getParticipant());
        assertEquals("", user4Conversation.getChatMessagesList().get(0).getGuestFirstName());
        assertEquals("", user4Conversation.getChatMessagesList().get(0).getGuestLastName());

        assertEquals("message10", user4Conversation.getChatMessagesList().get(1).getMessageId());
        assertEquals("user4", user4Conversation.getChatMessagesList().get(1).getTo());
        assertEquals("user5", user4Conversation.getChatMessagesList().get(1).getFrom());
        assertEquals(Enums.Chats.ConversationTypes.CHAT, user4Conversation.getChatMessagesList().get(1).getType());
        assertEquals("body10", user4Conversation.getChatMessagesList().get(1).getBody());
        assertTrue(user4Conversation.getChatMessagesList().get(1).isSender());
        assertTrue(user4Conversation.getChatMessagesList().get(1).isRead());
        assertEquals((Long) 1522273411427L, user4Conversation.getChatMessagesList().get(1).getTimestamp());
        assertEquals("", user4Conversation.getChatMessagesList().get(1).getParticipant());
        assertEquals("", user4Conversation.getChatMessagesList().get(1).getGuestFirstName());
        assertEquals("", user4Conversation.getChatMessagesList().get(1).getGuestLastName());

        assertEquals("message11", user4Conversation.getChatMessagesList().get(2).getMessageId());
        assertEquals("user5", user4Conversation.getChatMessagesList().get(2).getTo());
        assertEquals("user4", user4Conversation.getChatMessagesList().get(2).getFrom());
        assertEquals(Enums.Chats.ConversationTypes.CHAT, user4Conversation.getChatMessagesList().get(2).getType());
        assertEquals("body11", user4Conversation.getChatMessagesList().get(2).getBody());
        assertFalse(user4Conversation.getChatMessagesList().get(2).isSender());
        assertFalse(user4Conversation.getChatMessagesList().get(2).isRead());
        assertEquals((Long) 1522273311427L, user4Conversation.getChatMessagesList().get(2).getTimestamp());
        assertEquals("", user4Conversation.getChatMessagesList().get(2).getParticipant());
        assertEquals("", user4Conversation.getChatMessagesList().get(2).getGuestFirstName());
        assertEquals("", user4Conversation.getChatMessagesList().get(2).getGuestLastName());

        ChatConversation user6Conversation = chatConversationHashMap.get("user6");
        assertNotNull(user6Conversation);
        assertEquals(3, user6Conversation.getChatMessagesList().size());
        assertEquals("message12", user6Conversation.getChatMessagesList().get(0).getMessageId());
        assertEquals("user6", user6Conversation.getChatMessagesList().get(0).getTo());
        assertEquals("user7", user6Conversation.getChatMessagesList().get(0).getFrom());
        assertEquals(Enums.Chats.ConversationTypes.GROUP_CHAT, user6Conversation.getChatMessagesList().get(0).getType());
        assertEquals("body12", user6Conversation.getChatMessagesList().get(0).getBody());
        assertTrue(user6Conversation.getChatMessagesList().get(0).isSender());
        assertTrue(user6Conversation.getChatMessagesList().get(0).isRead());
        assertEquals((Long) 1522273211427L, user6Conversation.getChatMessagesList().get(0).getTimestamp());
        assertEquals("", user6Conversation.getChatMessagesList().get(0).getParticipant());
        assertEquals("", user6Conversation.getChatMessagesList().get(0).getGuestFirstName());
        assertEquals("", user6Conversation.getChatMessagesList().get(0).getGuestLastName());

        assertEquals("message13", user6Conversation.getChatMessagesList().get(1).getMessageId());
        assertEquals("user6", user6Conversation.getChatMessagesList().get(1).getTo());
        assertEquals("user7", user6Conversation.getChatMessagesList().get(1).getFrom());
        assertEquals(Enums.Chats.ConversationTypes.GROUP_CHAT, user6Conversation.getChatMessagesList().get(1).getType());
        assertEquals("body13", user6Conversation.getChatMessagesList().get(1).getBody());
        assertTrue(user6Conversation.getChatMessagesList().get(1).isSender());
        assertFalse(user6Conversation.getChatMessagesList().get(1).isRead());
        assertEquals((Long) 1522273111427L, user6Conversation.getChatMessagesList().get(1).getTimestamp());
        assertEquals("", user6Conversation.getChatMessagesList().get(1).getParticipant());
        assertEquals("Guest First", user6Conversation.getChatMessagesList().get(1).getGuestFirstName());
        assertEquals("", user6Conversation.getChatMessagesList().get(1).getGuestLastName());

        assertEquals("message14", user6Conversation.getChatMessagesList().get(2).getMessageId());
        assertEquals("user7", user6Conversation.getChatMessagesList().get(2).getTo());
        assertEquals("user6", user6Conversation.getChatMessagesList().get(2).getFrom());
        assertEquals(Enums.Chats.ConversationTypes.GROUP_CHAT, user6Conversation.getChatMessagesList().get(2).getType());
        assertEquals("body14", user6Conversation.getChatMessagesList().get(2).getBody());
        assertFalse(user6Conversation.getChatMessagesList().get(2).isSender());
        assertTrue(user6Conversation.getChatMessagesList().get(2).isRead());
        assertEquals((Long) 1522273011427L, user6Conversation.getChatMessagesList().get(2).getTimestamp());
        assertEquals("user6", user6Conversation.getChatMessagesList().get(2).getParticipant());
        assertEquals("", user6Conversation.getChatMessagesList().get(2).getGuestFirstName());
        assertEquals("Guest Last", user6Conversation.getChatMessagesList().get(2).getGuestLastName());

        ChatConversation user9Conversation = chatConversationHashMap.get("user9");
        assertNotNull(user9Conversation);
        assertEquals(4, user9Conversation.getChatMessagesList().size());
        assertEquals("message15", user9Conversation.getChatMessagesList().get(0).getMessageId());
        assertEquals("user8", user9Conversation.getChatMessagesList().get(0).getTo());
        assertEquals("user9", user9Conversation.getChatMessagesList().get(0).getFrom());
        assertEquals(Enums.Chats.ConversationTypes.GROUP_CHAT, user9Conversation.getChatMessagesList().get(0).getType());
        assertEquals("body15", user9Conversation.getChatMessagesList().get(0).getBody());
        assertFalse(user9Conversation.getChatMessagesList().get(0).isSender());
        assertFalse(user9Conversation.getChatMessagesList().get(0).isRead());
        assertEquals((Long) 1522272911427L, user9Conversation.getChatMessagesList().get(0).getTimestamp());
        assertEquals("user9", user9Conversation.getChatMessagesList().get(0).getParticipant());
        assertEquals("", user9Conversation.getChatMessagesList().get(0).getGuestFirstName());
        assertEquals("", user9Conversation.getChatMessagesList().get(0).getGuestLastName());

        assertEquals("message16", user9Conversation.getChatMessagesList().get(1).getMessageId());
        assertEquals("user8", user9Conversation.getChatMessagesList().get(1).getTo());
        assertEquals("user9", user9Conversation.getChatMessagesList().get(1).getFrom());
        assertEquals(Enums.Chats.ConversationTypes.GROUP_CHAT, user9Conversation.getChatMessagesList().get(1).getType());
        assertEquals("body16", user9Conversation.getChatMessagesList().get(1).getBody());
        assertFalse(user9Conversation.getChatMessagesList().get(1).isSender());
        assertTrue(user9Conversation.getChatMessagesList().get(1).isRead());
        assertEquals((Long) 1522272811427L, user9Conversation.getChatMessagesList().get(1).getTimestamp());
        assertEquals("user9", user9Conversation.getChatMessagesList().get(1).getParticipant());
        assertEquals("", user9Conversation.getChatMessagesList().get(1).getGuestFirstName());
        assertEquals("", user9Conversation.getChatMessagesList().get(1).getGuestLastName());

        assertEquals("message17", user9Conversation.getChatMessagesList().get(2).getMessageId());
        assertEquals("user9", user9Conversation.getChatMessagesList().get(2).getTo());
        assertEquals("user8", user9Conversation.getChatMessagesList().get(2).getFrom());
        assertEquals(Enums.Chats.ConversationTypes.GROUP_CHAT, user9Conversation.getChatMessagesList().get(2).getType());
        assertEquals("body17", user9Conversation.getChatMessagesList().get(2).getBody());
        assertTrue(user9Conversation.getChatMessagesList().get(2).isSender());
        assertFalse(user9Conversation.getChatMessagesList().get(2).isRead());
        assertEquals((Long) 1522272711427L, user9Conversation.getChatMessagesList().get(2).getTimestamp());
        assertEquals("", user9Conversation.getChatMessagesList().get(2).getParticipant());
        assertEquals("Guest First", user9Conversation.getChatMessagesList().get(2).getGuestFirstName());
        assertEquals("Guest Last", user9Conversation.getChatMessagesList().get(2).getGuestLastName());

        assertEquals("message18", user9Conversation.getChatMessagesList().get(3).getMessageId());
        assertEquals("user8", user9Conversation.getChatMessagesList().get(3).getTo());
        assertEquals("user9", user9Conversation.getChatMessagesList().get(3).getFrom());
        assertEquals(Enums.Chats.ConversationTypes.GROUP_CHAT, user9Conversation.getChatMessagesList().get(3).getType());
        assertEquals("body18", user9Conversation.getChatMessagesList().get(3).getBody());
        assertFalse(user9Conversation.getChatMessagesList().get(3).isSender());
        assertTrue(user9Conversation.getChatMessagesList().get(3).isRead());
        assertEquals((Long) 1522272611427L, user9Conversation.getChatMessagesList().get(3).getTimestamp());
        assertEquals("user9", user9Conversation.getChatMessagesList().get(3).getParticipant());
        assertEquals("", user9Conversation.getChatMessagesList().get(3).getGuestFirstName());
        assertEquals("", user9Conversation.getChatMessagesList().get(3).getGuestLastName());
    }

    @Test
    public void processChatConversations_emptyInput_doesNotProcessValues() {
        BroadsoftUmsChatMessagesResponse broadsoftUmsChatMessagesResponse = new BroadsoftUmsChatMessagesResponse(new BroadsoftUmsChatMessage[] {});
        HashMap<String, ChatConversation> chatConversationHashMap = new HashMap<>();

        BroadsoftUtil.processChatConversations(broadsoftUmsChatMessagesResponse, chatConversationHashMap);

        assertEquals(0, chatConversationHashMap.size());
    }

    @Test
    public void processChatConversations_invalidInput_doesNotProcessValues() {
        BroadsoftUmsChatMessagesResponse broadsoftUmsChatMessagesResponse = new BroadsoftUmsChatMessagesResponse();
        HashMap<String, ChatConversation> chatConversationHashMap = new HashMap<>();

        BroadsoftUtil.processChatConversations(broadsoftUmsChatMessagesResponse, chatConversationHashMap);

        assertEquals(0, chatConversationHashMap.size());
    }


    @Test
    public void processMobileConfig_validNextivaAnywhereEnabledInput_storesValues() {
        BroadsoftSupplementaryServicesXsiBroadworksAnywhere nextivaAnywhere = new BroadsoftSupplementaryServicesXsiBroadworksAnywhere("true");
        BroadsoftMobileConfigGeneralSetting remoteOffice = new BroadsoftMobileConfigGeneralSetting("false");

        BroadsoftSupplementaryServicesXsi broadsoftSupplementaryServicesXsi = new BroadsoftSupplementaryServicesXsi(nextivaAnywhere, remoteOffice);
        BroadsoftServicesSupplementaryServices broadsoftServicesSupplementaryServices = new BroadsoftServicesSupplementaryServices(broadsoftSupplementaryServicesXsi);
        BroadsoftMobileConfigServices broadsoftMobileConfigServices = new BroadsoftMobileConfigServices(broadsoftServicesSupplementaryServices);
        BroadsoftMobileConfigResponse broadsoftMobileConfigResponse = new BroadsoftMobileConfigResponse(broadsoftMobileConfigServices);

        BroadsoftUtil.processMobileConfig(mConfigManager, mNetManager, broadsoftMobileConfigResponse);

        verify(mConfigManager).setNextivaAnywhereEnabled(true);
    }

    @Test
    public void processMobileConfig_invalidNextivaAnywhereEnabledInput_doesNotProcessValues() {
        BroadsoftSupplementaryServicesXsiBroadworksAnywhere nextivaAnywhere = new BroadsoftSupplementaryServicesXsiBroadworksAnywhere();
        BroadsoftMobileConfigGeneralSetting remoteOffice = new BroadsoftMobileConfigGeneralSetting("false");

        BroadsoftSupplementaryServicesXsi broadsoftSupplementaryServicesXsi = new BroadsoftSupplementaryServicesXsi(nextivaAnywhere, remoteOffice);
        BroadsoftServicesSupplementaryServices broadsoftServicesSupplementaryServices = new BroadsoftServicesSupplementaryServices(broadsoftSupplementaryServicesXsi);
        BroadsoftMobileConfigServices broadsoftMobileConfigServices = new BroadsoftMobileConfigServices(broadsoftServicesSupplementaryServices);
        BroadsoftMobileConfigResponse broadsoftMobileConfigResponse = new BroadsoftMobileConfigResponse(broadsoftMobileConfigServices);

        BroadsoftUtil.processMobileConfig(mConfigManager, mNetManager, broadsoftMobileConfigResponse);

        verify(mConfigManager, times(0)).setNextivaAnywhereEnabled(anyBoolean());
    }

    @Test
    public void processMobileConfig_validRemoteOfficeEnabledInput_storesValues() {
        BroadsoftSupplementaryServicesXsiBroadworksAnywhere nextivaAnywhere = new BroadsoftSupplementaryServicesXsiBroadworksAnywhere("false");
        BroadsoftMobileConfigGeneralSetting remoteOffice = new BroadsoftMobileConfigGeneralSetting("true");

        BroadsoftSupplementaryServicesXsi broadsoftSupplementaryServicesXsi = new BroadsoftSupplementaryServicesXsi(nextivaAnywhere, remoteOffice);
        BroadsoftServicesSupplementaryServices broadsoftServicesSupplementaryServices = new BroadsoftServicesSupplementaryServices(broadsoftSupplementaryServicesXsi);
        BroadsoftMobileConfigServices broadsoftMobileConfigServices = new BroadsoftMobileConfigServices(broadsoftServicesSupplementaryServices);
        BroadsoftMobileConfigResponse broadsoftMobileConfigResponse = new BroadsoftMobileConfigResponse(broadsoftMobileConfigServices);

        BroadsoftUtil.processMobileConfig(mConfigManager, mNetManager, broadsoftMobileConfigResponse);

        verify(mConfigManager).setRemoteOfficeEnabled(true);
    }

    @Test
    public void processMobileConfig_invalidRemoteOfficeEnabledInput_doesNotProcessValues() {
        BroadsoftSupplementaryServicesXsiBroadworksAnywhere nextivaAnywhere = new BroadsoftSupplementaryServicesXsiBroadworksAnywhere("false");
        BroadsoftMobileConfigGeneralSetting remoteOffice = new BroadsoftMobileConfigGeneralSetting();

        BroadsoftSupplementaryServicesXsi broadsoftSupplementaryServicesXsi = new BroadsoftSupplementaryServicesXsi(nextivaAnywhere, remoteOffice);
        BroadsoftServicesSupplementaryServices broadsoftServicesSupplementaryServices = new BroadsoftServicesSupplementaryServices(broadsoftSupplementaryServicesXsi);
        BroadsoftMobileConfigServices broadsoftMobileConfigServices = new BroadsoftMobileConfigServices(broadsoftServicesSupplementaryServices);
        BroadsoftMobileConfigResponse broadsoftMobileConfigResponse = new BroadsoftMobileConfigResponse(broadsoftMobileConfigServices);

        BroadsoftUtil.processMobileConfig(mConfigManager, mNetManager, broadsoftMobileConfigResponse);

        verify(mConfigManager, times(0)).setRemoteOfficeEnabled(anyBoolean());
    }


}
