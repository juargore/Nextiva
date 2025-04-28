package com.nextiva.nextivaapp.android.xmpp

import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.model.DbPresence
import com.nextiva.nextivaapp.android.models.NextivaContact

object XmppActions {
    open class BaseXmppConnectionAction(var action: String)
    class XmppDisconnectConnection: BaseXmppConnectionAction(NextivaXMPPConnection.ACTION_DISCONNECT_CONNECTION)
    class XmppRefreshRoster(val forceRefresh: Boolean): BaseXmppConnectionAction(NextivaXMPPConnection.ACTION_REFRESH_ROSTER)
    class XmppContactUpdateRefresh: BaseXmppConnectionAction(NextivaXMPPConnection.ACTION_CONTACT_UPDATE_REFRESH)
    class XmppSetPresence(val dbPresence: DbPresence?): BaseXmppConnectionAction(NextivaXMPPConnection.ACTION_SET_PRESENCE)
    class XmppUpdateRoster(val contactsToAdd: ArrayList<NextivaContact>?,
                           val nextivaContacts: ArrayList<NextivaContact>?,
                           val actionType: Int?): BaseXmppConnectionAction(NextivaXMPPConnection.ACTION_UPDATE_CONTACTS)
    class XmppSubscribeToContact(val jid: String?): BaseXmppConnectionAction(NextivaXMPPConnection.ACTION_SUBSCRIBE_TO_CONTACT)
    class XmppUnsubscribeFromContact(val jid: String?): BaseXmppConnectionAction(NextivaXMPPConnection.ACTION_UNSUBSCRIBE_FROM_CONTACT)
    class XmppAcceptSubscriptionRequest(val jid: String?): BaseXmppConnectionAction(NextivaXMPPConnection.ACTION_ACCEPT_SUBSCRIPTION_REQUEST)
    class XmppDeclineSubscriptionRequest(val jid: String?): BaseXmppConnectionAction(NextivaXMPPConnection.ACTION_DECLINE_SUBSCRIPTION_REQUEST)
    class XmppHandlePendingSubscriptionRequests: BaseXmppConnectionAction(NextivaXMPPConnection.ACTION_HANDLE_PENDING_SUBSCRIPTION_REQUESTS)
    class XmppSendChatMessage(val jid: String?,
                              val chatMessage: String?,
                              @Enums.Chats.ConversationTypes.Type val chatType: String?): BaseXmppConnectionAction(NextivaXMPPConnection.ACTION_SEND_CHAT_MESSAGE)
    class XmppSendChatState(val jid: String?,
                            @Enums.Chats.States.State val chatState: String?): BaseXmppConnectionAction(NextivaXMPPConnection.ACTION_SEND_CHAT_STATE)
    class XmppSetPubSubStatusText(val statusText: String?): BaseXmppConnectionAction(NextivaXMPPConnection.ACTION_SET_PUBSUB_STATUS_TEXT)
    class XmppStartGroupChat(val jidList: ArrayList<String>?): BaseXmppConnectionAction(NextivaXMPPConnection.ACTION_START_GROUP_CHAT)
    class XmppJoinGroupChat(val mucJid: String?): BaseXmppConnectionAction(NextivaXMPPConnection.ACTION_JOIN_GROUP_CHAT)
    class XmppLeaveGroupChat(val mucJid: String?): BaseXmppConnectionAction(NextivaXMPPConnection.ACTION_LEAVE_GROUP_CHAT)
    class XmppRequestChatParticipants(val mucJid: String?): BaseXmppConnectionAction(NextivaXMPPConnection.ACTION_REQUEST_GROUP_CHAT_PARTICIPANTS)
    class XmppInviteChatParticipants(val jid: String?,
                                     val jidsToInvite: ArrayList<String>?): BaseXmppConnectionAction(NextivaXMPPConnection.ACTION_INVITE_GROUP_CHAT_PARTICIPANTS)
    class XmppDeclineGroupChatInvitation(val mucJid: String?,
                                         val inviterJid: String?): BaseXmppConnectionAction(NextivaXMPPConnection.ACTION_DECLINE_GROUP_CHAT_INVITATION)
    class XmppHandlePendingGroupChatInvitations: BaseXmppConnectionAction(NextivaXMPPConnection.ACTION_HANDLE_PENDING_GROUP_CHAT_INVITATIONS)
    class XmppSendVCardUpdatedPresence: BaseXmppConnectionAction(NextivaXMPPConnection.ACTION_SEND_VCARD_UPDATED_PRESENCE)
    class XmppTestConnection: BaseXmppConnectionAction(NextivaXMPPConnection.ACTION_TEST_CONNECTION)
}