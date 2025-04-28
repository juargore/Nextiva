/*
 * Copyright (c) 2022. Nextiva, inc. to Present.
 * All rights reserved
 */

package com.nextiva.nextivaapp.android.meetings

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.util.TypedValue
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.OnClickListener
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.TranslateAnimation
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amazonaws.services.chime.sdk.meetings.audiovideo.AttendeeInfo
import com.amazonaws.services.chime.sdk.meetings.audiovideo.video.DefaultVideoRenderView
import com.amazonaws.services.chime.sdk.meetings.audiovideo.video.RemoteVideoSource
import com.amazonaws.services.chime.sdk.meetings.audiovideo.video.VideoTileState
import com.amazonaws.services.chime.sdk.meetings.audiovideo.video.backgroundfilter.backgroundblur.BackgroundBlurVideoFrameProcessor
import com.amazonaws.services.chime.sdk.meetings.audiovideo.video.backgroundfilter.backgroundreplacement.BackgroundReplacementVideoFrameProcessor
import com.amazonaws.services.chime.sdk.meetings.audiovideo.video.capture.CameraCaptureSource
import com.amazonaws.services.chime.sdk.meetings.audiovideo.video.gl.EglCoreFactory
import com.amazonaws.services.chime.sdk.meetings.device.MediaDeviceType
import com.amazonaws.services.chime.sdk.meetings.session.MeetingSessionCredentials
import com.amazonaws.services.chime.sdk.meetings.session.MeetingSessionStatus
import com.amazonaws.services.chime.sdk.meetings.session.MeetingSessionStatusCode
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE
import com.google.android.material.snackbar.ContentViewCallback
import com.google.android.material.snackbar.Snackbar
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.databinding.FragmentActiveMeetingBinding
import com.nextiva.nextivaapp.android.fragments.BaseFragment
import com.nextiva.nextivaapp.android.managers.interfaces.PermissionManager
import com.nextiva.nextivaapp.android.meetings.adapter.VideoAdapter
import com.nextiva.nextivaapp.android.meetings.models.net.MeetingModel
import com.nextiva.nextivaapp.android.meetings.models.net.media.recordings.OngoingRecordMeetingResponse
import com.nextiva.nextivaapp.android.meetings.utils.CpuVideoProcessor
import com.nextiva.nextivaapp.android.meetings.utils.GpuVideoProcessor
import com.nextiva.nextivaapp.android.meetings.utils.PostLogger
import com.nextiva.nextivaapp.android.models.NextivaContact
import com.nextiva.nextivaapp.android.util.ApplicationUtil.isEmulator
import com.nextiva.nextivaapp.android.util.GsonUtil
import com.nextiva.nextivaapp.android.util.LogUtil
import com.nextiva.nextivaapp.android.util.MenuUtil
import com.nextiva.nextivaapp.android.util.extensions.withFontAwesomeDrawable
import com.nextiva.nextivaapp.android.util.fontawesome.FontDrawable
import com.nextiva.nextivaapp.android.view.AvatarView
import com.nextiva.nextivaapp.android.view.CustomSnackbar
import com.nextiva.nextivaapp.android.view.fontawesome.FontTextView
import com.nextiva.nextivaapp.android.viewmodels.MeetingSessionViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import javax.inject.Inject

@AndroidEntryPoint
class MeetingActiveFragment : BaseFragment(), Runnable, ContentViewCallback {

    @Inject
    lateinit var permissionManager: PermissionManager

    val TAG = "MeetingActiveFragment"
    val SECONDS_TO_WAIT_TO_CLOSE = 4
    val ONE_SECOND_IN_MILLIS = 1000L
    val ANIMATION_TIME_LENGTH_IN_MILLI: Long = 250
    val DELAY_FOR_REMOTE_VIDEO_LOADED: Long = ONE_SECOND_IN_MILLIS / 2

