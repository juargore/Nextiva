package com.nextiva.nextivaapp.android.managers;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.managers.interfaces.AppUpdateManager;
import com.nextiva.nextivaapp.android.managers.interfaces.LogManager;
import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager;

import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class NextivaAppUpdateManager implements AppUpdateManager {

//    private static final long VERSION_X_Y_Z = 100;

    private final LogManager mLogManager;
    private final SharedPreferencesManager mSharedPreferencesManager;

    @Inject
    public NextivaAppUpdateManager(
            @NonNull LogManager logManager,
            @NonNull SharedPreferencesManager sharedPreferencesManager) {

        mLogManager = logManager;
        mSharedPreferencesManager = sharedPreferencesManager;
    }

    /**
     * Convert a SemVer VersionName into a Long.
     * <br><br><p>
     * Ex. 5.12.563 would be converted to 5012563
     * <br>
     * Ex. 10.822.5 would be converted to 10822005
     * </p>
     *
     * @param versionName The SemVer VersionName
     * @return The Long version of the SemVer VersionName or 0 if there was an error
     */
    private long getSemVerLong(@Nullable String versionName) {
        if (TextUtils.isEmpty(versionName)) {
            return 0;
        }

        String[] parts = versionName.split("\\.");

        if (parts.length == 3) {
            try {
                String major = String.format(Locale.getDefault(), "%03d", Integer.valueOf(parts[0]));
                String minor = String.format(Locale.getDefault(), "%03d", Integer.valueOf(parts[1]));
                String patch = String.format(Locale.getDefault(), "%03d", Integer.valueOf(parts[2]));

                return Long.valueOf(major + minor + patch);

            } catch (NumberFormatException e) {
                return 0;
            }
        }

        return 0;
    }

    // --------------------------------------------------------------------------------------------
    // AppUpdateManager Methods
    // --------------------------------------------------------------------------------------------
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public void processUpdates(@NonNull String versionName) {
        mLogManager.logToFile(Enums.Logging.STATE_INFO, R.string.log_message_start);

        long lastSemVerLong = getSemVerLong(mSharedPreferencesManager.getString(SharedPreferencesManager.LAST_APP_VERSION, null));
        long currentSemVerLong = getSemVerLong(versionName);

        if (currentSemVerLong > lastSemVerLong) {
//        if (currentSemVerLong > VERSION_X_Y_Z) {
//            //Run some logic for X.Y.Z migration
//                    mLogManager.logToFile(Enums.Logging.STATE_INFO, "Success Update Version X.Y.Z");
//        }
        }

        mSharedPreferencesManager.setString(SharedPreferencesManager.LAST_APP_VERSION, versionName);
    }
    // --------------------------------------------------------------------------------------------
}
