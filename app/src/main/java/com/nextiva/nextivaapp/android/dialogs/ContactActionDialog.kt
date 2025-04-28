package com.nextiva.nextivaapp.android.dialogs

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.nextiva.nextivaapp.android.CreateBusinessContactActivity
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.DialogContactActionDetailListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.DialogContactActionHeaderListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.DialogContactActionListItem
import com.nextiva.nextivaapp.android.constants.Constants
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.constants.Enums.Contacts.ContactTypes
import com.nextiva.nextivaapp.android.databinding.DialogContactLongClickedBinding
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.db.model.PhoneNumber
import com.nextiva.nextivaapp.android.managers.NextivaSessionManager
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.ConversationRepository
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PlatformContactsRepository
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.SmsManagementRepository
import com.nextiva.nextivaapp.android.managers.interfaces.DialogManager
import com.nextiva.nextivaapp.android.managers.interfaces.IntentManager
import com.nextiva.nextivaapp.android.models.NextivaContact
import com.nextiva.nextivaapp.android.util.ContactDeleteHelper
import com.nextiva.nextivaapp.android.util.extensions.extractFirstNumber
import com.nextiva.nextivaapp.android.util.extensions.orFalse
import com.nextiva.nextivaapp.android.util.extensions.removeCountryCode
import com.nextiva.nextivaapp.android.viewmodels.ContactActionDialogViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@AndroidEntryPoint
class ContactActionDialog: BaseDialog() {

    companion object {
        const val CONTACT_ACTION_DIALOG_RESULT = "CONTACT_ACTION_DIALOG_RESULT"
        const val NEXTIVA_CONTACT = "NEXTIVA_CONTACT"
        const val PHONE_NUMBER = "PHONE_NUMBER"
        const val FROM_LONG_PRESS = "FROM_LONG_PRESS"
        private const val PHONE_ID = "PHONE_ID"
        private const val IS_IMPORTING = "IS_IMPORTING"

        fun newInstance(nextivaContact: NextivaContact?,
                        phoneId: Long? = null,
                        phoneNumber: String? = null,
                        fromLongPress: Boolean = false,
                        isImporting: Boolean = false) : ContactActionDialog {
            return ContactActionDialog().apply {
                arguments = Bundle().apply {
                    nextivaContact?.let { putSerializable(NEXTIVA_CONTACT, nextivaContact) }
                    phoneId?.let { putLong(PHONE_ID, phoneId) }
                    phoneNumber?.let { putString(PHONE_NUMBER, phoneNumber) }
                    putBoolean(FROM_LONG_PRESS, fromLongPress)
                    putBoolean(IS_IMPORTING, isImporting)
                }
            }
        }
    }

    @Inject
    lateinit var intentManager: IntentManager
    @Inject
    lateinit var sessionManager: NextivaSessionManager
    @Inject
    lateinit var dialogManager: DialogManager
    @Inject
    lateinit var dbManager: DbManager
    @Inject
    lateinit var platformContactsRepository: PlatformContactsRepository
    @Inject
    lateinit var conversationRepository: ConversationRepository
    @Inject
    lateinit var smsManagementRepository: SmsManagementRepository

    private val compositeDisposable = CompositeDisposable()

    private lateinit var viewModel: ContactActionDialogViewModel

    private lateinit var recyclerView: RecyclerView

    private lateinit var loadingLayout: View

    private var nextivaContact : NextivaContact? = null

    private var phoneId: Long? = null

    private var phoneNumber: String? = null

