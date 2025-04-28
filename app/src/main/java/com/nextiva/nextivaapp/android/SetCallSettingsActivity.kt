/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */
package com.nextiva.nextivaapp.android

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.balysv.materialmenu.MaterialMenuDrawable
import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.constants.Enums.Analytics.ScreenName
import com.nextiva.nextivaapp.android.constants.Enums.CallSettings.FormType
import com.nextiva.nextivaapp.android.constants.FragmentTags
import com.nextiva.nextivaapp.android.core.common.ui.ActiveCallBanner
import com.nextiva.nextivaapp.android.databinding.ActivitySetCallSettingsBinding
import com.nextiva.nextivaapp.android.db.DbManagerKt
import com.nextiva.nextivaapp.android.fragments.setcallsettings.SetCallSettingsAllowTerminationFragment.Companion.newInstance
import com.nextiva.nextivaapp.android.fragments.setcallsettings.SetCallSettingsBlockCallerIdFragment
import com.nextiva.nextivaapp.android.fragments.setcallsettings.SetCallSettingsCallCenterStatusFragment
import com.nextiva.nextivaapp.android.fragments.setcallsettings.SetCallSettingsCallForwardAlwaysFragment
import com.nextiva.nextivaapp.android.fragments.setcallsettings.SetCallSettingsCallForwardWhenBusyFragment
import com.nextiva.nextivaapp.android.fragments.setcallsettings.SetCallSettingsCallForwardWhenUnansweredFragment
import com.nextiva.nextivaapp.android.fragments.setcallsettings.SetCallSettingsCallForwardWhenUnreachableFragment
import com.nextiva.nextivaapp.android.fragments.setcallsettings.SetCallSettingsDialingServiceFragment
import com.nextiva.nextivaapp.android.fragments.setcallsettings.SetCallSettingsDoNotDisturbFragment
import com.nextiva.nextivaapp.android.fragments.setcallsettings.SetCallSettingsNextivaAnywhereFragment
import com.nextiva.nextivaapp.android.fragments.setcallsettings.SetCallSettingsNextivaAnywhereLocationFragment
import com.nextiva.nextivaapp.android.fragments.setcallsettings.SetCallSettingsRemoteOfficeFragment
import com.nextiva.nextivaapp.android.fragments.setcallsettings.SetCallSettingsSimultaneousRingFragment
import com.nextiva.nextivaapp.android.fragments.setcallsettings.SetCallSettingsSimultaneousRingLocationFragment
import com.nextiva.nextivaapp.android.fragments.setcallsettings.SetCallSettingsThisPhoneNumberFragment
import com.nextiva.nextivaapp.android.interfaces.BackFragmentListener
import com.nextiva.nextivaapp.android.interfaces.CallSettingsForm
import com.nextiva.nextivaapp.android.interfaces.EntryForm
import com.nextiva.nextivaapp.android.listeners.SetCallSettingsListener
import com.nextiva.nextivaapp.android.listeners.ToolbarListener
import com.nextiva.nextivaapp.android.managers.interfaces.AnalyticsManager
import com.nextiva.nextivaapp.android.models.NextivaAnywhereLocation
import com.nextiva.nextivaapp.android.models.ServiceSettings
import com.nextiva.nextivaapp.android.models.SimultaneousRingLocation
import com.nextiva.nextivaapp.android.sip.pjsip.PJSipManager
import com.nextiva.nextivaapp.android.util.ApplicationUtil.isNightModeEnabled
import com.nextiva.nextivaapp.android.util.MenuUtil
import com.nextiva.nextivaapp.android.util.ViewUtil
import com.nextiva.nextivaapp.android.util.extensions.serializable
import com.nextiva.pjsip.pjsip_lib.sipservice.SipCall
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class SetCallSettingsActivity : BaseActivity(), ToolbarListener, SetCallSettingsListener {
    private lateinit var toolbar: Toolbar
    private lateinit var toolbarTitleTextView: TextView
    private lateinit var activeCallBar: ComposeView

    private var materialMenuDrawable: MaterialMenuDrawable? = null
    private var saveMenuItem: MenuItem? = null

    @Inject
    lateinit var dbManagerKt: DbManagerKt
    @Inject
    lateinit var analyticsManager: AnalyticsManager
    @Inject
    lateinit var sipManager: PJSipManager

    @FormType
    private var formType: String? = null
    private var serviceSettings: ServiceSettings? = null
    private var nextivaAnywhereLocation: NextivaAnywhereLocation? = null
    private var simultaneousRingLocation: SimultaneousRingLocation? = null

    private val activeCallObserver = Observer<SipCall?> { sipCall ->
        if (sipCall != null) {
            MainScope().launch(Dispatchers.IO) {
                val contact = sipCall.participantInfoList.firstOrNull()?.numberToCall?.let { dbManagerKt.getContactFromPhoneNumberInThread(it).value }

                withContext(Dispatchers.Main) {
                    activeCallBar.setContent {
                        ActiveCallBanner(sipCall, contact, !isNightModeEnabled(applicationContext, mSettingsManager), sipManager.activeCallDurationLiveData) {
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(bindViews())

        intent.extras?.let { extras ->
            if (extras.containsKey(PARAMS_SERVICE_SETTINGS)) {
                serviceSettings = extras.serializable(PARAMS_SERVICE_SETTINGS)
            }

            if (extras.containsKey(PARAMS_FORM_TYPE)) {
                formType = extras.getString(PARAMS_FORM_TYPE)
            }

            if (extras.containsKey(PARAMS_NEXTIVA_ANYWHERE_LOCATION)) {
                nextivaAnywhereLocation = extras.serializable(PARAMS_NEXTIVA_ANYWHERE_LOCATION)
            }

            if (extras.containsKey(PARAMS_SIMULTANEOUS_RING_LOCATION)) {
                simultaneousRingLocation = extras.serializable(PARAMS_SIMULTANEOUS_RING_LOCATION)
            }
        }

        setupToolbar()
        setupFragment()

        sipManager.activeCallLiveData.observe(this, activeCallObserver)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_set_call_setting, menu)

        MenuUtil.tintAllIcons(menu, ContextCompat.getColor(this, R.color.white))

        saveMenuItem = menu.findItem(R.id.set_call_settings_save)
        saveMenuItem?.setEnabled(enableSaveButton())

        val deleteMenuItem = menu.findItem(R.id.set_call_settings_delete)
        deleteMenuItem.setVisible(enableDeleteButton())

        val helpMenuItem = menu.findItem(R.id.set_call_settings_help)
        helpMenuItem.setVisible(enableHelpButton())

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        ViewUtil.hideKeyboard(toolbar)

        when (item.itemId) {
            R.id.set_call_settings_save -> {
                if (changesMade()) {
                    val formFragment = supportFragmentManager.findFragmentByTag(FragmentTags.SET_SERVICE_SETTINGS)

                    if (formFragment is CallSettingsForm<*>) {
                        (formFragment as CallSettingsForm<*>).saveForm()

                    } else {
                        mDialogManager.showErrorDialog(this@SetCallSettingsActivity, ScreenName.UNKNOWN_CALL_SETTINGS)
                    }
                } else {
                    finish()
                }

                return true
            }
            R.id.set_call_settings_delete -> {
                val formFragment = supportFragmentManager.findFragmentByTag(FragmentTags.SET_SERVICE_SETTINGS)

                if (formFragment is CallSettingsForm<*>) {
                    (formFragment as CallSettingsForm<*>).deleteForm()

                } else {
                    mDialogManager.showErrorDialog(this@SetCallSettingsActivity, ScreenName.UNKNOWN_CALL_SETTINGS)
                }

                return true
            }
            R.id.set_call_settings_help -> {
                val formFragment = supportFragmentManager.findFragmentByTag(FragmentTags.SET_SERVICE_SETTINGS)

                if (formFragment is CallSettingsForm<*>) {
                    analyticsManager.logEvent((formFragment as CallSettingsForm<*>).analyticScreenName, Enums.Analytics.EventName.HELP_BUTTON_PRESSED)
                    mDialogManager.showDialog(
                        this@SetCallSettingsActivity,
                        (formFragment as CallSettingsForm<*>).formTitleResId,
                        (formFragment as CallSettingsForm<*>).helpTextResId,
                        R.string.general_ok
                    ) { _: MaterialDialog?, _: DialogAction? ->
                        analyticsManager.logEvent((formFragment as CallSettingsForm<*>).analyticScreenName, Enums.Analytics.EventName.CALL_SETTINGS_HELP_DIALOG_OK_BUTTON_PRESSED)
                    }

                    analyticsManager.logEvent((formFragment as CallSettingsForm<*>).analyticScreenName, Enums.Analytics.EventName.CALL_SETTINGS_HELP_DIALOG_SHOWN)

                } else {
                    mDialogManager.showErrorDialog(this@SetCallSettingsActivity, ScreenName.UNKNOWN_CALL_SETTINGS)
                }

                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        cancelScreen()
    }

    private fun bindViews(): View {
        val binding = ActivitySetCallSettingsBinding.inflate(layoutInflater)

        toolbar = binding.setCallSettingsToolbar
        toolbarTitleTextView = binding.setCallSettingsToolbarTitleTextView
        activeCallBar = binding.activeCallToolbar

        return binding.root
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)

        materialMenuDrawable = MaterialMenuDrawable(
            this@SetCallSettingsActivity,
            ContextCompat.getColor(this@SetCallSettingsActivity, R.color.white),
            MaterialMenuDrawable.Stroke.REGULAR
        )
        materialMenuDrawable?.iconState = MaterialMenuDrawable.IconState.ARROW
        toolbar.navigationIcon = materialMenuDrawable
        toolbar.setNavigationContentDescription(R.string.back_button_accessibility_id)
        toolbar.setNavigationOnClickListener {
            if (materialMenuDrawable?.isRunning == false) {
                if (materialMenuDrawable?.iconState == MaterialMenuDrawable.IconState.ARROW) {
                    onBackPressed()
                    val fragment = supportFragmentManager.findFragmentById(R.id.set_call_settings_fragment_container_layout)

                    if (fragment is BackFragmentListener) {
                        (fragment as BackFragmentListener).onBackPressed()
                    }
                }
            }
        }
    }

    private fun setupFragment() {
        val fragment = callSettingFragment

        if (fragment != null) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(
                R.id.set_call_settings_fragment_container_layout,
                fragment,
                FragmentTags.SET_SERVICE_SETTINGS
            )
            transaction.commit()
        }
    }

    private fun changesMade(): Boolean {
        val formFragment = supportFragmentManager.findFragmentByTag(FragmentTags.SET_SERVICE_SETTINGS)
        return formFragment is EntryForm && (formFragment as EntryForm).changesMade()
    }

    private fun enableSaveButton(): Boolean {
        val formFragment = supportFragmentManager.findFragmentByTag(FragmentTags.SET_SERVICE_SETTINGS)
        return formFragment is EntryForm && (formFragment as EntryForm).enableSaveButton()
    }

    private fun enableDeleteButton(): Boolean {
        val formFragment = supportFragmentManager.findFragmentByTag(FragmentTags.SET_SERVICE_SETTINGS)
        return formFragment is EntryForm && (formFragment as EntryForm).enableDeleteButton()
    }

    private fun enableHelpButton(): Boolean {
        val formFragment = supportFragmentManager.findFragmentByTag(FragmentTags.SET_SERVICE_SETTINGS)
        return formFragment is CallSettingsForm<*> && (formFragment as CallSettingsForm<*>).helpTextResId != 0
    }

    private fun cancelScreen() {
        ViewUtil.hideKeyboard(toolbar)

        val formFragment = supportFragmentManager.findFragmentByTag(FragmentTags.SET_SERVICE_SETTINGS)

        if (changesMade() && formFragment is CallSettingsForm<*>) {
            analyticsManager.logEvent((formFragment as CallSettingsForm<*>).analyticScreenName, Enums.Analytics.EventName.UNSAVED_CHANGES_DIALOG_SHOWN)

            mDialogManager.showDialog(
                this@SetCallSettingsActivity,
                0,
                R.string.error_unsaved_changes_title,
                R.string.general_discard,
                { _: MaterialDialog?, _: DialogAction? ->
                    super@SetCallSettingsActivity.onBackPressed()
                    analyticsManager.logEvent(
                        (formFragment as CallSettingsForm<*>).analyticScreenName,
                        Enums.Analytics.EventName.UNSAVED_CHANGES_DIALOG_DISCARD_BUTTON_PRESSED
                    )
                },
                R.string.general_cancel,
                { _: MaterialDialog?, _: DialogAction? ->
                    analyticsManager.logEvent(
                        (formFragment as CallSettingsForm<*>).analyticScreenName,
                        Enums.Analytics.EventName.UNSAVED_CHANGES_DIALOG_CANCEL_BUTTON_PRESSED
                    )
                })
        } else {
            super.onBackPressed()
        }
    }

    private val callSettingFragment: Fragment?
        get() {
            if (!TextUtils.isEmpty(formType)) {
                when (formType) {
                    Enums.Service.TYPE_DO_NOT_DISTURB ->
                        return serviceSettings?.let { SetCallSettingsDoNotDisturbFragment.newInstance(it) }
                    Enums.Service.TYPE_CALL_FORWARDING_NOT_REACHABLE ->
                        return serviceSettings?.let { SetCallSettingsCallForwardWhenUnreachableFragment.newInstance(it) }
                    Enums.Service.TYPE_CALL_FORWARDING_ALWAYS ->
                        return serviceSettings?.let { SetCallSettingsCallForwardAlwaysFragment.newInstance(it) }
                    Enums.Service.TYPE_CALL_FORWARDING_NO_ANSWER ->
                        return serviceSettings?.let { SetCallSettingsCallForwardWhenUnansweredFragment.newInstance(it) }
                    Enums.Service.TYPE_CALL_FORWARDING_BUSY ->
                        return serviceSettings?.let { SetCallSettingsCallForwardWhenBusyFragment.newInstance(it) }
                    Enums.Service.TYPE_REMOTE_OFFICE ->
                        return serviceSettings?.let { SetCallSettingsRemoteOfficeFragment.newInstance(it) }
                    Enums.Service.TYPE_CALLING_LINE_ID_DELIVERY_BLOCKING ->
                        return serviceSettings?.let { SetCallSettingsBlockCallerIdFragment.newInstance(it) }
                    Enums.Service.TYPE_BROADWORKS_ANYWHERE ->
                        return serviceSettings?.let { SetCallSettingsNextivaAnywhereFragment.newInstance(it) }
                    Enums.Service.TYPE_SIMULTANEOUS_RING_PERSONAL ->
                        return serviceSettings?.let { SetCallSettingsSimultaneousRingFragment.newInstance(it) }
                    SharedPreferencesManager.THIS_PHONE_NUMBER ->
                        return SetCallSettingsThisPhoneNumberFragment.newInstance(mSettingsManager.phoneNumber)
                    SharedPreferencesManager.DIALING_SERVICE ->
                        return SetCallSettingsDialingServiceFragment.newInstance(mSettingsManager.dialingService)
                    Enums.CallSettings.FORM_TYPE_NEXTIVA_ANYWHERE_LOCATION ->
                        return serviceSettings?.let { SetCallSettingsNextivaAnywhereLocationFragment.newInstance(it, nextivaAnywhereLocation) }
                    Enums.CallSettings.FORM_TYPE_SIMULTANEOUS_RING_LOCATION ->
                        return SetCallSettingsSimultaneousRingLocationFragment.newInstance(serviceSettings, simultaneousRingLocation)
                    SharedPreferencesManager.CALL_CENTER_STATUS ->
                        return SetCallSettingsCallCenterStatusFragment.newInstance(Enums.Service.CallCenterServiceStatuses.AVAILABLE)
                    SharedPreferencesManager.ALLOW_TERMINATION ->
                        return newInstance()
                }
            }

            return null
        }

    // --------------------------------------------------------------------------------------------
    // ToolbarListener Methods
    // --------------------------------------------------------------------------------------------
    override fun setToolbarTitle(title: String) {
        toolbarTitleTextView.text = title
    }

    override fun setToolbarTitle(titleResId: Int) {
        toolbarTitleTextView.setText(titleResId)
    }

    override fun setToolbarElevation(elevation: Float) {
    }

    // --------------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------------
    // SetCallSettingsListener Methods
    // --------------------------------------------------------------------------------------------
    override fun onFormUpdated() {
        saveMenuItem?.let { saveMenuItem ->
            if (saveMenuItem.isEnabled != enableSaveButton()) {
                saveMenuItem.setEnabled(enableSaveButton())
            }
        }
    }

    override fun onCallSettingsRetrieved(data: Intent?) {
        setResult(RESULT_OK, data)
    }

    override fun onCallSettingsSaved(data: Intent?) {
        setResult(RESULT_OK, data)
        finish()
    }

    override fun onCallSettingsDeleted(data: Intent?) {
        setResult(RESULT_OK, data)
        finish()
    } // --------------------------------------------------------------------------------------------

    companion object {
        private const val PARAMS_FORM_TYPE = "PARAMS_FORM_TYPE"
        private const val PARAMS_SERVICE_SETTINGS = "PARAMS_SERVICE_SETTINGS"
        private const val PARAMS_NEXTIVA_ANYWHERE_LOCATION = "PARAMS_NEXTIVA_ANYWHERE_LOCATION"
        private const val PARAMS_SIMULTANEOUS_RING_LOCATION = "PARAMS_SIMULTANEOUS_RING_LOCATION"

        @JvmStatic
        fun newIntent(context: Context, serviceSettings: ServiceSettings): Intent {
            val intent = Intent(context, SetCallSettingsActivity::class.java)
            intent.putExtra(PARAMS_FORM_TYPE, serviceSettings.type)
            intent.putExtra(PARAMS_SERVICE_SETTINGS, serviceSettings)
            return intent
        }

        @JvmStatic
        fun newIntent(context: Context, @FormType formType: String?): Intent {
            val intent = Intent(context, SetCallSettingsActivity::class.java)
            intent.putExtra(PARAMS_FORM_TYPE, formType)
            return intent
        }

        fun newIntent(
            context: Context,
            serviceSettings: ServiceSettings?,
            nextivaAnywhereLocation: NextivaAnywhereLocation?
        ): Intent {
            val intent = Intent(context, SetCallSettingsActivity::class.java)
            intent.putExtra(PARAMS_FORM_TYPE, Enums.CallSettings.FORM_TYPE_NEXTIVA_ANYWHERE_LOCATION)
            intent.putExtra(PARAMS_SERVICE_SETTINGS, serviceSettings)
            intent.putExtra(PARAMS_NEXTIVA_ANYWHERE_LOCATION, nextivaAnywhereLocation)
            return intent
        }

        fun newIntent(
            context: Context,
            serviceSettings: ServiceSettings?,
            simultaneousRingLocation: SimultaneousRingLocation?
        ): Intent {
            val intent = Intent(context, SetCallSettingsActivity::class.java)
            intent.putExtra(PARAMS_FORM_TYPE, Enums.CallSettings.FORM_TYPE_SIMULTANEOUS_RING_LOCATION)
            intent.putExtra(PARAMS_SERVICE_SETTINGS, serviceSettings)
            intent.putExtra(PARAMS_SIMULTANEOUS_RING_LOCATION, simultaneousRingLocation)
            return intent
        }
    }
}