    private var primaryMeetingCredentials: MeetingSessionCredentials? = null
    private lateinit var meetingInfoView: MeetingInfoView
    private lateinit var backgroundMeetingInfoView: MeetingInfoView
    private lateinit var avatarView: AvatarView
    private lateinit var viewModel: MeetingSessionViewModel
    private lateinit var meetingControlsConstraint: ConstraintLayout
    private lateinit var videoPreview: DefaultVideoRenderView
    private lateinit var videoMain: DefaultVideoRenderView
    private lateinit var cameraFlipButton: RelativeLayout
    private lateinit var volumeButton: FontTextView
    private lateinit var microphoneButton: AppCompatImageView
    private lateinit var videoButton: AppCompatImageView
    private lateinit var cameraCaptureSource: CameraCaptureSource
    private lateinit var messagingImageView: AppCompatImageView
    private lateinit var moreTextView: FontTextView
    private lateinit var endMeetingImageView: AppCompatImageView
    private lateinit var inviteParticipantsConstraintlayout: ConstraintLayout
    private lateinit var fragmentView: View
    private lateinit var videoTileAdapter: VideoAdapter
    private lateinit var screenTileAdapter: VideoAdapter
    private lateinit var recyclerViewRoster: RecyclerView
    private lateinit var activeMeetingLocalVideoContainer: ConstraintLayout
    private lateinit var viewVideo: LinearLayout
    private lateinit var recyclerViewVideoCollection: RecyclerView
    private lateinit var remoteVideoConstraintLayout: ConstraintLayout
    private lateinit var remoteVideoAttendeeNameTextView: AppCompatTextView
    private lateinit var recordingContainer: ConstraintLayout
    private lateinit var recordingTimerTextView: AppCompatTextView


    private val mutex = Mutex()
    private val uiScope = CoroutineScope(Dispatchers.Main)
    private val meetingModel: MeetingModel by lazy { ViewModelProvider(this)[MeetingModel::class.java] }


    // Cached for reuse and making sure we don't immediately stop content share
    private lateinit var gpuVideoProcessor: GpuVideoProcessor
    private lateinit var cpuVideoProcessor: CpuVideoProcessor
    private lateinit var backgroundBlurVideoFrameProcessor: BackgroundBlurVideoFrameProcessor
    private lateinit var backgroundReplacementVideoFrameProcessor: BackgroundReplacementVideoFrameProcessor
    private lateinit var eglCoreFactory: EglCoreFactory
    private lateinit var postLogger: PostLogger


    private lateinit var mediaProjectionManager: MediaProjectionManager
    private lateinit var powerManager: PowerManager
    private lateinit var credentials: MeetingSessionCredentials

    private lateinit var recyclerViewScreenShareCollection: RecyclerView

    private var handler: Handler = Handler(Looper.getMainLooper())


    private val videoStatusChangedObserver =
        Observer<MeetingSessionStatus> { meetingSessionStatus ->
            LogUtil.d(TAG, "MEETING Observer frag videoStatusChangedObserver")
            if (meetingSessionStatus.statusCode == MeetingSessionStatusCode.OK) {
                LogUtil.d(TAG, "MEETING Observer frag videoStatusChangedObserver  OK")

            } else {
                LogUtil.d(TAG,
                    "MEETING Observer frag videoStatusChangedObserver: ${
                        GsonUtil.getJSON(meetingSessionStatus.statusCode)
                    } ")
            }
        }

    private val recordingStateInfoObserver =
        Observer<ArrayList<OngoingRecordMeetingResponse>> { recordingStateInfo ->
        }

    private val recordingTimerObserver = Observer<RecordingTimer> {
        if(it.presentTime > 0)
            recordingTimerTextView.text = getString(R.string.meeting_recording_timer, it.presentTimeString)
        else
            recordingTimerTextView.text = getString(R.string.meeting_recording)

    }

    private val availableRemoteVideoSourceObserver =
        Observer<List<RemoteVideoSource>> { remoteVideoList ->
            if (remoteVideoList.isNotEmpty()) {
                showRemoteVideo()

                LogUtil.d(TAG, "MEETING Observer frag availableRemoteVideoSourceObserver")
                if (remoteVideoList.isNotEmpty() && remoteVideoList.last().attendeeId != "") {
                    remoteVideoAttendeeNameTextView.text =
                        viewModel.getAttendeeName(remoteVideoList.last().attendeeId, "")
                    remoteVideoAttendeeNameTextView.visibility = VISIBLE
                }

                for (remoteVideoSource in remoteVideoList) {
                    LogUtil.d(TAG,
                        "MEETING Observer Video Stream ID: ${remoteVideoSource.attendeeId}")
                    //Use for multiple video streams
                }

                if (viewModel.isCameraOn) {
                    startLocalVideo()
                }
            } else {
                hideRemoteVideo()
            }
        }

