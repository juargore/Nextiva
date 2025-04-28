package com.nextiva.nextivaapp.android.viewmodels.setcallsettings;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.UserRepository;
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager;
import com.nextiva.nextivaapp.android.managers.interfaces.SettingsManager;
import com.nextiva.nextivaapp.android.models.NextivaAnywhereLocation;
import com.nextiva.nextivaapp.android.models.Resource;
import com.nextiva.nextivaapp.android.models.ServiceSettings;
import com.nextiva.nextivaapp.android.models.SingleEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.NextivaAnywhereLocationDeleteResponseEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.NextivaAnywhereLocationGetResponseEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.NextivaAnywhereLocationSaveResponseEvent;
import com.nextiva.nextivaapp.android.util.CallUtil;
import com.nextiva.nextivaapp.android.util.StringUtil;
import com.nextiva.nextivaapp.android.viewmodels.BaseViewModel;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.functions.Consumer;

@HiltViewModel
public class SetCallSettingsNextivaAnywhereLocationViewModel extends BaseViewModel {

    private final UserRepository mUserRepository;
    private final SettingsManager mSettingsManager;
    private final SessionManager mSessionManager;

    private final MutableLiveData<ServiceSettings> mNextivaAnywhereServiceSettingsMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<Resource<NextivaAnywhereLocation>> mEditingLocationMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<SingleEvent<ValidationEvent>> mSaveLocationValidationEventMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<Resource<NextivaAnywhereLocationSaveResponseEvent>> mSaveLocationMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<SingleEvent<ValidationEvent>> mDeleteLocationValidationEventMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<Resource<NextivaAnywhereLocation>> mDeleteLocationMutableLiveData = new MutableLiveData<>();

    private final Consumer<NextivaAnywhereLocationGetResponseEvent> mNextivaAnywhereLocationGetResponseEventConsumer = event -> {
        if (event.isSuccessful()) {
            mEditingLocationMutableLiveData.setValue(Resource.success(event.getNextivaAnywhereLocation()));

        } else {
            mEditingLocationMutableLiveData.setValue(Resource.error("", getEditingLocation()));
        }
    };
    private final Consumer<NextivaAnywhereLocationSaveResponseEvent> mNextivaAnywhereLocationSaveResponseEventConsumer = new Consumer<NextivaAnywhereLocationSaveResponseEvent>() {
        @Override
        public void accept(NextivaAnywhereLocationSaveResponseEvent event) {
            if (event.isSuccessful()) {
                mSessionManager.setNextivaAnywhereServiceSettings(event.getNextivaAnywhereServiceSettings());
                mSaveLocationMutableLiveData.setValue(Resource.success(event));

            } else {
                mSaveLocationMutableLiveData.setValue(Resource.error("", event));
            }
        }
    };
    private final Consumer<NextivaAnywhereLocationDeleteResponseEvent> mNextivaAnywhereLocationDeleteResponseEventConsumer = new Consumer<NextivaAnywhereLocationDeleteResponseEvent>() {
        @Override
        public void accept(NextivaAnywhereLocationDeleteResponseEvent event) {
            if (event.isSuccessful()) {
                mSessionManager.setNextivaAnywhereServiceSettings(event.getProposedServiceSettings());
                mDeleteLocationMutableLiveData.setValue(Resource.success(event.getNextivaAnywhereLocation()));

            } else {
                mDeleteLocationMutableLiveData.setValue(Resource.error("", null));
            }
        }
    };

    @Inject
    public SetCallSettingsNextivaAnywhereLocationViewModel(
            @NonNull Application application,
            @NonNull UserRepository userRepository,
            @NonNull SettingsManager settingsManager,
            @NonNull SessionManager sessionManager) {

        super(application);
        mUserRepository = userRepository;
        mSettingsManager = settingsManager;
        mSessionManager = sessionManager;
    }

    public LiveData<ServiceSettings> getNextivaAnywhereServiceSettingsLiveData() {
        return mNextivaAnywhereServiceSettingsMutableLiveData;
    }

    public LiveData<Resource<NextivaAnywhereLocation>> getEditingLocationLiveData() {
        return mEditingLocationMutableLiveData;
    }

    public LiveData<SingleEvent<ValidationEvent>> getSaveLocationValidationEventLiveData() {
        return mSaveLocationValidationEventMutableLiveData;
    }

    public LiveData<Resource<NextivaAnywhereLocationSaveResponseEvent>> getSaveLocationLiveData() {
        return mSaveLocationMutableLiveData;
    }

    public LiveData<SingleEvent<ValidationEvent>> getDeleteLocationValidationEventLiveData() {
        return mDeleteLocationValidationEventMutableLiveData;
    }

    public LiveData<Resource<NextivaAnywhereLocation>> getDeleteLocationLiveData() {
        return mDeleteLocationMutableLiveData;
    }

