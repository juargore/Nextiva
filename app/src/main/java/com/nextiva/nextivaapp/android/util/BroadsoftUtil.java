package com.nextiva.nextivaapp.android.util;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.nextiva.nextivaapp.android.constants.Constants;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.db.model.EmailAddress;
import com.nextiva.nextivaapp.android.db.model.PhoneNumber;
import com.nextiva.nextivaapp.android.managers.interfaces.ConfigManager;
import com.nextiva.nextivaapp.android.managers.interfaces.NetManager;
import com.nextiva.nextivaapp.android.models.AudioCodec;
import com.nextiva.nextivaapp.android.models.CallLogEntry;
import com.nextiva.nextivaapp.android.models.ChatConversation;
import com.nextiva.nextivaapp.android.models.ChatMessage;
import com.nextiva.nextivaapp.android.models.NextivaAnywhereLocation;
import com.nextiva.nextivaapp.android.models.NextivaContact;
import com.nextiva.nextivaapp.android.models.Service;
import com.nextiva.nextivaapp.android.models.ServiceSettings;
import com.nextiva.nextivaapp.android.models.SimultaneousRingLocation;
import com.nextiva.nextivaapp.android.models.UserDetails;
import com.nextiva.nextivaapp.android.models.VideoCodec;
import com.nextiva.nextivaapp.android.models.mobileConfig.Calls;
import com.nextiva.nextivaapp.android.models.mobileConfig.Conference;
import com.nextiva.nextivaapp.android.models.mobileConfig.MobileConfig;
import com.nextiva.nextivaapp.android.models.mobileConfig.Sip;
import com.nextiva.nextivaapp.android.models.mobileConfig.Tcp;
import com.nextiva.nextivaapp.android.models.mobileConfig.Udp;
import com.nextiva.nextivaapp.android.models.mobileConfig.Xmpp;
import com.nextiva.nextivaapp.android.models.mobileConfig.Xsi;
import com.nextiva.nextivaapp.android.models.net.broadsoft.BroadsoftProfileResponse;
import com.nextiva.nextivaapp.android.models.net.broadsoft.BroadsoftServiceSettingsResponse;
import com.nextiva.nextivaapp.android.models.net.broadsoft.BroadsoftServicesResponse;
import com.nextiva.nextivaapp.android.models.net.broadsoft.calllogs.BroadsoftAllCallLogsResponse;
import com.nextiva.nextivaapp.android.models.net.broadsoft.calllogs.BroadsoftCallLogEntry;
import com.nextiva.nextivaapp.android.models.net.broadsoft.enterprisecontacts.BroadsoftEnterprise;
import com.nextiva.nextivaapp.android.models.net.broadsoft.enterprisecontacts.BroadsoftEnterpriseAdditionalDetails;
import com.nextiva.nextivaapp.android.models.net.broadsoft.enterprisecontacts.BroadsoftEnterpriseDirectoryDetails;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.BroadsoftMobileConfigProtocols;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.BroadsoftMobileConfigResponse;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.BroadsoftMobileConfigServices;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.protocols.BroadsoftSipProtocol;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.protocols.BroadsoftXmppProtocol;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.protocols.BroadsoftXsiProtocol;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.protocols.sip.BroadsoftSipTransports;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.BroadsoftServicesCalls;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.BroadsoftServicesSupplementaryServices;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.calls.BroadsoftCallsAudio;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.calls.BroadsoftCallsAudioCodec;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.calls.BroadsoftCallsConference;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.calls.BroadsoftCallsVideo;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.calls.BroadsoftCallsVideoCodec;
import com.nextiva.nextivaapp.android.models.net.broadsoft.mobileconfig.services.supplementaryservices.BroadsoftSupplementaryServicesXsi;
import com.nextiva.nextivaapp.android.models.net.broadsoft.services.BaseBroadsoftBroadWorksAnywhereLocation;
import com.nextiva.nextivaapp.android.models.net.broadsoft.services.BroadsoftBaseServiceSettings;
import com.nextiva.nextivaapp.android.models.net.broadsoft.services.BroadsoftBroadWorksAnywhereLocation;
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
import com.nextiva.nextivaapp.android.models.net.broadsoft.services.BroadsoftSimultaneousRingPersonalLocations;
import com.nextiva.nextivaapp.android.models.net.broadsoft.services.BroadsoftSimultaneousRingPersonalServiceSettings;
import com.nextiva.nextivaapp.android.models.net.broadsoft.ums.BroadsoftUmsChatMessage;
import com.nextiva.nextivaapp.android.models.net.broadsoft.ums.BroadsoftUmsChatMessagesResponse;
import com.nextiva.nextivaapp.android.models.net.broadsoft.ums.BroadsoftUmsJid;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Thaddeus Dannar on 3/1/18.
 */

public class BroadsoftUtil {

    private static final String DONT_RING_IF_ON_CALL_VALUE = "Do not Ring if on a Call";
    private static final String RING_FOR_ALL_INCOMING_CALLS_VALUE = "Ring for all Incoming Calls";

