/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.fragments.setcallsettings;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.adapters.CallCenterRecyclerViewAdapter;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.CallCenterListItem;
import com.nextiva.nextivaapp.android.constants.Constants;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.databinding.FragmentSetCallSettingsCallCenterStatusBinding;
import com.nextiva.nextivaapp.android.dialogs.SimpleListDialogListener;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.UserRepository;
import com.nextiva.nextivaapp.android.managers.interfaces.AnalyticsManager;
import com.nextiva.nextivaapp.android.managers.interfaces.CallSettingsManager;
import com.nextiva.nextivaapp.android.managers.interfaces.ConfigManager;
import com.nextiva.nextivaapp.android.managers.interfaces.IntentManager;
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager;
import com.nextiva.nextivaapp.android.managers.interfaces.SettingsManager;
import com.nextiva.nextivaapp.android.models.CallCenter;
import com.nextiva.nextivaapp.android.models.CallCenterDetailModel;
import com.nextiva.nextivaapp.android.models.net.broadsoft.services.BroadsoftCallCenter;
import com.nextiva.nextivaapp.android.models.net.broadsoft.services.BroadsoftCallCenterList;
import com.nextiva.nextivaapp.android.models.net.broadsoft.services.BroadsoftCallCenterUnavailableCodes;
import com.nextiva.nextivaapp.android.models.net.broadsoft.services.BroadsoftUnavailableDetail;
import com.nextiva.nextivaapp.android.util.GsonUtil;
import com.nextiva.nextivaapp.android.util.LogUtil;

