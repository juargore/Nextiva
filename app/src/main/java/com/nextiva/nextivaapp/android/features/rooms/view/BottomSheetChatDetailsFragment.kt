package com.nextiva.nextivaapp.android.features.rooms.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactListItem
import com.nextiva.nextivaapp.android.adapters.pagedlistadapters.ConnectContactListAdapter
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.core.common.UiUtil
import com.nextiva.nextivaapp.android.databinding.BottomSheetChatDetailBinding
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.db.model.DbPresence
import com.nextiva.nextivaapp.android.features.rooms.RoomsEnums
import com.nextiva.nextivaapp.android.features.rooms.model.DbRoom
import com.nextiva.nextivaapp.android.features.rooms.viewmodel.BottomSheetRoomDetailsViewModel
import com.nextiva.nextivaapp.android.fragments.bottomsheets.BaseBottomSheetDialogFragment
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.models.NextivaContact
import com.nextiva.nextivaapp.android.util.ViewUtil
import com.nextiva.nextivaapp.android.view.ConnectContactHeaderView
import com.nextiva.nextivaapp.android.view.ConnectQuickActionDetailButton
import com.nextiva.nextivaapp.android.view.fontawesome.FontTextView
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Math.abs
import javax.inject.Inject

@AndroidEntryPoint
class BottomSheetChatDetailsFragment(): BaseBottomSheetDialogFragment(), AppBarLayout.OnOffsetChangedListener {