    private BroadsoftUtil() {
    }

    @Nullable
    public static UserDetails getUserDetails(@Nullable BroadsoftProfileResponse profileResponse) {
        if (profileResponse != null &&
                profileResponse.getDetails() != null &&
                profileResponse.getAdditionalDetails() != null) {

            UserDetails userDetails = new UserDetails();

            userDetails.setFirstName(profileResponse.getDetails().getFirstName());
            userDetails.setLastName(profileResponse.getDetails().getLastName());
            userDetails.setEmail(profileResponse.getAdditionalDetails().getEmailAddress());
            userDetails.setImpId(profileResponse.getAdditionalDetails().getImpId());
            userDetails.setTelephoneNumber(profileResponse.getDetails().getNumber());
            userDetails.setExtension(profileResponse.getDetails().getExtension());
            userDetails.setLocation(profileResponse.getAdditionalDetails().getLocation());
            userDetails.setGroupId(profileResponse.getDetails().getGroupId());
            userDetails.setServiceProvider(profileResponse.getDetails().getServiceProvider());
            userDetails.setIsEnterprise(profileResponse.getDetails().getEnterprise());

            return userDetails;

        } else {
            return null;
        }
    }

    @Nullable
    public static Service[] getServices(@NonNull BroadsoftServicesResponse servicesResponse) {
        if (servicesResponse.getServicesList() != null && !servicesResponse.getServicesList().isEmpty()) {
            ArrayList<Service> servicesList = new ArrayList<>();

            for (BroadsoftService broadsoftService : servicesResponse.getServicesList()) {
                if (broadsoftService != null) {
                    servicesList.add(new Service(broadsoftService.getName(), broadsoftService.getUri()));
                }
            }

            return servicesList.toArray(new Service[0]);
        }

        return null;
    }

    public static ServiceSettings getServiceSettings(@NonNull @Enums.Service.Type String serviceType,
                                                     @NonNull String uri,
                                                     @NonNull BroadsoftServiceSettingsResponse serviceSettingsResponse) {

        ArrayList<NextivaAnywhereLocation> nextivaAnywhereLocationsList = null;

        if (serviceSettingsResponse.getNextivaAnywhereLocationsList() != null &&
                !serviceSettingsResponse.getNextivaAnywhereLocationsList().isEmpty()) {

            nextivaAnywhereLocationsList = new ArrayList<>();

            for (BroadsoftBroadWorksAnywhereLocation broadsoftBroadWorksAnywhereLocation : serviceSettingsResponse.getNextivaAnywhereLocationsList()) {
                nextivaAnywhereLocationsList.add(getNextivaAnywhereLocation(broadsoftBroadWorksAnywhereLocation,
                                                                            broadsoftBroadWorksAnywhereLocation.getDescription()));
            }
        }

        ArrayList<SimultaneousRingLocation> simultaneousRingLocationsList = null;

        if (serviceSettingsResponse.getSimultaneousRingLocationsList() != null &&
                !serviceSettingsResponse.getSimultaneousRingLocationsList().isEmpty()) {

            simultaneousRingLocationsList = new ArrayList<>();

            for (BroadsoftSimultaneousRingLocation broadsoftSimultaneousRingLocation : serviceSettingsResponse.getSimultaneousRingLocationsList()) {
                simultaneousRingLocationsList.add(getSimultaneousRingLocation(broadsoftSimultaneousRingLocation));
            }
        }

        Boolean dontRingWhileOnCall = null;
        if (!TextUtils.isEmpty(serviceSettingsResponse.getIncomingCalls())) {
            dontRingWhileOnCall = TextUtils.equals(DONT_RING_IF_ON_CALL_VALUE, serviceSettingsResponse.getIncomingCalls());
        }

        return new ServiceSettings(
                serviceType,
                uri,
                serviceSettingsResponse.getActive(),
                serviceSettingsResponse.getRingSplash(),
                serviceSettingsResponse.getNumberOfRings(),
                serviceSettingsResponse.getRemoteOfficeNumber(),
                serviceSettingsResponse.getForwardToPhoneNumber(),
                serviceSettingsResponse.getAlertAllLocationsForClickToDialCalls(),
                serviceSettingsResponse.getAlertAllLocationsForGroupPagingCalls(),
                nextivaAnywhereLocationsList,
                dontRingWhileOnCall,
                simultaneousRingLocationsList);
    }

