package com.nextiva.nextivaapp.android.adapters.masterlistadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.CallCenterListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.CallDetailDatetimeListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.CallDetailPhoneNumberListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.CallHistoryListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ChatConversationListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ChatHeaderListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ChatMessageListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConferencePhoneNumberListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectCallDetailListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactCategoryListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactDetailFooterListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactDetailHeaderListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactDetailListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactDetailTeamsListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactHeaderListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectHomeHeaderListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectHomeListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ContactListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.DatabaseCountUtilityListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.DesignSystemListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.DetailItemViewListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.DialogContactActionDetailListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.DialogContactActionHeaderListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.DialogContactActionListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.FeatureFlagListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.FontAwesomeListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.HeaderListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.HealthCheckListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.LoadingListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.MeetingListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.MessageHeaderListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.MessageListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.NextivaAnywhereLocationListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ServiceSettingsListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.SimultaneousRingLocationListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.SmsMessageListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.VoicemailListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.CallCenterStatusViewHolder;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.CallDetailDatetimeViewHolder;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.CallDetailPhoneNumberViewHolder;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.CallHistoryViewHolder;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.ChatConversationViewHolder;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.ChatHeaderViewHolder;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.ChatMessageReceivedViewHolder;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.ChatMessageSentViewHolder;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.ConferencePhoneNumberViewHolder;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.ConnectCallDetailViewHolder;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.ConnectContactCategoryViewHolder;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.ConnectContactDetailFooterViewHolder;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.ConnectContactDetailHeaderViewHolder;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.ConnectContactDetailTeamsViewHolder;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.ConnectContactDetailViewHolder;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.ConnectContactHeaderViewHolder;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.ConnectContactViewHolder;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.ConnectHomeHeaderViewHolder;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.ConnectHomeViewHolder;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.ConnectMeetingsListViewHolder;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.ContactViewHolder;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.DatabaseCountUtilityViewHolder;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.DesignSystemViewHolder;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.DetailItemViewServiceSettingsViewHolder;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.DetailItemViewSimultaneousRingLocationViewHolder;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.DetailItemViewViewHolder;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.DialogContactActionDetailViewHolder;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.DialogContactActionHeaderViewHolder;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.DialogContactActionViewHolder;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.FeatureFlagViewHolder;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.FontAwesomeViewHolder;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.HeaderViewHolder;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.HealthCheckViewHolder;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.LoadingViewHolder;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.MessageHeaderViewHolder;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.MessagesListViewholder;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.NextivaAnywhereLocationViewHolder;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.SmsMessageReceivedViewHolder;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.SmsMessageSentViewHolder;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.VoicemailViewHolder;
import com.nextiva.nextivaapp.android.db.DbManager;
import com.nextiva.nextivaapp.android.managers.interfaces.CalendarManager;
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager;
import com.nextiva.nextivaapp.android.managers.interfaces.SettingsManager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MasterListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM_TYPE_CONTACT = 0;
    private static final int ITEM_TYPE_CALL_HISTORY = 1;
    private static final int ITEM_TYPE_NEXTIVA_ANYWHERE_LOCATION = 2;
    private static final int ITEM_TYPE_DETAIL_ITEM_VIEW = 3;
    private static final int ITEM_TYPE_HEADER = 4;
    private static final int ITEM_TYPE_CHAT_CONVERSATION = 5;
    private static final int ITEM_TYPE_CHAT_HEADER = 6;
    private static final int ITEM_TYPE_CHAT_MESSAGE_SENT = 7;
    private static final int ITEM_TYPE_CHAT_MESSAGE_RECEIVED = 8;
    private static final int ITEM_TYPE_CONFERENCE_PHONE_NUMBER = 9;
    private static final int ITEM_TYPE_SERVICE_SETTINGS = 10;
    private static final int ITEM_TYPE_SIMULTANEOUS_RING_LOCATION = 11;
    private static final int ITEM_TYPE_CALL_DETAIL_DATETIME = 12;
    private static final int ITEM_TYPE_CALL_DETAIL_PHONE_NUMBER = 13;
    private static final int ITEM_TYPE_VOICEMAIL = 14;
    private static final int ITEM_TYPE_CALL_CENTER = 15;
    private static final int ITEM_TYPE_MESSAGES = 16;
    private static final int ITEM_TYPE_SMS_MESSAGE_RECEIVED = 17;
    private static final int ITEM_TYPE_SMS_MESSAGE_SENT = 18;
    private static final int ITEM_TYPE_SMS_MESSAGE_HEADER = 19;
    private static final int ITEM_TYPE_FEATURE_FLAG = 20;
    private static final int ITEM_TYPE_FONT_AWESOME_UTILITY = 21;
    private static final int ITEM_TYPE_CONNECT_CONTACT_HEADER = 22;
    private static final int ITEM_TYPE_CONNECT_CONTACT = 23;
    private static final int ITEM_TYPE_CONNECT_CONTACT_DETAIL_HEADER = 24;
    private static final int ITEM_TYPE_CONNECT_CONTACT_DETAIL = 25;
    private static final int ITEM_TYPE_CONNECT_CONTACT_DETAIL_FOOTER = 26;
    private static final int ITEM_TYPE_CONNECT_CONTACT_DETAIL_TEAMS = 27;
    private static final int ITEM_TYPE_CONNECT_HOME_HEADER = 28;
    private static final int ITEM_TYPE_CONNECT_HOME = 29;
    private static final int ITEM_TYPE_DESIGN_SYSTEM_UTILITY = 30;
    private static final int ITEM_TYPE_DATABASE_COUNT_UTILITY = 31;
    private static final int ITEM_TYPE_CONNECT_CALL_DETAIL = 33;
    private static final int ITEM_TYPE_DIALOG_CONTACT_ACTION = 34;
    private static final int ITEM_TYPE_DIALOG_CONTACT_ACTION_HEADER = 35;
    private static final int ITEM_TYPE_DIALOG_CONTACT_ACTION_DETAIL = 36;
    private static final int ITEM_TYPE_HEALTH_CHECK = 37;
    private static final int ITEM_TYPE_CONNECT_MEETING = 38;
    private static final int ITEM_TYPE_CONNECT_CONTACT_CATEGORY = 39;

    private static final int ITEM_TYPE_LOADING = 999;

    @NonNull
    private final Context mContext;

    @NonNull
    private final List<BaseListItem> mListItems;

    @Nullable
    private final MasterListListener mMasterListListener;

    @Nullable
    private final DbManager dbManager;

    @Nullable
    private final SessionManager sessionManager;

    @Nullable
    private final SettingsManager settingsManager;

    private final LoadingListItem mLoadingListItem = new LoadingListItem();
    private final CalendarManager calendarManager;

    @Inject
    public MasterListAdapter(
            @NonNull Context context,
            @NonNull List<BaseListItem> listItems,
            @Nullable MasterListListener masterListListener,
            CalendarManager calendarManager,
            DbManager dbManager,
            SessionManager sessionManager,
            SettingsManager settingsManager) {

        mContext = context;
        mListItems = listItems;
        mMasterListListener = masterListListener;
        this.calendarManager = calendarManager;
        this.dbManager = dbManager;
        this.sessionManager = sessionManager;
        this.settingsManager = settingsManager;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        BaseListItem listItem = mListItems.get(position);

        if (listItem.getClass().equals(ContactListItem.class)) {
            return ITEM_TYPE_CONTACT;

        } else if (listItem.getClass().equals(CallHistoryListItem.class)) {
            return ITEM_TYPE_CALL_HISTORY;

        } else if (listItem.getClass().equals(LoadingListItem.class)) {
            return ITEM_TYPE_LOADING;

        } else if (CallDetailDatetimeListItem.class.isAssignableFrom(listItem.getClass())) {
            return ITEM_TYPE_CALL_DETAIL_DATETIME;

        } else if (CallDetailPhoneNumberListItem.class.isAssignableFrom(listItem.getClass())) {
            return ITEM_TYPE_CALL_DETAIL_PHONE_NUMBER;

        } else if (NextivaAnywhereLocationListItem.class.isAssignableFrom(listItem.getClass())) {
            return ITEM_TYPE_NEXTIVA_ANYWHERE_LOCATION;

        } else if (ConferencePhoneNumberListItem.class.isAssignableFrom(listItem.getClass())) {
            return ITEM_TYPE_CONFERENCE_PHONE_NUMBER;

        } else if (SimultaneousRingLocationListItem.class.isAssignableFrom(listItem.getClass())) {
            return ITEM_TYPE_SIMULTANEOUS_RING_LOCATION;

        } else if (ServiceSettingsListItem.class.isAssignableFrom(listItem.getClass())) {
            return ITEM_TYPE_SERVICE_SETTINGS;

        } else if (DetailItemViewListItem.class.isAssignableFrom(listItem.getClass())) {
            return ITEM_TYPE_DETAIL_ITEM_VIEW;

        } else if (listItem.getClass().equals(HeaderListItem.class)) {
            return ITEM_TYPE_HEADER;

        } else if (listItem.getClass().equals(ChatConversationListItem.class)) {
            return ITEM_TYPE_CHAT_CONVERSATION;
        } else if (listItem.getClass().equals(ChatHeaderListItem.class)) {
            return ITEM_TYPE_CHAT_HEADER;
        } else if (listItem.getClass().equals(MessageHeaderListItem.class)) {
            return ITEM_TYPE_SMS_MESSAGE_HEADER;
        } else if (listItem.getClass().equals(MessageListItem.class)) {
            return ITEM_TYPE_MESSAGES;
        } else if (listItem.getClass().equals(ChatMessageListItem.class)) {
            return ((ChatMessageListItem) listItem).getData().isSender() ?
                    ITEM_TYPE_CHAT_MESSAGE_SENT :
                    ITEM_TYPE_CHAT_MESSAGE_RECEIVED;

        } else if (listItem.getClass().equals(SmsMessageListItem.class)) {
            return ((SmsMessageListItem) listItem).getData().isSender() ?
                    ITEM_TYPE_SMS_MESSAGE_SENT :
                    ITEM_TYPE_SMS_MESSAGE_RECEIVED;

        } else if (listItem.getClass().equals(VoicemailListItem.class)) {
            return ITEM_TYPE_VOICEMAIL;

        } else if (listItem.getClass().equals(CallCenterListItem.class)) {
            return ITEM_TYPE_CALL_CENTER;

        } else if (listItem.getClass().equals(FeatureFlagListItem.class)) {
            return ITEM_TYPE_FEATURE_FLAG;

        } else if (listItem.getClass().equals(FontAwesomeListItem.class)) {
            return ITEM_TYPE_FONT_AWESOME_UTILITY;

        } else if (listItem.getClass().equals(ConnectContactHeaderListItem.class)) {
            return ITEM_TYPE_CONNECT_CONTACT_HEADER;

        } else if (listItem.getClass().equals(ConnectContactListItem.class)) {
            return ITEM_TYPE_CONNECT_CONTACT;

        } else if (listItem.getClass().equals(ConnectContactDetailHeaderListItem.class)) {
            return ITEM_TYPE_CONNECT_CONTACT_DETAIL_HEADER;

        } else if (listItem.getClass().equals(ConnectContactDetailTeamsListItem.class)) {
            return ITEM_TYPE_CONNECT_CONTACT_DETAIL_TEAMS;

        } else if (listItem.getClass().equals(ConnectContactDetailListItem.class)) {
            return ITEM_TYPE_CONNECT_CONTACT_DETAIL;

        } else if (listItem.getClass().equals(ConnectContactDetailFooterListItem.class)) {
            return ITEM_TYPE_CONNECT_CONTACT_DETAIL_FOOTER;

        } else if (listItem.getClass().equals(ConnectHomeHeaderListItem.class)) {
            return ITEM_TYPE_CONNECT_HOME_HEADER;

        } else if (listItem.getClass().equals(ConnectHomeListItem.class)) {
            return ITEM_TYPE_CONNECT_HOME;

        } else if (listItem.getClass().equals(DesignSystemListItem.class)) {
            return ITEM_TYPE_DESIGN_SYSTEM_UTILITY;

        } else if (listItem.getClass().equals(DatabaseCountUtilityListItem.class)) {
            return ITEM_TYPE_DATABASE_COUNT_UTILITY;

        } else if (listItem.getClass().equals(ConnectCallDetailListItem.class)) {
            return ITEM_TYPE_CONNECT_CALL_DETAIL;

        } else if (listItem.getClass().equals(DialogContactActionListItem.class)) {
            return ITEM_TYPE_DIALOG_CONTACT_ACTION;

        } else if (listItem.getClass().equals(DialogContactActionHeaderListItem.class)) {
            return ITEM_TYPE_DIALOG_CONTACT_ACTION_HEADER;

        } else if (listItem.getClass().equals(DialogContactActionDetailListItem.class)) {
            return ITEM_TYPE_DIALOG_CONTACT_ACTION_DETAIL;

        } else if (listItem.getClass().equals(HealthCheckListItem.class)) {
            return ITEM_TYPE_HEALTH_CHECK;

        } else if (listItem.getClass().equals(MeetingListItem.class)) {
            return ITEM_TYPE_CONNECT_MEETING;

        } else if (listItem.getClass().equals(ConnectContactCategoryListItem.class)) {
            return ITEM_TYPE_CONNECT_CONTACT_CATEGORY;
        } else {
            listItem.getClass();
        }

        return -1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM_TYPE_CONTACT: {
                return new ContactViewHolder(parent, mContext, mMasterListListener);
            }
            case ITEM_TYPE_CALL_HISTORY: {
                return new CallHistoryViewHolder(parent, mContext, mMasterListListener);
            }
            case ITEM_TYPE_LOADING: {
                return new LoadingViewHolder(parent, mContext);
            }
            case ITEM_TYPE_NEXTIVA_ANYWHERE_LOCATION: {
                return new NextivaAnywhereLocationViewHolder(parent, mContext, mMasterListListener);
            }
            case ITEM_TYPE_CONFERENCE_PHONE_NUMBER: {
                return new ConferencePhoneNumberViewHolder(parent, mContext, mMasterListListener);
            }
            case ITEM_TYPE_SERVICE_SETTINGS: {
                return new DetailItemViewServiceSettingsViewHolder(parent, mContext, mMasterListListener);
            }
            case ITEM_TYPE_SIMULTANEOUS_RING_LOCATION: {
                return new DetailItemViewSimultaneousRingLocationViewHolder(parent, mContext, mMasterListListener);
            }
            case ITEM_TYPE_CALL_DETAIL_DATETIME: {
                return new CallDetailDatetimeViewHolder(parent, mContext, mMasterListListener);
            }
            case ITEM_TYPE_CALL_DETAIL_PHONE_NUMBER: {
                return new CallDetailPhoneNumberViewHolder(parent, mContext, mMasterListListener);
            }
            case ITEM_TYPE_DETAIL_ITEM_VIEW: {
                return new DetailItemViewViewHolder(parent, mContext, mMasterListListener);
            }
            case ITEM_TYPE_HEADER: {
                return new HeaderViewHolder(parent, mContext, mMasterListListener);
            }
            case ITEM_TYPE_CHAT_CONVERSATION: {
                return new ChatConversationViewHolder(parent, mContext, mMasterListListener);
            }
            case ITEM_TYPE_CHAT_HEADER: {
                return new ChatHeaderViewHolder(parent, mContext, mMasterListListener);
            }
            case ITEM_TYPE_CHAT_MESSAGE_SENT: {
                return new ChatMessageSentViewHolder(parent, mContext, mMasterListListener);
            }
            case ITEM_TYPE_CHAT_MESSAGE_RECEIVED: {
                return new ChatMessageReceivedViewHolder(parent, mContext, mMasterListListener);
            }
            case ITEM_TYPE_VOICEMAIL: {
                return new VoicemailViewHolder(parent, mContext, mMasterListListener);
            }
            case ITEM_TYPE_CALL_CENTER: {
                return new CallCenterStatusViewHolder(parent, mContext, settingsManager);
            }
            case ITEM_TYPE_MESSAGES: {
                return new MessagesListViewholder(parent, mContext, mMasterListListener);
            }
            case ITEM_TYPE_SMS_MESSAGE_SENT: {
                return new SmsMessageSentViewHolder(parent, mContext, mMasterListListener);
            }
            case ITEM_TYPE_SMS_MESSAGE_RECEIVED: {
                return new SmsMessageReceivedViewHolder(parent, mContext, mMasterListListener);
            }
            case ITEM_TYPE_SMS_MESSAGE_HEADER: {
                return new MessageHeaderViewHolder(parent, mContext, mMasterListListener);
            }
            case ITEM_TYPE_FEATURE_FLAG: {
                return new FeatureFlagViewHolder(parent, mContext, mMasterListListener);
            }
            case ITEM_TYPE_FONT_AWESOME_UTILITY: {
                return new FontAwesomeViewHolder(parent, mContext, mMasterListListener);
            }
            case ITEM_TYPE_CONNECT_CONTACT_HEADER: {
                return new ConnectContactHeaderViewHolder(parent, mContext, mMasterListListener);
            }
            case ITEM_TYPE_CONNECT_CONTACT: {
                return new ConnectContactViewHolder(parent, mContext, mMasterListListener, dbManager, sessionManager);
            }
            case ITEM_TYPE_CONNECT_CONTACT_DETAIL_HEADER: {
                return new ConnectContactDetailHeaderViewHolder(parent, mContext, mMasterListListener);
            }
            case ITEM_TYPE_CONNECT_CONTACT_DETAIL_TEAMS: {
                return new ConnectContactDetailTeamsViewHolder(parent, mContext, mMasterListListener);
            }
            case ITEM_TYPE_CONNECT_CONTACT_DETAIL: {
                return new ConnectContactDetailViewHolder(parent, mContext, mMasterListListener);
            }
            case ITEM_TYPE_CONNECT_CONTACT_DETAIL_FOOTER: {
                return new ConnectContactDetailFooterViewHolder(parent, mContext, mMasterListListener);
            }
            case ITEM_TYPE_CONNECT_HOME_HEADER: {
                return new ConnectHomeHeaderViewHolder(parent, mContext, mMasterListListener);
            }
            case ITEM_TYPE_CONNECT_HOME: {
                return new ConnectHomeViewHolder(parent, mContext, mMasterListListener);
            }
            case ITEM_TYPE_DESIGN_SYSTEM_UTILITY: {
                return new DesignSystemViewHolder(parent, mContext, mMasterListListener);
            }
            case ITEM_TYPE_DATABASE_COUNT_UTILITY: {
                return new DatabaseCountUtilityViewHolder(parent, mContext, mMasterListListener);
            }
            case ITEM_TYPE_CONNECT_CALL_DETAIL: {
                View view = inflater.inflate(R.layout.list_item_connect_call_details, parent, false);
                return new ConnectCallDetailViewHolder(view, parent.getContext(), mMasterListListener);

            }
            case ITEM_TYPE_DIALOG_CONTACT_ACTION: {
                return new DialogContactActionViewHolder(parent, mContext, mMasterListListener);
            }
            case ITEM_TYPE_DIALOG_CONTACT_ACTION_HEADER: {
                return new DialogContactActionHeaderViewHolder(parent, mContext, mMasterListListener);
            }
            case ITEM_TYPE_DIALOG_CONTACT_ACTION_DETAIL: {
                return new DialogContactActionDetailViewHolder(parent, mContext, mMasterListListener);
            }
            case ITEM_TYPE_HEALTH_CHECK: {
                return new HealthCheckViewHolder(parent, mContext, mMasterListListener);
            }
            case ITEM_TYPE_CONNECT_MEETING: {
                return new ConnectMeetingsListViewHolder(parent, mContext, mMasterListListener);
            }
            case ITEM_TYPE_CONNECT_CONTACT_CATEGORY: {
                return new ConnectContactCategoryViewHolder(parent, mContext, mMasterListListener);
            }
        }
        return new RecyclerView.ViewHolder(parent) {};
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        BaseListItem listItem = mListItems.get(position);

        if (listItem != null) {
            if (ContactViewHolder.class.isAssignableFrom(holder.getClass()) && listItem.getClass().equals(ContactListItem.class)) {
                ((ContactViewHolder) holder).bind((ContactListItem) listItem);

            } else if (CallHistoryViewHolder.class.isAssignableFrom(holder.getClass()) && listItem.getClass().equals(CallHistoryListItem.class)) {
                ((CallHistoryViewHolder) holder).bind((CallHistoryListItem) listItem);

            } else if (NextivaAnywhereLocationViewHolder.class.isAssignableFrom(holder.getClass()) && NextivaAnywhereLocationListItem.class.isAssignableFrom(listItem.getClass())) {
                ((NextivaAnywhereLocationViewHolder) holder).bind((NextivaAnywhereLocationListItem) listItem);

            } else if (ConferencePhoneNumberViewHolder.class.isAssignableFrom(holder.getClass()) && ConferencePhoneNumberListItem.class.isAssignableFrom(listItem.getClass())) {
                ((ConferencePhoneNumberViewHolder) holder).bind((ConferencePhoneNumberListItem) listItem);

            } else if (DetailItemViewServiceSettingsViewHolder.class.isAssignableFrom(holder.getClass()) && ServiceSettingsListItem.class.isAssignableFrom(listItem.getClass())) {
                ((DetailItemViewServiceSettingsViewHolder) holder).bind((ServiceSettingsListItem) listItem);

            } else if (DetailItemViewSimultaneousRingLocationViewHolder.class.isAssignableFrom(holder.getClass()) && SimultaneousRingLocationListItem.class.isAssignableFrom(listItem.getClass())) {
                ((DetailItemViewSimultaneousRingLocationViewHolder) holder).bind((SimultaneousRingLocationListItem) listItem);

            } else if (CallDetailDatetimeViewHolder.class.isAssignableFrom(holder.getClass()) && CallDetailDatetimeListItem.class.isAssignableFrom(listItem.getClass())) {
                ((CallDetailDatetimeViewHolder) holder).bind((CallDetailDatetimeListItem) listItem);

            } else if (CallDetailPhoneNumberViewHolder.class.isAssignableFrom(holder.getClass()) && CallDetailPhoneNumberListItem.class.isAssignableFrom(listItem.getClass())) {
                ((CallDetailPhoneNumberViewHolder) holder).bind((CallDetailPhoneNumberListItem) listItem);

            } else if (DetailItemViewViewHolder.class.isAssignableFrom(holder.getClass()) && DetailItemViewListItem.class.isAssignableFrom(listItem.getClass())) {
                ((DetailItemViewViewHolder) holder).bind((DetailItemViewListItem) listItem);

            } else if (HeaderViewHolder.class.isAssignableFrom(holder.getClass()) && listItem.getClass().equals(HeaderListItem.class)) {
                ((HeaderViewHolder) holder).bind((HeaderListItem) listItem);

            } else if (ChatConversationViewHolder.class.isAssignableFrom(holder.getClass()) && listItem.getClass().equals(ChatConversationListItem.class)) {
                ((ChatConversationViewHolder) holder).bind((ChatConversationListItem) listItem);
            } else if (ChatHeaderViewHolder.class.isAssignableFrom(holder.getClass()) && listItem.getClass().equals(ChatHeaderListItem.class)) {
                ((ChatHeaderViewHolder) holder).bind((ChatHeaderListItem) listItem);

            } else if (ChatMessageSentViewHolder.class.isAssignableFrom(holder.getClass()) && listItem.getClass().equals(ChatMessageListItem.class)) {
                ((ChatMessageSentViewHolder) holder).bind((ChatMessageListItem) listItem);

            } else if (ChatMessageReceivedViewHolder.class.isAssignableFrom(holder.getClass()) && listItem.getClass().equals(ChatMessageListItem.class)) {
                ((ChatMessageReceivedViewHolder) holder).bind((ChatMessageListItem) listItem);

            } else if (VoicemailViewHolder.class.isAssignableFrom(holder.getClass()) && listItem.getClass().equals(VoicemailListItem.class)) {
                ((VoicemailViewHolder) holder).bind((VoicemailListItem) listItem);
            } else if (MessagesListViewholder.class.isAssignableFrom(holder.getClass()) && listItem.getClass().equals(MessageListItem.class)) {
                ((MessagesListViewholder) holder).bind((MessageListItem) listItem);
            } else if (SmsMessageSentViewHolder.class.isAssignableFrom(holder.getClass()) && listItem.getClass().equals(SmsMessageListItem.class)) {
                ((SmsMessageSentViewHolder) holder).bind((SmsMessageListItem) listItem);
            } else if (SmsMessageReceivedViewHolder.class.isAssignableFrom(holder.getClass()) && listItem.getClass().equals(SmsMessageListItem.class)) {
                ((SmsMessageReceivedViewHolder) holder).bind((SmsMessageListItem) listItem);
            } else if (MessageHeaderViewHolder.class.isAssignableFrom(holder.getClass()) && listItem.getClass().equals(MessageHeaderListItem.class)) {
                ((MessageHeaderViewHolder) holder).bind((MessageHeaderListItem) listItem);
            } else if (FeatureFlagViewHolder.class.isAssignableFrom(holder.getClass()) && listItem.getClass().equals(FeatureFlagListItem.class)) {
                ((FeatureFlagViewHolder) holder).bind((FeatureFlagListItem) listItem);
            } else if (FontAwesomeViewHolder.class.isAssignableFrom(holder.getClass()) && listItem.getClass().equals(FontAwesomeListItem.class)) {
                ((FontAwesomeViewHolder) holder).bind((FontAwesomeListItem) listItem);
            } else if (ConnectContactHeaderViewHolder.class.isAssignableFrom(holder.getClass()) && listItem.getClass().equals(ConnectContactHeaderListItem.class)) {
                ((ConnectContactHeaderViewHolder) holder).bind((ConnectContactHeaderListItem) listItem);
            } else if (ConnectContactViewHolder.class.isAssignableFrom(holder.getClass()) && listItem.getClass().equals(ConnectContactListItem.class)) {
                ((ConnectContactViewHolder) holder).bind((ConnectContactListItem) listItem);
            } else if (ConnectContactDetailHeaderViewHolder.class.isAssignableFrom(holder.getClass()) && listItem.getClass().equals(ConnectContactDetailHeaderListItem.class)) {
                ((ConnectContactDetailHeaderViewHolder) holder).bind((ConnectContactDetailHeaderListItem) listItem);
            } else if (ConnectContactDetailTeamsViewHolder.class.isAssignableFrom(holder.getClass()) && listItem.getClass().equals(ConnectContactDetailTeamsListItem.class)) {
                ((ConnectContactDetailTeamsViewHolder) holder).bind((ConnectContactDetailTeamsListItem) listItem);
            } else if (ConnectContactDetailViewHolder.class.isAssignableFrom(holder.getClass()) && listItem.getClass().equals(ConnectContactDetailListItem.class)) {
                ((ConnectContactDetailViewHolder) holder).bind((ConnectContactDetailListItem) listItem);
            } else if (ConnectContactDetailFooterViewHolder.class.isAssignableFrom(holder.getClass()) && listItem.getClass().equals(ConnectContactDetailFooterListItem.class)) {
                ((ConnectContactDetailFooterViewHolder) holder).bind((ConnectContactDetailFooterListItem) listItem);
            } else if (ConnectHomeHeaderViewHolder.class.isAssignableFrom(holder.getClass()) && listItem.getClass().equals(ConnectHomeHeaderListItem.class)) {
                ((ConnectHomeHeaderViewHolder) holder).bind((ConnectHomeHeaderListItem) listItem);
            } else if (ConnectHomeViewHolder.class.isAssignableFrom(holder.getClass()) && listItem.getClass().equals(ConnectHomeListItem.class)) {
                ((ConnectHomeViewHolder) holder).bind((ConnectHomeListItem) listItem);
            } else if (DesignSystemViewHolder.class.isAssignableFrom(holder.getClass()) && listItem.getClass().equals(DesignSystemListItem.class)) {
                ((DesignSystemViewHolder) holder).bind((DesignSystemListItem) listItem);
            } else if (DatabaseCountUtilityViewHolder.class.isAssignableFrom(holder.getClass()) && listItem.getClass().equals(DatabaseCountUtilityListItem.class)) {
                ((DatabaseCountUtilityViewHolder) holder).bind((DatabaseCountUtilityListItem) listItem);
            } else if (ConnectCallDetailViewHolder.class.isAssignableFrom(holder.getClass()) && listItem.getClass().equals(ConnectCallDetailListItem.class)) {
                ((ConnectCallDetailViewHolder) holder).bind((ConnectCallDetailListItem) listItem);
            } else if (DialogContactActionViewHolder.class.isAssignableFrom(holder.getClass()) && listItem.getClass().equals(DialogContactActionListItem.class)) {
                ((DialogContactActionViewHolder) holder).bind((DialogContactActionListItem) listItem);
            } else if (DialogContactActionHeaderViewHolder.class.isAssignableFrom(holder.getClass()) && listItem.getClass().equals(DialogContactActionHeaderListItem.class)) {
                ((DialogContactActionHeaderViewHolder) holder).bind((DialogContactActionHeaderListItem) listItem);
            } else if (DialogContactActionDetailViewHolder.class.isAssignableFrom(holder.getClass()) && listItem.getClass().equals(DialogContactActionDetailListItem.class)) {
                ((DialogContactActionDetailViewHolder) holder).bind((DialogContactActionDetailListItem) listItem);
            } else if (HealthCheckViewHolder.class.isAssignableFrom(holder.getClass()) && listItem.getClass().equals(HealthCheckListItem.class)) {
                ((HealthCheckViewHolder) holder).bind((HealthCheckListItem) listItem);
            } else if (ConnectMeetingsListViewHolder.class.isAssignableFrom(holder.getClass()) && listItem.getClass().equals(MeetingListItem.class)) {
                ((ConnectMeetingsListViewHolder) holder).bind((MeetingListItem) listItem);
            } else if (ConnectContactCategoryViewHolder.class.isAssignableFrom(holder.getClass()) && listItem.getClass().equals(ConnectContactCategoryListItem.class)) {
                ((ConnectContactCategoryViewHolder) holder).bind((ConnectContactCategoryListItem) listItem);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mListItems.size();
    }

    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);

        if (holder instanceof VoicemailViewHolder) {
            ((VoicemailViewHolder) holder).observerLiveData();
        } else if (holder instanceof SmsMessageSentViewHolder) {
            ((SmsMessageSentViewHolder) holder).addObservers();
        } else if (holder instanceof SmsMessageReceivedViewHolder) {
            ((SmsMessageReceivedViewHolder) holder).addObservers();
        }
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);

        if (holder instanceof VoicemailViewHolder) {
            ((VoicemailViewHolder) holder).removeObservers();
        } else if (holder instanceof SmsMessageSentViewHolder) {
            ((SmsMessageSentViewHolder) holder).removeObservers();
        } else if (holder instanceof SmsMessageReceivedViewHolder) {
            ((SmsMessageReceivedViewHolder) holder).removeObservers();
        }
    }

    // --------------------------------------------------------------------------------------------
    // Class Methods
    // --------------------------------------------------------------------------------------------
    public void updateList(@Nullable List<BaseListItem> newList) {
        if (newList == null) {
            newList = new ArrayList<>();
        }

        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new MasterDiffUtilCallback(mListItems, newList));

        mListItems.clear();
        mListItems.addAll(newList);
        diffResult.dispatchUpdatesTo(this);
    }

    public void clearList() {
        int size = mListItems.size();
        mListItems.clear();
        notifyItemRangeRemoved(0, size);
    }

    private void addItem(BaseListItem item, int position) {
        if (!mListItems.contains(item)) {
            mListItems.add(position, item);
            notifyItemInserted(position);
        }
    }

    public void addItem(BaseListItem item) {
        addItem(item, mListItems.size());
    }

    public void removeItem(BaseListItem item) {
        if (mListItems.contains(item)) {
            int position = mListItems.indexOf(item);
            mListItems.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void addLoadingListItem() {
        if (containsLoadingListItem()) {
            removeItem(mLoadingListItem);
        }
        addItem(mLoadingListItem);
    }

    public void removeLoadingListItem() {
        if (containsLoadingListItem()) {
            mListItems.remove(mLoadingListItem);
            notifyDataSetChanged();
        }
    }

    public boolean containsLoadingListItem() {
        return mListItems.contains(mLoadingListItem);
    }
    // --------------------------------------------------------------------------------------------
}
