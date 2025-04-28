package com.nextiva.nextivaapp.android.fragments.bottomsheets

import android.graphics.Typeface
import android.os.Bundle
import android.text.InputType
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.constants.Constants
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.databinding.BottomSheetSetPresenceBinding
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.managers.FormatterManager
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PresenceRepository
import com.nextiva.nextivaapp.android.managers.interfaces.CalendarManager
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.util.ViewUtil
import com.nextiva.nextivaapp.android.util.extensions.makeClearableEditText
import com.nextiva.nextivaapp.android.util.extensions.nullIfEmpty
import com.nextiva.nextivaapp.android.view.ImeKeyEventEditText
import com.nextiva.nextivaapp.android.view.ImeKeyEventEditText.OnImeKeyEventEditTextListener
import com.nextiva.nextivaapp.android.view.fontawesome.FontTextView
import com.nextiva.nextivaapp.android.view.textwatchers.LengthLimitTextWatcher
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BottomSheetSetPresence : BaseBottomSheetDialogFragment(), OnImeKeyEventEditTextListener {

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var presenceRepository: PresenceRepository

    @Inject
    lateinit var dbManager: DbManager

    @Inject
    lateinit var calendarManager: CalendarManager

    private lateinit var parentLayout: LinearLayout
    private lateinit var cancelIcon: RelativeLayout
    private lateinit var statusText: ImeKeyEventEditText
    private lateinit var automaticLayout: LinearLayout
    private lateinit var automaticIcon: FontTextView
    private lateinit var automaticText: TextView
    private lateinit var availableLayout: LinearLayout
    private lateinit var availableIcon: FontTextView
    private lateinit var availableText: TextView
    private lateinit var busyLayout: LinearLayout
    private lateinit var busyIcon: FontTextView
    private lateinit var busyText: TextView
    private lateinit var doNotDisturbLayout: LinearLayout
    private lateinit var doNotDisturbText: TextView
    private lateinit var doNotDisturbIcon: FontTextView
    private lateinit var doNotDisturbExpiryTimeText: TextView
    private lateinit var awayLayout: LinearLayout
    private lateinit var awayIcon: FontTextView
    private lateinit var awayText: TextView
    private lateinit var offlineLayout: LinearLayout
    private lateinit var offlineIcon: FontTextView
    private lateinit var offlineText: TextView

    private val onEditorActionListener = TextView.OnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            saveStatusMessage()
            statusText.clearFocus()
            parentLayout.requestFocus()
            ViewUtil.hideKeyboard(statusText)

            return@OnEditorActionListener true
        }

        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_set_presence, container, false)
        view?.let { bindViews(view) }

        dbManager.ownConnectPresenceLiveData.observe(this) {
            setSelectedText()
        }

        return view
    }

    private fun bindViews(view: View) {
        val binding = BottomSheetSetPresenceBinding.bind(view)
        parentLayout = binding.bottomSheetSetPresenceLayout
        cancelIcon = binding.cancelIconInclude.closeIconView
        statusText = binding.bottomSheetSetPresenceTextInput
        automaticLayout = binding.bottomSheetSetPresenceAutomatic
        automaticIcon = binding.bottomSheetSetPresenceAutomaticIcon
        automaticText = binding.bottomSheetSetPresenceAutomaticText
        availableLayout = binding.bottomSheetSetPresenceAvailable
        availableIcon = binding.bottomSheetSetPresenceAvailableIcon
        availableText = binding.bottomSheetSetPresenceAvailableText
        busyLayout = binding.bottomSheetSetPresenceBusy
        busyIcon = binding.bottomSheetSetPresenceBusyIcon
        busyText = binding.bottomSheetSetPresenceBusyText
        doNotDisturbLayout = binding.bottomSheetSetPresenceDoNotDisturb
        doNotDisturbText = binding.bottomSheetSetPresenceDoNotDisturbText
        doNotDisturbIcon = binding.bottomSheetSetPresenceDoNotDisturbIcon
        doNotDisturbExpiryTimeText = binding.bottomSheetSetPresenceDndExpiryTimeText
        awayLayout = binding.bottomSheetSetPresenceAway
        awayIcon = binding.bottomSheetSetPresenceAwayIcon
        awayText = binding.bottomSheetSetPresenceAwayText
        offlineLayout = binding.bottomSheetSetPresenceOffline
        offlineIcon = binding.bottomSheetSetPresenceOfflineIcon
        offlineText = binding.bottomSheetSetPresenceOfflineText


        statusText.setOnImeKeyEventEditTextListener(this)
        statusText.imeOptions = EditorInfo.IME_ACTION_DONE
        statusText.setRawInputType(InputType.TYPE_CLASS_TEXT)
        statusText.setOnEditorActionListener(onEditorActionListener)
        statusText.makeClearableEditText()
        statusText.addTextChangedListener(LengthLimitTextWatcher(statusText, 128))

        cancelIcon.setOnClickListener { dismiss() }


        automaticLayout.setOnClickListener {
            presenceRepository.setPresence(
                Enums.Contacts.ConnectPresenceStates.AUTOMATIC,
                statusText.text.toString()
            )
        }
        availableLayout.setOnClickListener {
            presenceRepository.setPresence(
                Enums.Contacts.ConnectPresenceStates.ONLINE,
                statusText.text.toString()
            )
        }
        awayLayout.setOnClickListener {
            presenceRepository.setPresence(
                Enums.Contacts.ConnectPresenceStates.AWAY,
                statusText.text.toString()
            )
        }
        busyLayout.setOnClickListener {
            presenceRepository.setPresence(
                Enums.Contacts.ConnectPresenceStates.BUSY,
                statusText.text.toString()
            )
        }
        doNotDisturbLayout.setOnClickListener {
            val bottomSheetDNDFragment = BottomSheetSetDoNotDisturbDuration()
            val bundle = Bundle()
            bundle.putString(Constants.PRESENCE_STATUS_TEXT, statusText.text.toString())
            bottomSheetDNDFragment.arguments = bundle
            bottomSheetDNDFragment.show(parentFragmentManager, null)
            this.dismiss()
        }
        offlineLayout.setOnClickListener {
            presenceRepository.setPresence(
                Enums.Contacts.ConnectPresenceStates.OFFLINE,
                statusText.text.toString()
            )
        }

        setSelectedText()
    }

    private fun checkAndSetDndExpiryTime() {
        sessionManager.connectUserPresence?.let { presence ->
            when (presence.state) {
                Enums.Contacts.PresenceStates.CONNECT_DND -> {
                    presence.statusExpiryTime?.nullIfEmpty()?.let { statusExpiryTime ->
                        val formatterManager = FormatterManager.getInstance()
                        val formattedTime = formatterManager.getDndStatusExpiresTime(statusExpiryTime)

                        val humanReadableDndTime = context?.let { context ->
                            formattedTime?.let { timeInstant ->
                                formatterManager.format_humanReadableDndExpiryDateTime(context, calendarManager, timeInstant)
                            }
                        }

                        if (!humanReadableDndTime.isNullOrEmpty()) {
                            doNotDisturbExpiryTimeText.text = getString(R.string.set_do_not_disturb_bottom_sheet_duration_until_time, humanReadableDndTime)
                        }else{
                            doNotDisturbExpiryTimeText.text = getString(R.string.set_do_not_disturb_bottom_sheet_duration_until_status_change)
                        }

                        doNotDisturbExpiryTimeText.visibility = View.VISIBLE
                    }
                }
                else -> {
                    doNotDisturbExpiryTimeText.visibility = View.GONE
                }
            }

        }
    }

    private fun saveStatusMessage() {
        presenceRepository.setPresence(
            if (sessionManager.isConnectUserPresenceAutomatic) Enums.Contacts.ConnectPresenceStates.AUTOMATIC else
                when (sessionManager.connectUserPresence?.state) {
                    Enums.Contacts.PresenceStates.CONNECT_ONLINE,
                    Enums.Contacts.PresenceStates.CONNECT_ACTIVE -> Enums.Contacts.ConnectPresenceStates.ONLINE
                    Enums.Contacts.PresenceStates.CONNECT_AWAY,
                    Enums.Contacts.PresenceStates.CONNECT_BE_RIGHT_BACK -> Enums.Contacts.ConnectPresenceStates.AWAY
                    Enums.Contacts.PresenceStates.CONNECT_BUSY,
                    Enums.Contacts.PresenceStates.CONNECT_OUT_OF_OFFICE -> Enums.Contacts.ConnectPresenceStates.BUSY
                    Enums.Contacts.PresenceStates.CONNECT_DND -> Enums.Contacts.ConnectPresenceStates.DND
                    else -> Enums.Contacts.ConnectPresenceStates.OFFLINE
                }, statusText.text.toString()
        )
    }

    private fun setSelectedText() {
        automaticText.typeface = Typeface.DEFAULT
        automaticIcon.setIcon(R.string.fa_sync, Enums.FontAwesomeIconType.REGULAR)
        automaticIcon.setTextColor(ContextCompat.getColor(requireActivity(), R.color.connectGrey08))
        availableText.typeface = Typeface.DEFAULT
        setPresenceIconToDefault(availableIcon, R.color.connectPrimaryGreen, R.string.fa_check_circle, Enums.FontAwesomeIconType.SOLID)
        awayText.typeface = Typeface.DEFAULT
        setPresenceIconToDefault(awayIcon, R.color.connectPrimaryYellow, R.string.fa_clock, Enums.FontAwesomeIconType.SOLID)
        busyText.typeface = Typeface.DEFAULT
        setPresenceIconToDefault(busyIcon, R.color.connectPrimaryRed, R.string.fa_circle, Enums.FontAwesomeIconType.SOLID)
        offlineText.typeface = Typeface.DEFAULT
        setPresenceIconToDefault(offlineIcon, R.color.connectGrey10, R.string.fa_circle, Enums.FontAwesomeIconType.REGULAR)
        doNotDisturbText.typeface = Typeface.DEFAULT
        setPresenceIconToDefault(
            doNotDisturbIcon,
            R.color.connectPrimaryRed,
            R.string.fa_do_not_disturb,
            Enums.FontAwesomeIconType.SOLID
        )
        doNotDisturbExpiryTimeText.visibility = View.GONE
        sessionManager.connectUserPresence?.let { presence ->
            statusText.setText(presence.status ?: "")

            if (sessionManager.isConnectUserPresenceAutomatic) {
                automaticText.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                setIconSelected(automaticIcon)

            } else {
                when (presence.state) {
                    Enums.Contacts.PresenceStates.CONNECT_ACTIVE,
                    Enums.Contacts.PresenceStates.CONNECT_ONLINE -> {
                        availableText.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                        setIconSelected(availableIcon)
                    }
                    Enums.Contacts.PresenceStates.CONNECT_BE_RIGHT_BACK,
                    Enums.Contacts.PresenceStates.CONNECT_AWAY -> {
                        awayText.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                        setIconSelected(awayIcon)
                    }
                    Enums.Contacts.PresenceStates.CONNECT_DND -> {
                        doNotDisturbText.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                        setIconSelected(doNotDisturbIcon)
                        checkAndSetDndExpiryTime()
                    }
                    Enums.Contacts.PresenceStates.CONNECT_OUT_OF_OFFICE,
                    Enums.Contacts.PresenceStates.CONNECT_BUSY -> {
                        busyText.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                        setIconSelected(busyIcon)
                    }
                    Enums.Contacts.PresenceStates.CONNECT_OFFLINE,
                    Enums.Contacts.PresenceStates.CONNECT_AUTOMATIC -> {
                        offlineText.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                        setIconSelected(offlineIcon)
                    }
                    else -> {
                        automaticText.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                        setIconSelected(automaticIcon)
                    }
                }
            }
        }
    }

    private fun setPresenceIconToDefault(icon: FontTextView, colorId: Int, iconId: Int, iconType: Int) {
        icon.setIcon(iconId, iconType)
        icon.setTextColor(ContextCompat.getColor(requireActivity(), colorId))
        icon.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            resources.getDimension(R.dimen.connect_presence_icon)
        )
    }

    private fun setIconSelected(icon: FontTextView) {
        icon.setIcon(R.string.fa_check, Enums.FontAwesomeIconType.REGULAR)
        icon.setTextColor(ContextCompat.getColor(requireActivity(), R.color.connectGrey09))
        icon.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            resources.getDimension(R.dimen.material_text_menu)
        )
    }

    override fun onImeKeyEvent(ctrl: ImeKeyEventEditText?, keyCode: Int) {
        statusText.clearFocus()
        parentLayout.requestFocus()
        ViewUtil.hideKeyboard(statusText)
    }
}