    @Nullable
    public static BroadsoftBaseServiceSettings getBroadsoftBaseServiceSettings(@NonNull ServiceSettings serviceSettings) {
        if (!TextUtils.isEmpty(serviceSettings.getType())) {
            switch (serviceSettings.getType()) {
                case Enums.Service.TYPE_DO_NOT_DISTURB: {
                    return new BroadsoftDoNotDisturbServiceSettings(
                            serviceSettings.getActiveRaw(),
                            serviceSettings.getRingSplashEnabledRaw());
                }
                case Enums.Service.TYPE_CALL_FORWARDING_ALWAYS: {
                    return new BroadsoftCallForwardingAlwaysServiceSettings(
                            serviceSettings.getActiveRaw(),
                            serviceSettings.getRingSplashEnabledRaw(),
                            serviceSettings.getForwardToPhoneNumber());
                }
                case Enums.Service.TYPE_CALL_FORWARDING_BUSY: {
                    return new BroadsoftCallForwardingBusyServiceSettings(
                            serviceSettings.getActiveRaw(),
                            serviceSettings.getForwardToPhoneNumber());
                }
                case Enums.Service.TYPE_CALL_FORWARDING_NO_ANSWER: {
                    return new BroadsoftCallForwardingNoAnswerServiceSettings(
                            serviceSettings.getActiveRaw(),
                            serviceSettings.getNumberOfRings(),
                            serviceSettings.getForwardToPhoneNumber());
                }
                case Enums.Service.TYPE_CALL_FORWARDING_NOT_REACHABLE: {
                    return new BroadsoftCallForwardingNotReachableServiceSettings(
                            serviceSettings.getActiveRaw(),
                            serviceSettings.getForwardToPhoneNumber());
                }
                case Enums.Service.TYPE_REMOTE_OFFICE: {
                    return new BroadsoftRemoteOfficeServiceSettings(
                            serviceSettings.getActiveRaw(),
                            serviceSettings.getRemoteOfficeNumber());
                }
                case Enums.Service.TYPE_CALLING_LINE_ID_DELIVERY_BLOCKING: {
                    return new BroadsoftCallingIdDeliveryBlockingServiceSettings(
                            serviceSettings.getActiveRaw());
                }
                case Enums.Service.TYPE_BROADWORKS_ANYWHERE: {
                    ArrayList<BroadsoftBroadWorksAnywhereLocation> locationsList = null;

                    if (serviceSettings.getNextivaAnywhereLocationsList() != null && !serviceSettings.getNextivaAnywhereLocationsList().isEmpty()) {
                        locationsList = new ArrayList<>();

                        for (NextivaAnywhereLocation nextivaAnywhereLocation : serviceSettings.getNextivaAnywhereLocationsList()) {
                            locationsList.add(new BroadsoftBroadWorksAnywhereLocation(nextivaAnywhereLocation));
                        }
                    }

                    return new BroadsoftBroadWorksAnywhereServiceSettings(
                            serviceSettings.getAlertAllLocationsForClickToDialCallsRaw(),
                            serviceSettings.getAlertAllLocationsForGroupPagingCallsRaw(),
                            locationsList);
                }
                case Enums.Service.TYPE_SIMULTANEOUS_RING_PERSONAL: {
                    ArrayList<BroadsoftSimultaneousRingLocation> locationsList;
                    BroadsoftSimultaneousRingPersonalLocations broadsoftSimultaneousRingPersonalLocations = new BroadsoftSimultaneousRingPersonalLocations(null);

                    if (serviceSettings.getSimultaneousRingLocationsList() != null && !serviceSettings.getSimultaneousRingLocationsList().isEmpty()) {
                        locationsList = new ArrayList<>();

                        for (SimultaneousRingLocation simultaneousRingLocation : serviceSettings.getSimultaneousRingLocationsList()) {
                            locationsList.add(new BroadsoftSimultaneousRingLocation(simultaneousRingLocation));
                        }

                        broadsoftSimultaneousRingPersonalLocations.setSimultaneousRingLocations(locationsList);
                    }

                    String incomingCalls = null;
                    if (serviceSettings.getDontRingWhileOnCallRaw() != null) {
                        incomingCalls = serviceSettings.getDontRingWhileOnCall() ? DONT_RING_IF_ON_CALL_VALUE : RING_FOR_ALL_INCOMING_CALLS_VALUE;
                    }

                    return new BroadsoftSimultaneousRingPersonalServiceSettings(
                            serviceSettings.getActive(),
                            incomingCalls,
                            broadsoftSimultaneousRingPersonalLocations);
                }
            }
        }

        return null;
    }

    public static NextivaAnywhereLocation getNextivaAnywhereLocation(
            @NonNull BaseBroadsoftBroadWorksAnywhereLocation baseBroadsoftBroadWorksAnywhereLocation,
            @Nullable String description) {

        String phoneNumber = baseBroadsoftBroadWorksAnywhereLocation.getPhoneNumber();

        return new NextivaAnywhereLocation(
                TextUtils.isEmpty(phoneNumber) ? "" : phoneNumber,
                description,
                baseBroadsoftBroadWorksAnywhereLocation.getActive(),
                baseBroadsoftBroadWorksAnywhereLocation.getCallControlEnabled(),
                baseBroadsoftBroadWorksAnywhereLocation.getPreventDivertingCalls(),
                baseBroadsoftBroadWorksAnywhereLocation.getAnswerConfirmationRequired());
    }

