/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.fragments.setcallsettings

import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.SetCallSettingsActivity
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.DetailItemViewListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.SimultaneousRingLocationListItem
import com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.ADD_LOCATION_BUTTON_PRESSED
import com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.BACK_BUTTON_PRESSED
import com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.DO_NOT_RING_WHILE_ON_CALL_SWITCH_CHECKED
import com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.DO_NOT_RING_WHILE_ON_CALL_SWITCH_UNCHECKED
import com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.ENABLED_SWITCH_CHECKED
import com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.ENABLED_SWITCH_UNCHECKED
import com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.LOCATION_LIST_ITEM_PRESSED
import com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.SAVE_BUTTON_PRESSED
import com.nextiva.nextivaapp.android.constants.Enums.Analytics.ScreenName.SIMULTANEOUS_RING_LOCATIONS_LIST
import com.nextiva.nextivaapp.android.constants.RequestCodes
import com.nextiva.nextivaapp.android.databinding.FragmentSetCallSettingsSimultaneousRingBinding
import com.nextiva.nextivaapp.android.fragments.GeneralRecyclerViewFragment
import com.nextiva.nextivaapp.android.interfaces.BackFragmentListener
import com.nextiva.nextivaapp.android.interfaces.CallSettingsForm
import com.nextiva.nextivaapp.android.interfaces.EntryForm
import com.nextiva.nextivaapp.android.listeners.SetCallSettingsListener
import com.nextiva.nextivaapp.android.models.ServiceSettings
import com.nextiva.nextivaapp.android.models.SimultaneousRingLocation
import com.nextiva.nextivaapp.android.viewmodels.setcallsettings.SetCallSettingsSimultaneousRingViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class SetCallSettingsSimultaneousRingFragment : GeneralRecyclerViewFragment(), EntryForm, CallSettingsForm<ServiceSettings>, BackFragmentListener {

    lateinit var enabledCheckBox: CheckBox
    lateinit var dontRingWhileOnCallCheckBox: CheckBox

    private var pendingEnabled: Boolean? = null
    private var pendingDontRingWhileOnCall: Boolean? = null

    private lateinit var viewModel: SetCallSettingsSimultaneousRingViewModel

    private val mEnabledOnCheckedChangeListener = CompoundButton.OnCheckedChangeListener { _, isChecked ->
        pendingEnabled = if (isChecked == viewModel.serviceSettings?.active) null else isChecked
        viewModel.onFormUpdated()

        mAnalyticsManager.logEvent(analyticScreenName, if (isChecked) ENABLED_SWITCH_CHECKED else ENABLED_SWITCH_UNCHECKED)
    }

    private val mDontRingWhileOnCallOnCheckedChangeListener = CompoundButton.OnCheckedChangeListener { _, isChecked ->
        pendingDontRingWhileOnCall = if (isChecked == viewModel.serviceSettings?.dontRingWhileOnCall) null else isChecked
        viewModel.onFormUpdated()

        mAnalyticsManager.logEvent(analyticScreenName, if (isChecked) DO_NOT_RING_WHILE_ON_CALL_SWITCH_CHECKED else DO_NOT_RING_WHILE_ON_CALL_SWITCH_UNCHECKED)
    }

    private val serviceSettingsEventObserver = Observer<Boolean> { isSuccessful ->
        activity?.let { activity ->
            mDialogManager.dismissProgressDialog()
            stopRefreshing()
            setupServiceSettings()

            if (!isSuccessful) {
                mDialogManager.showErrorDialog(activity, analyticScreenName)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.let { activity ->
            viewModel = ViewModelProvider(this).get(SetCallSettingsSimultaneousRingViewModel::class.java)
            viewModel.serviceSettings = arguments?.getSerializable(PARAMS_SIMULTANEOUS_RING) as ServiceSettings
            viewModel.setCallSettingsListener(activity as SetCallSettingsListener, activity) // Pass the context here

            setHasOptionsMenu(true)

            savedInstanceState?.let {
                if (savedInstanceState.containsKey(SELECTED_PENDING_ENABLED_STATE)) {
                    pendingEnabled = savedInstanceState.getBoolean(SELECTED_PENDING_ENABLED_STATE)
                }

                if (savedInstanceState.containsKey(SELECTED_PENDING_DONT_RING_WHILE_ON_CALL_STATE)) {
                    pendingDontRingWhileOnCall = savedInstanceState.getBoolean(SELECTED_PENDING_DONT_RING_WHILE_ON_CALL_STATE)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mAnalyticsManager.logScreenView(analyticScreenName)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        view?.let {
            bindViews(it)
        }

        activity?.let { activity ->
            setupServiceSettings()

            viewModel.serviceSettingsEventLiveData.observe(viewLifecycleOwner, serviceSettingsEventObserver)

            mDialogManager.showProgressDialog(activity, analyticScreenName, R.string.progress_processing)
            fetchItemList(false)
        }

        return view
    }

    private fun bindViews(view: View) {
        val binding = FragmentSetCallSettingsSimultaneousRingBinding.bind(view)

        enabledCheckBox = binding.setCallSettingsSimultaneousRingEnabledCheckBox
        dontRingWhileOnCallCheckBox = binding.setCallSettingsSimultaneousRingDontRingWhileOnCallCheckBox

        binding.setCallSettingsSimultaneousRingAddLocationImageButton.setOnClickListener { onAddLocationImageButtonClicked() }
    }

    private fun onAddLocationImageButtonClicked() {
        activity?.let { activity ->
            startActivityForResult(SetCallSettingsActivity.newIntent(activity, viewModel.serviceSettings, null as SimultaneousRingLocation?),
                RequestCodes.ADD_SIMULTANEOUS_RING_LOCATION_REQUEST_CODE)

            mAnalyticsManager.logEvent(analyticScreenName, ADD_LOCATION_BUTTON_PRESSED)
        }
    }

    private fun setupServiceSettings() {
        mAdapter.clearList()

        enabledCheckBox.setOnCheckedChangeListener(null)
        dontRingWhileOnCallCheckBox.setOnCheckedChangeListener(null)

        if (viewModel.serviceSettings != null) {
            enabledCheckBox.isChecked = pendingEnabled ?: viewModel.serviceSettings!!.active
            dontRingWhileOnCallCheckBox.isChecked = pendingDontRingWhileOnCall
                ?: viewModel.serviceSettings!!.dontRingWhileOnCall
            addLocationListItems(viewModel.getLocationListItems())

        } else {
            pendingEnabled = null
            pendingDontRingWhileOnCall = null
            enabledCheckBox.isChecked = false
            dontRingWhileOnCallCheckBox.isChecked = false
        }

        enabledCheckBox.isEnabled = mAdapter.itemCount > 0

        if (!enabledCheckBox.isEnabled) {
            pendingEnabled = null
            enabledCheckBox.isChecked = false
        }

        enabledCheckBox.setOnCheckedChangeListener(mEnabledOnCheckedChangeListener)
        dontRingWhileOnCallCheckBox.setOnCheckedChangeListener(mDontRingWhileOnCallOnCheckedChangeListener)

        showCorrectState()

        viewModel.onFormUpdated()
    }

    private fun addLocationListItems(simultaneousRingLocations: ArrayList<SimultaneousRingLocation>) {
        if (simultaneousRingLocations.isNotEmpty()) {
            for (location in simultaneousRingLocations) {
                mAdapter.addItem(SimultaneousRingLocationListItem(
                    location,
                    getString(R.string.set_call_settings_simultaneous_ring_location_title),
                    if (location.phoneNumber.isNullOrEmpty())
                        getString(R.string.phone_type_general)
                    else
                        PhoneNumberUtils.formatNumber(location.phoneNumber, Locale.getDefault().country)))
            }
        }
    }

    // --------------------------------------------------------------------------------------------
    // GeneralRecyclerViewFragment Methods
    // --------------------------------------------------------------------------------------------
    override fun getLayoutId(): Int {
        return R.layout.fragment_set_call_settings_simultaneous_ring
    }

    override fun getSwipeRefreshLayoutId(): Int {
        return 0
    }

    override fun getRecyclerViewId(): Int {
        return R.id.set_call_settings_simultaneous_ring_recycler_view
    }

    override fun getEmptyStateViewId(): Int {
        return R.id.set_call_settings_simultaneous_ring_empty_state_view
    }

    override fun getAnalyticScreenName(): String {
        return SIMULTANEOUS_RING_LOCATIONS_LIST
    }

    override fun fetchItemList(forceRefresh: Boolean) {
        activity?.let {
            if (!mIsRefreshing) {
                startRefreshing()
                viewModel.getSingleServiceSettings()
            }
        }
    }
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // MasterListListener Methods
    // --------------------------------------------------------------------------------------------
    override fun onDetailItemViewListItemClicked(listItem: DetailItemViewListItem) {
        super.onDetailItemViewListItemClicked(listItem)

        activity?.let { activity ->
            if (listItem is SimultaneousRingLocationListItem && !listItem.location.phoneNumber.isNullOrEmpty()) {
                startActivityForResult(SetCallSettingsActivity.newIntent(activity,
                    viewModel.serviceSettings,
                    listItem.location),
                    RequestCodes.ADD_SIMULTANEOUS_RING_LOCATION_REQUEST_CODE)

                mAnalyticsManager.logEvent(analyticScreenName, LOCATION_LIST_ITEM_PRESSED)
            }
        }
    }
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // EntryForm Methods
    // --------------------------------------------------------------------------------------------
    override fun changesMade(): Boolean {
        return viewModel.serviceSettings != null &&
                (viewModel.serviceSettings!!.active != enabledCheckBox.isChecked ||
                        viewModel.serviceSettings?.dontRingWhileOnCall != dontRingWhileOnCallCheckBox.isChecked)
    }

    override fun enableSaveButton(): Boolean {
        return changesMade()
    }

    override fun enableDeleteButton(): Boolean {
        return false
    }
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // CallSettingsForm Methods
    // --------------------------------------------------------------------------------------------
    override fun getFormCallSettings(): ServiceSettings {
        val serviceSettings = ServiceSettings(viewModel.serviceSettings?.type
            ?: "", viewModel.serviceSettings?.uri
            ?: "")
        serviceSettings.setActive(enabledCheckBox.isChecked)
        serviceSettings.setDontRingWhileOnCall(dontRingWhileOnCallCheckBox.isChecked)
        serviceSettings.simultaneousRingLocationsList = viewModel.serviceSettings?.simultaneousRingLocationsList
        return serviceSettings
    }

    override fun validateForm(callBack: CallSettingsForm.ValidateCallSettingCallBack<ServiceSettings>) {}

    override fun saveForm() {
        activity?.let { activity ->
            mAnalyticsManager.logEvent(analyticScreenName, SAVE_BUTTON_PRESSED)
            mDialogManager.showProgressDialog(activity, analyticScreenName, R.string.progress_processing)
            viewModel.saveServiceSettings(formCallSettings)
        }
    }

    override fun deleteForm() {}

    override fun getFormTitleResId(): Int {
        return R.string.set_call_settings_simultaneous_ring_toolbar
    }

    override fun getHelpTextResId(): Int {
        return R.string.set_call_settings_simultaneous_ring_help_text
    }
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // BackFragmentListener Methods
    // --------------------------------------------------------------------------------------------
    override fun onBackPressed() {
        mAnalyticsManager.logEvent(analyticScreenName, BACK_BUTTON_PRESSED)
    }

    companion object {

        const val PARAMS_SIMULTANEOUS_RING = "PARAMS_SIMULTANEOUS_RING"
        const val SELECTED_PENDING_ENABLED_STATE = "SELECTED_PENDING_ENABLED_STATE"
        const val SELECTED_PENDING_DONT_RING_WHILE_ON_CALL_STATE = "SELECTED_PENDING_DONT_RING_WHILE_ON_CALL_STATE"

        fun newInstance(serviceSettings: ServiceSettings): SetCallSettingsSimultaneousRingFragment {
            val args = Bundle()
            args.putSerializable(PARAMS_SIMULTANEOUS_RING, serviceSettings)

            val fragment = SetCallSettingsSimultaneousRingFragment()
            fragment.arguments = args
            return fragment
        }
    }
    // --------------------------------------------------------------------------------------------
}