package com.nextiva.nextivaapp.android

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.balysv.materialmenu.MaterialMenuDrawable
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.databinding.ActivityLicenseCheckBinding
import com.nextiva.nextivaapp.android.viewmodels.LicenseCheckViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LicenseCheckActivity : BaseActivity() {


    lateinit var toolbar: Toolbar
    private lateinit var teamSmsLicensePass: TextView
    private lateinit var smsLicensePass: TextView
    private lateinit var voicemailLicensePass: TextView
    private lateinit var nextivaOneLicensePass: TextView
    private lateinit var videoGuestsLicensePass: TextView
    private lateinit var videoLicensePass: TextView
    private lateinit var phoneNumber: TextView

    private lateinit var viewModel: LicenseCheckViewModel

    private val teamSmsEnabledResultObserver = Observer<Boolean> {
        setResultText(teamSmsLicensePass, it)
        dismissProgressDialogIfAllChecksFinished()
    }

    private val smsLicenseResultObserver = Observer<Boolean> {
        setResultText(smsLicensePass,
                it)
        dismissProgressDialogIfAllChecksFinished()
    }

    private val voicemailTranscriptionLicenseResultObserver = Observer<Boolean> {
        setResultText(voicemailLicensePass,
                it)
        dismissProgressDialogIfAllChecksFinished()
    }

    private val connectLicenseResultObserver = Observer<Boolean> {
        setResultText(nextivaOneLicensePass,
                it)
        dismissProgressDialogIfAllChecksFinished()
    }

    private val videoGuestParticipantLicenseResultObserver = Observer<Boolean> {
        setResultText(videoGuestsLicensePass,
                it)
        dismissProgressDialogIfAllChecksFinished()
    }

    private val videoLicenseResultObserver = Observer<Boolean> {
        setResultText(videoLicensePass, it)
        dismissProgressDialogIfAllChecksFinished()
    }

    private val phoneNumberLicenseResultObserver = Observer<String?> {
        if (!it.isNullOrEmpty()) phoneNumber.text = it else phoneNumber.text = "None"
    }

    private val smsEnabledLicenseResultObserver = Observer<Boolean> {
        setResultText(smsLicensePass, it)
        if (it)
            dismissProgressDialogIfAllChecksFinished()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(bindViews())


        viewModel = ViewModelProvider(this)[LicenseCheckViewModel::class.java]

        viewModel.getSmsLicenseResultLiveData().observe(this, smsLicenseResultObserver)
        viewModel.getVoicemailTranscriptionLicenseResultLiveData().observe(this, voicemailTranscriptionLicenseResultObserver)
        viewModel.getConnectLicenseResultLiveData().observe(this, connectLicenseResultObserver)
        viewModel.getVideoGuestLicenseResultLiveData().observe(this, videoGuestParticipantLicenseResultObserver)
        viewModel.getVideoLicenseResultLiveData().observe(this, videoLicenseResultObserver)
        viewModel.getPhoneNumberResultLiveData().observe(this, phoneNumberLicenseResultObserver)
        viewModel.getSmsEnabledResultLiveData().observe(this, smsEnabledLicenseResultObserver)
        viewModel.getTeamSmsLicenseResultLiveData().observe(this, teamSmsEnabledResultObserver)

        val materialMenuDrawable = MaterialMenuDrawable(this,
                ContextCompat.getColor(this, R.color.white),
                MaterialMenuDrawable.Stroke.REGULAR)
        materialMenuDrawable.iconState = MaterialMenuDrawable.IconState.ARROW
        toolbar.navigationIcon = materialMenuDrawable

        setSupportActionBar(toolbar)
        setTitle(R.string.license_check_title)

        toolbar.setNavigationContentDescription(R.string.back_button_accessibility_id)
        toolbar.setNavigationOnClickListener {
            finish()
        }
        getLicenseCheckResults()
    }

    fun bindViews(): View {
        val binding = ActivityLicenseCheckBinding.inflate(layoutInflater)
        toolbar = binding.licenseCheckToolbar
        teamSmsLicensePass = binding.healthCheckTeamSmsLicensePass
        smsLicensePass = binding.healthCheckSmsLicensePass
        voicemailLicensePass = binding.healthCheckVoicemailLicensePass
        nextivaOneLicensePass = binding.licenseCheckNextivaOneLicensePass
        videoGuestsLicensePass = binding.licenseCheckVideoGuestsLicensePass
        videoLicensePass = binding.licenseCheckVideoPass
        phoneNumber = binding.licenseCheckPhoneNumber

        overrideEdgeToEdge(binding.root)

        return binding.root
    }

    private fun getLicenseCheckResults() {
        smsLicensePass.visibility = View.GONE
        voicemailLicensePass.visibility = View.GONE
        nextivaOneLicensePass.visibility = View.GONE
        videoGuestsLicensePass.visibility = View.GONE
        videoLicensePass.visibility = View.GONE
        smsLicensePass.visibility = View.GONE

        mDialogManager.showProgressDialog(this, Enums.Analytics.ScreenName.HEALTH_CHECK, R.string.progress_processing)
        viewModel.runCheck()
    }

    private fun dismissProgressDialogIfAllChecksFinished() {
        mDialogManager.dismissProgressDialog()
    }

    private fun setResultText(resultTextView: TextView, result: Boolean) {
        resultTextView.text = if (result) getString(R.string.health_check_pass) else getString(R.string.health_check_fail)
        resultTextView.setTextColor(ContextCompat.getColor(this, if (result) R.color.nextivaGreen else R.color.nextivaRed))
        resultTextView.visibility = View.VISIBLE
    }
}