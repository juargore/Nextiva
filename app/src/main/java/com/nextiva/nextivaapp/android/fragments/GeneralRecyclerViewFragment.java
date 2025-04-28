/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.CallSuper;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.MasterListAdapter;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.MasterListListener;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.CallHistoryListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ChatConversationListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactCategoryListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactDetailHeaderListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactDetailListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactHeaderListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectHomeListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ContactListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.DetailItemViewListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.DialogContactActionDetailListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.DialogContactActionHeaderListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.DialogContactActionListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.FeatureFlagListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.HeaderListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.MessageListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.SimpleBaseListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.SmsMessageListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.VoicemailListItem;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.db.DbManager;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.ConversationRepository;
import com.nextiva.nextivaapp.android.managers.interfaces.AnalyticsManager;
import com.nextiva.nextivaapp.android.managers.interfaces.CalendarManager;
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager;
import com.nextiva.nextivaapp.android.managers.interfaces.SettingsManager;
import com.nextiva.nextivaapp.android.models.ChatMessage;
import com.nextiva.nextivaapp.android.models.SmsMessage;
import com.nextiva.nextivaapp.android.util.ApplicationUtil;
import com.nextiva.nextivaapp.android.util.LogUtil;
import com.nextiva.nextivaapp.android.view.ConnectEmptyStateView;
import com.nextiva.nextivaapp.android.view.EmptyStateView;
import com.nextiva.nextivaapp.android.view.SeparatorDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Created by adammacdonald on 2/12/18.
 */

@AndroidEntryPoint
public abstract class GeneralRecyclerViewFragment extends BaseFragment implements MasterListListener {

    @Nullable
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected RecyclerView mRecyclerView;
    @Nullable
    protected EmptyStateView mEmptyStateView;
    @Nullable
    protected ConnectEmptyStateView mConnectEmptyStateView;
    @Nullable
    protected ConnectEmptyStateView mConnectEmptySearchResultsView;

    @Inject
    protected AnalyticsManager mAnalyticsManager;
    @Inject
    protected SettingsManager mSettingsManager;
    @Inject
    protected SessionManager mSessionManager;
    @Inject
    protected ConversationRepository mConversationRepository;
    @Inject
    protected CalendarManager mCalendarManager;
    @Inject
    protected DbManager mDbManager;

    protected final List<BaseListItem> mListItems = Collections.synchronizedList(new ArrayList<>());
    protected MasterListAdapter mAdapter;
    protected LinearLayoutManager mLayoutManager;
    protected MasterListListener mMasterListListener;

    protected boolean mIsRefreshing = false;
    protected boolean shouldAddDivider = true;
    protected boolean shouldReverseLayout = false;

