package com.nextiva.nextivaapp.android.fragments.bottomsheets

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.nextiva.nextivaapp.android.CreateBusinessContactActivity
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.constants.Constants
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.constants.RequestCodes
import com.nextiva.nextivaapp.android.databinding.BottomSheetDialerBinding
import com.nextiva.nextivaapp.android.databinding.IncludeDialerPadBinding
import com.nextiva.nextivaapp.android.managers.interfaces.CallManager
import com.nextiva.nextivaapp.android.managers.interfaces.DialogManager
import com.nextiva.nextivaapp.android.managers.interfaces.LogManager
import com.nextiva.nextivaapp.android.managers.interfaces.PermissionManager
import com.nextiva.nextivaapp.android.util.CallUtil
import com.nextiva.nextivaapp.android.util.extensions.extractDtfmTone
import com.nextiva.nextivaapp.android.util.extensions.extractFirstNumber
import com.nextiva.nextivaapp.android.view.DialerPadView
import com.nextiva.nextivaapp.android.view.DialerPadView.DialerPadClickListener
import com.nextiva.nextivaapp.android.view.fontawesome.FontTextView
import com.nextiva.nextivaapp.android.view.textwatchers.ExtensionEnabledPhoneNumberFormattingTextWatcher
import com.nextiva.nextivaapp.android.viewmodels.DialerViewModel
import com.nextiva.pjsip.pjsip_lib.sipservice.ParticipantInfo
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@AndroidEntryPoint
class BottomSheetDialerFragment(): BaseBottomSheetDialogFragment(), DialerPadClickListener, View.OnTouchListener,
    CallManager.ProcessParticipantInfoCallBack
{

    constructor(predialedNumber: String): this() {
        this.predialedNumber = predialedNumber
    }

    @Inject
    lateinit var dialogManager: DialogManager
    @Inject
    lateinit var callManager: CallManager
    @Inject
    lateinit var logManager: LogManager
    @Inject
    lateinit var permissionManager: PermissionManager

    private lateinit var viewModel: DialerViewModel

    private lateinit var phoneNumberEditText: EditText
    private lateinit var dialpadView: DialerPadView
    private lateinit var backArrowImgView: ImageView
    private lateinit var voicemailCountText: TextView
    private lateinit var voicemailButton: FontTextView
    private lateinit var voiceCallButton: FontTextView
    private lateinit var pullCall: TextView
    private lateinit var cancelIcon: RelativeLayout
    private lateinit var phoneNumberConstraintView: ConstraintLayout
    private lateinit var mPopupWindow: PopupWindow
    private lateinit var clipboardManager: ClipboardManager

    companion object {
        private const val POPUP_WINDOW_WIDTH_RATIO = 0.85
        private const val POPUP_WINDOW_X_OFFSET = 30
        private const val POPUP_WINDOW_Y_OFFSET = -50
    }

    private var predialedNumber: String? = null
    private var newCallType: Int = RequestCodes.NewCall.NEW_CALL_NONE

    private val mErrorDialogButtonCallback = SingleButtonCallback { dialog, _ ->
        dialog.dismiss()
        viewModel.clearErrorState()
    }

    private val mProcessedCallInfoObserver = Observer { participantInfo: ParticipantInfo? ->
        phoneNumberEditText.text = null

        participantInfo?.let {
            callManager.makeCall(requireActivity(), Enums.Analytics.ScreenName.BOTTOM_SHEET_DIALER, participantInfo, CompositeDisposable())
        }
    }
    private val mErrorStateObserver = Observer { isError: Boolean? ->
        if (isError != null && isError) {
            dialogManager.showErrorDialog(requireActivity(), Enums.Analytics.ScreenName.BOTTOM_SHEET_DIALER, mErrorDialogButtonCallback)
        }
    }
    private val mLastDialedPhoneNumberObserver = Observer { lastDialedPhoneNumber: String? ->
        if (!TextUtils.isEmpty(lastDialedPhoneNumber)) {
            phoneNumberEditText.setText(lastDialedPhoneNumber)
            phoneNumberEditText.setSelection(phoneNumberEditText.text.length)
            phoneNumberEditText.requestFocus()
        }
    }
    private val mGetVoicemailCountObserver = Observer { voicemailCount: Int -> this.setVoicemailCount(voicemailCount) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showFullHeight = true
        viewModel = ViewModelProvider(this)[DialerViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_dialer, container, false)
        view?.let { bindViews(view) }

        viewModel.processedCallInfoLiveData.observe(this, mProcessedCallInfoObserver)
        viewModel.errorStateLiveData.observe(this, mErrorStateObserver)
        viewModel.lastDialedPhoneNumberLiveData.observe(this, mLastDialedPhoneNumberObserver)
        viewModel.voicemailCountLiveData.observe(this, mGetVoicemailCountObserver)

        newCallType = requireActivity().intent.getIntExtra(
            Constants.Calls.PARAMS_NEW_CALL_TYPE,
            RequestCodes.NewCall.NEW_CALL_NONE
        )

        clipboardManager = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        return view
    }

    @SuppressLint("ClickableViewAccessibility")
    fun bindViews(view: View) {
        val binding = BottomSheetDialerBinding.bind(view)
        val mergeBinding = IncludeDialerPadBinding.bind(binding.root)

        phoneNumberEditText = binding.bottomSheetDialerPhoneNumberEditText
        phoneNumberConstraintView = binding.bottomSheetDialerPhoneNumberConstraint
        backArrowImgView = binding.bottomSheetDialerRemoveArrowImageView
        voicemailCountText = binding.bottomSheetVoicemailCountTextView
        voiceCallButton = binding.bottomSheetDialerVoiceCallButton
        voicemailButton = binding.bottomSheetDialerVoicemailImageView
        pullCall = binding.bottomSheetDialerPullCallImageView
        cancelIcon = binding.cancelIconInclude.closeIconView
        dialpadView = mergeBinding.dialerPadIncludeDialerPadView

        dialpadView.setDialerPadClickListener(this)
        dialpadView.setKeyTouchListeners(this)

        predialedNumber?.let { phoneNumberEditText.setText(it) }

        voicemailButton.setOnClickListener {
            viewModel.placeVoicemailCall()
            dismiss()
        }

        voiceCallButton.setOnClickListener {
            if(newCallType == RequestCodes.NewCall.NEW_CALL_NONE) {
                viewModel.placeCall(phoneNumberEditText.text.toString(), Enums.Sip.CallTypes.VOICE)
            }
            else {
                val participantInfo = ParticipantInfo(
                    numberToCall = phoneNumberEditText.text.toString().extractFirstNumber(),
                    metadata = phoneNumberEditText.text.toString().extractDtfmTone(),
                    dialingServiceType = Enums.Service.DialingServiceTypes.VOIP)

                if (newCallType == RequestCodes.NewCall.TRANSFER_REQUEST_CODE || newCallType == RequestCodes.NewCall.CONFERENCE_REQUEST_CODE) {
                    participantInfo.callType = Enums.Sip.CallTypes.VOICE
                }

                processCallInfo(participantInfo, Enums.Analytics.ScreenName.CONNECT_NEW_CALL_DIAL)
            }
            dismiss()
        }

        backArrowImgView.setOnClickListener{
            var cursorIndex: Int = phoneNumberEditText.selectionEnd
            while (cursorIndex > 0 && !CallUtil.isValidTextWatcherCharacter(phoneNumberEditText.text.toString()[cursorIndex - 1])) {
                cursorIndex--
            }
            if (cursorIndex > 0) {
                phoneNumberEditText.text.delete(cursorIndex - 1, cursorIndex)
            }
            updateBackArrowVisibilityState()
        }

        phoneNumberEditText.setOnLongClickListener {
            handlePhoneNumberEditTxtLongClick()
            true
        }

        pullCall.setOnClickListener {
            viewModel.pullCall()
        }

        backArrowImgView.setOnLongClickListener {
            phoneNumberEditText.text.clear()
            updateBackArrowVisibilityState()
            true
        }

        cancelIcon.setOnClickListener {
            dismiss()
        }

        phoneNumberEditText.addTextChangedListener(ExtensionEnabledPhoneNumberFormattingTextWatcher())
        phoneNumberEditText.showSoftInputOnFocus = false
    }

    private fun handlePhoneNumberEditTxtLongClick() {
        phoneNumberConstraintView.setBackgroundColor(resources.getColor(R.color.connectGrey02))
        val popupView = inflateCustomDialerDialog()
        configureAndShowPopupWindow(popupView)
        handlePopupViewActions(popupView)

    }

    private fun handlePopupViewActions(popupView: View) {
        val constraintCopyView = popupView.findViewById<ConstraintLayout>(R.id.constraint_copy_view)
        val constraintPasteView = popupView.findViewById<ConstraintLayout>(R.id.constraint_paste_view)
        val copyTv = popupView.findViewById<AppCompatTextView>(R.id.tv_copy)
        val addToContactsTv = popupView.findViewById<TextView>(R.id.tv_add_contacts)
        val addToExistingContactsTv = popupView.findViewById<TextView>(R.id.tv_add_existing_contact)

        if (phoneNumberEditText.text.isNotEmpty() && phoneNumberEditText.text.filter { it.isDigit() }.length >= 7){
            constraintPasteView.visibility = View.GONE
            constraintCopyView.visibility = View.VISIBLE
        }else{
            constraintCopyView.visibility = View.GONE
            constraintPasteView.visibility = View.VISIBLE
        }

        copyTv.setOnClickListener {
            val clipData = ClipData.newPlainText("text", phoneNumberEditText.text)
            clipboardManager.setPrimaryClip(clipData)
            mPopupWindow.dismiss()
        }

        constraintPasteView.setOnClickListener {
            val extractedText = extractValidContentFromClipBoard()
            if (extractedText.isNotEmpty()) {
                phoneNumberEditText.setText(extractedText)
                phoneNumberEditText.setSelection(phoneNumberEditText.text.length)
                phoneNumberEditText.inputType = InputType.TYPE_CLASS_PHONE
                updateBackArrowVisibilityState()
            }
            mPopupWindow.dismiss()
        }

        addToContactsTv.setOnClickListener {
            phoneNumberEditText.text?.toString()?.takeIf { it.isNotEmpty() }?.let { phoneNumber ->
                requireActivity().startActivity(
                    CreateBusinessContactActivity.newIntent(
                        requireActivity(),
                        phoneNumberEditText.text.toString()
                    )
                )
            }
            mPopupWindow.dismiss()
        }

        addToExistingContactsTv.setOnClickListener {
            phoneNumberEditText.text?.toString()?.takeIf { it.isNotEmpty() }?.let { phoneNumber ->
                BottomSheetSelectContactList(
                    intArrayOf(
                        Enums.Contacts.ContactTypes.CONNECT_SHARED,
                        Enums.Contacts.ContactTypes.CONNECT_PERSONAL
                    ),
                    phoneNumberEditText.text.toString(),
                    this
                )
                    .show(requireActivity().supportFragmentManager, null)
            }
            mPopupWindow.dismiss()
        }

    }

    private fun extractValidContentFromClipBoard(): String {
        val clipData = clipboardManager.primaryClip
        if (clipData != null && clipData.itemCount > 0) {
            val textFromClipboard = clipData.getItemAt(0).text
            val regex = Regex("[0-9#+()-,]+")
            val extractedChars = textFromClipboard?.toString()?.filter { it.toString().matches(regex) } ?: ""
            if (extractedChars.isNotEmpty()) {
                return extractedChars
            }
        }
        return ""
    }

    private fun inflateCustomDialerDialog(): View {
        return layoutInflater.inflate(R.layout.dialer_edit_txt_long_pressed_popup, null)
    }

    private fun configureAndShowPopupWindow(popupView: View){
        mPopupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )
        mPopupWindow.isFocusable = true
        mPopupWindow.isOutsideTouchable = true
        mPopupWindow.elevation = 8f


        mPopupWindow.width = (phoneNumberConstraintView.measuredWidth * POPUP_WINDOW_WIDTH_RATIO).toInt()

        mPopupWindow.setOnDismissListener {
            phoneNumberConstraintView.setBackgroundColor(resources.getColor(R.color.connectWhite))
        }
        mPopupWindow.showAsDropDown(phoneNumberConstraintView, POPUP_WINDOW_X_OFFSET, POPUP_WINDOW_Y_OFFSET, 0)
    }

    private fun updateBackArrowVisibilityState() {
        backArrowImgView.visibility = if (phoneNumberEditText.text?.isNotEmpty() == true) View.VISIBLE else View.GONE
    }

    private fun setVoicemailCount(voicemailCount: Int) {
        if (voicemailCount > 0) {
            voicemailCountText.text = voicemailCount.toString()
            voicemailCountText.visibility = View.VISIBLE
        } else {
            voicemailCountText.visibility = View.GONE
        }
    }

    private fun processCallInfo(
        participantInfo: ParticipantInfo,
        @Enums.Analytics.ScreenName.Screen analyticsScreenName: String
    ) {
        callManager.processParticipantInfo(
            requireActivity(),
            analyticsScreenName,
            participantInfo,
            null,
            compositeDisposable,
            this
        )
    }

    // --------------------------------------------------------------------------------------------
    // CallManager.ProcessCallInfoCallBack Methods
    // --------------------------------------------------------------------------------------------
    override fun onParticipantInfoProcessed(
        activity: Activity,
        @Enums.Analytics.ScreenName.Screen analyticsScreenName: String,
        participantInfo: ParticipantInfo,
        retrievalNumber: String?,
        compositeDisposable: CompositeDisposable
    ) {
        logManager.logToFile(
            Enums.Logging.STATE_INFO,
            R.string.log_message_success_with_message,
            activity.getString(R.string.log_message_processing_call, participantInfo.toString())
        )
        val callback = PermissionManager.PermissionGrantedCallback {
            val data = Intent()
            data.putExtra(
                Constants.EXTRA_PARTICIPANT_INFO,
                participantInfo
            )
            data.putExtra(
                Constants.EXTRA_RETRIEVAL_NUMBER,
                retrievalNumber
            )
            activity.setResult(Activity.RESULT_OK, data)
            activity.finish()
        }
        if (participantInfo.callType == Enums.Sip.CallTypes.VIDEO) {
            permissionManager.requestVideoCallPermission(
                activity,
                analyticsScreenName,
                callback,
                null
            )
        } else if (participantInfo.callType == Enums.Sip.CallTypes.VOICE) {
            permissionManager.requestVoiceCallPermission(
                activity,
                analyticsScreenName,
                callback
            )
        }
    }

    // --------------------------------------------------------------------------------------------


    override fun onKeyPressed(key: String) {
        phoneNumberEditText.text.insert(phoneNumberEditText.selectionEnd, key)
        viewModel.playDialerKeyPressedAudio(key)
        phoneNumberEditText.requestFocus()
        updateBackArrowVisibilityState()
    }

    override fun onVoiceMailPressed() {
        viewModel.placeVoicemailCall()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {
        (dialog as? BottomSheetDialog)?.behavior?.let { behavior ->
            when (motionEvent?.action) {
                MotionEvent.ACTION_DOWN -> {
                    behavior.isDraggable = false
                }
                MotionEvent.ACTION_UP -> {
                    behavior.isDraggable = true
                }
            }
        }

        return false
    }
}