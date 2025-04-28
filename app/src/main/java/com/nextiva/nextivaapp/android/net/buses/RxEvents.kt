package com.nextiva.nextivaapp.android.net.buses

import com.afollestad.materialdialogs.MaterialDialog
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.models.CallLogEntry
import com.nextiva.nextivaapp.android.models.ChatConversation
import com.nextiva.nextivaapp.android.models.ChatMessage
import com.nextiva.nextivaapp.android.models.FeatureAccessCodes
import com.nextiva.nextivaapp.android.models.NextivaAnywhereLocation
import com.nextiva.nextivaapp.android.models.NextivaContact
import com.nextiva.nextivaapp.android.models.Service
import com.nextiva.nextivaapp.android.models.ServiceSettings
import com.nextiva.nextivaapp.android.models.UserDetails
import com.nextiva.nextivaapp.android.models.net.broadsoft.BroadsoftErrorResponseBody
import com.nextiva.nextivaapp.android.models.net.broadsoft.BroadsoftUserConference
import com.nextiva.nextivaapp.android.models.net.broadsoft.services.BroadsoftCallCenter
import com.nextiva.nextivaapp.android.models.net.broadsoft.services.BroadsoftCallCenterUnavailableCodes
import com.nextiva.nextivaapp.android.models.net.platform.Products

object RxEvents {
    open class BaseResponseEvent(var isSuccessful: Boolean)

    // --------------------------------------------------------------------------------------------
    // Call Events
    // --------------------------------------------------------------------------------------------

    class CallBackCallResponseEvent(isSuccessful: Boolean) : BaseResponseEvent(isSuccessful)
    class CallThroughCallResponseEvent(isSuccessful: Boolean, val callThroughNumber: String?) : BaseResponseEvent(isSuccessful)
    class ChatConversationInvitationReceivedEvent(val roomId: String?, var wasHandled: Boolean, var userJid: String?)
    class DeleteAllCallsResponseEvent(isSuccessful: Boolean) : BaseResponseEvent(isSuccessful)
    class DeleteCallResponseEvent(isSuccessful: Boolean, @Enums.Calls.CallTypes.Type val callType: String, val callId: String) : BaseResponseEvent(isSuccessful)
    class CallUpdatedEvent(val callId: String, val trackingId: String, val callWasMissed: Boolean)

    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // Login Flow Events
    // --------------------------------------------------------------------------------------------

    class SipRegisterFinished(isSuccessful: Boolean, val unregisterStartTime: Long) : BaseResponseEvent(isSuccessful)
    class SipDeregisterFinished(isSuccessful: Boolean) : BaseResponseEvent(isSuccessful)

    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // Login Flow Events
    // --------------------------------------------------------------------------------------------

    class MobileConfigResponseEvent(isSuccessful: Boolean) : BaseResponseEvent(isSuccessful)
    class UserDetailsResponseEvent(isSuccessful: Boolean, val userDetails: UserDetails?) : BaseResponseEvent(isSuccessful)
    class FeatureAccessCodesResponseEvent(isSuccessful: Boolean, val featureAccessCodes: FeatureAccessCodes?) : BaseResponseEvent(isSuccessful)
    class ServicesResponseEvent(isSuccessful: Boolean, val services: Array<Service>?) : BaseResponseEvent(isSuccessful)
    class VoicemailMessageSummaryResponseEvent(isSuccessful: Boolean, val newMessageCount: Int?, val oldMessageCount: Int?) : BaseResponseEvent(isSuccessful)
    class CallHistoryResponseEvent(isSuccessful: Boolean, val callLogEntriesList: ArrayList<CallLogEntry>?) : BaseResponseEvent(isSuccessful)
    class RegisterForPushNotificationsResponseEvent(isSuccessful: Boolean) : BaseResponseEvent(isSuccessful)
    class ResetConferenceCallResponseEvent(isSuccessful: Boolean, val conferenceCallAddress: String?) : BaseResponseEvent(isSuccessful)
    class ClearConferenceCallsResponseEvent(isSuccessful: Boolean) : BaseResponseEvent(isSuccessful)
    class IsExistingActiveCallResponseEvent(isSuccessful: Boolean) : BaseResponseEvent(isSuccessful)
    class ProductsResponseEvent(isSuccessful: Boolean, val products: Products?) : BaseResponseEvent(isSuccessful)
    class PhoneInformationResponseEvent(isSuccessful: Boolean) : BaseResponseEvent(isSuccessful)
    class UnregisterForPushNotificationsResponseEvent(isSuccessful: Boolean) : BaseResponseEvent(isSuccessful)
    class RegisterDeviceResponseEvent(isSuccessful: Boolean) : BaseResponseEvent(isSuccessful)
    class UnregisterDeviceResponseEvent(isSuccessful: Boolean) : BaseResponseEvent(isSuccessful)
    class AuthenticationResponseEvent(isSuccessful: Boolean, val hasMobileDevice: Boolean) : BaseResponseEvent(isSuccessful)
    class FeatureFlagsResponseEvent(isSuccessful: Boolean) : BaseResponseEvent(isSuccessful)
    class AccountInformationResponseEvent(isSuccessful: Boolean) : BaseResponseEvent(isSuccessful)
    class PresenceResponseEvent : BaseResponseEvent(true)
    class PresenceSentResponseEvent: BaseResponseEvent(true)
    class ContactManagementPolicyResponseEvent: BaseResponseEvent(true)

    // --------------------------------------------------------------------------------------------
    // Xmpp Events
    // --------------------------------------------------------------------------------------------

