package com.nextiva.nextivaapp.android.viewmodels

import android.app.Application
import com.nextiva.nextivaapp.android.managers.interfaces.ConfigManager
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.models.AudioCodec
import com.nextiva.nextivaapp.android.sip.pjsip.PJSipManager
import com.nextiva.nextivaapp.android.util.GsonUtil
import com.nextiva.pjsip.pjsip_lib.sipservice.EnabledCodecs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.pjsip.pjsua2.pjmedia_echo_flag
import org.pjsip.pjsua2.pjmedia_echo_flag.PJMEDIA_ECHO_WEBRTC
import javax.inject.Inject

@HiltViewModel
class SipConfigurationViewModel @Inject constructor(application: Application,
                                                    var sessionManager: SessionManager,
                                                    var configManager: ConfigManager,
                                                    var sipManager: PJSipManager) : BaseViewModel(application) {

    private val defaultEchoCancellation = Pair("Default", 0)
    private val speexEchoCancellation = Pair("SPEEX", 1)
    private val simpleEchoCancellation = Pair("Simple", 2)
    private val webRtcEchoCancellation = Pair("WebRTC", 3)
    private val webRtcAec3EchoCancellation = Pair("WebRTC AEC3", 4)

    private val defaultAggressiveness = Pair("Default", 0)
    private val conservativeAggressiveness = Pair("Conservative", 0x1000)
    private val moderateAggressiveness = Pair("Moderate", 0x2000)
    private val aggressiveAggressiveness = Pair("Aggressive", 0x3000)

    val echoCancellationOptions = listOf(defaultEchoCancellation, speexEchoCancellation, simpleEchoCancellation, webRtcEchoCancellation, webRtcAec3EchoCancellation)
    val echoCancellationAggressiveness = listOf(defaultAggressiveness, conservativeAggressiveness, moderateAggressiveness, aggressiveAggressiveness)

    fun getEnabledAudioCodecs(): EnabledCodecs {
        return sessionManager.enabledAudioCodecs ?: EnabledCodecs(ArrayList(getAudioCodecs()?.map { it.name ?: "" } ?: arrayListOf()))
    }

    fun getEnabledAudioCodecsFlow(): Flow<EnabledCodecs> {
        return sessionManager.enabledAudioCodecsFlow.map {
            return@map if (it?.value == null) {
                getEnabledAudioCodecs()

            } else {
                GsonUtil.getObject(EnabledCodecs::class.java, it.value)
            }
        }
    }

    fun getAudioCodecs(): ArrayList<AudioCodec>? {
        return arrayListOf(AudioCodec("G722"), AudioCodec("PCMU"), AudioCodec("PCMA"), AudioCodec("AMR-WB"), AudioCodec("OPUS"))
    }

    fun enableAudioCodec(codecName: String?, enabled: Boolean) {
        codecName?.let {
            val enabledCodecs = getEnabledAudioCodecs().enabledCodecs

            if (enabled) {
                enabledCodecs.add(codecName)
            } else {
                enabledCodecs.remove(codecName)
            }

            sessionManager.enabledAudioCodecs = EnabledCodecs(enabledCodecs)
        }
    }

    fun enableAll() {
        getAudioCodecs()?.let { audioCodecs ->
            sessionManager.enabledAudioCodecs = EnabledCodecs(ArrayList(audioCodecs.map { it.name ?: "" }.filter { it != "" }))
        }
    }

    fun disableAll() {
        sessionManager.enabledAudioCodecs = EnabledCodecs(arrayListOf())
    }

    fun getEchoCancellationItemPosition(): Int {
        sessionManager.echoCancellation?.toIntOrNull()?.let { selected ->
            return echoCancellationOptions.indexOfFirst { it.second == selected }
        }

        return 3
    }

    fun setEchoCancellation(echoCancellation: Pair<String, Int>) {
        echoCancellationOptions.firstOrNull { it.first == echoCancellation.first }?.let {
            sessionManager.setEchoCancellation(it.second)
        }

        setEcOptions(
            sessionManager.isNoiseSuppressionEnabled,
            echoCancellation.second,
            echoCancellationAggressiveness.firstOrNull { it.second == sessionManager.aecAggressiveness?.toIntOrNull() }?.second ?: PJMEDIA_ECHO_WEBRTC)
    }

    fun getEchoAggressivenessItemPosition(): Int {
        sessionManager.aecAggressiveness?.toIntOrNull()?.let { selected ->
            return echoCancellationAggressiveness.indexOfFirst { it.second == selected }
        }

        return 0
    }

    fun setEchoAggressiveness(aggressiveness: Pair<String, Int>) {
        echoCancellationAggressiveness.firstOrNull { it.first == aggressiveness.first }?.let {
            sessionManager.setAECAggressiveness(it.second)
        }
        setEcOptions(sessionManager.isNoiseSuppressionEnabled,
            echoCancellationOptions.firstOrNull { it.second == sessionManager.echoCancellation?.toIntOrNull() }?.second ?: PJMEDIA_ECHO_WEBRTC,
            aggressiveness.second)
    }

    fun setNoiseSuppression(isEnabled: Boolean) {
        sessionManager.isNoiseSuppressionEnabled = isEnabled
        setEcOptions(isEnabled,
            echoCancellationOptions.firstOrNull { it.second == sessionManager.echoCancellation?.toIntOrNull() }?.second ?: PJMEDIA_ECHO_WEBRTC,
            echoCancellationAggressiveness.firstOrNull { it.second == sessionManager.aecAggressiveness?.toIntOrNull() }?.second ?: 0)
    }

    fun getNoiseSuppressionEnabled(): Boolean {
        return sessionManager.isNoiseSuppressionEnabled
    }

    private fun setEcOptions(isNoiseSuppressionEnabled: Boolean, echoCancellation: Int, aggressiveness: Int) {
        sipManager.setEcOptions(if (isNoiseSuppressionEnabled) {
            (echoCancellation or pjmedia_echo_flag.PJMEDIA_ECHO_USE_NOISE_SUPPRESSOR or aggressiveness).toLong()
        } else {
            (echoCancellation or aggressiveness).toLong()
        })
    }
}