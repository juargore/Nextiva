/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModelProviders
import com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.ACCEPT_BUTTON_PRESSED
import com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.ACCEPT_LICENSE_AGREEMENT_DIALOG_ACCEPT_BUTTON_PRESSED
import com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.ACCEPT_LICENSE_AGREEMENT_DIALOG_CANCEL_BUTTON_PRESSED
import com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.ACCEPT_LICENSE_AGREEMENT_DIALOG_SHOWN
import com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.DECLINE_BUTTON_PRESSED
import com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.DECLINE_LICENSE_AGREEMENT_DIALOG_DECLINE_BUTTON_PRESSED
import com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.DECLINE_LICENSE_AGREEMENT_DIALOG_REVIEW_BUTTON_PRESSED
import com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.DECLINE_LICENSE_AGREEMENT_DIALOG_SHOWN
import com.nextiva.nextivaapp.android.constants.Enums.Analytics.ScreenName.LICENSE_AGREEMENT
import com.nextiva.nextivaapp.android.databinding.FragmentLicenseAcceptanceBinding
import com.nextiva.nextivaapp.android.managers.interfaces.AnalyticsManager
import com.nextiva.nextivaapp.android.managers.interfaces.DialogManager
import com.nextiva.nextivaapp.android.managers.interfaces.IntentManager
import com.nextiva.nextivaapp.android.viewmodels.LicenseAcceptanceViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
open class LicenseAcceptanceFragment : BaseFragment() {

    @Inject
    lateinit var dialogManager: DialogManager

    @Inject
    lateinit var intentManager: IntentManager

    @Inject
    lateinit var analyticsManager: AnalyticsManager

    private lateinit var viewModel: LicenseAcceptanceViewModel

    private lateinit var fragmentListener: LicenseAcceptanceFragmentListener

    private val acceptAgreementPositiveButtonCallback = SingleButtonCallback { _, _ ->
        viewModel.setLicenseApproved()
        startActivity(intentManager.getInitialIntent(requireActivity()))

        analyticsManager.logEvent(LICENSE_AGREEMENT, ACCEPT_LICENSE_AGREEMENT_DIALOG_ACCEPT_BUTTON_PRESSED)
    }
    private val acceptAgreementNegativeButtonCallback = SingleButtonCallback { _, _ ->
        analyticsManager.logEvent(LICENSE_AGREEMENT, ACCEPT_LICENSE_AGREEMENT_DIALOG_CANCEL_BUTTON_PRESSED)
    }
    private val declineAgreementPositiveButtonCallback = SingleButtonCallback { _, _ ->
        analyticsManager.logEvent(LICENSE_AGREEMENT, DECLINE_LICENSE_AGREEMENT_DIALOG_REVIEW_BUTTON_PRESSED)
    }
    private val declineAgreementNegativeButtonCallback = SingleButtonCallback { _, _ ->
        fragmentListener.onAgreementDeclined()

        analyticsManager.logEvent(LICENSE_AGREEMENT, DECLINE_LICENSE_AGREEMENT_DIALOG_DECLINE_BUTTON_PRESSED)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (activity == null) {
            return
        }

        viewModel = ViewModelProviders.of(this).get(LicenseAcceptanceViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return bindViews(inflater, container)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            fragmentListener = context as LicenseAcceptanceFragmentListener
        } catch (exception: ClassCastException) {
            throw UnsupportedOperationException(context::class.java.simpleName + " must implement LicenseAcceptanceFragmentListener.")
        }
    }

    fun bindViews(inflater: LayoutInflater, container: ViewGroup?): View {
        val binding = FragmentLicenseAcceptanceBinding.inflate(inflater, container, false)

        binding.licenseAcceptanceAcceptButton.setOnClickListener { onLicenseAcceptanceAcceptButtonClick() }
        binding.licenseAcceptanceDeclineButton.setOnClickListener { onLicenseAcceptanceDeclineButtonClick() }

        return binding.root
    }

    open fun onLicenseAcceptanceAcceptButtonClick() {
        dialogManager.showDialog(
                requireActivity(),
                R.string.license_agreement_title,
                R.string.license_agreement_accept_dialog_text,
                R.string.license_agreement_accept_dialog_positive_button_text,
                acceptAgreementPositiveButtonCallback,
                R.string.license_agreement_accept_dialog_negative_button_text,
                acceptAgreementNegativeButtonCallback)

        analyticsManager.logEvent(LICENSE_AGREEMENT, ACCEPT_BUTTON_PRESSED)
        analyticsManager.logEvent(LICENSE_AGREEMENT, ACCEPT_LICENSE_AGREEMENT_DIALOG_SHOWN)
    }

    open fun onLicenseAcceptanceDeclineButtonClick() {
        dialogManager.showDialog(
                requireActivity(),
                getString(R.string.license_agreement_title),
                getString(R.string.license_agreement_decline_dialog_text, getString(R.string.app_name)),
                getString(R.string.license_agreement_decline_dialog_positive_button_text),
                declineAgreementPositiveButtonCallback,
                getString(R.string.license_agreement_decline_dialog_negative_button_text),
                declineAgreementNegativeButtonCallback)

        analyticsManager.logEvent(LICENSE_AGREEMENT, DECLINE_BUTTON_PRESSED)
        analyticsManager.logEvent(LICENSE_AGREEMENT, DECLINE_LICENSE_AGREEMENT_DIALOG_SHOWN)
    }

    @VisibleForTesting
    fun setViewModelForTest(licenseAcceptanceFragmentViewModel: LicenseAcceptanceViewModel) {
        viewModel = licenseAcceptanceFragmentViewModel
    }

    @VisibleForTesting
    fun setFragmentListenerForTest(licenseAcceptanceFragmentListener: LicenseAcceptanceFragmentListener) {
        fragmentListener = licenseAcceptanceFragmentListener
    }

    companion object {

        fun newInstance(): LicenseAcceptanceFragment {
            val args = Bundle()

            val fragment = LicenseAcceptanceFragment()
            fragment.arguments = args
            return fragment
        }
    }

    interface LicenseAcceptanceFragmentListener {
        fun onAgreementDeclined()
    }
}
