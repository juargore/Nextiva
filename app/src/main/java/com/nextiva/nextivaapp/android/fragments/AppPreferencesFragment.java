/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.fragments;

import static com.nextiva.nextivaapp.android.constants.Constants.ChromeOS.CHROME_OS_DEVICE_MANAGEMENT_FEATURE;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.CLEAR_ALL_LOGS_BUTTON_PRESSED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.CLEAR_ALL_LOGS_DIALOG_CANCEL_BUTTON_PRESSED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.CLEAR_ALL_LOGS_DIALOG_CLEAR_LOGS_BUTTON_PRESSED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.CLEAR_ALL_LOGS_DIALOG_SHOWN;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.EMAIL_LOGS_TO_SUPPORT_BUTTON_PRESSED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.ENABLE_LOGGING_SWITCH_CHECKED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.ENABLE_LOGGING_SWITCH_UNCHECKED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.FILE_LOGGING_SWITCH_CHECKED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.FILE_LOGGING_SWITCH_UNCHECKED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.NO_LOG_FILE_DIALOG_OK_BUTTON_PRESSED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.NO_LOG_FILE_DIALOG_SHOWN;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.SIP_LOGGING_SWITCH_CHECKED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.SIP_LOGGING_SWITCH_UNCHECKED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.XMPP_LOGGING_SWITCH_CHECKED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.XMPP_LOGGING_SWITCH_UNCHECKED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.ScreenName.APP_PREFERENCES;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.nextiva.nextivaapp.android.BuildConfig;
import com.nextiva.nextivaapp.android.DatabaseCountUtilityActivity;
import com.nextiva.nextivaapp.android.DesignSystemUtilityActivity;
import com.nextiva.nextivaapp.android.DevicePolicyActivity;
import com.nextiva.nextivaapp.android.FeatureFlagsActivity;
import com.nextiva.nextivaapp.android.FontAwesomeUtilityActivity;
import com.nextiva.nextivaapp.android.HealthCheckActivity;
import com.nextiva.nextivaapp.android.LicenseCheckActivity;
import com.nextiva.nextivaapp.android.MessageCountAPIActivity;
import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.SipConfigurationActivity;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.managers.interfaces.AnalyticsManager;
import com.nextiva.nextivaapp.android.managers.interfaces.DialogManager;
import com.nextiva.nextivaapp.android.managers.interfaces.IntentManager;
import com.nextiva.nextivaapp.android.managers.interfaces.LogManager;
import com.nextiva.nextivaapp.android.managers.interfaces.PermissionManager;
import com.nextiva.nextivaapp.android.managers.interfaces.SettingsManager;
import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager;
import com.nextiva.nextivaapp.android.meetings.MeetingActivity;
import com.nextiva.nextivaapp.android.sip.tone.ToneActivity;
import com.nextiva.nextivaapp.android.util.ApplicationUtil;
import com.nextiva.nextivaapp.android.view.CheckBoxPreferenceWithContentDescription;
import com.nextiva.nextivaapp.android.viewmodels.AppPreferenceViewModel;

