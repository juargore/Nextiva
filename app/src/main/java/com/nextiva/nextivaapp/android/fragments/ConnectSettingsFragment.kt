package com.nextiva.nextivaapp.android.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nextiva.nextivaapp.android.AboutActivity
import com.nextiva.nextivaapp.android.AppPreferencesActivity
import com.nextiva.nextivaapp.android.CallSettingsActivity
import com.nextiva.nextivaapp.android.ConnectUserDetailsActivity
import com.nextiva.nextivaapp.android.LoginActivity
import com.nextiva.nextivaapp.android.OneActiveCallActivity
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.navigationDrawer.NavigationRecyclerViewAdapter
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.core.common.ui.ActiveCallBanner
import com.nextiva.nextivaapp.android.core.notifications.view.ScheduleActivity
import com.nextiva.nextivaapp.android.databinding.FragmentConnectSettingsBinding
import com.nextiva.nextivaapp.android.fragments.bottomsheets.BottomSheetSetPresence
import com.nextiva.nextivaapp.android.managers.interfaces.ConnectionStateManager
import com.nextiva.nextivaapp.android.managers.interfaces.LogManager
import com.nextiva.nextivaapp.android.managers.interfaces.SettingsManager
import com.nextiva.nextivaapp.android.models.AvatarInfo
import com.nextiva.nextivaapp.android.models.NextivaContact
import com.nextiva.nextivaapp.android.util.ApplicationUtil.isNightModeEnabled
import com.nextiva.nextivaapp.android.view.AvatarView
import com.nextiva.nextivaapp.android.view.ConnectPresenceFilterView
import com.nextiva.nextivaapp.android.viewmodels.ConnectSettingsViewModel
import com.nextiva.pjsip.pjsip_lib.sipservice.SipCall
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class ConnectSettingsFragment : BaseFragment() {

    @Inject
    lateinit var logManager: LogManager
    @Inject
    lateinit var settingsManager: SettingsManager
    @Inject
    lateinit var connectionStateManager: ConnectionStateManager

    private lateinit var navigationRecyclerView: RecyclerView
    private lateinit var navigationAvatarView: AvatarView
    private lateinit var navigationTitleView: TextView
    private lateinit var navigationSubTitleView: TextView
    private lateinit var navigationHeader: LinearLayout
    private lateinit var navigationPresenceFilter: ConnectPresenceFilterView
    private lateinit var backArrowView: RelativeLayout
    private lateinit var activeCallBar: ComposeView

    private var navigationDrawerAdapter: NavigationRecyclerViewAdapter? = null

    private lateinit var viewModel: ConnectSettingsViewModel

    private val activeCallObserver = Observer<SipCall?> { sipCall ->
        if (sipCall != null) {
            MainScope().launch(Dispatchers.IO) {
                val contact = sipCall.participantInfoList.firstOrNull()?.numberToCall?.let { viewModel.getContactFromPhoneNumber(it) }

                withContext(Dispatchers.Main) {
                    activeCallBar.setContent {
                        ActiveCallBanner(sipCall, contact, !isNightModeEnabled(requireActivity(), settingsManager), viewModel.sipManager.activeCallDurationLiveData) {
                            startActivity(OneActiveCallActivity.newIntent(requireActivity(), sipCall.participantInfoList.firstOrNull(), null))
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

    private val ownAvatarObserver = Observer<AvatarInfo> { avatar: AvatarInfo? ->
        avatar?.let {
            navigationPresenceFilter.setPresence(avatar.presence)

            avatar.presence = null
            navigationAvatarView.setAvatar(avatar)
        }
    }

    private val userContactDetailsObserver = Observer<NextivaContact?> {
        if (it?.title.isNullOrEmpty()) {
            navigationSubTitleView.visibility = View.GONE

        } else {
            navigationSubTitleView.visibility = View.VISIBLE
            navigationSubTitleView.text = it?.title
        }
        it?.presence?.let { presence ->
            navigationPresenceFilter.setPresence(presence)
        }
    }

    private var preferencesLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                updateAdapter()
            }
        }

    private val finishSignOutObserver = Observer<Void?> {
        if(mDialogManager != null) {
            mDialogManager.dismissProgressDialog()
        }
        startActivity(LoginActivity.newIntent(requireActivity()))
        requireActivity().finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return bindViews(inflater, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(
            requireActivity()
        )[ConnectSettingsViewModel::class.java]
        viewModel.getUsersAvatarLiveData().observe(viewLifecycleOwner, ownAvatarObserver)
        viewModel.finishSignOutLiveData.observe(viewLifecycleOwner, finishSignOutObserver)
        viewModel.userContactDetailsLiveData.observe(viewLifecycleOwner, userContactDetailsObserver)
        viewModel.activeCallLiveData.observe(viewLifecycleOwner, activeCallObserver)

        setupNavigationDrawer()
        viewModel.getUsersAvatar()
    }

    private fun bindViews(inflater: LayoutInflater, container: ViewGroup?): View {
        val binding = FragmentConnectSettingsBinding.inflate(inflater, container, false)

        navigationRecyclerView = binding.connectNavigationDrawerRecyclerView
        navigationAvatarView = binding.connectNavigationHeaderAvatarView
        navigationTitleView = binding.connectNavigationHeaderTitleTextView
        navigationSubTitleView = binding.connectNavigationHeaderSubTitleTextView
        navigationHeader = binding.connectNavigationHeader
        navigationPresenceFilter = binding.connectNavigationPresenceFilter
        backArrowView = binding.backArrowInclude.backArrowView
        activeCallBar = binding.activeCallToolbar

        navigationPresenceFilter.setOnClickListener {
            BottomSheetSetPresence().show(childFragmentManager, null)
        }

        backArrowView.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        return binding.root
    }

    private fun setupNavigationDrawer() {
        navigationRecyclerView.layoutManager = LinearLayoutManager(requireActivity())
        navigationRecyclerView.setHasFixedSize(true)
        updateAdapter()

        navigationTitleView.text = viewModel.getUsersFullName()
    }

    private fun updateAdapter() {
        navigationDrawerAdapter = NavigationRecyclerViewAdapter(viewModel.getNavigationItemModels()) { item ->
            when (item.title) {
                getString(R.string.main_nav_user_details) -> startActivity(ConnectUserDetailsActivity.newIntent(requireContext()))
                getString(R.string.main_nav_call_settings) -> startActivity(CallSettingsActivity.newIntent(requireActivity()))
                getString(R.string.main_nav_preferences) -> preferencesLauncher.launch(AppPreferencesActivity.newIntent(requireActivity()))
                getString(R.string.main_nav_notifications) -> startActivity(ScheduleActivity.newIntent(requireActivity()))
                getString(R.string.main_nav_about) -> startActivity(AboutActivity.newIntent(requireActivity()))
                getString(R.string.main_nav_help) -> {
                    val help = Intent(Intent.ACTION_VIEW)
                    help.data = Uri.parse(getString(R.string.help_url))
                    startActivity(help)
                }
                getString(R.string.main_nav_sign_out) -> {
                    mDialogManager.showProgressDialog(
                        requireActivity(),
                        Enums.Analytics.ScreenName.MAIN,
                        R.string.progress_signing_out
                    )

                    logManager.logToFile(
                        Enums.Logging.STATE_INFO,
                        if (!connectionStateManager.isInternetConnected) {
                            "No Internet"
                        } else {
                            "Signing out"
                        }
                    )
                    viewModel.signOut()
                }
            }
        }
        navigationRecyclerView.adapter = navigationDrawerAdapter
        navigationDrawerAdapter?.notifyDataSetChanged()
    }
}