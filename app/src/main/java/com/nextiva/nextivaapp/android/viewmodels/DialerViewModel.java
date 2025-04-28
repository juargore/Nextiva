package com.nextiva.nextivaapp.android.viewmodels;

import android.app.Application;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.db.DbManager;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.CallManagementRepository;
import com.nextiva.nextivaapp.android.managers.interfaces.ConfigManager;
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager;
import com.nextiva.nextivaapp.android.models.FeatureAccessCode;
import com.nextiva.nextivaapp.android.net.buses.RxEvents;
import com.nextiva.nextivaapp.android.sip.pjsip.PJSipManager;
import com.nextiva.nextivaapp.android.util.CallUtil;
import com.nextiva.nextivaapp.android.util.extensions.StringExtensionsKt;
import com.nextiva.pjsip.pjsip_lib.sipservice.ParticipantInfo;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.observers.DisposableSingleObserver;

@HiltViewModel
public class DialerViewModel extends BaseViewModel {

    private final SessionManager mSessionManager;
    private final ConfigManager mConfigManager;
    private final DbManager mDbManager;
    private final CallManagementRepository mCallManagementRepository;

    private final MutableLiveData<ParticipantInfo> mProcessedCallInfoMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mErrorStateMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> mLastDialedPhoneNumberMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> mVoicemailCountLiveData = new MutableLiveData<>();
    private final PJSipManager mSipManager;

    @Inject
    public DialerViewModel(@NonNull Application application,
                           SessionManager sessionManager,
                           ConfigManager configManager,
                           DbManager dbManager,
                           PJSipManager sipManager,
                           CallManagementRepository callManagementRepository) {

        super(application);
        mSessionManager = sessionManager;
        mConfigManager = configManager;
        mDbManager = dbManager;
        mSipManager = sipManager;
        mCallManagementRepository = callManagementRepository;
    }

    public LiveData<ParticipantInfo> getProcessedCallInfoLiveData() {
        return mProcessedCallInfoMutableLiveData;
    }

    public LiveData<Boolean> getErrorStateLiveData() {
        return mErrorStateMutableLiveData;
    }

    public LiveData<String> getLastDialedPhoneNumberLiveData() {
        return mLastDialedPhoneNumberMutableLiveData;
    }

    public void clearErrorState() {
        mErrorStateMutableLiveData.setValue(false);
    }

    public void clearProcessedCallInfo() {
        mProcessedCallInfoMutableLiveData.setValue(null);
    }

    private void placeCall(String strippedNumber, @NonNull ParticipantInfo participantInfo) {
        if(participantInfo.getNumberToCall() != null)
            mCompositeDisposable.add(
                    mDbManager.getConnectContactFromPhoneNumber(strippedNumber != null ? strippedNumber : participantInfo.getNumberToCall())
                            .subscribe(dbResponse -> {
                                if (dbResponse != null && dbResponse.getValue() != null) {
                                    participantInfo.setDisplayName(dbResponse.getValue().getUiName());
                                    participantInfo.setContactId(dbResponse.getValue().getUserId());
                                }

                                mProcessedCallInfoMutableLiveData.setValue(participantInfo);
                                mLastDialedPhoneNumberMutableLiveData.setValue(null);
                            }));
    }

    public void placeCall(@NonNull String numberToCall, @Enums.Sip.CallTypes.Type int callType) {
        if (TextUtils.isEmpty(numberToCall)) {
            if (!TextUtils.isEmpty(mSessionManager.getLastDialedPhoneNumber())) {
                mLastDialedPhoneNumberMutableLiveData.setValue(mSessionManager.getLastDialedPhoneNumber());
            }

        } else if (mConfigManager.getMobileConfig() != null && TextUtils.equals(numberToCall, mConfigManager.getMobileConfig().getVoicemailPhoneNumber())) {
            placeVoicemailCall();

        } else {
            mSessionManager.setLastDialedPhoneNumber(numberToCall);

            String numberToDial = StringExtensionsKt.extractFirstNumber(numberToCall);
            String dtfmCode = StringExtensionsKt.extractDtfmTone(numberToCall);

            ParticipantInfo participantInfo = new ParticipantInfo();
            participantInfo.setCallType(callType);
            participantInfo.setNumberToCall(numberToDial);
            participantInfo.setMetadata(dtfmCode);
            placeCall(CallUtil.getStrippedPhoneNumber(numberToCall), participantInfo);
        }
    }

    public void placeVoicemailCall() {
        if (mConfigManager.getMobileConfig() != null && !TextUtils.isEmpty(mConfigManager.getMobileConfig().getVoicemailPhoneNumber())) {
            mSessionManager.setLastDialedPhoneNumber(mConfigManager.getMobileConfig().getVoicemailPhoneNumber());

            ParticipantInfo participantInfo = new ParticipantInfo();
            participantInfo.setCallType(Enums.Sip.CallTypes.VOICE);
            participantInfo.setNumberToCall(mConfigManager.getMobileConfig().getVoicemailPhoneNumber());
            participantInfo.setDisallowDialingServiceTypes(new int[] {Enums.Service.DialingServiceTypes.THIS_PHONE});

            placeCall(null, participantInfo);

        } else {
            mErrorStateMutableLiveData.setValue(true);
        }
    }

    public void pullCall() {

        mCallManagementRepository.isExistingActiveCall()
                .subscribe(new DisposableSingleObserver<RxEvents.IsExistingActiveCallResponseEvent>() {
                    @Override
                    public void onSuccess(RxEvents.IsExistingActiveCallResponseEvent callHistoryResponseEvent) {
                        if(callHistoryResponseEvent.isSuccessful()) {
                            pullCallUnchecked();
                        }
                        else
                        {
                            Toast.makeText(getApplication(), R.string.sip_pull_call_failed, Toast.LENGTH_SHORT).show();
                        }
                        dispose();
                    }

                    @Override
                    public void onError(Throwable e) {
                        dispose();
                    }
                });

    }


    public void pullCallUnchecked() {

        if (mSessionManager.getFeatureAccessCodes() != null && mSessionManager.getFeatureAccessCodes().getFeatureAccessCodesList() != null) {
            for (FeatureAccessCode featureAccessCode : mSessionManager.getFeatureAccessCodes().getFeatureAccessCodesList()) {
                if (featureAccessCode != null &&
                        !TextUtils.isEmpty(featureAccessCode.getCode()) &&
                        TextUtils.equals(featureAccessCode.getCodeName(), Enums.Service.FeatureAccessCodes.CALL_RETRIEVE)) {

                    ParticipantInfo participantInfo = new ParticipantInfo();
                    participantInfo.setCallType(Enums.Sip.CallTypes.VOICE);
                    participantInfo.setNumberToCall(featureAccessCode.getCode());
                    participantInfo.setDisallowDialingServiceTypes(new int[] {Enums.Service.DialingServiceTypes.THIS_PHONE});

                    placeCall(null, participantInfo);
                    break;
                }
            }

        } else {
            mErrorStateMutableLiveData.setValue(true);
            Toast.makeText(getApplication(), R.string.sip_pull_call_failed, Toast.LENGTH_SHORT).show();

        }
    }

    public void playDialerKeyPressedAudio(String keyPressed) {
        mSipManager.playDtmfTone(null, keyPressed);
    }

    public LiveData<Integer> getVoicemailCountLiveData() {
        return mDbManager.getNewVoicemailCountLiveData();
    }

}