    private val showMeetingInfoResponseObserver = Observer<Boolean> {
        if (meetingInfoView.visibility == GONE)
            showMeetingInfo()
        else
            hideMeetingInfo()
    }

    private val hideMeetingInfoResponseObserver = Observer<Boolean> {
        if (meetingInfoView.visibility == VISIBLE) {
            hideMeetingInfo()
        }
    }

    private val attendeesJoinedObserver = Observer<AttendeeInfo> { attendeeInfo ->
        if (attendeeInfo.externalUserId != viewModel.userEmail) {
            if(attendeeInfo.externalUserId.contains(viewModel.RECORDING_ATTENDEE_BASE_STRING))
            {
                viewModel.recordingState()

                val customSnackbar = CustomSnackbar.make(meetingControlsConstraint, LENGTH_INDEFINITE, this)
                customSnackbar.setAnchorView(meetingControlsConstraint)
                customSnackbar.setText(getString(R.string.meeting_is_being_recorded_message))
                customSnackbar.setLink(getString(R.string.recorded_message_link_text)) {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.recorded_message_url))))
                }
                customSnackbar.setCloseAction {
                    customSnackbar.dismiss()
                }

                customSnackbar.show()

                recordingContainer.visibility = VISIBLE

            }
            else {

                var attendeeName = viewModel.getAttendeeName(
                    attendeeInfo.attendeeId,
                    attendeeInfo.externalUserId
                )

                Toast.makeText(
                    context,
                    getString(
                        R.string.meeting_attendee_joined,
                        attendeeName
                    ),
                    Toast.LENGTH_SHORT
                ).show()

                // TODO: future Jira story to use real avatar
                val nextivaContact = NextivaContact(attendeeInfo.externalUserId)
                nextivaContact.contactType = Enums.Contacts.ContactTypes.CONNECT_PERSONAL
                nextivaContact.displayName = attendeeName
                val avatarInfo = nextivaContact.avatarInfo
                avatarView.setAvatar(avatarInfo)

                avatarView.visibility = VISIBLE

                remoteVideoAttendeeNameTextView.text = attendeeName
                remoteVideoAttendeeNameTextView.visibility = VISIBLE
            }
        }

    }

    private val attendeesLeftObserver = Observer<Array<AttendeeInfo>> { attendeesInfo ->
        for (attendeeInfo in attendeesInfo) {
            Toast.makeText(context,
                getString(R.string.meeting_attendee_left,
                    viewModel.getAttendeeName(attendeeInfo.attendeeId,
                        attendeeInfo.externalUserId)),
                Toast.LENGTH_SHORT).show()
            avatarView.visibility = GONE
        }
    }

    private val attendeesDroppedObserver = Observer<Array<AttendeeInfo>> { attendeesInfo ->
        for (attendeeInfo in attendeesInfo) {
            if(attendeeInfo.externalUserId.contains(viewModel.RECORDING_ATTENDEE_BASE_STRING))
            {
                viewModel.recordingState()
                Snackbar.make(requireView(), getString(R.string.meeting_recording_stopped_message), Snackbar.LENGTH_LONG)
                    .withFontAwesomeDrawable(FontDrawable(requireContext(),
                        R.string.fa_check_circle,
                        Enums.FontAwesomeIconType.SOLID)
                        .withColor(ContextCompat.getColor(requireContext(),
                            R.color.connectPrimaryGreen))).show()

                recordingContainer.visibility = GONE
            }
            else {
                Toast.makeText(
                    context,
                    getString(
                        R.string.meeting_attendee_dropped,
                        viewModel.getAttendeeName(
                            attendeeInfo.attendeeId,
                            attendeeInfo.externalUserId
                        )
                    ),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private val videoTileRemovedObserver = Observer<VideoTileState> { videoTileState ->
        if (!videoTileState.isLocalTile) {
            hideRemoteVideo()

            viewModel.audioVideo.unbindVideoView(videoTileState.tileId)
        }
    }


    companion object {
        fun newInstance(): MeetingActiveFragment {
            val fragment = MeetingActiveFragment()

            return fragment
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setFullscreen()

        if (Build.VERSION.SDK_INT > 10) {
            registerSystemUiVisibility()
        }

        viewModel = ViewModelProvider(
            requireActivity()
        )[MeetingSessionViewModel::class.java]

        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                viewModel.droppedMeeting()
                activity?.finish()
            }
        }
        activity?.let {
            requireActivity().onBackPressedDispatcher.addCallback(
                it,
                callback
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        fragmentView = bindViews(inflater, container)

        if (!viewModel.isMeetingSession()) {
            viewModel.droppedMeeting()
            activity?.finish()
            return fragmentView
        }

        viewModel.joinMeeting()

        if (!isEmulator()) {
            credentials = viewModel.credentials
            videoPreview.init((activity as MeetingActivity).getEglCoreFactory())
        }




        viewModel.remoteVideoView = videoMain

        setupInitialCameraState()


        view?.let { setupSubViews(it) }
        setMeetingDetails(meetingInfoView, true)
        context?.let {
            ContextCompat.getColor(
                it, R.color.activeMeetingToolbarBackground
            )
        }?.let {
            meetingInfoView.setBackgroundColor(
                it
            )
        }


        if (viewModel.isInstantMeeting) {
            inviteParticipantsConstraintlayout.visibility = View.VISIBLE
            meetingInfoView.visibility = GONE
            setMeetingDetails(backgroundMeetingInfoView, false)
            backgroundMeetingInfoView.visibility = View.VISIBLE
        } else {
            inviteParticipantsConstraintlayout.visibility = GONE
            backgroundMeetingInfoView.visibility = GONE

        }

        viewModel.meetingInfoButtonPressedLiveData.observe(
            requireActivity(),
            showMeetingInfoResponseObserver
        )

        viewModel.hideMeetingInfoIfShowingLiveData.observe(
            requireActivity(),
            hideMeetingInfoResponseObserver
        )

        viewModel.availableRemoteVideoSourceObserver.observe(
            requireActivity(),
            availableRemoteVideoSourceObserver
        )

        viewModel.videoStatusChangedObserver.observe(
            requireActivity(),
            videoStatusChangedObserver
        )
        viewModel.attendeesJoinedObserver.observe(
            requireActivity(),
            attendeesJoinedObserver
        )
        viewModel.attendeesLeftObserver.observe(
            requireActivity(),
            attendeesLeftObserver
        )
        viewModel.attendeesDroppedObserver.observe(
            requireActivity(),
            attendeesDroppedObserver
        )

        viewModel.videoTileRemovedObserver.observe(
            requireActivity(),
            videoTileRemovedObserver
        )

        viewModel.recordingStateInfoLiveData.observe(
            requireActivity(),
            recordingStateInfoObserver
        )

        viewModel.recordingTimerObserver.observe(
            requireActivity(),
            recordingTimerObserver
        )

        setupAudioVideoButtons()
        return fragmentView
    }


    /**
     * Prepare the Fragment host's standard options menu to be displayed.  This is
     * called right before the menu is shown, every time it is shown.  You can
     * use this method to efficiently enable/disable items or otherwise
     * dynamically modify the contents.  See
     * [Activity.onPrepareOptionsMenu]
     * for more information.
     *
     * @param menu The options menu as last shown or first initialized by
     * onCreateOptionsMenu().
     *
     * @see .setHasOptionsMenu
     *
     * @see .onCreateOptionsMenu
     */

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        if (activity == null) {
            return
        }

        inflater.inflate(R.menu.menu_active_meeting, menu)
        MenuUtil.tintAllIcons(menu, ContextCompat.getColor(requireActivity(), R.color.white))
        MenuUtil.setMenuContentDescriptions(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.meeting_info_button) {
            if (meetingInfoView.visibility == View.VISIBLE) {
                hideMeetingInfo()
            } else {
                showMeetingInfo()
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }


    private fun setupInitialCameraState() {
        videoPreview.visibility = GONE
        videoButton.setImageResource(R.drawable.ic_fa_video_slash)
        if (viewModel.isCameraOn) {
            //The preview view is not releasing fast enough causing the feed to break. It might need to be buffered on the viewmodel instead.
            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                if (viewModel.isCameraOn)
                    startLocalVideo()
            }, 500)
        }
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.isCameraPermissionNotGranted == true && requireActivity().checkCallingOrSelfPermission(
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            viewModel.isCameraOn = false
            viewModel.isCameraPermissionNotGranted = false
            toggleVideo()
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.isCameraPermissionNotGranted =
            requireActivity().checkCallingOrSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
    }


    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        stopLocalVideo()
        viewModel.isUsingCameraCaptureSource = false
        videoPreview.release()
    }

    fun bindViews(inflater: LayoutInflater, container: ViewGroup?): View {
        val binding = FragmentActiveMeetingBinding.inflate(inflater, container, false)

        videoMain = binding.videoMain
        videoPreview = binding.videoPreview
        cameraFlipButton = binding.cameraFlipButton
        volumeButton = binding.volumeButton
        microphoneButton = binding.microphoneButton
        videoButton = binding.videoButton
        avatarView = binding.avatarView
        endMeetingImageView = binding.endMeetingButton
        moreTextView = binding.moreButton
        messagingImageView = binding.messagingButton
        meetingInfoView = binding.meetingInfoView
        backgroundMeetingInfoView = binding.backgroundMeetingInfoView
        inviteParticipantsConstraintlayout = binding.inviteParticipantsConstraintLayout
        activeMeetingLocalVideoContainer = binding.activeMeetingLocalVideoContainer
        remoteVideoConstraintLayout = binding.remoteVideoConstraintLayout
        remoteVideoAttendeeNameTextView = binding.remoteVideoAttendeeNameTextView
        recordingContainer = binding.activeMeetingRecordingContainer
        recordingTimerTextView = binding.recordingTimerTextView
        meetingControlsConstraint = binding.meetingControls

        return binding.root
    }

    private fun setupAudioVideoButtons() {
        handler.postDelayed(
            {
                if (!isEmulator()) {
                    if (viewModel.isMuted) {

                        LogUtil.d("AUDIO MUTE AVBUTTON")
                        viewModel.isMuted = viewModel.audioVideo.realtimeLocalMute()
                    }
                }

                microphoneButton.setImageResource(if (viewModel.isMuted) R.drawable.ic_fa_microphone_slash else R.drawable.ic_fa_microphone)

            }, DELAY_FOR_REMOTE_VIDEO_LOADED)

        LogUtil.d("AUDIO MUTE: " + viewModel.isMuted)

        videoButton.setImageResource(if (!viewModel.isCameraOn) R.drawable.ic_fa_video_slash else R.drawable.ic_fa_video)
        microphoneButton.setOnClickListener(microphoneOnClickListener())
        cameraFlipButton.setOnClickListener(swapCameraOnClickListener())
        videoButton.setOnClickListener(toggleVideoOnClickListener())
        messagingImageView.setOnClickListener(messagingOnClickListener())
        moreTextView.setOnClickListener(moreOnClickListener())
        endMeetingImageView.setOnClickListener(endMeetingOnClickListener())
        videoPreview.setOnClickListener(videoPreviewOnClickListener())
    }

    private fun microphoneOnClickListener(): OnClickListener {
        return OnClickListener {
            toggleMute()
        }
    }

    private fun swapCameraOnClickListener(): OnClickListener {
        return OnClickListener {
            swapCamera()
        }
    }

    private fun toggleVideoOnClickListener(): OnClickListener {
        return OnClickListener {
            if (requireActivity().checkCallingOrSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                startLocalVideo()
            else
                toggleVideo()
        }
    }

    private fun endMeetingOnClickListener(): OnClickListener {
        return OnClickListener {
            endMeeting()
            viewModel.droppedMeeting()
        }
    }

    private fun messagingOnClickListener(): OnClickListener {
        return OnClickListener {
            messagingButton()
        }
    }

    private fun moreOnClickListener(): OnClickListener {
        return OnClickListener {
            moreButton()
        }
    }

    private fun videoPreviewOnClickListener(): OnClickListener {
        return OnClickListener {
            if (volumeButton.visibility == VISIBLE) {
                cameraFlipButton.visibility = GONE
                volumeButton.visibility = GONE
            } else {
                cameraFlipButton.visibility = VISIBLE
                volumeButton.visibility = VISIBLE
            }
        }
    }

    private fun toggleMute() {
        if (!viewModel.checkAndInformIsEmulator()) {
            if (viewModel.isMuted) {
                LogUtil.d("AUDIO UNMUTE toggleMute")
                viewModel.isMuted = !viewModel.audioVideo.realtimeLocalUnmute()
            } else {
                LogUtil.d("AUDIO MUTE toggleMute")
                viewModel.isMuted = viewModel.audioVideo.realtimeLocalMute()
            }
        }
        updateMuteIconState()
    }

    private fun updateMuteIconState() {
        microphoneButton.setImageResource(if (viewModel.isMuted) R.drawable.ic_fa_microphone_slash else R.drawable.ic_fa_microphone)
    }

    private fun toggleVideo() {
        if (viewModel.isCameraOn) {
            disableCamera()
        } else {
            enableCamera()
        }
    }

    private fun getActionBarHeight(): Float {
        if (context != null) {
            val tv = TypedValue()
            requireContext().theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)
            return resources.getDimension(tv.resourceId)
        }

        return 0F
    }

    private fun showMeetingInfo() {
        meetingInfoView.visibility = View.VISIBLE
        val animate = TranslateAnimation(
            0F, 0F,
            -meetingInfoView.height.toFloat(), 0F
        )

        animate.duration = ANIMATION_TIME_LENGTH_IN_MILLI
        animate.fillAfter = true
        meetingInfoView.startAnimation(animate)

    }

    private fun hideMeetingInfo() {
        meetingInfoView.visibility = GONE
        val animate = TranslateAnimation(
            0F, 0F, 0F,
            -meetingInfoView.height.toFloat()
        )
        animate.duration = ANIMATION_TIME_LENGTH_IN_MILLI
        meetingInfoView.startAnimation(animate)
    }

    private fun startLocalVideo() {
        val callback = PermissionManager.PermissionGrantedCallback {
            viewModel.isLocalVideoStarted = true

            if (!viewModel.checkAndInformIsEmulator()) {
                cameraCaptureSource = viewModel.cameraCaptureSource
                viewModel.audioVideo.startLocalVideo(cameraCaptureSource)
                cameraCaptureSource.addVideoSink(videoPreview)
                videoPreview.mirror =
                    cameraCaptureSource.device?.type == MediaDeviceType.VIDEO_FRONT_CAMERA
                cameraCaptureSource.start()
                viewModel.isUsingCameraCaptureSource = true
            }

            showLocalVideoFrame()
            videoButton.setImageResource(R.drawable.ic_fa_video)
        }

        val deniedCallback = PermissionManager.PermissionDeniedCallback {
            disableCamera()
        }

        activity?.let {
            permissionManager.checkMeetingPermission(
                it,
                Enums.Analytics.ScreenName.MEETING_ACTIVE_FRAGMENT_SCREEN,
                Manifest.permission.CAMERA,
                R.string.permission_video_call_camera_denied_message,
                callback,
                deniedCallback
            )
        }
    }

    private fun disableCamera(){

        stopLocalVideo()
        exitFullscreen(activity)
        videoPreview.visibility = GONE
        activeMeetingLocalVideoContainer.visibility = GONE
        viewModel.isCameraOn = false

    }
    private fun enableCamera(){
        setFullscreen()
        startLocalVideo()
        viewModel.isCameraOn = true

    }

    private fun stopLocalVideo() {
        if (!viewModel.checkAndInformIsEmulator() && viewModel.isUsingCameraCaptureSource) {
            viewModel.audioVideo.stopLocalVideo()
            viewModel.isLocalVideoStarted = false
            viewModel.isUsingCameraCaptureSource = false
            videoButton.setImageResource(R.drawable.ic_fa_video_slash)
        }
    }

    private fun swapCamera() {
        cameraCaptureSource.switchCamera()
        videoPreview.mirror =
            cameraCaptureSource.device?.type == MediaDeviceType.VIDEO_FRONT_CAMERA
    }


    private fun messagingButton() {
        //TODO:Messaging code here
    }

    private fun moreButton() {
        //TODO: More Button code here
    }

    private fun endMeeting() {
        viewModel.endMeeting()
    }

    private fun oneTouchDialOnClickListener() {
        viewModel.dialMeeting(
            requireActivity(),
            Enums.Analytics.ScreenName.CONNECT_MEETINGS_JOIN_USING_PHONE_BOTTOM_SHEET
        )
    }

    private fun shareInvitationOnClickListener() {
        //TODO: SHARED INVITATION
        Toast.makeText(
            requireContext(),
            "To be covered in the Shared Invitation Story",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun setMeetingDetails(meetingInfoView: MeetingInfoView, showJoinByPhone: Boolean) {
        var meetingDialInPhoneNumber = ""
        if (!viewModel.getMeetingDialNumber().isNullOrEmpty())
            meetingDialInPhoneNumber =
                getString(R.string.meeting_info_US, viewModel.getMeetingDialNumber())


        var meetingDialInTollFreePhoneNumber = ""
        if (!viewModel.getTollFreeNumber().isNullOrEmpty())
            meetingDialInTollFreePhoneNumber =
                getString(R.string.meeting_info_US_Toll_Free, viewModel.getTollFreeNumber())

        var meetingMeetingID = ""
        if (!viewModel.getFormattedMeetingId().isNullOrEmpty())
            meetingMeetingID =
                getString(R.string.meeting_info_meeting_id, viewModel.getFormattedMeetingId())

        meetingInfoView.meetingInfoJoinLinkTextView.text = viewModel.joinMeetingUrl()
        meetingInfoView.meetingInfoDialInPhoneNumberTextView.text = meetingDialInPhoneNumber
        meetingInfoView.meetingInfoDialInTollFreePhoneNumberTextView.text =
            meetingDialInTollFreePhoneNumber
        meetingInfoView.meetingInfoMeetingIdTextView.text = meetingMeetingID

        meetingInfoView.meetingShareInvitationButton.setOnClickListener { shareInvitationOnClickListener() }
        if (showJoinByPhone) {
            meetingInfoView.meetingJoinUsingAPhoneForAudioButton.visibility = View.VISIBLE
            meetingInfoView.meetingJoinUsingAPhoneForAudioButton.setOnClickListener { oneTouchDialOnClickListener() }
        } else {
            meetingInfoView.meetingJoinUsingAPhoneForAudioButton.visibility = GONE
        }
    }


    private fun setupSubViews(view: View) {
        // Roster
        recyclerViewRoster = view.findViewById(R.id.recyclerViewRoster)
        recyclerViewRoster.layoutManager = LinearLayoutManager(activity)

        //FOR multi-vid
        //rosterAdapter = RosterAdapter(meetingModel.currentRoster.values)
        //recyclerViewRoster.adapter = rosterAdapter
        recyclerViewRoster.visibility = View.VISIBLE

        // Video (camera & content)
        viewVideo = view.findViewById(R.id.subViewVideo)
        viewVideo.visibility = View.GONE

        recyclerViewVideoCollection =
            view.findViewById(R.id.recyclerViewVideoCollection)
        //FOR multi-vid
        //recyclerViewVideoCollection.layoutManager = createLinearLayoutManagerForOrientation()
        videoTileAdapter = VideoAdapter(
            meetingModel.videoStatesInCurrentPage,
            meetingModel,
            viewModel.audioVideo,
            cameraCaptureSource,
            backgroundBlurVideoFrameProcessor,
            backgroundReplacementVideoFrameProcessor,
            context,
            viewModel.logger
        )
        recyclerViewVideoCollection.adapter = videoTileAdapter

        recyclerViewScreenShareCollection =
            view.findViewById(R.id.recyclerViewScreenShareCollection)
        recyclerViewScreenShareCollection.layoutManager = LinearLayoutManager(activity)
        screenTileAdapter =
            VideoAdapter(
                meetingModel.currentScreenTiles,
                meetingModel,
                viewModel.audioVideo,
                cameraCaptureSource,
                null,
                null,
                context,
                viewModel.logger
            )
        recyclerViewScreenShareCollection.adapter = screenTileAdapter
        recyclerViewScreenShareCollection.visibility = View.GONE
/* FOR MULTI-VID
        recyclerViewMetrics = view.findViewById(R.id.recyclerViewMetrics)
        recyclerViewMetrics.layoutManager = LinearLayoutManager(activity)
        metricsAdapter = MetricAdapter(meetingModel.currentMetrics.values)
        recyclerViewMetrics.adapter = metricsAdapter
        recyclerViewMetrics.visibility = View.GONE

        // For in meeting Chat
        viewChat = view.findViewById(R.id.subViewChat)
        recyclerViewMessages = view.findViewById(R.id.recyclerViewMessages)
        recyclerViewMessages.layoutManager = LinearLayoutManager(activity)
        messageAdapter = MessageAdapter(meetingModel.currentMessages)
        recyclerViewMessages.adapter = messageAdapter

        editTextMessage = view.findViewById(R.id.editTextChatBox)
        editTextMessage.setOnEditorActionListener { _, actionId, _ ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_SEND -> {
                    sendMessage()
                    true
                }
                else -> false
            }
        }
        buttonSendChat = view.findViewById(R.id.buttonSubmitMessage)
        buttonSendChat.let {
            it.setOnClickListener { sendMessage() }
        }

        viewChat.visibility = View.GONE*/

    }

    private fun showLocalVideoFrame() {
        handler.postDelayed({
            activeMeetingLocalVideoContainer.bringToFront()
            activeMeetingLocalVideoContainer.visibility = VISIBLE
            activeMeetingLocalVideoContainer.bringChildToFront(videoPreview)
            videoPreview.visibility = INVISIBLE
            videoPreview.bringToFront()
            videoPreview.visibility = VISIBLE
        }, DELAY_FOR_REMOTE_VIDEO_LOADED)
    }

    private fun showRemoteVideo() {
        viewModel.remoteVideoView = videoMain
        remoteVideoConstraintLayout.visibility = VISIBLE
        viewModel.audioVideo.startRemoteVideo()
        avatarView.visibility = GONE
    }

    private fun hideRemoteVideo() {
        remoteVideoConstraintLayout.visibility = GONE
        viewModel.audioVideo.stopRemoteVideo()
        avatarView.visibility = VISIBLE
    }


    fun setFullscreen(activity: FragmentActivity?) {
        activity?.window?.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    fun exitFullscreen(activity: FragmentActivity?) {
        activity?.window?.setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
    }

    fun isImmersiveAvailable(): Boolean {
        return Build.VERSION.SDK_INT >= 19
    }

    fun onWindowFocusChanged(hasFocus: Boolean) {
        if (hasFocus) {
            _handler.removeCallbacks(this)
            _handler.postDelayed(this, 300)
        } else {
            _handler.removeCallbacks(this)
        }
    }

    fun onKeyDown(keyCode: Int) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            _handler.removeCallbacks(this)
            _handler.postDelayed(this, 500)
        }
    }

    override fun onStop() {
        _handler.removeCallbacks(this)
        super.onStop()
    }

    override fun run() {
        setFullscreen()
    }

    fun setFullscreen() {
        setFullscreen(activity)
    }


    private val _handler = Handler()

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private fun registerSystemUiVisibility() {
        val decorView = requireActivity().window.decorView
        decorView.setOnSystemUiVisibilityChangeListener { visibility ->
            if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN === 0) {
                setFullscreen()
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private fun unregisterSystemUiVisibility() {
        val decorView = requireActivity().window.decorView
        decorView.setOnSystemUiVisibilityChangeListener(null)
    }

    override fun animateContentIn(delay: Int, duration: Int) {
    }

    override fun animateContentOut(delay: Int, duration: Int) {
    }

}