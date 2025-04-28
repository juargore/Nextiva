/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.fragments;

import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.NO_INTERNET_DIALOG_OK_BUTTON_PRESSED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.NO_INTERNET_DIALOG_SETTINGS_BUTTON_PRESSED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.NO_INTERNET_DIALOG_SHOWN;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.DetailItemViewListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.LocalSettingListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ServiceSettingsListItem;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.constants.RequestCodes;
import com.nextiva.nextivaapp.android.managers.interfaces.AnalyticsManager;
import com.nextiva.nextivaapp.android.managers.interfaces.DialogManager;
import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager;
import com.nextiva.nextivaapp.android.models.Resource;
import com.nextiva.nextivaapp.android.models.SingleEvent;
import com.nextiva.nextivaapp.android.viewmodels.CallSettingsListViewModel;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Created by adammacdonald on 3/19/18.
 */

@AndroidEntryPoint
public class CallSettingsListFragment extends GeneralRecyclerViewFragment {

    @Inject
    protected DialogManager mDialogManager;
    @Inject
    protected AnalyticsManager mAnalyticsManager;

    private CallSettingsListViewModel mViewModel;

    private final Observer<Resource<List<BaseListItem>>> mListItemsObserver = resource -> {
        if (resource != null) {
            switch (resource.getStatus()) {
                case Enums.Net.StatusTypes.LOADING: {
                    startRefreshing();
                    break;
                }
                case Enums.Net.StatusTypes.SUCCESS: {
                    stopRefreshing();

                    if (mRecyclerView != null && mRecyclerView.getLayoutManager() != null) {
                        Parcelable recyclerViewState = mRecyclerView.getLayoutManager().onSaveInstanceState();
                        mAdapter.updateList(resource.getData());
                        mRecyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
                    }
                    break;
                }
                case Enums.Net.StatusTypes.ERROR: {
                    if (getActivity() == null) {
                        return;
                    }

                    stopRefreshing();
                    mDialogManager.showErrorDialog(getActivity(), getAnalyticScreenName());
                    break;
                }
            }
        }
    };
    private final Observer<Resource<SingleEvent<Intent>>> mNavigateToSetCallSettingsObserver = resource -> {
        if (getActivity() == null) {
            return;
        }

        if (resource != null) {
            switch (resource.getStatus()) {
                case Enums.Net.StatusTypes.LOADING: {
                    break;
                }
                case Enums.Net.StatusTypes.SUCCESS: {
                    if (resource.getData() != null && resource.getData().getContentIfNotHandled() != null) {
                        mViewModel.clearCompositeDisposable();
                        startActivityForResult(resource.getData().peekContent(), RequestCodes.SET_SERVICE_SETTINGS_REQUEST_CODE);
                    }
                    break;
                }
                case Enums.Net.StatusTypes.ERROR: {
                    Toast.makeText(getActivity(), R.string.call_settings_unknown_call_setting_toast, Toast.LENGTH_LONG).show();
                    break;
                }
            }
        }
    };
    private final Observer<SingleEvent<Boolean>> mInternetConnectionErrorObserver = singleEvent -> {
        if (singleEvent != null && singleEvent.getContentIfNotHandled() != null) {
            if (getActivity() == null) {
                return;
            }

            mDialogManager.showDialog(
                    getActivity(),
                    R.string.error_no_internet_title,
                    R.string.error_no_internet_call_settings_message,
                    R.string.general_ok,
                    (dialog, which) -> mAnalyticsManager.logEvent(getAnalyticScreenName(), NO_INTERNET_DIALOG_OK_BUTTON_PRESSED),
                    R.string.error_no_internet_settings_button,
                    (dialog, which) -> {
                        mViewModel.navigateToInternetSettings(getActivity());
                        mAnalyticsManager.logEvent(getAnalyticScreenName(), NO_INTERNET_DIALOG_SETTINGS_BUTTON_PRESSED);
                    });

            mAnalyticsManager.logEvent(getAnalyticScreenName(), NO_INTERNET_DIALOG_SHOWN);
        }
    };

    public CallSettingsListFragment() {
    }