import java.io.File;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AppPreferencesFragment extends PreferenceFragmentCompat {

    private static final int LOGS_DELETE_DIRECTORY_DELAY_MILLIS = 100;
    @Inject
    protected DialogManager mDialogManager;
    @Inject
    protected AnalyticsManager mAnalyticsManager;
    @Inject
    protected IntentManager mIntentManager;
    @Inject
    protected PermissionManager mPermissionManager;
    @Inject
    protected SettingsManager mSettingsManager;
    @Inject
    protected SharedPreferencesManager mSharedPreferencesManager;
    @Inject
    protected LogManager mLogManager;
    private PreferenceCategory mTroubleshootingCategory;
    private PreferenceCategory mQaCategory;
    private CheckBoxPreferenceWithContentDescription mEnableLoggingCheckbox;
    private CheckBoxPreferenceWithContentDescription mFileLoggingCheckbox;
    private CheckBoxPreferenceWithContentDescription mXMPPLoggingCheckbox;
    private CheckBoxPreferenceWithContentDescription mSIPLoggingCheckbox;
    private CheckBoxPreferenceWithContentDescription mDisplayAudioVideoCheckbox;
    private CheckBoxPreferenceWithContentDescription mEnableOldActiveCallLayout;
    private CheckBoxPreferenceWithContentDescription mDisplaySIPStateCheckbox;
    private CheckBoxPreferenceWithContentDescription mDisplaySIPErrorCheckbox;
    private CheckBoxPreferenceWithContentDescription mSipEchoCancellationCheckbox;
    private CheckBoxPreferenceWithContentDescription mSmsShowConfirmDialogToDeleteSmsCheckbox;
    private CheckBoxPreferenceWithContentDescription mSmsEnableSwipeActionsCheckbox;
    private CheckBoxPreferenceWithContentDescription mMobileBlockNumberForCallingCheckbox;
    private ListPreference mDarkModeList;
    private Preference mEmailLogs;
    private Preference mClearLogs;
    private Preference mHealthCheck;
    private Preference mLicenseCheck;
    private Preference mVideoConference;
    private Preference mFeatureFlags;
    private Preference mFontAwesomeUtility;
    private Preference mDesignSystemUtility;
    private Preference mResetLocalContactsDialogShown;
    private Preference mDatabaseCountUtility;
    private Preference mSipConfiguration;
    private Preference mDevicePolicy;
    private Preference mMessageCountAPI;
    private Preference mLogoutToken;
    private Preference mClearToken;
    private Preference mDeleteSms;
    @Nullable
    private Preference mExpireContactCache;
    private Preference mRingtonePreference;
    private Preference mNotificationTonePreference;

    private AppPreferenceViewModel mViewModel;

    public static AppPreferencesFragment newInstance() {
        Bundle args = new Bundle();

        AppPreferencesFragment fragment = new AppPreferencesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private boolean fileLoggingOnPreferenceChange(Boolean newValue) {
        if (getActivity() == null) {
            return false;
        }

        setCheckboxChecked(mFileLoggingCheckbox, mFileLoggingPreferenceChangeListener, newValue);

        if (newValue) {

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                mPermissionManager.requestFileLoggingPermission(
                        getActivity(),
                        APP_PREFERENCES,
                        () -> {
                            mViewModel.enableFileLogging(mFileLoggingCheckbox.isChecked());
                            mViewModel.setupLogger();
                        },
                        () -> {
                            setCheckboxChecked(mFileLoggingCheckbox, mFileLoggingPreferenceChangeListener, false);
                            mViewModel.enableFileLogging(false);
                        });
            } else {
                mViewModel.enableFileLogging(mFileLoggingCheckbox.isChecked());
                mViewModel.setupLogger();
            }

        } else {
            mViewModel.enableFileLogging(false);
        }

        disableLogsIfNotLogsEnabled();
        mAnalyticsManager.logEvent(APP_PREFERENCES, newValue ?
                FILE_LOGGING_SWITCH_CHECKED :
                FILE_LOGGING_SWITCH_UNCHECKED);
        FirebaseCrashlytics.getInstance().setCustomKey(Enums.Logging.UserDatas.FILE_LOGGING, newValue.toString());

        return false;
    }

    private boolean xmppLoggingOnPreferenceChange(Boolean newValue) {
        setCheckboxChecked(mXMPPLoggingCheckbox, mXmppLoggingPreferenceChangeListener, newValue);
        mViewModel.enableXmppLogging(newValue);

        disableLogsIfNotLogsEnabled();
        mAnalyticsManager.logEvent(APP_PREFERENCES, newValue ?
                XMPP_LOGGING_SWITCH_CHECKED :
                XMPP_LOGGING_SWITCH_UNCHECKED);

        FirebaseCrashlytics.getInstance().setCustomKey(Enums.Logging.UserDatas.XMPP_LOGGING, newValue);
        return false;
    }    private final Preference.OnPreferenceChangeListener mDisplayAudioVideoPreferenceChangeListener = displayAudioVideoPreferenceChangeListener();

    private Preference.OnPreferenceChangeListener displayAudioVideoPreferenceChangeListener() {
        return (preference, newValue) -> {
            setCheckboxChecked(mDisplayAudioVideoCheckbox, mDisplayAudioVideoPreferenceChangeListener, newValue.equals(true));
            mViewModel.enableDisplayAudioVideoStats(newValue.equals(true));

            mAnalyticsManager.logEvent(APP_PREFERENCES, newValue.equals(true) ?
                    Enums.Analytics.EventName.DISPLAY_AUDIO_VIDEO_STATS_CHECKED :
                    Enums.Analytics.EventName.DISPLAY_AUDIO_VIDEO_STATS_UNCHECKED);

            return false;
        };
    }

    private final Preference.OnPreferenceChangeListener mEnableOldActiveCallLayoutPreferenceChangeListener = enableOldActiveCallLayoutPreferenceChangeListener();
    private Preference.OnPreferenceChangeListener enableOldActiveCallLayoutPreferenceChangeListener() {
        return (preference, newValue) -> {
            setCheckboxChecked(mEnableOldActiveCallLayout, mEnableOldActiveCallLayoutPreferenceChangeListener, newValue.equals(true));
            mViewModel.enableOldActiveCallLayout(newValue.equals(true));
            return false;
        };
    }

    private Preference.OnPreferenceChangeListener displaySIPStatePreferenceChangeListener() {
        return (preference, newValue) -> {
            setCheckboxChecked(mDisplaySIPStateCheckbox, mDisplaySIPStatePreferenceChangeListener, newValue.equals(true));
            mViewModel.enableDisplaySIPState(newValue.equals(true));

            mAnalyticsManager.logEvent(APP_PREFERENCES, newValue.equals(true) ?
                    Enums.Analytics.EventName.DISPLAY_SIP_STATE_CHECKED :
                    Enums.Analytics.EventName.DISPLAY_SIP_STATE_UNCHECKED);

            return false;
        };
    }

    private Preference.OnPreferenceChangeListener displaySIPErrorPreferenceChangeListener() {
        return (preference, newValue) -> {
            setCheckboxChecked(mDisplaySIPErrorCheckbox, mDisplaySIPErrorPreferenceChangeListener, newValue.equals(true));
            mViewModel.enableDisplaySIPError(newValue.equals(true));

            mAnalyticsManager.logEvent(APP_PREFERENCES, newValue.equals(true) ?
                    Enums.Analytics.EventName.DISPLAY_SIP_ERROR_CHECKED :
                    Enums.Analytics.EventName.DISPLAY_SIP_ERROR_UNCHECKED);

            return false;
        };
    }

    private final Preference.OnPreferenceChangeListener mDisplaySIPStatePreferenceChangeListener = displaySIPStatePreferenceChangeListener();

    private boolean echoCancellationPreferenceChange(Boolean newValue) {
        setCheckboxChecked(mSipEchoCancellationCheckbox, mSipEchoCancellationPreferenceChangeListener, newValue);
        mViewModel.enableEchoCancellation(newValue);

        return false;
    }

    private boolean showConfirmationDialogToDeleteSmsPreferenceChange(Boolean newValue) {
        setCheckboxChecked(mSmsShowConfirmDialogToDeleteSmsCheckbox, mSmsShowConfirmDialogPreferenceChangeListener, newValue);
        mViewModel.enableShowDialogToDelete(newValue);

        return false;
    }

    private boolean EnableSwipeActionsSmsPreferenceChange(Boolean newValue) {
        setCheckboxChecked(mSmsEnableSwipeActionsCheckbox, mSmsEnableSwipeActionsChangeListener, newValue);
        mViewModel.enableSwipeActions(newValue);

        return false;
    }

    private boolean EnableBlockNumberForCallingPreferenceChange(Boolean newValue) {
        setCheckboxChecked(mMobileBlockNumberForCallingCheckbox, mBlockNumberForCallingChangeListener, newValue);
        mViewModel.enableBlockNumberForCalling(newValue);

        return false;
    }

    private boolean sipLoggingPreferenceChange(Boolean newValue) {
        setCheckboxChecked(mSIPLoggingCheckbox, mSipLoggingPreferenceChangeListener, newValue);
        mViewModel.enableSipLogging(newValue);
        disableLogsIfNotLogsEnabled();
        mAnalyticsManager.logEvent(APP_PREFERENCES, newValue ?
                SIP_LOGGING_SWITCH_CHECKED :
                SIP_LOGGING_SWITCH_UNCHECKED);
        FirebaseCrashlytics.getInstance().setCustomKey(Enums.Logging.UserDatas.SIP_LOGGING, newValue);

        return false;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences_troubleshooting);
        addPreferencesFromResource(R.xml.preferences_sip);
        addPreferencesFromResource(R.xml.preferences_sms);
        addPreferencesFromResource(R.xml.preferences_other);

        //Enable QA Tools based on build environment
        if (!TextUtils.equals(getString(R.string.app_environment), getString(R.string.environment_prod))) {
            addPreferencesFromResource(R.xml.preferences_qa);
        }

        mViewModel = new ViewModelProvider(this).get(AppPreferenceViewModel.class);

        mEnableLoggingCheckbox = getPreferenceScreen().findPreference(getString(R.string.app_preferences_key_enable_logging));
        mFileLoggingCheckbox = getPreferenceScreen().findPreference(getString(R.string.app_preferences_key_file_logging));
        mXMPPLoggingCheckbox = getPreferenceScreen().findPreference(getString(R.string.app_preferences_key_xmpp_logging));
        mSIPLoggingCheckbox = getPreferenceScreen().findPreference(getString(R.string.app_preferences_key_sip_logging));
        mDisplayAudioVideoCheckbox = getPreferenceScreen().findPreference(getString(R.string.app_preferences_key_audio_video_stats));
        mEnableOldActiveCallLayout = getPreferenceScreen().findPreference(getString(R.string.app_preferences_key_new_active_call_layout));
        mDisplaySIPStateCheckbox = getPreferenceScreen().findPreference(getString(R.string.app_preferences_key_sip_state));
        mDisplaySIPErrorCheckbox = getPreferenceScreen().findPreference(getString(R.string.app_preferences_key_sip_error));
        mSipEchoCancellationCheckbox = getPreferenceScreen().findPreference(getString(R.string.app_preference_key_echo_cancellation));
        mSmsShowConfirmDialogToDeleteSmsCheckbox = getPreferenceScreen().findPreference(getString(R.string.app_preference_key_show_confirm_dialog_to_delete));
        mSmsEnableSwipeActionsCheckbox = getPreferenceScreen().findPreference(getString(R.string.app_preference_key_enable_swipe_actions));
        mTroubleshootingCategory = getPreferenceScreen().findPreference(getString(R.string.app_preferences_key_troubleshooting));
        mQaCategory = getPreferenceScreen().findPreference(getString(R.string.app_preferences_key_qa));

        mEmailLogs = getPreferenceScreen().findPreference(getString(R.string.app_preferences_key_email_logs));
        mClearLogs = getPreferenceScreen().findPreference(getString(R.string.app_preferences_key_clear_logs));
        mHealthCheck = getPreferenceScreen().findPreference(getString(R.string.app_preferences_key_health_check));
        mLicenseCheck = getPreferenceScreen().findPreference(getString(R.string.app_preferences_key_license_check));
        mFeatureFlags = getPreferenceScreen().findPreference(getString(R.string.app_preferences_key_feature_flags));
        mSipConfiguration = getPreferenceScreen().findPreference(getString(R.string.app_preferences_key_sip_configuration));
        mVideoConference = getPreferenceScreen().findPreference(getString(R.string.app_preferences_key_video_conference));
        mFontAwesomeUtility = getPreferenceScreen().findPreference(getString(R.string.app_preferences_key_font_awesome_utility));
        mDesignSystemUtility = getPreferenceScreen().findPreference(getString(R.string.app_preferences_key_design_system_utility));
        mResetLocalContactsDialogShown = getPreferenceScreen().findPreference(getString(R.string.app_preferences_key_reset_local_contacts_dialog_shown));
        mExpireContactCache = getPreferenceScreen().findPreference(getString(R.string.app_preferences_key_expire_contact_cache));
        mDatabaseCountUtility = getPreferenceScreen().findPreference(getString(R.string.app_preferences_key_database_count_utility));
        mDevicePolicy = getPreferenceScreen().findPreference(getString(R.string.app_preferences_key_device_policy));
        mMessageCountAPI = getPreferenceScreen().findPreference(getString(R.string.app_preferences_key_message_count_api));
        mLogoutToken = getPreferenceScreen().findPreference(getString(R.string.app_preferences_key_logout_token));
        mClearToken = getPreferenceScreen().findPreference(getString(R.string.app_preferences_key_clear_token));
        mDeleteSms = getPreferenceScreen().findPreference(getString(R.string.app_preferences_key_delete_sms));
        mRingtonePreference = getPreferenceScreen().findPreference(getString(R.string.app_preferences_key_set_ringtone));
        mNotificationTonePreference = getPreferenceScreen().findPreference(getString(R.string.app_preferences_key_set_sms_tone));
        final Preference crashApp = getPreferenceScreen().findPreference(getString(R.string.app_preferences_key_crash_the_app));
        final Preference generalLogs = getPreferenceScreen().findPreference(getString(R.string.app_preferences_key_generate_logs));
        mDarkModeList = getPreferenceScreen().findPreference(getString(R.string.app_preferences_key_night_mode));

        if (mEmailLogs != null) {
            mEmailLogs.setOnPreferenceClickListener(preference -> {
                mAnalyticsManager.logEvent(APP_PREFERENCES, EMAIL_LOGS_TO_SUPPORT_BUTTON_PRESSED);

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                    mPermissionManager.requestFileLoggingPermission(
                            getActivity(),
                            APP_PREFERENCES,
                            this::emailLogsFromFile,
                            null);
                } else {
                    emailLogsFromFile();
                }

                return false;
            });
        }

        if (mClearLogs != null) {
            mClearLogs.setOnPreferenceClickListener(preference -> {
                mAnalyticsManager.logEvent(APP_PREFERENCES, CLEAR_ALL_LOGS_BUTTON_PRESSED);

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                    mPermissionManager.requestFileLoggingPermission(
                            getActivity(),
                            APP_PREFERENCES,
                            this::clearLogs,
                            null);
                } else {
                    clearLogs();
                }

                return false;
            });
        }

        if (mHealthCheck != null) {
            mHealthCheck.setOnPreferenceClickListener(preference -> {
                Intent intent = new Intent(getActivity(), HealthCheckActivity.class);
                getActivity().startActivity(intent);

                return false;
            });
        }

        if (mLicenseCheck != null) {
            mLicenseCheck.setOnPreferenceClickListener(preference -> {
                Intent intent = new Intent(getActivity(), LicenseCheckActivity.class);
                getActivity().startActivity(intent);
                return false;
            });
        }

        if (mFeatureFlags != null) {
            mFeatureFlags.setOnPreferenceClickListener(preference -> {
                Intent intent = new Intent(getActivity(), FeatureFlagsActivity.class);
                getActivity().startActivity(intent);

                return false;
            });
        }

        if (mSipConfiguration != null) {
            mSipConfiguration.setOnPreferenceClickListener(preference -> {
                Intent intent = new Intent(getActivity(), SipConfigurationActivity.class);
                getActivity().startActivity(intent);

                return false;
            });
        }

        if (mVideoConference != null) {
            mVideoConference.setOnPreferenceClickListener(preference -> {
                Intent intent = new Intent(getActivity(), MeetingActivity.class);
                getActivity().startActivity(intent);
                return false;
            });
        }

        if (mFontAwesomeUtility != null) {
            mFontAwesomeUtility.setOnPreferenceClickListener(preference -> {
                Intent intent = new Intent(getActivity(), FontAwesomeUtilityActivity.class);
                getActivity().startActivity(intent);
                return false;
            });
        }

        if (mDesignSystemUtility != null) {
            mDesignSystemUtility.setOnPreferenceClickListener(preference -> {
                Intent intent = new Intent(getActivity(), DesignSystemUtilityActivity.class);
                getActivity().startActivity(intent);
                return false;
            });
        }

        if (mDatabaseCountUtility != null) {
            mDatabaseCountUtility.setOnPreferenceClickListener(preference -> {
                Intent intent = new Intent(getActivity(), DatabaseCountUtilityActivity.class);
                getActivity().startActivity(intent);
                return false;
            });
        }

        if (mDevicePolicy != null) {
            mDevicePolicy.setOnPreferenceClickListener(preference -> {
                Intent intent = new Intent(getActivity(), DevicePolicyActivity.class);
                getActivity().startActivity(intent);
                return false;
            });
        }

        if (mMessageCountAPI != null) {
            mMessageCountAPI.setOnPreferenceClickListener(preference -> {
                Intent intent = new Intent(getActivity(), MessageCountAPIActivity.class);
                getActivity().startActivity(intent);
                return false;
            });
        }

        if (mLogoutToken != null) {
            mLogoutToken.setOnPreferenceClickListener(preference -> false);
        }

        if (mClearToken != null) {
            mClearToken.setOnPreferenceClickListener(preference -> false);
        }

        if (mDeleteSms != null) {
            mDeleteSms.setOnPreferenceClickListener(preference -> {
                mViewModel.deleteSms();
                return false;
            });
        }

        if (mResetLocalContactsDialogShown != null) {
            mResetLocalContactsDialogShown.setOnPreferenceClickListener(preference -> {
                mSharedPreferencesManager.setBoolean(SharedPreferencesManager.IMPORT_LOCAL_CONTACTS_DIALOG_SHOWN, false);
                return false;
            });
        }

        if(mViewModel.isCustomToneEnabled() && mRingtonePreference != null && getActivity() != null && !getActivity().getPackageManager().hasSystemFeature(CHROME_OS_DEVICE_MANAGEMENT_FEATURE)) {
            mRingtonePreference.setOnPreferenceClickListener(preference -> {
                Intent intent = ToneActivity.Companion.newIntent(getActivity(), Enums.Sip.CallTones.ToneTypes.RING);
                getActivity().startActivity(intent);
                return false;
            });
        }
        else {
            mRingtonePreference.setVisible(false);
        }

        if(mViewModel.isCustomToneEnabled() && mNotificationTonePreference != null && getActivity() != null && !getActivity().getPackageManager().hasSystemFeature(CHROME_OS_DEVICE_MANAGEMENT_FEATURE)) {
            mNotificationTonePreference.setOnPreferenceClickListener(preference -> {
                Intent intent = ToneActivity.Companion.newIntent(getActivity(), Enums.Sip.CallTones.ToneTypes.NOTIFICATION);
                getActivity().startActivity(intent);
                return false;
            });
        }
        else {
            mNotificationTonePreference.setVisible(false);
        }

        if (mExpireContactCache != null) {
            mExpireContactCache.setOnPreferenceClickListener(preference -> {
                mDialogManager.showDialog(
                        getActivity(),
                        0,
                        R.string.app_preference_expire_contact_cache_message,
                        R.string.app_preference_expire_contact_cache_positive_action,
                        (dialog, which) -> {
                            mViewModel.expireContactsCache();
                            Toast.makeText(getActivity(), getString(R.string.app_preference_contact_cache_expired), Toast.LENGTH_SHORT).show();
                        },
                        R.string.general_cancel,
                        (dialog, which) -> {
                        });

                return false;
            });
        }

        if (crashApp != null && !requireContext().getPackageName().contains("nextiva_android")) {
            crashApp.setOnPreferenceClickListener(preference -> {
                throw new RuntimeException("You have opted to crash the app!");
            });
        } else if (crashApp != null) {
            crashApp.setVisible(false);
        }

        if (generalLogs != null && !requireContext().getPackageName().contains("nextiva_android")) {
            generalLogs.setOnPreferenceClickListener(preference -> {
                showGenerateLogsDialog();
                return false;
            });
        } else if (generalLogs != null) {
            generalLogs.setVisible(false);
        }

        if (mTroubleshootingCategory != null && mHealthCheck != null && !mViewModel.isLoggedIn()) {
            mTroubleshootingCategory.removePreference(mHealthCheck);
        }
        init();
    }    private final Preference.OnPreferenceChangeListener mDisplaySIPErrorPreferenceChangeListener = displaySIPErrorPreferenceChangeListener();

    private void emailLogsFromFile() {
        if (getActivity() == null) {
            return;
        }

        try {
            String emailSubject = getString(R.string.app_preference_email_logs_subject,
                                            getString(R.string.app_name),
                                            Build.MODEL,
                                            Build.VERSION.RELEASE,
                                            BuildConfig.VERSION_NAME);

            mViewModel.deleteCurrentZips();

            String filename = mViewModel.zipLogs();
            mIntentManager.sendEmail(getActivity(),
                                     APP_PREFERENCES,
                                     getString(R.string.app_preference_email_logs_email_address),
                                     getString(R.string.app_preference_email_logs_name),
                                     emailSubject,
                                     getString(R.string.app_preference_email_logs_body),
                                     new File(filename));

        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            mAnalyticsManager.logEvent(APP_PREFERENCES, NO_LOG_FILE_DIALOG_SHOWN);

            mDialogManager.showDialog(
                    getActivity(),
                    R.string.app_preference_no_log_file_dialog_title,
                    R.string.app_preference_no_log_file_dialog_message,
                    R.string.general_ok,
                    (dialog, which) -> mAnalyticsManager.logEvent(APP_PREFERENCES, NO_LOG_FILE_DIALOG_OK_BUTTON_PRESSED));
        }
    }

    private void clearLogs() {

        if (getActivity() == null || getView() == null) {
            return;
        }

        mAnalyticsManager.logEvent(APP_PREFERENCES, CLEAR_ALL_LOGS_DIALOG_SHOWN);

        mDialogManager.showDialog(
                getActivity(),
                0,
                R.string.app_preference_clear_logs_dialog_message,
                R.string.app_preference_clear_logs_dialog_positive_action,
                (dialog, which) -> {
                    File deleteFile = new File(getActivity().getApplication().getFilesDir().getPath() + AppPreferenceViewModel.LOGS_DIR);

                    mViewModel.deleteDirectory(deleteFile);

                    if (mViewModel.isFileLoggingEnabled()) {
                        getView().postDelayed(
                                () -> mViewModel.setupLogger(),
                                LOGS_DELETE_DIRECTORY_DELAY_MILLIS);
                    }

                    mAnalyticsManager.logEvent(APP_PREFERENCES, CLEAR_ALL_LOGS_DIALOG_CLEAR_LOGS_BUTTON_PRESSED);
                },
                R.string.general_cancel,
                (dialog, which) -> mAnalyticsManager.logEvent(APP_PREFERENCES, CLEAR_ALL_LOGS_DIALOG_CANCEL_BUTTON_PRESSED));

    }

    @Override
    public void onStart() {
        super.onStart();
        setDivider(null);
    }    private final Preference.OnPreferenceChangeListener mEnableLoggingPreferenceChangeListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            mViewModel.enableLogging(newValue.equals(true));

            if (newValue.equals(true)) {
                setCheckboxChecked(mEnableLoggingCheckbox, mEnableLoggingPreferenceChangeListener, true);
                fileLoggingOnPreferenceChange(true);
                xmppLoggingOnPreferenceChange(true);
                sipLoggingPreferenceChange(true);
                setCheckboxChecked(mFileLoggingCheckbox, mFileLoggingPreferenceChangeListener, true);
                mViewModel.enableFileLogging(true);
                setCheckboxChecked(mXMPPLoggingCheckbox, mXmppLoggingPreferenceChangeListener, true);
                mViewModel.enableXmppLogging(true);
                setCheckboxChecked(mSIPLoggingCheckbox, mSipLoggingPreferenceChangeListener, true);
                mViewModel.enableSipLogging(true);

            } else {
                setCheckboxChecked(mEnableLoggingCheckbox, mEnableLoggingPreferenceChangeListener, false);
                fileLoggingOnPreferenceChange(false);
                xmppLoggingOnPreferenceChange(false);
                sipLoggingPreferenceChange(false);
                setCheckboxChecked(mFileLoggingCheckbox, mFileLoggingPreferenceChangeListener, false);
                mViewModel.enableFileLogging(false);
                setCheckboxChecked(mXMPPLoggingCheckbox, mXmppLoggingPreferenceChangeListener, false);
                mViewModel.enableXmppLogging(false);
                setCheckboxChecked(mSIPLoggingCheckbox, mSipLoggingPreferenceChangeListener, false);
                mViewModel.enableSipLogging(false);


                setCheckboxChecked(mDisplayAudioVideoCheckbox, mDisplayAudioVideoPreferenceChangeListener, false);
                setCheckboxChecked(mEnableOldActiveCallLayout, mEnableOldActiveCallLayoutPreferenceChangeListener, false);
                setCheckboxChecked(mDisplaySIPStateCheckbox, mDisplaySIPStatePreferenceChangeListener, false);
                setCheckboxChecked(mDisplaySIPErrorCheckbox, mDisplaySIPErrorPreferenceChangeListener, false);


            }

            setCheckboxesEnabled();


            FirebaseCrashlytics.getInstance().setCustomKey(Enums.Logging.UserDatas.ENABLE_LOGGING, newValue.toString());
            FirebaseCrashlytics.getInstance().setCustomKey(Enums.Logging.UserDatas.FILE_LOGGING, newValue.toString());
            FirebaseCrashlytics.getInstance().setCustomKey(Enums.Logging.UserDatas.SIP_LOGGING, newValue.toString());
            FirebaseCrashlytics.getInstance().setCustomKey(Enums.Logging.UserDatas.XMPP_LOGGING, newValue.toString());

            mAnalyticsManager.logEvent(APP_PREFERENCES, newValue.equals(true) ?
                    ENABLE_LOGGING_SWITCH_CHECKED :
                    ENABLE_LOGGING_SWITCH_UNCHECKED);

            return false;
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        mAnalyticsManager.logScreenView(APP_PREFERENCES);
    }

    private void init() {
        if (getActivity() == null) {
            return;
        }

        int storageGranted = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q && storageGranted != PackageManager.PERMISSION_GRANTED) {
            mViewModel.enableFileLogging(false);
        }

        setCheckboxChecked(mSipEchoCancellationCheckbox, mSipEchoCancellationPreferenceChangeListener, mViewModel.isSipEchoCancellationEnabled());
        setCheckboxChecked(mSmsShowConfirmDialogToDeleteSmsCheckbox, mSmsShowConfirmDialogPreferenceChangeListener, mViewModel.isShowDialogToDeleteSmsEnabled());
        setCheckboxChecked(mSmsEnableSwipeActionsCheckbox, mSmsEnableSwipeActionsChangeListener, mViewModel.isSwipeActionsEnabled());
        setCheckboxChecked(mEnableLoggingCheckbox, mEnableLoggingPreferenceChangeListener, mViewModel.isLoggingEnabled());
        setCheckboxChecked(mFileLoggingCheckbox, mFileLoggingPreferenceChangeListener, mViewModel.isFileLoggingEnabled());
        setCheckboxChecked(mXMPPLoggingCheckbox, mXmppLoggingPreferenceChangeListener, mViewModel.isXmppLoggingEnabled());
        setCheckboxChecked(mSIPLoggingCheckbox, mSipLoggingPreferenceChangeListener, mViewModel.isSipLoggingEnabled());
        setCheckboxChecked(mDisplayAudioVideoCheckbox, mDisplayAudioVideoPreferenceChangeListener, mViewModel.isDisplayAudioVideoStatsEnabled());
        setCheckboxChecked(mEnableOldActiveCallLayout, mEnableOldActiveCallLayoutPreferenceChangeListener, mViewModel.isOldActiveCallLayoutEnabled());
        setCheckboxChecked(mDisplaySIPStateCheckbox, mDisplaySIPStatePreferenceChangeListener, mViewModel.isDisplaySIPStateEnabled());
        setCheckboxChecked(mDisplaySIPErrorCheckbox, mDisplaySIPErrorPreferenceChangeListener, mViewModel.isDisplaySIPErrorEnabled());

        mDarkModeList.setOnPreferenceChangeListener(null);
        mDarkModeList.setEntries(getDarkModeEntries());
        mDarkModeList.setEntryValues(getDarkModeEntryValues());
        mDarkModeList.setValue(getDarkModeValue());
        mDarkModeList.setOnPreferenceChangeListener(mDarkModePreferenceChangeListener);

        disableLogsIfNotLogsEnabled();
    }

    private void setCheckboxChecked(CheckBoxPreferenceWithContentDescription checkbox, Preference.OnPreferenceChangeListener listener, boolean enabled) {
        if (checkbox != null) {
            checkbox.setOnPreferenceChangeListener(null);
            checkbox.setChecked(enabled);
            checkbox.setOnPreferenceChangeListener(listener);
        }
    }    private final Preference.OnPreferenceChangeListener mFileLoggingPreferenceChangeListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            return fileLoggingOnPreferenceChange(newValue.equals(true));
        }
    };

    private void setCheckboxesEnabled() {

        mFileLoggingCheckbox.setEnabled(mEnableLoggingCheckbox.isChecked());
        mXMPPLoggingCheckbox.setEnabled(mEnableLoggingCheckbox.isChecked());
        mSIPLoggingCheckbox.setEnabled(mEnableLoggingCheckbox.isChecked());
    }

    private boolean checkIfAnyLogsEnabled() {
        if (mFileLoggingCheckbox.isChecked()) {
            return true;
        } else if (mXMPPLoggingCheckbox.isChecked()) {
            return true;
        } else {
            return mSIPLoggingCheckbox.isChecked();
        }
    }

    private void disableLogsIfNotLogsEnabled() {
        if (!checkIfAnyLogsEnabled()) {
            mViewModel.enableLogging(false);
//            fileLoggingOnPreferenceChange(false);
//            xmppLoggingOnPreferenceChange(false);
//            sipLoggingPreferenceChange(false);


            setCheckboxChecked(mEnableLoggingCheckbox, mEnableLoggingPreferenceChangeListener, false);
            setCheckboxesEnabled();
        }
    }

    private String[] getDarkModeEntryValues() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return new String[] {getResources().getString(R.string.app_preference_night_mode_light),
                    getResources().getString(R.string.app_preference_night_mode_dark),
                    getResources().getString(R.string.app_preference_night_mode_system_default)};

        } else {
            return new String[] {getResources().getString(R.string.app_preference_night_mode_light),
                    getResources().getString(R.string.app_preference_night_mode_dark),
                    getResources().getString(R.string.app_preference_night_mode_auto)};
        }
    }    private final Preference.OnPreferenceChangeListener mXmppLoggingPreferenceChangeListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            return xmppLoggingOnPreferenceChange(newValue.equals(true));
        }
    };

    private String[] getDarkModeEntries() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return new String[] {getResources().getString(R.string.app_preference_night_mode_light),
                    getResources().getString(R.string.app_preference_night_mode_dark),
                    getResources().getString(R.string.app_preference_night_mode_system_default)};

        } else {
            return new String[] {getResources().getString(R.string.app_preference_night_mode_light),
                    getResources().getString(R.string.app_preference_night_mode_dark),
                    getResources().getString(R.string.app_preference_night_mode_auto)};
        }
    }

    private String getDarkModeValue() {
        switch (mSettingsManager.getNightModeState()) {
            case Enums.Session.NightModeState.NIGHT_MODE_STATE_LIGHT:
                return getResources().getString(R.string.app_preference_night_mode_light);

            case Enums.Session.NightModeState.NIGHT_MODE_STATE_DARK:
                return getResources().getString(R.string.app_preference_night_mode_dark);

            case Enums.Session.NightModeState.NIGHT_MODE_STATE_AUTO:
                return getResources().getString(R.string.app_preference_night_mode_auto);

            case Enums.Session.NightModeState.NIGHT_MODE_STATE_SYSTEM_DEFAULT:
                return getResources().getString(R.string.app_preference_night_mode_system_default);

            default:
                return "";
        }
    }

    private void showGenerateLogsDialog() {
        if (getContext() != null) {
            mDialogManager.showEditTextDialog(getContext(),
                                              R.string.active_call_transfer_to_mobile,
                                              InputType.TYPE_CLASS_NUMBER,
                                              6,
                                              getString(R.string.app_preference_generate_logs_dialog_log_text),
                                              "20",
                                              null,
                                              getString(R.string.app_preference_generate_logs_dialog_positive_button_text),
                                              positiveGenerateLogs(),
                                              getString(R.string.general_cancel),
                                              negativeGenerateLogs(),
                                              R.color.white);
        }
    }

    private MaterialDialog.SingleButtonCallback positiveGenerateLogs() {

        return (dialog, which) -> {
            EditText logsCountEditText = (EditText) dialog.findViewById(R.id.text_input_dialog_edit_text);

            if (!TextUtils.isEmpty(logsCountEditText.getText().toString())) {
                new Thread(() -> {
                    int totalLogs = 0;
                    String logMessage = getResources().getString(R.string.app_preference_generate_logs_dialog_log_logged_text);

                    try {
                        totalLogs = Integer.parseInt(logsCountEditText.getText().toString());
                    } catch (NumberFormatException exception) {
                        if (getContext() != null) {
                            Handler mainHandler = new Handler(getContext().getMainLooper());
                            mainHandler.post(() -> Toast.makeText(getContext().getApplicationContext(), R.string.app_preference_generate_logs_numbers_only_toast, Toast.LENGTH_SHORT).show());
                        }
                    }
                    if (totalLogs > 2000) {
                        if (getContext() != null) {
                            Handler mainHandler = new Handler(getContext().getMainLooper());
                            mainHandler.post(() -> Toast.makeText(getContext().getApplicationContext(), R.string.app_preference_generate_logs_warning_toast, Toast.LENGTH_SHORT).show());
                        }
                    }

                    for (int i = 0; i < totalLogs; i++) {
                        mLogManager.logToFile(logMessage);
                    }
                }).start();
            }
        };
    }

    private MaterialDialog.SingleButtonCallback negativeGenerateLogs() {
        return (dialog, which) -> {
        };
    }

    @VisibleForTesting
    public CheckBoxPreferenceWithContentDescription getEnableLoggingCheckbox() {
        return mEnableLoggingCheckbox;
    }

    @VisibleForTesting
    public CheckBoxPreferenceWithContentDescription getFileLoggingCheckbox() {
        return mFileLoggingCheckbox;
    }    private final Preference.OnPreferenceChangeListener mSipEchoCancellationPreferenceChangeListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            return echoCancellationPreferenceChange(newValue.equals(true));
        }
    };

    private final Preference.OnPreferenceChangeListener mSmsShowConfirmDialogPreferenceChangeListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            return showConfirmationDialogToDeleteSmsPreferenceChange(newValue.equals(true));
        }
    };

    private final Preference.OnPreferenceChangeListener mSmsEnableSwipeActionsChangeListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            return EnableSwipeActionsSmsPreferenceChange(newValue.equals(true));
        }
    };

    private final Preference.OnPreferenceChangeListener mBlockNumberForCallingChangeListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            return EnableBlockNumberForCallingPreferenceChange(newValue.equals(true));
        }
    };

    @VisibleForTesting
    public CheckBoxPreferenceWithContentDescription getSIPLoggingCheckbox() {
        return mSIPLoggingCheckbox;
    }

    @VisibleForTesting
    public CheckBoxPreferenceWithContentDescription getXMPPLoggingCheckbox() {
        return mXMPPLoggingCheckbox;
    }

    @VisibleForTesting
    public CheckBoxPreferenceWithContentDescription getDisplayAudioVideoCheckbox() {
        return mDisplayAudioVideoCheckbox;
    }

    @VisibleForTesting
    public CheckBoxPreferenceWithContentDescription getEnableNewActiveCallLayout() {
        return mEnableOldActiveCallLayout;
    }

    @VisibleForTesting
    public CheckBoxPreferenceWithContentDescription getDisplaySIPStateCheckbox() {
        return mDisplaySIPStateCheckbox;
    }    private final Preference.OnPreferenceChangeListener mSipLoggingPreferenceChangeListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            return sipLoggingPreferenceChange(newValue.equals(true));
        }
    };

    @VisibleForTesting
    public CheckBoxPreferenceWithContentDescription getDisplaySIPErrorCheckbox() {
        return mDisplaySIPErrorCheckbox;
    }

    @VisibleForTesting
    public Preference getEmailLogs() {
        return mEmailLogs;
    }

    @VisibleForTesting
    public Preference getClearLogs() {
        return mClearLogs;
    }

    @Nullable
    @VisibleForTesting
    public Preference getExpireContactCache() {
        return mExpireContactCache;
    }    private final Preference.OnPreferenceChangeListener mDarkModePreferenceChangeListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            mDarkModeList.setOnPreferenceChangeListener(null);

            if (newValue.equals(getResources().getString(R.string.app_preference_night_mode_light))) {
                mSettingsManager.setNightModeState(Enums.Session.NightModeState.NIGHT_MODE_STATE_LIGHT);

            } else if (newValue.equals(getResources().getString(R.string.app_preference_night_mode_dark))) {
                mSettingsManager.setNightModeState(Enums.Session.NightModeState.NIGHT_MODE_STATE_DARK);

            } else if (newValue.equals(getResources().getString(R.string.app_preference_night_mode_auto))) {
                mSettingsManager.setNightModeState(Enums.Session.NightModeState.NIGHT_MODE_STATE_AUTO);

            } else if (newValue.equals(getResources().getString(R.string.app_preference_night_mode_system_default))) {
                mSettingsManager.setNightModeState(Enums.Session.NightModeState.NIGHT_MODE_STATE_SYSTEM_DEFAULT);
            }

            mDarkModeList.setValue((String) newValue);

            if (getActivity() != null) {
                ApplicationUtil.updateNightMode(mSettingsManager, getActivity());

                Intent data = new Intent();
                data.putExtra("EXTRA_APP_COMPAT", AppCompatDelegate.getDefaultNightMode());
                getActivity().setResult(Activity.RESULT_OK);
            }

            mDarkModeList.setOnPreferenceChangeListener(mDarkModePreferenceChangeListener);
            FirebaseCrashlytics.getInstance().setCustomKey(Enums.Logging.UserDatas.NIGHT_MODE, newValue.toString());

            getActivity().recreate();
            return false;
        }
    };
}