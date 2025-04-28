package com.nextiva.nextivaapp.android.mocks.managers;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.nextiva.nextivaapp.android.managers.interfaces.NextivaMediaPlayer;

import java.io.File;
import java.util.ArrayList;

import javax.inject.Inject;

public class FakeMediaPlayerManager implements NextivaMediaPlayer {

    private boolean speakerPhoneEnabled = false;
    private int currentPlayingProgress = 0;
    private String currentActiveAudioFileMessageId = "";

    @Inject
    public FakeMediaPlayerManager() {
    }

    @Override
    public void createAudioFile(@NonNull Context context, @Nullable String content, @Nullable String fileType, @NonNull String messageUuid) {
    }

    @Nullable
    @Override
    public File getAudioFileFromCache(@NonNull Context context, @NonNull String messageUuid) {
        return new File(messageUuid);
    }

    @Override
    public void deleteAudioFile(@NonNull Context context, @NonNull String messageId) {
    }

    @Override
    public void deleteAudioFiles(@NonNull Context context,  @NonNull ArrayList<String> messageIds){
    }

    @Override
    public void finishPlayingAudioFile() {
    }

    @Override
    public void playVoicemailFromFile(@NonNull Context context, @NonNull File voicemailFile) {
    }

    @Override
    public void playVoicemail(@NonNull Context context, @NonNull String messageDetailsPath, boolean isRead) {
    }

    @Override
    public void playAudioFile(@NonNull Context context, @NonNull File audioFile) {
    }

    @Override
    public void pausePlaying() {
    }

    @Override
    public void setProgress(int progress, boolean fromUser) {
        currentPlayingProgress = progress;
    }

    @Override
    public int getCurrentPlayingProgress() {
        return currentPlayingProgress;
    }

    @Nullable
    @Override
    public String getCurrentActiveAudioFileMessageId() {
        return currentActiveAudioFileMessageId;
    }

    @Override
    public void setCurrentActiveAudioFileMessageId(@NonNull String messageId) {
        currentActiveAudioFileMessageId = messageId;
    }

    @Override
    public boolean isPlaying() {
        return false;
    }

    @Override
    public boolean isSpeakerPhoneEnabled() {
        return speakerPhoneEnabled;
    }

    @Override
    public void toggleSpeakerPhone(@NonNull Context context) {
        speakerPhoneEnabled = !speakerPhoneEnabled;
    }

    @NonNull
    @Override
    public LiveData<String> getActiveVoicemailMessageIdChangedLiveData() {
        return null;
    }

    @NonNull
    @Override
    public LiveData<Integer> getCurrentPlayingProgressChangedLiveData() {
        return new MutableLiveData<>();
    }

    @NonNull
    @Override
    public LiveData<String> getCurrentPlayingVoicemailPausedLiveData() {
        return null;
    }

    @NonNull
    @Override
    public LiveData<Integer> getFetchingVoicemailDetailsStartedLiveData() {
        return null;
    }

    @NonNull
    @Override
    public LiveData<Boolean> getFetchingVoicemailDetailsFinishedLiveData() {
        return null;
    }

    @NonNull
    @Override
    public LiveData<Void> getFetchingVoicemailFailedNoInternetLiveData() {
        return null;
    }

    @Override
    public void playSmsAudioFileFromURI(@NonNull Context context, @NonNull Uri uri) {
    }

    @Nullable
    @Override
    public String getCurrentActiveSpeakerMessageId() {
        return null;
    }

    @Override
    public void setCurrentActiveSpeakerMessageId(@NonNull String messageId) {
    }

    @Override
    public boolean getIsPlayerCurrentlyPaused() {
        return false;
    }

    @NonNull
    @Override
    public LiveData<Boolean> getViewHolderPlayButtonClickedLiveData() {
        return null;
    }

    @Override
    public void setViewHolderPlayButtonClickedLiveData(boolean isPlayButtonClicked) {
    }

    @Override
    public void reset() {
    }

    @Override
    public void playVoicemail(@NonNull Context context, @NonNull String messageDetailsPath, @NonNull String callId, boolean isRead) {

    }

    @Nullable
    @Override
    public File getAudioFileFromCacheByName(@NonNull Context context, @NonNull String audioName) {
        return new File(audioName);
    }

    @Override
    public boolean isInternetAvailableToPlayMedia() {
        return false;
    }
}
