package com.nextiva.nextivaapp.android.fragments;

import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.NO_LOG_FILE_DIALOG_OK_BUTTON_PRESSED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.NO_LOG_FILE_DIALOG_SHOWN;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.ScreenName.APP_PREFERENCES;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceClickListener;
import androidx.preference.PreferenceFragmentCompat;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.nextiva.nextivaapp.android.AboutActivity;
import com.nextiva.nextivaapp.android.AppPreferencesActivity;
import com.nextiva.nextivaapp.android.BuildConfig;
import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.SetCallSettingsActivity;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.constants.RequestCodes;
import com.nextiva.nextivaapp.android.managers.interfaces.AnalyticsManager;
import com.nextiva.nextivaapp.android.managers.interfaces.DialogManager;
import com.nextiva.nextivaapp.android.managers.interfaces.IntentManager;
import com.nextiva.nextivaapp.android.managers.interfaces.NetManager;
import com.nextiva.nextivaapp.android.managers.interfaces.PermissionManager;
import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager;
import com.nextiva.nextivaapp.android.viewmodels.LoginPreferencesViewModel;

import java.io.File;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LoginPreferencesFragment extends PreferenceFragmentCompat {

    @Inject
    protected AnalyticsManager mAnalyticsManager;
    @Inject
    protected DialogManager mDialogManager;
    @Inject
    protected SharedPreferencesManager mSharedPreferencesManager;
    @Inject
    protected NetManager mNetManager;
    @Inject
    protected PermissionManager mPermissionManager;
    @Inject
    protected IntentManager mIntentManager;

    private LoginPreferencesViewModel mViewModel;

    private final OnPreferenceClickListener mTroubleshootingOnPreferenceClickListener = new OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            if (getActivity() != null) {
                Intent intent = AppPreferencesActivity.newIntent(getActivity());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getActivity().startActivity(intent);
            }

            mAnalyticsManager.logEvent(Enums.Analytics.ScreenName.LOGIN_PREFERENCES, Enums.Analytics.EventName.TROUBLESHOOTING_LIST_ITEM_PRESSED);
            return true;
        }
    };
    private final OnPreferenceClickListener mThisPhoneNumberOnPreferenceClickListener = new OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            if (getActivity() != null) {
                Intent intent = SetCallSettingsActivity.newIntent(getActivity(), SharedPreferencesManager.THIS_PHONE_NUMBER);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getActivity().startActivity(intent);
            }

            mAnalyticsManager.logEvent(Enums.Analytics.ScreenName.LOGIN_PREFERENCES, Enums.Analytics.EventName.THIS_PHONE_NUMBER_LIST_ITEM_PRESSED);
            return true;
        }
    };
    private final OnPreferenceClickListener mAboutOnPreferenceClickListener = new OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            if (getActivity() != null) {
                Intent intent = AboutActivity.newIntent(getActivity());
                getActivity().startActivityForResult(intent, RequestCodes.DEVELOPER_MODE_ENABLED_REQUEST_CODE);
            }

            mAnalyticsManager.logEvent(Enums.Analytics.ScreenName.LOGIN_PREFERENCES, Enums.Analytics.EventName.ABOUT_LIST_ITEM_PRESSED);
            return true;
        }
    };
    private final OnPreferenceClickListener mHelpOnPreferenceClickListener = new OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            if (getActivity() != null) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(getActivity().getString(R.string.help_url)));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getActivity().startActivity(intent);
            }

            mAnalyticsManager.logEvent(Enums.Analytics.ScreenName.LOGIN_PREFERENCES, Enums.Analytics.EventName.HELP_LIST_ITEM_PRESSED);
            return true;
        }
    };
    private final OnPreferenceClickListener mDomainOnPreferenceClickListener = new OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            if (getActivity() == null) {
                return false;
            }

            mDialogManager.showEndpointEditTextDialog(getActivity(),
                                                      R.string.login_preference_endpoints,
                                                      getActivity().getString(R.string.endpoints_hostname),
                                                      mSharedPreferencesManager.getString(SharedPreferencesManager.HOSTNAME, BuildConfig.HOSTNAME),
                                                      getActivity().getString(R.string.endpoints_api_url),
                                                      mSharedPreferencesManager.getString(SharedPreferencesManager.BROADSOFT_API_URL, BuildConfig.BROADSOFT_API_URL),
                                                      //getActivity().getString(R.string.endpoints_okta_endpoint_uri),
                                                      //mSharedPreferencesManager.getString(SharedPreferencesManager.OKTA_ENDPOINT_URI, BuildConfig.OKTA_ENDPOINT_URI),
                                                      //getActivity().getString(R.string.endpoints_okta_client_id),
                                                      //mSharedPreferencesManager.getString(SharedPreferencesManager.OKTA_CLIENT_ID, BuildConfig.OKTA_CLIENT_ID),
                                                      getActivity().getString(R.string.endpoints_platform_api_url),
                                                      mSharedPreferencesManager.getString(SharedPreferencesManager.PLATFORM_ACCESS_API_URL, BuildConfig.PLATFORM_ACCESS_API_URL),
                                                      getActivity().getString(R.string.endpoints_messages_api_url),
                                                      mSharedPreferencesManager.getString(SharedPreferencesManager.PLATFORM_API_URL, BuildConfig.PLATFORM_API_URL),
                                                      getActivity().getString(R.string.endpoints_auth_proxy_public_host),
                                                      mSharedPreferencesManager.getString(SharedPreferencesManager.AUTH_PROXY_PUBLIC_HOST, BuildConfig.AUTH_PROXY_PUBLIC_HOST),
                                                      getActivity().getString(R.string.endpoints_auth_proxy_rest_api),
                                                      mSharedPreferencesManager.getString(SharedPreferencesManager.AUTH_PROXY_REST_API, BuildConfig.AUTH_PROXY_REST_API),
                                                      getActivity().getString(R.string.endpoints_auth_tenant_rest_api),
                                                      mSharedPreferencesManager.getString(SharedPreferencesManager.AUTH_TENANT_REST_API, BuildConfig.AUTH_TENANT_REST_API),
                                                      getActivity().getString(R.string.endpoints_general_rest_api),
                                                      mSharedPreferencesManager.getString(SharedPreferencesManager.GENERAL_REST_API, BuildConfig.GENERAL_REST_API),

                                                      (dialog, which) -> {
                                                          EditText hostnameEditText = (EditText) dialog.findViewById(R.id.hostname_text_input_dialog_edit_text);
                                                          EditText broadsoftApiUrlEditText = (EditText) dialog.findViewById(R.id.broadsoft_api_url_text_input_dialog_edit_text);
                                                          EditText platformApiUrlEditText = (EditText) dialog.findViewById(R.id.platform_api_url_text_input_dialog_edit_text);
                                                          EditText messageApiUrlEditText = (EditText) dialog.findViewById(R.id.message_api_url_text_input_dialog_edit_text);

                                                          EditText authProxyPublicHostEditText = (EditText) dialog.findViewById(R.id.auth_proxy_public_host_text_input_dialog_edit_text);
                                                          EditText authProxyRestApiEditText = (EditText) dialog.findViewById(R.id.auth_proxy_rest_api_text_input_dialog_edit_text);
                                                          EditText authTenantRestApiEditText = (EditText) dialog.findViewById(R.id.auth_tenant_rest_api_text_input_dialog_edit_text);
                                                          EditText generalRestApiEditText = (EditText) dialog.findViewById(R.id.general_rest_api_text_input_dialog_edit_text);


                                                          if (!TextUtils.isEmpty(hostnameEditText.getText().toString())) {
                                                              mSharedPreferencesManager.setString(SharedPreferencesManager.HOSTNAME, hostnameEditText.getText().toString());
                                                          } else {
                                                              mSharedPreferencesManager.setString(SharedPreferencesManager.HOSTNAME, null);
                                                          }

                                                          if (!TextUtils.isEmpty(broadsoftApiUrlEditText.getText().toString())) {
                                                              mSharedPreferencesManager.setString(SharedPreferencesManager.BROADSOFT_API_URL, broadsoftApiUrlEditText.getText().toString());
                                                          } else {
                                                              mSharedPreferencesManager.setString(SharedPreferencesManager.BROADSOFT_API_URL, null);
                                                          }

                                                          if (!TextUtils.isEmpty(platformApiUrlEditText.getText().toString())) {
                                                              mSharedPreferencesManager.setString(SharedPreferencesManager.PLATFORM_ACCESS_API_URL, platformApiUrlEditText.getText().toString());
                                                          } else {
                                                              mSharedPreferencesManager.setString(SharedPreferencesManager.PLATFORM_ACCESS_API_URL, null);
                                                          }
                                                          if (!TextUtils.isEmpty(messageApiUrlEditText.getText().toString())) {
                                                              mSharedPreferencesManager.setString(SharedPreferencesManager.PLATFORM_API_URL, messageApiUrlEditText.getText().toString());
                                                          } else {
                                                              mSharedPreferencesManager.setString(SharedPreferencesManager.PLATFORM_API_URL, null);

                                                          }
                                                          if (!TextUtils.isEmpty(authProxyPublicHostEditText.getText().toString())) {
                                                              mSharedPreferencesManager.setString(SharedPreferencesManager.AUTH_PROXY_PUBLIC_HOST, authProxyPublicHostEditText.getText().toString());
                                                          } else {
                                                              mSharedPreferencesManager.setString(SharedPreferencesManager.AUTH_PROXY_PUBLIC_HOST, null);
                                                          }

                                                          if (!TextUtils.isEmpty(authProxyRestApiEditText.getText().toString())) {
                                                              mSharedPreferencesManager.setString(SharedPreferencesManager.AUTH_PROXY_REST_API, authProxyRestApiEditText.getText().toString());
                                                          } else {
                                                              mSharedPreferencesManager.setString(SharedPreferencesManager.AUTH_PROXY_REST_API, null);
                                                          }

                                                          if (!TextUtils.isEmpty(authTenantRestApiEditText.getText().toString())) {
                                                              mSharedPreferencesManager.setString(SharedPreferencesManager.AUTH_TENANT_REST_API, authTenantRestApiEditText.getText().toString());
                                                          } else {
                                                              mSharedPreferencesManager.setString(SharedPreferencesManager.AUTH_TENANT_REST_API, null);
                                                          }

                                                          if (!TextUtils.isEmpty(generalRestApiEditText.getText().toString())) {
                                                              mSharedPreferencesManager.setString(SharedPreferencesManager.GENERAL_REST_API, generalRestApiEditText.getText().toString());
                                                          } else {
                                                              mSharedPreferencesManager.setString(SharedPreferencesManager.GENERAL_REST_API, null);
                                                          }

                                                          mNetManager.clearPlatformApiManager();
                                                          mNetManager.clearPlatformApi();

                                                          Toast.makeText(getActivity(), getActivity().getString(R.string.endpoints_changes_toast_text), Toast.LENGTH_SHORT).show();
                                                      },
                                                      (dialog, which) -> {
                                                          mSharedPreferencesManager.setString(SharedPreferencesManager.HOSTNAME, null);
                                                          mSharedPreferencesManager.setString(SharedPreferencesManager.BROADSOFT_API_URL, null);
                                                          mSharedPreferencesManager.setString(SharedPreferencesManager.OKTA_ENDPOINT_URI, null);
                                                          mSharedPreferencesManager.setString(SharedPreferencesManager.OKTA_CLIENT_ID, null);
                                                          mSharedPreferencesManager.setString(SharedPreferencesManager.PLATFORM_ACCESS_API_URL, null);
                                                          mSharedPreferencesManager.setString(SharedPreferencesManager.PLATFORM_API_URL, null);
                                                          mSharedPreferencesManager.setString(SharedPreferencesManager.AUTH_PROXY_PUBLIC_HOST, null);
                                                          mSharedPreferencesManager.setString(SharedPreferencesManager.AUTH_PROXY_REST_API, null);
                                                          mSharedPreferencesManager.setString(SharedPreferencesManager.AUTH_TENANT_REST_API, null);
                                                          mSharedPreferencesManager.setString(SharedPreferencesManager.GENERAL_REST_API, null);


                                                          mNetManager.clearPlatformApiManager();
                                                          mNetManager.clearPlatformApi();

                        Toast.makeText(getActivity(), getActivity().getString(R.string.endpoints_changes_toast_text), Toast.LENGTH_SHORT).show();
                    },
                    (dialog, which) -> {
                    },
                    R.color.nextivaOrange);
            return true;
        }
    };
    private final OnPreferenceClickListener mCredentialsOnPreferenceClickListener = new OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            if (getActivity() == null) {
                return false;
            }

            mDialogManager.showCredentialsEditTextDialog(getActivity(),
                    R.string.login_preference_endpoints,
                    "",
                    mSharedPreferencesManager.getString(SharedPreferencesManager.BROADWORKS_USERNAME, ""),
                    "",
                    mSharedPreferencesManager.getString(SharedPreferencesManager.BROADWORKS_PASSWORD, ""),
                    (dialog, which) -> {
                        EditText usernameEditText = (EditText) dialog.findViewById(R.id.username_text_input_dialog_edit_text);
                        EditText passwordEditText = (EditText) dialog.findViewById(R.id.password_text_input_dialog_edit_text);

                        if (!TextUtils.isEmpty(usernameEditText.getText().toString())) {
                            mSharedPreferencesManager.setString(SharedPreferencesManager.BROADWORKS_USERNAME, usernameEditText.getText().toString());
                        } else {
                            mSharedPreferencesManager.setString(SharedPreferencesManager.BROADWORKS_USERNAME, null);
                        }

                        if (!TextUtils.isEmpty(passwordEditText.getText().toString())) {
                            mSharedPreferencesManager.setString(SharedPreferencesManager.BROADWORKS_PASSWORD, passwordEditText.getText().toString());
                        } else {
                            mSharedPreferencesManager.setString(SharedPreferencesManager.BROADWORKS_PASSWORD, null);
                        }
                    },
                    (dialog, which) -> {
                        mSharedPreferencesManager.setString(SharedPreferencesManager.BROADWORKS_USERNAME, null);
                        mSharedPreferencesManager.setString(SharedPreferencesManager.BROADWORKS_PASSWORD, null);

                        Toast.makeText(getActivity(), getActivity().getString(R.string.endpoints_changes_toast_text), Toast.LENGTH_SHORT).show();
                    },
                    (dialog, which) -> {
                    },
                    R.color.nextivaOrange);
            return true;
        }
    };

    public static LoginPreferencesFragment newInstance() {
        Bundle args = new Bundle();

        LoginPreferencesFragment fragment = new LoginPreferencesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences_login);

        mViewModel = new ViewModelProvider(this).get(LoginPreferencesViewModel.class);

        Preference troubleshootingPreference = getPreferenceScreen().findPreference(getString(R.string.login_preferences_key_troubleshooting));
        Preference thisPhoneNumberPreference = getPreferenceScreen().findPreference(getString(R.string.login_preferences_key_this_phone_number));
        Preference aboutPreference = getPreferenceScreen().findPreference(getString(R.string.login_preferences_key_about));
        Preference helpPreference = getPreferenceScreen().findPreference(getString(R.string.login_preferences_key_help));
        Preference emailLogsPreference = getPreferenceScreen().findPreference(getString(R.string.app_preferences_key_email_logs));

        if ((!TextUtils.equals(getString(R.string.app_environment), getString(R.string.environment_prod)) &&
                !TextUtils.equals(getString(R.string.app_environment), getString(R.string.environment_prodBeta))) ||
                mSharedPreferencesManager.getBoolean(SharedPreferencesManager.ENDPOINTS_AND_CREDENTIALS_ENABLED, false)) {
            addPreferencesFromResource(R.xml.preferences_endpoints);
            addPreferencesFromResource(R.xml.preferences_credentials);

            Preference domainPreference = getPreferenceScreen().findPreference(getString(R.string.login_preferences_key_endpoints));
            domainPreference.setOnPreferenceClickListener(mDomainOnPreferenceClickListener);
            Preference credentialsPreference = getPreferenceScreen().findPreference(getString(R.string.login_preferences_key_credentials));
            credentialsPreference.setOnPreferenceClickListener(mCredentialsOnPreferenceClickListener);
        }

        troubleshootingPreference.setOnPreferenceClickListener(mTroubleshootingOnPreferenceClickListener);
        thisPhoneNumberPreference.setOnPreferenceClickListener(mThisPhoneNumberOnPreferenceClickListener);
        aboutPreference.setOnPreferenceClickListener(mAboutOnPreferenceClickListener);
        helpPreference.setOnPreferenceClickListener(mHelpOnPreferenceClickListener);
        emailLogsPreference.setOnPreferenceClickListener(preference -> {
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

    @Override
    public void onStart() {
        super.onStart();
        setDivider(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        mAnalyticsManager.logScreenView(Enums.Analytics.ScreenName.LOGIN_PREFERENCES);
    }

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
}
