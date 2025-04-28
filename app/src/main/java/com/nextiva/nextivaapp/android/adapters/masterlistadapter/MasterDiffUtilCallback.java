/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.adapters.masterlistadapter;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.CallHistoryListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ChatConversationListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ChatHeaderListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ChatMessageListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactCategoryListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactDetailFooterListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactDetailHeaderListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactDetailListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactDetailShowMoreListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactDetailTeamsListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactHeaderListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectHomeHeaderListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectHomeListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ContactDetailListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ContactListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.DatabaseCountUtilityListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.DialogContactActionDetailListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.DialogContactActionHeaderListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.DialogContactActionListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.HeaderListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.LocalSettingListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.MeetingListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.MessageHeaderListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.MessageListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ServiceSettingsListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.SmsMessageListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.VoicemailListItem;
import com.nextiva.nextivaapp.android.db.model.DbAttachment;
import com.nextiva.nextivaapp.android.meetings.MeetingUtil;
import com.nextiva.nextivaapp.android.models.ChatMessage;
import com.nextiva.nextivaapp.android.models.SmsMessage;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Created by adammacdonald on 2/16/18.
 */

class MasterDiffUtilCallback extends DiffUtil.Callback {

    private final List<BaseListItem> mOldList;
    private final List<BaseListItem> mNewList;

    MasterDiffUtilCallback(@NonNull List<BaseListItem> oldList, @NonNull List<BaseListItem> newList) {
        mOldList = oldList;
        mNewList = newList;
    }

    @Override
    public int getOldListSize() {
        return mOldList.size();
    }

