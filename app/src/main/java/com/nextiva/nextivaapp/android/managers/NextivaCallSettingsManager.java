package com.nextiva.nextivaapp.android.managers;

import android.app.Application;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.HeaderListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.LocalSettingListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ServiceSettingsListItem;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.UserRepository;
import com.nextiva.nextivaapp.android.managers.interfaces.CallSettingsManager;
import com.nextiva.nextivaapp.android.managers.interfaces.ConfigManager;
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager;
import com.nextiva.nextivaapp.android.managers.interfaces.SettingsManager;
import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager;
import com.nextiva.nextivaapp.android.models.ListHeaderRow;
import com.nextiva.nextivaapp.android.models.NextivaAnywhereLocation;
import com.nextiva.nextivaapp.android.models.Resource;
import com.nextiva.nextivaapp.android.models.ServiceSettings;
import com.nextiva.nextivaapp.android.models.net.broadsoft.services.BroadsoftCallCenter;
import com.nextiva.nextivaapp.android.models.net.broadsoft.services.BroadsoftCallCenterUnavailableCodes;
import com.nextiva.nextivaapp.android.models.net.broadsoft.services.BroadsoftUnavailableDetail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.disposables.Disposable;

@Singleton
@SuppressWarnings("ConstantConditions")
public class NextivaCallSettingsManager implements CallSettingsManager {

    private static final String[] FILTER_SERVICE_SETTINGS = new String[] {
            Enums.Service.TYPE_DO_NOT_DISTURB,
            Enums.Service.TYPE_CALL_FORWARDING_ALWAYS,
            Enums.Service.TYPE_CALL_FORWARDING_BUSY,
            Enums.Service.TYPE_CALL_FORWARDING_NO_ANSWER,
            Enums.Service.TYPE_CALL_FORWARDING_NOT_REACHABLE,
            Enums.Service.TYPE_REMOTE_OFFICE,
            Enums.Service.TYPE_CALLING_LINE_ID_DELIVERY_BLOCKING,
            Enums.Service.TYPE_BROADWORKS_ANYWHERE,
            Enums.Service.TYPE_SIMULTANEOUS_RING_PERSONAL};

    private final Application mApplication;
    private final ConfigManager mConfigManager;
    private final SettingsManager mSettingsManager;
    private final UserRepository mUserRepository;
    private final SessionManager mSessionManager;

    private final MutableLiveData<Resource<List<BaseListItem>>> mListItemsListMutableLiveData = new MutableLiveData<>();

    private final HashMap<String, ServiceSettings> mServiceSettingsMap;

    private BroadsoftCallCenter mBroadsoftCallCenter;
    private BroadsoftCallCenterUnavailableCodes mBroadsoftCallCenterUnavailableCodes;

    @Inject
    public NextivaCallSettingsManager(
            @NonNull Application application,
            @NonNull ConfigManager configManager,
            @NonNull SessionManager sessionManager,
            @NonNull SettingsManager settingsManager,
            @NonNull UserRepository userRepository) {

        mApplication = application;
        mConfigManager = configManager;
        mSettingsManager = settingsManager;
        mUserRepository = userRepository;
        mSessionManager = sessionManager;

        mServiceSettingsMap = new HashMap<>();
        mServiceSettingsMap.put(Enums.Service.TYPE_BROADWORKS_ANYWHERE, sessionManager.getNextivaAnywhereServiceSettings());
        mServiceSettingsMap.put(Enums.Service.TYPE_REMOTE_OFFICE, sessionManager.getRemoteOfficeServiceSettings());
    }

