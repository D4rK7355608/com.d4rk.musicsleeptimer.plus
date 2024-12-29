package com.d4rk.musicsleeptimer.plus.workers

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.d4rk.musicsleeptimer.plus.receivers.SleepAudioReceiver
import java.util.concurrent.TimeUnit

@RequiresApi(Build.VERSION_CODES.O)
class SleepAudioWorker(
    context : Context ,
    workerParams : WorkerParameters ,
) : Worker(context , workerParams) {

    companion object {
        private val FADE_STEP_MILLIS : Long = TimeUnit.SECONDS.toMillis(1)
        private val RESTORE_VOLUME_MILLIS : Long = TimeUnit.SECONDS.toMillis(2)
        private const val UNIQUE_WORK_NAME : String = "sleep_audio_work"
        const val ACTION_SLEEP_AUDIO : String = "com.d4rk.musicsleeptimer.plus.action.SLEEP_AUDIO"

        fun pendingIntent(context : Context) : PendingIntent? {
            val intent : Intent = Intent(context , SleepAudioReceiver::class.java).apply {
                action = ACTION_SLEEP_AUDIO
            }
            return PendingIntent.getBroadcast(
                context , 0 , intent , PendingIntent.FLAG_IMMUTABLE
            )
        }

        internal fun startWork(context : Context) {
            val workRequest : OneTimeWorkRequest = OneTimeWorkRequestBuilder<SleepAudioWorker>().build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                UNIQUE_WORK_NAME , ExistingWorkPolicy.REPLACE , workRequest
            )
        }
    }

    override fun doWork(): Result {
        return runCatching {
            applicationContext.getSystemService(AudioManager::class.java)?.run {
                val volumeIndex: Int = getStreamVolume(AudioManager.STREAM_MUSIC)

                do {
                    adjustStreamVolume(
                        AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, 0
                    )
                    Thread.sleep(FADE_STEP_MILLIS)
                } while (getStreamVolume(AudioManager.STREAM_MUSIC) > 0)

                val attributes: AudioAttributes = AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()

                val focusRequest: AudioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                        .setAudioAttributes(attributes)
                        .setOnAudioFocusChangeListener {}
                        .build()

                requestAudioFocus(focusRequest)

                Thread.sleep(RESTORE_VOLUME_MILLIS)

                setStreamVolume(AudioManager.STREAM_MUSIC, volumeIndex, 0)

                abandonAudioFocusRequest(focusRequest)

                Result.success()
            } ?: Result.failure()
        }.getOrElse {
            Result.failure()
        }
    }
}