/*
 * Copyright (c) 2022. Nextiva, inc. to Present.
 * All rights reserved
 */

package com.nextiva.nextivaapp.android.meetings

import android.Manifest
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.amazonaws.services.chime.sdk.meetings.audiovideo.video.DefaultVideoRenderView
import com.amazonaws.services.chime.sdk.meetings.audiovideo.video.gl.DefaultEglCoreFactory
import com.amazonaws.services.chime.sdk.meetings.audiovideo.video.gl.EglCoreFactory
import com.amazonaws.services.chime.sdk.meetings.device.MediaDeviceType
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.databinding.FragmentMeetingPreviewBinding
import com.nextiva.nextivaapp.android.fragments.BaseFragment
import com.nextiva.nextivaapp.android.fragments.bottomsheets.BottomSheetPhoneForAudio
import com.nextiva.nextivaapp.android.managers.interfaces.PermissionManager
import com.nextiva.nextivaapp.android.meetings.models.net.events.MeetingsEventResponse
import com.nextiva.nextivaapp.android.meetings.models.net.media.MediaCallInfoResponse
import com.nextiva.nextivaapp.android.models.AvatarInfo
import com.nextiva.nextivaapp.android.util.ApplicationUtil.isEmulator
import com.nextiva.nextivaapp.android.view.AvatarView
import com.nextiva.nextivaapp.android.view.fontawesome.FontTextView
import com.nextiva.nextivaapp.android.viewmodels.MeetingSessionViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class MeetingPreviewFragment : BaseFragment() {


    @Inject
    lateinit var permissionManager: PermissionManager

    private lateinit var videoPreview: DefaultVideoRenderView
    private lateinit var cameraFlipButton: RelativeLayout
    private lateinit var volumeButton: FontTextView
    private lateinit var microphoneButton: AppCompatImageView
    private lateinit var videoButton: AppCompatImageView
    private lateinit var audioSourceTextView: AppCompatTextView
    private lateinit var meetingTitle: AppCompatTextView
    private lateinit var meetingSubtitle: AppCompatTextView
    private lateinit var meetingParticipantsTextView: AppCompatTextView
    private lateinit var avatarsView: ComposeView
    private lateinit var buttonJoin: AppCompatButton
    private lateinit var buttonCancel: AppCompatButton
    private lateinit var buttonPhoneForAudio: AppCompatButton
    private lateinit var videoPreviewWrapper: ConstraintLayout
    private lateinit var videoDisabledView: ConstraintLayout

    private lateinit var viewModel: MeetingSessionViewModel
    private var isInstantMeeting = false

    private var handler: Handler = Handler(Looper.getMainLooper())
    private var runnable: Runnable? = null
    private var delay = 5000

    // Desktop uses this but I think we don't need it. Will remove after asking the API team.
    private val meetingInfoObserver = Observer<MediaCallInfoResponse> { callInfo ->
        run {
            if (callInfo.dialIn != null && callInfo.dialIn.quickDialIn.isNullOrEmpty()) {
                buttonPhoneForAudio.visibility = VISIBLE
            }
        }
    }
    private val meetingEventInfoObserver = Observer<MeetingsEventResponse> { meetingEventInfo ->
        run {
            if (meetingEventInfo != null) {
                if (meetingEventInfo.dialIn != null && meetingEventInfo.dialIn)
                    buttonPhoneForAudio.visibility = VISIBLE
                else
                    buttonPhoneForAudio.visibility = GONE
            }
        }
    }

    private val waitingForHostObserver = Observer<Boolean> { isWaiting ->
        run {
            if (isAdded && !isInstantMeeting) {
                setUI(isWaiting)
            }
        }
    }

    private val meetingUpdatesObserver = Observer<String> { mediaCallMeetingId ->
        if (mediaCallMeetingId != null && mediaCallMeetingId != "" && runnable == null && viewModel.isMeetingSession()) {
            handler.postDelayed(Runnable {
                handler.postDelayed(runnable!!, delay.toLong())
                viewModel.isMeetingActive(mediaCallMeetingId)
            }.also { runnable = it }, delay.toLong())
        }
    }

    // Graphics/capture related objects
    val eglCoreFactory: EglCoreFactory = DefaultEglCoreFactory()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(
            requireActivity()
        )[MeetingSessionViewModel::class.java]

        viewModel.meetingEventInfoLiveData.observe(requireActivity(), meetingEventInfoObserver)

        //Desktop uses this but I think we don't need it. Will remove after asking the API team.
        viewModel.mediaInfoLiveData.observe(requireActivity(), meetingInfoObserver)
        viewModel.waitingForHost.observe(requireActivity(), waitingForHostObserver)
        viewModel.mediaCallMeetingId.observe(requireActivity(), meetingUpdatesObserver)
        viewModel.stopTimer()


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
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return bindViews(inflater, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        if (activity != null) {

            isInstantMeeting = viewModel.isInstantMeeting
            buttonJoin.setOnClickListener {

                val callback = PermissionManager.PermissionGrantedCallback {
                    activity?.let {
                        stopLocalVideo(true)
                        if (isEmulator())
                            videoPreview.release()

                        viewModel.goToActiveMeetingLiveData.postValue(true)
                    }
                }

                val deniedCallback = PermissionManager.PermissionDeniedCallback {
                    microphoneButton.setImageResource(R.drawable.ic_fa_microphone_slash)
                    viewModel.isMuted = true
                }

                activity?.let {
                    permissionManager.checkMeetingPermission(
                        it,
                        Enums.Analytics.ScreenName.MEETING_PREVIEW_FRAGMENT_SCREEN,
                        Manifest.permission.RECORD_AUDIO,
                        R.string.permission_video_call_record_audio_denied_message,
                        callback,
                        deniedCallback
                    )
                }
            }

            buttonCancel.setOnClickListener {
                viewModel.droppedMeeting()
                activity?.finish()
            }

            setupAudioVideoButtons()

            if (!isEmulator()) {
                videoPreview.init((activity as MeetingActivity).getEglCoreFactory())
            }

            if(viewModel.isCameraOn)
            {
                startLocalVideo()
            }
            else{
                stopLocalVideo()
            }

            buttonPhoneForAudio.visibility = if (!isInstantMeeting) VISIBLE else GONE
            buttonPhoneForAudio.setOnClickListener {
                BottomSheetPhoneForAudio().show(requireActivity().supportFragmentManager, null)
            }
            //TODO: UPDATE FOR AUDIO SOURCE
            audioSourceTextView.text = getString(R.string.meeting_audio_source, "TEMP Headset")


            if (isInstantMeeting) {
                meetingTitle.text = getString(R.string.meeting_instant_meeting)
                buttonJoin.text = getString(R.string.meeting_start_now)
                meetingSubtitle.visibility = INVISIBLE
            } else {
                //TODO:CONNECT MEETING DETAILS
                meetingTitle.text = viewModel.meetingTitle
                buttonJoin.text = getString(R.string.meeting_join)
                meetingSubtitle.text = dateBeforeStart(viewModel.waitingStartTime)
                meetingSubtitle.visibility = VISIBLE

                setUI(false)
            }
        }

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()

        if (viewModel.isCameraOn) {
            startLocalVideo()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        if (!isEmulator() && viewModel.isUsingCameraCaptureSource) {
            stopLocalVideo(true)
            videoPreview.release()
        }
    }

    override fun onPause() {
        super.onPause()
        if (!isEmulator() && viewModel.isUsingCameraCaptureSource) {
            stopLocalVideo(true)
            videoPreview.release()
        }
        runnable?.let { handler.removeCallbacks(it) }
        runnable = null
    }

    override fun onDestroy() {
        super.onDestroy()
        runnable = null
    }

    fun bindViews(inflater: LayoutInflater, container: ViewGroup?): View {
        val binding = FragmentMeetingPreviewBinding.inflate(inflater, container, false)

        videoPreview = binding.videoPreview
        cameraFlipButton = binding.cameraFlipButton
        volumeButton = binding.volumeButton
        microphoneButton = binding.microphoneButton
        videoButton = binding.videoButton
        meetingTitle = binding.meetingTitle
        avatarsView = binding.avatarsComposeView
        buttonJoin = binding.buttonJoin
        buttonCancel = binding.buttonCancel
        buttonPhoneForAudio = binding.btnPhoneForAudio
        audioSourceTextView = binding.audioSourceTextView
        meetingParticipantsTextView = binding.meetingParticipantsTextView
        videoDisabledView = binding.videoDisabledView
        videoPreviewWrapper = binding.videoPreviewWrapper
        meetingSubtitle = binding.meetingSubtitle

        return binding.root
    }

    private fun setupAudioVideoButtons() {
        microphoneButton.setImageResource(if (viewModel.isMuted) R.drawable.ic_fa_microphone_slash else R.drawable.ic_fa_microphone)
        videoButton.setImageResource(if (viewModel.isCameraOn) R.drawable.ic_fa_video_slash else R.drawable.ic_fa_video)
        microphoneButton.setOnClickListener(microphoneOnClickListener())
        cameraFlipButton.setOnClickListener(swapCameraOnClickListener())
        videoButton.setOnClickListener(toggleVideoOnClickListener())
    }

    private fun microphoneOnClickListener(): View.OnClickListener {
        return View.OnClickListener {
            toggleMute()
        }
    }

    private fun swapCameraOnClickListener(): View.OnClickListener {
        return View.OnClickListener {
            swapCamera()
        }
    }

    private fun toggleVideoOnClickListener(): View.OnClickListener {
        return View.OnClickListener {
            toggleVideo()
        }
    }

    private fun toggleMute() {
        if (!viewModel.isMuted) {
            viewModel.audioVideo.realtimeLocalUnmute()
        } else {
            viewModel.audioVideo.realtimeLocalMute()
        }
        viewModel.isMuted = !viewModel.isMuted

        microphoneButton.setImageResource(if (viewModel.isMuted) R.drawable.ic_fa_microphone_slash else R.drawable.ic_fa_microphone)

    }

    private fun toggleVideo() {

        if (viewModel.isCameraOn) {
            stopLocalVideo()
        } else {
            startLocalVideo()
        }

    }

    private fun startLocalVideo() {
        val callback = PermissionManager.PermissionGrantedCallback {
            viewModel.isLocalVideoStarted = true

            if (!viewModel.checkAndInformIsEmulator()) {

                handler.postDelayed({
                    //cameraCaptureSource = viewModel.cameraCaptureSource
                    viewModel.audioVideo.startLocalVideo(viewModel.cameraCaptureSource)
                    viewModel.cameraCaptureSource.addVideoSink(videoPreview)

                    videoPreview.mirror =
                        viewModel.cameraCaptureSource.device?.type == MediaDeviceType.VIDEO_FRONT_CAMERA
                    viewModel.cameraCaptureSource.start()
                    viewModel.isUsingCameraCaptureSource = true
                    viewModel.isCameraOn = true
                }, 500)
            }
            videoPreviewWrapper.visibility = VISIBLE
            videoDisabledView.visibility = GONE
            videoButton.setImageResource(R.drawable.ic_fa_video)
        }

        val deniedCallback = PermissionManager.PermissionDeniedCallback {
            stopLocalVideo()
        }

        activity?.let {
            permissionManager.checkMeetingPermission(
                it,
                Enums.Analytics.ScreenName.MEETING_PREVIEW_FRAGMENT_SCREEN,
                Manifest.permission.CAMERA,
                R.string.permission_video_call_camera_denied_message,
                callback,
                deniedCallback
            )
        }

    }

    private fun stopLocalVideo(isTemporary: Boolean = false) {
        videoPreviewWrapper.visibility = GONE
        videoDisabledView.visibility = VISIBLE
        viewModel.isLocalVideoStarted = false
        if(!isTemporary)
            viewModel.isCameraOn = false

        if (!isEmulator()) {
            viewModel.cameraCaptureSource.stop()
            viewModel.cameraCaptureSource.removeVideoSink(videoPreview)

            viewModel.isUsingCameraCaptureSource = false

            viewModel.audioVideo.stopLocalVideo()
        }
        videoButton.setImageResource(R.drawable.ic_fa_video_slash)
    }

    private fun swapCamera() {
        if (!viewModel.checkAndInformIsEmulator()) {
            viewModel.cameraCaptureSource.switchCamera()
            videoPreview.mirror =
                viewModel.cameraCaptureSource.device?.type == MediaDeviceType.VIDEO_FRONT_CAMERA
        }
    }

    private fun dateBeforeStart(startTime: String): String {
        if (startTime.isBlank())
            return startTime
        var dateFormat = SimpleDateFormat(
            getString(R.string.date_format_short_day_of_week_short_month_day),
            Locale.getDefault()
        )
        val meetingDate = dateFormat.format(startTime.toLong())

        dateFormat = SimpleDateFormat(
            getString(R.string.date_format_short_time_12_hour),
            Locale.getDefault()
        )
        val meetingTime = dateFormat.format(startTime.toLong()).lowercase()

        return getString(R.string.meeting_preview_start_date, meetingDate, meetingTime)
    }

    private fun setUI(isWaiting: Boolean) {
        audioSourceTextView.visibility = GONE //this view seems it's gone on the figma
        if (isWaiting) {
            buttonJoin.visibility = INVISIBLE
            buttonPhoneForAudio.visibility = INVISIBLE
            avatarsView.visibility = INVISIBLE
            meetingParticipantsTextView.text = getString(R.string.meeting_wait_message)
        } else {
            buttonJoin.visibility = VISIBLE
            buttonPhoneForAudio.visibility = VISIBLE
            val count = viewModel.getNumPeopleJoined()
            if (count < 1) {
                avatarsView.visibility = GONE
                meetingParticipantsTextView.text =
                    getString(R.string.meeting_no_participants_count_text)
            } else {
                avatarsView.visibility = VISIBLE
                meetingParticipantsTextView.text =
                    getString(if (count == 1) R.string.meeting_one_participant_count_text else R.string.meeting_participants_count_text,
                        count)
                getAvatars()
            }
        }
        meetingParticipantsTextView.visibility = VISIBLE

    }

    private fun getAvatars() {
        val participants = viewModel.getParticipants()
        var avatarInfo: AvatarInfo
        participants?.let {
            avatarsView.setContent {
                LazyRow {
                    itemsIndexed(if (participants.size > 5) it.take(5) else it) { index, participant ->
                        AndroidView(
                            modifier = Modifier
                                .width(40.dp)
                                .height(40.dp),
                            factory = {
                                avatarInfo = AvatarInfo.Builder()
                                    .setDisplayName(participant.name)
                                    .setFontAwesomeIconResId(R.string.fa_user)
                                    .isConnect(true).build()
                                if (index > 3 && participants.size > 5) {
                                    avatarInfo.setCounter(participants.size - index)
                                }
                                AvatarView(requireContext()).apply {
                                    setAvatar(avatarInfo)
                                }
                            },
                            update = {
                                avatarInfo =
                                    getAvatarInfo(participant.name, participants.size, index)
                                it.setAvatar(avatarInfo)
                            }
                        )
                    }
                }
            }
            avatarsView.visibility = VISIBLE
        }
    }

    private fun getAvatarInfo(name: String?, size: Int, index: Int): AvatarInfo {
        val avatarInfo = AvatarInfo.Builder()
            .setDisplayName(name)
            .setFontAwesomeIconResId(R.string.fa_user)
            .isConnect(true).build()
        if (index > 3 && size > 5) {
            avatarInfo.setPlusCounter(size - index)
        }

        return avatarInfo
    }
}