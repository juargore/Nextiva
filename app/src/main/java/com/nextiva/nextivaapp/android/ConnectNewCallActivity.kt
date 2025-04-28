package com.nextiva.nextivaapp.android

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.balysv.materialmenu.MaterialMenuDrawable
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.nextiva.nextivaapp.android.adapters.ConnectNewCallViewPagerAdapter
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.CallHistoryListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ContactListItem
import com.nextiva.nextivaapp.android.constants.Constants
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.constants.Enums.Analytics.ScreenName
import com.nextiva.nextivaapp.android.constants.Enums.Analytics.ScreenName.Screen
import com.nextiva.nextivaapp.android.constants.Enums.Service.DialingServiceTypes
import com.nextiva.nextivaapp.android.constants.RequestCodes
import com.nextiva.nextivaapp.android.constants.RequestCodes.NewCall.NewCallType
import com.nextiva.nextivaapp.android.databinding.ActivityConnectMainBinding
import com.nextiva.nextivaapp.android.fragments.bottomsheets.BottomSheetDialerFragment
import com.nextiva.nextivaapp.android.managers.NextivaSessionManager
import com.nextiva.nextivaapp.android.managers.interfaces.AnalyticsManager
import com.nextiva.nextivaapp.android.managers.interfaces.CallManager
import com.nextiva.nextivaapp.android.managers.interfaces.CallManager.ProcessParticipantInfoCallBack
import com.nextiva.nextivaapp.android.managers.interfaces.DialogManager
import com.nextiva.nextivaapp.android.managers.interfaces.LocalContactsManager
import com.nextiva.nextivaapp.android.managers.interfaces.PermissionManager
import com.nextiva.nextivaapp.android.managers.interfaces.PermissionManager.PermissionGrantedCallback
import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager
import com.nextiva.nextivaapp.android.util.ApplicationUtil.isNightModeEnabled
import com.nextiva.nextivaapp.android.util.fontawesome.FontDrawable
import com.nextiva.nextivaapp.android.view.ConnectBottomNavigationView
import com.nextiva.nextivaapp.android.viewmodels.ConnectNewCallViewModel
import com.nextiva.pjsip.pjsip_lib.sipservice.ParticipantInfo
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@AndroidEntryPoint
class ConnectNewCallActivity : BaseActivity(),
    ProcessParticipantInfoCallBack {

    @Inject
    lateinit var permissionManager: PermissionManager
    @Inject
    lateinit var localContactsManager: LocalContactsManager
    @Inject
    lateinit var sharedPreferencesManager: SharedPreferencesManager
    @Inject
    lateinit var sessionManager: NextivaSessionManager
    @Inject
    lateinit var dialogManager: DialogManager
    @Inject
    lateinit var analyticsManager: AnalyticsManager
    @Inject
    lateinit var callManager: CallManager

    private lateinit var toolbar: Toolbar
    private lateinit var appBarLayout: AppBarLayout
    private lateinit var collapsingToolbarLayout: CollapsingToolbarLayout
    private lateinit var titleTextView: TextView
    private lateinit var viewPager: ViewPager2
    private lateinit var bottomNavigation: ConnectBottomNavigationView
    private lateinit var floatingActionButton: FloatingActionButton
    private lateinit var coordinatorLayout: CoordinatorLayout

    private lateinit var viewPagerAdapter: ConnectNewCallViewPagerAdapter

    private lateinit var viewModel: ConnectNewCallViewModel

    @NewCallType
    private var newCallType = 0

    companion object {
        @JvmStatic
        fun newIntent(
            context: Context?,
            @RequestCodes.NewCall.NewCallType newCallType: Int
        ): Intent {
            val intent = Intent(context, ConnectNewCallActivity::class.java)
            intent.putExtra(Constants.Calls.PARAMS_NEW_CALL_TYPE, newCallType)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[ConnectNewCallViewModel::class.java]
        viewModel.getLocalContacts()

        setContentView(bindViews())
        setSupportActionBar(toolbar)

        title = null

        setupViewPager()
        setupFloatingActionButton(0)

        newCallType = intent.getIntExtra(
            Constants.Calls.PARAMS_NEW_CALL_TYPE,
            RequestCodes.NewCall.NEW_CALL_NONE
        )

        addBackButton()
    }

    private fun addBackButton() {
        val materialMenuDrawable = MaterialMenuDrawable(
            this,
            ContextCompat.getColor(this,
                if (isNightModeEnabled(this, mSettingsManager)) R.color.white else R.color.black),
            MaterialMenuDrawable.Stroke.REGULAR
        )
        materialMenuDrawable.iconState = MaterialMenuDrawable.IconState.ARROW

        toolbar.navigationIcon = materialMenuDrawable
        toolbar.setNavigationContentDescription(R.string.back_button_accessibility_id)
        toolbar.setNavigationOnClickListener {
            analyticsManager.logEvent(
                ScreenName.CONNECT_NEW_CALL_MAIN,
                Enums.Analytics.EventName.BACK_BUTTON_PRESSED
            )
            onBackPressed()
        }

    }

    fun bindViews(): View {
        val binding = ActivityConnectMainBinding.inflate(layoutInflater)

        toolbar = binding.connectMainToolbar
        titleTextView = binding.connectMainTitle
        appBarLayout = binding.connectMainAppBarLayout
        collapsingToolbarLayout = binding.connectMainCollapsingToolbar
        viewPager = binding.connectMainViewPager
        floatingActionButton = binding.connectMainFab
        coordinatorLayout = binding.connectMainCoordinatorLayout

        bottomNavigation = binding.connectMainBottomNav
        bottomNavigation.setOnItemSelectedListener { item ->
            viewPager.currentItem = when (item.itemId) {
                R.id.connect_main_bottom_navigation_item_1 -> viewPagerAdapter.callsTabIndex
                R.id.connect_main_bottom_navigation_item_2 -> viewPagerAdapter.contactsTabIndex
                else -> viewPager.currentItem
            }

            bottomNavigation.setIcons(item.itemId)
            setupFloatingActionButton(viewPager.currentItem)

            true
        }

        overrideEdgeToEdge(binding.root)
        return binding.root
    }

    fun setNewViewPagerPosition(position: Int) {
        when (position) {
            viewPagerAdapter.callsTabIndex -> {
                titleTextView.text = getString(R.string.connect_calls_title)
                bottomNavigation.setItemSelected(R.id.connect_main_bottom_navigation_item_1)
                appBarLayout.visibility = View.VISIBLE
            }
            viewPagerAdapter.contactsTabIndex -> {
                titleTextView.text = getString(R.string.connect_contacts_toolbar_title)
                bottomNavigation.setItemSelected(R.id.connect_main_bottom_navigation_item_5)
                appBarLayout.visibility = View.VISIBLE
            }
        }

        setupFloatingActionButton(position)
    }

    private fun setupViewPager() {
        viewPagerAdapter = ConnectNewCallViewPagerAdapter(this) { isFocused -> viewPagerAdapterHideShowOnFocus(isFocused) }

        viewPager.offscreenPageLimit = viewPagerAdapter.itemCount - 1
        viewPager.adapter = viewPagerAdapter
        viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setNewViewPagerPosition(position)
                invalidateOptionsMenu()
            }
        })
        setupBottomNavigationMenu()
    }

    private fun viewPagerAdapterHideShowOnFocus(isFocused: Boolean){
        val showHideViewState = if(isFocused) View.GONE else View.VISIBLE

        collapsingToolbarLayout.visibility = showHideViewState
        floatingActionButton.visibility = showHideViewState
        bottomNavigation.visibility = showHideViewState
    }

    private fun setupBottomNavigationMenu() {
        bottomNavigation.menu.clear()
        (viewPager.adapter as? ConnectNewCallViewPagerAdapter)?.let {
            bottomNavigation.menu.add(
                Menu.NONE,
                R.id.connect_main_bottom_navigation_item_1,
                viewPagerAdapter.callsTabIndex,
                R.string.connect_main_bottom_navigation_calls
            )
            bottomNavigation.menu.add(
                Menu.NONE,
                R.id.connect_main_bottom_navigation_item_2,
                viewPagerAdapter.contactsTabIndex,
                R.string.connect_main_bottom_navigation_contacts
            )
        }
    }

    private fun setupFloatingActionButton(position: Int) {
        when (position) {
            viewPagerAdapter.callsTabIndex -> {
                floatingActionButton.setImageDrawable(FontDrawable(this,
                        R.string.fa_custom_dialer,
                        Enums.FontAwesomeIconType.CUSTOM)
                        .withColor(ContextCompat.getColor(this, R.color.connectWhite)))
                floatingActionButton.setOnClickListener { BottomSheetDialerFragment().show(supportFragmentManager, null) }
                floatingActionButton.show()
            }
            viewPagerAdapter.contactsTabIndex -> {
                floatingActionButton.hide()
            }
        }
    }

    private fun processParticipantInfo(
        participantInfo: ParticipantInfo,
        @Screen analyticsScreenName: String
    ) {
        callManager.processParticipantInfo(
            this,
            analyticsScreenName,
            participantInfo,
            null,
            compositeDisposable,
            this
        )
    }

    // --------------------------------------------------------------------------------------------
    // CallManager.Process
    // CallBack Methods
    // --------------------------------------------------------------------------------------------
    override fun onParticipantInfoProcessed(
        activity: Activity,
        @Screen analyticsScreenName: String,
        participantInfo: ParticipantInfo,
        retrievalNumber: String?,
        compositeDisposable: CompositeDisposable
    ) {
        mLogManager.logToFile(
            Enums.Logging.STATE_INFO,
            R.string.log_message_success_with_message,
            activity.getString(R.string.log_message_processing_call, participantInfo.toString())
        )
        val callback = PermissionGrantedCallback {
            val data = Intent()
            data.putExtra(Constants.EXTRA_PARTICIPANT_INFO, participantInfo)
            data.putExtra(Constants.EXTRA_RETRIEVAL_NUMBER, retrievalNumber)
            setResult(RESULT_OK, data)
            finish()
        }
        if (participantInfo.callType == Enums.Sip.CallTypes.VIDEO) {
            permissionManager.requestVideoCallPermission(
                this,
                analyticsScreenName,
                callback,
                null
            )
        } else if (participantInfo.callType == Enums.Sip.CallTypes.VOICE) {
            permissionManager.requestVoiceCallPermission(
                this,
                analyticsScreenName,
                callback
            )
        }
    }
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------------
    // ContactsListFragment.ContactsListFragmentListener Methods
    // --------------------------------------------------------------------------------------------
    fun onContactListItemClicked(fragment: Fragment, listItem: ContactListItem) {
        val participantInfo = listItem.data.getParticipantInfo(null)
        participantInfo.dialingServiceType = DialingServiceTypes.VOIP

        if (newCallType == RequestCodes.NewCall.TRANSFER_REQUEST_CODE || newCallType == RequestCodes.NewCall.CONFERENCE_REQUEST_CODE) {
            participantInfo.callType = Enums.Sip.CallTypes.VOICE
        }
        processParticipantInfo(participantInfo, ScreenName.NEW_CALL_CONTACTS_LIST)
    }
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------------
    // DialerFragment.DialerFragmentListener Methods
    // --------------------------------------------------------------------------------------------
    fun onProcessCall(participantInfo: ParticipantInfo) {
        participantInfo.dialingServiceType = DialingServiceTypes.VOIP
        processParticipantInfo(participantInfo, ScreenName.NEW_CALL_DIALER)
    }
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------------
    // CallHistoryListFragment.CallHistoryListFragmentListener Methods
    // --------------------------------------------------------------------------------------------
    fun onCallHistoryListItemClicked(fragment: Fragment, listItem: CallHistoryListItem) {
        if (TextUtils.isEmpty(listItem.data.phoneNumber)) {
            mDialogManager.showErrorDialog(this, ScreenName.NEW_CALL_MAIN)
            return
        }

        var participantInfo = ParticipantInfo(numberToCall = listItem.data.phoneNumber!!,
            dialingServiceType = DialingServiceTypes.VOIP)

        if (newCallType == RequestCodes.NewCall.TRANSFER_REQUEST_CODE || newCallType == RequestCodes.NewCall.CONFERENCE_REQUEST_CODE) {
            participantInfo.callType = Enums.Sip.CallTypes.VOICE
        }
        processParticipantInfo(participantInfo, ScreenName.NEW_CALL_CALL_HISTORY_LIST)
    }
    // --------------------------------------------------------------------------------------------

}