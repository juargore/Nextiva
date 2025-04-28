package com.nextiva.nextivaapp.android.features.messaging.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.nextiva.nextivaapp.android.BaseActivity
import com.nextiva.nextivaapp.android.ConnectContactDetailsActivity.Companion.newIntent
import com.nextiva.nextivaapp.android.ConnectMainActivity.Companion.created
import com.nextiva.nextivaapp.android.ConnectMainActivity.Companion.newIntent
import com.nextiva.nextivaapp.android.ConnectMainActivity.Companion.shouldShowMessagingTab
import com.nextiva.nextivaapp.android.LoginActivity
import com.nextiva.nextivaapp.android.OneActiveCallActivity
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.constants.Constants
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.constants.Enums.Analytics.ScreenName
import com.nextiva.nextivaapp.android.constants.Enums.Chats.ChatScreens.ChatScreen
import com.nextiva.nextivaapp.android.core.common.ui.ActiveCallBanner
import com.nextiva.nextivaapp.android.core.common.ui.PendingMessageData
import com.nextiva.nextivaapp.android.databinding.ActivityConnectSmsChatBinding
import com.nextiva.nextivaapp.android.db.model.SmsTeam
import com.nextiva.nextivaapp.android.features.messaging.view.ConversationFragment.Companion.newInstance
import com.nextiva.nextivaapp.android.features.messaging.viewmodel.CommonSmsViewModel
import com.nextiva.nextivaapp.android.fragments.bottomsheets.BottomSheetSmsDetails.Companion.newInstance
import com.nextiva.nextivaapp.android.managers.interfaces.AnalyticsManager
import com.nextiva.nextivaapp.android.managers.interfaces.CallManager
import com.nextiva.nextivaapp.android.managers.interfaces.SettingsManager
import com.nextiva.nextivaapp.android.models.NextivaContact
import com.nextiva.nextivaapp.android.models.SmsConversationDetails
import com.nextiva.nextivaapp.android.util.ApplicationUtil.isNightModeEnabled
import com.nextiva.nextivaapp.android.util.CallUtil
import com.nextiva.nextivaapp.android.util.GsonUtil
import com.nextiva.pjsip.pjsip_lib.sipservice.ParticipantInfo
import com.nextiva.pjsip.pjsip_lib.sipservice.SipCall
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class ConversationActivity() : BaseActivity() {

    @Inject
    lateinit var mCallManager: CallManager
    @Inject
    lateinit var settingsManager: SettingsManager
    @Inject
    lateinit var mAnalyticsManager: AnalyticsManager

    private lateinit var mChatSmsToolbar: Toolbar
    lateinit var titleTextView: TextView
    private lateinit var mBackArrowView: RelativeLayout
    lateinit var mToolbarCallButton: RelativeLayout
    private lateinit var activeCallBar: ComposeView

    private var conversationType: String? = ""
    private lateinit var mCurrentFragment: ConversationFragment
    private lateinit var viewModel: CommonSmsViewModel

    private val activeCallObserver = Observer<SipCall?> { sipCall ->
        if (sipCall != null) {
            MainScope().launch(Dispatchers.IO) {
                val contact = sipCall.participantInfoList.firstOrNull()?.numberToCall?.let { viewModel.getContactFromPhoneNumberInThread(it) }

                withContext(Dispatchers.Main) {
                    activeCallBar.setContent {
                        ActiveCallBanner(sipCall, contact, !isNightModeEnabled(applicationContext, settingsManager), viewModel.sipManager.activeCallDurationLiveData) {
                            startActivity(OneActiveCallActivity.newIntent(applicationContext, sipCall.participantInfoList.firstOrNull(), null))
                        }
                    }
                    activeCallBar.visibility = View.VISIBLE
                }
            }

        } else {
            activeCallBar.disposeComposition()
            activeCallBar.visibility = View.GONE
        }
    }

    fun bindViews(): View {
        val binding = ActivityConnectSmsChatBinding.inflate(
            layoutInflater
        )

        mChatSmsToolbar = binding.chatSmsToolbar
        titleTextView = binding.chatSmsTitleTextView
        mBackArrowView = binding.backArrowInclude.backArrowView
        mToolbarCallButton = binding.callIconInclude.viewToolbarCallButton
        activeCallBar = binding.activeCallToolbar

        overrideEdgeToEdge(binding.root)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(bindViews())

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        setStatusBarColor(ContextCompat.getColor(this, R.color.connectGrey01))

        var groupValue: String = ""

        if ((intent != null) && (intent.action != null) && intent.action.equals(
                Intent.ACTION_SENDTO,
                ignoreCase = true
            ) && (intent.data != null) && !TextUtils.isEmpty(
                intent.data?.schemeSpecificPart
            )
        ) {
            if (mSessionManager.userDetails != null) {
                val participantList = ArrayList<String>()
                val participantNumber =
                    CallUtil.getCountryCode() + CallUtil.cleanPhoneNumberAndRemoveUSCountryCode(
                        CallUtil.getStrippedPhoneNumber(
                            intent.data!!.schemeSpecificPart
                        )
                    )
                participantList.add(participantNumber)
                groupValue =
                    CallUtil.getCountryCode() + mSessionManager.userDetails?.telephoneNumber + "," + participantNumber
                val sortingGroupValueList =
                    groupValue.replace("\\s".toRegex(), "").split(",".toRegex())
                        .dropLastWhile { it.isEmpty() }.toMutableList()
                sortingGroupValueList.sortWith { s, t1 ->
                    s.trim().toLong().compareTo(t1.trim().toLong())
                }
                groupValue = TextUtils.join(",", sortingGroupValueList).trim { it <= ' ' }
                intent.putExtra(Constants.Chats.PARAMS_PARTICIPANTS, participantList)
                intent.putExtra(Constants.Chats.PARAMS_CHAT_TYPE, Enums.Chats.ConversationTypes.SMS)
                intent.putExtra(Constants.Chats.PARAMS_GROUP_VALUE, groupValue)
                intent.putExtra(Constants.Chats.PARAMS_IS_NEW_CHAT, false)
                intent.putExtra(
                    Constants.Chats.PARAMS_CHAT_SCREEN,
                    Enums.Chats.ChatScreens.CONVERSATION
                )
            } else {
                startActivity(LoginActivity.newIntent(this@ConversationActivity, "ChatSms"))
                finish()
            }
        }


        @ChatScreen val chatScreen: String? =
            intent.getStringExtra(Constants.Chats.PARAMS_CHAT_SCREEN)

        viewModel = ViewModelProvider(this).get(CommonSmsViewModel::class.java)
        viewModel.activeCallLiveData.observe(this, activeCallObserver)
        viewModel.toolbarTitle.observe(this) { title: String? ->
            titleTextView.text = title
        }

        if (intent != null && intent.hasExtra(Constants.Chats.PARAMS_CHAT_TYPE)) {
            conversationType = intent.getStringExtra(Constants.Chats.PARAMS_CHAT_TYPE)
        } else {
            conversationType = Constants.CHAT_CONVERSATION_TYPE
        }

        chatConversationOnCreate()

        setSupportActionBar(mChatSmsToolbar)

        mBackArrowView.setOnClickListener {
            mAnalyticsManager.logEvent(
                ScreenName.NEW_CALL_MAIN,
                Enums.Analytics.EventName.BACK_BUTTON_PRESSED
            )
            this.onBackPressed()
        }

        mToolbarCallButton.setOnClickListener {
            mAnalyticsManager.logEvent(
                ScreenName.NEW_CALL_MAIN,
                Enums.Analytics.EventName.BACK_BUTTON_PRESSED
            )
            this.onBackPressed()
        }

        titleTextView.setOnClickListener { _: View ->
            val participants: List<String> = mCurrentFragment.getParticipantsList()
            mCurrentFragment.getCurrentConversationDetails()?.let { conversationDetails ->

                var participantsCount: Int = 0
                var teamsCount: Int = 0

                participantsCount =
                    HashSet(participants).size // convert to Set to remove duplicates

                val teams: List<SmsTeam> = conversationDetails.getAllTeams()
                teamsCount = teams.size

                if (participantsCount + teamsCount > 1 || teamsCount > 0) {
                    newInstance(
                        conversationDetails,
                        ArrayList(participants),
                        true
                    ).show(supportFragmentManager, null)
                } else if (participantsCount > 0) {
                    val contact: NextivaContact? =
                        viewModel.getContactFromPhoneNumber(participants[0])
                    if (contact != null) {
                        startActivity(newIntent(this, contact))
                    }
                } else {
                    // Self conversation
                    val contact: NextivaContact? =
                        viewModel.getContactFromPhoneNumber(mSessionManager.getPhoneNumberInformation().phoneNumber)
                    if (contact != null) {
                        startActivity(newIntent(this, contact))
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        mCurrentFragment.saveMessageAsDraftIfNeeded(false)
    }

    override fun onBackPressed() {
        setResult(RESULT_OK)
        if (created) {
            super.onBackPressed()
        } else {
            shouldShowMessagingTab = true
            startMainActivityAndFinishThis()
        }
    }

    private fun startMainActivityAndFinishThis() {
        val main = newIntent(applicationContext)
        applicationContext.startActivity(main)
        finish()
    }

    val currentConversationId: String?
        get() {
            return mCurrentFragment.getCurrentConversationDetails()?.getConversationId()
        }

    fun updateReadStatus() {
        mCurrentFragment.updateReadStatus()
    }

    //TODO: COMBINE WITH loadConversationFragment
    private fun chatConversationOnCreate() {
        mCurrentFragment = newInstance(
            (intent.getStringExtra(Constants.Chats.PARAMS_CHAT_TYPE))!!,
            intent.getStringArrayListExtra(Constants.Chats.PARAMS_PARTICIPANTS),
            GsonUtil.getObject(
                SmsConversationDetails::class.java,
                intent.getStringExtra(Constants.Chats.PARAMS_SMS_CONVERSATION_DETAILS)
            ),
            intent.getSerializableExtra(Constants.Chats.PARAMS_PENDING_MESSAGE_DATA) as PendingMessageData?,
            false,
            intent.getBooleanExtra(Constants.Chats.PARAMS_IS_NEW_CHAT, false),
            null
        )

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, mCurrentFragment)
            .commit()
    }

    override fun startActivity(intent: Intent, options: Bundle?) {
        if ((TextUtils.equals(intent.action, Intent.ACTION_VIEW) &&
                    !TextUtils.isEmpty(intent.dataString) &&
                    intent.dataString!!.startsWith("tel:"))
        ) {
            val participantInfo = ParticipantInfo()
            participantInfo.numberToCall = CallUtil.getStrippedPhoneNumber((intent.dataString)!!)
            mCallManager.makeCall(
                this,
                ScreenName.CHAT_DETAILS,
                participantInfo,
                compositeDisposable
            )
        } else {
            super.startActivity(intent, options)
        }
    }

    companion object {
        private const val CONVERSATION_SCREEN = Enums.Chats.ChatScreens.CONVERSATION

        @JvmStatic
        fun newIntent(
            context: Context?,
            conversationDetails: SmsConversationDetails?,
            isNewChat: Boolean?,
            @Enums.Chats.ConversationTypes.Type chatType: String?,
            @ChatScreen chatScreen: String?
        ): Intent {
            return newIntent(context, conversationDetails, null, isNewChat, chatType, chatScreen)
        }

        fun newIntent(
            context: Context?,
            conversationDetails: SmsConversationDetails?,
            pendingMessage: PendingMessageData?,
            isNewChat: Boolean?,
            @Enums.Chats.ConversationTypes.Type chatType: String?,
            @ChatScreen chatScreen: String?
        ): Intent {
            val intent = Intent(context, ConversationActivity::class.java)
            intent.putExtra(Constants.Chats.PARAMS_CHAT_TYPE, chatType)
            intent.putExtra(
                Constants.Chats.PARAMS_SMS_CONVERSATION_DETAILS,
                GsonUtil.getJSON(conversationDetails)
            )
            intent.putExtra(Constants.Chats.PARAMS_PENDING_MESSAGE_DATA, pendingMessage)
            intent.putExtra(Constants.Chats.PARAMS_IS_NEW_CHAT, isNewChat)
            intent.putExtra(Constants.Chats.PARAMS_CHAT_SCREEN, chatScreen)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            return intent
        }
    }
}