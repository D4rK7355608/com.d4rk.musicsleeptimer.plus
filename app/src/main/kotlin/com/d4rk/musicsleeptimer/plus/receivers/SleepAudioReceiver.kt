package com.d4rk.musicsleeptimer.plus.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.d4rk.musicsleeptimer.plus.workers.SleepAudioWorker

@RequiresApi(Build.VERSION_CODES.O)
class SleepAudioReceiver : BroadcastReceiver() {
    override fun onReceive(context : Context , intent : Intent) {
        if (intent.action == SleepAudioWorker.ACTION_SLEEP_AUDIO) {
            SleepAudioWorker.startWork(context)
        }
    }
}