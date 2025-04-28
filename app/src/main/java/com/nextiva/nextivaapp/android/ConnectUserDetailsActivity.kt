package com.nextiva.nextivaapp.android

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.nextiva.nextivaapp.android.databinding.ActivityConnectUserDetailsBinding
import com.nextiva.nextivaapp.android.fragments.ConnectUserDetailsListFragment
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.models.NextivaContact
import com.nextiva.nextivaapp.android.util.ViewUtil
import com.nextiva.nextivaapp.android.view.ConnectContactHeaderView
import com.nextiva.nextivaapp.android.viewmodels.ConnectUserDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.abs

@AndroidEntryPoint
class ConnectUserDetailsActivity : BaseActivity(), AppBarLayout.OnOffsetChangedListener {

    private val percentageToShowTitleAtToolbar = 0.8f

    private lateinit var connectUserDetailsViewModel: ConnectUserDetailsViewModel

    @Inject
    lateinit var sessionManager: SessionManager

    private lateinit var contactDetailHeaderView: ConnectContactHeaderView
    private lateinit var collapsedTitleTextView: TextView
    private lateinit var toolbarTitleTextView: TextView
    private lateinit var titleTextView: TextView
    private lateinit var appBarLayout: AppBarLayout
    private lateinit var backArrowView: RelativeLayout
    private lateinit var toolbar: Toolbar
    private lateinit var collapsingToolbarLayout: CollapsingToolbarLayout

    private var isTheTitleVisible = false

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, ConnectUserDetailsActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectUserDetailsViewModel =
            ViewModelProvider(this)[ConnectUserDetailsViewModel::class.java]

        setContentView(bindViews())
        setHeaderViews(connectUserDetailsViewModel.getUserContact())

        ViewUtil.startAlphaAnimation(collapsedTitleTextView, 0, View.GONE)
        appBarLayout.addOnOffsetChangedListener(this)
        backArrowView.setOnClickListener { onBackPressed() }

        // setup toolbar
        collapsingToolbarLayout.setExpandedTitleColor(ContextCompat.getColor(this, android.R.color.transparent))
        collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(this, R.color.connectGrey01))

        setSupportActionBar(toolbar)
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.black))

        // Load list fragment
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.connect_user_detail_list_fragment_container_layout, ConnectUserDetailsListFragment())
        transaction.commit()


    }


    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        appBarLayout?.totalScrollRange?.let { maxScroll ->
            val percentage = abs(verticalOffset).toFloat() / maxScroll.toFloat()
            handleToolbarTitleVisibility(percentage)
        }
    }

    fun bindViews(): View {

        val binding = ActivityConnectUserDetailsBinding.inflate(layoutInflater)
        contactDetailHeaderView = binding.connectUserDetailsContactHeaderView
        collapsedTitleTextView = binding.connectUserDetailsToolbarCollapsedTitleTextView
        titleTextView = binding.connectUserDetailsToolbarTitleTextView
        appBarLayout = binding.connectUserDetailAppBarLayout
        backArrowView = binding.backArrowInclude.backArrowView
        collapsingToolbarLayout = binding.connectUserDetailsCollapsingToolbar
        toolbar = binding.connectUserDetailsToolbar
        toolbarTitleTextView = binding.connectUserDetailsToolbarTitleTextView
        overrideEdgeToEdge(binding.root)
        return binding.root
    }

    private fun setHeaderViews(contact: NextivaContact) {
        contactDetailHeaderView.setNameText(contact)
        contactDetailHeaderView.setAvatar(connectUserDetailsViewModel.getUsersAvatarWithFullName())
        collapsedTitleTextView.text = contact.uiName
    }

    private fun handleToolbarTitleVisibility(percentage: Float) {
        if (percentage >= percentageToShowTitleAtToolbar) {
            if (!isTheTitleVisible) {
                ViewUtil.startAlphaAnimation(collapsedTitleTextView,
                    resources.getInteger(R.integer.general_animation_short_duration_millis)
                        .toLong(),
                    View.VISIBLE
                )
                ViewUtil.startAlphaAnimation(titleTextView,
                    resources.getInteger(R.integer.general_animation_short_duration_millis)
                        .toLong(),
                    View.GONE
                )

                isTheTitleVisible = true
            }
        } else {
            if (isTheTitleVisible) {
                ViewUtil.startAlphaAnimation(collapsedTitleTextView,
                    resources.getInteger(R.integer.general_animation_short_duration_millis)
                        .toLong(),
                    View.GONE
                )
                ViewUtil.startAlphaAnimation(titleTextView,
                    resources.getInteger(R.integer.general_animation_short_duration_millis)
                        .toLong(),
                    View.VISIBLE
                )

                isTheTitleVisible = false
            }
        }

        contactDetailHeaderView.visibility = if (isTheTitleVisible) {
            View.INVISIBLE
        } else {
            View.VISIBLE
        }
    }
}