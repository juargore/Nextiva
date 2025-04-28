package com.nextiva.nextivaapp.android.managers

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.media.AudioAttributes
import android.media.Ringtone
import android.media.RingtoneManager
import android.media.ToneGenerator
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.constants.Constants.ChromeOS
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.constants.Enums.Sip.CallTones.CallTone
import com.nextiva.nextivaapp.android.managers.interfaces.AudioManager
import com.nextiva.nextivaapp.android.managers.interfaces.LogManager
import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager
import com.nextiva.nextivaapp.android.util.CallUtil
import com.nextiva.nextivaapp.android.util.GsonUtil
import com.nextiva.nextivaapp.android.util.LogUtil
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Thaddeus Dannar on 10/17/18.
 */
@Singleton
class NextivaAudioManager @Inject constructor(
    application: Application,
    val logManager: LogManager,
    private val sharedPreferenceManager: SharedPreferencesManager): AudioManager {

    private var context: Context = application.applicationContext ?: application.baseContext
    private var headsetTonePlayer: ToneGenerator? = ToneGenerator(android.media.AudioManager.STREAM_VOICE_CALL, TONE_RELATIVE_VOLUME)
    private var audioManager: android.media.AudioManager? = null
    private var savedMode = android.media.AudioManager.MODE_NORMAL
    private var ringtonePlayer: Ringtone? = null
    private var ringRef = 0
    private var vibrator: Vibrator? = null
    private var headsetToneActive = false

    init {
        try {
            audioManager = context.getSystemService(Context.AUDIO_SERVICE) as android.media.AudioManager

            } catch (x: Exception) {
                FirebaseCrashlytics.getInstance().recordException(x)
            }

        try {
            attemptToSetRingtone()

        } catch (x: Exception) {
            logManager.sipLogToFile(Enums.Logging.STATE_ERROR, x.javaClass.simpleName)
            LogUtil.d(x.toString())
            FirebaseCrashlytics.getInstance().recordException(x)
        }
    }

    private fun attemptToSetRingtone() {
        if (context.packageManager.hasSystemFeature(ChromeOS.CHROME_OS_DEVICE_MANAGEMENT_FEATURE)) {
            val defaultRingtoneUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.packageName + "/" + R.raw.legacyringtone)
            ringtonePlayer = RingtoneManager.getRingtone(context, defaultRingtoneUri)

        } else {
            val ringtoneUriString = sharedPreferenceManager.getString(SharedPreferencesManager.RINGTONE_URI, "")
            var ringtoneUri = if (ringtoneUriString.isNotEmpty()) {
                Uri.parse(ringtoneUriString)
            } else {
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            }

            if (ringtoneUri.scheme == null) {
                ringtoneUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.packageName + "/" + R.raw.notification)
            }

            ringtonePlayer = RingtoneManager.getRingtone(context, ringtoneUri)
        }

        ringtonePlayer?.setAudioAttributes(
            AudioAttributes.Builder()
                .setLegacyStreamType(android.media.AudioManager.STREAM_RING)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                .build()
        )
    }

    override fun stop() {
        stopHeadsetTone()
        stopRingTone()
    }

    private fun vibratePhone() {
        if (CallUtil.isUserVibrateOn(context)) {
            val pattern = NextivaNotificationManager.VIBRATION_PATTERN_SIMPLE
            vibrator?.vibrate(VibrationEffect.createWaveform(pattern, 0)) // 0 to repeat endlessly.
        }
    }

    private fun vibratePhoneOnCall() {
        if (CallUtil.isUserVibrateOn(context)) {
            val pattern = longArrayOf(0, 500, 9500) //0 to start now, 500 to vibrate 500 ms, 9500 to sleep for 9500 ms.
            vibrator?.vibrate(VibrationEffect.createWaveform(pattern, 1)) // 0 to repeat endlessly.
        }
    }

    override fun startRingTone(isOnCall: Boolean) {
        try {
            attemptToSetRingtone()

        } catch (x: Exception) {
            logManager.sipLogToFile(Enums.Logging.STATE_ERROR, x.javaClass.simpleName)
            LogUtil.d(x.toString())
            FirebaseCrashlytics.getInstance().recordException(x)
        }

        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator

        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (isOnCall) {
            if (!headsetToneActive) {
                startHeadsetTone(Enums.Sip.CallTones.CALL_WAITING)
                vibratePhoneOnCall()
            }
        } else {
            if (ringtonePlayer != null && ringtonePlayer?.isPlaying == true) {
                ringRef++
                return
            }

            savedMode = audioManager?.mode ?: android.media.AudioManager.MODE_NORMAL
            audioManager?.mode = android.media.AudioManager.MODE_RINGTONE

            ringtonePlayer?.let { ringtonePlayer ->
                synchronized(ringtonePlayer) {
                    try {
                        ringRef++

                        logManager.logToFile(Enums.Logging.STATE_INFO, "NextivaAudioManager startRingTone play")

                        if (!ringtonePlayer.isPlaying && !CallUtil.isRingerSilent(context)) {
                            ringtonePlayer.play()
                        }

                    } catch (e: Exception) {
                        FirebaseCrashlytics.getInstance().recordException(e)
                        logManager.logToFile(Enums.Logging.STATE_ERROR, "NextivaAudioManager startRingTone: $e")
                    }

                    vibratePhone()
                    logManager.logToFile(Enums.Logging.STATE_INFO, "NextivaAudioManager RINGING ringtonePlayer getAudioAttributes: " + GsonUtil.getJSON(ringtonePlayer.isPlaying))
                }
            }
        }
    }

    override fun stopRingTone() {
        ringtonePlayer?.let { ringtonePlayer ->
            synchronized(ringtonePlayer) {
                if (--ringRef <= 0) {
                    try {
                        logManager.logToFile(Enums.Logging.STATE_INFO, "NextivaAudioManager stopRingTone stopTone")
                        audioManager?.mode = savedMode
                        ringtonePlayer.stop()

                    } catch (e: Exception) {
                        FirebaseCrashlytics.getInstance().recordException(e)
                        logManager.logToFile(Enums.Logging.STATE_ERROR, "NextivaAudioManager stopRingTone: $e")
                    }

                    try {
                        vibrator?.cancel()
                        
                    } catch (e: Exception) {
                        FirebaseCrashlytics.getInstance().recordException(e)
                        logManager.logToFile(Enums.Logging.STATE_ERROR, "NextivaAudioManager vibrator: $e")
                    }
                }
            }
        }

        stopHeadsetTone()
    }

    override fun startHeadsetTone(@CallTone tone: Int) {
        if (!headsetToneActive) {
            if (headsetTonePlayer == null) {
                headsetTonePlayer = ToneGenerator(
                    if (tone == Enums.Sip.CallTones.RINGBACK) android.media.AudioManager.STREAM_RING else android.media.AudioManager.STREAM_VOICE_CALL,
                    audioManager?.getStreamVolume(android.media.AudioManager.STREAM_RING) ?: TONE_RELATIVE_VOLUME)
            }

            headsetTonePlayer?.let { headsetTonePlayer ->
                synchronized(headsetTonePlayer) {
                    try {
                        logManager.logToFile(Enums.Logging.STATE_INFO, "NextivaAudioManager startHeadsetTone startTone")
                        headsetTonePlayer.startTone(tone)
                        headsetToneActive = true

                    } catch (e: Exception) {
                        logManager.logToFile(Enums.Logging.STATE_ERROR, "Start Headset Tone Player Error: $e")
                        FirebaseCrashlytics.getInstance().recordException(e)
                    }
                }
            }
        }
    }

    override fun stopHeadsetTone() {
        headsetTonePlayer?.let { headsetTonePlayer ->
            synchronized(headsetTonePlayer) {
                if (headsetToneActive) {
                    try {
                        headsetToneActive = false
                        logManager.logToFile(Enums.Logging.STATE_INFO, "NextivaAudioManager stopHeadsetTone stopTone")
                        headsetTonePlayer.stopTone()

                    } catch (e: Exception) {
                        logManager.logToFile(Enums.Logging.STATE_ERROR, "NextivaAudioManager stopHeadsetTone stopTone Error: $e")
                        FirebaseCrashlytics.getInstance().recordException(e)
                    }

                    try {
                        logManager.logToFile(Enums.Logging.STATE_INFO, "NextivaAudioManager stopHeadsetTone release")
                        headsetTonePlayer.release()

                    } catch (e: Exception) {
                        logManager.logToFile(Enums.Logging.STATE_ERROR, "NextivaAudioManager stopHeadsetTone release Error: $e")
                        FirebaseCrashlytics.getInstance().recordException(e)
                    }

                    this.headsetTonePlayer = null

                } else {
                    logManager.logToFile(Enums.Logging.STATE_INFO, "NextivaAudioManager stopHeadsetTone headsetTonePlayer is not active")
                }
            }
        }
    }

    companion object {
        private const val TONE_RELATIVE_VOLUME = 70
    }
}
