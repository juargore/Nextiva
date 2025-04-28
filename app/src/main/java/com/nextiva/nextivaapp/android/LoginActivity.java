/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import dagger.hilt.android.AndroidEntryPoint;
import me.leolin.shortcutbadger.ShortcutBadger;

import javax.inject.Inject;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.nextiva.nextivaapp.android.constants.Constants;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.databinding.ActivityLoginBinding;
import com.nextiva.nextivaapp.android.managers.interfaces.AnalyticsManager;
import com.nextiva.nextivaapp.android.managers.interfaces.DialogManager;
import com.nextiva.nextivaapp.android.managers.interfaces.IntentManager;
import com.nextiva.nextivaapp.android.managers.interfaces.LogManager;
import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager;
import com.nextiva.nextivaapp.android.models.SingleEvent;
import com.nextiva.nextivaapp.android.util.LogUtil;
import com.nextiva.nextivaapp.android.viewmodels.LoginViewModel;

import static com.nextiva.nextivaapp.android.constants.Constants.ONE_SECOND_IN_MILLIS;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.ACCESS_DEVICE_NOT_FOUND_DIALOG_SHOWN;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.SETTINGS_BUTTON_PRESSED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.SIGN_IN_BUTTON_PRESSED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.ScreenName.LOGIN;

/**
 * Created by adammacdonald on 2/1/18.
 */

@AndroidEntryPoint
public class LoginActivity extends BaseActivity {

    private static final String EXTRA_SESSION_ID = "EXTRA_SESSION_ID";
    private static final String EXTRA_TENANT = "EXTRA_TENANT";
    private static final String EXTRA_USER_INFO= "EXTRA_USER_INFO";
    private static final String EXTRA_VOICE_IDENTITY = "EXTRA_VOICE_IDENTITY";


    protected ConstraintLayout mFormLayout;
    protected LinearLayout mLoadingLayout;
    protected AppCompatButton mSignInButton;
    protected AppCompatImageButton mLoginModuleButton;
    protected TextView mDelayedTextView;
    private final Handler mLoginDelayedHandler = new Handler(Looper.getMainLooper());
    private final Runnable mLoginDelayedRunnable = () -> mDelayedTextView.setVisibility(View.VISIBLE);

    @Inject
    protected DialogManager mDialogManager;
    @Inject
    protected AnalyticsManager mAnalyticsManager;
    @Inject
    protected IntentManager mIntentManager;
    @Inject
    protected LogManager mLogManager;

    private LoginViewModel mViewModel;
    private boolean mHasFailed;

    private final Observer<SingleEvent<Boolean>> mLoginErrorObserver = event -> {
        if (event != null && event.getContentIfNotHandled() != null && event.peekContent()) {
            showFormState();
            mDialogManager.showErrorDialog(LoginActivity.this,
                                           LOGIN,
                                           ACCESS_DEVICE_NOT_FOUND_DIALOG_SHOWN,
                                           getString(R.string.login_general_error_dialog_title),
                                           getString(R.string.login_general_error_dialog_content),
                                           (dialog, which) -> { });
        }
    };

    private final Observer<SingleEvent<Boolean>> mNoAccessDeviceFoundObserver = event -> {
        if (event != null && event.getContentIfNotHandled() != null && event.peekContent()) {
            showFormState();
            mDialogManager.showErrorDialog(LoginActivity.this,
                                           LOGIN,
                                           ACCESS_DEVICE_NOT_FOUND_DIALOG_SHOWN,
                                           getString(R.string.login_no_access_device_found_dialog_title),
                                           getString(R.string.login_no_access_device_found_dialog_content),
                                           (dialog, which) -> { });
        }
    };

    private final Observer<Boolean> mFinalizeLoginProcessObserver = loginSuccessful -> {
        if (loginSuccessful != null && loginSuccessful) {
            mViewModel.processExistingDialingServiceSetting();
            mDbManager.markAllCallLogEntriesRead().subscribe();
            startActivity(new Intent(LoginActivity.this, ConnectMainActivity.class));
            finish();
        }
    };

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    public static Intent newIntent(Context context, String type) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(Constants.EXTRA_CHAT_SMS_INTENT, type);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        init();

        Log.e("LOGINNNNN", "Login");

        ShortcutBadger.removeCount(this);
        FirebaseCrashlytics.getInstance().setCustomKey(Enums.Logging.UserDatas.LOGGED_IN, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAnalyticsManager.logScreenView(LOGIN);
    }

    @Override
    protected void onPause() {
        super.onPause();
        showFormState();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLoginDelayedHandler.removeCallbacks(mLoginDelayedRunnable);
    }

    private View bindViews() {
        ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());

        mFormLayout = binding.loginFormLayout;
        mLoadingLayout = binding.loginLoadingLayout;
        mSignInButton = binding.loginSignInButton;
        mDelayedTextView = binding.loginDelayedTextView;

        mSignInButton.setOnClickListener(v -> onSignInButtonClicked());
        binding.loginPreferencesImageButton.setOnClickListener(v -> onPreferencesButtonClicked());

        overrideEdgeToEdge(binding.getRoot());