    @Override
    public int getNewListSize() {
        return mNewList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        BaseListItem oldItem = mOldList.get(oldItemPosition);
        BaseListItem newItem = mNewList.get(newItemPosition);

        if (oldItem instanceof CallHistoryListItem && newItem instanceof CallHistoryListItem) {
            return TextUtils.equals(((CallHistoryListItem) oldItem).getData().getCallLogId(),
                    ((CallHistoryListItem) newItem).getData().getCallLogId());

        } else if (oldItem instanceof ContactListItem && newItem instanceof ContactListItem) {
            return TextUtils.equals(((ContactListItem) oldItem).getData().getUserId(),
                    ((ContactListItem) newItem).getData().getUserId());

        } else if (oldItem instanceof ContactDetailListItem && newItem instanceof ContactDetailListItem) {
            return ((ContactDetailListItem) oldItem).getViewType() == ((ContactDetailListItem) newItem).getViewType()
                    && TextUtils.equals(((ContactDetailListItem) oldItem).getTitle(),
                    ((ContactDetailListItem) newItem).getTitle())
                    && TextUtils.equals(((ContactDetailListItem) oldItem).getSubTitle(),
                    ((ContactDetailListItem) newItem).getSubTitle());

        } else if (oldItem instanceof HeaderListItem && newItem instanceof HeaderListItem) {
            return true;

        } else if (oldItem instanceof MessageListItem && newItem instanceof MessageListItem) {
            try {

                return (TextUtils.equals(((MessageListItem) oldItem).getData().getMessageId(), ((MessageListItem) newItem).getData().getMessageId()));

            } catch (NumberFormatException e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                return false;
            }
        } else if (oldItem instanceof ServiceSettingsListItem && newItem instanceof ServiceSettingsListItem) {
            return TextUtils.equals(((ServiceSettingsListItem) oldItem).getServiceSettings().getType(),
                    ((ServiceSettingsListItem) newItem).getServiceSettings().getType());

        } else if (oldItem instanceof LocalSettingListItem && newItem instanceof LocalSettingListItem) {
            return TextUtils.equals(((LocalSettingListItem) oldItem).getSettingKey(),
                    ((LocalSettingListItem) newItem).getSettingKey());

        } else if (oldItem instanceof ChatMessageListItem && newItem instanceof ChatMessageListItem) {
            try {
                Long oldTimestamp = ((ChatMessageListItem) oldItem).getData().getTimestamp();
                Long newTimestamp = ((ChatMessageListItem) newItem).getData().getTimestamp();

                return oldTimestamp.equals(newTimestamp) && TextUtils.equals(((ChatMessageListItem) oldItem).getData().getBody(), ((ChatMessageListItem) newItem).getData().getBody());

            } catch (NumberFormatException e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                return false;
            }

        } else if (oldItem instanceof SmsMessageListItem && newItem instanceof SmsMessageListItem) {
            try {

                Long oldTimestamp = Objects.requireNonNull(Objects.requireNonNull(((SmsMessageListItem) oldItem).getData()).getSent()).toEpochMilli();
                Long newTimestamp = Objects.requireNonNull(Objects.requireNonNull(((SmsMessageListItem) newItem).getData()).getSent()).toEpochMilli();

                return oldTimestamp.equals(newTimestamp) && TextUtils.equals(Objects.requireNonNull(((SmsMessageListItem) oldItem).getData()).getBody(), Objects.requireNonNull(((SmsMessageListItem) newItem).getData()).getBody()) &&
                        TextUtils.equals(Objects.requireNonNull(((SmsMessageListItem) oldItem).getData()).getGroupValue(), Objects.requireNonNull(((SmsMessageListItem) newItem).getData()).getGroupValue());

            } catch (NumberFormatException e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                return false;
            }

        } else if (oldItem instanceof ChatHeaderListItem && newItem instanceof ChatHeaderListItem) {
            return TextUtils.equals(((ChatHeaderListItem) oldItem).getData(), ((ChatHeaderListItem) newItem).getData());

        } else if (oldItem instanceof MessageHeaderListItem && newItem instanceof MessageHeaderListItem) {
            return TextUtils.equals(((MessageHeaderListItem) oldItem).getData(), ((MessageHeaderListItem) newItem).getData());

        } else if (oldItem instanceof ChatConversationListItem && newItem instanceof ChatConversationListItem) {
            try {
                Long oldTimestamp = ((ChatConversationListItem) oldItem).getData().getLastMessageTimestamp();
                Long newTimestamp = ((ChatConversationListItem) newItem).getData().getLastMessageTimestamp();

                return oldTimestamp.equals(newTimestamp) &&
                        TextUtils.equals(((ChatConversationListItem) oldItem).getData().getLastMessageBody(), ((ChatConversationListItem) newItem).getData().getLastMessageBody()) &&
                        TextUtils.equals(((ChatConversationListItem) oldItem).getData().getChatWith(), ((ChatConversationListItem) newItem).getData().getChatWith()) &&
                        ((ChatConversationListItem) oldItem).getData().getUnreadCount() == ((ChatConversationListItem) newItem).getData().getUnreadCount();

            } catch (NumberFormatException e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                return false;
            }
        } else if (oldItem instanceof VoicemailListItem && newItem instanceof VoicemailListItem) {
            return TextUtils.equals(((VoicemailListItem) oldItem).getData().getMessageId(), ((VoicemailListItem) newItem).getData().getMessageId());

        } else if (oldItem instanceof ConnectContactHeaderListItem && newItem instanceof ConnectContactHeaderListItem) {
            return TextUtils.equals(((ConnectContactHeaderListItem) oldItem).getItemType(), ((ConnectContactHeaderListItem) newItem).getItemType());

        } else if (oldItem instanceof ConnectContactDetailHeaderListItem && newItem instanceof ConnectContactDetailHeaderListItem) {
            return TextUtils.equals(((ConnectContactDetailHeaderListItem) oldItem).getItemType(), ((ConnectContactDetailHeaderListItem) newItem).getItemType());

        } else if (oldItem instanceof ConnectContactDetailListItem && newItem instanceof ConnectContactDetailListItem) {
            return TextUtils.equals(((ConnectContactDetailListItem) oldItem).getTitle(), ((ConnectContactDetailListItem) newItem).getTitle());

        } else if (oldItem instanceof ConnectContactDetailFooterListItem && newItem instanceof ConnectContactDetailFooterListItem) {
            return TextUtils.equals(((ConnectContactDetailFooterListItem) oldItem).getData().getUserId(), ((ConnectContactDetailFooterListItem) newItem).getData().getUserId());

        } else if (oldItem instanceof ConnectContactDetailTeamsListItem && newItem instanceof ConnectContactDetailTeamsListItem) {
            return ((ConnectContactDetailTeamsListItem) oldItem).getTeamsList().equals(((ConnectContactDetailTeamsListItem) newItem).getTeamsList());

        } else if (oldItem instanceof ConnectContactDetailShowMoreListItem && newItem instanceof ConnectContactDetailShowMoreListItem) {
            return true;

        } else if (oldItem instanceof ConnectHomeHeaderListItem && newItem instanceof ConnectHomeHeaderListItem) {
            return TextUtils.equals(((ConnectHomeHeaderListItem) oldItem).getTitle(), ((ConnectHomeHeaderListItem) newItem).getTitle());

        } else if (oldItem instanceof ConnectHomeListItem && newItem instanceof ConnectHomeListItem) {
            return ((ConnectHomeListItem) oldItem).getChannel() == ((ConnectHomeListItem) newItem).getChannel();

        } else if (oldItem instanceof DatabaseCountUtilityListItem && newItem instanceof DatabaseCountUtilityListItem) {
            return TextUtils.equals(((DatabaseCountUtilityListItem) oldItem).getTitle(), ((DatabaseCountUtilityListItem) newItem).getTitle());

        } else if (oldItem instanceof DialogContactActionListItem && newItem instanceof DialogContactActionListItem) {
            return TextUtils.equals(((DialogContactActionListItem) oldItem).getTitle(), ((DialogContactActionListItem) newItem).getTitle());

        } else if (oldItem instanceof DialogContactActionHeaderListItem && newItem instanceof DialogContactActionHeaderListItem) {
            return TextUtils.equals(((DialogContactActionHeaderListItem) oldItem).getContact().getUiName(), ((DialogContactActionHeaderListItem) newItem).getContact().getUiName());

        } else if (oldItem instanceof DialogContactActionDetailListItem && newItem instanceof DialogContactActionDetailListItem) {
            return TextUtils.equals(((DialogContactActionDetailListItem) oldItem).getTitle(), ((DialogContactActionDetailListItem) newItem).getTitle());

        } else if (oldItem instanceof MeetingListItem && newItem instanceof MeetingListItem) {
            return TextUtils.equals(((MeetingListItem) oldItem).getTitle(), ((MeetingListItem) newItem).getTitle()) &&
                    TextUtils.equals(((MeetingListItem) oldItem).getStartTime(), ((MeetingListItem) newItem).getStartTime());

        } else if (oldItem instanceof ConnectContactCategoryListItem && newItem instanceof ConnectContactCategoryListItem) {
            return oldItem == newItem;
        }

        return false;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        BaseListItem oldItem = mOldList.get(oldItemPosition);
        BaseListItem newItem = mNewList.get(newItemPosition);

        if (oldItem instanceof CallHistoryListItem && newItem instanceof CallHistoryListItem) {
            CallHistoryListItem oldCallHistory = (CallHistoryListItem) oldItem;
            CallHistoryListItem newCallHistory = (CallHistoryListItem) newItem;

            return TextUtils.equals(oldCallHistory.getData().getCallLogId(), newCallHistory.getData().getCallLogId()) &&
                    TextUtils.equals(oldCallHistory.getSearchTerm(), newCallHistory.getSearchTerm()) &&
                    TextUtils.equals(oldCallHistory.getData().getUiName(), newCallHistory.getData().getUiName()) &&
                    oldCallHistory.getData().getPresenceState() == newCallHistory.getData().getPresenceState() &&
                    Arrays.equals(oldCallHistory.getData().getAvatar(), newCallHistory.getData().getAvatar()) &&
                    oldCallHistory.getData().getIsRead() == newCallHistory.getData().getIsRead();

        } else if (oldItem instanceof ContactListItem && newItem instanceof ContactListItem) {
            return ((ContactListItem) oldItem).getData().equals(((ContactListItem) newItem).getData()) &&
                    TextUtils.equals(((ContactListItem) oldItem).getSearchTerm(), ((ContactListItem) newItem).getSearchTerm());

        } else if (oldItem instanceof ContactDetailListItem && newItem instanceof ContactDetailListItem) {
            return ((ContactDetailListItem) oldItem).getViewType() == ((ContactDetailListItem) newItem).getViewType()
                    && TextUtils.equals(((ContactDetailListItem) oldItem).getTitle(),
                    ((ContactDetailListItem) newItem).getTitle())
                    && TextUtils.equals(((ContactDetailListItem) oldItem).getSubTitle(),
                    ((ContactDetailListItem) newItem).getSubTitle());

        } else if (oldItem instanceof HeaderListItem && newItem instanceof HeaderListItem) {
            return false;

        } else if (oldItem instanceof ServiceSettingsListItem && newItem instanceof ServiceSettingsListItem) {
            return false;

        } else if (oldItem instanceof LocalSettingListItem && newItem instanceof LocalSettingListItem) {
            return false;

        } else if (oldItem instanceof ChatMessageListItem && newItem instanceof ChatMessageListItem) {
            try {
                ChatMessage oldChatMessage = ((ChatMessageListItem) oldItem).getData();
                ChatMessage newChatMessage = ((ChatMessageListItem) newItem).getData();

                return TextUtils.equals(oldChatMessage.getBody(), newChatMessage.getBody()) &&
                        TextUtils.equals(oldChatMessage.getUIName(), newChatMessage.getUIName()) &&
                        oldChatMessage.getTimestamp().equals(newChatMessage.getTimestamp()) &&
                        oldChatMessage.getSentStatus().equals(newChatMessage.getSentStatus()) &&
                        oldChatMessage.getPresenceState().equals(newChatMessage.getPresenceState()) &&
                        oldChatMessage.getAvatar() == newChatMessage.getAvatar();

            } catch (NumberFormatException e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                return false;
            }

        } else if (oldItem instanceof SmsMessageListItem && newItem instanceof SmsMessageListItem) {
            try {
                SmsMessage oldSmsMessage = ((SmsMessageListItem) oldItem).getData();
                SmsMessage newSmsMessage = ((SmsMessageListItem) newItem).getData();
                if (oldSmsMessage == null || newSmsMessage == null) {
                    return false;
                }

                Long oldTimestamp = Objects.requireNonNull(oldSmsMessage.getSent()).toEpochMilli();
                Long newTimestamp = Objects.requireNonNull(newSmsMessage.getSent()).toEpochMilli();

                Integer oldSentStatus = oldSmsMessage.getSentStatus();
                Integer newSentStatus = newSmsMessage.getSentStatus();

                int oldItemAttachmentCount = 0;
                if (oldSmsMessage.getAttachments() != null) {
                    oldItemAttachmentCount = oldSmsMessage.getAttachments().size();
                }
                int newItemAttachmentCount = 0;
                if (newSmsMessage.getAttachments() != null) {
                    newItemAttachmentCount = newSmsMessage.getAttachments().size();
                }

                boolean attachmentsSame = (oldItemAttachmentCount == newItemAttachmentCount);
                if (attachmentsSame) {
                    for (int index = 0; index < oldItemAttachmentCount; index++) {
                        DbAttachment oldItemAttachment = oldSmsMessage.getAttachments().get(index);
                        DbAttachment newItemAttachment = newSmsMessage.getAttachments().get(index);

                        if (oldItemAttachment != null && newItemAttachment != null) {
                            Long oldFileDuration = oldItemAttachment.getFileDuration() != null ? oldItemAttachment.getFileDuration() : 0L;
                            Long newFileDuration = newItemAttachment.getFileDuration() != null ? newItemAttachment.getFileDuration() : 0L;

                            if (!TextUtils.equals(oldItemAttachment.getLink(), newItemAttachment.getLink()) ||
                                    !oldFileDuration.equals(newFileDuration) ||
                                    !Arrays.equals(newItemAttachment.getContentData(), oldItemAttachment.getContentData())) {
                                attachmentsSame = false;
                            }
                        }
                    }
                }

                return oldTimestamp.equals(newTimestamp) &&
                        oldSentStatus.equals(newSentStatus) &&
                        TextUtils.equals(oldSmsMessage.getBody(), newSmsMessage.getBody()) &&
                        TextUtils.equals(oldSmsMessage.getSenderUiName(), newSmsMessage.getSenderUiName()) &&
                        attachmentsSame;

            } catch (NumberFormatException e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                return false;
            }

        } else if (oldItem instanceof ChatHeaderListItem && newItem instanceof ChatHeaderListItem) {
            return TextUtils.equals(((ChatHeaderListItem) oldItem).getData(), ((ChatHeaderListItem) newItem).getData());

        } else if (oldItem instanceof MessageHeaderListItem && newItem instanceof MessageHeaderListItem) {
            return TextUtils.equals(((MessageHeaderListItem) oldItem).getData(), ((MessageHeaderListItem) newItem).getData());

        } else if (oldItem instanceof ChatConversationListItem && newItem instanceof ChatConversationListItem) {
            ChatConversationListItem oldConversation = (ChatConversationListItem) oldItem;
            ChatConversationListItem newConversation = (ChatConversationListItem) newItem;

            Integer oldPresence = null;
            Integer newPresence = null;

            if (oldConversation.getData() != null && oldConversation.getData().getChatMessagesList() != null &&
                    !oldConversation.getData().getChatMessagesList().isEmpty() && oldConversation.getData().getChatMessagesList().get(0).getPresenceState() != null) {
                oldPresence = oldConversation.getData().getChatMessagesList().get(0).getPresenceState();
            }

            if (newConversation.getData() != null && newConversation.getData().getChatMessagesList() != null &&
                    !newConversation.getData().getChatMessagesList().isEmpty() && newConversation.getData().getChatMessagesList().get(0).getPresenceState() != null) {
                newPresence = newConversation.getData().getChatMessagesList().get(0).getPresenceState();
            }

            return TextUtils.equals(oldConversation.getData().getChatWith(), newConversation.getData().getChatWith()) &&
                    TextUtils.equals(oldConversation.getDisplayName(), newConversation.getDisplayName()) &&
                    oldPresence.equals(newPresence) &&
                    Arrays.equals(oldConversation.getAvatarBytes(), newConversation.getAvatarBytes()) &&
                    oldConversation.getUnreadMessagesCount() == newConversation.getUnreadMessagesCount() &&
                    oldConversation.getData().getUnreadCount() == newConversation.getUnreadMessagesCount();

        } else if (oldItem instanceof VoicemailListItem && newItem instanceof VoicemailListItem) {
            VoicemailListItem oldVoicemail = (VoicemailListItem) oldItem;
            VoicemailListItem newVoicemail = (VoicemailListItem) newItem;

            return oldVoicemail.getData().getMessageId().equals(newVoicemail.getData().getMessageId()) &&
                    oldVoicemail.getData().isRead().equals(newVoicemail.getData().isRead()) &&
                    oldVoicemail.getData().getPresenceState() == newVoicemail.getData().getPresenceState() &&
                    oldVoicemail.getData().getDuration().equals(newVoicemail.getData().getDuration()) &&
                    Arrays.equals(oldVoicemail.getData().getAvatar(), newVoicemail.getData().getAvatar()) &&
                    TextUtils.equals(oldVoicemail.getData().getTranscription(), newVoicemail.getData().getTranscription()) &&
                    TextUtils.equals(oldVoicemail.getData().getRating(), newVoicemail.getData().getRating());

        } else if (oldItem instanceof MessageListItem && newItem instanceof MessageListItem) {
            SmsMessage oldMessage = ((MessageListItem) oldItem).getData();
            SmsMessage newMessage = ((MessageListItem) newItem).getData();

            return oldMessage.getMessageState() != null && newMessage.getMessageState() != null &&
                    oldMessage.getSender() != null && newMessage.getSender() != null &&
                    !oldMessage.getSender().isEmpty() && !newMessage.getSender().isEmpty() &&
                    TextUtils.equals(oldMessage.getMessageState().getReadStatus(), newMessage.getMessageState().getReadStatus()) &&
                    TextUtils.equals(oldMessage.getSender().get(0).getName(), newMessage.getSender().get(0).getName()) &&
                    TextUtils.equals(oldMessage.getSender().get(0).getPhoneNumber(), newMessage.getSender().get(0).getPhoneNumber()) &&
                    TextUtils.equals(oldMessage.getSenderUiName(), newMessage.getSenderUiName());

        } else if (oldItem instanceof ConnectContactDetailFooterListItem && newItem instanceof ConnectContactDetailFooterListItem) {
            ConnectContactDetailFooterListItem oldContact = (ConnectContactDetailFooterListItem) oldItem;
            ConnectContactDetailFooterListItem newContact = (ConnectContactDetailFooterListItem) newItem;

            return TextUtils.equals(oldContact.getData().getCreatedBy(), newContact.getData().getCreatedBy()) &&
                    TextUtils.equals(oldContact.getData().getLastModifiedBy(), newContact.getData().getLastModifiedBy()) &&
                    TextUtils.equals(oldContact.getData().getLastModifiedOn(), newContact.getData().getLastModifiedOn());
            
        } else if (oldItem instanceof ConnectContactDetailTeamsListItem && newItem instanceof ConnectContactDetailTeamsListItem) {
            return ((ConnectContactDetailTeamsListItem) oldItem).getTeamsList().equals(((ConnectContactDetailTeamsListItem) newItem).getTeamsList());

        } else if (oldItem instanceof ConnectHomeListItem && newItem instanceof ConnectHomeListItem) {
            if (((ConnectHomeListItem) oldItem).getCount() != null &&
                    ((ConnectHomeListItem) newItem).getCount() != null) {
                return ((ConnectHomeListItem) oldItem).getCount().equals(((ConnectHomeListItem) newItem).getCount());
            }

        } else if (oldItem instanceof ConnectContactDetailShowMoreListItem && newItem instanceof ConnectContactDetailShowMoreListItem) {
            return true;

        } else if (oldItem instanceof DatabaseCountUtilityListItem && newItem instanceof DatabaseCountUtilityListItem) {
            return ((DatabaseCountUtilityListItem) oldItem).getCount() == ((DatabaseCountUtilityListItem) newItem).getCount();

        } else if (oldItem instanceof DialogContactActionListItem && newItem instanceof DialogContactActionListItem) {
            return ((DialogContactActionListItem) oldItem).isExpanded() == ((DialogContactActionListItem) newItem).isExpanded();

        } else if (oldItem instanceof DialogContactActionHeaderListItem && newItem instanceof DialogContactActionHeaderListItem) {
            return TextUtils.equals(((DialogContactActionHeaderListItem) oldItem).getContact().getUiName(), ((DialogContactActionHeaderListItem) newItem).getContact().getUiName());

        } else if (oldItem instanceof DialogContactActionDetailListItem && newItem instanceof DialogContactActionDetailListItem) {
            return TextUtils.equals(((DialogContactActionDetailListItem) oldItem).getSubtitle(), ((DialogContactActionDetailListItem) newItem).getSubtitle());

        } else if (oldItem instanceof MeetingListItem && newItem instanceof MeetingListItem) {
            if(!TextUtils.equals(((MeetingListItem) oldItem).getCalendarEvent().getStatus(), ((MeetingListItem) newItem).getCalendarEvent().getStatus()))
                return false;
            else {
                int oldAttendees = MeetingUtil.Companion.getPeopleJoined(((MeetingListItem) oldItem).getCalendarEvent());
                int newAttendees = MeetingUtil.Companion.getPeopleJoined(((MeetingListItem) newItem).getCalendarEvent());
                return oldAttendees != newAttendees;
            }

        }
        
        return false;
    }

}
