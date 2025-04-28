/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.managers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

import android.app.Application;
import android.content.SharedPreferences;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by adammacdonald on 3/23/18.
 */

public class NextivaSharedPreferencesManagerTest {

    @Mock
    private Application mMockApplication;
    @Mock
    private SharedPreferences mMockSharedPreferences;
    @Mock
    private SharedPreferences.Editor mMockEditor;

    private NextivaSharedPreferencesManager mSharedPreferencesManager;

    @Before
    public void setup() {
        mMockEditor = PowerMockito.mock(SharedPreferences.Editor.class);
        PowerMockito.when(mMockEditor.putString(anyString(), anyString())).thenReturn(mMockEditor);
        PowerMockito.when(mMockEditor.putBoolean(anyString(), anyBoolean())).thenReturn(mMockEditor);
        PowerMockito.when(mMockEditor.putFloat(anyString(), anyFloat())).thenReturn(mMockEditor);
        PowerMockito.when(mMockEditor.putInt(anyString(), anyInt())).thenReturn(mMockEditor);
        PowerMockito.when(mMockEditor.putLong(anyString(), anyLong())).thenReturn(mMockEditor);
        PowerMockito.when(mMockEditor.putStringSet(anyString(), any(Set.class))).thenReturn(mMockEditor);
        PowerMockito.when(mMockEditor.clear()).thenReturn(mMockEditor);

        mMockSharedPreferences = PowerMockito.mock(SharedPreferences.class);
        PowerMockito.when(mMockSharedPreferences.edit()).thenReturn(mMockEditor);

        mMockApplication = PowerMockito.mock(Application.class);
        PowerMockito.when(mMockApplication.getSharedPreferences(anyString(), anyInt())).thenReturn(mMockSharedPreferences);

        mSharedPreferencesManager = new NextivaSharedPreferencesManager(mMockApplication);
    }

    @Test
    public void getString_pullsFromSharedPrefs() {
        mSharedPreferencesManager.getString("key", "defValue");

        verify(mMockSharedPreferences).getString("key", "defValue");
    }

    @Test
    public void setString_setsToSharedPrefs() {
        mSharedPreferencesManager.setString("key", "value");

        verify(mMockEditor).putString("key", "value");
        verify(mMockEditor).commit();
    }

    @Test
    public void getBoolean_pullsFromSharedPrefs() {
        mSharedPreferencesManager.getBoolean("key", true);

        verify(mMockSharedPreferences).getBoolean("key", true);
    }

    @Test
    public void setBoolean_setsToSharedPrefs() {
        mSharedPreferencesManager.setBoolean("key", true);

        verify(mMockEditor).putBoolean("key", true);
        verify(mMockEditor).commit();
    }

    @Test
    public void getLong_pullsFromSharedPrefs() {
        mSharedPreferencesManager.getLong("key", 25L);

        verify(mMockSharedPreferences).getLong("key", 25L);
    }

    @Test
    public void setLong_setsToSharedPrefs() {
        mSharedPreferencesManager.setLong("key", 30L);

        verify(mMockEditor).putLong("key", 30L);
        verify(mMockEditor).commit();
    }

    @Test
    public void getInt_pullsFromSharedPrefs() {
        mSharedPreferencesManager.getInt("key", 100);

        verify(mMockSharedPreferences).getInt("key", 100);
    }

    @Test
    public void setInt_setsToSharedPrefs() {
        mSharedPreferencesManager.setInt("key", 110);

        verify(mMockEditor).putInt("key", 110);
        verify(mMockEditor).commit();
    }

    @Test
    public void getFloat_pullsFromSharedPrefs() {
        mSharedPreferencesManager.getFloat("key", 1.0f);

        verify(mMockSharedPreferences).getFloat("key", 1.0f);
    }

    @Test
    public void setFloat_setsToSharedPrefs() {
        mSharedPreferencesManager.setFloat("key", 2.0f);

        verify(mMockEditor).putFloat("key", 2.0f);
        verify(mMockEditor).commit();
    }

    @Test
    public void getStringSet_pullsFromSharedPrefs() {
        Set<String> stringSet = new HashSet<>();

        mSharedPreferencesManager.getStringSet("key", stringSet);

        verify(mMockSharedPreferences).getStringSet("key", stringSet);
    }

    @Test
    public void setStringSet_setsToSharedPrefs() {
        Set<String> stringSet = new HashSet<>();

        mSharedPreferencesManager.setStringSet("key", stringSet);

        verify(mMockEditor).putStringSet("key", stringSet);
        verify(mMockEditor).commit();
    }

    @Test
    public void clear_clearsSharedPrefs() {
        mSharedPreferencesManager.clear();

        verify(mMockEditor).clear();
        verify(mMockEditor).commit();
    }
}
