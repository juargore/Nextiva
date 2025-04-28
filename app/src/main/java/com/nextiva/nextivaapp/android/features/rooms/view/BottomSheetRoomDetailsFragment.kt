package com.nextiva.nextivaapp.android.features.rooms.view

import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.nextiva.nextivaapp.android.ConnectMainActivity
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactListItem
import com.nextiva.nextivaapp.android.adapters.pagedlistadapters.ConnectContactListAdapter
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.databinding.BottomSheetRoomDetailBinding
import com.nextiva.nextivaapp.android.databinding.RoomDateDetailsBinding
import com.nextiva.nextivaapp.android.databinding.RoomDetailsBinding
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.db.model.DbPresence
import com.nextiva.nextivaapp.android.features.rooms.RoomsEnums
import com.nextiva.nextivaapp.android.features.rooms.model.DbRoom
import com.nextiva.nextivaapp.android.features.rooms.view.components.RoundButton
import com.nextiva.nextivaapp.android.features.rooms.viewmodel.BottomSheetRoomDetailsViewModel
import com.nextiva.nextivaapp.android.fragments.bottomsheets.BaseBottomSheetDialogFragment
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PlatformRoomsRepository
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.models.AvatarInfo
import com.nextiva.nextivaapp.android.util.ViewUtil
import com.nextiva.nextivaapp.android.util.extensions.orFalse
import com.nextiva.nextivaapp.android.view.ConnectQuickActionDetailButton
import com.nextiva.nextivaapp.android.view.fontawesome.FontTextView
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Math.abs
import javax.inject.Inject

@AndroidEntryPoint
class BottomSheetRoomDetailsFragment(): BaseBottomSheetDialogFragment(), AppBarLayout.OnOffsetChangedListener {

