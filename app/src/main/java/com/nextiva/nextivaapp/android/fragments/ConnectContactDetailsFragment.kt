package com.nextiva.nextivaapp.android.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactCategoryListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactDetailHeaderListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactDetailListItem
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.model.EmailAddress
import com.nextiva.nextivaapp.android.db.model.PhoneNumber
import com.nextiva.nextivaapp.android.dialogs.ContactActionDialog
import com.nextiva.nextivaapp.android.features.rooms.view.BottomSheetRoomDetailsFragment
import com.nextiva.nextivaapp.android.features.rooms.view.RoomConversationActivity
import com.nextiva.nextivaapp.android.managers.interfaces.IntentManager
import com.nextiva.nextivaapp.android.viewmodels.ConnectContactDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ConnectContactDetailsFragment : GeneralRecyclerViewFragment() {

    @Inject
    lateinit var intentManager: IntentManager

    private lateinit var viewModel: ConnectContactDetailsViewModel

    private var blockingListener: BlockingNumberListener? = null

    private val baseListItemObserver = Observer<ArrayList<BaseListItem>> { listItems ->
        mAdapter.updateList(listItems)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BlockingNumberListener) {
            blockingListener = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        shouldAddDivider = false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(
            requireActivity()
        )[ConnectContactDetailsViewModel::class.java]
        viewModel.getBaseListItemsLiveData().observe(viewLifecycleOwner, baseListItemObserver)
        viewModel.getDetailListItems()

        if (isAdded) {
            setFragmentResultListener(ContactActionDialog.CONTACT_ACTION_DIALOG_RESULT) { _, bundle ->
                val phoneNumber: String? = bundle.getString(ContactActionDialog.PHONE_NUMBER, null)
                phoneNumber?.let { number ->
                    blockingListener?.onBlockUnblockAction(number)
                }
            }
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_connect_contact_details
    }

    override fun getRecyclerViewId(): Int {
        return R.id.connect_contact_details_recycler_view
    }

    override fun getAnalyticScreenName(): String {
        return Enums.Analytics.ScreenName.CONNECT_CONTACT_DETAILS
    }

    override fun getSwipeRefreshLayoutId(): Int {
        return 0
    }

    override fun fetchItemList(forceRefresh: Boolean) {
        super.fetchItemList(forceRefresh)
        stopRefreshing()
    }

    override fun onConnectContactDetailHeaderListItemClicked(listItem: ConnectContactDetailHeaderListItem) {
        super.onConnectContactDetailHeaderListItemClicked(listItem)
        viewModel.getDetailListItems()
    }

    override fun onConnectContactCategoryItemClicked(listItem: ConnectContactCategoryListItem) {
        super.onConnectContactCategoryItemClicked(listItem)
        when (listItem.data) {
            is EmailAddress -> {
                activity ?. let { activity ->
                    listItem.data.address?.let { email ->
                        intentManager.sendEmail(
                            activity,
                            Enums.Analytics.ScreenName.CONNECT_CONTACT_DETAILS,
                            email,
                            "",
                            "",
                            "",
                            null
                        )
                    }
                }
            }
            is PhoneNumber -> {
                viewModel.nextivaContact?.let { contact ->
                    listItem.data.id?.let { rowId ->
                        ContactActionDialog.newInstance(contact, rowId)
                            .show(requireActivity().supportFragmentManager, null)
                    }
                } ?: run {
                    ContactActionDialog.newInstance(
                        nextivaContact = null,
                        phoneNumber = listItem.data.strippedNumber ?: listItem.data.number
                    ).show(requireActivity().supportFragmentManager, null)
                }
            }
            else -> { }
        }
    }


    override fun onConnectContactDetailListItemClicked(listItem: ConnectContactDetailListItem) {
        super.onConnectContactDetailListItemClicked(listItem)
        when (listItem.actionType) {
            Enums.Platform.ConnectContactDetailClickAction.LINK -> {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(if (listItem.subtitle?.startsWith("https://") == true) listItem.subtitle else "https://${listItem.subtitle}")
                )
                startActivity(intent)
            }

            Enums.Platform.ConnectContactDetailClickAction.ADDRESS -> {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("geo:0,0?q=${listItem.subtitle}")
                )
                startActivity(intent)
            }

            Enums.Platform.ConnectContactDetailClickAction.EMAIL -> {
                activity?.let { activity ->
                    listItem.subtitle?.let { email ->
                        intentManager.sendEmail(
                            activity,
                            Enums.Analytics.ScreenName.CONNECT_CONTACT_DETAILS,
                            email,
                            "",
                            "",
                            "",
                            null
                        )
                    }
                }
            }

            Enums.Platform.ConnectContactDetailClickAction.PHONE -> {
            }

            Enums.Platform.ConnectContactDetailClickAction.ROOM_DETAILS -> {
                viewModel.room?.id?.let { roomId ->
                    BottomSheetRoomDetailsFragment.newInstance(roomId)
                        .show(childFragmentManager, null)
                }
            }

            Enums.Platform.ConnectContactDetailClickAction.ROOM_CONVERSATION -> {
                viewModel.room?.id?.let { roomId ->
                    viewModel.joinRoom()
                    activity?.let { activity ->
                        val intent = RoomConversationActivity.newIntent(
                            activity,
                            roomId,
                            listItem.uiName ?: ""
                        )
                        startActivity(intent)
                    }
                }
            }

            Enums.Platform.ConnectContactDetailClickAction.NONE -> {

            }
        }
    }

    interface BlockingNumberListener {
        fun onBlockUnblockAction(phoneNumber: String)
    }
}
