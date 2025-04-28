package com.nextiva.nextivaapp.android.meetings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import com.nextiva.nextivaapp.android.databinding.FragmentMeetingNotificationBinding
import com.nextiva.nextivaapp.android.fragments.BaseFragment


class MeetingNotificationFragment : BaseFragment() {

    companion object {
        fun newInstance(): MeetingNotificationFragment {
            return MeetingNotificationFragment()
        }
    }

    private lateinit var reJoinButton: AppCompatButton
    private lateinit var learnMoreButton: AppCompatButton
    private lateinit var mainMessage: AppCompatTextView
    private lateinit var secondaryMessage: AppCompatTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
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
        savedInstanceState: Bundle?
    ): View? {
        return bindViews(inflater, container)
    }

    fun bindViews(inflater: LayoutInflater, container: ViewGroup?): View {
        val binding = FragmentMeetingNotificationBinding.inflate(inflater, container, false)
        reJoinButton = binding.btnNotificationRejoin
        learnMoreButton = binding.btnNotificationLearnMore
        mainMessage = binding.tvNotificationMainMessage
        secondaryMessage = binding.tvNotificationSecondMessage
        return binding.root
    }


}