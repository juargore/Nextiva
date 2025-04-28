package com.nextiva.nextivaapp.android

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.balysv.materialmenu.MaterialMenuDrawable
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.databinding.ActivityHealthCheckBinding
import com.nextiva.nextivaapp.android.viewmodels.HealthCheckViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HealthCheckActivity: BaseActivity() {


    private lateinit var toolbar: Toolbar
    private lateinit var runButton: Button

    private lateinit var viewModel: HealthCheckViewModel

    private val checkFinishedObserver = Observer<Void?> {
        mDialogManager.dismissProgressDialog()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(bindViews())

        viewModel = ViewModelProvider(this)[HealthCheckViewModel::class.java]

        viewModel.checkFinished.observe(this, checkFinishedObserver)

        val materialMenuDrawable = MaterialMenuDrawable(this,
                ContextCompat.getColor(this, R.color.white),
                MaterialMenuDrawable.Stroke.REGULAR)
        materialMenuDrawable.iconState = MaterialMenuDrawable.IconState.ARROW
        toolbar.navigationIcon = materialMenuDrawable

        setSupportActionBar(toolbar)
        setTitle(R.string.health_check_title)

        toolbar.setNavigationContentDescription(R.string.back_button_accessibility_id)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        runButton.setOnClickListener {
            mDialogManager.showProgressDialog(this, Enums.Analytics.ScreenName.HEALTH_CHECK, R.string.progress_processing)
            viewModel.runCheck()
        }
    }

    private fun bindViews(): View {
        val binding = ActivityHealthCheckBinding.inflate(layoutInflater)

        toolbar = binding.healthCheckToolbar
        runButton = binding.healthCheckRunCheckButton

        overrideEdgeToEdge(binding.root)

        return binding.root
    }
}