    public static SimultaneousRingLocation getSimultaneousRingLocation(
            @NonNull BroadsoftSimultaneousRingLocation broadsoftSimultaneousRingLocation) {

        return new SimultaneousRingLocation(broadsoftSimultaneousRingLocation.getAddress(),
                                            broadsoftSimultaneousRingLocation.getAnswerConfirmationRequired());
    }

    @Nullable
    public static NextivaContact getNextivaContact(@Nullable BroadsoftEnterpriseDirectoryDetails directoryDetails) {
        if (directoryDetails != null && !TextUtils.isEmpty(directoryDetails.getUserId())) {
            ArrayList<EmailAddress> emailAddresses = new ArrayList<>();
            ArrayList<PhoneNumber> numbers = new ArrayList<>();

            NextivaContact nextivaContact = new NextivaContact(directoryDetails.getUserId());
            nextivaContact.setContactType(Enums.Contacts.ContactTypes.ENTERPRISE);
            nextivaContact.setFirstName(directoryDetails.getFirstName());
            nextivaContact.setLastName(directoryDetails.getLastName());
            nextivaContact.setServerUserId(directoryDetails.getUserId());
            nextivaContact.setHiraganaFirstName(directoryDetails.getHiranganaFirstName());
            nextivaContact.setHiraganaLastName(directoryDetails.getHiranganaLastName());
            nextivaContact.setGroupId(directoryDetails.getGroupId());

            if (directoryDetails.getAdditionalDetails() != null) {

                BroadsoftEnterpriseAdditionalDetails additionalDetails = directoryDetails.getAdditionalDetails();

//                nextivaContact.setTitle(additionalDetails.getTitle());
//
//                Address address = new Address(additionalDetails.getAddressLineOne(),
//                        additionalDetails.getAddressLineTwo(),
//                        additionalDetails.getZip(),
//                        additionalDetails.getCity(),
//                        additionalDetails.getState(),
//                        additionalDetails.getCountry(),
//                        additionalDetails.getLocation());
//
//                if (address.hasNonNullValue()) {
//                    nextivaContact.setAddress(address);
//                }

                if (!TextUtils.isEmpty(additionalDetails.getEmailAddress())) {
                    emailAddresses.add(new EmailAddress(Enums.Contacts.EmailTypes.WORK_EMAIL,
                                                        additionalDetails.getEmailAddress(),
                                                        null));
                }

                if (!TextUtils.isEmpty(additionalDetails.getImpId())) {
                    nextivaContact.setJid(additionalDetails.getImpId());
                }

                if (!TextUtils.isEmpty(additionalDetails.getMobileNumber())) {
                    numbers.add(new PhoneNumber(Enums.Contacts.PhoneTypes.MOBILE_PHONE,
                                                additionalDetails.getMobileNumber(),
                                                null));
                }
            }

            if (!TextUtils.isEmpty(directoryDetails.getPhoneNumber())) {
                numbers.add(new PhoneNumber(Enums.Contacts.PhoneTypes.WORK_PHONE,
                                            directoryDetails.getPhoneNumber(),
                                            null));
            }

            if (!TextUtils.isEmpty(directoryDetails.getExtension())) {
                numbers.add(new PhoneNumber(Enums.Contacts.PhoneTypes.WORK_EXTENSION,
                                            directoryDetails.getExtension()));
            }

            nextivaContact.setEmailAddresses(emailAddresses);
            nextivaContact.setAllPhoneNumbers(numbers);

            return nextivaContact;
        }

        return null;
    }

    @NonNull
    public static ArrayList<NextivaContact> getNextivaContacts(@Nullable BroadsoftEnterprise broadsoftEnterprise) {
        ArrayList<NextivaContact> nextivaContactsList = new ArrayList<>();

        if (broadsoftEnterprise != null && broadsoftEnterprise.getEnterpriseDirectoryDetailsList() != null) {
            for (BroadsoftEnterpriseDirectoryDetails directoryDetails : broadsoftEnterprise.getEnterpriseDirectoryDetailsList()) {
                nextivaContactsList.add(BroadsoftUtil.getNextivaContact(directoryDetails));
            }
        }

        return nextivaContactsList;
    }

    public static ArrayList<CallLogEntry> getCallLogEntries(@NonNull BroadsoftAllCallLogsResponse callLogsResponse) {
        ArrayList<CallLogEntry> listCallLogEntries = new ArrayList<>();

        if (callLogsResponse.getMissedCallLogs() != null) {
            processCallLogEntries(callLogsResponse.getMissedCallLogs(), listCallLogEntries, Enums.Calls.CallTypes.MISSED);
        }

        if (callLogsResponse.getPlacedCallLogs() != null) {
            processCallLogEntries(callLogsResponse.getPlacedCallLogs(), listCallLogEntries, Enums.Calls.CallTypes.PLACED);
        }

        if (callLogsResponse.getReceivedCallLogs() != null) {
            processCallLogEntries(callLogsResponse.getReceivedCallLogs(), listCallLogEntries, Enums.Calls.CallTypes.RECEIVED);
        }

        CallUtil.sortCallLogEntries(listCallLogEntries);

        return listCallLogEntries;
    }

