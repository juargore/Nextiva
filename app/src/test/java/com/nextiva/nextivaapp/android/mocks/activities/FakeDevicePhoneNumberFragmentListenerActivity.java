package com.nextiva.nextivaapp.android.mocks.activities;

import androidx.fragment.app.FragmentActivity;

import com.nextiva.nextivaapp.android.fragments.DevicePhoneNumberFragment;

public class FakeDevicePhoneNumberFragmentListenerActivity extends FragmentActivity implements DevicePhoneNumberFragment.DevicePhoneNumberFragmentListener {
    @Override
    public void onPhoneNumberSaved() {

    }
}