    private val baseListItemObserver = Observer<ArrayList<BaseListItem>> { listItems ->
        adapter?.updateList(listItems)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[ContactActionDialogViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_contact_long_clicked, container, false)

        nextivaContact = arguments?.getSerializable(NEXTIVA_CONTACT) as NextivaContact?
        phoneId = arguments?.getLong(PHONE_ID)
        phoneNumber = arguments?.getString(PHONE_NUMBER)

        view?.let { bindViews(view) }

        recyclerView.adapter = adapter
        viewModel.nextivaContact = nextivaContact
        viewModel.phoneId = phoneId
        viewModel.isImporting = arguments?.getBoolean(IS_IMPORTING).orFalse()
        viewModel.fromLongPress = arguments?.getBoolean(FROM_LONG_PRESS).orFalse()
        viewModel.getBaseListItemsLiveData().observe(this, baseListItemObserver)
        viewModel.refreshListItems()

        nextivaContact?.userId.let { userId ->
            dbManager.getPresenceLiveDataFromContactTypeId(userId).observe(this) { dbPresence ->
                dbPresence?.let {
                    viewModel.updatePresence(it)
                    adapter?.notifyItemChanged(0)
                }
            }
        }

        if (nextivaContact == null && phoneNumber != null) {
            nextivaContact = NextivaContact(phoneNumber, ContactTypes.UNKNOWN)
            viewModel.nextivaContact = nextivaContact
            viewModel.refreshListItems()
        }

        viewModel.loading.observe(viewLifecycleOwner) { loading ->
            loadingLayout.visibility = if(loading) View.VISIBLE else View.GONE
        }

        viewModel.groupId.observe(viewLifecycleOwner) { groupId ->
            requireActivity().startActivity(viewModel.getSmsIntent(groupId.first))
            dismiss()
            Log.d("ContactActionDialog", "[Fetched groupId]: $groupId")
        }

        return view
    }


    fun bindViews(view: View) {
        val binding = DialogContactLongClickedBinding.bind(view)

        recyclerView = binding.dialogContactLongClickedRecyclerView
        loadingLayout = binding.loadingLayout

        binding.editButton.setOnClickListener {
            viewModel.nextivaContact?.let { contact ->
                activity?.let { activity ->
                    startActivity(CreateBusinessContactActivity.newIntent(activity, contact))
                    dismiss()
                }
            }
        }

        binding.deleteButton.setOnClickListener {
            deleteAction(viewModel.nextivaContact)
        }

        val contactType = nextivaContact?.contactType
        val hideButtonGroup = (contactType == ContactTypes.CONNECT_USER ||
            contactType == ContactTypes.CONNECT_TEAM ||
                contactType == ContactTypes.CONNECT_CALL_FLOW ||
                contactType == ContactTypes.CONNECT_CALL_CENTERS ||
            contactType == ContactTypes.LOCAL ||
            phoneId != null || nextivaContact?.aliases?.lowercase()?.contains(Constants.Contacts.Aliases.XBERT_ALIASES) == true
        )
        val hideDeleteButton = ((contactType == ContactTypes.CONNECT_SHARED) && !sessionManager.hasContactManagementPrivilege())
        val barColor = if (hideButtonGroup) R.color.connectWhite else R.color.connectGrey01

        binding.dialogContactLongClickedView.setBackgroundColor(ContextCompat.getColor(requireContext(), barColor))
        binding.buttonGroupLayout.visibility = if (hideButtonGroup) View.GONE else View.VISIBLE
        binding.deleteButtonLayout.visibility = if (hideDeleteButton) View.GONE else View.VISIBLE
    }

    private fun deleteAction(contact: NextivaContact?) {
        if (contact == null) return
        val fragmentManager = parentFragmentManager
        val context = activity ?: return
        val helper = ContactDeleteHelper(
            context,
            fragmentManager,
            compositeDisposable,
            dbManager,
            platformContactsRepository,
            smsManagementRepository,
            conversationRepository
        )
        helper.deleteContactConfirmation(
            contact = contact,
            resetCallback = { },
            onSuccessCallback = { dismiss() }
        )
    }

