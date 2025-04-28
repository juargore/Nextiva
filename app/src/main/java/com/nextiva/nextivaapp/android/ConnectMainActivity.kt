package com.nextiva.nextivaapp.android

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.provider.Settings
import android.text.TextUtils
import android.view.Menu
import android.view.View
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.widget.Toolbar
import androidx.compose.ui.platform.ComposeView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.perf.FirebasePerformance
import com.nextiva.nextivaapp.android.adapters.ConnectMainViewPagerAdapter
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BottomSheetMenuListItem
import com.nextiva.nextivaapp.android.constants.Constants
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.constants.Enums.Analytics.ScreenName
import com.nextiva.nextivaapp.android.constants.Enums.Logging.PendoUserDatas
import com.nextiva.nextivaapp.android.constants.Enums.Logging.UserDatas
import com.nextiva.nextivaapp.android.constants.Enums.Platform.ViewsToShow.*
import com.nextiva.nextivaapp.android.core.common.ui.BottomSheetSelectContacts
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.features.messaging.view.BottomSheetNewMessage
import com.nextiva.nextivaapp.android.features.rooms.db.RoomsDbManager
import com.nextiva.nextivaapp.android.features.rooms.viewmodel.NewMessageContactSelection
import com.nextiva.nextivaapp.android.fragments.ConnectSettingsFragment
import com.nextiva.nextivaapp.android.fragments.bottomsheets.*
import com.nextiva.nextivaapp.android.fragments.bottomsheets.importwizard.BottomSheetImportWizard
import com.nextiva.nextivaapp.android.interfaces.BackFragmentListener
import com.nextiva.nextivaapp.android.listeners.NavigationListener
import com.nextiva.nextivaapp.android.managers.interfaces.DialogManager
import com.nextiva.nextivaapp.android.managers.interfaces.LocalContactsManager
import com.nextiva.nextivaapp.android.managers.interfaces.PermissionManager
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager
import com.nextiva.nextivaapp.android.core.common.ui.ActiveCallBanner
import com.nextiva.nextivaapp.android.db.DbManagerKt
import com.nextiva.nextivaapp.android.meetings.MeetingActivity
import com.nextiva.nextivaapp.android.models.AvatarInfo
import com.nextiva.nextivaapp.android.models.BottomNavigationItem
import com.nextiva.nextivaapp.android.util.ApplicationUtil.isNightModeEnabled
import com.nextiva.nextivaapp.android.util.UIUtil
import com.nextiva.nextivaapp.android.util.extensions.withFontAwesomeDrawable
import com.nextiva.nextivaapp.android.util.fontawesome.FontDrawable
import com.nextiva.nextivaapp.android.view.AvatarView
import com.nextiva.nextivaapp.android.view.ConnectBottomNavigationView
import com.nextiva.nextivaapp.android.viewmodels.ConnectMainViewModel
import com.nextiva.pjsip.pjsip_lib.sipservice.SipCall
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import sdk.pendo.io.Pendo
import javax.inject.Inject

@AndroidEntryPoint
class ConnectMainActivity : BaseActivity(), NavigationListener {

    @Inject
    lateinit var permissionManager: PermissionManager
    @Inject
    lateinit var localContactsManager: LocalContactsManager
    @Inject
    lateinit var sharedPreferencesManager: SharedPreferencesManager
    @Inject
    lateinit var dialogManager: DialogManager
    @Inject
    lateinit var dbManager: DbManager
    @Inject
    lateinit var dbManagerKt: DbManagerKt
    @Inject
    lateinit var roomsDbManager: RoomsDbManager
    @Inject
    lateinit var sessionManager: SessionManager

    private var viewToShow = NO_VIEW
    private lateinit var toolbar: Toolbar
    private lateinit var appBarLayout: AppBarLayout
    private lateinit var collapsingToolbarLayout: CollapsingToolbarLayout
    private lateinit var titleTextView: TextView
    private lateinit var viewPager: ViewPager2
    private lateinit var bottomNavigation: ConnectBottomNavigationView
    private lateinit var floatingActionButton: FloatingActionButton
    private lateinit var coordinatorLayout: CoordinatorLayout
    private lateinit var profileAvatar: AvatarView
    private lateinit var activeCallBar: ComposeView

    private lateinit var viewPagerAdapter: ConnectMainViewPagerAdapter