    private final Handler mHandlerRefreshing = new Handler(Looper.getMainLooper());
    private final Runnable mRunnableStartRefreshing = new Runnable() {
        @Override
        public void run() {
            if (mSwipeRefreshLayout != null) {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        }
    };
    private final Runnable mRunnableStopRefreshing = new Runnable() {
        @Override
        public void run() {
            if (mSwipeRefreshLayout != null) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }
    };

    public GeneralRecyclerViewFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() == null) {
            return;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Inflate the layout for this fragment
        View view = inflater.inflate(getLayoutId(), container, false);

        if (getActivity() == null) {
            return view;
        }

        if (getSwipeRefreshLayoutId() != 0) {
            mSwipeRefreshLayout = view.findViewById(getSwipeRefreshLayoutId());
        }
        mRecyclerView = view.findViewById(getRecyclerViewId());
        if (getEmptyStateViewId() != 0) {
            mEmptyStateView = view.findViewById(getEmptyStateViewId());
        }
        if (getConnectEmptyStateViewId() != 0) {
            mConnectEmptyStateView = view.findViewById(getConnectEmptyStateViewId());
        }
        if (getConnectEmptySearchResultsViewId() != 0) {
            mConnectEmptySearchResultsView = view.findViewById(getConnectEmptySearchResultsViewId());
        }

        mLayoutManager = getLayoutManager();

        if (shouldReverseLayout) {
            mLayoutManager.setReverseLayout(true);
        }

        mAdapter = new MasterListAdapter(getActivity(),
                                         mListItems,
                                         this,
                                         mCalendarManager, mDbManager, mSessionManager,
                                         mSettingsManager);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        if (shouldAddDivider) {
            mRecyclerView.addItemDecoration(new SeparatorDecoration(ApplicationUtil.isNightModeEnabled(getActivity(), mSettingsManager) ?
                                                                            ContextCompat.getColor(getActivity(), R.color.listItemDividerDark) :
                                                                            ContextCompat.getColor(getActivity(), R.color.listItemDividerLight),
                                                                    getResources().getDimension(R.dimen.hairline_small)));
        }

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

            }
        });

        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(false);
            mSwipeRefreshLayout.setColorSchemeResources(R.color.nextivaOrange, R.color.nextivaPrimaryBlue);
            mSwipeRefreshLayout.setProgressBackgroundColorSchemeColor(ApplicationUtil.isNightModeEnabled(getActivity(), mSettingsManager) ?
                                                                              getResources().getColor(R.color.black) :
                                                                              getResources().getColor(R.color.white));
            mSwipeRefreshLayout.setOnRefreshListener(() -> {
                fetchItemList(true);
                logEvent(Enums.Analytics.EventName.PULL_TO_REFRESH);
            });
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Ensure mRecyclerView is initialized before calling fetchItemListInitial
        if (mRecyclerView != null) {
            fetchItemListInitial();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mHandlerRefreshing.removeCallbacks(mRunnableStartRefreshing);
        mHandlerRefreshing.removeCallbacks(mRunnableStopRefreshing);
        mHandlerRefreshing.post(mRunnableStopRefreshing);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mMasterListListener = (MasterListListener) context;
        } catch (ClassCastException e) {
            LogUtil.log(context.getClass().getSimpleName() + " is probably meant to implement MasterListListener.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mAdapter != null) {
            mAdapter.clearList();
            mAdapter = null;
        }
    }

    /**
     * Fetch method which will be called each time the screen is shown to the user
     * <br><br>
     * The default functionality of this method is to fetch for items only if there are
     * currently no items shown.  If there are items in the recycler view, the recycler
     * view will just be shown
     *
     * <br><br>
     * This method will be called from within {@link GeneralRecyclerViewFragment#onStart()}
     */
    void fetchItemListInitial() {
        // This will prevent the screen from refreshing too often if it already has data.
        // We might want to readdress this, maybe with websockets
        if (mRecyclerView.getAdapter() != null && mRecyclerView.getAdapter().getItemCount() == 0) {
            fetchItemList(false);
        } else {
            showRecyclerView();
        }
    }

    protected void fetchItemList(boolean forceRefresh) {
    }

    private LinearLayoutManager getLayoutManager() {
        return new LinearLayoutManager(getActivity());
    }

    @LayoutRes
    protected abstract int getLayoutId();

    @IdRes
    protected abstract int getSwipeRefreshLayoutId();

    @IdRes
    protected abstract int getRecyclerViewId();

    @IdRes
    protected int getEmptyStateViewId() {
        return 0;
    }

    @IdRes
    protected int getConnectEmptyStateViewId() {
        return 0;
    }

    @IdRes
    protected int getConnectEmptySearchResultsViewId() {
        return 0;
    }

    @Enums.Analytics.ScreenName.Screen
    protected abstract String getAnalyticScreenName();

    protected void startRefreshing() {
        if (mIsRefreshing) {
            return;
        }
        mIsRefreshing = true;
        mHandlerRefreshing.removeCallbacks(mRunnableStartRefreshing);
        mHandlerRefreshing.removeCallbacks(mRunnableStopRefreshing);
        mHandlerRefreshing.postDelayed(mRunnableStartRefreshing, 200);
    }

    public void stopRefreshing() {
        mIsRefreshing = false;
        final boolean isLoadingMore = false;
        mHandlerRefreshing.removeCallbacks(mRunnableStartRefreshing);
        mHandlerRefreshing.removeCallbacks(mRunnableStopRefreshing);
        mHandlerRefreshing.postDelayed(mRunnableStopRefreshing, 200);
    }

    public void scrollToTop() {
        if( mRecyclerView != null && mRecyclerView.getLayoutManager() != null)
            mRecyclerView.scrollToPosition(0);
    }

    public void recyclerViewAddScrollListener(RecyclerView.OnScrollListener listener) {
        if (mRecyclerView != null && mRecyclerView.getLayoutManager() != null) {
            mRecyclerView.addOnScrollListener(listener);
        }
    }

    protected void showCorrectState() {
        if (mRecyclerView.getAdapter() != null && mRecyclerView.getAdapter().getItemCount() == 0) {
            showEmptyState();

        } else {
            showRecyclerView();
        }
    }

    protected void showEmptyState() {
        mRecyclerView.setVisibility(View.GONE);

        if (mConnectEmptyStateView != null) {
            mConnectEmptyStateView.setVisibility(View.VISIBLE);
        }

        if (mEmptyStateView != null) {
            mEmptyStateView.setVisibility(View.VISIBLE);
        }

        if (mConnectEmptySearchResultsView != null) {
            mConnectEmptySearchResultsView.setVisibility(View.GONE);
        }

        mAdapter.clearList();
    }

    protected void showEmptySearchResultsState() {
        mRecyclerView.setVisibility(View.GONE);

        if (mConnectEmptyStateView != null) {
            mConnectEmptyStateView.setVisibility(View.GONE);
        }

        if (mEmptyStateView != null) {
            mEmptyStateView.setVisibility(View.GONE);
        }

        if (mConnectEmptySearchResultsView != null) {
            mConnectEmptySearchResultsView.setVisibility(View.VISIBLE);
        }

        mAdapter.clearList();
    }

    public void setButtonClickListener(View.OnClickListener clickListener) {
        if (mConnectEmptyStateView != null) {
            mConnectEmptyStateView.setButtonClickListener(clickListener);
        }
    }

    protected void showRecyclerView() {
        if (mConnectEmptyStateView != null) {
            mConnectEmptyStateView.setVisibility(View.GONE);
        }
        if (mEmptyStateView != null) {
            mEmptyStateView.setVisibility(View.GONE);
        }
        if (mConnectEmptySearchResultsView != null) {
            mConnectEmptySearchResultsView.setVisibility(View.GONE);
        }
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void logEvent(@Enums.Analytics.EventName.Event @NonNull String event) {
        if (!TextUtils.isEmpty(getAnalyticScreenName())) {
            mAnalyticsManager.logEvent(getAnalyticScreenName(), event);
        }
    }

    // --------------------------------------------------------------------------------------------
    // MasterListListener Methods
    // --------------------------------------------------------------------------------------------
    @CallSuper
    @Override
    public void onCallHistoryListItemClicked(@NonNull CallHistoryListItem listItem) {
        if (mMasterListListener != null) {
            mMasterListListener.onCallHistoryListItemClicked(listItem);
        }

        mAnalyticsManager.logEvent(getAnalyticScreenName(), Enums.Analytics.EventName.CALL_HISTORY_LIST_ITEM_PRESSED);
    }

    @CallSuper
    @Override
    public void onCallHistoryListItemLongClicked(@NonNull CallHistoryListItem listItem) {
        if (mMasterListListener != null) {
            mMasterListListener.onCallHistoryListItemLongClicked(listItem);
        }

        mAnalyticsManager.logEvent(getAnalyticScreenName(), Enums.Analytics.EventName.CALL_HISTORY_LIST_ITEM_LONG_PRESSED);
    }

    @CallSuper
    @Override
    public void onCallHistoryCallButtonClicked(@NonNull CallHistoryListItem listItem) {
        if (mMasterListListener != null) {
            mMasterListListener.onCallHistoryCallButtonClicked(listItem);
        }

        mAnalyticsManager.logEvent(getAnalyticScreenName(), Enums.Analytics.EventName.CALL_HISTORY_LIST_ITEM_VOICE_CALL_BUTTON_PRESSED);
    }

    @CallSuper
    @Override
    public void onDetailItemViewListItemClicked(@NonNull DetailItemViewListItem listItem) {
        if (mMasterListListener != null) {
            mMasterListListener.onDetailItemViewListItemClicked(listItem);
        }
    }

    @CallSuper
    @Override
    public void onDetailItemViewListItemLongClicked(@NonNull DetailItemViewListItem listItem) {
        if (mMasterListListener != null) {
            mMasterListListener.onDetailItemViewListItemLongClicked(listItem);
        }
    }

    @CallSuper
    @Override
    public void onDetailItemViewListItemAction1ButtonClicked(@NonNull DetailItemViewListItem listItem) {
        if (mMasterListListener != null) {
            mMasterListListener.onDetailItemViewListItemAction1ButtonClicked(listItem);
        }
    }

    @CallSuper
    @Override
    public void onDetailItemViewListItemAction2ButtonClicked(@NonNull DetailItemViewListItem listItem) {
        if (mMasterListListener != null) {
            mMasterListListener.onDetailItemViewListItemAction2ButtonClicked(listItem);
        }
    }

    @CallSuper
    @Override
    public void onContactHeaderListItemClicked(@NonNull HeaderListItem listItem) {
        if (mMasterListListener != null) {
            mMasterListListener.onContactHeaderListItemClicked(listItem);
        }

        mAnalyticsManager.logEvent(getAnalyticScreenName(), listItem.isExpanded() ?
                Enums.Analytics.EventName.SECTION_HEADER_EXPANDED :
                Enums.Analytics.EventName.SECTION_HEADER_COLLAPSED);
    }

    @CallSuper
    @Override
    public void onContactHeaderListItemLongClicked(@NonNull HeaderListItem listItem) {
        if (mMasterListListener != null) {
            mMasterListListener.onContactHeaderListItemLongClicked(listItem);
        }
    }

    @CallSuper
    @Override
    public void onContactListItemClicked(@NonNull ContactListItem listItem) {
        if (mMasterListListener != null) {
            mMasterListListener.onContactListItemClicked(listItem);
        }

        mAnalyticsManager.logEvent(getAnalyticScreenName(), Enums.Analytics.EventName.CONTACT_LIST_ITEM_PRESSED);
    }

    @CallSuper
    @Override
    public void onContactListItemLongClicked(@NonNull ContactListItem listItem) {
        if (mMasterListListener != null) {
            mMasterListListener.onContactListItemClicked(listItem);
        }

        mAnalyticsManager.logEvent(getAnalyticScreenName(), Enums.Analytics.EventName.CONTACT_LIST_ITEM_LONG_PRESSED);
    }

    @CallSuper
    @Override
    public void onChatConversationItemClicked(@NonNull ChatConversationListItem listItem) {
        if (mMasterListListener != null) {
            mMasterListListener.onChatConversationItemClicked(listItem);
        }

        mAnalyticsManager.logEvent(getAnalyticScreenName(), Enums.Analytics.EventName.CHAT_LIST_ITEM_PRESSED);
    }

    @CallSuper
    @Override
    public void onChatConversationItemLongClicked(@NonNull ChatConversationListItem listItem) {
        if (mMasterListListener != null) {
            mMasterListListener.onChatConversationItemLongClicked(listItem);
        }

//        mAnalyticsManager.logEvent(getAnalyticScreenName(), Enums.Analytics.EventName.);
    }

    @Override
    public void onResendFailedChatMessageClicked(@NonNull SimpleBaseListItem<ChatMessage> listItem) {
        if (mMasterListListener != null) {
            mMasterListListener.onResendFailedChatMessageClicked(listItem);
        }
    }

    @Override
    public void onResendFailedSmsMessageClicked(@NonNull SimpleBaseListItem<SmsMessage> listItem) {
        if (mMasterListListener != null) {
            mMasterListListener.onResendFailedSmsMessageClicked(listItem);
        }
    }

    @CallSuper
    @Override
    public void onChatMessageListItemDatetimeVisibilityToggled(@NonNull SimpleBaseListItem<ChatMessage> listItem) {
        if (mMasterListListener != null) {
            mMasterListListener.onChatMessageListItemDatetimeVisibilityToggled(listItem);
        }

//        mAnalyticsManager.logEvent(getAnalyticScreenName(), Enums.Analytics.EventName.);
    }

    @Override
    public void onVoicemailCallButtonClicked(@NonNull VoicemailListItem listItem) {
        if (mMasterListListener != null) {
            mMasterListListener.onVoicemailCallButtonClicked(listItem);
        }
    }

    @Override
    public void onVoicemailReadButtonClicked(@NonNull VoicemailListItem listItem) {
        if (mMasterListListener != null) {
            mMasterListListener.onVoicemailReadButtonClicked(listItem);
        }
    }

    @Override
    public void onVoicemailDeleteButtonClicked(@NonNull VoicemailListItem listItem) {
        if (mMasterListListener != null) {
            mMasterListListener.onVoicemailDeleteButtonClicked(listItem);
        }
    }

    @Override
    public void onVoicemailContactButtonClicked(@NonNull VoicemailListItem listItem) {
        if (mMasterListListener != null) {
            mMasterListListener.onVoicemailContactButtonClicked(listItem);
        }
    }

    @Override
    public void onVoicemailSmsButtonClicked(@NonNull VoicemailListItem listItem) {
        if (mMasterListListener != null) {
            mMasterListListener.onVoicemailSmsButtonClicked(listItem);
        }
    }

    @Override
    public void onSmsConversationItemClicked(@NonNull MessageListItem listItem) {
        if (mMasterListListener != null) {
            mMasterListListener.onSmsConversationItemClicked(listItem);
        }
    }

    @Override
    public void onSmsMessageListItemDatetimeVisibilityToggled(@NonNull SmsMessageListItem listItem) {
        if (mMasterListListener != null) {
            mMasterListListener.onSmsMessageListItemDatetimeVisibilityToggled(listItem);
        }
    }

    @Override
    public void onConnectContactHeaderListItemClicked(@NonNull ConnectContactHeaderListItem listItem) {
        if (mMasterListListener != null) {
            mMasterListListener.onConnectContactHeaderListItemClicked(listItem);
        }
    }

    @Override
    public void onPositiveRatingItemClicked(@NonNull VoicemailListItem voicemailListItem) {
        if (mMasterListListener != null) {
            mMasterListListener.onPositiveRatingItemClicked(voicemailListItem);
        }
    }

    @Override
    public void onNegativeRatingItemClicked(@NonNull VoicemailListItem voicemailListItem) {
        if (mMasterListListener != null) {
            mMasterListListener.onNegativeRatingItemClicked(voicemailListItem);
        }
    }


    @Override
    public void onConnectContactFavoriteIconClicked(@NonNull ConnectContactListItem listItem) {
        if (mMasterListListener != null) {
            mMasterListListener.onConnectContactFavoriteIconClicked(listItem);
        }
    }

    @Override
    public void onConnectContactListItemClicked(@NonNull ConnectContactListItem listItem) {
        if (mMasterListListener != null) {
            mMasterListListener.onConnectContactListItemClicked(listItem);
        }
    }

    @Override
    public void onConnectContactDetailHeaderListItemClicked(@NonNull ConnectContactDetailHeaderListItem listItem) {
        if (mMasterListListener != null) {
            mMasterListListener.onConnectContactDetailHeaderListItemClicked(listItem);
        }
    }

    @Override
    public void onConnectContactCategoryItemClicked(@NonNull ConnectContactCategoryListItem listItem) {
        if (mMasterListListener != null) {
            mMasterListListener.onConnectContactCategoryItemClicked(listItem);
        }
    }

    @Override
    public void onConnectContactDetailListItemClicked(@NonNull ConnectContactDetailListItem listItem) {
        if (mMasterListListener != null) {
            mMasterListListener.onConnectContactDetailListItemClicked(listItem);
        }
    }

    @Override
    public void onConnectHomeListItemClicked(@NonNull ConnectHomeListItem listItem) {
        if (mMasterListListener != null) {
            mMasterListListener.onConnectHomeListItemClicked(listItem);
        }
    }

    @Override
    public void onFeatureFlagListItemChecked(@NonNull FeatureFlagListItem listItem) {
        if (mMasterListListener != null) {
            mMasterListListener.onFeatureFlagListItemChecked(listItem);
        }
    }


    @Override
    public void onConnectContactListItemLongClicked(@NonNull ConnectContactListItem listItem) {
        if (mMasterListListener != null) {
            mMasterListListener.onConnectContactListItemLongClicked(listItem);
        }
    }

    @Override
    public void onDialogContactActionHeaderListItemClicked(@NonNull DialogContactActionHeaderListItem listItem) {
        if (mMasterListListener != null) {
            mMasterListListener.onDialogContactActionHeaderListItemClicked(listItem);
        }
    }

    @Override
    public void onDialogContactActionListItemClicked(@NonNull DialogContactActionListItem listItem) {
        if (mMasterListListener != null) {
            mMasterListListener.onDialogContactActionListItemClicked(listItem);
        }
    }

    @Override
    public void onDialogContactActionDetailListItemClicked(@NonNull DialogContactActionDetailListItem listItem) {
        if (mMasterListListener != null) {
            mMasterListListener.onDialogContactActionDetailListItemClicked(listItem);
        }
    }

    // --------------------------------------------------------------------------------------------
}