    public static CallSettingsListFragment newInstance() {
        Bundle args = new Bundle();

        CallSettingsListFragment fragment = new CallSettingsListFragment();
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

    @Override
    public void onResume() {
        super.onResume();
        mViewModel.getServiceSettings();
        mAnalyticsManager.logScreenView(getAnalyticScreenName());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(CallSettingsListViewModel.class);

        mViewModel.getListItemsListLiveData().observe(this, mListItemsObserver);
        mViewModel.getNavigateToSetCallSettingsIntentLiveData().observe(this, mNavigateToSetCallSettingsObserver);
        mViewModel.getInternetConnectionErrorLiveData().observe(this, mInternetConnectionErrorObserver);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!mViewModel.onActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    // --------------------------------------------------------------------------------------------
    // GeneralRecyclerViewFragment Methods
    // --------------------------------------------------------------------------------------------
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_call_settings_list;
    }

    @Override
    protected int getSwipeRefreshLayoutId() {
        return R.id.call_settings_swipe_refresh_layout;
    }

    @Override
    protected int getRecyclerViewId() {
        return R.id.call_settings_recycler_view;
    }

    @Override
    protected String getAnalyticScreenName() {
        return Enums.Analytics.ScreenName.CALL_SETTINGS_LIST;
    }

    @Override
    protected void fetchItemList(boolean forceRefresh) {
        if (mIsRefreshing) {
            return;
        }

        mViewModel.getServiceSettings();
    }
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // MasterListListener Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public void onDetailItemViewListItemClicked(@NonNull final DetailItemViewListItem listItem) {
        super.onDetailItemViewListItemClicked(listItem);

        mViewModel.onDetailItemViewListItemClicked(listItem);

        if (listItem instanceof ServiceSettingsListItem) {
            switch (((ServiceSettingsListItem) listItem).getServiceSettings().getType()) {
                case Enums.Service.TYPE_BROADWORKS_ANYWHERE:
                    mAnalyticsManager.logEvent(getAnalyticScreenName(), Enums.Analytics.EventName.NEXTIVA_ANYWHERE_LIST_ITEM_PRESSED);
                    break;
                case Enums.Service.TYPE_SIMULTANEOUS_RING_PERSONAL:
                    mAnalyticsManager.logEvent(getAnalyticScreenName(), Enums.Analytics.EventName.SIMULTANEOUS_RING_LIST_ITEM_PRESSED);
                    break;
                case Enums.Service.TYPE_REMOTE_OFFICE:
                    mAnalyticsManager.logEvent(getAnalyticScreenName(), Enums.Analytics.EventName.REMOTE_OFFICE_LIST_ITEM_PRESSED);
                    break;
                case Enums.Service.TYPE_CALL_FORWARDING_NOT_REACHABLE:
                    mAnalyticsManager.logEvent(getAnalyticScreenName(), Enums.Analytics.EventName.CALL_FORWARDING_WHEN_UNREACHABLE_LIST_ITEM_PRESSED);
                    break;
                case Enums.Service.TYPE_CALL_FORWARDING_ALWAYS:
                    mAnalyticsManager.logEvent(getAnalyticScreenName(), Enums.Analytics.EventName.CALL_FORWARDING_ALWAYS_LIST_ITEM_PRESSED);
                    break;
                case Enums.Service.TYPE_CALL_FORWARDING_NO_ANSWER:
                    mAnalyticsManager.logEvent(getAnalyticScreenName(), Enums.Analytics.EventName.CALL_FORWARDING_WHEN_UNANSWERED_LIST_ITEM_PRESSED);
                    break;
                case Enums.Service.TYPE_CALL_FORWARDING_BUSY:
                    mAnalyticsManager.logEvent(getAnalyticScreenName(), Enums.Analytics.EventName.CALL_FORWARDING_WHEN_BUSY_LIST_ITEM_PRESSED);
                    break;
                case Enums.Service.TYPE_DO_NOT_DISTURB:
                    mAnalyticsManager.logEvent(getAnalyticScreenName(), Enums.Analytics.EventName.DO_NOT_DISTURB_LIST_ITEM_PRESSED);
                    break;
                case Enums.Service.TYPE_CALLING_LINE_ID_DELIVERY_BLOCKING:
                    mAnalyticsManager.logEvent(getAnalyticScreenName(), Enums.Analytics.EventName.BLOCK_MY_CALLER_ID_LIST_ITEM_PRESSED);
                    break;
            }

        } else if (listItem instanceof LocalSettingListItem) {
            switch (((LocalSettingListItem) listItem).getSettingKey()) {
                case SharedPreferencesManager.DIALING_SERVICE:
                    mAnalyticsManager.logEvent(getAnalyticScreenName(), Enums.Analytics.EventName.DIALING_SERVICE_LIST_ITEM_PRESSED);
                    break;
                case SharedPreferencesManager.THIS_PHONE_NUMBER:
                    mAnalyticsManager.logEvent(getAnalyticScreenName(), Enums.Analytics.EventName.THIS_PHONE_NUMBER_LIST_ITEM_PRESSED);
                    break;
                case SharedPreferencesManager.ALLOW_TERMINATION:
                    mAnalyticsManager.logEvent(getAnalyticScreenName(), Enums.Analytics.EventName.ALLOW_TERMINATION_LIST_ITEM_CLICKED);
                    break;
            }
        }
    }
    // --------------------------------------------------------------------------------------------
}
