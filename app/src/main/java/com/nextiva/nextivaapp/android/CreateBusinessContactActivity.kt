package com.nextiva.nextivaapp.android

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.nextiva.nextivaapp.android.constants.Constants
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.databinding.ActivityCreateBusinessContactBinding
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.db.model.EmailAddress
import com.nextiva.nextivaapp.android.db.model.PhoneNumber
import com.nextiva.nextivaapp.android.fragments.bottomsheets.BottomSheetDialog
import com.nextiva.nextivaapp.android.fragments.bottomsheets.BottomSheetShareContactConfirmation
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.ConversationRepository
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PlatformContactsRepository
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.SmsManagementRepository
import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager
import com.nextiva.nextivaapp.android.core.common.ui.ActiveCallBanner
import com.nextiva.nextivaapp.android.managers.interfaces.SettingsManager
import com.nextiva.nextivaapp.android.models.AvatarInfo
import com.nextiva.nextivaapp.android.models.CallLogEntry
import com.nextiva.nextivaapp.android.models.NextivaContact
import com.nextiva.nextivaapp.android.models.Voicemail
import com.nextiva.nextivaapp.android.util.ApplicationUtil.isNightModeEnabled
import com.nextiva.nextivaapp.android.util.CallUtil
import com.nextiva.nextivaapp.android.util.ContactDeleteHelper
import com.nextiva.nextivaapp.android.util.GsonUtil
import com.nextiva.nextivaapp.android.util.LogUtil
import com.nextiva.nextivaapp.android.util.ViewUtil
import com.nextiva.nextivaapp.android.util.extensions.orFalse
import com.nextiva.nextivaapp.android.view.AvatarView
import com.nextiva.nextivaapp.android.view.ConnectGroupAddItemView
import com.nextiva.nextivaapp.android.view.ConnectTextInputView
import com.nextiva.nextivaapp.android.viewmodels.CreateBusinessContactViewModel
import com.nextiva.pjsip.pjsip_lib.sipservice.SipCall
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class CreateBusinessContactActivity : BaseActivity() {
    private enum class State {
        ADD, EDIT
    }

    companion object {

        private const val MAX_ITEMS = 500
        fun newIntent(context: Context): Intent {
            return Intent(context, CreateBusinessContactActivity::class.java)
        }

        fun newIntent(context: Context, phoneNumberToAdd: String?): Intent {
            val intent = Intent(context, CreateBusinessContactActivity::class.java)

            phoneNumberToAdd?.let {
                intent.putExtra(Constants.EXTRA_PHONE_NUMBER, phoneNumberToAdd)
            }

            return intent
        }

        fun newIntent(context: Context, contact: NextivaContact): Intent {
            val intent = Intent(context, CreateBusinessContactActivity::class.java)
            intent.putExtra(Constants.EXTRA_NEXTIVA_CONTACT_JSON, GsonUtil.getJSON(contact))
            return intent
        }

        fun newIntent(context: Context, callLogEntry: CallLogEntry): Intent {
            val intent = Intent(context, CreateBusinessContactActivity::class.java)
            intent.putExtra(Constants.EXTRA_CALL_LOG_ENTRY, GsonUtil.getJSON(callLogEntry))
            return intent
        }

        fun newIntent(context: Context, voicemail: Voicemail): Intent {
            val intent = Intent(context, CreateBusinessContactActivity::class.java)
            intent.putExtra(Constants.EXTRA_VOICEMAIL, GsonUtil.getJSON(voicemail))
            return intent
        }

        fun newIntent(
            context: Context,
            contact: NextivaContact,
            phoneNumberToAdd: String?
        ): Intent {
            val intent = Intent(context, CreateBusinessContactActivity::class.java)
            intent.putExtra(Constants.EXTRA_NEXTIVA_CONTACT_JSON, GsonUtil.getJSON(contact))

            phoneNumberToAdd?.let {
                intent.putExtra(Constants.EXTRA_PHONE_NUMBER, phoneNumberToAdd)
            }

            return intent
        }
    }

    @Inject
    lateinit var sharedPreferencesManager: SharedPreferencesManager
    @Inject
    lateinit var dbManager: DbManager
    @Inject
    lateinit var platformContactsRepository: PlatformContactsRepository
    @Inject
    lateinit var conversationRepository: ConversationRepository
    @Inject
    lateinit var smsManagementRepository: SmsManagementRepository
    @Inject
    lateinit var settingsManager: SettingsManager

    private lateinit var toolbar: Toolbar
    private lateinit var avatarView: AvatarView
    private lateinit var sharedSwitch: SwitchCompat
    private lateinit var firstNameTextInput: ConnectTextInputView
    private lateinit var lastNameTextInput: ConnectTextInputView
    private lateinit var companyNameTextInput: ConnectTextInputView
    private lateinit var faxNumberTextInput: ConnectTextInputView
    private lateinit var websiteTextInput: ConnectTextInputView
    private lateinit var descriptionTextInput: ConnectTextInputView
    private lateinit var jobTitleTextInput: ConnectTextInputView
    private lateinit var departmentTextInput: ConnectTextInputView
    private lateinit var saveButton: TextView
    private lateinit var phoneGroupItem: ConnectGroupAddItemView
    private lateinit var emailGroupItem: ConnectGroupAddItemView
    private lateinit var dateGroupItem: ConnectGroupAddItemView
    private lateinit var socialProfileGroupItem: ConnectGroupAddItemView
    private lateinit var addressGroupItem: ConnectGroupAddItemView
    private lateinit var backArrowView: RelativeLayout
    private lateinit var titleTextView: TextView
    private lateinit var deleteButtonLayout: RelativeLayout
    private lateinit var activeCallBar: ComposeView
    private var isKeyboardVisible = false

    private lateinit var state: State

    private val onValueChangedTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            validateForm()
        }

        override fun afterTextChanged(editable: Editable) {}
    }

    private val onValueChanged: () -> Unit = { viewModel.triggerValidation() }

    private val activeCallObserver = Observer<SipCall?> { sipCall ->
        if (sipCall != null) {
            MainScope().launch(Dispatchers.IO) {
                val contact = sipCall.participantInfoList.firstOrNull()?.numberToCall?.let { viewModel.getContactFromPhoneNumber(it) }

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

    private val removeEditTextFocusCallback: () -> Unit = {
        firstNameTextInput.clearFocus()
        lastNameTextInput.clearFocus()
        companyNameTextInput.clearFocus()
        phoneGroupItem.clearAllFocus()
        emailGroupItem.clearAllFocus()
    }

    private lateinit var viewModel: CreateBusinessContactViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel =
            ViewModelProvider(this)[CreateBusinessContactViewModel::class.java]

        var contact: NextivaContact? = null
        state = State.ADD

        intent.getStringExtra(Constants.EXTRA_NEXTIVA_CONTACT_JSON)?.let {
            state = State.EDIT
            contact = GsonUtil.getObject(NextivaContact::class.java, it)
            viewModel.nextivaContact = contact
        }

        setContentView(bindViews())
        setSupportActionBar(toolbar)

        viewModel.activeCallLiveData.observe(this, activeCallObserver)

        contact?.let {
            setContactValues(it)
        }

        intent.getStringExtra(Constants.EXTRA_PHONE_NUMBER)
            ?.let { number -> phoneGroupItem.addNewPhoneNumber(number) }
        intent.getStringExtra(Constants.EXTRA_CALL_LOG_ENTRY)
            ?.let { setCallLogEntryValues(GsonUtil.getObject(CallLogEntry::class.java, it)) }
        intent.getStringExtra(Constants.EXTRA_VOICEMAIL)
            ?.let { setVoicemailValues(GsonUtil.getObject(Voicemail::class.java, it)) }

        titleTextView.text =
            if (contact == null) getString(R.string.connect_create_contact_title) else getString(R.string.connect_edit_contact_title)

        val onBackPressedCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                confirmDiscardChanges()
            }
        }

        backArrowView.setOnClickListener {
            LogUtil.d(
                "CreateBusinessContactActivity",
                "backArrowView isKeyboardVisible: $isKeyboardVisible"
            )
            if (isKeyboardVisible) {
                if (currentFocus != null) {
                    ViewUtil.hideKeyboard(currentFocus!!)
                }
            } else {
                onBackPressedDispatcher.onBackPressed()
            }
        }

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        setStatusBarColor(ContextCompat.getColor(this, R.color.connectGrey01))

        avatarView.setAvatar(
            AvatarInfo.Builder()
                .setFontAwesomeIconResId(R.string.fa_user)
                .setDisplayName(contact?.displayName)
                .isConnect(true)
                .build()
        )

        validateForm()

        viewModel.dataValidation.observe(this) {
            validateForm()
        }

        val isKeyboardVisibleCallback: (Boolean) -> Unit = { isKeyboardVisible = it }
        ViewUtil.checkIfKeyboardIsShowing(this, isKeyboardVisibleCallback)
    }