    private lateinit var viewModel: ConnectMainViewModel
    private var sessionCheckHandler: Handler? = null
    private var sessionCheckRunnable: Runnable? = null
    private val SESSION_CHECK_INTERVAL = Constants.ONE_SECOND_IN_MILLIS * 15

    private val ownAvatarObserver = Observer<AvatarInfo> { avatar: AvatarInfo? ->
        avatar?.let {
            profileAvatar.setAvatar(avatar)
        }
    }

    private val editModeObserver = Observer<Boolean> { isEnabled ->
        viewPagerAdapterHideShowOnEditView(isEnabled)
    }

    private val activeCallObserver = Observer<SipCall?> { sipCall ->
        if (sipCall != null) {
            MainScope().launch(Dispatchers.IO) {
                val contact = sipCall.participantInfoList.firstOrNull()?.numberToCall?.let { dbManagerKt.getContactFromPhoneNumberInThread(it).value }

                withContext(Dispatchers.Main) {
                    activeCallBar.setContent {
                        ActiveCallBanner(sipCall, contact, !isNightModeEnabled(applicationContext, mSettingsManager), viewModel.sipManager.activeCallDurationLiveData) {
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

    private lateinit var batteryOptimizationPermissions: ActivityResultLauncher<Intent>

    private val unreadCallsVoicemailObserver = Observer<Int> {
        setBadgeCount(ConnectMainViewPagerAdapter.FeatureType.Calls, it)
    }

    private val unreadMessagesObserver = Observer<Int> {
        setBadgeCount(ConnectMainViewPagerAdapter.FeatureType.Messaging, it)
    }

    private val unreadRoomsMessagesObserver = Observer<Int> {
        setBadgeCount(ConnectMainViewPagerAdapter.FeatureType.Rooms, it)
    }

    private val unreadChatsObserver = Observer<Int> {
        setBadgeCount(ConnectMainViewPagerAdapter.FeatureType.Chat, it)
    }

    private fun setBadgeCount(featureType: ConnectMainViewPagerAdapter.FeatureType, badgeCount: Int) {
        if (bottomNavigation.getBottomNavigationItems().isNotEmpty()) {
            var navPosition = -1
            bottomNavigation.getBottomNavigationItems()
                .forEachIndexed { index, bottomNavigationItem ->
                    if (bottomNavigationItem.itemId == featureType && index < itemPositionIdList.size - 1) {
                        navPosition = index
                    }
                }

            if (navPosition > -1 && navPosition < itemPositionIdList.size) {
                bottomNavigation.setBadge(itemPositionIdList[navPosition], badgeCount)
            }
        }
    }

    private val itemPositionIdList = arrayListOf(
        R.id.connect_main_bottom_navigation_item_1,
        R.id.connect_main_bottom_navigation_item_2,
        R.id.connect_main_bottom_navigation_item_3,
        R.id.connect_main_bottom_navigation_item_4,
        R.id.connect_main_bottom_navigation_item_5
    )

    companion object {
        var created = false
        var shouldShowMessagingTab = false
        fun newIntent(context: Context): Intent {
            val intent = Intent(context, ConnectMainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                    Intent.FLAG_ACTIVITY_NEW_TASK

            return intent
        }

        @JvmStatic
        fun newIntent(
            context: Context,
            @ViewToShow viewToShow: Int
        ): Intent {
            val intent = newIntent(context)
            intent.putExtra(Constants.Navigation.PARAMS_VIEW_TO_SHOW, viewToShow)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        batteryOptimizationPermissions =
            registerForActivityResult(StartActivityForResult()) { result: ActivityResult ->
                sharedPreferencesManager.setBoolean(
                    SharedPreferencesManager.BATTERY_OPTIMIZATION_IGNORE_REQUEST_SHOWN,
                    true
                )

                if (result.resultCode == RESULT_OK) {
                    mLogManager.logToFile(
                        Enums.Logging.STATE_INFO,
                        "Battery Optimization is now ignored."
                    )
                } else if (result.resultCode == RESULT_CANCELED) {
                    mLogManager.logToFile(
                        Enums.Logging.STATE_FAILURE,
                        "Request to ignore battery optimization was denied."
                    )
                }
            }

        viewToShow = intent.getIntExtra(Constants.Navigation.PARAMS_VIEW_TO_SHOW, NO_VIEW)

        val firebaseLoginTrace = FirebasePerformance.startTrace(Enums.Analytics.FirebasePerformance.LOGIN)
        firebaseLoginTrace.stop()

        viewModel = ViewModelProvider(this)[ConnectMainViewModel::class.java]

        viewModel.getLocalContacts()

        setContentView(bindViews())
        setSupportActionBar(toolbar)

        viewModel.unreadVoiceCallVoicemailMediatorLiveData.observe(this, unreadCallsVoicemailObserver)
        viewModel.unreadChatSmsMediatorLiveData.observe(this, unreadMessagesObserver)
        viewModel.unreadRoomsMediatorLiveData.observe(this, unreadRoomsMessagesObserver)
        viewModel.unreadChatsMediatorLiveData.observe(this, unreadChatsObserver)
        viewModel.activeCallLiveData.observe(this, activeCallObserver)

        viewModel.getUsersAvatarLiveData().observe(this, ownAvatarObserver)
        viewModel.onEditModeEnabledLiveData.observe(this, editModeObserver)
        viewModel.getUsersAvatar()

        title = null

        setupViewPager(savedInstanceState)
        setupFloatingActionButton(0)
        viewModel.setUnreadBadgeCounts(applicationContext)

        handleAppLoad()
        created = true
        viewModel.getDevicePolicies()
        viewModel.getUserIsSuperAdmin()

        // Initialize session check handler and runnable
        sessionCheckHandler = Handler(Looper.getMainLooper())
        sessionCheckRunnable = object : Runnable {
            override fun run() {
                checkSession()
                sessionCheckHandler!!.postDelayed(this, SESSION_CHECK_INTERVAL)
            }
        }

        // Start the session check runnable
        sessionCheckHandler!!.post(sessionCheckRunnable!!)
    }

    override fun onDestroy() {
        super.onDestroy()
        created = false

        // Stop the session check runnable
        sessionCheckHandler?.removeCallbacks(sessionCheckRunnable!!)
    }

    override fun onPause() {
        super.onPause()

        // Stop the session check runnable
        sessionCheckHandler?.removeCallbacks(sessionCheckRunnable!!)
    }

    override fun onResume() {
        super.onResume()

        if (shouldShowMessagingTab) {
            viewPager.currentItem = 1
            shouldShowMessagingTab = false
        }

        if (viewModel.isSmsMessagesEnabled() != viewPagerAdapter.isSmsEnabled ||
            viewModel.isMeetingEnabled(applicationContext) != viewPagerAdapter.isMeetingEnabled ||
            viewModel.isRoomsEnabled(applicationContext) != viewPagerAdapter.isRoomsEnabled
        ) {
            recreate()
        }

        viewModel.setUnreadBadgeCounts(this)

        // Start the session check runnable
        sessionCheckHandler?.post(sessionCheckRunnable!!)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        ((viewPagerAdapter as? ConnectMainViewPagerAdapter)
            ?.getFragment(viewPager.currentItem) as? BackFragmentListener)
            ?.let { fragment ->
                fragment.onBackPressed()
            }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(Constants.TAB, viewPager.currentItem)
    }

    private fun handleAppLoad() {
        permissionManager.requestInitialPermissions(
            this,
            ScreenName.MAIN) { checkBatteryOptimizationSettings() }

        viewModel.initialLoad()

        try {
            settingCrashlyticsData()
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }

        viewModel.createLogPostWorker()
        viewModel.cancelNotifications()
    }

    fun bindViews(): View {
        val binding = com.nextiva.nextivaapp.android.databinding.ActivityConnectMainBinding.inflate(layoutInflater)

        toolbar = binding.connectMainToolbar
        titleTextView = binding.connectMainTitle
        appBarLayout = binding.connectMainAppBarLayout
        collapsingToolbarLayout = binding.connectMainCollapsingToolbar
        viewPager = binding.connectMainViewPager
        floatingActionButton = binding.connectMainFab
        coordinatorLayout = binding.connectMainCoordinatorLayout
        profileAvatar = binding.connectProfileIcon

        bottomNavigation = binding.connectMainBottomNav
        bottomNavigation.setOnItemSelectedListener { item ->
            if (item.title?.equals(getString(R.string.connect_main_bottom_navigation_more)) == true) {
                val bottomNavigationMore = BottomSheetBottomNavigationMore()

                val bundle = Bundle()
                bundle.putSerializable(bottomNavigationMore.BOTTOM_NAVIGATION_ITEM_LIST, viewModel.getMoreMenuListItems(bottomNavigation))
                bottomNavigationMore.arguments = bundle
                bottomNavigationMore.show(supportFragmentManager, null)
            } else {
                viewPager.currentItem = item.order
                bottomNavigation.setIcons(item.itemId)
                setupFloatingActionButton(viewPager.currentItem)
            }
            true
        }

        profileAvatar.setOnClickListener {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.add(R.id.fragment_container, ConnectSettingsFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }

        titleTextView.setOnClickListener {
            viewPagerAdapter.scrollToTop(viewPager.currentItem)
        }

        viewPager.isUserInputEnabled = false
        activeCallBar = binding.activeCallToolbar

        overrideEdgeToEdge(binding.root)
        return binding.root
    }


    private fun setupBottomNavigationMenu() {
        bottomNavigation.menu.clear()
        bottomNavigation.setFeatureFlags(viewModel.isSmsMessagesEnabled(), viewModel.isMeetingEnabled(applicationContext), viewModel.isRoomsEnabled(applicationContext))

        (viewPager.adapter as? ConnectMainViewPagerAdapter)?.let {
            val itemCount = bottomNavigation.getBottomNavigationItems().size

            for ((count, itemPositionId) in itemPositionIdList.withIndex()) {
                if (itemCount > itemPositionIdList.size && itemPositionId == itemPositionIdList.last()) {
                    bottomNavigation.menu.add(
                        Menu.NONE,
                        itemPositionId,
                        count,
                        R.string.connect_main_bottom_navigation_more
                    ).icon = UIUtil.getFontAwesomeDrawable(this, R.drawable.more_icon)
                } else if (count < itemCount) {
                    bottomNavigation.menu.add(
                        Menu.NONE,
                        itemPositionId,
                        count,
                        bottomNavigation.getBottomNavigationItems()[count].title
                    )
                }
            }
        }
    }

    private fun getBottomNavigationItemFromId(viewToShow: Int): BottomNavigationItem? {
        return bottomNavigation.getBottomNavigationItems().find { it.itemId == bottomNavigation.getFeatureTypeFromViewToShow(viewToShow) }
    }

    fun setSearching(isSearching: Boolean) {
        if (isSearching) {
            bottomNavigation.visibility = View.GONE
            toolbar.setContentInsetsAbsolute(
                toolbar.contentInsetLeft,
                resources.getDimension(R.dimen.general_padding_medium).toInt()
            )

        } else {
            bottomNavigation.visibility = View.VISIBLE
            toolbar.setContentInsetsAbsolute(toolbar.contentInsetLeft, 0)
        }
    }

    fun setNewViewPagerPosition(position: Int) {
        val title =
            if(position < 4)
                bottomNavigation.getBottomNavigationItems()[position].title
            else if(getBottomNavigationItemFromId(viewToShow) != null)
                getBottomNavigationItemFromId(viewToShow)!!.title
            else
                ""


        if (bottomNavigation.getBottomNavigationItems()[position].itemId == ConnectMainViewPagerAdapter.FeatureType.More ||
            bottomNavigation.getBottomNavigationItems()[position].itemId == ConnectMainViewPagerAdapter.FeatureType.Unknown ||
            position >= itemPositionIdList.size)
        {
            bottomNavigation.getBottomNavigationItems()[position].title?.let {
                setPageFrameState(it, -1, View.INVISIBLE)
            }
        }
        else
        {
            setPageFrameState(title, itemPositionIdList[position], View.VISIBLE)
        }



        setupFloatingActionButton(position)
    }

    private fun setPageFrameState(title: String, bottomNavItemId: Int, isAppBarVisible: Int) {
        titleTextView.text = title
        bottomNavigation.setItemSelected(bottomNavItemId)
        appBarLayout.visibility = isAppBarVisible
    }

    private fun setupViewPager(savedInstanceState: Bundle? = null) {
        bottomNavigation.setFeatureFlags(
            viewModel.isSmsMessagesEnabled(),
            viewModel.isMeetingEnabled(applicationContext),
            viewModel.isRoomsEnabled(applicationContext)
        )
        viewPagerAdapter = ConnectMainViewPagerAdapter(
            this,
            viewModel.isSmsMessagesEnabled(),
            viewModel.isMeetingEnabled(applicationContext),
            viewModel.isRoomsEnabled(applicationContext),
            bottomNavigation.getBottomNavigationItems(),
            bottomNavigation.menu.size(),
            viewToShow
        ) { isFocused ->
            viewPagerAdapterHideShowOnFocus(isFocused)
        }
        //Offscreen page limit must be OFFSCREEN_PAGE_LIMIT_DEFAULT or a number > 0
        if(viewPagerAdapter.itemCount > 2)
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
        var tab = 0
        bottomNavigation.getBottomNavigationItems().forEachIndexed { index, bottomNavigationItem ->
            if (bottomNavigationItem.itemId == getBottomNavigationIDofViewToShow()) {
                if (index < itemPositionIdList.size - 1) {
                    tab = index
                } else
                {
                    tab = itemPositionIdList.size-1
                    return@forEachIndexed
                }
            }
        }


        viewPager.currentItem = if(viewToShow != NO_VIEW) tab else savedInstanceState?.getInt(Constants.TAB) ?: 0
    }

    private fun getBottomNavigationIDofViewToShow(): ConnectMainViewPagerAdapter.FeatureType {
        return when(viewToShow){
            CALLS -> ConnectMainViewPagerAdapter.FeatureType.Calls
            CALLS_MISSED -> ConnectMainViewPagerAdapter.FeatureType.Calls
            CALLS_VOICEMAIL -> ConnectMainViewPagerAdapter.FeatureType.Voicemail
            CONTACTS -> ConnectMainViewPagerAdapter.FeatureType.Contacts
            MESSAGING -> ConnectMainViewPagerAdapter.FeatureType.Messaging
            MEETINGS -> ConnectMainViewPagerAdapter.FeatureType.Meetings
            ROOMS -> ConnectMainViewPagerAdapter.FeatureType.Rooms
            CHAT -> ConnectMainViewPagerAdapter.FeatureType.Chat
            MORE -> ConnectMainViewPagerAdapter.FeatureType.More
            else -> ConnectMainViewPagerAdapter.FeatureType.Calls

        }
    }

    private fun viewPagerAdapterHideShowOnFocus(isFocused: Boolean){
        val showHideViewState = if(isFocused) View.GONE else View.VISIBLE

        collapsingToolbarLayout.visibility = showHideViewState
        floatingActionButton.visibility = showHideViewState
        bottomNavigation.visibility = showHideViewState
    }

    private fun viewPagerAdapterHideShowOnEditView(isEnabled: Boolean){
        val showHideViewState = if(isEnabled) View.GONE else View.VISIBLE

        floatingActionButton.visibility = showHideViewState
        bottomNavigation.visibility = showHideViewState
    }

    private fun setupFloatingActionButton(position: Int) {
        when (viewPagerAdapter.getFeatureType(position)) {
            ConnectMainViewPagerAdapter.FeatureType.Calls,
            ConnectMainViewPagerAdapter.FeatureType.Voicemail -> {
                floatingActionButton.setImageDrawable(
                    FontDrawable(
                        this,
                        R.string.fa_custom_dialer,
                        Enums.FontAwesomeIconType.CUSTOM
                    )
                        .withColor(ContextCompat.getColor(this, R.color.connectWhite))
                )
                floatingActionButton.setOnClickListener {
                    BottomSheetDialerFragment().show(
                        supportFragmentManager,
                        null
                    )
                }
                enableFloatingActionButton(true)
            }
            ConnectMainViewPagerAdapter.FeatureType.Messaging -> {
                floatingActionButton.setImageDrawable(
                    FontDrawable(
                        this,
                        R.string.fa_comment_plus,
                        Enums.FontAwesomeIconType.REGULAR
                    )
                        .withColor(ContextCompat.getColor(this, R.color.connectWhite))
                )
                floatingActionButton.setOnClickListener {
                    if (viewModel.isSmsLicenseEnabled() && (viewModel.isSmsMessagesEnabled() || viewModel.isTeamSmsEnabled())) {
                        BottomSheetNewMessage().show(supportFragmentManager, null)
                    } else {
                        val dialogTitle = if (!viewModel.isSmsLicenseEnabled()) getString(R.string.invalid_license_dialog_title) else getString(R.string.invalid_provisioning_dialog_title)
                        val dialogBody = if (!viewModel.isSmsLicenseEnabled()) getString(R.string.invalid_license_dialog_body) else getString(
                            R.string.invalid_provisioning_dialog_body
                        )

                        dialogManager.showDialog(
                            this,
                            dialogTitle,
                            dialogBody,
                            getString(R.string.general_ok)
                        ) { _, _ -> }
                    }
                }
                enableFloatingActionButton(viewModel.isSmsProvisioningEnabled() || viewModel.isTeamSmsEnabled())
            }
            ConnectMainViewPagerAdapter.FeatureType.Chat -> {
                floatingActionButton.setImageDrawable(
                    FontDrawable(
                        this,
                        R.string.fa_comments_alt,
                        Enums.FontAwesomeIconType.REGULAR
                    )
                        .withColor(ContextCompat.getColor(this, R.color.connectWhite))
                )
                floatingActionButton.setOnClickListener {
                    val contactSelectionInterface = NewMessageContactSelection(
                        dbManager = dbManager,
                        roomsDbManager = roomsDbManager,
                        sessionManager = sessionManager
                    )
                    BottomSheetSelectContacts(contactSelectionInterface).show(supportFragmentManager, null)
                }
                enableFloatingActionButton(true)
            }
            ConnectMainViewPagerAdapter.FeatureType.Meetings,
            ConnectMainViewPagerAdapter.FeatureType.Calendar -> {
                floatingActionButton.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.ic_video_on)
                )
                floatingActionButton.setOnClickListener {
                    val meetingIntent = MeetingActivity.newIntent(applicationContext, true)
                    startActivity(meetingIntent)
                }
                enableFloatingActionButton(true)
            }
            ConnectMainViewPagerAdapter.FeatureType.Contacts -> {
                floatingActionButton.setImageDrawable(FontDrawable(this,
                    R.string.fa_user_plus,
                    Enums.FontAwesomeIconType.REGULAR
                )
                    .withColor(ContextCompat.getColor(this, R.color.connectWhite))
                )
                floatingActionButton.setOnClickListener {
                    val bottomSheetListItems = arrayListOf(
                        BottomSheetMenuListItem(
                            getString(R.string.bottom_sheet_contact_menu_create_business),
                            R.string.fa_user
                        ),
                        BottomSheetMenuListItem(
                            getString(R.string.bottom_sheet_contact_menu_import_local),
                            R.string.fa_plus_circle
                        )
                    )

                    BottomSheetMenu(bottomSheetListItems) { listItem ->
                        when (listItem.text) {
                            getString(R.string.bottom_sheet_contact_menu_create_business) -> startActivity(
                                CreateBusinessContactActivity.newIntent(this)
                            )
                            getString(R.string.bottom_sheet_contact_menu_import_local) -> {
                                if (ContextCompat.checkSelfPermission(
                                        this,
                                        Manifest.permission.READ_CONTACTS
                                    ) != PackageManager.PERMISSION_GRANTED
                                ) {
                                    BottomSheetAllowContactAccess().show(
                                        supportFragmentManager,
                                        null
                                    )
                                } else {
                                    BottomSheetImportWizard.newInstance(
                                        viewModel.hasLocalContacts(),
                                        true
                                    ).show(this.supportFragmentManager, null)
                                }
                            }
                        }

                    }.show(supportFragmentManager, null)
                }
                enableFloatingActionButton(true)
            }
            ConnectMainViewPagerAdapter.FeatureType.Rooms -> {
                floatingActionButton.setImageDrawable(FontDrawable(this,
                    R.string.fa_door_open,
                    Enums.FontAwesomeIconType.REGULAR
                )
                    .withColor(ContextCompat.getColor(this, R.color.connectWhite))
                )
                floatingActionButton.setOnClickListener {

                }
                floatingActionButton.hide()  //TODO: show correct icon
            }
            ConnectMainViewPagerAdapter.FeatureType.More,
            ConnectMainViewPagerAdapter.FeatureType.Unknown -> {
                floatingActionButton.hideMotionSpec
            }
        }
    }

    private fun enableFloatingActionButton(enabled: Boolean) {
        floatingActionButton.isEnabled = enabled
        floatingActionButton.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(this, if (enabled) R.color.connectPrimaryBlue else R.color.connectGrey03)
        )
        floatingActionButton.show()
    }

    fun showSnackbar(text: String, icon: FontDrawable) {
        Snackbar.make(coordinatorLayout, text, Snackbar.LENGTH_SHORT)
            .withFontAwesomeDrawable(icon).show()
    }

    @SuppressLint("BatteryLife")
    private fun checkBatteryOptimizationSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val powerManager = getSystemService(POWER_SERVICE) as PowerManager
            FirebaseCrashlytics.getInstance().setCustomKey(
                UserDatas.BATTERY_OPTIMIZATION_ENABLED,
                if (powerManager.isIgnoringBatteryOptimizations(packageName)) getString(R.string.general_on) else getString(
                    R.string.general_off
                )
            )

            if (!powerManager.isIgnoringBatteryOptimizations(packageName)) {
                if (!sharedPreferencesManager.getBoolean(
                        SharedPreferencesManager.BATTERY_OPTIMIZATION_IGNORE_REQUEST_SHOWN,
                        false
                    )
                ) {
                    mLogManager.logToFile(
                        Enums.Logging.STATE_INFO,
                        "Request to ignore battery optimization will be shown."
                    )
                    val intent = Intent()
                    intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                    intent.data = Uri.parse("package:$packageName")
                    batteryOptimizationPermissions.launch(intent)

                } else {
                    mLogManager.logToFile(
                        Enums.Logging.STATE_FAILURE,
                        "User has chosen to Deny our request to ignore battery optimization."
                    )
                }

            } else {
                mLogManager.logToFile(
                    Enums.Logging.STATE_INFO,
                    "Battery optimization is already ignored."
                )
            }
        }
    }

    override fun performBack() {
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    @Throws(java.lang.Exception::class)
    private fun settingCrashlyticsData() {
        (viewModel.getUserDetails()?.impId ?: viewModel.getUserDetails()?.email)?.let {
            FirebaseCrashlytics.getInstance().setUserId(it)
        }

        FirebaseCrashlytics.getInstance().setCustomKey(UserDatas.LOGGED_IN, true)
        FirebaseCrashlytics.getInstance()
            .setCustomKey(UserDatas.SMS_ENABLED, sessionManager.isSmsEnabled)
        FirebaseCrashlytics.getInstance().setCustomKey(
            UserDatas.VOICE_TRANSCRIPT_ENABLED,
            sessionManager.isVoicemailTranscriptionEnabled
        )
        FirebaseCrashlytics.getInstance().setCustomKey(
            UserDatas.USER_PRESENCE_AUTOMATIC,
            sessionManager.isUserPresenceAutomatic
        )
        FirebaseCrashlytics.getInstance()
            .setCustomKey(UserDatas.INTERNET_CONNECTED, mConnectionStateManager.isInternetConnected)
        FirebaseCrashlytics.getInstance().setCustomKey(
            UserDatas.LAST_SIP_REGISTRATION_SUCCESSFUL,
            mConnectionStateManager.isSipConnected
        )
        FirebaseCrashlytics.getInstance()
            .setCustomKey(UserDatas.UMS_CONNECTED, mConnectionStateManager.isUMSConnected)
        FirebaseCrashlytics.getInstance()
            .setCustomKey(UserDatas.XMPP_CONNECTED, mConnectionStateManager.isXmppConnected)
        FirebaseCrashlytics.getInstance()
            .setCustomKey(UserDatas.CALL_CENTER_STATUS, mSettingsManager.callCenterStatus)
        FirebaseCrashlytics.getInstance()
            .setCustomKey(UserDatas.ENABLE_LOGGING, mSettingsManager.enableLogging)
        FirebaseCrashlytics.getInstance()
            .setCustomKey(UserDatas.FILE_LOGGING, mSettingsManager.fileLogging)
        FirebaseCrashlytics.getInstance()
            .setCustomKey(UserDatas.SIP_LOGGING, mSettingsManager.sipLogging)
        FirebaseCrashlytics.getInstance()
            .setCustomKey(UserDatas.XMPP_LOGGING, mSettingsManager.xmppLogging)
        FirebaseCrashlytics.getInstance()
            .setCustomKey(UserDatas.NIGHT_MODE, mSettingsManager.nightModeState)
        FirebaseCrashlytics.getInstance()
            .setCustomKey(UserDatas.IS_SHOW_SMS, sessionManager.isShowSms)
        FirebaseCrashlytics.getInstance()
            .setCustomKey(UserDatas.IS_SMS_ENABLED, sessionManager.isSmsEnabled)
        FirebaseCrashlytics.getInstance()
            .setCustomKey(UserDatas.IS_SMS_LICENSE_ENABLED, sessionManager.isSmsLicenseEnabled)
        FirebaseCrashlytics.getInstance().setCustomKey(
            UserDatas.IS_SMS_PROVISIONING_ENABLED,
            sessionManager.isSmsProvisioningEnabled
        )
        FirebaseCrashlytics.getInstance().setCustomKey(
            UserDatas.IS_CONNECT_USER_PRESENCE_AUTOMATIC,
            sessionManager.isConnectUserPresenceAutomatic
        )
        FirebaseCrashlytics.getInstance().setCustomKey(
            UserDatas.IS_NEXTIVA_CONNECT_ENABLED,
            sessionManager.isNextivaConnectEnabled
        )
        FirebaseCrashlytics.getInstance()
            .setCustomKey(UserDatas.SIP_CONNECTED, mSipManager.isRegistered())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val powerManager = getSystemService(POWER_SERVICE) as PowerManager
            FirebaseCrashlytics.getInstance().setCustomKey(
                UserDatas.BATTERY_OPTIMIZATION_ENABLED,
                !powerManager.isIgnoringBatteryOptimizations(packageName)
            )
            FirebaseCrashlytics.getInstance()
                .setCustomKey(UserDatas.POWER_SAVING_MODE_ENABLED, powerManager.isPowerSaveMode)
            FirebaseCrashlytics.getInstance()
                .setCustomKey(UserDatas.DEVICE_IDLE_MODE, powerManager.isDeviceIdleMode)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val thermalStatus = when (powerManager.currentThermalStatus) {
                    PowerManager.THERMAL_STATUS_NONE -> "NONE"
                    PowerManager.THERMAL_STATUS_LIGHT -> "LIGHT"
                    PowerManager.THERMAL_STATUS_MODERATE -> "MODERATE"
                    PowerManager.THERMAL_STATUS_SEVERE -> "SEVERE"
                    PowerManager.THERMAL_STATUS_SHUTDOWN -> "SHUTDOWN"
                    PowerManager.THERMAL_STATUS_CRITICAL -> "CRITICAL"
                    PowerManager.THERMAL_STATUS_EMERGENCY -> "EMERGENCY"
                    else -> ""
                }
                FirebaseCrashlytics.getInstance().setCustomKey(
                    UserDatas.THERMAL_STATUS,
                    thermalStatus
                )
            }
        }

        setupPendo()
        /*        FirebaseCrashlytics.getInstance()
                    .setCustomKey(UserDatas.IS_PLATFORM_USER, JwtUtil.isPlatformUser(accessToken))*/
        FirebaseCrashlytics.getInstance().setCustomKey(
            UserDatas.CORP_ACCOUNT_NUMBER,
            sessionManager.userInfo?.comNextivaCorpAccountNumber.toString()
        )
        FirebaseCrashlytics.getInstance()
            .setCustomKey(UserDatas.OKTA_USER_UUID, sessionManager.userInfo?.comNextivaUseruuid.toString())
    }

    private fun setupPendo() {
        FirebaseCrashlytics.getInstance()
            .setCustomKey(UserDatas.OKTA_USER_UUID, sessionManager.userInfo?.comNextivaUseruuid.toString())

        val accountId = viewModel.getUserAccount()?.domainName
        val visitorData = HashMap<String, Any>()
        visitorData[PendoUserDatas.PENDO_OKTA_USER_UUID] = sessionManager.userInfo?.comNextivaUseruuid.toString()
        visitorData[PendoUserDatas.PENDO_MOBILE_VERSION_ANDROID] = BuildConfig.VERSION_NAME
        val accountData = HashMap<String, Any>()
        accountData[PendoUserDatas.PENDO_CORP_ACCOUNT_NUMBER] = sessionManager.userInfo?.comNextivaCorpAccountNumber.toString()
        Pendo.startSession(
            viewModel.getUserDetails()?.email ?: viewModel.getUserDetails()?.impId ?: "",
            accountId,
            visitorData,
            accountData
        )

        sessionManager.userDetails?.email?.let {
            viewModel.fetchPendoFirstVisit(it)
            viewModel.pendoFirstVisit.observe(this) { timestamp ->
                visitorData[PendoUserDatas.PENDO_FIRST_VISIT_ANDROID] = timestamp
                Pendo.setVisitorData(visitorData)
            }
        }
    }

    private fun checkSession() {
        if (TextUtils.isEmpty(sessionManager.getSessionId())) {
            mLogManager.logToFile(Enums.Logging.STATE_INFO, "Empty session Id: ${sessionManager.getSessionId()}")
            showSessionErrorDialog()
        }
    }

    private fun showSessionErrorDialog() {
        mDialogManager.showErrorDialog(
            this,
            ScreenName.LOGIN,
            Enums.Analytics.EventName.LOGIN_FAILED_DIALOG_SHOWN,
            this.getString(R.string.login_authorization_session_expired_title),
            this.getString(R.string.login_authorization_session_expired_title_dialog_content)
        ) { dialog: MaterialDialog?, which: DialogAction? ->

            viewModel.clearSession()

            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        }
    }
}