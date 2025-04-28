package com.nextiva.nextivaapp.android.core.common.ui

import android.content.res.ColorStateList
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.FOCUS_DOWN
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.core.view.children
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.snackbar.Snackbar
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.constants.Constants
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.databinding.BottomSheetNewSmsBinding
import com.nextiva.nextivaapp.android.fragments.bottomsheets.BaseBottomSheetDialogFragment
import com.nextiva.nextivaapp.android.managers.interfaces.AnalyticsManager
import com.nextiva.nextivaapp.android.managers.interfaces.AvatarManager
import com.nextiva.nextivaapp.android.managers.interfaces.DialogManager
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.models.NextivaContact
import com.nextiva.nextivaapp.android.util.CallUtil
import com.nextiva.nextivaapp.android.util.extensions.withFontAwesomeDrawable
import com.nextiva.nextivaapp.android.util.fontawesome.FontDrawable
import com.nextiva.nextivaapp.android.view.ConnectMaxHeightScrollView
import com.nextiva.nextivaapp.android.view.fontawesome.FontTextView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BottomSheetSelectContacts(private var selectionInterface: BottomSheetSelectContactsInterface) : BaseBottomSheetDialogFragment(tintedAppBar = true), TextWatcher {

    companion object {
        const val MAX_CHIP_LINES_COUNT = 3
        const val CHIP_MARGINS_AND_PADDING = 20
        const val conversationType = Enums.Chats.ConversationTypes.SMS
        const val FRAGMENT_TAG_SEARCH = "search_fragment"

        fun newInstance(selectionInterface: BottomSheetSelectContactsInterface): BottomSheetSelectContacts {
            return BottomSheetSelectContacts(selectionInterface)
        }
    }

    @Inject
    lateinit var sessionManager: SessionManager
    @Inject
    lateinit var analyticsManager: AnalyticsManager
    @Inject
    lateinit var avatarManager: AvatarManager
    @Inject
    lateinit var dialogManager: DialogManager

    private lateinit var cancelIcon: RelativeLayout
    private lateinit var cancelIconFontTextView: FontTextView
    private lateinit var cancelSearch: FontTextView
    private lateinit var chipGroupScrollview: ConnectMaxHeightScrollView
    private lateinit var contactsChipGroup: ChipGroup
    private lateinit var editTextSearchBox: EditText
    private lateinit var toConstraintLayout: ConstraintLayout
    private lateinit var header: LinearLayout
    private lateinit var coordinatorLayout: CoordinatorLayout

    private lateinit var contactsListFragment: ContactsListFragment

    private var nextivaContactList = ArrayList<NextivaContact>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        contactsListFragment = ContactsListFragment()
        showFullHeight = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_new_sms, container, false)
        view?.let { bindViews(view) }

        editTextSearchBox.addTextChangedListener(this)

        cancelSearch.setOnClickListener {
            editTextSearchBox.editableText.clear()

            selectionInterface.selectedContacts.forEach { _ ->
                contactsChipGroup.children.forEach { chip ->
                    if (chip is Chip) {
                        contactsChipGroup.removeView(chip)
                    }
                }
            }
            selectionInterface.removeAllContacts()
            nextivaContactList.clear()

            mustShowIconX(false)
            setSearchBoxHint()
            updateIcon()
        }

        selectionInterface.addSelectedContact.observe(viewLifecycleOwner) {
            nextivaContactSelected(it)
            updateIcon()
        }

        selectionInterface.removeSelectedContact.observe(viewLifecycleOwner) {
            updateIcon()
        }

        cancelIcon.setOnClickListener {
            if (selectionInterface.selectedContactsCount() > 0) {
                activity?.let { activity ->
                    activity.startActivity(selectionInterface.nextIntent(activity))
                }
            }
            dismiss()
        }

        switchToSearchView()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contactsListFragment.setViewModel(selectionInterface)

        selectionInterface.selectedContacts.forEach { contact ->
            if (nextivaContactList.count { it.userId == contact.userId } == 0) {
                addMessageContact(contact)
            }
        }
    }

    private fun switchToSearchView() {
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.bottom_sheet_connect_new_sms_container, contactsListFragment, FRAGMENT_TAG_SEARCH)
        transaction.commit()
        childFragmentManager.executePendingTransactions()
        contactsListFragment.setViewModel(selectionInterface)
    }

    private fun updateIcon() {
        cancelIconFontTextView.setIcon(
            id = if (selectionInterface.selectedContactsCount() == 0) R.string.fa_times else R.string.fa_chevron_right,
            iconType = Enums.FontAwesomeIconType.SOLID)
    }

    private fun bindViews(view: View) {
        val binding = BottomSheetNewSmsBinding.bind(view)

        cancelIcon = binding.cancelIconInclude.closeIconView
        cancelIconFontTextView = binding.cancelIconInclude.closeIconFontTextView
        cancelSearch = binding.chatSmsSearchCancel
        contactsChipGroup = binding.chatSmsContactsInChatChipGroup
        editTextSearchBox = binding.editTextSearchBox
        toConstraintLayout = binding.toConstraintLayout
        chipGroupScrollview = binding.chipGroupScrollview
        header = binding.header
        coordinatorLayout = binding.bottomSheetConnectNewSmsCoordinator
    }

    private fun addMessageContact(nextivaContact: NextivaContact) {
        val chip: Chip = getContactChip(nextivaContact)
        if (!chipGroupScrollview.isMaxHeightSet) {
            val params = chipGroupScrollview.layoutParams as ViewGroup.MarginLayoutParams
            chipGroupScrollview.setMaxHeight(chip.chipMinHeight * MAX_CHIP_LINES_COUNT +
                    params.bottomMargin + params.topMargin + CHIP_MARGINS_AND_PADDING)
        }

        nextivaContactList.add(nextivaContact)
        selectionInterface.contactAdded(nextivaContact)

        contactsChipGroup.addView(chip, contactsChipGroup.childCount - 1)

        if (editTextSearchBox.text.toString().isNotEmpty()) {
            editTextSearchBox.setText("")
        }
        setSearchBoxHint()

        chipGroupScrollview.fullScroll(FOCUS_DOWN)
    }

    private fun setSearchBoxHint() {
        if (contactsChipGroup.childCount > 1) {
            editTextSearchBox.hint = ""
            editTextSearchBox.setText("")
        } else {
            editTextSearchBox.setHint(R.string.connect_new_chat_search_box_hint)
        }
    }

    private fun getContactChip(nextivaContact: NextivaContact?): Chip {
        val chip = Chip(context)
        context?.let{ context ->
            nextivaContact?.let { contact ->
                if (TextUtils.isEmpty(nextivaContact.userId) && (nextivaContact.allPhoneNumbers?.size
                        ?: 0) > 0
                ) {
                    nextivaContact.allPhoneNumbers?.firstOrNull()?.strippedNumber?.let { strippedNumber ->
                        chip.text = if (strippedNumber.length == 10) {
                            CallUtil.phoneNumberFormatNumberDefaultCountry(strippedNumber)
                        } else {
                            strippedNumber
                        }
                    }

                } else {
                    chip.text = contact.uiName
                }
            }

            chip.setChipBackgroundColorResource(R.color.connectGrey02)
            chip.setTextColor(ContextCompat.getColor(context, R.color.connectSecondaryDarkBlue))

            chip.closeIcon?.let {
                it.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(ContextCompat.getColor(context, R.color.connectGrey09), BlendModeCompat.SRC_ATOP)
            }

            chip.tag = nextivaContact
            chip.chipStrokeWidth = 1.0f
            chip.chipStrokeColor = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.connectGrey02))
            chip.setEnsureMinTouchTargetSize(false)

            nextivaContact?.let {
                val avatarInfo = it.avatarInfo
                if (it.contactType == Enums.Contacts.ContactTypes.CONNECT_TEAM){
                    avatarInfo.iconResId = R.drawable.avatar_team
                }
                avatarInfo.fontAwesomeIconResId = R.string.fa_user
                val bitmap = avatarManager.getBitmap(avatarInfo)
                chip.chipIcon = BitmapDrawable(context.resources, bitmap)
            }

            chip.isCloseIconVisible = true
            chip.setOnCloseIconClickListener {
                (chip.tag as? NextivaContact)?.let { contact ->
                    selectionInterface.removeContact(contact)
                    nextivaContactList.remove(contact)
                    contactsChipGroup.removeView(chip)
                }
                if (contactsChipGroup.childCount < 2) {
                    mustShowIconX(false)
                    setSearchBoxHint()
                }
            }
        }
        return chip
    }

    private fun nextivaContactSelected(nextivaContact: NextivaContact) {
        if (selectionInterface.selectedContactsCount() >= Constants.SMS.MAX_SMS_RECIPIENT_COUNT) {
            dialogManager.showDialog(requireContext(),
                    getString(R.string.chat_details_max_recipient_title),
                    getString(R.string.chat_details_max_recipient_body, Constants.SMS.MAX_SMS_RECIPIENT_COUNT),
                    getString(R.string.general_okay)) { _, _ -> }

        } else if (!selectionInterface.isMessageContactAlreadyAdded(nextivaContact)) {
            addMessageContact(nextivaContact)
        } else if (isResumed) {
            Snackbar.make(coordinatorLayout,
                    getString(R.string.connect_sms_contact_already_added),
                    Snackbar.LENGTH_SHORT)
                    .withFontAwesomeDrawable(FontDrawable(requireContext(),
                            R.string.fa_check_circle,
                            Enums.FontAwesomeIconType.SOLID)
                            .withColor(ContextCompat.getColor(requireContext(),
                                    R.color.connectPrimaryGreen))).show()
        }
    }

    // --------------------------------------------------------------------------------------------
    // TextListener Methods
    // --------------------------------------------------------------------------------------------
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {
        mustShowIconX(s?.toString() != null && s.toString().isNotEmpty() || contactsChipGroup.childCount > 1)
        s?.toString()?.let {
            selectionInterface.onSearchTermUpdated(it)
            contactsListFragment.onSearchTermUpdated(it)
        }
    }

    private fun mustShowIconX(show: Boolean) {
        cancelSearch.visibility = if (show) View.VISIBLE else View.GONE
    }

    // --------------------------------------------------------------------------------------------

}
