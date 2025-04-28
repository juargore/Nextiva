package com.nextiva.nextivaapp.android;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.nextiva.nextivaapp.android.constants.Constants;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.databinding.IncomingCallActivityBinding;
import com.nextiva.nextivaapp.android.fragments.IncomingCallFragment;
import com.nextiva.nextivaapp.android.managers.interfaces.PermissionManager;
import com.nextiva.nextivaapp.android.models.IncomingCall;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class IncomingCallActivity extends BaseActivity implements
        IncomingCallFragment.IncomingCallFragmentListener {
    Handler callCheckHandler = new Handler(Looper.getMainLooper());
    Runnable callCheckRunnable;
    int callCheckDelay = 1000;

    @Inject
    protected PermissionManager mPermissionManager;

    public static Intent newIntent(Context context, IncomingCall incomingCall) {
        return new Intent(context, IncomingCallActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP)
                .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                .addFlags(Intent.FLAG_FROM_BACKGROUND)
                .putExtra(Constants.Calls.PARAMS_INCOMING_CALL, incomingCall);
    }

    public static Intent newIntent(Context context, IncomingCall incomingCall, @Enums.Sip.CallTypes.Type int answerAction) {
        return newIntent(context, incomingCall)
                .putExtra(Constants.Calls.PARAMS_ANSWER_ACTION, answerAction);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(bindViews());

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_FULLSCREEN
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
        getCompositeDisposable().clear();

        if (mSipManager != null)
            mSipManager.setIncomingCallActivity(this);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        } else {
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                            | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

        }

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            IncomingCall incomingCall = new IncomingCall();
            int answerCallType = Enums.Sip.CallTypes.NONE;

            if (extras != null) {
                if (extras.containsKey(Constants.Calls.PARAMS_ANSWER_ACTION)) {
                    answerCallType = extras.getInt(Constants.Calls.PARAMS_ANSWER_ACTION);
                }

                if (extras.containsKey(Constants.Calls.PARAMS_INCOMING_CALL)) {
                    incomingCall = (IncomingCall) extras.getSerializable(Constants.Calls.PARAMS_INCOMING_CALL);
                }
            }

            if (incomingCall != null && answerCallType != Enums.Sip.CallTypes.NONE) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, IncomingCallFragment.newInstance(incomingCall, answerCallType))
                        .commitNow();
            } else if (incomingCall != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, IncomingCallFragment.newInstance(incomingCall))
                        .commitNow();
            }
        }

        checkCallIsActiveStartTimer();
    }

    public View bindViews() {
        IncomingCallActivityBinding binding = IncomingCallActivityBinding.inflate(getLayoutInflater());
        overrideEdgeToEdge(binding.getRoot());
        return binding.getRoot();
    }

    private void checkCallIsActiveStartTimer() {
        callCheckHandler.postDelayed(callCheckRunnable = () -> {
            callCheckHandler.postDelayed(callCheckRunnable, callCheckDelay); //Post must be before checkIncomingCallIsActive or it will loop forever
            checkIncomingCallIsActive();
        }, callCheckDelay);
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkIncomingCallIsActive();
        mLogManager.logToFile(Enums.Logging.STATE_INFO, "Incoming Call onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkCallIsActiveStartTimer();
        checkIncomingCallIsActive();
        mLogManager.logToFile(Enums.Logging.STATE_INFO, "Incoming Call onResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        checkIncomingCallIsActive();
        mLogManager.logToFile(Enums.Logging.STATE_INFO, "Incoming Call onRestart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        checkIncomingCallIsActive();
        callCheckHandler.removeCallbacks(callCheckRunnable);
        mLogManager.logToFile(Enums.Logging.STATE_INFO, "Incoming Call onPause");
    }

    private void checkIncomingCallIsActive() {
        if (mSipManager == null || mSipManager.getIncomingCall() == null) {
            callCheckHandler.removeCallbacks(callCheckRunnable);
            mLogManager.logToFile(Enums.Logging.STATE_INFO, "Incoming Call checkIncomingCallIsActive should finish");
            finish();
        }
    }

    @Override
    public boolean onKeyDown(final int keyCode, final KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
            case KeyEvent.KEYCODE_VOLUME_MUTE:
                mLogManager.logToFile(Enums.Logging.STATE_INFO, "Volume Down Mute Ringtone");
                mSipManager.muteRingtone();
        }

        return super.onKeyDown(keyCode, event);
    }

    // --------------------------------------------------------------------------------------------
    // IncomingCallFragment.IncomingCallFragmentListener Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public void onFinished() {
        if (mSipManager != null) {
            mLogManager.logToFile(Enums.Logging.STATE_INFO, "onFinished Mute Ringtone");
            mSipManager.muteRingtone();
        }
        finish();
    }

    // --------------------------------------------------------------------------------------------
}
