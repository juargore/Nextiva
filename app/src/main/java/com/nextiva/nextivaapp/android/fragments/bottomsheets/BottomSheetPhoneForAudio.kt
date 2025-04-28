package com.nextiva.nextivaapp.android.fragments.bottomsheets

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.google.android.material.snackbar.Snackbar
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.constants.Enums.AssignCoHost
import com.nextiva.nextivaapp.android.databinding.BottomSheetPhoneForAudioBinding
import com.nextiva.nextivaapp.android.meetings.MeetingActivity
import com.nextiva.nextivaapp.android.util.extensions.withFontAwesomeDrawable
import com.nextiva.nextivaapp.android.view.fontawesome.FontTextView
import com.nextiva.nextivaapp.android.viewmodels.MeetingSessionViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BottomSheetPhoneForAudio : BaseBottomSheetDialogFragment(tintedAppBar = true) {

    private lateinit var informationContainer: LinearLayout
    private lateinit var cancelIcon: FontTextView
    private lateinit var phoneForAudioPhoneNumberTextView: AppCompatTextView
    private lateinit var phoneForAudioMeetingIdTextView: AppCompatTextView
    private lateinit var informationIcon: FontTextView
    private lateinit var informationText: AppCompatTextView
    private lateinit var phoneForAudioMessageTextView: AppCompatTextView
    private lateinit var oneTouchDial: AppCompatButton
    private lateinit var coordinator: CoordinatorLayout
    private lateinit var viewModel: MeetingSessionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.bottom_sheet_phone_for_audio, container, false)
        view?.let { bindViews(view) }
        viewModel = (activity as MeetingActivity).getViewModel()
        cancelIcon.setOnClickListener { dismiss() }
        oneTouchDial.setOnClickListener { oneTouchDialOnClickListener() }

        setMeetingDetails()

        if( viewModel.isHost() ){
            informationContainer.visibility = View.VISIBLE
            setClickableMessage()
            val coHostResult = viewModel.getCoHostsAssigned()
            if(coHostResult.isNotEmpty() && coHostResult != AssignCoHost.NO_COHOST){
                val message = if (coHostResult.indexOf(",") > -1) getString(
                    R.string.bottom_sheet_assign_cohost_multiple_success_message,
                    coHostResult
                ) else getString(R.string.bottom_sheet_assign_cohost_success_message, coHostResult)
                 val snackbar = Snackbar.make(
                    coordinator,
                    message,
                    Snackbar.LENGTH_SHORT
                ).withFontAwesomeDrawable(null).setAnchorView(oneTouchDial)
                val snackTextView = snackbar.view.findViewById<View>(com.google.android.material.R.id.snackbar_text) as TextView
                snackTextView.maxLines = 10
                snackbar.show()
            }

        } else
            informationContainer.visibility = View.GONE

        return view
    }

    override fun onPause() {
        super.onPause()
        viewModel.clearCoHostsAssigned()
    }

    private fun bindViews(view: View) {
        val binding = BottomSheetPhoneForAudioBinding.bind(view)
        phoneForAudioPhoneNumberTextView = binding.tvPhoneForAudioPhoneNumber
        phoneForAudioMeetingIdTextView = binding.tvPhoneForAudioMeetingId
        informationContainer = binding.clPhoneForAudioInformation
        cancelIcon = binding.ftvPhoneForAudioIcon
        informationIcon = binding.ftvPhoneForAudioInformationIcon
        informationText = binding.tvPhoneForAudioInformationMessage
        phoneForAudioMessageTextView = binding.bottomSheetPhoneForAudioMessageTextView
        oneTouchDial = binding.btnOneTouchDial
        coordinator = binding.clCohostAssigmentResult
    }

    private fun setMeetingDetails()
    {
        phoneForAudioPhoneNumberTextView.text = viewModel.getMeetingDialNumber()
        phoneForAudioMeetingIdTextView.text = viewModel.getFormattedMeetingId()

    }

    private fun setClickableMessage() {
        val txt: String
        val clickableTxt = getString(R.string.bottom_sheet_phone_for_audio_alert_clickable)
        val color: Int
        if (viewModel.isOnlyHost()) {
            txt = getString(R.string.bottom_sheet_phone_for_audio_alert_only_host)
            color = ContextCompat.getColor(requireContext(), R.color.connectSecondaryRed)
        } else {
            txt = getString(R.string.bottom_sheet_phone_for_audio_alert_host)
            color = ContextCompat.getColor(requireContext(), R.color.connectSecondaryDarkBlue)
            ViewCompat.setBackgroundTintList(
                    informationContainer,
                    ColorStateList.valueOf(
                            ContextCompat.getColor(requireContext(),
                                    R.color.connectSecondaryLightBlue)
                    )
            )
            informationIcon.setTextColor(
                    ContextCompat.getColor(
                            requireContext(),
                            R.color.blueIconColor
                    )
            )
            informationText.setTextColor(
                    ContextCompat.getColor(
                            requireContext(),
                            R.color.connectSecondaryDarkBlue
                    )
            )
        }

        val index = txt.indexOf(clickableTxt)
        val message = SpannableString(txt)
        val clickableSpan: ClickableSpan = object : ClickableSpan() {

            override fun onClick(p0: View) {
                BottomSheetAssignCoHost().show(requireActivity().supportFragmentManager, null)
                viewModel.clearCoHostsAssigned()
                dismiss()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = color
            }
        }
        message.setSpan(
                clickableSpan,
                index,
                (index + 1 + clickableTxt.length),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        informationText.text = message
        informationText.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun oneTouchDialOnClickListener() {
        viewModel.dialMeeting(requireActivity(), Enums.Analytics.ScreenName.CONNECT_MEETINGS_JOIN_USING_PHONE_BOTTOM_SHEET)
    }
}