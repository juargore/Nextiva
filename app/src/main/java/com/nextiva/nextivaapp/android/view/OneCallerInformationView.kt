package com.nextiva.nextivaapp.android.view

import android.content.Context
import android.telephony.PhoneNumberUtils
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.databinding.ViewOneCallerInformationBinding
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.models.AvatarInfo
import com.nextiva.nextivaapp.android.models.DbResponse
import com.nextiva.nextivaapp.android.models.NextivaContact
import com.nextiva.nextivaapp.android.util.CallUtil
import com.nextiva.nextivaapp.android.view.textwatchers.ExtensionEnabledPhoneNumberFormattingTextWatcher
import com.nextiva.pjsip.pjsip_lib.sipservice.ParticipantInfo
import io.reactivex.observers.DisposableSingleObserver
import java.util.Locale

class OneCallerInformationView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : CoordinatorLayout(context, attrs, defStyleAttr) {
    private lateinit var callerNameTextView: TextView
    private lateinit var avatarView: AvatarView
    private lateinit var callerNumberTextView: TextView
    private lateinit var statusTextView: TextView

    private var contact: NextivaContact? = null
    private var phoneNumber: String? = null
    private var displayName: String? = null
    private var calleeCount = 1

    private fun init(context: Context) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        bindViews(inflater, this)
    }

    private fun bindViews(inflater: LayoutInflater, container: ViewGroup) {
        val binding = ViewOneCallerInformationBinding.inflate(inflater, container, true)

        callerNameTextView = binding.callerInformationCallerNameTextView
        avatarView = binding.callerInformationCallerAvatarView
        callerNumberTextView = binding.callerInformationCallerNumberTextView
        statusTextView = binding.callerInformationStatusTextView
    }

    fun populateCallerInfo(sessionManager: SessionManager, participantInfoArrayList: ArrayList<ParticipantInfo>, dbManager: DbManager, isConference: Boolean) {
        val callNameStringBuilder = StringBuilder()
        calleeCount = participantInfoArrayList.size

        participantInfoArrayList.forEach { callInfo ->
            if (callNameStringBuilder.isNotEmpty()) {
                callNameStringBuilder.append(", ")
            }

            when {
                (callInfo.displayName ?: "").trim().isNotEmpty() -> callNameStringBuilder.append(callInfo.displayName)
                else -> {
                    val contact = if (sessionManager.isNextivaConnectEnabled) {
                        dbManager.getConnectContactFromPhoneNumberInThread(callInfo.numberToCall)
                    } else {
                        dbManager.getContactFromPhoneNumberInThread(callInfo.numberToCall)
                    }

                    callNameStringBuilder.append(
                        contact.value?.displayName ?: callInfo.numberToCall
                        ?: context.getString(R.string.general_unavailable)
                    )
                }
            }
        }

        displayName = callNameStringBuilder.toString()

        if (participantInfoArrayList.size > 1 || isConference) {
            phoneNumber = "${context.getString(R.string.active_call_conference)} ${context.getString(R.string.active_call_conference_callee_count, calleeCount)}"

        } else {
            participantInfoArrayList.firstOrNull()?.let { callInfo ->
                callInfo.numberToCall?.let { number ->
                    val separatePhoneNumberFromDTMFTonesArray = CallUtil.separatePhoneNumberFromDTMFTones(number)

                    phoneNumber = separatePhoneNumberFromDTMFTonesArray.firstOrNull()?.let { numberToFormat ->
                        PhoneNumberUtils.formatNumber(numberToFormat, Locale.getDefault().country)
                    } ?: ""
                }
            }
        }

        populateCallerInfo(dbManager)
    }

    private fun populateCallerInfo(dbManager: DbManager) {
        callerNumberTextView.text = phoneNumber
        callerNameTextView.text = getCallerDisplayName(contact)

        val avatarBuilder = AvatarInfo.Builder().isConnect(true)

        if (calleeCount < 2) {
            contact?.let { contact ->
                avatarView.setAvatar(contact.avatarInfo)

            } ?: kotlin.run {
                dbManager.getConnectContactFromPhoneNumber(phoneNumber)
                    .subscribe(object : DisposableSingleObserver<DbResponse<NextivaContact>>() {
                        override fun onSuccess(response: DbResponse<NextivaContact>) {
                            callerNameTextView.text = getCallerDisplayName(response.value)
                            avatarView.setAvatar(response.value?.getAvatarInfo(true) ?: avatarBuilder
                                .setDisplayName(displayName)
                                .build())
                        }

                        override fun onError(e: Throwable) {
                            avatarView.setAvatar(avatarBuilder
                                .setDisplayName(displayName)
                                .build())
                        }
                    })
            }

        } else {
            avatarView.setAvatar(avatarBuilder
                .setIconResId(R.drawable.avatar_group)
                .build())
        }

        setContentDescriptions()
    }

    private fun getCallerDisplayName(contact: NextivaContact?): String {
        if (!TextUtils.isEmpty(contact?.uiName) && CallUtil.getStrippedPhoneNumber(phoneNumber ?: "").contains(CallUtil.getStrippedPhoneNumber(contact?.uiName ?: ""))) {
            return contact?.uiName ?: displayName ?: context.getString(R.string.general_unavailable)
        }

        return displayName ?: context.getString(R.string.general_unavailable)
    }

    fun setCallStatus(callStatus: String?) {
        statusTextView.text = callStatus
        statusTextView.contentDescription = context.getString(R.string.caller_information_status_text_view_content_description)
    }

    fun setCallStatus(callStatus: Int) {
        statusTextView.setText(callStatus)
        statusTextView.contentDescription = context.getString(R.string.caller_information_status_text_view_content_description)
    }

    private fun setContentDescriptions() {
        callerNameTextView.contentDescription = context.getString(R.string.caller_information_caller_name_text_view_content_description)
        avatarView.contentDescription = context.getString(R.string.caller_information_caller_avatar_view_content_description)
        callerNumberTextView.contentDescription = context.getString(R.string.caller_information_caller_number_text_view_content_description)
        statusTextView.contentDescription = context.getString(R.string.caller_information_status_text_view_content_description)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        //TODO:Causing issues displaying layout
        if (!isInEditMode) {
            callerNameTextView.addTextChangedListener(ExtensionEnabledPhoneNumberFormattingTextWatcher())
            callerNumberTextView.addTextChangedListener(ExtensionEnabledPhoneNumberFormattingTextWatcher())
        }
    }

    init {
        init(context)
    }
}