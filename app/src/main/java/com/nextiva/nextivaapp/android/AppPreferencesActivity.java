package com.nextiva.nextivaapp.android;

import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.BACK_BUTTON_PRESSED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.ScreenName.APP_PREFERENCES;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.nextiva.nextivaapp.android.databinding.ActivityAppPreferencesBinding;
import com.nextiva.nextivaapp.android.fragments.AppPreferencesFragment;
import com.nextiva.nextivaapp.android.managers.interfaces.AnalyticsManager;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AppPreferencesActivity extends BaseActivity {

    protected Toolbar mToolbar;
    protected RelativeLayout backArrowView;

    @Inject
    protected AnalyticsManager mAnalyticsManager;

    public static Intent newIntent(@NonNull Context context) {
        return new Intent(context, AppPreferencesActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(bindViews());

        setSupportActionBar(mToolbar);

        backArrowView.setOnClickListener(v -> {
            mAnalyticsManager.logEvent(APP_PREFERENCES, BACK_BUTTON_PRESSED);
            setResult(RESULT_OK);
            onBackPressed();
        });

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.app_prefs_fragment_container_layout, AppPreferencesFragment.newInstance())
                .commit();
    }

    private View bindViews() {
        ActivityAppPreferencesBinding binding = ActivityAppPreferencesBinding.inflate(getLayoutInflater());

        mToolbar = binding.appPrefsToolbar;
        backArrowView = binding.backArrowInclude.backArrowView;

        overrideEdgeToEdge(binding.getRoot());

        return binding.getRoot();
    }
}