    public void setNextivaAnywhereServiceSettings(ServiceSettings serviceSettings) {
        mNextivaAnywhereServiceSettingsMutableLiveData.setValue(serviceSettings);
    }

    public void setEditingLocation(NextivaAnywhereLocation nextivaAnywhereLocation) {
        mEditingLocationMutableLiveData.setValue(Resource.success(nextivaAnywhereLocation));
    }

    public void fetchNextivaAnywhereLocation() {
        NextivaAnywhereLocation editingLocation = getEditingLocation();

        if (editingLocation == null) {
            return;
        }

        mEditingLocationMutableLiveData.setValue(Resource.loading(editingLocation));

        mCompositeDisposable.add(
                mUserRepository.getNextivaAnywhereLocation(editingLocation.getPhoneNumber())
                        .subscribe(mNextivaAnywhereLocationGetResponseEventConsumer));
    }

    public void validateSaveNextivaAnywhereLocation(@NonNull NextivaAnywhereLocation nextivaAnywhereLocation) {
        final ServiceSettings proposedServiceSettings = getProposedServiceSettings(nextivaAnywhereLocation);

        if (proposedServiceSettings == null) {
            return;
        }

        if (isCallBackConflicting(proposedServiceSettings)) {
            mSaveLocationValidationEventMutableLiveData.setValue(new SingleEvent<>(
                    new ValidationEvent(proposedServiceSettings, nextivaAnywhereLocation, true, false)));

        } else if (isCallThroughConflicting(proposedServiceSettings)) {
            mSaveLocationValidationEventMutableLiveData.setValue(new SingleEvent<>(
                    new ValidationEvent(proposedServiceSettings, nextivaAnywhereLocation, false, true)));

        } else {
            mSaveLocationValidationEventMutableLiveData.setValue(new SingleEvent<>(
                    new ValidationEvent(proposedServiceSettings, nextivaAnywhereLocation, false, false)));
        }
    }

    public void saveNextivaAnywhereLocation(ServiceSettings proposedServiceSettings, NextivaAnywhereLocation nextivaAnywhereLocation) {
        if (isCallBackConflicting(proposedServiceSettings) || isCallThroughConflicting(proposedServiceSettings)) {
            mSettingsManager.setDialingService(Enums.Service.DialingServiceTypes.VOIP);
        }

        mSaveLocationMutableLiveData.setValue(Resource.loading(null));

        NextivaAnywhereLocation editingLocation = getEditingLocation();
        if (editingLocation != null) {
            mCompositeDisposable.add(
                    mUserRepository.putNextivaAnywhereLocation(proposedServiceSettings, nextivaAnywhereLocation, editingLocation.getPhoneNumber())
                            .subscribe(mNextivaAnywhereLocationSaveResponseEventConsumer));

        } else {
            mCompositeDisposable.add(
                    mUserRepository.postNextivaAnywhereLocation(proposedServiceSettings, nextivaAnywhereLocation)
                            .subscribe(mNextivaAnywhereLocationSaveResponseEventConsumer));
        }
    }

    public void validateDeleteNextivaAnywhereLocation() {
        NextivaAnywhereLocation editingLocation = getEditingLocation();
        final ServiceSettings proposedServiceSettings = getProposedServiceSettings(editingLocation);

        if (editingLocation == null || proposedServiceSettings == null) {
            return;
        }

        if (proposedServiceSettings.getNextivaAnywhereLocationsList() != null) {
            proposedServiceSettings.getNextivaAnywhereLocationsList().remove(editingLocation);
        }

        if (isCallBackConflicting(proposedServiceSettings)) {
            mDeleteLocationValidationEventMutableLiveData.setValue(new SingleEvent<>(
                    new ValidationEvent(proposedServiceSettings, editingLocation, true, false)));

        } else if (isCallThroughConflicting(proposedServiceSettings)) {
            mDeleteLocationValidationEventMutableLiveData.setValue(new SingleEvent<>(
                    new ValidationEvent(proposedServiceSettings, editingLocation, false, true)));

        } else {
            mDeleteLocationValidationEventMutableLiveData.setValue(new SingleEvent<>(
                    new ValidationEvent(proposedServiceSettings, editingLocation, false, false)));
        }
    }

    public void deleteNextivaAnywhereLocation(ServiceSettings proposedServiceSettings, NextivaAnywhereLocation nextivaAnywhereLocation) {
        if (isCallBackConflicting(proposedServiceSettings) || isCallThroughConflicting(proposedServiceSettings)) {
            mSettingsManager.setDialingService(Enums.Service.DialingServiceTypes.VOIP);
        }

        mDeleteLocationMutableLiveData.setValue(Resource.loading(null));

        mCompositeDisposable.add(
                mUserRepository.deleteNextivaAnywhereLocation(proposedServiceSettings, nextivaAnywhereLocation)
                        .subscribe(mNextivaAnywhereLocationDeleteResponseEventConsumer));
    }

