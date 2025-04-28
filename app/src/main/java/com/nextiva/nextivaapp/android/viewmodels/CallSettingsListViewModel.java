package com.nextiva.nextivaapp.android.viewmodels;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.nextiva.nextivaapp.android.SetCallSettingsActivity;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.DetailItemViewListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.LocalSettingListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ServiceSettingsListItem;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.constants.RequestCodes;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.UserRepository;
import com.nextiva.nextivaapp.android.managers.interfaces.CallSettingsManager;
import com.nextiva.nextivaapp.android.managers.interfaces.ConnectionStateManager;
import com.nextiva.nextivaapp.android.managers.interfaces.IntentManager;
import com.nextiva.nextivaapp.android.managers.interfaces.SettingsManager;
import com.nextiva.nextivaapp.android.models.Resource;
import com.nextiva.nextivaapp.android.models.SingleEvent;
import com.nextiva.nextivaapp.android.models.net.broadsoft.services.BroadsoftCallCenter;
import com.nextiva.nextivaapp.android.models.net.broadsoft.services.BroadsoftCallCenterUnavailableCodes;
import com.nextiva.nextivaapp.android.util.GsonUtil;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class CallSettingsListViewModel extends BaseViewModel {

    private final ConnectionStateManager mConnectionStateManager;
    private final IntentManager mIntentManager;
    private final CallSettingsManager mCallSettingsManager;
    private final UserRepository mUserRepository;
    private final SettingsManager mSettingsManager;

    private final MutableLiveData<Resource<SingleEvent<Intent>>> mNavigateToSetCallSettingsIntentMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<SingleEvent<Boolean>> mInternetConnectionErrorMutableLiveData = new MutableLiveData<>();

    private BroadsoftCallCenter mBroadsoftCallCenter = new BroadsoftCallCenter();
    private BroadsoftCallCenterUnavailableCodes mBroadsoftCallCenterUnavailableCodes = new BroadsoftCallCenterUnavailableCodes();

    @Inject
    public CallSettingsListViewModel(
            @NonNull Application application,
            @NonNull ConnectionStateManager connectionStateManager,
            @NonNull IntentManager intentManager,
            @NonNull CallSettingsManager callSettingsManager,
            @NonNull UserRepository userRepository,
            @NonNull SettingsManager settingsManager) {

        super(application);

        mConnectionStateManager = connectionStateManager;
        mIntentManager = intentManager;
        mCallSettingsManager = callSettingsManager;
        mUserRepository = userRepository;
        mSettingsManager = settingsManager;

    }

    public LiveData<Resource<List<BaseListItem>>> getListItemsListLiveData() {
        return mCallSettingsManager.getListItemsListLiveData();
    }

    public LiveData<Resource<SingleEvent<Intent>>> getNavigateToSetCallSettingsIntentLiveData() {
        return mNavigateToSetCallSettingsIntentMutableLiveData;
    }

    public LiveData<SingleEvent<Boolean>> getInternetConnectionErrorLiveData() {
        return mInternetConnectionErrorMutableLiveData;
    }

    public void getServiceSettings() {
        if (!mConnectionStateManager.isInternetConnected()) {
            mInternetConnectionErrorMutableLiveData.postValue(new SingleEvent<>(true));


            mCallSettingsManager.clearServiceSettings();
            mCallSettingsManager.processServiceSettings();

        } else {
            mCompositeDisposable.add(
                    mUserRepository.getCallCenterService().subscribe((broadsoftCallCenterResponseEvent) -> {
                        if (broadsoftCallCenterResponseEvent != null && broadsoftCallCenterResponseEvent.isSuccessful()) {
                            broadsoftCallCenterResponseEvent.getCallCenter();
                            if (broadsoftCallCenterResponseEvent.getCallCenter().getBroadsoftCallCenterList() != null && !broadsoftCallCenterResponseEvent.getCallCenter().getBroadsoftCallCenterList().isEmpty()) {
                                mSettingsManager.setCallCenterStatus(GsonUtil.getJSON(broadsoftCallCenterResponseEvent.getCallCenter()));
                                mBroadsoftCallCenter = broadsoftCallCenterResponseEvent.getCallCenter();
                                mCallSettingsManager.setBroadsoftCallCenter(mBroadsoftCallCenter);
                            } else {
                                mSettingsManager.setCallCenterStatus("");
                            }
                        } else {
                            mSettingsManager.setCallCenterStatus("");
                        }

                    }));

            mCompositeDisposable.add(mUserRepository.getCallCenterServiceUnavailableCodes().subscribe((broadsoftCallCenterUnavailableCodesResponseEvent) -> {
                if (broadsoftCallCenterUnavailableCodesResponseEvent != null && broadsoftCallCenterUnavailableCodesResponseEvent.isSuccessful()) {
                    mSettingsManager.setCallCenterUnavailableCodes(GsonUtil.getJSON(broadsoftCallCenterUnavailableCodesResponseEvent.getCallCenterUnavailableCodes()));
                    mBroadsoftCallCenterUnavailableCodes = broadsoftCallCenterUnavailableCodesResponseEvent.getCallCenterUnavailableCodes();
                    mCallSettingsManager.setBroadsoftCallCenterUnavailableCodes(mBroadsoftCallCenterUnavailableCodes);
                } else {
                    mSettingsManager.setCallCenterUnavailableCodes("");
                }

            }));

            mCompositeDisposable.add(mCallSettingsManager.getFilteredServiceSettings());
        }
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RequestCodes.SET_SERVICE_SETTINGS_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                mCallSettingsManager.processServiceSettings();
            }
            return true;
        }

        return false;
    }

    public void onDetailItemViewListItemClicked(@NonNull final DetailItemViewListItem listItem) {
        if (listItem instanceof ServiceSettingsListItem) {
            switch (((ServiceSettingsListItem) listItem).getServiceSettings().getType()) {
                case Enums.Service.TYPE_BROADWORKS_ANYWHERE:
                case Enums.Service.TYPE_SIMULTANEOUS_RING_PERSONAL:
                case Enums.Service.TYPE_REMOTE_OFFICE:
                case Enums.Service.TYPE_CALL_FORWARDING_NOT_REACHABLE:
                case Enums.Service.TYPE_CALL_FORWARDING_ALWAYS:
                case Enums.Service.TYPE_CALL_FORWARDING_NO_ANSWER:
                case Enums.Service.TYPE_CALL_FORWARDING_BUSY:
                case Enums.Service.TYPE_DO_NOT_DISTURB:
                case Enums.Service.TYPE_CALLING_LINE_ID_DELIVERY_BLOCKING: {
                    mNavigateToSetCallSettingsIntentMutableLiveData.setValue(Resource.success(new SingleEvent<>(
                            SetCallSettingsActivity.newIntent(getApplication(), ((ServiceSettingsListItem) listItem).getServiceSettings()))));
                    break;
                }
                default: {
                    mNavigateToSetCallSettingsIntentMutableLiveData.setValue(Resource.error("", null));
                    break;
                }
            }

        } else if (listItem instanceof LocalSettingListItem) {
            mNavigateToSetCallSettingsIntentMutableLiveData.setValue(Resource.success(new SingleEvent<>(
                    SetCallSettingsActivity.newIntent(getApplication(), ((LocalSettingListItem) listItem).getSettingKey()))));
        }
    }

    public void navigateToInternetSettings(@NonNull Context context) {
        mIntentManager.navigateToInternetSettings(context);
    }

    public BroadsoftCallCenter getBroadsoftCallCenter() {
        return mBroadsoftCallCenter;
    }

    public BroadsoftCallCenterUnavailableCodes getBroadsoftCallCenterUnavailableCodes() {
        return mBroadsoftCallCenterUnavailableCodes;
    }
}
