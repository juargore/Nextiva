/*
 * Copyright (c) 2022. Nextiva, inc. to Present.
 * All rights reserved
 */

package com.nextiva.nextivaapp.android.meetings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.animation.TranslateAnimation
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.amazonaws.services.chime.sdk.meetings.audiovideo.video.capture.CameraCaptureSource
import com.amazonaws.services.chime.sdk.meetings.audiovideo.video.gl.EglCoreFactory
import com.amazonaws.services.chime.sdk.meetings.session.DefaultMeetingSession
import com.amazonaws.services.chime.sdk.meetings.utils.logger.ConsoleLogger
import com.amazonaws.services.chime.sdk.meetings.utils.logger.LogLevel
import com.balysv.materialmenu.MaterialMenuDrawable
import com.google.android.material.appbar.AppBarLayout
import com.nextiva.nextivaapp.android.BaseActivity
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.constants.Constants
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.constants.Enums.Analytics.ScreenName.MEETING_ACTIVITY_SCREEN
import com.nextiva.nextivaapp.android.databinding.ActivityConnectMeetingBinding
import com.nextiva.nextivaapp.android.models.net.calendar.events.CalendarApiEventDetail
import com.nextiva.nextivaapp.android.models.net.mediacall.MediaCallMetaData
import com.nextiva.nextivaapp.android.models.net.mediacall.MediaCallResponse
import com.nextiva.nextivaapp.android.util.ApplicationUtil.isEmulator
import com.nextiva.nextivaapp.android.util.GsonUtil
import com.nextiva.nextivaapp.android.viewmodels.MeetingSessionViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MeetingActivity : BaseActivity() {
    private val ANIMATION_TIME_LENGTH_IN_MILLI: Long = 250
    private val SECONDS_TO_WAIT_TO_CLOSE = 4

    companion object {
        fun newIntent(context: Context, isInstant: Boolean): Intent {
            val intent = Intent(context, MeetingActivity::class.java)
            intent.putExtra(Constants.EXTRA_IS_INSTANT_MEETING, isInstant)
            return intent
        }

        fun newIntent(
            context: Context,
            isInstant: Boolean,
            meetingTitle: String,
            meetingId: String,
            meetingEventId: String,
            meetingStartTime: String,
            email: String,
            fullName: String,
            calendarApiEventDetail: CalendarApiEventDetail,
        ): Intent {
            val intent = Intent(context, MeetingActivity::class.java)
            intent.putExtra(Constants.EXTRA_IS_INSTANT_MEETING, isInstant)
            intent.putExtra(Constants.EXTRA_MEETING_TITLE, meetingTitle)
            intent.putExtra(Constants.EXTRA_MEETING_ID, meetingId)
            intent.putExtra(Constants.EXTRA_MEETING_EVENT_ID, meetingEventId)
            intent.putExtra(Constants.EXTRA_MEETING_START_TIME, meetingStartTime)
            intent.putExtra(Constants.EXTRA_MEETING_EMAIL, email)
            intent.putExtra(Constants.EXTRA_MEETING_FULL_NAME, fullName)
            intent.putExtra(Constants.EXTRA_MEETING_MEETING_DETAILS,
                GsonUtil.getJSON(calendarApiEventDetail))
            return intent
        }

        fun newIntent(
            context: Context,
            isInstant: Boolean,
            meetingTitle: String,
            meetingId: String,
            meetingEventId: String,
            meetingStartTime: String,
        ): Intent {
            val intent = Intent(context, MeetingActivity::class.java)
            intent.putExtra(Constants.EXTRA_IS_INSTANT_MEETING, isInstant)
            intent.putExtra(Constants.EXTRA_MEETING_TITLE, meetingTitle)
            intent.putExtra(Constants.EXTRA_MEETING_ID, meetingId)
            intent.putExtra(Constants.EXTRA_MEETING_EVENT_ID, meetingEventId)
            intent.putExtra(Constants.EXTRA_MEETING_START_TIME, meetingStartTime)
            return intent
        }
    }


    private lateinit var binding: ActivityConnectMeetingBinding
    private lateinit var meetingSessionViewModel: MeetingSessionViewModel
    private lateinit var toolbar: Toolbar
    private lateinit var materialMenuDrawable: MaterialMenuDrawable
    private lateinit var meetingActivityConstraintLayout: ConstraintLayout
    private lateinit var meetingAppBarLayout: AppBarLayout
    private lateinit var meetingTimerTextView: AppCompatTextView
    private lateinit var calendarEvent: CalendarApiEventDetail


    var isInstantMeeting: Boolean = false

    private val mMeetingActiveFragment: MeetingActiveFragment = MeetingActiveFragment.newInstance()
    private val mMeetingNotificationFragment: MeetingNotificationFragment = MeetingNotificationFragment.newInstance()

    private val hideTitleBarHandler = Handler(Looper.getMainLooper())
    private val hideTitleBarRunnable = Runnable {
        hideTitleBar()
    }

    private val meetingStartedObserver = Observer<Boolean> { success ->
        mDialogManager.dismissProgressDialog()

        //Toast.makeText(this, if (success) "Meeting Started" else "Failed to start meeting", Toast.LENGTH_LONG).show()

    }

    private val mediaCallResponseObserver = Observer<MediaCallResponse> { mediaCallResponse ->
        if (mediaCallResponse != null) {
            val sessionConfig =
                meetingSessionViewModel.createSessionConfiguration(mediaCallResponse)

            if (!isEmulator()) {
                val meetingSession = sessionConfig?.let {
                    DefaultMeetingSession(
                        sessionConfig,
                        ConsoleLogger(LogLevel.DEBUG),
                        applicationContext,
                        meetingSessionViewModel.eglCoreFactory
                    )
                }

                meetingSession?.let {
                    meetingSessionViewModel.setMeetingSession(it)
                }


            }
        }


    }

    private val endMeetObserver = Observer<Boolean> { endMeeting ->
        if (endMeeting) {
            meetingSessionViewModel.stopTimer()
            meetingTimerTextView.visibility = View.GONE
            finish()
        }
    }


    private val switchToActiveMeetingObserver = Observer<Boolean> { switch ->
        if (switch)
            selectActiveMeetingFragment()
    }


    private val meetingRuntimeObserver = Observer<Long> { seconds ->
        run {
            if (this::meetingTimerTextView.isInitialized && seconds > 0) {
                if (seconds > 0) {
                    meetingTimerTextView.visibility = View.VISIBLE
                    meetingTimerTextView.text = meetingSessionViewModel.getTimerString(seconds)
                } else {
                    meetingTimerTextView.visibility = View.GONE
                }
            }
        }
    }


    private val meetingInfoButtonPressedResponseObserver = Observer<Boolean> {
        hideTitleBarHandler.removeCallbacks(hideTitleBarRunnable)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        meetingSessionViewModel =
            ViewModelProvider(this)[MeetingSessionViewModel::class.java]
        processIntent(intent)
        getMeetingEventInfo()
        if (savedInstanceState == null && !isEmulator()) {
            val sessionConfig = meetingSessionViewModel.createSessionConfiguration(null)

            val meetingSession = sessionConfig?.let {
                DefaultMeetingSession(
                    it,
                    ConsoleLogger(LogLevel.DEBUG),
                    applicationContext,
                    meetingSessionViewModel.eglCoreFactory
                )
            }

            meetingSession?.let {
                meetingSessionViewModel.setMeetingSession(it)
            }

        }
        setupObservers()
        setContentView(bindViews())
        if (!mConnectionStateManager.isInternetConnected){
            selectNotificationMeetingFragment()
            return
        }
        setSupportActionBar(toolbar)

        materialMenuDrawable = MaterialMenuDrawable(
            this,
            ContextCompat.getColor(this, R.color.connectGrey09),
            MaterialMenuDrawable.Stroke.REGULAR
        )

        materialMenuDrawable.iconState = MaterialMenuDrawable.IconState.ARROW
        meetingAppBarLayout.alpha = 128F
        toolbar.navigationIcon = materialMenuDrawable
        toolbar.setNavigationContentDescription(R.string.back_button_accessibility_id)
        toolbar.setNavigationOnClickListener {
            if (!materialMenuDrawable.isRunning) {
                if (materialMenuDrawable.iconState == MaterialMenuDrawable.IconState.ARROW) {
                    onBackPressed()
                }
            }
        }
        toolbar.title =
            if (isInstantMeeting) getString(R.string.meeting_instant_title) else getString(R.string.meeting_schedule_title)
    }

    fun bindViews(): View {

        binding = ActivityConnectMeetingBinding.inflate(layoutInflater)
        meetingAppBarLayout = binding.meetingAppBarLayout
        meetingActivityConstraintLayout = binding.meetingActivityConstraintLayout
        toolbar = binding.meetingToolBar
        meetingTimerTextView = binding.meetingTimerTextView
        return binding.root
    }

    fun setupObservers() {
        meetingSessionViewModel.startCallSuccessLiveData.observe(this, meetingStartedObserver)
        meetingSessionViewModel.mediaCallResponse.observe(this, mediaCallResponseObserver)
        meetingSessionViewModel.goToActiveMeetingLiveData.observe(
            this,
            switchToActiveMeetingObserver
        )
        meetingSessionViewModel.endMeetingLiveData.observe(this, endMeetObserver)


        meetingSessionViewModel.meetingRunTimeLiveData.observe(
            this,
            meetingRuntimeObserver
        )

        meetingSessionViewModel.meetingInfoButtonPressedLiveData.observe(this,
            meetingInfoButtonPressedResponseObserver
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.meeting_info_button) {
            meetingSessionViewModel.meetingInfoButtonPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        meetingSessionViewModel.stopTimer()
    }

    private fun getMeetingEventInfo() {
        if (meetingSessionViewModel.isInstantMeeting)
            startInstantCall()
        else
            meetingSessionViewModel.getMeetingInfo()
        meetingSessionViewModel.getMeetingEventInfo()
    }

    private fun startInstantCall() {

        val mediaCallMetaData =
            MediaCallMetaData(Enums.MediaCall.CallCategories.INSTANT, null, null, null, null)

        mDialogManager.showProgressDialog(
            this,
            MEETING_ACTIVITY_SCREEN,
            R.string.progress_processing
        )
        meetingSessionViewModel.startMediaCall(
            Enums.MediaCall.AttendeeTypes.HOST,
            mediaCallMetaData,
            Enums.MediaCall.CallTypes.REGULAR,
            getString(R.string.meeting_instant_meeting),
            null
        )

    }

    private fun selectActiveMeetingFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_content_connect_meeting, mMeetingActiveFragment)
            .commit()

        materialMenuDrawable = MaterialMenuDrawable(
            this,
            ContextCompat.getColor(this, R.color.connectWhite),
            MaterialMenuDrawable.Stroke.REGULAR
        )

        materialMenuDrawable.iconState = MaterialMenuDrawable.IconState.ARROW

        toolbar.navigationIcon = materialMenuDrawable
        toolbar.inflateMenu(R.menu.menu_active_meeting)
        toolbar.title = meetingSessionViewModel.meetingTitle
        toolbar.setTitleTextAppearance(this, R.style.Meeting_Title_Text)
        toolbar.setBackgroundColor(
            ContextCompat.getColor(
                this, R.color.activeMeetingToolbarBackground
            )

        )
        toolbar.setTitleTextColor(ContextCompat.getColor(
            this, R.color.connectWhite
        ))
        toolbar.background.alpha = 128

        meetingActivityConstraintLayout.setOnClickListener {
            if (meetingAppBarLayout.visibility == View.VISIBLE) {
                hideTitleBar()
            } else {
                showTitleBar()
            }
        }

        if (isInstantMeeting) {
            val infoButton = toolbar.menu.findItem(R.id.meeting_info_button)
            infoButton.isVisible = false
        }
        meetingSessionViewModel.startMeetingRunTime()

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        setStatusBarColor(ContextCompat.getColor(this, R.color.connectGrey01))

        val windowInsetsController =
            ViewCompat.getWindowInsetsController(window.decorView) ?: return
        // Configure the behavior of the hidden system bars
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        // Hide both the status bar and the navigation bar
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

        hideTitleBarHandler.postDelayed(
            hideTitleBarRunnable,
            Constants.ONE_SECOND_IN_MILLIS * SECONDS_TO_WAIT_TO_CLOSE
        )
    }

    private fun showTitleBar() {
        val animate = TranslateAnimation(
            0F, 0F,
            -meetingAppBarLayout.height.toFloat(), 0F
        )

        animate.duration = ANIMATION_TIME_LENGTH_IN_MILLI
        animate.fillAfter = true
        meetingAppBarLayout.startAnimation(animate)
        meetingAppBarLayout.visibility = View.VISIBLE


        hideTitleBarHandler.removeCallbacks(hideTitleBarRunnable)
//If it should be hidden every 4 seconds uncomment
/*        handler.postDelayed(
            hideTitleBarRunnable,
            Constants.ONE_SECOND_IN_MILLIS * SECONDS_TO_WAIT_TO_CLOSE
        )*/
    }

    private fun hideTitleBar() {
        val animate = TranslateAnimation(
            0F, 0F, 0F,
            -toolbar.height.toFloat()
        )
        animate.duration = ANIMATION_TIME_LENGTH_IN_MILLI
        meetingAppBarLayout.startAnimation(animate)
        meetingAppBarLayout.visibility = View.GONE

        hideTitleBarHandler.removeCallbacks(hideTitleBarRunnable)

        meetingSessionViewModel.hideMeetingInfoIfShowing()
    }

    private fun processIntent(intent: Intent?) {
        isInstantMeeting = intent!!.getBooleanExtra(Constants.EXTRA_IS_INSTANT_MEETING, false)
        meetingSessionViewModel.waitingStartTime =
            intent.getStringExtra(Constants.EXTRA_MEETING_START_TIME) ?: ""
        meetingSessionViewModel.isInstantMeeting = isInstantMeeting
        meetingSessionViewModel.userEmail =
            intent.getStringExtra(Constants.EXTRA_MEETING_EMAIL).toString()
        meetingSessionViewModel.userFullName =
            intent.getStringExtra(Constants.EXTRA_MEETING_FULL_NAME).toString()

        if (intent.hasExtra(Constants.EXTRA_MEETING_MEETING_DETAILS))
            meetingSessionViewModel.calendarEvent =
                GsonUtil.getObject(CalendarApiEventDetail::class.java,
                    intent.getStringExtra(Constants.EXTRA_MEETING_MEETING_DETAILS).toString())

        if (!isInstantMeeting) {
            meetingSessionViewModel.meetingTitle =
                intent.getStringExtra(Constants.EXTRA_MEETING_TITLE).toString()
            intent.getStringExtra(Constants.EXTRA_MEETING_ID)
                ?.let { meetingSessionViewModel.setMeetingId(it) }
            meetingSessionViewModel.meetingEventId =
                intent.getStringExtra(Constants.EXTRA_MEETING_EVENT_ID).toString()
        } else {
            meetingSessionViewModel.meetingTitle = getString(R.string.meeting_instant_meeting)
        }
    }

    fun getEglCoreFactory(): EglCoreFactory = meetingSessionViewModel.eglCoreFactory

    fun getCameraCaptureSource(): CameraCaptureSource = meetingSessionViewModel.cameraCaptureSource

    fun getViewModel(): MeetingSessionViewModel {
        return meetingSessionViewModel
    }

    private fun selectNotificationMeetingFragment(){
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_content_connect_meeting, mMeetingNotificationFragment)
            .commit()
        toolbar.title = ""
    }

}
