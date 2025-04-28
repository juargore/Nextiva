package com.nextiva.nextivaapp.android.managers

import android.Manifest
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.media.AudioManager.ACTION_HEADSET_PLUG
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nextiva.nextivaapp.android.constants.Enums.AudioDevices.AudioDevice
import com.nextiva.nextivaapp.android.util.extensions.isNull
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(DelicateCoroutinesApi::class)
class AudioDeviceManager @Inject constructor(
    private val context: Application
){
    private val _availableAudioDevices = MutableLiveData<Pair<AudioDevice, List<AudioDevice>>>()
    val availableAudioDevices : LiveData<Pair<AudioDevice, List<AudioDevice>>> = _availableAudioDevices

    private var currentAudioDev = AudioDevice.EARPIECE
    private var audioDeviceList = setOf<AudioDevice>()

    private var job : Job? = null
    private var scope: CoroutineScope? = null
    private var bluetoothIntent : Intent? = null
    private var headsetIntent : Intent? = null
    private var bluetoothName: String? = null

    private val headsetBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when(intent.action) {
                BluetoothDevice.ACTION_ACL_CONNECTED -> { waitAudioDeviceRemoval(AudioDevice.WIRED_HEADSET) }
                BluetoothDevice.ACTION_ACL_DISCONNECTED -> { waitAudioDeviceRemoval(AudioDevice.WIRED_HEADSET) }
            }
        }
    }

    private val bluetoothBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when(intent.action) {
                BluetoothDevice.ACTION_ACL_CONNECTED -> { waitAudioDeviceToConnect(AudioDevice.BLUETOOTH) }
                BluetoothDevice.ACTION_ACL_DISCONNECTED -> { waitAudioDeviceRemoval(AudioDevice.BLUETOOTH) }
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun start(coroutineScope: CoroutineScope = GlobalScope) {
        scope = coroutineScope

        val init = bluetoothIntent.isNull() && headsetIntent.isNull()

        if(bluetoothIntent.isNull()) {
            Log.d("AudioDeviceManager", "AudioDeviceManager: start Bluetooth monitor")
            IntentFilter().apply {
                addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
                addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
                bluetoothIntent = context?.registerReceiver(bluetoothBroadcastReceiver, this)
            }
        }

        if(headsetIntent.isNull()) {
            Log.d("AudioDeviceManager", "AudioDeviceManager: start Headset monitor")
            IntentFilter(ACTION_HEADSET_PLUG).let {
                headsetIntent = context?.registerReceiver(headsetBroadcastReceiver, it)
            }
        }

        if(init) {
            getCurrentAudioDevice()
            getAudioDevices().apply {
                if (contains(AudioDevice.WIRED_HEADSET)) {
                    setAudioDevice(AudioDevice.WIRED_HEADSET)
                } else if (contains(AudioDevice.BLUETOOTH)) {
                    setAudioDevice(AudioDevice.BLUETOOTH)
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
                        getBluetoothDeviceNameAndroid11()
                    } else {
                        getBluetoothDeviceName()
                    }
                } else {
                    setAudioDevice(AudioDevice.EARPIECE)
                }
            }
            updateAvailableDevices()
        }
    }

    fun close() {
        Log.d("AudioDeviceManager", "AudioDeviceManager: Closing")

        job?.cancel()
        job = null
        (context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager)?.apply {
            mode = AudioManager.MODE_NORMAL
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                clearCommunicationDevice()
            } else {
                setAudioDevice(AudioDevice.EARPIECE)
            }
        }
        bluetoothIntent?.let { context.unregisterReceiver(bluetoothBroadcastReceiver) }
        headsetIntent?.let { context.unregisterReceiver(headsetBroadcastReceiver) }
        bluetoothIntent = null
        headsetIntent = null
    }

    private fun getAudioDevices() : Set<AudioDevice> {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            audioManager.availableCommunicationDevices
        } else {
            audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS).toList()
        }.let { devices ->
            mutableSetOf<AudioDevice>().apply {
                devices.forEach {
                    when (it.type) {
                        AudioDeviceInfo.TYPE_BUILTIN_SPEAKER -> add(AudioDevice.SPEAKER_PHONE)
                        AudioDeviceInfo.TYPE_BUILTIN_EARPIECE -> add(AudioDevice.EARPIECE)
                        AudioDeviceInfo.TYPE_WIRED_HEADSET,
                        AudioDeviceInfo.TYPE_WIRED_HEADPHONES -> add(AudioDevice.WIRED_HEADSET)

                        AudioDeviceInfo.TYPE_BLUETOOTH_A2DP,
                        AudioDeviceInfo.TYPE_BLUETOOTH_SCO -> add(AudioDevice.BLUETOOTH)

                        else -> {}
                    }
                }
            }
        }.apply { audioDeviceList = this }
    }

    fun setAudioDevice(device: AudioDevice): Boolean {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            var speaker: AudioDeviceInfo? = null
            var bluetooth: AudioDeviceInfo? = null
            var earpiece: AudioDeviceInfo? = null
            var headset: AudioDeviceInfo? = null
            audioManager.availableCommunicationDevices.forEach { acd ->
                when (acd.type) {
                    AudioDeviceInfo.TYPE_BUILTIN_SPEAKER -> speaker = acd
                    AudioDeviceInfo.TYPE_BUILTIN_EARPIECE -> earpiece = acd
                    AudioDeviceInfo.TYPE_BLUETOOTH_SCO -> bluetooth = acd
                    AudioDeviceInfo.TYPE_WIRED_HEADSET -> headset = acd
                    else -> {}
                }
            }

            when (device) {
                AudioDevice.SPEAKER_PHONE -> speaker?.let {
                    audioManager.setCommunicationDevice(it)
                } ?: false
                AudioDevice.BLUETOOTH -> bluetooth?.let {
                    audioManager.setCommunicationDevice(it)
                } ?: false
                AudioDevice.WIRED_HEADSET -> headset?.let {
                    audioManager.setCommunicationDevice(it)
                } ?: false

                else -> earpiece?.let {
                    audioManager.setCommunicationDevice(it)
                } ?: false
            }
        } else {
            try {
                when (device) {
                    AudioDevice.SPEAKER_PHONE -> {
                        audioManager.isSpeakerphoneOn = true
                    }
                    AudioDevice.WIRED_HEADSET -> {
                        audioManager.isWiredHeadsetOn = true
                    }
                    AudioDevice.BLUETOOTH -> {
                        audioManager.startBluetoothSco()
                        audioManager.isBluetoothScoOn = true
                        audioManager.isBluetoothA2dpOn = true
                        audioManager.isWiredHeadsetOn = false
                        audioManager.isSpeakerphoneOn = false
                    }
                    else -> {
                        audioManager.isBluetoothScoOn = false
                        audioManager.isBluetoothA2dpOn = false
                        audioManager.isWiredHeadsetOn = false
                        audioManager.isSpeakerphoneOn = false
                    }
                }
                true
            } catch (e: Exception) {
                false
            }
        }.apply {
            if(this) currentAudioDev = device
            updateAvailableDevices()
        }
    }

    private fun getCurrentAudioDevice(): AudioDevice {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            when (audioManager.communicationDevice?.type) {
                AudioDeviceInfo.TYPE_BUILTIN_SPEAKER -> {
                    AudioDevice.SPEAKER_PHONE
                }
                AudioDeviceInfo.TYPE_WIRED_HEADSET,
                AudioDeviceInfo.TYPE_WIRED_HEADPHONES -> AudioDevice.WIRED_HEADSET
                AudioDeviceInfo.TYPE_BLUETOOTH_A2DP,
                AudioDeviceInfo.TYPE_BLUETOOTH_SCO -> AudioDevice.BLUETOOTH
                else -> AudioDevice.EARPIECE
            }
        } else {
            when {
                audioManager.isSpeakerphoneOn -> {
                    AudioDevice.SPEAKER_PHONE
                }
                audioManager.isBluetoothA2dpOn || audioManager.isBluetoothScoOn -> AudioDevice.BLUETOOTH
                audioManager.isWiredHeadsetOn -> AudioDevice.WIRED_HEADSET
                else -> AudioDevice.EARPIECE
            }
        }.apply {
            currentAudioDev = this
            updateAvailableDevices()
        }
    }

    private fun waitAudioDeviceToConnect(audioDevice: AudioDevice) {
        job?.cancel()
        job = scope?.launch(Dispatchers.IO) {
            var counter = 5
            while (counter-- >= 0 && isActive) {
                if (getAudioDevices().contains(audioDevice)) {
                    if (audioDevice == AudioDevice.BLUETOOTH) {
                        getBluetoothDeviceName()
                    }
                    Log.d("AudioDeviceManager", "AudioDeviceManager: device connected $audioDevice")
                    setAudioDevice(audioDevice)
                    return@launch
                }
                if (isActive) delay(1200)
            }
        }
    }

    private fun waitAudioDeviceRemoval(removedDevice: AudioDevice) {
        scope?.launch(Dispatchers.IO) {
            var counter = 5
            while (counter-- >= 0 && isActive) {
                getAudioDevices()
                if (!getAudioDevices().contains(removedDevice)) {
                    Log.d("AudioDeviceManager", "AudioDeviceManager: device removed $removedDevice")
                    if (removedDevice == AudioDevice.BLUETOOTH) {
                        bluetoothName = null
                    }
                    if (currentAudioDev == removedDevice) {
                        setAudioDevice(AudioDevice.EARPIECE)
                    } else {
                        updateAvailableDevices()
                    }
                    return@launch
                }
                if (isActive) delay(1200)
            }
        }
    }

    fun toggleSpeaker() {
        MainScope().launch(Dispatchers.IO) {
            val audioDevice = when (currentAudioDev) {
                AudioDevice.EARPIECE -> AudioDevice.SPEAKER_PHONE
                else -> AudioDevice.EARPIECE
            }
            val result = setAudioDevice(audioDevice)
            updateAvailableDevices()
            Log.d("AudioDeviceManager", "AudioDevice : ToggleSpeaker: $audioDevice : $result")
        }
    }

    private fun getBluetoothDeviceName() {
        job?.cancel()
        job = scope?.launch(Dispatchers.IO) {
            var pxy: BluetoothProfile? = null
            var counter = 3
            while (counter-- > 0 && bluetoothName.isNull() && isActive) {
                Log.d("AudioDeviceManager", "AudioDeviceManager : ($counter) Trying to get bluetooth name...")
                val bluetoothAdapter = (context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager)?.adapter
                bluetoothAdapter?.let { bluetoothAdapter ->
                    bluetoothAdapter.getProfileProxy(
                        context,
                        object : BluetoothProfile.ServiceListener {
                            override fun onServiceConnected(
                                profile: Int,
                                proxy: BluetoothProfile
                            ) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.BLUETOOTH_CONNECT
                                    ) == PackageManager.PERMISSION_GRANTED
                                ) {
                                    pxy = proxy
                                    pxy?.connectedDevices?.firstOrNull()?.name?.let {
                                        bluetoothName = it
                                        updateAvailableDevices()
                                        Log.d(
                                            "AudioDeviceManager",
                                            "AudioDeviceManager : BT Connected : $bluetoothName"
                                        )
                                    }
                                }
                            }

                            override fun onServiceDisconnected(profile: Int) {
                            }
                        },
                        BluetoothProfile.A2DP
                    )
                }
                // Wait 6 seconds on each try, bluetooth takes several seconds
                // for the system to recognize the device's info
                if(isActive && bluetoothName.isNull()) delay(6000)
                pxy?.let { bluetoothAdapter?.closeProfileProxy(BluetoothProfile.A2DP, it) }
            }
        }
    }

    private fun getBluetoothDeviceNameAndroid11() {
        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            Log.d("AudioDeviceManager", "BluetoothAdapter not supported")
        } else if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            Log.d("AudioDeviceManager", "Bluetooth permission not granted")
        } else {
            bluetoothName = bluetoothAdapter.name
        }
    }

    private fun updateAvailableDevices() {
        _availableAudioDevices.postValue(Pair(currentAudioDev, audioDeviceList.toList()))
    }

    fun getCurrentAudioDev() = currentAudioDev

    fun getBluetoothName() = bluetoothName

}