    companion object {

        private const val ROOM_ID = "ROOM_ID"

        fun newInstance(roomId: String) : BottomSheetChatDetailsFragment {
            return BottomSheetChatDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ROOM_ID, roomId)
                }
            }
        }
    }

    lateinit var roomId: String

    private val percentageToShowTitleAtToolbar = 0.8f
    private var isTheTitleVisible = false

    private lateinit var roomHeaderView: ConnectContactHeaderView
    private lateinit var toolbar: Toolbar
    private lateinit var appBarLayout: AppBarLayout
    private lateinit var collapsingToolbarLayout: CollapsingToolbarLayout
    private lateinit var quickActionChatButton: ConnectQuickActionDetailButton
    private lateinit var collapsedTitleTextView: TextView
    private lateinit var titleTextView: TextView
    private lateinit var participantsInRoomTextView: TextView
    private lateinit var addParticipantsInRoomTextView: FontTextView
    private lateinit var cancelIcon: RelativeLayout
    private lateinit var recyclerView: RecyclerView

    private lateinit var adapter: ConnectContactListAdapter

    @Inject
    lateinit var dbManager: DbManager

    @Inject
    lateinit var sessionManager: SessionManager

    private lateinit var viewModel: BottomSheetRoomDetailsViewModel

    private val listItemObserver = Observer<ArrayList<BaseListItem>> {
        adapter.updateList(it)

        participantsInRoomTextView.text = if (it.size > 1)
            getString(R.string.connect_bottom_sheet_chat_details_participants_in_room, it.size.toString())
        else
            getString(R.string.connect_bottom_sheet_room_details_participant_in_room)

        val contactList = mutableListOf<NextivaContact>()
        it.forEach { baseListItem ->
            (baseListItem as? ConnectContactListItem)?.let { listItem ->
                listItem.nextivaContact?.userId?.let { userId ->
                    dbManager.getPresenceLiveDataFromContactTypeId(userId).observe(this, presenceChangedObserver)
                }

                listItem.nextivaContact?.let { contact ->
                    if (contact.userId != sessionManager.userInfo?.comNextivaUseruuid) {
                        contactList.add(contact)
                    }
                }
            }
        }

        roomHeaderView.setAvatars(UiUtil.getAvatarInfoList(contactList, null))
        activity?.let { activity ->
            roomHeaderView.setupWithGroupSms(UiUtil.getUiName(activity, contactList, null))
        }
    }

    private val presenceChangedObserver = Observer<DbPresence> {
        if (it != null) {
            adapter.updatePresence(it)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.roomId = arguments?.getString(BottomSheetRoomDetailsFragment.ROOM_ID).orEmpty()

        showFullHeight = true
        viewModel = ViewModelProvider(requireActivity())[BottomSheetRoomDetailsViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_chat_detail, container, false)
        view?.let { bindViews(view) }

        toolbar.setTitleTextColor(ContextCompat.getColor(requireActivity(), R.color.black))

        viewModel.contactListItemLiveData.observe(viewLifecycleOwner, listItemObserver)
        viewModel.roomId.postValue(roomId)
        viewModel.dbRoom.observe(viewLifecycleOwner, roomObserver)

        ViewUtil.startAlphaAnimation(collapsedTitleTextView, 0, View.GONE)
        appBarLayout.addOnOffsetChangedListener(this)
        collapsingToolbarLayout.setExpandedTitleColor(ContextCompat.getColor(requireActivity(), android.R.color.transparent))
        collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(requireActivity(), android.R.color.transparent))

        quickActionChatButton.setOnClickListener {
            startActivity(RoomConversationActivity.newIntent(requireActivity(),
                roomId,
                viewModel.displayName(isToolbarTitle = true)
            ))
            dismiss()
        }

        addParticipantsInRoomTextView.setIcon(R.string.fa_user_plus, Enums.FontAwesomeIconType.REGULAR)
        addParticipantsInRoomTextView.setOnClickListener {
            viewModel.dbRoom?.value?.let {
                BottomSheetAddParticipants(it).show(childFragmentManager, null)
            }
        }

        return view
    }

    private val roomObserver = Observer<DbRoom?> {
        val dbRoom = it ?: return@Observer

        collapsedTitleTextView.text = dbRoom.name

        val private = dbRoom.type?.equals(RoomsEnums.ConnectRoomsTypes.PRIVATE_ROOM.value) == true

        val isCurrentUserOwner = (sessionManager.userInfo?.comNextivaUseruuid == dbRoom.createdBy)
        val disableAddParticipants = (dbRoom.typeEnum() == RoomsEnums.ConnectRoomsTypes.MY_ROOM) ||
                (private && !isCurrentUserOwner)
        addParticipantsInRoomTextView.visibility = if (disableAddParticipants) View.GONE else View.VISIBLE
    }

    private fun bindViews(view: View) {
        val binding = BottomSheetChatDetailBinding.bind(view)

        roomHeaderView = binding.bottomSheetRoomDetailHeaderView
        toolbar = binding.bottomSheetRoomDetailsToolbar
        appBarLayout = binding.bottomSheetContactDetailAppBarLayout
        collapsingToolbarLayout = binding.bottomSheetContactDetailsCollapsingToolbar
        collapsedTitleTextView = binding.bottomSheetRoomDetailsToolbarCollapsedTitleTextView
        titleTextView = binding.bottomSheetRoomDetailsToolbarTitleTextView
        cancelIcon = binding.cancelIconInclude.closeIconView
        participantsInRoomTextView = binding.bottomSheetRoomDetailsParticipants
        addParticipantsInRoomTextView = binding.bottomSheetRoomDetailsAddParticipants
        recyclerView = binding.bottomSheetRoomDetailsRecyclerViews
        quickActionChatButton = binding.bottomSheetRoomDetailsChatTextView

        adapter = ConnectContactListAdapter(requireActivity(), this, dbManager, sessionManager)
        recyclerView.adapter = adapter

        cancelIcon.setOnClickListener { dismiss() }
    }

    private fun handleToolbarTitleVisibility(percentage: Float) {
        if (percentage >= percentageToShowTitleAtToolbar) {
            if (!isTheTitleVisible) {
                ViewUtil.startAlphaAnimation(collapsedTitleTextView, resources.getInteger(R.integer.general_animation_short_duration_millis).toLong(), View.VISIBLE)
                ViewUtil.startAlphaAnimation(titleTextView, resources.getInteger(R.integer.general_animation_short_duration_millis).toLong(), View.GONE)
                isTheTitleVisible = true
            }
        } else {
            if (isTheTitleVisible) {
                ViewUtil.startAlphaAnimation(collapsedTitleTextView, resources.getInteger(R.integer.general_animation_short_duration_millis).toLong(), View.GONE)
                ViewUtil.startAlphaAnimation(titleTextView, resources.getInteger(R.integer.general_animation_short_duration_millis).toLong(), View.VISIBLE)
                isTheTitleVisible = false
            }
        }

        roomHeaderView.visibility = if (isTheTitleVisible) { View.INVISIBLE } else { View.VISIBLE }
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        appBarLayout?.totalScrollRange?.let { maxScroll ->
            val percentage = abs(verticalOffset).toFloat() / maxScroll.toFloat()
            handleToolbarTitleVisibility(percentage)
        }
    }
}