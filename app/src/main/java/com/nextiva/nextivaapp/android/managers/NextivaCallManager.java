/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.managers;

import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.CALL_BACK_INITIATED_DIALOG_OK_BUTTON_PRESSED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.CALL_BACK_INITIATED_DIALOG_SHOWN;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.CALL_TYPE_SELECTION_DIALOG_CALL_TYPE_SELECTED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.CALL_TYPE_SELECTION_DIALOG_CANCEL_BUTTON_PRESSED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.CALL_TYPE_SELECTION_DIALOG_SHOWN;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.DIALING_SERVICE_SELECTION_DIALOG_CANCEL_BUTTON_PRESSED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.DIALING_SERVICE_SELECTION_DIALOG_DIALING_SERVICE_SELECTED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.DIALING_SERVICE_SELECTION_DIALOG_SHOWN;

import android.app.Activity;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.constants.RequestCodes;
import com.nextiva.nextivaapp.android.dialogs.SimpleListDialogListener;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.UserRepository;
import com.nextiva.nextivaapp.android.managers.interfaces.AnalyticsManager;
import com.nextiva.nextivaapp.android.managers.interfaces.CallManager;
import com.nextiva.nextivaapp.android.managers.interfaces.ConfigManager;
import com.nextiva.nextivaapp.android.managers.interfaces.DialogManager;
import com.nextiva.nextivaapp.android.managers.interfaces.IntentManager;
import com.nextiva.nextivaapp.android.managers.interfaces.LogManager;
import com.nextiva.nextivaapp.android.managers.interfaces.PermissionManager;
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager;
import com.nextiva.nextivaapp.android.managers.interfaces.SettingsManager;
import com.nextiva.nextivaapp.android.util.CallUtil;
import com.nextiva.pjsip.pjsip_lib.sipservice.ParticipantInfo;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by adammacdonald on 2/14/18.
 */

@Singleton
public class NextivaCallManager implements CallManager {

    private final DialogManager mDialogManager;
    private final SettingsManager mSettingsManager;
    private final SessionManager mSessionManager;
    private final UserRepository mUserRepository;
    private final IntentManager mIntentManager;
    private final ConfigManager mConfigManager;
    private final LogManager mLogManager;
    private final PermissionManager mPermissionManager;
    private final AnalyticsManager mAnalyticsManager;

    private final MultiplePhoneNumbersDialogCallback mMultiplePhoneNumbersDialogCallback = this::processParticipantInfo;