//    private fun hasInputEntered(): Boolean {
//        return firstNameTextInput.text.isNotEmpty() ||
//                lastNameTextInput.text.isNotEmpty() ||
//                companyNameTextInput.text.isNotEmpty() ||
//                phoneGroupItem.getPhoneNumbers()?.isNotEmpty() == true ||
//                emailGroupItem.getEmails()?.isNotEmpty() == true
//    }

    internal fun confirmDiscardChanges() {
        LogUtil.d("CreateBusinessContactActivity", "confirmDiscardChanges")
        BottomSheetDialog(
            if (state == State.ADD) {
                getString(R.string.connect_create_contact_discard_changes_dialog_title)
            } else {
                getString(R.string.connect_create_contact_discard_changes_edit_dialog_title)
            },
            if (state == State.ADD) {
                getString(R.string.connect_create_contact_discard_changes_dialog_message)
            } else {
                getString(R.string.connect_create_contact_discard_changes_edit_dialog_message)
            },
            getString(R.string.connect_create_contact_discard_changes_dialog_primary),
            getString(R.string.connect_create_contact_discard_changes_dialog_secondary),
            ContextCompat.getColor(this, R.color.connectPrimaryRed),
            ContextCompat.getColor(this, R.color.connectSecondaryDarkBlue),
            ContextCompat.getColor(this, R.color.connectGrey03)
        ) {
            finish()
        }.show(supportFragmentManager, null)
    }

    private fun passesEmailAndPhoneValidation(): Boolean {
        val phoneNumbers = phoneGroupItem.getPhoneNumbers()
        val emailAddresses = emailGroupItem.getEmails()

        if (phoneGroupItem.checkDuplicates().orFalse() ||
            emailGroupItem.checkDuplicates().orFalse() ||
            socialProfileGroupItem.checkDuplicates().orFalse() ||
            (phoneNumbers.isNullOrEmpty() && emailAddresses.isNullOrEmpty())
        ) {
            return false
        }

        phoneNumbers?.filter { !it.number?.isBlank().orFalse() }
            ?.forEach {
            if (!Patterns.PHONE.matcher(it.number as CharSequence).matches() &&
                !Patterns.PHONE.matcher(
                    it.number?.replace("(", "")
                        ?.replace(")", "")
                        ?.replace("N", "")
                        ?.replace("+", "")
                        ?.replace(",", "")
                        ?.replace("-", "")
                        ?.replace("#", "") as CharSequence
                ).matches()
            ) {
                return false
            }
        }

        emailAddresses?.filter { !it.address?.isBlank().orFalse() }
            ?.forEach {
                if (!Patterns.EMAIL_ADDRESS.matcher(it.address as CharSequence).matches()) {
                    return false
                }
            }

        return true
    }

    fun validateForm() {
        saveButton.apply {
            if (passesEmailAndPhoneValidation()) {
                isEnabled = true
                backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        this@CreateBusinessContactActivity,
                        R.color.connectPrimaryBlue
                    )
                )
                setTextColor(
                    ContextCompat.getColor(
                        this@CreateBusinessContactActivity,
                        R.color.connectWhite
                    )
                )

            } else {
                isEnabled = false
                backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        this@CreateBusinessContactActivity,
                        R.color.connectGrey03
                    )
                )
                setTextColor(
                    ContextCompat.getColor(
                        this@CreateBusinessContactActivity,
                        R.color.connectPrimaryGrey
                    )
                )
            }
        }
    }

    fun bindViews(): View {
        val binding = ActivityCreateBusinessContactBinding.inflate(layoutInflater)

        toolbar = binding.createBusinessContactToolbar
        backArrowView = binding.backArrowInclude.backArrowView
        titleTextView = binding.titleTextView
        avatarView = binding.createBusinessContactAvatarView
        sharedSwitch = binding.createBusinessContactSharedSwitch
        saveButton = binding.createBusinessContactSaveButton
        phoneGroupItem = binding.createBusinessContactPhoneGroupItem
        emailGroupItem = binding.createBusinessContactEmailGroupItem
        dateGroupItem = binding.createBusinessContactDateGroupItem
        socialProfileGroupItem = binding.createBusinessContactSocialProfileGroupItem
        addressGroupItem = binding.createBusinessContactAddressGroupItem
        firstNameTextInput = binding.createBusinessContactFirstName
        lastNameTextInput = binding.createBusinessContactLastName
        companyNameTextInput = binding.createBusinessContactCompanyName
        faxNumberTextInput = binding.createBusinessContactFaxNumber
        websiteTextInput = binding.createBusinessContactWebsite
        descriptionTextInput = binding.createBusinessContactDescription
        jobTitleTextInput = binding.createBusinessContactJobTitle
        departmentTextInput = binding.createBusinessContactDepartment
        deleteButtonLayout = binding.deleteButtonInclude.buttonIconView
        activeCallBar = binding.activeCallToolbar

        faxNumberTextInput.setInputType(InputType.TYPE_CLASS_PHONE)
        firstNameTextInput.setTextWatcher(onValueChangedTextWatcher)
        lastNameTextInput.setTextWatcher(onValueChangedTextWatcher)
        companyNameTextInput.setTextWatcher(onValueChangedTextWatcher)
        jobTitleTextInput.setTextWatcher(onValueChangedTextWatcher)
        departmentTextInput.setTextWatcher(onValueChangedTextWatcher)
        phoneGroupItem.setup(MAX_ITEMS, onValueChanged, removeEditTextFocusCallback)
        emailGroupItem.setup(MAX_ITEMS, onValueChanged, removeEditTextFocusCallback)
        socialProfileGroupItem.setup(MAX_ITEMS, onValueChanged, removeEditTextFocusCallback)
        dateGroupItem.setup(4, onValueChanged, removeEditTextFocusCallback)
        addressGroupItem.setup(MAX_ITEMS, onValueChanged, removeEditTextFocusCallback)

        sharedSwitch.setOnCheckedChangeListener { _, _ ->
            if (sharedSwitch.isChecked && !sharedPreferencesManager.getBoolean(
                    SharedPreferencesManager.CONNECT_CONTACT_SHARED_DIALOG_DONT_SHOW,
                    false
                )
            ) {
                if (viewModel.nextivaContact == null || viewModel.nextivaContact?.contactType != Enums.Contacts.ContactTypes.CONNECT_SHARED) {
                    BottomSheetShareContactConfirmation { shared ->
                        if (!shared) {
                            sharedSwitch.isChecked = false
                        }
                    }.show(supportFragmentManager, null)
                }
            }

            validateForm()
        }

        saveButton.setOnClickListener {
            removeEditTextFocusCallback()

            val nextivaContact = viewModel.nextivaContact ?: NextivaContact()

            nextivaContact.contactType =
                if (sharedSwitch.isChecked) Enums.Contacts.ContactTypes.CONNECT_SHARED else Enums.Contacts.ContactTypes.CONNECT_PERSONAL
            nextivaContact.firstName = firstNameTextInput.text
            nextivaContact.lastName = lastNameTextInput.text
            nextivaContact.company = companyNameTextInput.text
            nextivaContact.department = departmentTextInput.text
            nextivaContact.title = jobTitleTextInput.text
            nextivaContact.website = websiteTextInput.text.ifBlank { null }
            nextivaContact.description = descriptionTextInput.text.ifBlank { null }

            // Phones

            val phoneNumbers: ArrayList<PhoneNumber> = ArrayList()
            phoneGroupItem.getPhoneNumbers()
                ?.filter { !it.number?.isBlank().orFalse() }
                ?.let { numbers -> phoneNumbers.addAll(numbers) }

            if (faxNumberTextInput.text.isNotBlank()) {
                phoneNumbers.add(
                    PhoneNumber(
                        Enums.Contacts.PhoneTypes.FAX,
                        faxNumberTextInput.text
                    )
                )
            }
            nextivaContact.phoneNumbers = phoneNumbers

            // Emails

            nextivaContact.emailAddresses = ArrayList<EmailAddress>().apply {
                emailGroupItem.getEmails()
                    ?.filter { !it.address?.isBlank().orFalse() }
                    ?.let { emails ->
                        this.addAll(emails)
                    }
            }

            nextivaContact.dates = dateGroupItem.getDates()
            nextivaContact.socialMediaAccounts = socialProfileGroupItem.getSocialProfiles()
            nextivaContact.addresses = addressGroupItem.getAddresses()

            when (state) {
                State.ADD -> {
                    viewModel.saveContact(nextivaContact) {
                        showCustomToast()
                        setResult(RESULT_OK)
                        finish()
                    }
                }

                State.EDIT -> {
                    viewModel.patchContact(nextivaContact) {
                        showCustomToast()
                        setResult(Activity.RESULT_OK)
                        finish()
                    }
                }
            }
        }

        if (state == State.EDIT) {
            deleteButtonLayout.visibility = View.VISIBLE
            deleteButtonLayout.setOnClickListener {
                viewModel.nextivaContact?.let { contact ->
                    deleteAction(contact)
                }
            }
            binding.deleteButtonInclude.buttonIconFontTextView.setIcon(
                getString(R.string.fa_trash_alt),
                Enums.FontAwesomeIconType.REGULAR
            )
        }

        overrideEdgeToEdge(binding.root)

        return binding.root
    }

    private fun showCustomToast() {
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.custom_toast, null)
        view.findViewById<TextView>(R.id.custom_toast_message)?.let {
            it.text = when(state) {
                State.ADD -> getString(R.string.connect_contacts_details_added)
                State.EDIT -> getString(R.string.connect_contacts_details_edited)
            }
        }
        val toast = Toast(this)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = view
        toast.show()
    }


    private fun deleteAction(contact: NextivaContact) {
        val helper = ContactDeleteHelper(
            this,
            supportFragmentManager,
            compositeDisposable,
            dbManager,
            platformContactsRepository,
            smsManagementRepository,
            conversationRepository
        )
        helper.deleteContactConfirmation(
            contact = contact,
            resetCallback = { },
            onSuccessCallback = { finish() }
        )
    }

    private fun setCallLogEntryValues(callLogEntry: CallLogEntry) {
        callLogEntry.createContactFirstName?.let { firstNameTextInput.setText(it) }
        callLogEntry.createContactLastName?.let { lastNameTextInput.setText(it) }
        callLogEntry.phoneNumber?.let { phoneGroupItem.addNewPhoneNumber(it) }
    }

    private fun setVoicemailValues(voicemail: Voicemail) {
        voicemail.getCreateContactFirstName()?.let { firstNameTextInput.setText(it) }
        voicemail.getCreateContactLastName()?.let { lastNameTextInput.setText(it) }
        voicemail.address?.let { phoneGroupItem.addNewPhoneNumber(it) }
    }

    private fun setContactValues(contact: NextivaContact) {
        contact.firstName?.let { firstNameTextInput.setText(it) }
        contact.lastName?.let { lastNameTextInput.setText(it) }
        contact.company?.let { companyNameTextInput.setText(it) }
        phoneGroupItem.setPhoneNumbers(ArrayList(contact.phoneNumbers.orEmpty()))
        emailGroupItem.setEmails(ArrayList(contact.emailAddresses.orEmpty()))
        contact.title?.let { jobTitleTextInput.setText(it) }
        contact.department?.let { departmentTextInput.setText(it) }
        contact.phoneNumbers?.firstOrNull { it.type == Enums.Contacts.PhoneTypes.FAX }?.number?.let {
            val phoneNumberFormatNumberDefaultCountry =
                CallUtil.phoneNumberFormatNumberDefaultCountry(it)
            faxNumberTextInput.setText(
                if (TextUtils.isEmpty(phoneNumberFormatNumberDefaultCountry)) {
                    it
                } else {
                    phoneNumberFormatNumberDefaultCountry
                }
            )
        }
        contact.website?.let { websiteTextInput.setText(it) }
        contact.description?.let { descriptionTextInput.setText(it) }
        dateGroupItem.setDates(ArrayList(contact.dates.orEmpty()))
        socialProfileGroupItem.setSocialProfiles(ArrayList(contact.socialMediaAccounts.orEmpty()))
        addressGroupItem.setAddresses(ArrayList(contact.addresses.orEmpty()))

        if (contact.contactType == Enums.Contacts.ContactTypes.CONNECT_SHARED) {
            sharedSwitch.isEnabled = false
            sharedSwitch.isChecked = true

        } else if (contact.contactType == Enums.Contacts.ContactTypes.CONNECT_PERSONAL) {
            sharedSwitch.isEnabled = true
            sharedSwitch.isChecked = false
        }

        val allowDelete = (contact.contactType == Enums.Contacts.ContactTypes.CONNECT_PERSONAL) ||
                mSessionManager.hasContactManagementPrivilege()
        deleteButtonLayout.visibility = if (allowDelete) View.VISIBLE else View.GONE
    }
}