    @Nullable
    public NextivaAnywhereLocation getEditingLocation() {
        if (mEditingLocationMutableLiveData.getValue() != null) {
            return mEditingLocationMutableLiveData.getValue().getData();
        } else {
            return null;
        }
    }

    public boolean changesMade(String phoneNumber,
                               String description,
                               boolean enableThisLocation,
                               boolean callControl,
                               boolean preventDivertingCalls,
                               boolean answerConfirmation) {

        NextivaAnywhereLocation editingLocation = getEditingLocation();

        if (editingLocation != null) {
            return StringUtil.changesMade(CallUtil.cleanForTextWatcher(phoneNumber), CallUtil.cleanForTextWatcher(editingLocation.getPhoneNumber())) ||
                    StringUtil.changesMade(description, editingLocation.getDescription()) ||
                    enableThisLocation != editingLocation.getActive() ||
                    callControl != editingLocation.getCallControlEnabled() ||
                    preventDivertingCalls != editingLocation.getPreventDivertingCalls() ||
                    answerConfirmation != editingLocation.getAnswerConfirmationRequired();

        } else {
            return !TextUtils.isEmpty(phoneNumber) ||
                    !TextUtils.isEmpty(description) ||
                    enableThisLocation ||
                    callControl ||
                    preventDivertingCalls ||
                    answerConfirmation;
        }
    }

    private boolean isCallBackConflicting(ServiceSettings proposedServiceSettings) {
        return mSettingsManager.getDialingService() == Enums.Service.DialingServiceTypes.CALL_BACK &&
                !mSessionManager.getIsCallBackEnabled(mSessionManager.getRemoteOfficeServiceSettings(), proposedServiceSettings);
    }

    private boolean isCallThroughConflicting(ServiceSettings proposedServiceSettings) {
        return mSettingsManager.getDialingService() == Enums.Service.DialingServiceTypes.CALL_THROUGH &&
                !mSessionManager.getIsCallThroughEnabled(proposedServiceSettings, mSettingsManager.getPhoneNumber());
    }

    @Nullable
    private ServiceSettings getProposedServiceSettings(NextivaAnywhereLocation newLocation) {
        if (mNextivaAnywhereServiceSettingsMutableLiveData.getValue() == null) {
            return null;
        }

        ServiceSettings proposedServiceSettings = new ServiceSettings(mNextivaAnywhereServiceSettingsMutableLiveData.getValue());
        NextivaAnywhereLocation editingLocation = getEditingLocation();

        if (proposedServiceSettings.getNextivaAnywhereLocationsList() == null) {
            proposedServiceSettings.setNextivaAnywhereLocationsList(new ArrayList<>());
        }

        int locationIndex = -1;

        if (editingLocation != null) {
            for (int i = 0; i < proposedServiceSettings.getNextivaAnywhereLocationsList().size(); i++) {
                if (proposedServiceSettings.getNextivaAnywhereLocationsList().get(i) != null &&
                        TextUtils.equals(proposedServiceSettings.getNextivaAnywhereLocationsList().get(i).getPhoneNumber(), editingLocation.getPhoneNumber())) {

                    locationIndex = i;
                    break;
                }
            }
        }

        if (locationIndex > -1) {
            proposedServiceSettings.getNextivaAnywhereLocationsList().set(locationIndex, newLocation);

        } else {
            proposedServiceSettings.getNextivaAnywhereLocationsList().add(newLocation);
        }

        return proposedServiceSettings;
    }

    public static class ValidationEvent {

        @NonNull
        private final ServiceSettings mProposedServiceSettings;
        @NonNull
        private final NextivaAnywhereLocation mNextivaAnywhereLocation;
        private final boolean mIsCallBackConflicting;
        private final boolean mIsCallThroughConflicting;

        public ValidationEvent(
                @NonNull ServiceSettings proposedServiceSettings,
                @NonNull NextivaAnywhereLocation nextivaAnywhereLocation,
                boolean isCallBackConflicting,
                boolean isCallThroughConflicting) {

            mProposedServiceSettings = proposedServiceSettings;
            mNextivaAnywhereLocation = nextivaAnywhereLocation;
            mIsCallBackConflicting = isCallBackConflicting;
            mIsCallThroughConflicting = isCallThroughConflicting;
        }

        @NonNull
        public ServiceSettings getProposedServiceSettings() {
            return mProposedServiceSettings;
        }

        @NonNull
        public NextivaAnywhereLocation getNextivaAnywhereLocation() {
            return mNextivaAnywhereLocation;
        }

        public boolean isCallBackConflicting() {
            return mIsCallBackConflicting;
        }

        public boolean isCallThroughConflicting() {
            return mIsCallThroughConflicting;
        }
    }
}