    // Chat
    class ChatConversationJoinedEvent(isSuccessful: Boolean, val mucJid: String, val isRoomClosed: Boolean) : BaseResponseEvent(isSuccessful)

    class ChatConversationLeftEvent(isSuccessful: Boolean, var mucJid: String) : BaseResponseEvent(isSuccessful)
    class ChatConversationParticipantJoinedEvent(var jid: String, var mucJid: String)
    class ChatConversationParticipantLeftEvent(var jid: String, var mucJid: String)
    class ChatConversationParticipantsEvent(isSuccessful: Boolean, var mucJid: String, var participants: ArrayList<String>?) : BaseResponseEvent(isSuccessful)
    class ChatConversationParticipantsInvitedEvent(isSuccessful: Boolean) : BaseResponseEvent(isSuccessful)
    class ChatConversationsResponseEvent(isSuccessful: Boolean, var chatConversationsList: ArrayList<ChatConversation>?) : BaseResponseEvent(isSuccessful)
    class ChatConversationStartedEvent(isSuccessful: Boolean, var mucJid: String?) : BaseResponseEvent(isSuccessful)
    class IncomingChatMessageResponseEvent(isSuccessful: Boolean, val chatMessage: ChatMessage) : BaseResponseEvent(isSuccessful)

    class ContactUpdatedResponseEvent(isSuccessful: Boolean) : BaseResponseEvent(isSuccessful)
    class RosterResponseEvent(isSuccessful: Boolean, val favoritesNumber: Int) : BaseResponseEvent(isSuccessful)

    //Presence
    class RosterPresenceSubscriptionRequestEvent(var wasHandled: Boolean, var jid: String?, var dialog: MaterialDialog? = null)

    //Other
    class XmppCannotConnectEvent(val exception: Exception)

    class XmppPingEvent(isSuccessful: Boolean) : BaseResponseEvent(isSuccessful)

    class XmppErrorEvent(val errorException: Exception)
    class XmppUserResponseEvent

    // --------------------------------------------------------------------------------------------
    // Service Setting Events
    // --------------------------------------------------------------------------------------------

    class ServiceSettingsMapResponseEvent(isSuccessful: Boolean, val serviceSettingsMap: Map<String, ServiceSettings>?) : BaseResponseEvent(isSuccessful)
    class ServiceSettingsPutResponseEvent(isSuccessful: Boolean, val errorInfo: BroadsoftErrorResponseBody?, val serviceSettings: ServiceSettings?) : BaseResponseEvent(isSuccessful)
    class ServiceSettingsGetResponseEvent(isSuccessful: Boolean, val serviceSettings: ServiceSettings?) : BaseResponseEvent(isSuccessful)
    class NextivaAnywhereLocationDeleteResponseEvent(isSuccessful: Boolean, val proposedServiceSettings: ServiceSettings?, val nextivaAnywhereLocation: NextivaAnywhereLocation?) : BaseResponseEvent(isSuccessful)
    class NextivaAnywhereLocationGetResponseEvent(isSuccessful: Boolean, val nextivaAnywhereLocation: NextivaAnywhereLocation?) : BaseResponseEvent(isSuccessful)
    class NextivaAnywhereLocationSaveResponseEvent(isSuccessful: Boolean, val errorInfo: BroadsoftErrorResponseBody?, val nextivaAnywhereServiceSettings: ServiceSettings?, val nextivaAnywhereLocation: NextivaAnywhereLocation?, val oldPhoneNumber: String?) : BaseResponseEvent(isSuccessful)
    class BroadsoftMeetMeConferenceResponseEvent(isSuccessful: Boolean, val conferenceNumber: String, val bridgeId: String) : BaseResponseEvent(isSuccessful)
    class BroadsoftMeetMeConferencingUserConferencesResponseEvent(isSuccessful: Boolean, val userConferences: ArrayList<BroadsoftUserConference>) : BaseResponseEvent(isSuccessful)
    class BroadsoftMeetMeConferencingConferenceResponseEvent(isSuccessful: Boolean, val conferenceNumber: String, val conferencePin: String) : BaseResponseEvent(isSuccessful)
    class BroadsoftCallCenterResponseEvent(isSuccessful: Boolean, val callCenter: BroadsoftCallCenter) : BaseResponseEvent(isSuccessful)
    class BroadsoftCallCenterPutResponseEvent(isSuccessful: Boolean) : BaseResponseEvent(isSuccessful)
    class BroadsoftCallCenterUnavailableCodesResponseEvent(isSuccessful: Boolean, val callCenterUnavailableCodes: BroadsoftCallCenterUnavailableCodes) : BaseResponseEvent(isSuccessful)

    // --------------------------------------------------------------------------------------------
    // API Events
    // --------------------------------------------------------------------------------------------

    class EnterpriseContactByImpIdResponseEvent(isSuccessful: Boolean, val nextivaContact: NextivaContact?, @Enums.Sip.CallTypes.Type val callType: Int?) : BaseResponseEvent(isSuccessful)
    class EnterpriseContactByNameResponseEvent(isSuccessful: Boolean, val nextivaContact: NextivaContact?) : BaseResponseEvent(isSuccessful)
    open class EnterpriseContactByNumberResponseEvent(isSuccessful: Boolean, val nextivaContact: NextivaContact?, val phoneNumber: String?, @Enums.Sip.CallTypes.Type val callType: Int) : BaseResponseEvent(isSuccessful)

    // --------------------------------------------------------------------------------------------
    // API Logging Events
    // --------------------------------------------------------------------------------------------
    class LoggingResponseEvent(isSuccessful: Boolean) : BaseResponseEvent(isSuccessful)
}