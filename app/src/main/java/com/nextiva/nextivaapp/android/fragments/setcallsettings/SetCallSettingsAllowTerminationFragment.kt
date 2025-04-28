package com.nextiva.nextivaapp.android.fragments.setcallsettings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.constants.Constants
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.databinding.FragmentSetCallSettingsAllowTerminationBinding
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.UserRepository
import com.nextiva.nextivaapp.android.managers.interfaces.AnalyticsManager
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.models.ServiceSettings
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.observers.DisposableSingleObserver
import javax.inject.Inject

@AndroidEntryPoint
class SetCallSettingsAllowTerminationFragment: SetCallSettingsBaseFragment<Boolean>() {

    @Inject
    lateinit var userRepository: UserRepository
    @Inject
    lateinit var sessionManager: SessionManager
    @Inject
    lateinit var analyticsManager: AnalyticsManager

    private lateinit var enabledCheckbox: CheckBox

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = bindViews(inflater, container)
        super.onCreateView(inflater, container, savedInstanceState)
        return view
    }

    private fun bindViews(inflater: LayoutInflater, container: ViewGroup?): View {
        val binding: FragmentSetCallSettingsAllowTerminationBinding = FragmentSetCallSettingsAllowTerminationBinding.inflate(inflater, container, false)
        enabledCheckbox = binding.setCallSettingsAllowTerminationEnabledCheckBox
        return binding.getRoot()
    }

    // --------------------------------------------------------------------------------------------
    override fun changesMade(): Boolean {
        return enabledCheckbox.isChecked != sessionManager.allowTermination
    }

    override fun getFormCallSettings(): Boolean {
        return enabledCheckbox.isChecked
    }

    override fun saveForm() {
        if (activity == null) {
            return
        }

        analyticsManager.logEvent(analyticScreenName, Enums.Analytics.EventName.SAVE_BUTTON_PRESSED)

        validateForm { type: String?, allowTermination: Boolean? ->
            userRepository.setAllowTermination(enabledCheckbox.isChecked)
                .subscribe(object : DisposableSingleObserver<Boolean>() {
                    override fun onSuccess(wasSuccessful: Boolean) {
                        if (wasSuccessful) {
                            val data = Intent()
                            data.putExtra(Constants.EXTRA_CALL_SETTINGS_KEY, type)
                            data.putExtra(Constants.EXTRA_CALL_SETTINGS_VALUE, allowTermination)

                            if (mSetCallSettingsListener != null) {
                                mSetCallSettingsListener.onCallSettingsSaved(data)
                            }

                        } else {
                            mDialogManager.showErrorDialog(requireActivity(), analyticScreenName)
                        }
                    }

                    override fun onError(e: Throwable) {
                        mDialogManager.showErrorDialog(requireActivity(), analyticScreenName)
                    }
                })
        }
    }

    override fun getAnalyticScreenName(): String {
        return Enums.Analytics.ScreenName.ALLOW_TERMINATION_SCREEN
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_set_call_settings_allow_termination
    }

    override fun getToolbarTitleStringResId(): Int {
        return R.string.set_call_settings_allow_termination_toolbar_text
    }

    public override fun setupCallSettings() {
            enabledCheckbox.setOnCheckedChangeListener(null)
            enabledCheckbox.setChecked(sessionManager.allowTermination)
            enabledCheckbox.setOnCheckedChangeListener(mEnabledOnCheckedChangeListener)

            if (mSetCallSettingsListener != null) {
                mSetCallSettingsListener.onFormUpdated()
            }
    }

    companion object {
        fun newInstance(): SetCallSettingsAllowTerminationFragment {
            return SetCallSettingsAllowTerminationFragment()
        }
    }
}