    private final ProcessParticipantInfoCallBack mProcessCallInfoCallBack = new ProcessParticipantInfoCallBack() {
        @Override
        public void onParticipantInfoProcessed(
                @NonNull Activity activity,
                @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
                @NonNull ParticipantInfo participantInfo,
                @Nullable String retrievalNumber,
                @NonNull CompositeDisposable compositeDisposable) {

            mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_success_with_message, activity.getString(R.string.log_message_processing_call, participantInfo.toString()));

            if (participantInfo.getCallType() == Enums.Sip.CallTypes.VIDEO) {
                startVideoCall(activity, analyticsScreenName, participantInfo, retrievalNumber);
                return;
            } else if (participantInfo.getCallType() == Enums.Sip.CallTypes.VOICE) {
                if (participantInfo.getDialingServiceType() == Enums.Service.DialingServiceTypes.VOIP) {
                    startVoiceCallUsingVoip(activity, analyticsScreenName, participantInfo, retrievalNumber);
                    return;
                } else if (participantInfo.getDialingServiceType() == Enums.Service.DialingServiceTypes.CALL_BACK) {
                    startVoiceCallUsingCallBack(activity, analyticsScreenName, participantInfo, compositeDisposable);
                    return;
                } else if (participantInfo.getDialingServiceType() == Enums.Service.DialingServiceTypes.CALL_THROUGH) {
                    startVoiceCallUsingCallThrough(activity, analyticsScreenName, participantInfo, compositeDisposable);
                    return;
                } else if (participantInfo.getDialingServiceType() == Enums.Service.DialingServiceTypes.THIS_PHONE) {
                    startVoiceCallUsingThisPhone(activity, analyticsScreenName, participantInfo);
                    return;
                }
            }
            mDialogManager.showErrorDialog(activity, analyticsScreenName);
        }

    };

    @Inject
    public NextivaCallManager(@NonNull DialogManager dialogManager,
                              @NonNull SettingsManager settingsManager,
                              @NonNull SessionManager sessionManager,
                              @NonNull UserRepository userRepository,
                              @NonNull IntentManager intentManager,
                              @NonNull ConfigManager configManager,
                              @NonNull LogManager logManager,
                              @NonNull PermissionManager permissionManager,
                              @NonNull AnalyticsManager analyticsManager) {

        mDialogManager = dialogManager;
        mSettingsManager = settingsManager;
        mSessionManager = sessionManager;
        mUserRepository = userRepository;
        mIntentManager = intentManager;
        mConfigManager = configManager;
        mLogManager = logManager;
        mPermissionManager = permissionManager;
        mAnalyticsManager = analyticsManager;
    }

    private void showMultiplePhoneNumbersDialog(
            @NonNull final Activity activity,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @NonNull final ParticipantInfo participantInfo,
            @NonNull final CompositeDisposable compositeDisposable,
            @NonNull final MultiplePhoneNumbersDialogCallback multiplePhoneNumbersDialogCallback,
            @NonNull final ProcessParticipantInfoCallBack processCallInfoCallBack) {

        if (participantInfo.getContactId() == null) {
            mDialogManager.showErrorDialog(activity, analyticsScreenName);
            return;
        }

        final ArrayList<String> numbersList = new ArrayList<>();

        ArrayList<String> numberObjectsList = participantInfo.getContactNumbersSorted();

        if (numbersList.isEmpty()) {
            mDialogManager.showErrorDialog(activity,
                                           analyticsScreenName,
                                           Enums.Analytics.EventName.NO_NUMBER_FOUND_DIALOG_SHOWN,
                                           null,
                                           activity.getString(R.string.error_no_phone_number_message),
                                           (dialog, which) -> mAnalyticsManager.logEvent(analyticsScreenName, Enums.Analytics.EventName.NO_NUMBER_FOUND_DIALOG_OK_BUTTON_PRESSED));

        } else if (numbersList.size() == 1) {
            participantInfo.setNumberToCall(numbersList.get(0));
            multiplePhoneNumbersDialogCallback.onProcessCall(activity, analyticsScreenName, participantInfo, compositeDisposable, processCallInfoCallBack);

        } else {
            mDialogManager.showSimpleListDialog(
                    activity,
                    activity.getString(R.string.call_select_phone_number_title),
                    numbersList,
                    position -> {
                        participantInfo.setNumberToCall(numbersList.get(position));
                        multiplePhoneNumbersDialogCallback.onProcessCall(activity, analyticsScreenName, participantInfo, compositeDisposable, processCallInfoCallBack);
                        mAnalyticsManager.logEvent(analyticsScreenName, Enums.Analytics.EventName.MULTI_NUMBER_SELECTION_DIALOG_NUMBER_SELECTED);
                    },
                    (dialog, which) -> mAnalyticsManager.logEvent(analyticsScreenName, Enums.Analytics.EventName.MULTI_NUMBER_SELECTION_DIALOG_CANCEL_BUTTON_PRESSED));

            mAnalyticsManager.logEvent(analyticsScreenName, Enums.Analytics.EventName.MULTI_NUMBER_SELECTION_DIALOG_SHOWN);
        }
    }

    private void showCallTypeDialog(
            @NonNull final Activity activity,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @NonNull final ParticipantInfo participantInfo,
            @NonNull final CompositeDisposable compositeDisposable,
            @NonNull final ProcessParticipantInfoCallBack processCallInfoCallBack) {

        final List<String> itemsList = new ArrayList<>();
        itemsList.add(activity.getString(R.string.call_call_type_voice));
        itemsList.add(activity.getString(R.string.call_call_type_video));

        mDialogManager.showSimpleListDialog(
                activity,
                activity.getString(R.string.call_select_call_type_title),
                itemsList,
                position -> {
                    if (TextUtils.equals(itemsList.get(position), activity.getString(R.string.call_call_type_voice))) {
                        participantInfo.setCallType(Enums.Sip.CallTypes.VOICE);

                    } else if (TextUtils.equals(itemsList.get(position), activity.getString(R.string.call_call_type_video))) {
                        participantInfo.setCallType(Enums.Sip.CallTypes.VIDEO);
                    }

                    processParticipantInfo(activity, analyticsScreenName, participantInfo, compositeDisposable, processCallInfoCallBack);
                    mAnalyticsManager.logEvent(analyticsScreenName, CALL_TYPE_SELECTION_DIALOG_CALL_TYPE_SELECTED);
                },
                (dialog, which) -> mAnalyticsManager.logEvent(analyticsScreenName, CALL_TYPE_SELECTION_DIALOG_CANCEL_BUTTON_PRESSED));

        mAnalyticsManager.logEvent(analyticsScreenName, CALL_TYPE_SELECTION_DIALOG_SHOWN);
    }

    private void showDialingServicesDialog(
            @NonNull final Activity activity,
            @NonNull final @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @NonNull final ParticipantInfo participantInfo,
            @NonNull final CompositeDisposable compositeDisposable,
            @NonNull final ProcessParticipantInfoCallBack processCallInfoCallBack) {

        boolean isRemoteOfficeLicensingEnabled = mConfigManager.getRemoteOfficeEnabled() && mSessionManager.getRemoteOfficeServiceSettings() != null;
        boolean isNextivaAnywhereLicensingEnabled = mConfigManager.getNextivaAnywhereEnabled() && mSessionManager.getNextivaAnywhereServiceSettings() != null;

        boolean isCallBackEnabled = mSessionManager.getIsCallBackEnabled(mSessionManager.getRemoteOfficeServiceSettings(), mSessionManager.getNextivaAnywhereServiceSettings());
        boolean isCallThroughEnabled = mSessionManager.getIsCallThroughEnabled(mSessionManager.getNextivaAnywhereServiceSettings(), mSettingsManager.getPhoneNumber());
        boolean isVoipDisallowed = false;
        boolean isCallBackDisallowed = false;
        boolean isCallThroughDisallowed = false;
        boolean isThisPhoneDisallowed = false;

        if (participantInfo.getDisallowDialingServiceTypes() != null) {
            for (int disallowedDialingService : participantInfo.getDisallowDialingServiceTypes()) {
                switch (disallowedDialingService) {
                    case Enums.Service.DialingServiceTypes.VOIP: {
                        isVoipDisallowed = true;
                        break;
                    }
                    case Enums.Service.DialingServiceTypes.CALL_BACK: {
                        isCallBackDisallowed = true;
                        break;
                    }
                    case Enums.Service.DialingServiceTypes.CALL_THROUGH: {
                        isCallThroughDisallowed = true;
                        break;
                    }
                    case Enums.Service.DialingServiceTypes.THIS_PHONE: {
                        isThisPhoneDisallowed = true;
                        break;
                    }
                }
            }
        }

        final List<String> itemsList = new ArrayList<>();

        if (!isVoipDisallowed) {
            itemsList.add(activity.getString(R.string.call_dialing_service_voip));
        }

        if ((isRemoteOfficeLicensingEnabled || isNextivaAnywhereLicensingEnabled) && isCallBackEnabled && !isCallBackDisallowed) {
            itemsList.add(activity.getString(R.string.call_dialing_service_call_back));
        }

        if (isNextivaAnywhereLicensingEnabled && isCallThroughEnabled && !isCallThroughDisallowed) {
            itemsList.add(activity.getString(R.string.call_dialing_service_call_through));
        }

        if (mIntentManager.isAbleToMakePhoneCall(activity) && !isThisPhoneDisallowed) {
            itemsList.add(activity.getString(R.string.call_dialing_service_this_phone));
        }

        SimpleListDialogListener listDialogListener = position -> {
            if (position >= 0) {
                if (TextUtils.equals(itemsList.get(position), activity.getString(R.string.call_dialing_service_voip))) {
                    participantInfo.setDialingServiceType(Enums.Service.DialingServiceTypes.VOIP);

                } else if (TextUtils.equals(itemsList.get(position), activity.getString(R.string.call_dialing_service_call_back))) {
                    participantInfo.setDialingServiceType(Enums.Service.DialingServiceTypes.CALL_BACK);

                } else if (TextUtils.equals(itemsList.get(position), activity.getString(R.string.call_dialing_service_call_through))) {
                    participantInfo.setDialingServiceType(Enums.Service.DialingServiceTypes.CALL_THROUGH);

                } else if (TextUtils.equals(itemsList.get(position), activity.getString(R.string.call_dialing_service_this_phone))) {
                    participantInfo.setDialingServiceType(Enums.Service.DialingServiceTypes.THIS_PHONE);
                }
            } else {
                participantInfo.setDialingServiceType(participantInfo.getDisallowDialingServiceTypes()[0]);
            }

            processParticipantInfo(activity, analyticsScreenName, participantInfo, compositeDisposable, processCallInfoCallBack);
            mAnalyticsManager.logEvent(analyticsScreenName, DIALING_SERVICE_SELECTION_DIALOG_DIALING_SERVICE_SELECTED);
        };

        @Enums.Service.DialingServiceTypes.DialingServiceType int dialingServiceType = mSettingsManager.getDialingService();

        if (participantInfo.getDialingServiceType() != Enums.Service.DialingServiceTypes.NONE) {
            dialingServiceType = participantInfo.getDialingServiceType();
        }

        switch (dialingServiceType) {
            case Enums.Service.DialingServiceTypes.ALWAYS_ASK:
                if (itemsList.size() > 1) {
                    mDialogManager.showSimpleListDialog(
                            activity,
                            activity.getString(R.string.call_select_dialing_service_title),
                            itemsList,
                            listDialogListener,
                            (dialog, which) -> mAnalyticsManager.logEvent(analyticsScreenName, DIALING_SERVICE_SELECTION_DIALOG_CANCEL_BUTTON_PRESSED));

                    mAnalyticsManager.logEvent(analyticsScreenName, DIALING_SERVICE_SELECTION_DIALOG_SHOWN);

                } else {
                    listDialogListener.onSelectionMade(0);
                }
                break;

            case Enums.Service.DialingServiceTypes.CALL_BACK:
                listDialogListener.onSelectionMade(itemsList.indexOf(activity.getString(R.string.call_dialing_service_call_back)));
                break;

            case Enums.Service.DialingServiceTypes.CALL_THROUGH:
                listDialogListener.onSelectionMade(itemsList.indexOf(activity.getString(R.string.call_dialing_service_call_through)));
                break;

            case Enums.Service.DialingServiceTypes.THIS_PHONE:
                listDialogListener.onSelectionMade(itemsList.indexOf(activity.getString(R.string.call_dialing_service_this_phone)));
                break;

            case Enums.Service.DialingServiceTypes.VOIP:
                listDialogListener.onSelectionMade(itemsList.indexOf(activity.getString(R.string.call_dialing_service_voip)));
                break;
            case Enums.Service.DialingServiceTypes.NONE:
                break;
        }
    }

    private void startVideoCall(
            @NonNull final Activity activity,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @NonNull final ParticipantInfo participantInfo,
            @Nullable final String retrievalNumber) {

        mPermissionManager.requestVideoCallPermission(
                activity,
                analyticsScreenName,
                () -> activity.startActivityForResult(mIntentManager.newActiveCallActivityIntent(activity, participantInfo, retrievalNumber),
                                                      RequestCodes.PLACE_CALL_REQUEST_CODE),
                null);
    }

    private void startVoiceCallUsingVoip(
            @NonNull final Activity activity,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @NonNull final ParticipantInfo participantInfo,
            @Nullable final String retrievalNumber) {

        mPermissionManager.requestVoiceCallPermission(
                activity,
                analyticsScreenName,
                () -> activity.startActivityForResult(mIntentManager.newActiveCallActivityIntent(activity, participantInfo, retrievalNumber),
                                                      RequestCodes.PLACE_CALL_REQUEST_CODE));
    }

    private void startVoiceCallUsingCallBack(
            @NonNull final Activity activity,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @NonNull ParticipantInfo participantInfo,
            @NonNull CompositeDisposable compositeDisposable) {

        if (TextUtils.isEmpty(participantInfo.getNumberToCall())) {
            mDialogManager.showErrorDialog(activity, analyticsScreenName);

        } else {
            mDialogManager.showProgressDialog(activity, analyticsScreenName, R.string.progress_performing_call_back);

            compositeDisposable.add(
                    mUserRepository.postNewCallBackCall(CallUtil.getStrippedPhoneNumber(participantInfo.getNumberToCall()))
                            .subscribe(callBackCallResponseEvent -> {
                                mDialogManager.dismissProgressDialog();

                                if (callBackCallResponseEvent.isSuccessful()) {
                                    mAnalyticsManager.logEvent(analyticsScreenName, CALL_BACK_INITIATED_DIALOG_SHOWN);

                                    mDialogManager.showDialog(
                                            activity,
                                            null,
                                            activity.getString(R.string.call_back_initiated),
                                            activity.getString(R.string.general_ok),
                                            (dialog, which) -> mAnalyticsManager.logEvent(analyticsScreenName, CALL_BACK_INITIATED_DIALOG_OK_BUTTON_PRESSED));

                                } else {
                                    mDialogManager.showErrorDialog(activity, analyticsScreenName);
                                }
                            }));
        }
    }

    private void startVoiceCallUsingCallThrough(
            @NonNull final Activity activity,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @NonNull final ParticipantInfo participantInfo,
            @NonNull final CompositeDisposable compositeDisposable) {

        if (TextUtils.isEmpty(participantInfo.getNumberToCall())) {
            mDialogManager.showErrorDialog(activity, analyticsScreenName);

        } else {
            mPermissionManager.requestPhonePermission(
                    activity,
                    analyticsScreenName,
                    () -> {
                        if (!TextUtils.isEmpty(participantInfo.getNumberToCall()) && !TextUtils.isEmpty(mSettingsManager.getPhoneNumber())) {
                            mDialogManager.showProgressDialog(activity, analyticsScreenName, R.string.progress_performing_call_through);

                            compositeDisposable.add(
                                    mUserRepository.postNewCallThroughCall(mSettingsManager.getPhoneNumber(), participantInfo.getNumberToCall())
                                            .subscribe(callThroughCallResponseEvent -> {
                                                mDialogManager.dismissProgressDialog();

                                                if (callThroughCallResponseEvent.getCallThroughNumber() != null && callThroughCallResponseEvent.isSuccessful()) {
                                                    mIntentManager.callPhone(activity, analyticsScreenName, callThroughCallResponseEvent.getCallThroughNumber());
                                                } else {
                                                    mDialogManager.showErrorDialog(activity, analyticsScreenName);
                                                }
                                            }));
                        }
                    });
        }
    }

    private void startVoiceCallUsingThisPhone(
            @NonNull final Activity activity,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @NonNull final ParticipantInfo participantInfo) {

        if (TextUtils.isEmpty(participantInfo.getNumberToCall())) {
            mDialogManager.showErrorDialog(activity, analyticsScreenName);

        } else {
            mPermissionManager.requestPhonePermission(
                    activity,
                    analyticsScreenName,
                    () -> mIntentManager.callPhone(activity, analyticsScreenName, participantInfo.getNumberToCall()));
        }
    }

    // --------------------------------------------------------------------------------------------
    // CallManager Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public void makeCall(
            @NonNull Activity activity,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @NonNull ParticipantInfo participantInfo,
            @NonNull CompositeDisposable compositeDisposable) {
        processParticipantInfo(activity, analyticsScreenName, participantInfo, compositeDisposable, mProcessCallInfoCallBack);
    }

    @Override
    public void makeCall(@NonNull Activity activity,
                         @NonNull String analyticsScreenName,
                         @NonNull ParticipantInfo participantInfo,
                         @Nullable String retrievalNumber,
                         @NonNull CompositeDisposable compositeDisposable) {
        this.processParticipantInfo(activity, analyticsScreenName, participantInfo, retrievalNumber, compositeDisposable, mProcessCallInfoCallBack);
    }

    @Override
    public void processParticipantInfo(
            @NonNull Activity activity,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @NonNull ParticipantInfo participantInfo,
            @NonNull CompositeDisposable compositeDisposable,
            @NonNull ProcessParticipantInfoCallBack processCallInfoCallBack) {
        this.processParticipantInfo(activity, analyticsScreenName, participantInfo, null, compositeDisposable, processCallInfoCallBack);
    }

    @Override
    public void processParticipantInfo(
            @NonNull Activity activity,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @NonNull ParticipantInfo participantInfo,
            @Nullable String retrievalNumber,
            @NonNull CompositeDisposable compositeDisposable,
            @NonNull ProcessParticipantInfoCallBack processCallInfoCallBack) {


        boolean isEmergency = false;
        if (mConfigManager.getMobileConfig() != null && mConfigManager.getMobileConfig().getEmergencyNumbers() != null) {
            for (String number : mConfigManager.getMobileConfig().getEmergencyNumbers()) {
                if (number.equals(participantInfo.getNumberToCall())) {
                    isEmergency = true;
                    break;
                }
            }
        }

        if (isEmergency) {
            participantInfo.setDialingServiceType(Enums.Service.DialingServiceTypes.THIS_PHONE);
            startVoiceCallUsingThisPhone(activity, analyticsScreenName, participantInfo);
        } else if (TextUtils.isEmpty(participantInfo.getNumberToCall())) {
            showMultiplePhoneNumbersDialog(
                    activity,
                    analyticsScreenName,
                    participantInfo,
                    compositeDisposable,
                    mMultiplePhoneNumbersDialogCallback,
                    processCallInfoCallBack);

        } else if (participantInfo.getCallType() == Enums.Sip.CallTypes.NONE) {
            showCallTypeDialog(activity, analyticsScreenName, participantInfo, compositeDisposable, processCallInfoCallBack);

        } else if (participantInfo.getCallType() == Enums.Sip.CallTypes.VOICE && participantInfo.getDialingServiceType() == Enums.Service.DialingServiceTypes.NONE) {
            showDialingServicesDialog(activity, analyticsScreenName, participantInfo, compositeDisposable, processCallInfoCallBack);

        } else {
            processCallInfoCallBack.onParticipantInfoProcessed(activity, analyticsScreenName, participantInfo, retrievalNumber, compositeDisposable);
        }
    }
    // --------------------------------------------------------------------------------------------

    private interface MultiplePhoneNumbersDialogCallback {
        void onProcessCall(
                @NonNull Activity activity,
                @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
                @NonNull ParticipantInfo participantInfo,
                @NonNull CompositeDisposable compositeDisposable,
                @NonNull ProcessParticipantInfoCallBack processCallInfoCallBack);
    }
}
