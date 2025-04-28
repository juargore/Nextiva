/*
 * Copyright (c) 2023. Nextiva, inc. to Present.
 * All rights reserved
 */

package com.nextiva.nextivaapp.android

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.balysv.materialmenu.MaterialMenuDrawable
import com.nextiva.nextivaapp.android.databinding.ActivityMessageCountApiBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MessageCountAPIActivity : BaseActivity() {

    private lateinit var toolbar: Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(bindViews())

        val materialMenuDrawable = MaterialMenuDrawable(this,
            ContextCompat.getColor(this, R.color.white),
            MaterialMenuDrawable.Stroke.REGULAR)
        materialMenuDrawable.iconState = MaterialMenuDrawable.IconState.ARROW
        toolbar.navigationIcon = materialMenuDrawable

        setSupportActionBar(toolbar)
        setTitle(R.string.app_preferences_message_count_api)

        toolbar.setNavigationContentDescription(R.string.back_button_accessibility_id)
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun bindViews(): View {
        val binding = ActivityMessageCountApiBinding.inflate(layoutInflater)

        toolbar = binding.messageCountApiToolbar

        overrideEdgeToEdge(binding.root)

        return binding.root
    }
}