package com.d4rk.musicsleeptimer.plus.notifications
import android.app.Notification
import android.app.Notification.CATEGORY_EVENT
import android.app.Notification.VISIBILITY_PUBLIC
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import com.d4rk.musicsleeptimer.plus.R
import com.d4rk.musicsleeptimer.plus.services.SleepAudioService
import com.d4rk.musicsleeptimer.plus.services.SleepTileService
import java.lang.System.currentTimeMillis
import java.text.DateFormat
import java.text.DateFormat.SHORT
import java.util.Date
import java.util.concurrent.TimeUnit.MILLISECONDS
import java.util.concurrent.TimeUnit.MINUTES
object SleepNotification {
    private val TIMEOUT_INITIAL_MILLIS = MINUTES.toMillis(30)
    private val TIMEOUT_INCREMENT_MILLIS = MINUTES.toMillis(10)
    private val TIMEOUT_DECREMENT_MILLIS = MINUTES.toMillis(10)
    private enum class Action(private val value: String) {
        CANCEL("com.d4rk.musicsleeptimer.action.CANCEL") {
            override fun title(context: Context) = context.getText(R.string.notification_action_cancel)
        },
        INCREMENT("com.d4rk.musicsleeptimer.action.INCREMENT") {
            override fun title(context: Context) = context.getString(
                R.string.notification_action_increment, MILLISECONDS.toMinutes(
                    TIMEOUT_INCREMENT_MILLIS
                ))
        },
        DECREMENT("com.d4rk.musicsleeptimer.action.DECREMENT") {
            override fun title(context: Context) = context.getString(
                R.string.notification_action_decrement, MILLISECONDS.toMinutes(
                    TIMEOUT_DECREMENT_MILLIS
                ))
        },;
        companion object {
            fun parse(value: String?): Action? = values().firstOrNull { it.value == value }
        }
        fun intent(context: Context): Intent = Intent(context, SleepTileService::class.java).setAction(value)
        fun pendingIntent(context: Context, cancel: Boolean = false): PendingIntent? = PendingIntent.getService(context, 0, intent(context), FLAG_IMMUTABLE).apply { if (cancel) cancel() }
        fun action(context: Context, cancel: Boolean = false): Notification.Action.Builder = Notification.Action.Builder(Icon.createWithResource(context, 0), title(context), pendingIntent(context, cancel))
        abstract fun title(context: Context): CharSequence?
    }
    private fun Context.notificationManager() = getSystemService(NotificationManager::class.java)
    fun Context.find() = notificationManager()?.activeNotifications?.firstOrNull { it.id == R.id.notification_id }?.notification
    fun Context.handle(intent: Intent?) = when (Action.parse(intent?.action)) {
        Action.INCREMENT -> update(TIMEOUT_INCREMENT_MILLIS)
        Action.DECREMENT -> update(-TIMEOUT_DECREMENT_MILLIS)
        Action.CANCEL -> cancel()
        null -> Unit
    }
    fun Context.toggle() = if (find() == null) show() else cancel()
    private fun Context.cancel() = notificationManager()?.cancel(R.id.notification_id) ?: Unit
    private fun Context.update(timeout: Long) = find()?.let { it.`when` - currentTimeMillis() }?.let { if (it > -timeout) it + timeout else it }?.let { show(it) }
    private fun Context.show(timeout: Long = TIMEOUT_INITIAL_MILLIS) {
        require(timeout > 0)
        val eta = currentTimeMillis() + timeout
        val notification = Notification.Builder(this, getString(R.string.notification_channel_id))
            .setCategory(CATEGORY_EVENT)
            .setVisibility(VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_music_off)
            .setSubText(DateFormat.getTimeInstance(SHORT).format(Date(eta)))
            .setShowWhen(true).setWhen(eta)
            .setUsesChronometer(true).setChronometerCountDown(true)
            .setTimeoutAfter(timeout)
            .setDeleteIntent(SleepAudioService.pendingIntent(this))
            .addAction(Action.INCREMENT.action(this).build())
            .addAction(Action.DECREMENT.action(this, cancel = timeout <= TIMEOUT_DECREMENT_MILLIS).build())
            .addAction(Action.CANCEL.action(this).build())
            .build()
        createNotificationChannel()
        notificationManager()?.notify(R.id.notification_id, notification)
    }
    private fun Context.createNotificationChannel() {
        val id = getString(R.string.notification_channel_id)
        val name: CharSequence = getString(R.string.app_name)
        val channel = NotificationChannel(id, name, IMPORTANCE_LOW).apply {
            setBypassDnd(true)
            lockscreenVisibility = VISIBILITY_PUBLIC
        }
        notificationManager()?.createNotificationChannel(channel)
    }
}