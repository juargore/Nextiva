package com.nextiva.nextivaapp.android.fragments.bottomsheets

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
import com.nextiva.nextivaapp.android.ConnectContactDetailsActivity
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ConnectContactListItem
import com.nextiva.nextivaapp.android.adapters.pagedlistadapters.ConnectContactListAdapter
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.core.common.UiUtil
import com.nextiva.nextivaapp.android.databinding.BottomSheetSmsDetailsBinding
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.db.model.DbPresence
import com.nextiva.nextivaapp.android.features.messaging.view.BottomSheetNewMessage
import com.nextiva.nextivaapp.android.features.messaging.view.ConversationActivity
import com.nextiva.nextivaapp.android.models.SmsConversationDetails
import com.nextiva.nextivaapp.android.util.ViewUtil
import com.nextiva.nextivaapp.android.util.extensions.clickWithThrottle
import com.nextiva.nextivaapp.android.util.extensions.serializable
import com.nextiva.nextivaapp.android.view.ConnectContactHeaderView
import com.nextiva.nextivaapp.android.view.ConnectQuickActionDetailButton
import com.nextiva.nextivaapp.android.viewmodels.BottomSheetSmsGroupDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.abs

@AndroidEntryPoint
class BottomSheetSmsDetails : BaseBottomSheetDialogFragment(), AppBarLayout.OnOffsetChangedListener {

    companion object {
        private const val CONVERSATION_DETAILS = "conversation_details"
        private const val PARTICIPANT_LIST = "participant_list"
        private const val FROM_CONVERSATION = "from_conversation"

        fun newInstance(
            conversationDetails: SmsConversationDetails,
            participantsList: ArrayList<String>?,
            isFromConversation: Boolean = false
        ): BottomSheetSmsDetails = BottomSheetSmsDetails().apply {
            arguments = Bundle().apply {
                putSerializable(CONVERSATION_DETAILS, conversationDetails)
                putStringArrayList(PARTICIPANT_LIST, participantsList)
                putBoolean(FROM_CONVERSATION, isFromConversation)
            }
        }
    }

    private lateinit var conversationDetails: SmsConversationDetails
    private var participantsList: ArrayList<String>? = null
    private var isFromConversation: Boolean = false

    private val percentageToShowTitleAtToolbar = 0.8f
    private var isTheTitleVisible = false

    @Inject
    lateinit var dbManager: DbManager

    private lateinit var contactHeaderView: ConnectContactHeaderView
    private lateinit var quickActionSmsButton: ConnectQuickActionDetailButton
    private lateinit var toolbar: Toolbar
    private lateinit var appBarLayout: AppBarLayout
    private lateinit var collapsingToolbarLayout: CollapsingToolbarLayout
    private lateinit var collapsedTitleTextView: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var cancelIcon: RelativeLayout
    private lateinit var participantCount: TextView
    private lateinit var addUserButton: TextView

    private lateinit var adapter: ConnectContactListAdapter

    private lateinit var viewModel: BottomSheetSmsGroupDetailsViewModel

    private val listItemObserver = Observer<ArrayList<BaseListItem>> {
        participantCount.text = getString(R.string.bottom_sheet_sms_details_participant_count, it.size)
        adapter.updateList(it)

        it.forEach { baseListItem ->
            (baseListItem as? ConnectContactListItem)?.let { listItem ->
                listItem.nextivaContact?.userId?.let { userId ->
                    dbManager.getPresenceLiveDataFromContactTypeId(userId).observe(this, presenceChangedObserver)
                }
            }
        }
    }

