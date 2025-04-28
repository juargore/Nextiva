package com.nextiva.nextivaapp.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.VisibleForTesting;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;

import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.databinding.ActivityLicenseAgreementBinding;
import com.nextiva.nextivaapp.android.fragments.LicenseAcceptanceFragment;
import com.nextiva.nextivaapp.android.managers.interfaces.AnalyticsManager;
import com.nextiva.nextivaapp.android.managers.interfaces.IntentManager;
import com.nextiva.nextivaapp.android.viewmodels.LicenseAgreementViewModel;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
@AndroidEntryPoint
public class LicenseAcceptanceActivity extends BaseActivity implements
        LicenseAcceptanceFragment.LicenseAcceptanceFragmentListener {

    protected Toolbar mToolbar;

    private LicenseAgreementViewModel mViewModel;

    @Inject
    protected IntentManager mIntentManager;
    @Inject
    protected AnalyticsManager mAnalyticsManager;


    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, LicenseAcceptanceActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(bindViews());

        mViewModel = ViewModelProviders.of(this).get(LicenseAgreementViewModel.class);

        setSupportActionBar(mToolbar);
        mToolbar.setNavigationContentDescription(R.string.back_button_accessibility_id);
        setTitle(getString(R.string.license_agreement_title));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAnalyticsManager.logScreenView(Enums.Analytics.ScreenName.LICENSE_AGREEMENT);
    }

    private View bindViews() {
        ActivityLicenseAgreementBinding binding = ActivityLicenseAgreementBinding.inflate(getLayoutInflater());

        mToolbar = binding.licenseAgreementToolbar;

        overrideEdgeToEdge(binding.getRoot());

        return binding.getRoot();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    // --------------------------------------------------------------------------------------------
    // LicenseAcceptanceFragment.LicenseAcceptanceFragmentListener Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public void onAgreementDeclined() {
        finish();
    }
    // --------------------------------------------------------------------------------------------

    @VisibleForTesting
    public LicenseAgreementViewModel getViewModel() {
        return mViewModel;
    }

    @VisibleForTesting
    public void setViewModel(LicenseAgreementViewModel viewModel) {
        mViewModel = viewModel;
    }
}