    private fun performAction(action: Int?, value: String, phoneNumber: PhoneNumber? = null) {

        var numberToDial = value.extractFirstNumber().orEmpty() // cleaned number to dial
        var fullNumberToDial = phoneNumber?.number // includes DTFM, extensions, trash...

        if(nextivaContact?.contactType == ContactTypes.CONNECT_USER) {
            numberToDial = numberToDial.removeCountryCode().orEmpty()
            fullNumberToDial = fullNumberToDial?.removeCountryCode()
        }

        when (action) {
            Enums.Platform.ConnectContactLongClickAction.MAKE_A_CALL -> {
                viewModel.placeCall(
                    requireActivity(),
                    Enums.Analytics.ScreenName.CONNECT_CONTACT_ACTION_DIALOG,
                    phoneNumber?.strippedNumber ?: numberToDial,
                    fullNumberToDial ?: numberToDial
                )
                dismiss()
            }
            Enums.Platform.ConnectContactLongClickAction.DIAL_EXTENSION -> {
                viewModel.placeCall(
                    requireActivity(),
                    Enums.Analytics.ScreenName.CONNECT_CONTACT_ACTION_DIALOG,
                    phoneNumber?.extension ?: numberToDial,
                    phoneNumber?.extension ?: numberToDial
                )
                dismiss()
            }
            Enums.Platform.ConnectContactLongClickAction.SEND_A_TEXT -> {
                val contact = nextivaContact ?: return
                if (sessionManager.isSmsLicenseEnabled && (sessionManager.isSmsProvisioningEnabled || viewModel.sessionManager.isTeamSmsEnabled)) {
                    if (viewModel.sessionManager.canSendSms()) {
                        if (contact.contactType == ContactTypes.CONNECT_TEAM) {
                            if (viewModel.isTeamSmsEnabled(contact)) {
                                viewModel.fetchGroupId(numberToDial)
                                return
                            } else {
                                dialogManager.showDialog(
                                    requireContext(),
                                    "",
                                    getString(R.string.contact_detail_team_sms_not_supported),
                                    getString(R.string.general_ok)
                                ) { _, _ -> }
                            }

                        } else {
                            viewModel.fetchGroupId(numberToDial)
                            return
                        }

                    } else {
                        intentManager.sendPersonalSMS(requireActivity(), Enums.Analytics.ScreenName.CONNECT_CONTACT_ACTION_DIALOG, value, "", "", "")
                    }
                } else {
                    val dialogTitle = if (!sessionManager.isSmsLicenseEnabled) getString(R.string.invalid_license_dialog_title) else getString(R.string.invalid_provisioning_dialog_title)
                    val dialogBody = if (!sessionManager.isSmsLicenseEnabled) getString(R.string.invalid_license_dialog_body) else getString(R.string.invalid_provisioning_dialog_body)

                    dialogManager.showDialog(requireContext(),
                            dialogTitle,
                            dialogBody,
                            getString(R.string.general_ok)
                    ) { _, _ -> }
                }

                dismiss()
            }
            Enums.Platform.ConnectContactLongClickAction.COPY_TO_CLIPBOARD -> {
                val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText(getString(R.string.general_copied_to_clipboard), value)
                clipboard.setPrimaryClip(clip)
                dismiss()
            }
            Enums.Platform.ConnectContactLongClickAction.BLOCK_NUMBER -> {
                if (sessionManager.isBlockNumberForCallingEnabled) {
                    setFragmentResult(
                        CONTACT_ACTION_DIALOG_RESULT,
                        Bundle().apply { putString(PHONE_NUMBER, numberToDial) }
                    )
                    dismiss()
                }
            }
        }
    }

    override fun onDialogContactActionDetailListItemClicked(listItem: DialogContactActionDetailListItem) {
        super.onDialogContactActionDetailListItemClicked(listItem)
        performAction(listItem.action, listItem.subtitle, listItem.data as? PhoneNumber)
    }

    override fun onDialogContactActionListItemClicked(listItem: DialogContactActionListItem) {
        super.onDialogContactActionListItemClicked(listItem)

        if (listItem.isExpandable) {
            viewModel.refreshListItems()

        } else {
            performAction(listItem.action, viewModel.getValueForAction(listItem.action), listItem.data as? PhoneNumber)
        }
    }

    override fun onDialogContactActionHeaderListItemClicked(listItem: DialogContactActionHeaderListItem) {
        super.onDialogContactActionHeaderListItemClicked(listItem)
        arguments?.let { bundle ->
            parentFragmentManager.setFragmentResult(CONTACT_ACTION_DIALOG_RESULT, bundle)
        }
        dismiss()
    }
}