    private val presenceChangedObserver = Observer<DbPresence?> {
        if (it != null) {
            adapter.updatePresence(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showFullHeight = true
        viewModel = ViewModelProvider(requireActivity())[BottomSheetSmsGroupDetailsViewModel::class.java]

        arguments?.let {
            it.serializable<SmsConversationDetails>(CONVERSATION_DETAILS)
                ?.let { data -> conversationDetails = data }
            participantsList = it.getStringArrayList(PARTICIPANT_LIST)
            isFromConversation = it.getBoolean(FROM_CONVERSATION)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_sms_details, container, false)
        view?.let { bindViews(view) }

        viewModel.contactListItemLiveData.observe(viewLifecycleOwner, listItemObserver)

        viewModel.setup(participantsList, conversationDetails) { participants ->
            activity?.let { activity ->
                val uiName = UiUtil.getUiName(activity, participants, conversationDetails.getAllTeams())
                collapsedTitleTextView.text = uiName
                contactHeaderView.setupWithGroupSms(uiName)
            }
            contactHeaderView.setAvatars(UiUtil.getAvatarInfoList(participants, conversationDetails.getAllTeams()))
        }

        return view
    }

    private fun bindViews(view: View) {
        val binding = BottomSheetSmsDetailsBinding.bind(view)

        adapter = ConnectContactListAdapter(requireActivity(), this, dbManager, viewModel.sessionManager)

        contactHeaderView = binding.bottomSheetSmsDetailsContactHeaderView
        quickActionSmsButton = binding.bottomSheetSmsDetailsSmsTextView
        toolbar = binding.bottomSheetSmsDetailsToolbar
        recyclerView = binding.bottomSheetSmsDetailsRecyclerView
        appBarLayout = binding.bottomSheetSmsDetailsAppBarLayout
        collapsingToolbarLayout = binding.bottomSheetSmsDetailsCollapsingToolbar
        collapsedTitleTextView = binding.bottomSheetSmsDetailsToolbarCollapsedTitleTextView
        cancelIcon = binding.cancelIconInclude.closeIconView
        participantCount = binding.bottomSheetSmsDetailsParticipantCount
        addUserButton = binding.bottomSheetSmsDetailsUserPlus

        recyclerView.adapter = adapter

        addUserButton.visibility = View.GONE
        activity?.supportFragmentManager?.let { supportFragmentManager ->
            viewModel.preload.observe(this) { list ->
                list?.let {
                    addUserButton.visibility = View.VISIBLE
                    addUserButton.clickWithThrottle {
                        val dialog = BottomSheetNewMessage.newInstance(list, conversationDetails.groupId)
                        dialog.show(supportFragmentManager, null)
                    }
                }
            }
            viewModel.getPreloadContacts(conversationDetails)
        }
        cancelIcon.setOnClickListener { dismiss() }
        quickActionSmsButton.setOnClickListener {
            if (isFromConversation) {
                dismiss()

            } else {
                startActivity(
                    ConversationActivity.newIntent(requireActivity(),
                        conversationDetails,
                        false,
                        Enums.Chats.ConversationTypes.SMS,
                        Enums.Chats.ChatScreens.CONVERSATION))
                dismiss()
            }
        }

        ViewUtil.startAlphaAnimation(collapsedTitleTextView, 0, View.GONE)
        appBarLayout.addOnOffsetChangedListener(this)
        collapsingToolbarLayout.setExpandedTitleColor(ContextCompat.getColor(requireActivity(), android.R.color.transparent))
        collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(requireActivity(), android.R.color.transparent))
    }

    private fun handleToolbarTitleVisibility(percentage: Float) {
        if (percentage >= percentageToShowTitleAtToolbar) {
            if (!isTheTitleVisible) {
                ViewUtil.startAlphaAnimation(collapsedTitleTextView, resources.getInteger(R.integer.general_animation_short_duration_millis).toLong(), View.VISIBLE)
                isTheTitleVisible = true
            }
        } else {
            if (isTheTitleVisible) {
                ViewUtil.startAlphaAnimation(collapsedTitleTextView, resources.getInteger(R.integer.general_animation_short_duration_millis).toLong(), View.GONE)
                isTheTitleVisible = false
            }
        }

        contactHeaderView.visibility = if (isTheTitleVisible) {
            View.INVISIBLE
        } else {
            View.VISIBLE
        }
    }

    override fun onConnectContactListItemClicked(listItem: ConnectContactListItem) {
        super.onConnectContactListItemClicked(listItem)
        if (!listItem.showSelfIndicator) {
            listItem.nextivaContact?.let { contact ->
                requireActivity().startActivity(ConnectContactDetailsActivity.newIntent(
                        requireActivity(),
                        contact))
            }
        }
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        appBarLayout?.totalScrollRange?.let { maxScroll ->
            val percentage = abs(verticalOffset).toFloat() / maxScroll.toFloat()
            handleToolbarTitleVisibility(percentage)
        }
    }
}