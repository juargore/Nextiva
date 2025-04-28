/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */
package com.nextiva.nextivaapp.android

import android.content.Intent
import android.os.Bundle
import android.view.Window
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by adammacdonald on 2/1/18.
 */
@AndroidEntryPoint
class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
        window.enterTransition = null
        window.exitTransition = null
        super.onCreate(savedInstanceState)
        if ((!isTaskRoot
                    && intent.hasCategory(Intent.CATEGORY_LAUNCHER)) && intent.action != null && intent.action == Intent.ACTION_MAIN
        ) {
            finish()
            return
        }
        startInitialActivity()
    }

    private fun startInitialActivity() {
        overridePendingTransition(0, 0)
        startActivity(
            mIntentManager.getInitialIntent(this@SplashActivity)
                .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        )
        overridePendingTransition(0, 0)
        finish()
    }
}