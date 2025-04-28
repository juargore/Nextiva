/*
 * Copyright (c) 2023. Nextiva, inc. to Present.
 * All rights reserved
 */

package com.nextiva.nextivaapp.android.meetings

import java.util.Locale
import java.util.Timer
import java.util.TimerTask

/**
 * Created by Thaddeus Dannar on 10/27/23.
 */
class RecordingTimer {
    var timer = Timer()
    var startTime: Long = 0
    var presentTime: Long = 0
    var presentTimeString: String = ""

    fun clearTimer() {
        presentTime = 0L
    }

    fun startTimerFromExistingTimeInMilli(time: Long) {
        presentTime = time
        startRecordingTimer()
    }
    fun getTimerInMillis(): Long {
        if (presentTime == 0L) {
            return presentTime
        }
        return System.currentTimeMillis() - presentTime
    }


    fun startRecordingTimer(){
        timer.scheduleAtFixedRate(
            object : TimerTask() {
                override fun run() {
                    if (presentTime != 0L) {
                        presentTimeString = String.format(
                            Locale.getDefault(),
                            "%02d:%02d:%02d",
                            presentTime / 3600,
                            presentTime % 3600 / 60,
                            presentTime % 60
                        )
                    } else {
                        presentTime++
                    }
                }
            },
            1000,
            1000
        )
    }
}