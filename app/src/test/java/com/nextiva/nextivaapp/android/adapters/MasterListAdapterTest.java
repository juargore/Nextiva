/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.adapters;

import static junit.framework.Assert.assertEquals;

import androidx.test.core.app.ApplicationProvider;

import com.nextiva.nextivaapp.android.BaseRobolectricTest;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.MasterListAdapter;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.MasterListListener;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.HeaderListItem;
import com.nextiva.nextivaapp.android.models.ListHeaderRow;

import org.junit.Test;
import org.mockito.Mock;

import java.io.IOException;
import java.util.ArrayList;

public class MasterListAdapterTest extends BaseRobolectricTest {

    private MasterListAdapter mMasterListAdapter;
    private ArrayList<BaseListItem> mListItems = new ArrayList<>();

    @Mock
    private MasterListListener mMockMasterListListener;

    @Override
    public void setup() throws IOException {
        super.setup();

        mMasterListAdapter = new MasterListAdapter(ApplicationProvider.getApplicationContext(), mListItems, null, null, null, null, null);
    }

//    @Test
//    public void getItemViewType_contactListItem_returnsCorrectTypeInteger() {
//        mListItems.add(new ContactListItem(new NextivaContact("1"), "", true));
//
//        assertEquals(0, mMasterListAdapter.getItemViewType(0));
//    }
//
//    @Test
//    public void getItemViewType_callHistoryListItem_returnsCorrectTypeInteger() {
//        mListItems.add(new CallHistoryListItem(new CallLogEntry(null, null, null, null, null, null, null, null, Enums.Contacts.PresenceStates.NONE, -128, "", 0), true, true, null));
//
//        assertEquals(1, mMasterListAdapter.getItemViewType(0));
//    }
//
//    @Test
//    public void getItemViewType_nextivaAnywhereLocationListItem_returnsCorrectTypeInteger() {
//        mListItems.add(new NextivaAnywhereLocationListItem(new NextivaAnywhereLocation("1", null, null, null, null, null), "Title", "SubTitle"));
//
//        assertEquals(2, mMasterListAdapter.getItemViewType(0));
//    }
//
//    @Test
//    public void getItemViewType_contactDetailListItem_returnsCorrectTypeInteger() {
//        mListItems.add(new ContactDetailListItem(1, "Title", "SubTitle", 0, 0, true));
//
//        assertEquals(3, mMasterListAdapter.getItemViewType(0));
//    }
//
//    @Test
//    public void getItemViewType_localSettingListItem_returnsCorrectTypeInteger() {
//        mListItems.add(new LocalSettingListItem("key", "Title", "SubTitle"));
//
//        assertEquals(3, mMasterListAdapter.getItemViewType(0));
//    }
//
//    @Test
//    public void getItemViewType_simultaneousRingLocationListItem_returnsCorrectTypeInteger() {
//        mListItems.add(new SimultaneousRingLocationListItem(new SimultaneousRingLocation(), "Title", "SubTitle"));
//
//        assertEquals(11, mMasterListAdapter.getItemViewType(0));
//    }
//
//    @Test
//    public void getItemViewType_headerListItem_returnsCorrectTypeInteger() {
//        mListItems.add(new HeaderListItem(new ListHeaderRow(), new ArrayList<>(), true, false));
//
//        assertEquals(4, mMasterListAdapter.getItemViewType(0));
//    }
//
//    @Test
//    public void getItemViewType_chatConversationListItem_returnsCorrectTypeInteger() {
//        mListItems.add(new ChatConversationListItem(new ChatConversation("type"), new DbPresence(null, Enums.Contacts.PresenceStates.AVAILABLE, -10, null, Enums.Contacts.PresenceTypes.AVAILABLE)));
//
//        assertEquals(5, mMasterListAdapter.getItemViewType(0));
//    }
//
//    @Test
//    public void getItemViewType_chatHeader_returnsCorrectTypeInteger() {
//        mListItems.add(new ChatHeaderListItem(""));
//
//        assertEquals(6, mMasterListAdapter.getItemViewType(0));
//    }
//
//    @Test
//    public void getItemViewType_chatMessageSentListItem_returnsCorrectTypeInteger() {
//        mListItems.add(new ChatMessageListItem(new ChatMessage(null, null, null, null, null, true, false, null, null, null, null, null, null, null, null), "Today", new byte[] {2}));
//
//        assertEquals(7, mMasterListAdapter.getItemViewType(0));
//    }
//
//    @Test
//    public void getItemViewType_chatMessageReceivedListItem_returnsCorrectTypeInteger() {
//        mListItems.add(new ChatMessageListItem(new ChatMessage(null, null, null, null, null, false, false, null, null, null, null, null, null, null, null), "Today", new byte[] {2}));
//
//        assertEquals(8, mMasterListAdapter.getItemViewType(0));
//    }
//
//    @Test
//    public void getItemViewType_conferencePhoneNumberListItem_returnsCorrectTypeInteger() {
//        mListItems.add(new ConferencePhoneNumberListItem(1, "Title", null, null, null, null));
//
//        assertEquals(9, mMasterListAdapter.getItemViewType(0));
//    }
//
//    @Test
//    public void getItemViewType_serviceSettingsListItem_returnsCorrectTypeInteger() {
//        ServiceSettings serviceSettings = new ServiceSettings(Enums.Service.TYPE_REMOTE_OFFICE, "uri");
//        mListItems.add(new ServiceSettingsListItem(serviceSettings, "Title", "SubTitle"));
//
//        assertEquals(10, mMasterListAdapter.getItemViewType(0));
//    }
//
//    @Test
//    public void getItemViewType_callDetailDatetimeListItem_returnsCorrectTypeInteger() {
//        mListItems.add(new CallDetailDatetimeListItem(Enums.Calls.CallTypes.MISSED, "SubTitle"));
//
//        assertEquals(12, mMasterListAdapter.getItemViewType(0));
//    }
//
//    @Test
//    public void getItemViewType_callDetailPhoneNumberListItem_returnsCorrectTypeInteger() {
//        mListItems.add(new CallDetailPhoneNumberListItem("Title", "SubTitle", R.drawable.ic_phone, R.drawable.ic_video));
//
//        assertEquals(13, mMasterListAdapter.getItemViewType(0));
//    }
//
//    @Test
//    public void getItemViewType_callDetailListItem_returnsCorrectTypeInteger() {
//        mListItems.add(new CallDetailListItem(Enums.Calls.DetailViewTypes.SEND_PERSONAL_SMS, "Title", "SubTitle", R.drawable.ic_phone, R.drawable.ic_video, true));
//
//        assertEquals(3, mMasterListAdapter.getItemViewType(0));
//    }
//
//    @Test
//    public void getItemViewType_loadingListItem_returnsCorrectTypeInteger() {
//        mListItems.add(new LoadingListItem());
//
//        assertEquals(999, mMasterListAdapter.getItemViewType(0));
//    }
//
//    @Test
//    public void getItemViewType_unknownListItem_returnsCorrectTypeInteger() {
//        mListItems.add(new BaseListItem() {
//        });
//
//        assertEquals(-1, mMasterListAdapter.getItemViewType(0));
//    }

//    @Test
//    public void onCreateViewHolder_contactListItem_returnsCorrectViewHolder() {
//        mListItems.add(new ContactListItem(new NextivaContact("1"), "", true));
//
//        assertThat(mMasterListAdapter.onCreateViewHolder(new LinearLayout(ApplicationProvider.getApplicationContext()), mMasterListAdapter.getItemViewType(0)), instanceOf(ContactViewHolder.class));
//    }
//
//    @Test
//    public void onCreateViewHolder_callHistoryListItem_returnsCorrectViewHolder() {
//        mListItems.add(new CallHistoryListItem(new CallLogEntry(null, null, null, null, null, null, null, null, Enums.Contacts.PresenceStates.NONE, -128, "", 0), true, true, null));
//
//
//        LinearLayout linearLayout = new LinearLayout(ApplicationProvider.getApplicationContext());
//        RecyclerView.ViewHolder masterListAdapter = mMasterListAdapter.onCreateViewHolder(linearLayout, mMasterListAdapter.getItemViewType(0));
//
//        assertThat(masterListAdapter, instanceOf(CallHistoryViewHolder.class));
//    }
//
//    @Test
//    public void onCreateViewHolder_nextivaAnywhereLocationListItem_returnsCorrectViewHolder() {
//        mListItems.add(new NextivaAnywhereLocationListItem(new NextivaAnywhereLocation("1", null, null, null, null, null), "Title", "SubTitle"));
//
//        assertThat(mMasterListAdapter.onCreateViewHolder(new LinearLayout(ApplicationProvider.getApplicationContext()), mMasterListAdapter.getItemViewType(0)), instanceOf(NextivaAnywhereLocationViewHolder.class));
//    }
//
//    @Test
//    public void onCreateViewHolder_contactDetailListItem_returnsCorrectViewHolder() {
//        mListItems.add(new ContactDetailListItem(1, "Title", "SubTitle", 0, 0, true));
//
//        assertThat(mMasterListAdapter.onCreateViewHolder(new LinearLayout(ApplicationProvider.getApplicationContext()), mMasterListAdapter.getItemViewType(0)), instanceOf(DetailItemViewViewHolder.class));
//    }
//
//    @Test
//    public void onCreateViewHolder_localSettingListItem_returnsCorrectViewHolder() {
//        mListItems.add(new LocalSettingListItem("key", "Title", "SubTitle"));
//
//        assertThat(mMasterListAdapter.onCreateViewHolder(new LinearLayout(ApplicationProvider.getApplicationContext()), mMasterListAdapter.getItemViewType(0)), instanceOf(DetailItemViewViewHolder.class));
//    }
//
//    @Test
//    public void onCreateViewHolder_serviceSettingsListItem_returnsCorrectViewHolder() {
//        mListItems.add(new ServiceSettingsListItem(new ServiceSettings("Type", "URI"), "Title", "SubTitle"));
//
//        assertThat(mMasterListAdapter.onCreateViewHolder(new LinearLayout(ApplicationProvider.getApplicationContext()), mMasterListAdapter.getItemViewType(0)), instanceOf(DetailItemViewViewHolder.class));
//    }
//
//    @Test
//    public void onCreateViewHolder_simultaneousRingLocationListItem_returnsCorrectViewHolder() {
//        mListItems.add(new SimultaneousRingLocationListItem(new SimultaneousRingLocation(), "Title", "SubTitle"));
//
//        assertThat(mMasterListAdapter.onCreateViewHolder(new LinearLayout(ApplicationProvider.getApplicationContext()), mMasterListAdapter.getItemViewType(0)), instanceOf(DetailItemViewSimultaneousRingLocationViewHolder.class));
//    }
//
//    @Test
//    public void onCreateViewHolder_headerListItem_returnsCorrectViewHolder() {
//        mListItems.add(new HeaderListItem(new ListHeaderRow(), new ArrayList<>(), true, false));
//
//        assertThat(mMasterListAdapter.onCreateViewHolder(new LinearLayout(ApplicationProvider.getApplicationContext()), mMasterListAdapter.getItemViewType(0)), instanceOf(HeaderViewHolder.class));
//    }
//
//    @Test
//    public void onCreateViewHolder_chatConversationListItem_returnsCorrectViewHolder() {
//        mListItems.add(new ChatConversationListItem(new ChatConversation("type"), new DbPresence(null, Enums.Contacts.PresenceStates.AVAILABLE, -10, null, Enums.Contacts.PresenceTypes.AVAILABLE)));
//
//        assertThat(mMasterListAdapter.onCreateViewHolder(new LinearLayout(ApplicationProvider.getApplicationContext()), mMasterListAdapter.getItemViewType(0)), instanceOf(ChatConversationViewHolder.class));
//    }
//
//    @Test
//    public void onCreateViewHolder_chatHeaderListItem_returnsCorrectViewHolder() {
//        mListItems.add(new ChatHeaderListItem(""));
//
//        assertThat(mMasterListAdapter.onCreateViewHolder(new LinearLayout(ApplicationProvider.getApplicationContext()), mMasterListAdapter.getItemViewType(0)), instanceOf(ChatHeaderViewHolder.class));
//    }
//
//    @Test
//    public void onCreateViewHolder_chatMessageSentListItem_returnsCorrectViewHolder() {
//        mListItems.add(new ChatMessageListItem(new ChatMessage(null, null, null, null, null, true, false, null, null, null, null, null, null, null, null), "Today", new byte[] {2}));
//
//        assertThat(mMasterListAdapter.onCreateViewHolder(new LinearLayout(ApplicationProvider.getApplicationContext()), mMasterListAdapter.getItemViewType(0)), instanceOf(ChatMessageSentViewHolder.class));
//    }
//
//    @Test
//    public void onCreateViewHolder_chatMessageReceivedListItem_returnsCorrectViewHolder() {
//        mListItems.add(new ChatMessageListItem(new ChatMessage(null, null, null, null, null, false, false, null, null, null, null, null, null, null, null), "Today", new byte[] {2}));
//
//        assertThat(mMasterListAdapter.onCreateViewHolder(new LinearLayout(ApplicationProvider.getApplicationContext()), mMasterListAdapter.getItemViewType(0)), instanceOf(ChatMessageReceivedViewHolder.class));
//    }
//
//    @Test
//    public void onCreateViewHolder_conferencePhoneNumberListItem_returnsCorrectViewHolder() {
//        mListItems.add(new ConferencePhoneNumberListItem(1, "Title", null, null, null, null));
//
//        assertThat(mMasterListAdapter.onCreateViewHolder(new LinearLayout(ApplicationProvider.getApplicationContext()), mMasterListAdapter.getItemViewType(0)), instanceOf(ConferencePhoneNumberViewHolder.class));
//    }
//
//    @Test
//    public void onCreateViewHolder_serviceSettingListItem_returnsCorrectViewHolder() {
//        ServiceSettings serviceSettings = new ServiceSettings(Enums.Service.TYPE_REMOTE_OFFICE, "uri");
//        mListItems.add(new ServiceSettingsListItem(serviceSettings, "Title", "SubTitle"));
//
//        assertThat(mMasterListAdapter.onCreateViewHolder(new LinearLayout(ApplicationProvider.getApplicationContext()), mMasterListAdapter.getItemViewType(0)), instanceOf(DetailItemViewServiceSettingsViewHolder.class));
//    }
//
//    @Test
//    public void onCreateViewHolder_callDetailDatetimeListItem_returnsCorrectViewHolder() {
//        mListItems.add(new CallDetailDatetimeListItem(Enums.Calls.CallTypes.MISSED, "SubTitle"));
//
//        assertThat(mMasterListAdapter.onCreateViewHolder(new LinearLayout(ApplicationProvider.getApplicationContext()), mMasterListAdapter.getItemViewType(0)), instanceOf(CallDetailDatetimeViewHolder.class));
//    }
//
//    @Test
//    public void onCreateViewHolder_callDetailPhoneNumberListItem_returnsCorrectViewHolder() {
//        mListItems.add(new CallDetailPhoneNumberListItem("Title", "SubTitle", R.drawable.ic_phone, R.drawable.ic_video));
//
//        assertThat(mMasterListAdapter.onCreateViewHolder(new LinearLayout(ApplicationProvider.getApplicationContext()), mMasterListAdapter.getItemViewType(0)), instanceOf(CallDetailPhoneNumberViewHolder.class));
//    }
//
//    @Test
//    public void onCreateViewHolder_callDetailListItem_returnsCorrectViewHolder() {
//        mListItems.add(new CallDetailListItem(Enums.Calls.DetailViewTypes.PHONE_NUMBER, "Title", "SubTitle", R.drawable.ic_phone, R.drawable.ic_video, true));
//
//        assertThat(mMasterListAdapter.onCreateViewHolder(new LinearLayout(ApplicationProvider.getApplicationContext()), mMasterListAdapter.getItemViewType(0)), instanceOf(DetailItemViewViewHolder.class));
//    }
//
//    @Test
//    public void onCreateViewHolder_loadingListItem_returnsCorrectViewHolder() {
//        mListItems.add(new LoadingListItem());
//
//        assertThat(mMasterListAdapter.onCreateViewHolder(new LinearLayout(ApplicationProvider.getApplicationContext()), mMasterListAdapter.getItemViewType(0)), instanceOf(LoadingViewHolder.class));
//    }
//
//    @Test
//    public void onCreateViewHolder_unknownListItem_returnsCorrectViewHolder() {
//        mListItems.add(new BaseListItem() {
//        });
//
//        assertThat(mMasterListAdapter.onCreateViewHolder(new LinearLayout(ApplicationProvider.getApplicationContext()), mMasterListAdapter.getItemViewType(0)), instanceOf(RecyclerView.ViewHolder.class));
//    }

//    @Test
//    public void onBindViewHolder_contactViewHolder_callsToBindViewHolder() {
//        ContactListItem listItem = new ContactListItem(new NextivaContact("1"), "", true);
//        mListItems.add(listItem);
//
//        ContactViewHolder viewHolder = spy(new ContactViewHolder(new FrameLayout(ApplicationProvider.getApplicationContext()), ApplicationProvider.getApplicationContext(), null));
//
//        mMasterListAdapter.onBindViewHolder(viewHolder, 0);
//
//        verify(viewHolder).bind(listItem);
//    }
//
//    @Test
//    public void onBindViewHolder_callHistoryViewHolder_callsToBindViewHolder() {
//        CallHistoryListItem listItem = new CallHistoryListItem(new CallLogEntry(null, null, null, null, null, null, null, null, Enums.Contacts.PresenceStates.NONE, -128, "", 0), true, true, null);
//        mListItems.add(listItem);
//
//        CallHistoryViewHolder viewHolder = spy(new CallHistoryViewHolder(new FrameLayout(ApplicationProvider.getApplicationContext()), ApplicationProvider.getApplicationContext(), mMockMasterListListener));
//
//        mMasterListAdapter.onBindViewHolder(viewHolder, 0);
//
//        verify(viewHolder).bind(listItem);
//    }
//
//    @Test
//    public void onBindViewHolder_nextivaAnywhereLocationViewHolder_callsToBindViewHolder() {
//        NextivaAnywhereLocationListItem listItem = new NextivaAnywhereLocationListItem(new NextivaAnywhereLocation("1", null, null, null, null, null), "Title", "SubTitle");
//        mListItems.add(listItem);
//
//        NextivaAnywhereLocationViewHolder viewHolder = spy(new NextivaAnywhereLocationViewHolder(new FrameLayout(ApplicationProvider.getApplicationContext()), ApplicationProvider.getApplicationContext(), mMockMasterListListener));
//
//        mMasterListAdapter.onBindViewHolder(viewHolder, 0);
//
//        verify(viewHolder).bind(listItem);
//    }
//
//    @Test
//    public void onBindViewHolder_contactDetailViewHolder_callsToBindViewHolder() {
//        ContactDetailListItem listItem = new ContactDetailListItem(1, "Title", "SubTitle", 0, 0, true);
//        mListItems.add(listItem);
//
//        DetailItemViewViewHolder viewHolder = spy(new DetailItemViewViewHolder(new FrameLayout(ApplicationProvider.getApplicationContext()), ApplicationProvider.getApplicationContext(), null));
//
//        mMasterListAdapter.onBindViewHolder(viewHolder, 0);
//
//        verify(viewHolder).bind(listItem);
//    }
//
//    @Test
//    public void onBindViewHolder_localSettingViewHolder_callsToBindViewHolder() {
//        LocalSettingListItem listItem = new LocalSettingListItem("key", "Title", "SubTitle");
//        mListItems.add(listItem);
//
//        DetailItemViewViewHolder viewHolder = spy(new DetailItemViewViewHolder(new FrameLayout(ApplicationProvider.getApplicationContext()), ApplicationProvider.getApplicationContext(), null));
//
//        mMasterListAdapter.onBindViewHolder(viewHolder, 0);
//
//        verify(viewHolder).bind(listItem);
//    }
//
//    @Test
//    public void onBindViewHolder_serviceSettingsViewHolder_callsToBindViewHolder() {
//        ServiceSettingsListItem listItem = new ServiceSettingsListItem(new ServiceSettings("Type", "URI"), "Title", "SubTitle");
//        mListItems.add(listItem);
//
//        DetailItemViewViewHolder viewHolder = spy(new DetailItemViewViewHolder(new FrameLayout(ApplicationProvider.getApplicationContext()), ApplicationProvider.getApplicationContext(), null));
//
//        mMasterListAdapter.onBindViewHolder(viewHolder, 0);
//
//        verify(viewHolder).bind(listItem);
//    }
//
//    @Test
//    public void onBindViewHolder_simultaneousRingLocationViewHolder_callsToBindViewHolder() {
//        SimultaneousRingLocationListItem listItem = new SimultaneousRingLocationListItem(new SimultaneousRingLocation(), "Title", "SubTitle");
//        mListItems.add(listItem);
//
//        DetailItemViewSimultaneousRingLocationViewHolder viewHolder = spy(new DetailItemViewSimultaneousRingLocationViewHolder(new FrameLayout(ApplicationProvider.getApplicationContext()), ApplicationProvider.getApplicationContext(), null));
//
//        mMasterListAdapter.onBindViewHolder(viewHolder, 0);
//
//        verify(viewHolder).bind(listItem);
//    }
//
//    @Test
//    public void onBindViewHolder_headerViewHolder_callsToBindViewHolder() {
//        HeaderListItem listItem = new HeaderListItem(new ListHeaderRow(), new ArrayList<>(), true, false);
//        mListItems.add(listItem);
//
//        HeaderViewHolder viewHolder = spy(new HeaderViewHolder(new FrameLayout(ApplicationProvider.getApplicationContext()), ApplicationProvider.getApplicationContext(), null));
//
//        mMasterListAdapter.onBindViewHolder(viewHolder, 0);
//
//        verify(viewHolder).bind(listItem);
//    }
//
//    @Test
//    public void onBindViewHolder_chatConversationViewHolder_callsToBindViewHolder() {
//        ChatConversationListItem listItem = new ChatConversationListItem(new ChatConversation("type"), new DbPresence(null, Enums.Contacts.PresenceStates.AVAILABLE, -10, null, Enums.Contacts.PresenceTypes.AVAILABLE));
//        mListItems.add(listItem);
//
//        ChatConversationViewHolder viewHolder = spy(new ChatConversationViewHolder(new FrameLayout(ApplicationProvider.getApplicationContext()), ApplicationProvider.getApplicationContext(), null));
//
//        mMasterListAdapter.onBindViewHolder(viewHolder, 0);
//
//        verify(viewHolder).bind(listItem);
//    }
//
//    @Test
//    public void onBindViewHolder_chatHeaderViewHolder_callsToBindViewHolder() {
//        ChatHeaderListItem listItem = new ChatHeaderListItem("");
//        mListItems.add(listItem);
//
//        ChatHeaderViewHolder viewHolder = spy(new ChatHeaderViewHolder(new FrameLayout(ApplicationProvider.getApplicationContext()), ApplicationProvider.getApplicationContext(), null));
//
//        mMasterListAdapter.onBindViewHolder(viewHolder, 0);
//
//        verify(viewHolder).bind(listItem);
//    }
//
//    @Test
//    public void onBindViewHolder_chatMessageSentViewHolder_callsToBindViewHolder() {
//        ChatMessageListItem listItem = new ChatMessageListItem(new ChatMessage(null, null, null, null, null, true, false, null, null, null, null, null, null, null, null), "Today", new byte[] {2});
//        mListItems.add(listItem);
//
//        ChatMessageSentViewHolder viewHolder = spy(new ChatMessageSentViewHolder(new FrameLayout(ApplicationProvider.getApplicationContext()), ApplicationProvider.getApplicationContext(), null));
//
//        mMasterListAdapter.onBindViewHolder(viewHolder, 0);
//
//        verify(viewHolder).bind(listItem);
//    }
//
//    @Test
//    public void onBindViewHolder_chatMessageReceivedViewHolder_callsToBindViewHolder() {
//        ChatMessageListItem listItem = new ChatMessageListItem(new ChatMessage(null, null, null, null, null, false, false, null, null, null, null, null, null, null, null), "Today", new byte[] {2});
//        mListItems.add(listItem);
//
//        ChatMessageReceivedViewHolder viewHolder = spy(new ChatMessageReceivedViewHolder(new FrameLayout(ApplicationProvider.getApplicationContext()), ApplicationProvider.getApplicationContext(), null));
//
//        mMasterListAdapter.onBindViewHolder(viewHolder, 0);
//
//        verify(viewHolder).bind(listItem);
//    }
//
//    @Test
//    public void onBindViewHolder_conferencePhoneNumberViewHolder_callsToBindViewHolder() {
//        ConferencePhoneNumberListItem listItem = new ConferencePhoneNumberListItem(1, "Title", null, null, null, null);
//        mListItems.add(listItem);
//
//        ConferencePhoneNumberViewHolder viewHolder = spy(new ConferencePhoneNumberViewHolder(new FrameLayout(ApplicationProvider.getApplicationContext()), ApplicationProvider.getApplicationContext(), null));
//
//        mMasterListAdapter.onBindViewHolder(viewHolder, 0);
//
//        verify(viewHolder).bind(listItem);
//    }
//
//    @Test
//    public void onBindViewHolder_detailItemViewServiceSettingsViewHolder_callsToBindViewHolder() {
//        ServiceSettings serviceSettings = new ServiceSettings(Enums.Service.TYPE_REMOTE_OFFICE, "uri");
//        ServiceSettingsListItem listItem = new ServiceSettingsListItem(serviceSettings, "Title", "SubTitle");
//        mListItems.add(listItem);
//
//        DetailItemViewServiceSettingsViewHolder viewHolder = spy(new DetailItemViewServiceSettingsViewHolder(new FrameLayout(ApplicationProvider.getApplicationContext()), ApplicationProvider.getApplicationContext(), null));
//
//        mMasterListAdapter.onBindViewHolder(viewHolder, 0);
//
//        verify(viewHolder).bind(listItem);
//    }
//
//    @Test
//    public void onBindViewHolder_callDetailDatetimeViewHolder_callsToBindViewHolder() {
//        CallDetailDatetimeListItem listItem = new CallDetailDatetimeListItem(Enums.Calls.CallTypes.MISSED, "SubTitle");
//        mListItems.add(listItem);
//
//        CallDetailDatetimeViewHolder viewHolder = spy(new CallDetailDatetimeViewHolder(new FrameLayout(ApplicationProvider.getApplicationContext()), ApplicationProvider.getApplicationContext(), null));
//
//        mMasterListAdapter.onBindViewHolder(viewHolder, 0);
//
//        verify(viewHolder).bind(listItem);
//    }
//
//    @Test
//    public void onBindViewHolder_callDetailPhoneNumberViewHolder_callsToBindViewHolder() {
//        CallDetailPhoneNumberListItem listItem = new CallDetailPhoneNumberListItem("Title", "SubTitle", R.drawable.ic_phone, R.drawable.ic_video);
//        mListItems.add(listItem);
//
//        CallDetailPhoneNumberViewHolder viewHolder = spy(new CallDetailPhoneNumberViewHolder(new FrameLayout(ApplicationProvider.getApplicationContext()), ApplicationProvider.getApplicationContext(), null));
//
//        mMasterListAdapter.onBindViewHolder(viewHolder, 0);
//
//        verify(viewHolder).bind(listItem);
//    }
//
//    @Test
//    public void getItemId_returnsPosition() {
//        assertEquals(1234, mMasterListAdapter.getItemId(1234));
//    }
//
//    @Test
//    public void getItemCount_returnsListItemsCount() {
//        assertEquals(0, mMasterListAdapter.getItemCount());
//
//        mListItems.add(new LoadingListItem());
//        assertEquals(1, mMasterListAdapter.getItemCount());
//
//        mListItems.clear();
//        assertEquals(0, mMasterListAdapter.getItemCount());
//    }

