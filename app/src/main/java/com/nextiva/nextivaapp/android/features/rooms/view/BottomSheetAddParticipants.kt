package com.nextiva.nextivaapp.android.features.rooms.view

import android.app.Dialog
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.databinding.BottomSheetAddParticipantsBinding
import com.nextiva.nextivaapp.android.features.rooms.model.DbRoom
import com.nextiva.nextivaapp.android.features.rooms.view.components.ChipSelectorView
import com.nextiva.nextivaapp.android.features.rooms.viewmodel.BottomSheetRoomParticipantsViewModel
import com.nextiva.nextivaapp.android.fragments.bottomsheets.BaseBottomSheetDialogFragment
import com.nextiva.nextivaapp.android.managers.interfaces.AnalyticsManager
import com.nextiva.nextivaapp.android.managers.interfaces.AvatarManager
import com.nextiva.nextivaapp.android.managers.interfaces.DialogManager
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.models.NextivaContact
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BottomSheetAddParticipants() : BaseBottomSheetDialogFragment(), TextWatcher {

    constructor(dbRoom: DbRoom): this() {
        this.dbRoom = dbRoom
    }

    lateinit var dbRoom: DbRoom

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var analyticsManager: AnalyticsManager

    @Inject
    lateinit var avatarManager: AvatarManager

    @Inject
    lateinit var dialogManager: DialogManager

    private lateinit var viewModel: BottomSheetRoomParticipantsViewModel

    private lateinit var cancelIcon: RelativeLayout
    private lateinit var chipSelectorView: ChipSelectorView
    private lateinit var header: LinearLayout
    private lateinit var fragmentContainer: FragmentContainerView

    private lateinit var contactListFragment: ContactListFragment
    private lateinit var cancelButton: TextView
    private lateinit var addButton: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[BottomSheetRoomParticipantsViewModel::class.java]
        viewModel.dbRoom.postValue(dbRoom)

        setupContactListFragment()
        showFullHeight = true
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState) as BottomSheetDialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_add_participants, container, false)
        view?.let { bindViews(view) }

        chipSelectorView.setSearchBoxHint(R.string.connect_bottom_sheet_room_search_hint)
        chipSelectorView.getEditText().addTextChangedListener(this)
        chipSelectorView.chipObjects.observe(this) { chipObjects ->
            viewModel.removeAllContacts()
            chipObjects.forEach {
                (it as? NextivaContact)?.let { contact -> viewModel.contactAdded(contact) }
            }
            contactListUpdated()
        }

        viewModel.addSelectedContact.observe(viewLifecycleOwner) {
            nextivaContactSelected(it)
        }

        return view
    }

    private fun setupContactListFragment() {
        contactListFragment = ContactListFragment()
        contactListFragment.setViewModel(viewModel)

        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, contactListFragment)
        transaction.commit()
        childFragmentManager.executePendingTransactions()
    }

    private fun bindViews(view: View) {
        val binding = BottomSheetAddParticipantsBinding.bind(view)

        cancelIcon = binding.cancelIconInclude.closeIconView
        chipSelectorView = binding.chipSelectorView
        header = binding.header
        addButton = binding.addButton
        cancelButton = binding.cancelButton
        fragmentContainer = binding.fragmentContainer

        cancelIcon.setOnClickListener {
            dismiss()
        }

        cancelButton.setOnClickListener {
            dismiss()
        }

        addButton.setOnClickListener {
            viewModel.sendMembers {
                dismiss()
            }
        }
    }

    private fun contactListUpdated() {
        activity?.let { activity ->
            addButton.apply {
                if (viewModel.selectedContacts.isNotEmpty() && (viewModel.searchTerm?.length ?: 0) == 0) {
                    isEnabled = true
                    backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.connectPrimaryBlue))
                    setTextColor(ContextCompat.getColor(activity, R.color.connectWhite))
                } else {
                    isEnabled = false
                    backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.connectGrey03))
                    setTextColor(ContextCompat.getColor(activity, R.color.connectPrimaryGrey))
                }
            }
        }
    }

    fun nextivaContactSelected(nextivaContact: NextivaContact) {
        if (!viewModel.isMessageContactAlreadyAdded(nextivaContact)) {
            viewModel.contactAdded(nextivaContact)
            chipSelectorView.addChip(nextivaContact.displayName ?: "", nextivaContact)
            contactListUpdated()
        } else {
            Toast.makeText(activity, R.string.connect_sms_contact_already_added, Toast.LENGTH_LONG).show()
        }
    }

    // --------------------------------------------------------------------------------------------
    // TextListener Methods
    // --------------------------------------------------------------------------------------------
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {
        s?.toString()?.let {
            viewModel.onSearchTermUpdated(it)
            contactListFragment.onSearchTermUpdated(it)
            contactListUpdated()
        }
    }
    // --------------------------------------------------------------------------------------------

}