    private String getServiceSettingsSubTitle(@NonNull ServiceSettings serviceSettings) {
        if (!TextUtils.isEmpty(serviceSettings.getType())) {
            switch (serviceSettings.getType()) {
                case Enums.Service.TYPE_DO_NOT_DISTURB:
                case Enums.Service.TYPE_CALLING_LINE_ID_DELIVERY_BLOCKING: {
                    return serviceSettings.getActive() ? mApplication.getString(R.string.general_on) : mApplication.getString(R.string.general_off);
                }
                case Enums.Service.TYPE_CALL_FORWARDING_ALWAYS:
                case Enums.Service.TYPE_CALL_FORWARDING_BUSY:
                case Enums.Service.TYPE_CALL_FORWARDING_NO_ANSWER:
                case Enums.Service.TYPE_CALL_FORWARDING_NOT_REACHABLE: {
                    return serviceSettings.getActive() ? PhoneNumberUtils.formatNumber(serviceSettings.getForwardToPhoneNumber(), Locale.getDefault().getCountry()) : mApplication.getString(R.string.general_off);
                }
                case Enums.Service.TYPE_REMOTE_OFFICE: {
                    return serviceSettings.getActive() ? PhoneNumberUtils.formatNumber(serviceSettings.getRemoteOfficeNumber(), Locale.getDefault().getCountry()) : mApplication.getString(R.string.general_off);
                }
                case Enums.Service.TYPE_BROADWORKS_ANYWHERE: {
                    ArrayList<NextivaAnywhereLocation> nextivaAnywhereLocationsList = new ArrayList<>();

                    if (serviceSettings.getNextivaAnywhereLocationsList() != null) {
                        for (NextivaAnywhereLocation location : serviceSettings.getNextivaAnywhereLocationsList()) {
                            if (location.getActive()) {
                                nextivaAnywhereLocationsList.add(location);
                            }

                            if (nextivaAnywhereLocationsList.size() == 2) {
                                break;
                            }
                        }
                    }

                    if (nextivaAnywhereLocationsList.size() >= 2) {
                        return mApplication.getString(R.string.general_multiple_locations);

                    } else if (nextivaAnywhereLocationsList.size() == 1) {
                        if (!TextUtils.isEmpty(nextivaAnywhereLocationsList.get(0).getDescription())) {
                            return nextivaAnywhereLocationsList.get(0).getDescription();

                        } else {
                            return PhoneNumberUtils.formatNumber(nextivaAnywhereLocationsList.get(0).getPhoneNumber(),
                                                                 Locale.getDefault().getCountry());
                        }

                    } else {
                        return mApplication.getString(R.string.general_off);
                    }
                }
                case Enums.Service.TYPE_SIMULTANEOUS_RING_PERSONAL: {
                    if (serviceSettings.getActive()) {
                        int activeLocations = serviceSettings.getSimultaneousRingLocationsList() != null ?
                                serviceSettings.getSimultaneousRingLocationsList().size() :
                                0;

                        if (activeLocations >= 2) {
                            return mApplication.getString(R.string.general_multiple_locations);

                        } else if (activeLocations == 1) {
                            return PhoneNumberUtils.formatNumber(serviceSettings.getSimultaneousRingLocationsList().get(0).getPhoneNumber(),
                                                                 Locale.getDefault().getCountry());

                        } else {
                            return mApplication.getString(R.string.general_off);
                        }

                    } else {
                        return mApplication.getString(R.string.general_off);
                    }
                }
            }
        }

        return null;
    }

    private String getLocalSettingsSubTitle(@NonNull @SharedPreferencesManager.SettingsKey String settingKey) {
        switch (settingKey) {
            case SharedPreferencesManager.DIALING_SERVICE: {
                switch (mSettingsManager.getDialingService()) {
                    case Enums.Service.DialingServiceTypes.VOIP: {
                        return mApplication.getString(R.string.call_dialing_service_voip);
                    }
                    case Enums.Service.DialingServiceTypes.CALL_BACK: {
                        return mApplication.getString(R.string.call_dialing_service_call_back);
                    }
                    case Enums.Service.DialingServiceTypes.CALL_THROUGH: {
                        return mApplication.getString(R.string.call_dialing_service_call_through);
                    }
                    case Enums.Service.DialingServiceTypes.THIS_PHONE: {
                        return mApplication.getString(R.string.call_dialing_service_this_phone);
                    }
                    case Enums.Service.DialingServiceTypes.ALWAYS_ASK: {
                        return mApplication.getString(R.string.set_call_settings_dialing_service_always_ask);
                    }
                    case Enums.Service.DialingServiceTypes.NONE:
                        return null;
                }
            }
            case SharedPreferencesManager.THIS_PHONE_NUMBER: {
                if (!TextUtils.isEmpty(mSettingsManager.getPhoneNumber())) {
                    return PhoneNumberUtils.formatNumber(mSettingsManager.getPhoneNumber(),
                                                         Locale.getDefault().getCountry());
                } else {
                    return mApplication.getString(R.string.call_settings_this_phone_number_configure);
                }
            }
            case SharedPreferencesManager.CALL_CENTER_STATUS: {
                switch (mSettingsManager.getCallCenterStatus()) {
                    case Enums.Service.CallCenterServiceStatuses.AVAILABLE: {
                        return mApplication.getString(R.string.call_settings_call_center_available);
                    }
                    case Enums.Service.CallCenterServiceStatuses.UNAVAILABLE: {
                        return mApplication.getString(R.string.call_settings_call_center_unavailable);
                    }
                    case Enums.Service.CallCenterServiceStatuses.SIGN_IN: {
                        return mApplication.getString(R.string.call_settings_call_center_sign_in);
                    }
                    case Enums.Service.CallCenterServiceStatuses.SIGN_OUT: {
                        return mApplication.getString(R.string.call_settings_call_center_sign_out);
                    }
                    case Enums.Service.CallCenterServiceStatuses.WRAP_UP: {
                        return mApplication.getString(R.string.call_settings_call_center_wrap_up);
                    }
                    case Enums.Service.CallCenterServiceStatuses.NONE:
                    default: {
                        return "";
                    }

                }
            }
            case SharedPreferencesManager.ALLOW_TERMINATION:
                return mSessionManager.getAllowTermination() ? mApplication.getString(R.string.general_on) : mApplication.getString(R.string.general_off);
        }

        return null;
    }

