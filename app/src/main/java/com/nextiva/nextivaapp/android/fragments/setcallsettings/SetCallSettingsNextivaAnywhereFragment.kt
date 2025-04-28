/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.fragments.setcallsettings

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.SetCallSettingsActivity
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.DetailItemViewListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.NextivaAnywhereLocationListItem
import com.nextiva.nextivaapp.android.constants.Constants
import com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.ADD_LOCATION_BUTTON_PRESSED
import com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.ALERT_ALL_LOCATIONS_SWITCH_CHECKED
import com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.ALERT_ALL_LOCATIONS_SWITCH_UNCHECKED
import com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.BACK_BUTTON_PRESSED
import com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.LOCATION_LIST_ITEM_PRESSED
import com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.SAVE_BUTTON_PRESSED
import com.nextiva.nextivaapp.android.constants.Enums.Analytics.ScreenName.NEXTIVA_ANYWHERE_LOCATIONS_LIST_SCREEN
import com.nextiva.nextivaapp.android.constants.RequestCodes
import com.nextiva.nextivaapp.android.databinding.FragmentSetCallSettingsNextivaAnywhereBinding
import com.nextiva.nextivaapp.android.fragments.GeneralRecyclerViewFragment
import com.nextiva.nextivaapp.android.interfaces.BackFragmentListener
import com.nextiva.nextivaapp.android.interfaces.CallSettingsForm
import com.nextiva.nextivaapp.android.interfaces.EntryForm
import com.nextiva.nextivaapp.android.listeners.SetCallSettingsListener
import com.nextiva.nextivaapp.android.managers.interfaces.DialogManager
import com.nextiva.nextivaapp.android.models.NextivaAnywhereLocation
import com.nextiva.nextivaapp.android.models.ServiceSettings
import com.nextiva.nextivaapp.android.util.MenuUtil
import com.nextiva.nextivaapp.android.viewmodels.setcallsettings.SetCallSettingsNextivaAnywhereViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SetCallSettingsNextivaAnywhereFragment : GeneralRecyclerViewFragment(), EntryForm, CallSettingsForm<ServiceSettings>, BackFragmentListener {

    lateinit var mAlertAllLocationsCheckBox: CheckBox

    @Inject
    lateinit var dialogManager: DialogManager

    private lateinit var viewModel: SetCallSettingsNextivaAnywhereViewModel

    private val alertAllLocationsOnCheckedChangeListener = CompoundButton.OnCheckedChangeListener { _, isChecked ->
        viewModel.pendingAlertAllLocations = if (isChecked == viewModel.serviceSettings?.alertAllLocationsForClickToDialCalls) false else isChecked
        viewModel.onFormUpdated()

        mAnalyticsManager.logEvent(analyticScreenName, if (isChecked) ALERT_ALL_LOCATIONS_SWITCH_CHECKED else ALERT_ALL_LOCATIONS_SWITCH_UNCHECKED)
    }

    private val serviceSettingsGetResponseObserver = Observer<Boolean> { isSuccessful ->
        activity?.let { activity ->
            dialogManager.dismissProgressDialog()
            stopRefreshing()
            setupServiceSettings()

            if (!isSuccessful) {
                dialogManager.showErrorDialog(activity, analyticScreenName)
            }
        }
    }

    private val saveServiceSettingsObserver = Observer<Boolean> { isSuccessful ->
        activity?.let { activity ->
            dialogManager.dismissProgressDialog()
            stopRefreshing()

            if (!isSuccessful) {
                dialogManager.showErrorDialog(activity, analyticScreenName)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SetCallSettingsNextivaAnywhereViewModel::class.java)
        viewModel.serviceSettings = arguments?.getSerializable(PARAMS_NEXTIVA_ANYWHERE) as ServiceSettings
        viewModel.setCallSettingsListener(requireActivity() as? SetCallSettingsListener, requireActivity())

        setHasOptionsMenu(true)

        savedInstanceState?.let { savedInstanceState ->
            if (savedInstanceState.containsKey(SELECTED_PENDING_ALERT_ALL_LOCATIONS_STATE)) {
                viewModel.pendingAlertAllLocations = savedInstanceState.getBoolean(SELECTED_PENDING_ALERT_ALL_LOCATIONS_STATE)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mAnalyticsManager.logScreenView(analyticScreenName)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        view?.let { bindViews(it) }

        activity?.let { activity ->
            setupServiceSettings()

            viewModel.serviceSettingsGetResponseEventLiveData.observe(viewLifecycleOwner, serviceSettingsGetResponseObserver)
            viewModel.serviceSettingsSaveResponseEventLiveData.observe(viewLifecycleOwner, saveServiceSettingsObserver)

            dialogManager.showProgressDialog(activity, analyticScreenName, R.string.progress_processing)
            fetchItemList(false)
        }

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        mToolbarListener.setToolbarTitle(R.string.set_call_settings_nextiva_anywhere_toolbar)
        MenuUtil.setMenuContentDescriptions(menu)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        viewModel.pendingAlertAllLocations?.let { pendingAlertAllLocations ->
            outState.putBoolean(SELECTED_PENDING_ALERT_ALL_LOCATIONS_STATE, pendingAlertAllLocations)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        activity?.let {
            when (requestCode) {
                RequestCodes.ADD_NEXTIVA_ANYWHERE_LOCATION_REQUEST_CODE -> {
                    if (resultCode == Activity.RESULT_OK &&
                        data != null &&
                        data.hasExtra(Constants.EXTRA_ACTION) &&
                        data.hasExtra(Constants.EXTRA_CALL_SETTINGS_VALUE)) {

                        val nextivaAnywhereLocation = data.getSerializableExtra(Constants.EXTRA_CALL_SETTINGS_VALUE) as NextivaAnywhereLocation

                        if (TextUtils.equals(Constants.ACTION_TYPE_SAVED, data.getStringExtra(Constants.EXTRA_ACTION))) {
                            val nextivaAnywhereLocationsList = viewModel.serviceSettings?.nextivaAnywhereLocationsList
                                ?: ArrayList()

                            var locationIndex = -1

                            for (i in 0 until nextivaAnywhereLocationsList.size) {
                                if (TextUtils.equals(data.getStringExtra(Constants.EXTRA_OLD_PHONE_NUMBER),
                                        nextivaAnywhereLocationsList[i].phoneNumber)) {

                                    locationIndex = i
                                    break
                                }
                            }

                            if (locationIndex > -1) {
                                nextivaAnywhereLocationsList[locationIndex] = nextivaAnywhereLocation
                                setupServiceSettings()

                            } else {
                                nextivaAnywhereLocationsList.add(nextivaAnywhereLocation)
                                processSavedLocation(data.getStringExtra(Constants.EXTRA_OLD_PHONE_NUMBER), nextivaAnywhereLocation)
                            }

                        } else if (TextUtils.equals(Constants.ACTION_TYPE_DELETED, data.getStringExtra(Constants.EXTRA_ACTION))) {
                            var removeLocation: NextivaAnywhereLocation? = null

                            viewModel.serviceSettings?.nextivaAnywhereLocationsList?.let { nextivaAnywhereLocationsList ->
                                for (searchLocation in nextivaAnywhereLocationsList) {
                                    if (TextUtils.equals(searchLocation.phoneNumber, nextivaAnywhereLocation.phoneNumber)) {
                                        removeLocation = searchLocation
                                        break
                                    }
                                }

                                if (removeLocation != null) {
                                    nextivaAnywhereLocationsList.remove(removeLocation)
                                }
                            }

                            processDeletedLocation(data.getSerializableExtra(Constants.EXTRA_CALL_SETTINGS_VALUE) as NextivaAnywhereLocation)
                        }

                        viewModel.saveSessionServiceSettings()

                        val resultData = Intent()
                        resultData.putExtra(Constants.EXTRA_SERVICE_SETTINGS, viewModel.serviceSettings)

                        viewModel.setCallSettingsListener?.onCallSettingsRetrieved(resultData)
                    }
                }
                else -> {
                    super.onActivityResult(requestCode, resultCode, data)
                }
            }
        }
    }

    private fun bindViews(view: View) {
        val binding = FragmentSetCallSettingsNextivaAnywhereBinding.bind(view)

        mAlertAllLocationsCheckBox = binding.setCallSettingsNextivaAnywhereAlertAllLocationsCheckBox
        binding.setCallSettingsNextivaAnywhereLocationsImageButton.setOnClickListener { onAddLocationImageButtonClicked() }
    }

    fun onAddLocationImageButtonClicked() {
        activity?.let { activity ->
            startActivityForResult(SetCallSettingsActivity.newIntent(activity, viewModel.serviceSettings, null as NextivaAnywhereLocation?),
                RequestCodes.ADD_NEXTIVA_ANYWHERE_LOCATION_REQUEST_CODE)

            mAnalyticsManager.logEvent(analyticScreenName, ADD_LOCATION_BUTTON_PRESSED)
        }
    }

    private fun setupServiceSettings() {
        mAdapter.clearList()

        mAlertAllLocationsCheckBox.setOnCheckedChangeListener(null)

        if (viewModel.serviceSettings != null) {
            mAlertAllLocationsCheckBox.isChecked = viewModel.pendingAlertAllLocations
                ?: viewModel.serviceSettings!!.alertAllLocationsForClickToDialCalls
            addLocationListItems(viewModel.getLocationListItems())

        } else {
            viewModel.pendingAlertAllLocations = false
            mAlertAllLocationsCheckBox.isChecked = false
        }

        mAlertAllLocationsCheckBox.setOnCheckedChangeListener(alertAllLocationsOnCheckedChangeListener)

        showCorrectState()
        viewModel.onFormUpdated()
    }

    private fun addLocationListItems(nextivaAnywhereLocations: ArrayList<NextivaAnywhereLocation>) {
        activity?.let { activity ->
            if (nextivaAnywhereLocations.isNotEmpty()) {
                for (nextivaAnywhereLocation in nextivaAnywhereLocations) {
                    mAdapter.addItem(NextivaAnywhereLocationListItem(
                        nextivaAnywhereLocation,
                        nextivaAnywhereLocation.description
                            ?: activity.getString(R.string.set_call_settings_nextiva_anywhere_phone_number),
                        nextivaAnywhereLocation.phoneNumber))
                }
            }
        }
    }

    private fun findListItem(oldPhoneNumber: String?): NextivaAnywhereLocationListItem? {
        oldPhoneNumber?.let {
            for (baseListItem in mListItems) {
                if (baseListItem is NextivaAnywhereLocationListItem && TextUtils.equals(baseListItem.location.phoneNumber, oldPhoneNumber)) {
                    return baseListItem
                }
            }
        }

        return null
    }

    private fun processSavedLocation(oldPhoneNumber: String?, nextivaAnywhereLocation: NextivaAnywhereLocation) {
        val foundListItem = findListItem(oldPhoneNumber)

        mRecyclerView.recycledViewPool.clear()

        activity?.let { activity ->
            if (foundListItem != null) {
                viewModel.serviceSettings?.nextivaAnywhereLocationsList?.let { nextivaAnywhereLocationsList ->
                    nextivaAnywhereLocationsList[nextivaAnywhereLocationsList.indexOf(foundListItem.location)] = nextivaAnywhereLocation
                }

                foundListItem.location = nextivaAnywhereLocation
                foundListItem.title = nextivaAnywhereLocation.description
                    ?: activity.getString(R.string.set_call_settings_nextiva_anywhere_phone_number)
                foundListItem.subTitle = nextivaAnywhereLocation.phoneNumber
                mAdapter.notifyItemChanged(mListItems.indexOf(foundListItem))

            } else {
                addLocationListItems(arrayListOf(nextivaAnywhereLocation))
            }
        }

        mAdapter.notifyDataSetChanged()
        showCorrectState()
    }

    private fun processDeletedLocation(nextivaAnywhereLocation: NextivaAnywhereLocation) {
        findListItem(nextivaAnywhereLocation.phoneNumber)?.let { foundListItem ->
            mRecyclerView.recycledViewPool.clear()
            mAdapter.removeItem(foundListItem)
            mAdapter.notifyDataSetChanged()
        }

        showCorrectState()
    }

    // --------------------------------------------------------------------------------------------
    // GeneralRecyclerViewFragment Methods
    // --------------------------------------------------------------------------------------------
    override fun getLayoutId(): Int {
        return R.layout.fragment_set_call_settings_nextiva_anywhere
    }

    override fun getSwipeRefreshLayoutId(): Int {
        return 0
    }

    override fun getRecyclerViewId(): Int {
        return R.id.set_call_settings_nextiva_anywhere_locations_recycler_view
    }

    override fun getEmptyStateViewId(): Int {
        return R.id.set_call_settings_nextiva_anywhere_locations_empty_state_view
    }

    override fun getAnalyticScreenName(): String {
        return NEXTIVA_ANYWHERE_LOCATIONS_LIST_SCREEN
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
            if (listItem is NextivaAnywhereLocationListItem) {
                startActivityForResult(
                    SetCallSettingsActivity.newIntent(
                        activity,
                        viewModel.serviceSettings,
                        listItem.location),
                    RequestCodes.ADD_NEXTIVA_ANYWHERE_LOCATION_REQUEST_CODE)

                mAnalyticsManager.logEvent(analyticScreenName, LOCATION_LIST_ITEM_PRESSED)
            }
        }
    }
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // EntryForm Methods
    // --------------------------------------------------------------------------------------------
    override fun changesMade(): Boolean {
        return viewModel.serviceSettings != null && viewModel.serviceSettings?.alertAllLocationsForClickToDialCalls != mAlertAllLocationsCheckBox.isChecked
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
        serviceSettings.setAlertAllLocationsForClickToDialCalls(mAlertAllLocationsCheckBox.isChecked)
        return serviceSettings
    }

    override fun validateForm(callBack: CallSettingsForm.ValidateCallSettingCallBack<ServiceSettings>) {}

    override fun saveForm() {
        activity?.let { activity ->
            mAnalyticsManager.logEvent(analyticScreenName, SAVE_BUTTON_PRESSED)
            dialogManager.showProgressDialog(activity, analyticScreenName, R.string.progress_processing)
            viewModel.saveServiceSettings(formCallSettings)
        }
    }

    override fun deleteForm() {}

    override fun getFormTitleResId(): Int {
        return R.string.set_call_settings_nextiva_anywhere_toolbar
    }

    override fun getHelpTextResId(): Int {
        return R.string.set_call_settings_nextiva_anywhere_help_text
    }
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // BackFragmentListener Methods
    // --------------------------------------------------------------------------------------------
    override fun onBackPressed() {
        mAnalyticsManager.logEvent(analyticScreenName, BACK_BUTTON_PRESSED)
    }

    companion object {

        const val PARAMS_NEXTIVA_ANYWHERE = "PARAMS_NEXTIVA_ANYWHERE"
        const val SELECTED_PENDING_ALERT_ALL_LOCATIONS_STATE = "SELECTED_PENDING_ALERT_ALL_LOCATIONS_STATE"

        fun newInstance(serviceSettings: ServiceSettings): SetCallSettingsNextivaAnywhereFragment {
            val args = Bundle()
            args.putSerializable(PARAMS_NEXTIVA_ANYWHERE, serviceSettings)

            val fragment = SetCallSettingsNextivaAnywhereFragment()
            fragment.arguments = args
            return fragment
        }
    }
    // --------------------------------------------------------------------------------------------
}