    @Test
    public void updateList_replacesListItems() {
        HeaderListItem listItem1 = new HeaderListItem(new ListHeaderRow("Title1"), new ArrayList<>(), true, false);
        HeaderListItem listItem2 = new HeaderListItem(new ListHeaderRow("Title2"), new ArrayList<>(), true, false);
        HeaderListItem listItem3 = new HeaderListItem(new ListHeaderRow("Title3"), new ArrayList<>(), true, false);
        HeaderListItem listItem4 = new HeaderListItem(new ListHeaderRow("Title4"), new ArrayList<>(), true, false);

        ArrayList<BaseListItem> updatedListItems = new ArrayList<>();
        updatedListItems.add(listItem3);
        updatedListItems.add(listItem4);
        updatedListItems.add(listItem1);

        mListItems.add(listItem1);
        mListItems.add(listItem2);

        mMasterListAdapter.updateList(updatedListItems);

        assertEquals(listItem3, mListItems.get(0));
        assertEquals(listItem4, mListItems.get(1));
        assertEquals(listItem1, mListItems.get(2));
    }

//    @Test
//    public void clearList_clearsArrayList() {
//        mListItems.add(new LoadingListItem());
//
//        mMasterListAdapter.clearList();
//
//        assertEquals(0, mListItems.size());
//    }
//
//    @Test
//    public void addItem_notPreviouslyAdded_addsItem() {
//        assertEquals(0, mListItems.size());
//
//        LoadingListItem listItem = new LoadingListItem();
//
//        mMasterListAdapter.addItem(listItem);
//
//        assertEquals(listItem, mListItems.get(0));
//        assertEquals(1, mListItems.size());
//    }
//
//    @Test
//    public void addItem_previouslyAdded_doesNotAddItem() {
//        assertEquals(0, mListItems.size());
//
//        LoadingListItem listItem = new LoadingListItem();
//
//        mMasterListAdapter.addItem(listItem);
//        mMasterListAdapter.addItem(listItem);
//
//        assertEquals(listItem, mListItems.get(0));
//        assertEquals(1, mListItems.size());
//    }
//
//    @Test
//    public void removeItem_previouslyAdded_removesItem() {
//        LoadingListItem listItem = new LoadingListItem();
//        mListItems.add(listItem);
//
//        mMasterListAdapter.removeItem(listItem);
//
//        assertEquals(0, mListItems.size());
//    }
//
//    @Test
//    public void removeItem_notPreviouslyAdded_doesNotRemoveItem() {
//        LoadingListItem listItem = new LoadingListItem();
//        mListItems.add(listItem);
//
//        mMasterListAdapter.removeItem(new LoadingListItem());
//
//        assertEquals(listItem, mListItems.get(0));
//        assertEquals(1, mListItems.size());
//    }
//
//    @Test
//    public void addLoadingListItem_notAdded_addsToEndOfList() {
//        mMasterListAdapter.addLoadingListItem();
//
//        assertEquals(1, mListItems.size());
//        assertThat(mListItems.get(0), instanceOf(LoadingListItem.class));
//    }
//
//    @Test
//    public void addLoadingListItem_alreadyAdded_movesToEndOfList() {
//        mMasterListAdapter.addLoadingListItem();
//        mListItems.add(new HeaderListItem(new ListHeaderRow("Title"), new ArrayList<>(), true, false));
//
//        assertEquals(2, mListItems.size());
//
//        assertThat(mListItems.get(0), instanceOf(LoadingListItem.class));
//
//        mMasterListAdapter.addLoadingListItem();
//        assertThat(mListItems.get(1), instanceOf(LoadingListItem.class));
//    }
//
//    @Test
//    public void removeLoadingListItem_alreadyAdded_removesFromList() {
//        mMasterListAdapter.addLoadingListItem();
//        assertEquals(1, mListItems.size());
//
//        mMasterListAdapter.removeLoadingListItem();
//        assertEquals(0, mListItems.size());
//    }
//
//    @Test
//    public void containsLoadingListItem_containsListItem_returnsTrue() {
//        mMasterListAdapter.addLoadingListItem();
//
//        assertEquals(1, mListItems.size());
//        assertTrue(mMasterListAdapter.containsLoadingListItem());
//    }
//
//    @Test
//    public void containsLoadingListItem_doesNotContainListList_returnsFalse() {
//        assertEquals(0, mListItems.size());
//        assertFalse(mMasterListAdapter.containsLoadingListItem());
//    }
}