import java.util.ArrayList;
import java.util.Locale;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SetCallSettingsCallCenterStatusFragment extends SetCallSettingsBaseFragment<String> {

    CallCenterRecyclerViewAdapter mAdapter;

    protected LinearLayout mAgentStatusSelection;
    protected LinearLayout mUnavailableStatusSelection;
    protected TextView mAgentStatusTextView;
    protected TextView mUnavailableStatusTextView;
    protected TextView mStatusMessageTextView;
    protected RecyclerView mRecyclerView;

    @Inject
    protected SettingsManager mSettingsManager;
    @Inject
    protected CallSettingsManager mCallSettingsManager;
    @Inject
    protected SessionManager mSessionManager;
    @Inject
    protected ConfigManager mConfigManager;
    @Inject
    protected IntentManager mIntentManager;
    @Inject
    protected AnalyticsManager mAnalyticsManager;
    @Inject
    protected UserRepository mUserRepository;

    private BroadsoftCallCenter mBroadsoftCallCenterOriginal;
    private BroadsoftCallCenter mBroadsoftCallCenter;

    private BroadsoftCallCenterUnavailableCodes mBroadsoftCallCenterUnavailableCodes;

    static ArrayList<CallCenterListItem> baseListItemsList = new ArrayList<>();

    private final ArrayList<String> mAgentStatusList = new ArrayList<>();

    private final ArrayList<String> mUnavailableOptionsList = new ArrayList<>();
    private BroadsoftUnavailableDetail mBroadsoftUnavailableDetailSelected;
    private BroadsoftUnavailableDetail mBroadsoftUnavailableDetailDefault;

    boolean mIsCallCenterAgentACDStateUpdated;
    boolean mIsCallCenterUnavailableCodeUpdated;
    boolean mIsCallCenterListUpdated;

    private MenuItem mSaveMenuItem;


    SimpleListDialogListener mUnavailableStatusListener = position -> {
        LogUtil.d("Call Center Unavailable Listener: " + position);
        String unavailableStatusSelected = mUnavailableOptionsList.get(position);
        if (unavailableStatusSelected.equals(getString(R.string.call_center_unavailable_status))) {
            mUnavailableStatusTextView.setText(R.string.call_center_unavailable_status);
            mBroadsoftUnavailableDetailSelected = null;
            mBroadsoftCallCenter.setAgentUnavailableCode("00");
        } else if (mBroadsoftCallCenterUnavailableCodes != null && mBroadsoftCallCenterUnavailableCodes.getUnavailableCodes() != null) {
            for (BroadsoftUnavailableDetail unavailableOption : mBroadsoftCallCenterUnavailableCodes.getUnavailableCodes()) {
                if (unavailableOption.getCode() != null && unavailableStatusSelected.contains(unavailableOption.getCode())) {
                    mUnavailableStatusTextView.setText(unavailableStatusSelected);
                    mBroadsoftUnavailableDetailSelected = unavailableOption;
                    mBroadsoftCallCenter.setAgentUnavailableCode(unavailableOption.getCode());
                }
            }


        }
    };

    SimpleListDialogListener mAgentStatusListener = position -> {
        mBroadsoftCallCenter.setAgentACDState(mAgentStatusList.get(position));
        mAgentStatusTextView.setText(mAgentStatusList.get(position));
        setStatusMessage(mAgentStatusList.get(position));


        updateUnavailable();
    };


    private final View.OnClickListener mAgentStatusOnClickListener = v -> {
        if (!mAgentStatusList.isEmpty()) {
            mDialogManager.showSimpleListDialog(getActivity(), getString(R.string.set_call_settings_call_center_status_agent_status), mAgentStatusList, mAgentStatusListener, (dialog, which) -> {
            });
        }
    };

    private final View.OnClickListener mUnavailableOnClickListener = v -> {
        if (!mUnavailableOptionsList.isEmpty()) {
            mDialogManager.showSimpleListDialog(getActivity(), getString(R.string.set_call_settings_call_center_status_unavailable_code), mUnavailableOptionsList, mUnavailableStatusListener, (dialog, which) -> {
            });
        }
    };

    public SetCallSettingsCallCenterStatusFragment() {
        // Required empty public constructor
    }

    public static SetCallSettingsCallCenterStatusFragment newInstance(@Enums.Service.CallCenterServiceStatuses.CallCenterServiceStatus String callCenterServiceStatus) {
        Bundle args = new Bundle();

        SetCallSettingsCallCenterStatusFragment fragment = new SetCallSettingsCallCenterStatusFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() == null) {
            return;
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = bindViews(inflater, container);

        mBroadsoftCallCenterOriginal = mCallSettingsManager.getBroadsoftCallCenter();
        mBroadsoftCallCenter = new BroadsoftCallCenter(mBroadsoftCallCenterOriginal);
        mBroadsoftCallCenterUnavailableCodes = mCallSettingsManager.getBroadsoftCallCenterUnavailableCodes();

        LogUtil.d("Call Center SetCallSettingsCallCenterStatusFragment mBroadsoftCallCenter:" + GsonUtil.getJSON(mBroadsoftCallCenter));
        LogUtil.d("Call Center SetCallSettingsCallCenterStatusFragment mBroadsoftCallCenterUnavailableCodes:" + GsonUtil.getJSON(mBroadsoftCallCenterUnavailableCodes));

        if (mBroadsoftCallCenter != null && mBroadsoftCallCenter.getAgentACDState() != null && !TextUtils.isEmpty(mBroadsoftCallCenter.getAgentACDState())) {
            mAgentStatusTextView.setText(mBroadsoftCallCenter.getAgentACDState());
            setStatusMessage(mBroadsoftCallCenter.getAgentACDState());
            mAgentStatusSelection.setVisibility(View.VISIBLE);
            mStatusMessageTextView.setVisibility(View.VISIBLE);
            setupAgentStatusList();
            mAgentStatusSelection.setOnClickListener(mAgentStatusOnClickListener);

            updateUnavailable();
        } else {
            mAgentStatusSelection.setVisibility(View.GONE);
            mStatusMessageTextView.setVisibility(View.GONE);
            mAgentStatusTextView.setText("");
        }

        if (mBroadsoftCallCenter != null && mBroadsoftCallCenter.getBroadsoftCallCenterList() != null) {
            populateCallCenters(mBroadsoftCallCenter.getBroadsoftCallCenterList());
        }


        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new CallCenterRecyclerViewAdapter(getActivity(), baseListItemsList, mSettingsManager);



        mRecyclerView.setAdapter(mAdapter);

        setupCallSettings();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        mSaveMenuItem = menu.findItem(R.id.set_call_settings_save);
        mSaveMenuItem.setEnabled(true);
    }

    private View bindViews(LayoutInflater inflater, ViewGroup container) {
        FragmentSetCallSettingsCallCenterStatusBinding binding = FragmentSetCallSettingsCallCenterStatusBinding.inflate(inflater, container, false);

        mAgentStatusSelection = binding.setCallSettingsCallCenterStatusSelection;
        mUnavailableStatusSelection = binding.setCallSettingsCallCenterStatusUnavailableSelection;
        mAgentStatusTextView = binding.setCallSettingsCallCenterStatusSelectionSubTitleTextView;
        mUnavailableStatusTextView = binding.setCallSettingsCallCenterUnavailableStatusMessageTextView;
        mStatusMessageTextView = binding.setCallSettingsCallCenterStatusMessageTextView;
        mRecyclerView = binding.setCallSettingsCallCenterStatusRecyclerView;

        return binding.getRoot();
    }

    private void updateUnavailable() {

        if (mBroadsoftCallCenter != null && mBroadsoftCallCenter.getAgentACDState() != null && mBroadsoftCallCenter.getAgentACDState().equals(Enums.Service.CallCenterServiceStatuses.UNAVAILABLE) && mBroadsoftCallCenterUnavailableCodes != null && mBroadsoftCallCenterUnavailableCodes.getEnableAgentUnavailableCodes() && mBroadsoftCallCenterUnavailableCodes.getUnavailableCodes() != null && mBroadsoftCallCenterUnavailableCodes.getUnavailableCodes().size() > 0) {

            String callCenterStatus = "";
            if (!TextUtils.isEmpty(mBroadsoftCallCenter.getAgentACDState()) && mBroadsoftCallCenter.getAgentACDState().contains(Enums.Service.CallCenterServiceStatuses.UNAVAILABLE)) {
                for (BroadsoftUnavailableDetail broadsoftUnavailableDetail : mBroadsoftCallCenterUnavailableCodes.getUnavailableCodes()) {
                    if (broadsoftUnavailableDetail.getCode().equals(mBroadsoftCallCenter.getAgentUnavailableCode())) {
                        callCenterStatus = broadsoftUnavailableDetail.getCode() + "-" + ((broadsoftUnavailableDetail.getDescription() != null)? broadsoftUnavailableDetail.getDescription() : "");
                        break;
                    }

                    if (broadsoftUnavailableDetail.getDefault() != null && broadsoftUnavailableDetail.getDefault()) {
                        mBroadsoftUnavailableDetailDefault = broadsoftUnavailableDetail;
                    }
                }

                if (callCenterStatus != null && TextUtils.isEmpty(callCenterStatus)) {
                    if (mBroadsoftUnavailableDetailDefault != null) {
                        callCenterStatus = mBroadsoftUnavailableDetailDefault.getCode() + "-" + ((mBroadsoftUnavailableDetailDefault.getDescription() != null)? mBroadsoftUnavailableDetailDefault.getDescription() : "");
                    } else {
                        callCenterStatus = getString(R.string.call_center_unavailable_status);
                    }
                }
            } else {
                callCenterStatus = "";
            }


            mUnavailableStatusTextView.setText(callCenterStatus);
            mUnavailableStatusSelection.setVisibility(View.VISIBLE);
            setupUnavailableOptionsList();
        } else {
            mUnavailableStatusSelection.setVisibility(View.GONE);
        }
    }

    private void setStatusMessage(String status) {
        String statusMessage = "";
        switch (status) {
            case Enums.Service.CallCenterServiceStatuses.SIGN_IN:
                statusMessage = getString(R.string.set_call_settings_call_center_status_message_signed_in);
                break;
            case Enums.Service.CallCenterServiceStatuses.AVAILABLE:
                statusMessage = getString(R.string.set_call_settings_call_center_status_message_available);
                break;
            case Enums.Service.CallCenterServiceStatuses.UNAVAILABLE:
                statusMessage = getString(R.string.set_call_settings_call_center_status_message_unavailable);
                break;
            case Enums.Service.CallCenterServiceStatuses.WRAP_UP:
                statusMessage = getString(R.string.set_call_settings_call_center_status_message_wrapping_up);
                break;
            case Enums.Service.CallCenterServiceStatuses.SIGN_OUT:
                statusMessage = getString(R.string.set_call_settings_call_center_status_message_signed_out);
                break;
            case Enums.Service.CallCenterServiceStatuses.NONE:
                statusMessage = "";
                break;
        }

        mStatusMessageTextView.setText(statusMessage);

    }

    private void setupAgentStatusList() {
        mAgentStatusList.add(Enums.Service.CallCenterServiceStatuses.SIGN_IN);
        mAgentStatusList.add(Enums.Service.CallCenterServiceStatuses.AVAILABLE);
        mAgentStatusList.add(Enums.Service.CallCenterServiceStatuses.UNAVAILABLE);
        mAgentStatusList.add(Enums.Service.CallCenterServiceStatuses.WRAP_UP);
        mAgentStatusList.add(Enums.Service.CallCenterServiceStatuses.SIGN_OUT);
    }

    private void setupUnavailableOptionsList() {
        if (mBroadsoftCallCenterUnavailableCodes != null && mBroadsoftCallCenterUnavailableCodes.getUnavailableCodes() != null && !mBroadsoftCallCenterUnavailableCodes.getUnavailableCodes().isEmpty()) {
            mUnavailableOptionsList.clear();

            for (BroadsoftUnavailableDetail unavailableOption : mBroadsoftCallCenterUnavailableCodes.getUnavailableCodes()) {
                if (unavailableOption.getActive() != null && unavailableOption.getActive()) {
                    mUnavailableOptionsList.add(unavailableOption.getCode() + "-" + ((unavailableOption.getDescription() != null)? unavailableOption.getDescription() : ""));

                    if (unavailableOption.getDefault() != null && unavailableOption.getDefault()) {
                        mBroadsoftUnavailableDetailDefault = unavailableOption;
                    }
                }

            }

            if (mBroadsoftUnavailableDetailDefault == null && !mUnavailableOptionsList.isEmpty()) {
                mUnavailableOptionsList.add(0, getString(R.string.call_center_unavailable_status));
            }

            mUnavailableStatusSelection.setOnClickListener(mUnavailableOnClickListener);
        }
    }

    // --------------------------------------------------------------------------------------------
    // SetCallSettingsBaseFragment Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public int getLayoutResId() {
        return R.layout.fragment_set_call_settings_call_center_status;
    }

    @Override
    public int getToolbarTitleStringResId() {
        return R.string.call_settings_call_center;
    }

    @Override
    protected void setupScreenWidgets() {
        if (getActivity() == null) {
            return;
        }
    }

    @Override
    public void setupCallSettings() {
    }
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // EntryForm Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public boolean changesMade() {
        BroadsoftCallCenter changesMadeBroadsoftCallCenter = GsonUtil.getObject(BroadsoftCallCenter.class, mSettingsManager.getCallCenterStatus());

        if (mBroadsoftCallCenterOriginal != null &&
                mBroadsoftCallCenterOriginal.getAgentACDState() != null &&
                mBroadsoftCallCenter != null &&
                mBroadsoftCallCenter.getAgentACDState() != null &&
                !mBroadsoftCallCenterOriginal.getAgentACDState().equals(mBroadsoftCallCenter.getAgentACDState())) {
            mIsCallCenterAgentACDStateUpdated = true;
        }

        if ((mBroadsoftCallCenterOriginal != null &&
                mBroadsoftCallCenterOriginal.getAgentUnavailableCode() == null &&
                mBroadsoftCallCenter != null &&
                mBroadsoftCallCenter.getAgentUnavailableCode() != null)
                ||
                (mBroadsoftCallCenterOriginal != null &&
                    mBroadsoftCallCenterOriginal.getAgentUnavailableCode() != null &&
                    mBroadsoftCallCenter != null &&
                    mBroadsoftCallCenter.getAgentUnavailableCode() != null && !mBroadsoftCallCenterOriginal.getAgentUnavailableCode().equals(mBroadsoftCallCenter.getAgentUnavailableCode()))) {
            mIsCallCenterUnavailableCodeUpdated = true;
        }
        else
            mIsCallCenterUnavailableCodeUpdated = false;

        if (mBroadsoftCallCenterOriginal != null &&
                mBroadsoftCallCenterOriginal.getBroadsoftCallCenterList() != null &&
                changesMadeBroadsoftCallCenter != null &&
                changesMadeBroadsoftCallCenter.getBroadsoftCallCenterList() != null) {
            for(BroadsoftCallCenterList broadsoftCallCenterList : mBroadsoftCallCenterOriginal.getBroadsoftCallCenterList()) {
                for(BroadsoftCallCenterList changedBroadsoftCallCenterList : changesMadeBroadsoftCallCenter.getBroadsoftCallCenterList())
                    if (broadsoftCallCenterList != null &&
                            broadsoftCallCenterList.getServiceUserId() != null &&
                            broadsoftCallCenterList.getServiceUserId().equals(changedBroadsoftCallCenterList.getServiceUserId()) &&
                            broadsoftCallCenterList.getAvailable() != changedBroadsoftCallCenterList.getAvailable()) {
                        mIsCallCenterListUpdated = true;
                        return true;
                    }
                    else
                        mIsCallCenterListUpdated = false;
            }
        }
        else
            mIsCallCenterListUpdated = false;


        if(mIsCallCenterAgentACDStateUpdated || mIsCallCenterUnavailableCodeUpdated)
        {
            return true;
        }

        return false;

        //        return mSettingsManager.getCallCenterStatus() != getCallCenterServiceStatusBySelectedRadioButton();
    }
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // CallSettingsForm Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public String getFormCallSettings() {
        return "";
    }

    @Override
    public void saveForm() {
        mAnalyticsManager.logEvent(getAnalyticScreenName(), Enums.Analytics.EventName.SAVE_BUTTON_PRESSED);



        CallCenter callCenter = loadPresentSettings();
        CallCenter updateCallCenter;

        //mDialogManager.showProgressDialog(getActivity(), getAnalyticScreenName(), R.string.progress_processing);
        //mCompositeDisposable.add(

        mUserRepository.putCallCenterService(callCenter).subscribe();
        validateForm((type, callSettings) -> {
            mSettingsManager.setCallCenterStatus(callSettings);

            Intent data = new Intent();
            data.putExtra(Constants.EXTRA_CALL_SETTINGS_KEY, type);
            data.putExtra(Constants.EXTRA_CALL_SETTINGS_VALUE, callSettings);

            if (mSetCallSettingsListener != null) {
                mSetCallSettingsListener.onCallSettingsSaved(data);
            }

        });
    }

    @NonNull
    @Override
    public String getAnalyticScreenName() {
        return Enums.Analytics.ScreenName.DIALING_SERVICE_SCREEN;
    }

    private void populateCallCenters(ArrayList<BroadsoftCallCenterList> callCenterList) {
        baseListItemsList.clear();
        String phoneNumber = "";
        for (BroadsoftCallCenterList callCenter : callCenterList) {
            if (callCenter != null) {
                if (!TextUtils.isEmpty(callCenter.getPhoneNumber()) && !TextUtils.isEmpty(callCenter.getExtension())) {
                    phoneNumber = getString(R.string.call_center_number_with_extension, PhoneNumberUtils.formatNumber(callCenter.getPhoneNumber(), Locale.getDefault().getCountry()), callCenter.getExtension());
                } else if (!TextUtils.isEmpty(callCenter.getPhoneNumber())) {
                    phoneNumber = getString(R.string.call_center_number, PhoneNumberUtils.formatNumber(callCenter.getPhoneNumber(), Locale.getDefault().getCountry()));
                } else if (!TextUtils.isEmpty(callCenter.getExtension())) {
                    phoneNumber = getString(R.string.call_center_extension, callCenter.getExtension());
                } else {
                    phoneNumber = "";
                }
            }

            baseListItemsList.add(new CallCenterListItem(callCenter.getServiceUserId(), phoneNumber, callCenter.getAvailable(), callCenter.getLogOffAllowed()));
        }

    }
    // --------------------------------------------------------------------------------------------

    private CallCenter loadPresentSettings()
    {
        CallCenter callCenter = new CallCenter();
        BroadsoftCallCenter broadsoftCallCenter;
        CallCenterDetailModel callCenterDetailModel = new CallCenterDetailModel();
        ArrayList<CallCenterDetailModel> callCenterDetailModelArrayList = new ArrayList<>();
        if(mBroadsoftCallCenter != null &&
                mBroadsoftCallCenter.getAgentACDState() != null &&
                !mBroadsoftCallCenter.getAgentACDState().isEmpty()) {

            if(mIsCallCenterAgentACDStateUpdated)
                callCenter.setAgentACDState(mBroadsoftCallCenter.getAgentACDState());

            if(mIsCallCenterUnavailableCodeUpdated) {
                if (mBroadsoftCallCenter.getAgentUnavailableCode() != null) {
                    callCenter.setAgentUnavailableCode(mBroadsoftCallCenter.getAgentUnavailableCode());
                } else {
                    callCenter.setAgentUnavailableCode("00");
                }
            }
        }


        if(mIsCallCenterListUpdated) {

            BroadsoftCallCenter changesMadeBroadsoftCallCenter = GsonUtil.getObject(BroadsoftCallCenter.class, mSettingsManager.getCallCenterStatus());
            if (mBroadsoftCallCenterOriginal != null &&
                    mBroadsoftCallCenterOriginal.getBroadsoftCallCenterList() != null &&
                    changesMadeBroadsoftCallCenter != null &&
                    changesMadeBroadsoftCallCenter.getBroadsoftCallCenterList() != null) {
                for (BroadsoftCallCenterList broadsoftCallCenterList : mBroadsoftCallCenterOriginal.getBroadsoftCallCenterList()) {
                    for (BroadsoftCallCenterList changedBroadsoftCallCenterList : changesMadeBroadsoftCallCenter.getBroadsoftCallCenterList())
                        if (broadsoftCallCenterList != null &&
                                broadsoftCallCenterList.getServiceUserId() != null &&
                                broadsoftCallCenterList.getServiceUserId().equals(changedBroadsoftCallCenterList.getServiceUserId()) &&
                                broadsoftCallCenterList.getAvailable() != changedBroadsoftCallCenterList.getAvailable()) {
                            callCenterDetailModel = new CallCenterDetailModel();
                            callCenterDetailModel.setServiceUserId(changedBroadsoftCallCenterList.getServiceUserId());
                            callCenterDetailModel.setAvailable(changedBroadsoftCallCenterList.getAvailable());
                            callCenterDetailModelArrayList.add(callCenterDetailModel);
                        }
                }

            }


            if (!callCenterDetailModelArrayList.isEmpty())
                callCenter.setCallCenterList(callCenterDetailModelArrayList);
        }
        return callCenter;
    }

}
