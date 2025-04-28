package com.nextiva.nextivaapp.android.util;

import android.text.TextUtils;
import android.util.Base64;

import androidx.annotation.NonNull;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.nextiva.nextivaapp.android.models.CurrentUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class JwtUtil {

    public static final String CORP_ACCOUNT_NUM = "com.nextiva.corpAccountNumber";
    public static final String USER_UUID = "com.nextiva.useruuid";
    public static final String USER_FIRST_NAME = "com.nextiva.firstName";
    public static final String USER_LAST_NAME = "com.nextiva.lastName";
    public static final String USER_EMAIL = "com.nextiva.email";
    public static final String PROFILE_ID = "com.nextiva.profileId";
    public static final String DOMAIN = "com.nextiva.domain";
    public static final String LOCATION_ID = "com.nextiva.locationId";
    public static final String USER_STATUS = "com.nextiva.userStatus";
    public static final String DISPLAY_NAME = "com.nextiva.profileDisplayName";
    public static final String LOGIN_ID = "com.nextiva.loginId";

    @NonNull
    public static String getDecodedCorpAccount(@NonNull String jwtEncoded){
        if (TextUtils.isEmpty(jwtEncoded)) {
            return "";
        }

        try {
            String[] split = jwtEncoded.split("\\.");
            if(split.length > 1){
                JSONObject jsonObject = getDecodedJWT(getJson(split[1]));
                if (jsonObject != null) {
                    return jsonObject.getString("com.nextiva.selectedCorpAcctNbr");
                }
            }
        } catch (UnsupportedEncodingException | ArrayIndexOutOfBoundsException | NullPointerException | JSONException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            // Token is incomplete
        }

        return "";
    }

    @NonNull
    public static Boolean isPlatformUser(@NonNull String jwtEncoded)  {
        if (TextUtils.isEmpty(jwtEncoded)) {
            return false;
        }

        try {
            String[] split = jwtEncoded.split("\\.");
            if(split.length > 1){
                JSONObject jsonObject = getDecodedJWT(getJson(split[1]));
                if (jsonObject != null) {
                    String loginId = jsonObject.getString("com.nextiva.loginId");
                    if(loginId.contains("@np3.nextiva.com"))
                    {
                        return false;
                    }
                    else
                    {
                        return true;
                    }
                }
            }
        } catch (UnsupportedEncodingException | ArrayIndexOutOfBoundsException | NullPointerException | JSONException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            // Token is incomplete
        }

        return false;
    }

    public static String getCorpAccountNumber(@NonNull String jwtEncoded)  {
        return getUserValue(jwtEncoded, CORP_ACCOUNT_NUM);
    }

    public static String getUserUUID(@NonNull String jwtEncoded) {
        return getUserValue(jwtEncoded, USER_UUID);
    }

    public static String getUserProfileID(@NonNull String jwtEncoded) {
        return getUserValue(jwtEncoded, PROFILE_ID);
    }

    public static String getUserDomain(@NonNull String jwtEncoded) {
        return getUserValue(jwtEncoded, DOMAIN);
    }

    public static String getUserLocationID(@NonNull String jwtEncoded) {
        return getUserValue(jwtEncoded, LOCATION_ID);
    }

    public static CurrentUser getCurrentUser(@NonNull String jwtEncoded) {
        return new CurrentUser(getUserValue(jwtEncoded, USER_UUID),
                getUserValue(jwtEncoded, CORP_ACCOUNT_NUM),
                getUserValue(jwtEncoded, USER_STATUS),
                getUserValue(jwtEncoded, DISPLAY_NAME),
                getUserValue(jwtEncoded, USER_EMAIL),
                getUserValue(jwtEncoded, PROFILE_ID),
                getUserValue(jwtEncoded, USER_LAST_NAME),
                getUserValue(jwtEncoded, DOMAIN),
                getUserValue(jwtEncoded, LOCATION_ID),
                getUserValue(jwtEncoded, LOGIN_ID),
                getUserValue(jwtEncoded, USER_FIRST_NAME));
    }

    public static String getUserValue(@NonNull String jwtEncoded, @NonNull String key) {
        if (TextUtils.isEmpty(jwtEncoded)) {
            return "";
        }

        try {
            String[] split = jwtEncoded.split("\\.");
            if(split.length > 1){
                JSONObject jsonObject = getDecodedJWT(getJson(split[1]));
                if (jsonObject != null) {
                    return jsonObject.getString(key);
                }
            }
        } catch (UnsupportedEncodingException | ArrayIndexOutOfBoundsException | NullPointerException | JSONException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            // Token is incomplete
        }

        return "";
    }

    private static JSONObject getDecodedJWT(String jwtEncoded) {
        if (TextUtils.isEmpty(jwtEncoded)) {
            return null;
        }

        try {
            return new JSONObject(jwtEncoded);

        } catch (ArrayIndexOutOfBoundsException | NullPointerException | JSONException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            // Token is incomplete
        }
        return null;
    }

    private static String getJson(String strEncoded) throws UnsupportedEncodingException{
        byte[] decodedBytes = Base64.decode(strEncoded, Base64.URL_SAFE);
        return new String(decodedBytes, "UTF-8");
    }
}