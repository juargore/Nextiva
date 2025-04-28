/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.managers;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by adammacdonald on 2/2/18.
 */


@Singleton
public class NextivaSharedPreferencesManager implements SharedPreferencesManager {

    @NonNull
    private final SharedPreferences mSharedPreferences;

    @Inject
    public NextivaSharedPreferencesManager(Application application) {
        mSharedPreferences = application.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    private SharedPreferences.Editor getEditor() {
        return mSharedPreferences.edit();
    }

    @Override
    public String getString(@SettingsKey String key, String defaultValue) {
        return mSharedPreferences.getString(key, defaultValue);
    }

    @Override
    public void setString(@SettingsKey String key, String value) {
        SharedPreferences.Editor editor = getEditor();
        editor.putString(key, value).commit();
    }

    @Override
    public boolean getBoolean(@SettingsKey String key, boolean defaultValue) {
        return mSharedPreferences.getBoolean(key, defaultValue);
    }

    @Override
    public void setBoolean(@SettingsKey String key, boolean value) {
        SharedPreferences.Editor editor = getEditor();
        editor.putBoolean(key, value).commit();
    }

    @Override
    public long getLong(@SettingsKey String key, long defaultValue) {
        return mSharedPreferences.getLong(key, defaultValue);
    }

    @Override
    public void setLong(@SettingsKey String key, long value) {
        SharedPreferences.Editor editor = getEditor();
        editor.putLong(key, value).commit();
    }

    @Override
    public int getInt(@SettingsKey String key, int defaultValue) {
        return mSharedPreferences.getInt(key, defaultValue);
    }

    @Override
    public void setInt(@SettingsKey String key, int value) {
        SharedPreferences.Editor editor = getEditor();
        editor.putInt(key, value).commit();
    }

    @Override
    public float getFloat(@SettingsKey String key, float defaultValue) {
        return mSharedPreferences.getFloat(key, defaultValue);
    }

    @Override
    public void setFloat(@SettingsKey String key, float value) {
        SharedPreferences.Editor editor = getEditor();
        editor.putFloat(key, value).commit();
    }

    @Override
    public void setStringSet(@SettingsKey String key, Set<String> value) {
        SharedPreferences.Editor editor = getEditor();
        editor.putStringSet(key, value).commit();
    }

    @Override
    public Set<String> getStringSet(@SettingsKey String key, Set<String> defaultValue) {
        return mSharedPreferences.getStringSet(key, defaultValue);
    }

    @Override
    public void clear() {
        getEditor().clear().commit();
    }

    @Override
    public void removeKey(@SettingsKey String key) {
        try {
            SharedPreferences.Editor editor = getEditor();
            editor.remove(key).commit();
        } catch(Exception e) {
            // ignore
        }
    }
}
