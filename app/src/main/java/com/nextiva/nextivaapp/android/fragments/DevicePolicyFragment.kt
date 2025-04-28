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
import com.nextiva.nextivaapp.android.databinding.FragmentDevicePolicyBinding
import com.nextiva.nextivaapp.android.viewmodels.DevicePolicyViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DevicePolicyFragment : BaseFragment() {


    companion object {
        fun newInstance(): DevicePolicyFragment {
            return DevicePolicyFragment()
        }
    }

    private lateinit var callDeclineStatusTextView: AppCompatTextView
    private lateinit var viewModel: DevicePolicyViewModel

    private val callDeclineObserver =
        Observer<Boolean> { isCallDecline: Boolean? ->
            if (isCallDecline != null) {
                callDeclineStatusTextView.text = isCallDecline.toString()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val view = inflater.inflate(R.layout.fragment_device_policy, container, false)
        bindViews(view)
        viewModel = ViewModelProvider(this)[DevicePolicyViewModel::class.java]

        viewModel.isCallDecline.observe(viewLifecycleOwner, callDeclineObserver)
        viewModel.loadDevicePolicies()
        return view
    }

    fun bindViews(view: View) {
        val binding = FragmentDevicePolicyBinding.bind(view)
        callDeclineStatusTextView = binding.callDeclineStatusTextView
    }
}