    companion object {

        const val ROOM_ID = "ROOM_ID"
        const val FROM_ACTIVITY = "FROM_ACTIVITY"

        fun newInstance(roomId: String, createdFromActivity: Boolean = false) : BottomSheetRoomDetailsFragment {
            return BottomSheetRoomDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ROOM_ID, roomId)
                    putBoolean(FROM_ACTIVITY, createdFromActivity)
                }
            }
        }
    }

    var createdFromActivity: Boolean = false
    lateinit var roomId: String

    private val percentageToShowTitleAtToolbar = 0.8f
    private var isTheTitleVisible = false

    private lateinit var roomHeaderView: RoomDetailHeaderView
    private lateinit var toolbar: Toolbar
    private lateinit var appBarLayout: AppBarLayout
    private lateinit var collapsingToolbarLayout: CollapsingToolbarLayout
    private lateinit var quickActionChatButton: ConnectQuickActionDetailButton
    private lateinit var collapsedTitleTextView: TextView
    private lateinit var titleTextView: TextView
    private lateinit var roomTypeTextView: TextView
    private lateinit var participantsInRoomTextView: TextView
    private lateinit var addParticipantsInRoomTextView: FontTextView
    private lateinit var toolbarLockImage: FontTextView
    private lateinit var fragmentContainer: FrameLayout
    private lateinit var cancelIcon: RelativeLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var bottomButtonLayout: LinearLayout
    private lateinit var leaveRoomButton: TextView

    private lateinit var roomDetailLayout: RoomDetailsBinding
    private lateinit var roomDateLayout: RoomDateDetailsBinding

    private lateinit var adapter: ConnectContactListAdapter

    @Inject
    lateinit var platformRoomsRepository: PlatformRoomsRepository

    @Inject
    lateinit var dbManager: DbManager

    @Inject
    lateinit var sessionManager: SessionManager

    private lateinit var viewModel: BottomSheetRoomDetailsViewModel

    private val listItemObserver = Observer<ArrayList<BaseListItem>> {
        adapter.updateList(it)

        participantsInRoomTextView.text = if (it.size > 1)
            getString(R.string.connect_bottom_sheet_room_details_participants_in_room, it.size.toString())
        else
            getString(R.string.connect_bottom_sheet_room_details_participant_in_room)

        it.forEach { baseListItem ->
            (baseListItem as? ConnectContactListItem)?.let { listItem ->
                listItem.nextivaContact?.userId?.let { userId ->
                    dbManager.getPresenceLiveDataFromContactTypeId(userId).observe(this, presenceChangedObserver)
                }
            }
        }
    }

    private val presenceChangedObserver = Observer<DbPresence> {
        if (it != null) {
            adapter.updatePresence(it)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.roomId = arguments?.getString(ROOM_ID).orEmpty()
        this.createdFromActivity = arguments?.getBoolean(FROM_ACTIVITY).orFalse()

        showFullHeight = true
        viewModel = ViewModelProvider(requireActivity())[BottomSheetRoomDetailsViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_room_detail, container, false)
        view?.let { bindViews(view) }

        toolbar.setTitleTextColor(ContextCompat.getColor(requireActivity(), R.color.black))

        viewModel.contactListItemLiveData.observe(viewLifecycleOwner, listItemObserver)
        viewModel.roomId.postValue(roomId)
        viewModel.dbRoom.observe(viewLifecycleOwner, roomObserver)

        ViewUtil.startAlphaAnimation(collapsedTitleTextView, 0, View.GONE)
        appBarLayout.addOnOffsetChangedListener(this)
        collapsingToolbarLayout.setExpandedTitleColor(ContextCompat.getColor(requireActivity(), android.R.color.transparent))
        collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(requireActivity(), android.R.color.transparent))

        view.findViewById<ComposeView>(R.id.favorite_button).setContent {
            RoundButton(
                    buttonActiveTitle = stringResource(R.string.connect_room_details_remove_button),
                    buttonInactiveTitle = stringResource(R.string.connect_room_details_favorite_button),
                    buttonActiveIcon = stringResource(R.string.fa_star),
                    buttonInactiveIcon = stringResource(R.string.fa_star),
                    isActive = (viewModel.dbRoom?.value?.requestorFavorite == true)
            ) { viewModel.setFavorite() }
        }
        view.findViewById<ComposeView>(R.id.mute_button).setContent {
            RoundButton(
                    buttonActiveTitle = stringResource(R.string.connect_room_details_unmute_button),
                    buttonInactiveTitle = stringResource(R.string.connect_room_details_mute_button),
                    buttonActiveIcon = stringResource(R.string.fa_bell),
                    buttonInactiveIcon = stringResource(R.string.fa_bell_slash),
                    isActive = false
            ) { }
        }
        view.findViewById<ComposeView>(R.id.hide_button).setContent {
            RoundButton(
                    buttonActiveTitle = stringResource(R.string.connect_room_details_show_button),
                    buttonInactiveTitle = stringResource(R.string.connect_room_details_hide_button),
                    buttonActiveIcon = stringResource(R.string.fa_eye),
                    buttonInactiveIcon = stringResource(R.string.fa_eye_slash),
                    isActive = false
            ) { }
        }

        quickActionChatButton.setOnClickListener {
            if (!createdFromActivity) {
                startActivity(RoomConversationActivity.newIntent(requireActivity(),
                        roomId,
                        viewModel.displayName(isToolbarTitle = true)
                ))
            }
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

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.let { window ->
            WindowCompat.getInsetsController(window, window.decorView)?.let { controller ->
                window.navigationBarColor = ContextCompat.getColor(requireActivity(), R.color.connectGrey01)
            }
        }
        return dialog
    }

    private val roomObserver = Observer<DbRoom?> {
        val dbRoom = it ?: return@Observer

        roomHeaderView.setNameText(dbRoom.name ?: "")
        roomHeaderView.setDescriptionText(dbRoom.description ?: "")

        if (dbRoom.typeEnum() == RoomsEnums.ConnectRoomsTypes.CURRENT_USER_MY_ROOM) {
            var myRoom = requireContext().getString(R.string.my_status_my_room)
            var displayName = myRoom
            viewModel.sessionManager.userDetails?.fullName?.let { fullName ->
                if (!TextUtils.isEmpty(fullName)) {
                    myRoom = "$myRoom ($fullName)"
                    displayName = fullName
                }
            }

            val avatarInfo = AvatarInfo.Builder()
                .isConnect(true)
                .setDisplayName(displayName)
                .setFontAwesomeIconResId(R.string.fa_user)
                .build()
            roomHeaderView.setAvatar(avatarInfo)
            roomTypeTextView.text = requireContext().getString(R.string.my_status_my_room)
            roomDetailLayout.roomOwnerTextview.text = getString(
                R.string.general_name_with_self_indicator,
                viewModel.sessionManager.userDetails?.fullName
            )
        } else {
            val avatarInfo = AvatarInfo.Builder()
                .setDisplayName(dbRoom.name)
                .isConnect(true)
                .setFontAwesomeIconResId(R.string.fa_door_open)
                .setFontAwesomeFontResId(R.font.fa_regular_400)
                .setAlwaysShowIcon(true)
                .build()
            roomHeaderView.setAvatar(avatarInfo)
            roomTypeTextView.text = requireContext().getString(R.string.connect_room_toolbar_title)
            roomDetailLayout.roomOwnerTextview.text =
                dbRoom.ownerId?.let { ownerId ->
                    dbRoom.members?.firstOrNull { member -> member.userUuid == ownerId }
                        ?.let { member ->
                            "${member.firstName} ${member.lastName}"
                        }
                }
        }
        bottomButtonLayout.visibility = if (viewModel.allowLeaveRoom()) View.VISIBLE else View.GONE

        collapsedTitleTextView.text = dbRoom.name

        val private = dbRoom.type?.equals(RoomsEnums.ConnectRoomsTypes.PRIVATE_ROOM.value) == true
        toolbarLockImage.visibility = if (private) View.VISIBLE else View.GONE

        val isCurrentUserOwner = (sessionManager.userInfo?.comNextivaUseruuid == dbRoom.createdBy)
        val disableAddParticipants = (dbRoom.typeEnum() == RoomsEnums.ConnectRoomsTypes.MY_ROOM) ||
                (private && !isCurrentUserOwner)
        addParticipantsInRoomTextView.visibility = if (disableAddParticipants) View.GONE else View.VISIBLE

        // -------------------------
        // Room & Date Details

        roomDetailLayout.roomNameTextview.text = dbRoom.name
        roomDetailLayout.roomDescriptionTextview.text = dbRoom.description

        roomDateLayout.createdByTextview.text =
            dbRoom.members?.firstOrNull { member -> member.userUuid == dbRoom.createdBy }?.let { member ->
                "${member.firstName} ${member.lastName}"
            }

        roomDateLayout.lastModifiedTextview.text =
            dbRoom.members?.firstOrNull { member -> member.userUuid == dbRoom.lastModifiedBy }?.let { member ->
                "${member.firstName} ${member.lastName}"
            }
    }

    private fun bindViews(view: View) {
        val binding = BottomSheetRoomDetailBinding.bind(view)

        roomHeaderView = binding.bottomSheetRoomDetailHeaderView
        toolbar = binding.bottomSheetRoomDetailsToolbar
        fragmentContainer = binding.bottomSheetRoomDetailListFragmentContainerLayout
        appBarLayout = binding.bottomSheetContactDetailAppBarLayout
        collapsingToolbarLayout = binding.bottomSheetContactDetailsCollapsingToolbar
        collapsedTitleTextView = binding.bottomSheetRoomDetailsToolbarCollapsedTitleTextView
        titleTextView = binding.bottomSheetRoomDetailsToolbarTitleTextView
        roomTypeTextView = binding.roomTypeTextView
        toolbarLockImage = binding.bottomSheetRoomDetailsToolbarLock
        cancelIcon = binding.cancelIconInclude.closeIconView
        participantsInRoomTextView = binding.bottomSheetRoomDetailsParticipants
        addParticipantsInRoomTextView = binding.bottomSheetRoomDetailsAddParticipants
        recyclerView = binding.bottomSheetRoomDetailsRecyclerViews
        quickActionChatButton = binding.bottomSheetRoomDetailsChatTextView
        bottomButtonLayout = binding.connectRoomDetailsButtonLayout
        leaveRoomButton = binding.connectRoomDetailsLeaveButton

        roomDetailLayout = RoomDetailsBinding.bind(binding.root)
        roomDateLayout = RoomDateDetailsBinding.bind(binding.root)

        adapter = ConnectContactListAdapter(requireActivity(), this, dbManager, sessionManager)
        recyclerView.adapter = adapter

        cancelIcon.setOnClickListener { dismiss() }
        leaveRoomButton.setOnClickListener { handleLeaveRoom() }
    }

    private fun handleLeaveRoom() {
        viewModel.leaveRoom()
        if (activity is ConnectMainActivity) {
            dismiss()
        } else {
            activity?.finish()
        }
    }

    private fun handleToolbarTitleVisibility(percentage: Float) {
        val updateLock = viewModel.dbRoom.value?.type == RoomsEnums.ConnectRoomsTypes.PRIVATE_ROOM.value
        if (percentage >= percentageToShowTitleAtToolbar) {
            if (!isTheTitleVisible) {
                ViewUtil.startAlphaAnimation(collapsedTitleTextView, resources.getInteger(R.integer.general_animation_short_duration_millis).toLong(), View.VISIBLE)
                ViewUtil.startAlphaAnimation(titleTextView, resources.getInteger(R.integer.general_animation_short_duration_millis).toLong(), View.GONE)
                if(updateLock) ViewUtil.startAlphaAnimation(toolbarLockImage, resources.getInteger(R.integer.general_animation_short_duration_millis).toLong(), View.GONE)
                isTheTitleVisible = true
            }
        } else {
            if (isTheTitleVisible) {
                ViewUtil.startAlphaAnimation(collapsedTitleTextView, resources.getInteger(R.integer.general_animation_short_duration_millis).toLong(), View.GONE)
                ViewUtil.startAlphaAnimation(titleTextView, resources.getInteger(R.integer.general_animation_short_duration_millis).toLong(), View.VISIBLE)
                if(updateLock) ViewUtil.startAlphaAnimation(toolbarLockImage, resources.getInteger(R.integer.general_animation_short_duration_millis).toLong(), View.VISIBLE)
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