package com.nextiva.nextivaapp.android.mocks.activities;

import androidx.fragment.app.FragmentActivity;

import com.nextiva.nextivaapp.android.fragments.LicenseAcceptanceFragment;

public class FakeLicenseAcceptanceFragment extends FragmentActivity implements LicenseAcceptanceFragment.LicenseAcceptanceFragmentListener {

    @Override
    public void onAgreementDeclined() {

    }
}
