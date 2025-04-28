package com.nextiva.nextivaapp.android.managers.interfaces

import com.nextiva.nextivaapp.android.constants.Enums.Sip.CallTones.CallTone

/**
 * Created by Thaddeus Dannar on 10/17/18.
 */
interface AudioManager {
    fun stop()

    fun startRingTone(isOnCall: Boolean)

    fun stopRingTone()

    fun startHeadsetTone(@CallTone tone: Int)

    fun stopHeadsetTone()
}
