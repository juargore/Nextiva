package com.nextiva.nextivaapp.android;

import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.BACK_BUTTON_PRESSED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.ScreenName.LOGIN_PREFERENCES;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.nextiva.nextivaapp.android.constants.RequestCodes;
import com.nextiva.nextivaapp.android.databinding.ActivityLoginPreferencesBinding;
import com.nextiva.nextivaapp.android.fragments.LoginPreferencesFragment;
import com.nextiva.nextivaapp.android.managers.interfaces.AnalyticsManager;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LoginPreferencesActivity extends BaseActivity {

    protected Toolbar mToolbar;

    @Inject
    protected AnalyticsManager mAnalyticsManager;

    public static Intent newIntent(@NonNull Context context) {
        return new Intent(context, LoginPreferencesActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(bindViews());

        mToolbar.setTitle(getString(R.string.login_preferences_toolbar));
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        final MaterialMenuDrawable materialMenuDrawable = new MaterialMenuDrawable(LoginPreferencesActivity.this,
                                                                                   ContextCompat.getColor(LoginPreferencesActivity.this, R.color.white),
                                                                                   MaterialMenuDrawable.Stroke.REGULAR);
        materialMenuDrawable.setIconState(MaterialMenuDrawable.IconState.ARROW);

        mToolbar.setNavigationIcon(materialMenuDrawable);
        mToolbar.setNavigationContentDescription(R.string.back_button_accessibility_id);
        mToolbar.setNavigationOnClickListener(v -> {
            mAnalyticsManager.logEvent(LOGIN_PREFERENCES, BACK_BUTTON_PRESSED);
            onBackPressed();

        });

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.login_preferences_fragment_container_layout, LoginPreferencesFragment.newInstance())
                .commit();
    }

    private View bindViews() {
        ActivityLoginPreferencesBinding binding = ActivityLoginPreferencesBinding.inflate(getLayoutInflater());

        mToolbar = binding.loginPreferencesToolbar;

        overrideEdgeToEdge(binding.getRoot());

        return binding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == RequestCodes.DEVELOPER_MODE_ENABLED_REQUEST_CODE) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.login_preferences_fragment_container_layout, LoginPreferencesFragment.newInstance())
                    .commit();
        }
    }
}
