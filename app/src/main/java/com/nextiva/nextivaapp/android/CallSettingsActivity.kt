/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */
package com.nextiva.nextivaapp.android

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.widget.Toolbar
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Observer
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.constants.Enums.Analytics.ScreenName
import com.nextiva.nextivaapp.android.core.common.ui.ActiveCallBanner
import com.nextiva.nextivaapp.android.databinding.ActivityCallSettingsBinding
import com.nextiva.nextivaapp.android.db.DbManagerKt
import com.nextiva.nextivaapp.android.managers.interfaces.AnalyticsManager
import com.nextiva.nextivaapp.android.sip.pjsip.PJSipManager
import com.nextiva.nextivaapp.android.util.ApplicationUtil.isNightModeEnabled
import com.nextiva.pjsip.pjsip_lib.sipservice.SipCall
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class CallSettingsActivity : BaseActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var backArrowView: RelativeLayout
    private lateinit var activeCallBar: ComposeView

    @Inject
    lateinit var analyticsManager: AnalyticsManager
    @Inject
    lateinit var sipManager: PJSipManager
    @Inject
    lateinit var dbManagerKt: DbManagerKt

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

        setSupportActionBar(toolbar)
        backArrowView.setOnClickListener {
            analyticsManager.logEvent(ScreenName.CALL_SETTINGS_LIST, Enums.Analytics.EventName.BACK_BUTTON_PRESSED)
            onBackPressed()
        }

        sipManager.activeCallLiveData.observe(this, activeCallObserver)
    }

    private fun bindViews(): View {
        val binding = ActivityCallSettingsBinding.inflate(layoutInflater)

        toolbar = binding.callSettingsToolbar
        backArrowView = binding.backArrowInclude.backArrowView
        activeCallBar = binding.activeCallToolbar

        return binding.root
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, CallSettingsActivity::class.java)
        }
    }
}
