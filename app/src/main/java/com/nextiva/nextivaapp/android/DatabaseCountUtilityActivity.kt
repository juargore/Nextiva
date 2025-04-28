package com.nextiva.nextivaapp.android

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.balysv.materialmenu.MaterialMenuDrawable
import com.nextiva.nextivaapp.android.databinding.ActivityDatabaseCountUtilityBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DatabaseCountUtilityActivity : BaseActivity() {

    private var toolbar: Toolbar? = null
    lateinit var materialMenuDrawable: MaterialMenuDrawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(bindViews())

        setSupportActionBar(toolbar)
        setTitle(R.string.database_count_utility_toolbar)

        materialMenuDrawable = MaterialMenuDrawable(this,
                ContextCompat.getColor(this, R.color.white),
                MaterialMenuDrawable.Stroke.REGULAR)

        materialMenuDrawable.iconState = MaterialMenuDrawable.IconState.ARROW
        toolbar?.navigationIcon = materialMenuDrawable
        toolbar?.setNavigationContentDescription(R.string.back_button_accessibility_id)
        toolbar?.setNavigationOnClickListener {
            if (!materialMenuDrawable.isRunning) {
                if (materialMenuDrawable.iconState == MaterialMenuDrawable.IconState.ARROW) {
                    onBackPressed()
                }
            }
        }
    }

    private fun bindViews(): View {
        val binding = ActivityDatabaseCountUtilityBinding.inflate(layoutInflater)
        toolbar = binding.databaseCountUtilityToolbar
        overrideEdgeToEdge(binding.root)
        return binding.root
    }

    fun newIntent(context: Context): Intent {
        return Intent(context, DatabaseCountUtilityActivity::class.java)
    }
}