        return binding.getRoot();
    }

    protected void onSignInButtonClicked() {
        if (mConnectionStateManager.isInternetConnected()) {
            if (!TextUtils.isEmpty(mSharedPreferencesManager.getString(SharedPreferencesManager.BROADWORKS_USERNAME, "")) &&
                    !TextUtils.isEmpty(mSharedPreferencesManager.getString(SharedPreferencesManager.BROADWORKS_PASSWORD, ""))) {

                showLoadingState();
                mSignInButton.setEnabled(true);
                mViewModel.authenticateUser(mSharedPreferencesManager.getString(SharedPreferencesManager.BROADWORKS_USERNAME, ""),
                                            mSharedPreferencesManager.getString(SharedPreferencesManager.BROADWORKS_PASSWORD, ""));

            } else {
                Intent intent = new Intent(LoginActivity.this, com.nextiva.androidNextivaAuth.presentation.ui.MainActivity.class);
                startActivityResultLauncher.launch(intent);
            }


        } else {
            mLogManager.sipLogToFile(Enums.Logging.STATE_ERROR, R.string.error_no_internet_login_message);
            mDialogManager.showErrorDialog(LoginActivity.this,
                                           LOGIN,
                                           Enums.Analytics.EventName.NO_INTERNET_DIALOG_SHOWN,
                                           getString(R.string.error_no_internet_title),
                                           getString(R.string.error_no_internet_login_message),
                                           (dialog, which) -> {
                                           });
            mSignInButton.setEnabled(true);
        }

        mAnalyticsManager.logEvent(LOGIN, SIGN_IN_BUTTON_PRESSED);
    }

    protected void onPreferencesButtonClicked() {
        startActivity(LoginPreferencesActivity.newIntent(LoginActivity.this));
        mAnalyticsManager.logEvent(LOGIN, SETTINGS_BUTTON_PRESSED);
    }

    public void init() {
        mViewModel.clearAndResetAllTables();

        if (!mViewModel.hasLicenseOrPhoneNumber()) {
            startActivity(mIntentManager.getInitialIntent(getApplication()));
        }

        setContentView(bindViews());

        if (getIntent() != null && getIntent().hasExtra(Constants.EXTRA_CHAT_SMS_INTENT)) {
            mDialogManager.showErrorDialog(LoginActivity.this,
                                           LOGIN,
                                           ACCESS_DEVICE_NOT_FOUND_DIALOG_SHOWN,
                                           getString(R.string.sending_sms_failed_user_not_logged_in_title),
                                           getString(R.string.sending_sms_failed_user_not_logged_in_content),
                                           (dialog, which) -> dialog.dismiss());
        }

        mViewModel.getLoginFailedLiveData().observe(LoginActivity.this, mLoginErrorObserver);
        mViewModel.getFinalizeLoginProcessLiveData().observe(LoginActivity.this, mFinalizeLoginProcessObserver);
        mViewModel.getNoAccessDeviceFoundLiveData().observe(LoginActivity.this, mNoAccessDeviceFoundObserver);

        if(!mViewModel.getSessionId().isEmpty())
            mViewModel.clearSession();
    }

    private void showLoadingState() {
        mFormLayout.setVisibility(View.GONE);
        mLoadingLayout.setVisibility(View.VISIBLE);

        mLoginDelayedHandler.postDelayed(mLoginDelayedRunnable, ONE_SECOND_IN_MILLIS * 15);
    }

    private void showFormState() {
        mLoadingLayout.setVisibility(View.GONE);
        mFormLayout.setVisibility(View.VISIBLE);
        mLoginDelayedHandler.removeCallbacks(mLoginDelayedRunnable);
        mDelayedTextView.setVisibility(View.GONE);
    }


    //Direct to NextAuth
    private void setupLoginModuleButton() {
        mLoginModuleButton.setVisibility(View.VISIBLE);
        mLoginModuleButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, com.nextiva.androidNextivaAuth.presentation.ui.MainActivity.class);
            startActivityResultLauncher.launch(intent);
        });
    }
    private final ActivityResultLauncher<Intent> startActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        String sessionId = data.getStringExtra(EXTRA_SESSION_ID);
                        String tenant = data.getStringExtra(EXTRA_TENANT);
                        String userInfo = data.getStringExtra(EXTRA_USER_INFO);
                        String voiceIdentity = data.getStringExtra(EXTRA_VOICE_IDENTITY);

                        if (sessionId != null) {
                            mViewModel.setSessionId(sessionId);
                        }
                        if (userInfo != null) {
                            mViewModel.setUserInfo(userInfo);
                        }
                        if (voiceIdentity != null) {
                            mViewModel.setVoiceIdentity(voiceIdentity);
                        }
                        if (tenant != null) {
                            mViewModel.setSessionTenant(tenant);
                        }

                        showFormState();
                        showLoadingState();
                        mLogManager.setupLogger();
                        new Handler(Looper.getMainLooper()).postDelayed(() -> mViewModel.getBroadworksCredentials(
                                LoginActivity.this
                        ), ONE_SECOND_IN_MILLIS);
                    } else {
                        LogUtil.d("LoginActivity onActivityResult AUTH_LOGIN_REQUEST_CODE: data is null");
                    }
                } else {
                    LogUtil.d("LoginActivity onActivityResult AUTH_LOGIN_REQUEST_CODE: result not OK");
                }
            }
    );

    private void logoutAuth() {
/*        com.nextiva.androidNextivaAuth.domain.repository.CodePostRepository codePostRepository = new com.nextiva.androidNextivaAuth.domain.repository.CodePostRepository();
        codePostRepository.logoutAuth(this, new com.nextiva.androidNextivaAuth.domain.repository.CodePostRepository.LogoutAuthCallback() {
            @Override
            public void onLogoutAuthSuccess() {
                LogUtil.d("LogoutAuth Success");
            }

            @Override
            public void onLogoutAuthFailure() {
                LogUtil.d("LogoutAuth Failure");
            }
        });*/
    }
}
