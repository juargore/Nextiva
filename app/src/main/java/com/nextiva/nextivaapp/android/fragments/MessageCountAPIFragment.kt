/*
 * Copyright (c) 2023. Nextiva, inc. to Present.
 * All rights reserved
 */

package com.nextiva.nextivaapp.android.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.databinding.FragmentMessageCountApiBinding
import com.nextiva.nextivaapp.android.viewmodels.MessageCountAPIViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MessageCountAPIFragment : BaseFragment() {


    companion object {
        fun newInstance(): MessageCountAPIFragment {
            return MessageCountAPIFragment()
        }
    }

    private lateinit var voiceTextView: AppCompatTextView
    private lateinit var voicemailTextView: AppCompatTextView
    private lateinit var emailTextView: AppCompatTextView
    private lateinit var surveyTextView: AppCompatTextView
    private lateinit var chatTextView: AppCompatTextView
    private lateinit var smsTextView: AppCompatTextView
    private lateinit var meetingTextView: AppCompatTextView
    private lateinit var viewModel: MessageCountAPIViewModel

    private val voiceObserver =
        Observer<Int> { count: Int? ->
            voiceTextView.text = getString(R.string.app_preference_message_count_voice, count.toString())
        }

    private val voicemailObserver =
        Observer<Int> { count: Int? ->
            voicemailTextView.text = getString(R.string.app_preference_message_count_voicemail, count.toString())
        }

    private val emailObserver =
        Observer<Int> { count: Int? ->
            emailTextView.text = getString(R.string.app_preference_message_count_email, count.toString())
        }

    private val smsObserver =
        Observer<Int> { count: Int? ->
            smsTextView.text = getString(R.string.app_preference_message_count_sms, count.toString())
        }

    private val surveyObserver =
        Observer<Int> { count: Int? ->
            surveyTextView.text = getString(R.string.app_preference_message_count_survey, count.toString())
        }

    private val chatObserver =
        Observer<Int> { count: Int? ->
            chatTextView.text = getString(R.string.app_preference_message_count_chat, count.toString())
        }

    private val meetingObserver =
        Observer<Int> { count: Int? ->
            meetingTextView.text = getString(R.string.app_preference_message_count_meeting, count.toString())
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val view = inflater.inflate(R.layout.fragment_message_count_api, container, false)
        bindViews(view)
        viewModel = ViewModelProvider(this)[MessageCountAPIViewModel::class.java]
        loadObservers()
        viewModel.loadCount()
        return view
    }

    fun bindViews(view: View) {
        val binding = FragmentMessageCountApiBinding.bind(view)
        voiceTextView = binding.voiceTextView
        voicemailTextView = binding.voicemailTextView
        emailTextView = binding.emailTextView
        smsTextView = binding.smsTextView
        surveyTextView = binding.surveyTextView
        chatTextView = binding.chatTextView
        meetingTextView = binding.meetingTextView
    }

    private fun loadObservers() {
        viewModel.apiVoiceCountLiveData.observe(viewLifecycleOwner, voiceObserver)
        viewModel.apiVoicemailCountLiveData.observe(viewLifecycleOwner, voicemailObserver)
        viewModel.apiEmailCountLiveData.observe(viewLifecycleOwner, emailObserver)
        viewModel.apiChatCountLiveData.observe(viewLifecycleOwner, chatObserver)
        viewModel.apiSurveyCountLiveData.observe(viewLifecycleOwner, surveyObserver)
        viewModel.apiMeetingCountLiveData.observe(viewLifecycleOwner, meetingObserver)
        viewModel.apiSMSCountLiveData.observe(viewLifecycleOwner, smsObserver)
    }
}