    @VisibleForTesting
    public HashMap<String, ServiceSettings> getServiceSettingsMap() {
        return mServiceSettingsMap;
    }

    // --------------------------------------------------------------------------------------------
    // CallSettingsManager Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public LiveData<Resource<List<BaseListItem>>> getListItemsListLiveData() {
        return mListItemsListMutableLiveData;
    }

    @Override
    public Disposable getFilteredServiceSettings() {
        mListItemsListMutableLiveData.setValue(Resource.loading(null));

        return mUserRepository.getServiceSettingsFiltered(
                FILTER_SERVICE_SETTINGS)
                .subscribe(event -> {
                               if (event.isSuccessful()) {
                                   mServiceSettingsMap.clear();

                                   if (event.getServiceSettingsMap() != null) {
                                       mServiceSettingsMap.putAll(event.getServiceSettingsMap());
                                   }

                                   processServiceSettings();

                               } else {
                                   mListItemsListMutableLiveData.setValue(Resource.error("", null));
                               }
                           },
                           throwable -> {
                               FirebaseCrashlytics.getInstance().recordException(throwable);
                               mListItemsListMutableLiveData.setValue(Resource.error("", null));
                            });
    }

    @Override
    public void clearServiceSettings() {
        mServiceSettingsMap.clear();
    }

    public void processServiceSettings() {
        mServiceSettingsMap.values().removeAll(Collections.singleton(null));

        List<BaseListItem> listItemsList = new ArrayList<>();

        if (!TextUtils.isEmpty(mSettingsManager.getCallCenterStatus()) && mBroadsoftCallCenter != null) {

            listItemsList.add(new HeaderListItem(new ListHeaderRow(mApplication.getString(R.string.call_settings_services)), null, false, false));

            String callCenterStatus = "";
            if (!TextUtils.isEmpty(mBroadsoftCallCenter.getAgentACDState()) &&
                    mBroadsoftCallCenter.getAgentACDState().contains(Enums.Service.CallCenterServiceStatuses.UNAVAILABLE)) {
                for (BroadsoftUnavailableDetail broadsoftUnavailableDetail : mBroadsoftCallCenterUnavailableCodes.getUnavailableCodes()) {
                    if (broadsoftUnavailableDetail != null &&
                            mBroadsoftCallCenter != null &&
                            mBroadsoftCallCenter.getAgentUnavailableCode() != null &&
                            mBroadsoftCallCenter.getAgentUnavailableCode().equals(broadsoftUnavailableDetail.getCode())) {
                        callCenterStatus = mBroadsoftCallCenter.getAgentACDState() + ": " + broadsoftUnavailableDetail.getCode() + "-" + ((broadsoftUnavailableDetail.getDescription() != null)? broadsoftUnavailableDetail.getDescription() : "");
                        break;
                    }
                }
                if (TextUtils.isEmpty(callCenterStatus)) {
                    callCenterStatus = mBroadsoftCallCenter.getAgentACDState();
                }
            } else if (!TextUtils.isEmpty(mBroadsoftCallCenter.getAgentACDState())) {
                callCenterStatus = mBroadsoftCallCenter.getAgentACDState();
            }


            listItemsList.add(new LocalSettingListItem(SharedPreferencesManager.CALL_CENTER_STATUS,
                                                       mApplication.getString(R.string.call_settings_call_center),
                                                       callCenterStatus));
        }

        if ((mConfigManager.getNextivaAnywhereEnabled() && mServiceSettingsMap.containsKey(Enums.Service.TYPE_BROADWORKS_ANYWHERE)) ||
                (mConfigManager.getRemoteOfficeEnabled() && mServiceSettingsMap.containsKey(Enums.Service.TYPE_REMOTE_OFFICE)) ||
                mServiceSettingsMap.containsKey(Enums.Service.TYPE_SIMULTANEOUS_RING_PERSONAL)) {
            listItemsList.add(new HeaderListItem(new ListHeaderRow(mApplication.getString(R.string.call_settings_mobility_section_header)), null, false, false));
        }

        if (mServiceSettingsMap.get(Enums.Service.TYPE_BROADWORKS_ANYWHERE) != null && mServiceSettingsMap.containsKey(Enums.Service.TYPE_BROADWORKS_ANYWHERE) && mConfigManager.getNextivaAnywhereEnabled()) {
            listItemsList.add(new ServiceSettingsListItem(
                    mServiceSettingsMap.get(Enums.Service.TYPE_BROADWORKS_ANYWHERE),
                    mApplication.getString(R.string.call_settings_nextiva_anywhere_title),
                    getServiceSettingsSubTitle(mServiceSettingsMap.get(Enums.Service.TYPE_BROADWORKS_ANYWHERE))));
        }

        if (mServiceSettingsMap.containsKey(Enums.Service.TYPE_REMOTE_OFFICE) && mConfigManager.getRemoteOfficeEnabled()) {
            listItemsList.add(new ServiceSettingsListItem(
                    mServiceSettingsMap.get(Enums.Service.TYPE_REMOTE_OFFICE),
                    mApplication.getString(R.string.call_settings_remote_office_title),
                    getServiceSettingsSubTitle(mServiceSettingsMap.get(Enums.Service.TYPE_REMOTE_OFFICE))));
        }

        if (mServiceSettingsMap.containsKey(Enums.Service.TYPE_SIMULTANEOUS_RING_PERSONAL)) {
            listItemsList.add(new ServiceSettingsListItem(
                    mServiceSettingsMap.get(Enums.Service.TYPE_SIMULTANEOUS_RING_PERSONAL),
                    mApplication.getString(R.string.call_settings_simultaneous_ring_title),
                    getServiceSettingsSubTitle(mServiceSettingsMap.get(Enums.Service.TYPE_SIMULTANEOUS_RING_PERSONAL))));
        }

        if (mServiceSettingsMap.containsKey(Enums.Service.TYPE_CALL_FORWARDING_ALWAYS) ||
                mServiceSettingsMap.containsKey(Enums.Service.TYPE_CALL_FORWARDING_BUSY) ||
                mServiceSettingsMap.containsKey(Enums.Service.TYPE_CALL_FORWARDING_NO_ANSWER) ||
                mServiceSettingsMap.containsKey(Enums.Service.TYPE_CALL_FORWARDING_NOT_REACHABLE)) {
            listItemsList.add(new HeaderListItem(new ListHeaderRow(mApplication.getString(R.string.call_settings_forwarding_section_header)), null, false, false));
        }

        if (mServiceSettingsMap.containsKey(Enums.Service.TYPE_CALL_FORWARDING_ALWAYS)) {
            listItemsList.add(new ServiceSettingsListItem(
                    mServiceSettingsMap.get(Enums.Service.TYPE_CALL_FORWARDING_ALWAYS),
                    mApplication.getString(R.string.call_settings_call_forwarding_forward_always_title),
                    getServiceSettingsSubTitle(mServiceSettingsMap.get(Enums.Service.TYPE_CALL_FORWARDING_ALWAYS))));
        }

        if (mServiceSettingsMap.containsKey(Enums.Service.TYPE_CALL_FORWARDING_BUSY)) {
            listItemsList.add(new ServiceSettingsListItem(
                    mServiceSettingsMap.get(Enums.Service.TYPE_CALL_FORWARDING_BUSY),
                    mApplication.getString(R.string.call_settings_call_forwarding_forward_when_busy_title),
                    getServiceSettingsSubTitle(mServiceSettingsMap.get(Enums.Service.TYPE_CALL_FORWARDING_BUSY))));
        }

        if (mServiceSettingsMap.containsKey(Enums.Service.TYPE_CALL_FORWARDING_NO_ANSWER)) {
            listItemsList.add(new ServiceSettingsListItem(
                    mServiceSettingsMap.get(Enums.Service.TYPE_CALL_FORWARDING_NO_ANSWER),
                    mApplication.getString(R.string.call_settings_call_forwarding_forward_when_unanswered_title),
                    getServiceSettingsSubTitle(mServiceSettingsMap.get(Enums.Service.TYPE_CALL_FORWARDING_NO_ANSWER))));
        }

        if (mServiceSettingsMap.containsKey(Enums.Service.TYPE_CALL_FORWARDING_NOT_REACHABLE)) {
            listItemsList.add(new ServiceSettingsListItem(
                    mServiceSettingsMap.get(Enums.Service.TYPE_CALL_FORWARDING_NOT_REACHABLE),
                    mApplication.getString(R.string.call_settings_call_forwarding_forward_when_unreachable_title),
                    getServiceSettingsSubTitle(mServiceSettingsMap.get(Enums.Service.TYPE_CALL_FORWARDING_NOT_REACHABLE))));
        }

        if (mServiceSettingsMap.containsKey(Enums.Service.TYPE_DO_NOT_DISTURB)) {
            listItemsList.add(new HeaderListItem(new ListHeaderRow(mApplication.getString(R.string.call_settings_routing_section_header)), null, false, false));

            listItemsList.add(new ServiceSettingsListItem(
                    mServiceSettingsMap.get(Enums.Service.TYPE_DO_NOT_DISTURB),
                    mApplication.getString(R.string.call_settings_do_not_disturb_title),
                    getServiceSettingsSubTitle(mServiceSettingsMap.get(Enums.Service.TYPE_DO_NOT_DISTURB))));
        }

        if (mServiceSettingsMap.containsKey(Enums.Service.TYPE_CALLING_LINE_ID_DELIVERY_BLOCKING)) {
            listItemsList.add(new HeaderListItem(new ListHeaderRow(mApplication.getString(R.string.call_settings_call_identification_section_header)), null, false, false));

            listItemsList.add(new ServiceSettingsListItem(
                    mServiceSettingsMap.get(Enums.Service.TYPE_CALLING_LINE_ID_DELIVERY_BLOCKING),
                    mApplication.getString(R.string.call_settings_block_caller_id_title),
                    getServiceSettingsSubTitle(mServiceSettingsMap.get(Enums.Service.TYPE_CALLING_LINE_ID_DELIVERY_BLOCKING))));
        }

        listItemsList.add(new HeaderListItem(new ListHeaderRow(mApplication.getString(R.string.call_settings_call_options_section_header)), null, false, false));
        listItemsList.add(new LocalSettingListItem(SharedPreferencesManager.DIALING_SERVICE,
                                                   mApplication.getString(R.string.call_settings_dialing_service_title),
                                                   getLocalSettingsSubTitle(SharedPreferencesManager.DIALING_SERVICE)));
        listItemsList.add(new LocalSettingListItem(SharedPreferencesManager.THIS_PHONE_NUMBER,
                                                   mApplication.getString(R.string.call_settings_this_phone_number_title),
                                                   getLocalSettingsSubTitle(SharedPreferencesManager.THIS_PHONE_NUMBER)));

        listItemsList.add(new HeaderListItem(new ListHeaderRow(mApplication.getString(R.string.set_call_settings_shared_call_appearance)), null, false, false));
        listItemsList.add(new LocalSettingListItem(SharedPreferencesManager.ALLOW_TERMINATION,
                mApplication.getString(R.string.set_call_settings_allow_termination_text),
                getLocalSettingsSubTitle(SharedPreferencesManager.ALLOW_TERMINATION), false));

        mListItemsListMutableLiveData.setValue(Resource.success(listItemsList));
    }

    @Override
    public void putServiceSetting(@NonNull String type, @Nullable ServiceSettings serviceSettings) {
        if (serviceSettings != null) {
            mServiceSettingsMap.put(type, serviceSettings);
        } else {
            mServiceSettingsMap.remove(type);
        }
    }
    // --------------------------------------------------------------------------------------------

    @Override
    public void setBroadsoftCallCenter(BroadsoftCallCenter broadsoftCallCenter) {
        mBroadsoftCallCenter = broadsoftCallCenter;
    }

    @Override
    public void setBroadsoftCallCenterUnavailableCodes(BroadsoftCallCenterUnavailableCodes broadsoftCallCenterUnavailableCodes) {
        mBroadsoftCallCenterUnavailableCodes = broadsoftCallCenterUnavailableCodes;
    }

    @Override
    public BroadsoftCallCenter getBroadsoftCallCenter() {
        return mBroadsoftCallCenter;
    }

    @Override
    public BroadsoftCallCenterUnavailableCodes getBroadsoftCallCenterUnavailableCodes() {
        return mBroadsoftCallCenterUnavailableCodes;
    }


}