    private static void processCallLogEntries(
            @NonNull ArrayList<BroadsoftCallLogEntry> broadsoftCallLogEntriesList,
            @NonNull ArrayList<CallLogEntry> callLogEntriesList,
            @NonNull @Enums.Calls.CallTypes.Type String type) {

        for (BroadsoftCallLogEntry broadsoftCallLogEntry : broadsoftCallLogEntriesList) {
            if (broadsoftCallLogEntry != null &&
                    !TextUtils.isEmpty(broadsoftCallLogEntry.getPhoneNumber()) &&
                    !TextUtils.isEmpty(broadsoftCallLogEntry.getTime())) {

                try {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault());
                    Date date = format.parse(broadsoftCallLogEntry.getTime());

                    callLogEntriesList.add(new CallLogEntry(
                            broadsoftCallLogEntry.getCallLogId(),
                            broadsoftCallLogEntry.getName(),
                            date.getTime(),
                            broadsoftCallLogEntry.getCountryCode(),
                            broadsoftCallLogEntry.getPhoneNumber(),
                            type,
                            null,
                            null,
                            Enums.Contacts.PresenceStates.NONE,
                            Constants.PRESENCE_OFFLINE_PRIORITY,
                            null,
                            0,
                            null,
                            Enums.Contacts.PresenceTypes.NONE,
                            0,
                            ""));

                } catch (ParseException e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                }
            }
        }
    }

    public static void processChatConversations(@NonNull BroadsoftUmsChatMessagesResponse chatMessagesResponse,
                                                @NonNull HashMap<String, ChatConversation> chatConversationsMap) {

        if (chatMessagesResponse.getChatMessages() != null && chatMessagesResponse.getChatMessages().length > 0) {
            String conversationKey;
            ChatMessage chatMessage;

            for (BroadsoftUmsChatMessage umsChatMessage : chatMessagesResponse.getChatMessages()) {
                if (!TextUtils.isEmpty(umsChatMessage.getFrom()) &&
                        !TextUtils.isEmpty(umsChatMessage.getType()) &&
                        !TextUtils.isEmpty(umsChatMessage.getBody()) &&
                        umsChatMessage.getTimestamp() != null &&
                        umsChatMessage.getSender() != null) {

                    chatMessage = null;

                    switch (umsChatMessage.getType()) {
                        case Enums.Chats.ConversationTypes.GROUP_CHAT:
                            chatMessage = new ChatMessage(
                                    umsChatMessage.getMessageId(),
                                    umsChatMessage.getTo(),
                                    umsChatMessage.getSender() ? umsChatMessage.getFrom() : umsChatMessage.getParticipant(),
                                    umsChatMessage.getType(),
                                    umsChatMessage.getBody(),
                                    umsChatMessage.getSender(),
                                    umsChatMessage.getRead() != null ? umsChatMessage.getRead() : false,
                                    umsChatMessage.getTimestamp(),
                                    umsChatMessage.getThreadId(),
                                    null,
                                    umsChatMessage.getLanguage(),
                                    umsChatMessage.getParticipant(),
                                    umsChatMessage.getGuestFirstName(),
                                    umsChatMessage.getGuestLastName(),
                                    Enums.Chats.SentStatus.SUCCESSFUL,
                                    umsChatMessage.getSender() ? umsChatMessage.getTo() : umsChatMessage.getFrom());
                            break;

                        case Enums.Chats.ConversationTypes.CHAT:
                            chatMessage = new ChatMessage(
                                    umsChatMessage.getMessageId(),
                                    umsChatMessage.getTo(),
                                    umsChatMessage.getFrom(),
                                    umsChatMessage.getType(),
                                    umsChatMessage.getBody(),
                                    umsChatMessage.getSender(),
                                    umsChatMessage.getRead() != null ? umsChatMessage.getRead() : false,
                                    umsChatMessage.getTimestamp(),
                                    umsChatMessage.getThreadId(),
                                    null,
                                    umsChatMessage.getLanguage(),
                                    umsChatMessage.getParticipant(),
                                    umsChatMessage.getGuestFirstName(),
                                    umsChatMessage.getGuestLastName(),
                                    Enums.Chats.SentStatus.SUCCESSFUL,
                                    umsChatMessage.getSender() ? umsChatMessage.getTo() : umsChatMessage.getFrom());
                            break;

                        case Enums.Chats.ConversationTypes.GROUP_ALIAS:
                            ArrayList<String> jids = new ArrayList<>();

                            if (umsChatMessage.getMembers() != null) {
                                for (BroadsoftUmsJid jid : umsChatMessage.getMembers()) {
                                    jids.add(jid.getJid());
                                }
                            }

                            chatMessage = new ChatMessage(
                                    umsChatMessage.getMessageId(),
                                    umsChatMessage.getTo(),
                                    umsChatMessage.getFrom(),
                                    umsChatMessage.getType(),
                                    umsChatMessage.getBody(),
                                    umsChatMessage.getSender(),
                                    umsChatMessage.getRead() != null ? umsChatMessage.getRead() : false,
                                    umsChatMessage.getTimestamp(),
                                    umsChatMessage.getThreadId(),
                                    GsonUtil.getJSON(jids),
                                    umsChatMessage.getLanguage(),
                                    umsChatMessage.getParticipant(),
                                    umsChatMessage.getGuestFirstName(),
                                    umsChatMessage.getGuestLastName(),
                                    Enums.Chats.SentStatus.SUCCESSFUL,
                                    umsChatMessage.getThreadId());
                            break;
                    }

                    if (chatMessage != null) {
                        conversationKey = chatMessage.getChatWith();

                        if (!chatConversationsMap.containsKey(conversationKey)) {
                            chatConversationsMap.put(conversationKey, new ChatConversation(chatMessage.getType()));
                        }

                        ChatConversation conversationKeyChatConversation = chatConversationsMap.get(conversationKey);
                        if (conversationKeyChatConversation != null) {
                            conversationKeyChatConversation.addChatMessage(chatMessage);
                        }

                    }
                }
            }
        }
    }


    public static void processMobileConfig(@NonNull ConfigManager configManager, @NonNull NetManager netManager, @NonNull BroadsoftMobileConfigResponse mobileConfigResponse) {
        configManager.clearCache();

        MobileConfig mobileConfig = new MobileConfig();
        Sip mobileConfigSip = new Sip();
        Calls mobileConfigCalls = new Calls();
        Xmpp mobileConfigXmpp = new Xmpp();
        Xsi mobileConfigXsi = new Xsi();

        BroadsoftMobileConfigProtocols protocols = mobileConfigResponse.getConfigProtocols();

        if (protocols != null) {
            //THIS SHOULD BE KEPT UNTIL TESTING HAS PROVEN THERE ARE NO MORE LOST DATA ISSUES
            configManager.setFullMobileConfigForTesting(GsonUtil.getJSON(protocols));

            BroadsoftXmppProtocol xmppProtocol = protocols.getBroadsoftXmppProtocol();

            if (xmppProtocol != null) {
                if (xmppProtocol.getXmppDomain() != null) {
                    mobileConfigXmpp.setDomain(xmppProtocol.getXmppDomain().getDomain());
                }

                if (xmppProtocol.getKeepAliveIntervalSec() != null) {
                    mobileConfigXmpp.setKeepAliveTimeOut(Integer.parseInt(xmppProtocol.getKeepAliveIntervalSec()));
                }

                if (xmppProtocol.getXmppCredentials() != null) {
                    mobileConfigXmpp.setUsername(xmppProtocol.getXmppCredentials().getXmppUsername());
                    mobileConfigXmpp.setPassword(xmppProtocol.getXmppCredentials().getXmppPassword());
                }

            }

            BroadsoftXsiProtocol xsiProtocol = protocols.getBroadsoftXsiProtocol();

            if (xsiProtocol != null && xsiProtocol.getXsiPath() != null) {
                mobileConfigXsi.setXsiRoot(xsiProtocol.getXsiPath().getXsiPathRoot());
                mobileConfigXsi.setXsiActions(xsiProtocol.getXsiPath().getXsiActions());
                mobileConfigXsi.setXsiEvents(xsiProtocol.getXsiPath().getXsiEvents());

                // XSI Actions comes back as null in labs
                if (!TextUtils.isEmpty(xsiProtocol.getXsiPath().getXsiActions()) &&
                        !TextUtils.isEmpty(xsiProtocol.getXsiPath().getXsiPathRoot())) {
                    netManager.setupBroadsoftUserApi(xsiProtocol.getXsiPath().getXsiPathRoot() + xsiProtocol.getXsiPath().getXsiActions());
                }
            }

            BroadsoftSipProtocol sipProtocol = protocols.getBroadsoftSipProtocol();

            if (sipProtocol != null) {
                mobileConfigSip.setDomain(sipProtocol.getDomain());
                mobileConfigSip.setUserAgent(sipProtocol.getUserAgent());

                if (sipProtocol.getCredentials() != null) {
                    mobileConfigSip.setUsername(sipProtocol.getCredentials().getUsername());
                    mobileConfigSip.setPassword(sipProtocol.getCredentials().getPassword());

                    if (sipProtocol.getCredentials().getSipAuth() != null) {
                        mobileConfigSip.setAuthorizationUsername(sipProtocol.getCredentials().getSipAuth().getUsername());
                    }
                }

                if (sipProtocol.getUseRport() != null) {
                    mobileConfigSip.setUseRport(Boolean.valueOf(sipProtocol.getUseRport().isEnabled()));
                }

                if (sipProtocol.getProxy() != null) {
                    mobileConfigSip.setProxyDomain(sipProtocol.getProxy().getAddress());
                }

                if (sipProtocol.getProxy() != null) {
                    if (sipProtocol.getProxy().getPort() != null) {
                        mobileConfigSip.setProxyPort(Integer.parseInt(sipProtocol.getProxy().getPort()));
                    }
                }

                if (sipProtocol.getPreferredPort() != null) {
                    mobileConfigSip.setPreferredPort(Integer.parseInt(sipProtocol.getPreferredPort()));
                }

                if (sipProtocol.getRegistrar() != null) {
                    if (sipProtocol.getRegistrar().getPort() != null) {
                        mobileConfigSip.setRegistrarPort(Integer.parseInt(sipProtocol.getRegistrar().getPort()));
                    }

                    mobileConfigSip.setRegistrationUri(sipProtocol.getRegistrar().getUri());
                }

                if (sipProtocol.getRegistrationRefreshInterval() != null) {
                    mobileConfigSip.setRegistrationRefreshInterval(sipProtocol.getRegistrationRefreshInterval());
                }

                if (sipProtocol.getSession() != null && sipProtocol.getSession().getExpiresSec() != null) {
                    mobileConfigSip.setSessionExpiresSec(Integer.parseInt(sipProtocol.getSession().getExpiresSec()));
                }

                if (sipProtocol.getTransports() != null) {
                    BroadsoftSipTransports broadsoftSipTransports = sipProtocol.getTransports();

                    mobileConfigSip.setTransportType(broadsoftSipTransports.getType());

                    if (broadsoftSipTransports.getTcp() != null && broadsoftSipTransports.getTcp().getKeepalive() != null) {
                        if (broadsoftSipTransports.getTcp().getKeepalive().isEnabled() != null && broadsoftSipTransports.getTcp().getKeepalive().getTimeoutSec() != null) {
                            Tcp tcp = new Tcp(Boolean.valueOf(broadsoftSipTransports.getTcp().getKeepalive().isEnabled()), Integer.parseInt(broadsoftSipTransports.getTcp().getKeepalive().getTimeoutSec()));
                            mobileConfigSip.setTcp(tcp);
                        }
                    }

                    if (broadsoftSipTransports.getUdp() != null && broadsoftSipTransports.getUdp().getKeepalive() != null) {

                        if (broadsoftSipTransports.getUdp().getKeepalive().isEnabled() != null && broadsoftSipTransports.getUdp().getKeepalive().getTimeoutSec() != null) {
                            Udp udp = new Udp(Boolean.valueOf(broadsoftSipTransports.getUdp().getKeepalive().isEnabled()), Integer.parseInt(broadsoftSipTransports.getUdp().getKeepalive().getTimeoutSec()));
                            mobileConfigSip.setUdp(udp);
                        }
                    }
                }
            }

            if (mobileConfigResponse.getConfigProtocols().getBroadsoftVoicemailProtocol() != null) {
                mobileConfig.setVoicemailPhoneNumber(mobileConfigResponse.getConfigProtocols().getBroadsoftVoicemailProtocol().getVoicemailCenterNumber());
            }
        }

        BroadsoftMobileConfigServices configServices = mobileConfigResponse.getConfigServices();

        if (configServices != null) {
            BroadsoftServicesSupplementaryServices supplementaryServices = configServices.getSupplementaryServices();

            if (supplementaryServices != null) {
                BroadsoftSupplementaryServicesXsi supplementaryServicesXsi = supplementaryServices.getXsi();

                if (supplementaryServicesXsi != null) {
                    if (supplementaryServicesXsi.getBroadworksAnywhere() != null &&
                            supplementaryServicesXsi.getBroadworksAnywhere().isEnabled() != null) {

                        configManager.setNextivaAnywhereEnabled(Boolean.valueOf(supplementaryServicesXsi.getBroadworksAnywhere().isEnabled()));
                        mobileConfigXsi.setNextivaAnywhereEnabled(Boolean.valueOf(supplementaryServicesXsi.getBroadworksAnywhere().isEnabled()));
                    }

                    if (supplementaryServicesXsi.getRemoteOffice() != null &&
                            supplementaryServicesXsi.getRemoteOffice().isEnabled() != null) {

                        configManager.setRemoteOfficeEnabled(Boolean.valueOf(supplementaryServicesXsi.getRemoteOffice().isEnabled()));
                        mobileConfigXsi.setRemoteOfficeEnabled(Boolean.valueOf(supplementaryServicesXsi.getRemoteOffice().isEnabled()));
                    }
                }
            }

            if (configServices.getCalls() != null) {

                BroadsoftServicesCalls calls = configServices.getCalls();
                Conference conference = new Conference();

                if (calls.getConference() != null) {
                    BroadsoftCallsConference callsConference = calls.getConference();

                    if (callsConference.isEnabled() != null && callsConference.isEnabled() != null) {
                        conference.setEnabled(Boolean.valueOf(callsConference.isEnabled()));
                    }

                    if (callsConference.getXsiEnabled() != null && callsConference.getXsiEnabled() != null) {
                        conference.setXSIEnabled(Boolean.valueOf(callsConference.getXsiEnabled()));
                    }

                    if (callsConference.getServiceUri() != null) {
                        conference.setServiceURI(callsConference.getServiceUri());
                    }

                    if (callsConference.getCallParticipants() != null && callsConference.getCallParticipants().isEnabled() != null) {
                        conference.setCallParticipants(Boolean.valueOf(callsConference.getCallParticipants().isEnabled()));
                    }

                    if (callsConference.getSubscribeConferenceInfo() != null && callsConference.getSubscribeConferenceInfo().isEnabled() != null) {
                        conference.setSubscribeConferenceInfo(Boolean.valueOf(callsConference.getSubscribeConferenceInfo().isEnabled()));
                    }

                    if (callsConference.getDoNotHoldConferenceBeforeRefers() != null && callsConference.getDoNotHoldConferenceBeforeRefers().isEnabled() != null) {
                        conference.setDoNotHoldConferenceBeforeRefers(Boolean.valueOf(callsConference.getDoNotHoldConferenceBeforeRefers().isEnabled()));
                    }

                    mobileConfigSip.setConference(conference);

                }

                if (calls.getAudio() != null) {
                    BroadsoftCallsAudio callsAudio = calls.getAudio();

                    if (callsAudio.getAudioCodecsList() != null) {
                        ArrayList<AudioCodec> audioCodecArrayList = new ArrayList<>();
                        AudioCodec audioCodec;

                        if (callsAudio.getAudioQualityEnhancements() != null && callsAudio.getAudioQualityEnhancements().getQos() != null && callsAudio.getAudioQualityEnhancements().getQos().isEnabled() != null) {
                            mobileConfigCalls.setAudioQos(Boolean.valueOf(callsAudio.getAudioQualityEnhancements().getQos().isEnabled()));
                        }

                        for (BroadsoftCallsAudioCodec broadsoftCallsAudioCodec : callsAudio.getAudioCodecsList()) {
                            audioCodec = new AudioCodec();
                            audioCodec.setInBand(broadsoftCallsAudioCodec.getInBand());
                            audioCodec.setName(broadsoftCallsAudioCodec.getName());
                            audioCodec.setPayload(broadsoftCallsAudioCodec.getPayload());
                            audioCodec.setPriority(broadsoftCallsAudioCodec.getPriority());
                            audioCodecArrayList.add(audioCodec);
                        }

                        mobileConfigSip.setAudioCodecs(audioCodecArrayList);
                    }
                }

                if (calls.getVideo() != null) {
                    BroadsoftCallsVideo callsVideo = calls.getVideo();


                    if (callsVideo.getVideoQualityEnhancements() != null && callsVideo.getVideoQualityEnhancements().getQos() != null && callsVideo.getVideoQualityEnhancements().getQos().isEnabled() != null) {
                        mobileConfigCalls.setVideoQos(Boolean.valueOf(callsVideo.getVideoQualityEnhancements().getQos().isEnabled()));
                    }

                    if (callsVideo.getVideoCodecsList() != null) {
                        ArrayList<VideoCodec> videoCodecArrayList = new ArrayList<>();
                        VideoCodec videoCodec;
                        for (BroadsoftCallsVideoCodec broadsoftCallsVideoCodec : callsVideo.getVideoCodecsList()) {
                            videoCodec = new VideoCodec();
                            videoCodec.setBitrate(broadsoftCallsVideoCodec.getBitrate());
                            videoCodec.setFramerate(broadsoftCallsVideoCodec.getFramerate());
                            videoCodec.setName(broadsoftCallsVideoCodec.getName());
                            videoCodec.setPayload(broadsoftCallsVideoCodec.getPayload());
                            videoCodec.setPriority(broadsoftCallsVideoCodec.getPriority());
                            videoCodec.setResolution(broadsoftCallsVideoCodec.getResolution());
                            videoCodecArrayList.add(videoCodec);
                        }
                        mobileConfigSip.setVideoCodecs(videoCodecArrayList);
                    }
                }

                if (calls.getRejectWith486() != null && calls.getRejectWith486().isEnabled() != null) {
                    mobileConfigCalls.setRejectWith486(Boolean.valueOf(calls.getRejectWith486().isEnabled()));
                }
            }

            if (configServices.getEmergencyDialing() != null) {
                if (configServices.getEmergencyDialing().getNumbers() != null && !configServices.getEmergencyDialing().getNumbers().isEmpty()) {
                    mobileConfig.setEmergencyNumbers(configServices.getEmergencyDialing().getNumbers());

                }

            }

        }

        mobileConfig.setXsi(mobileConfigXsi);
        mobileConfig.setXmpp(mobileConfigXmpp);
        mobileConfig.setSip(mobileConfigSip);
        mobileConfig.setCalls(mobileConfigCalls);
        configManager.setMobileConfig(mobileConfig);
    }
}
