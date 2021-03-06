package com.d4rk.musicsleeptimer.plus.services
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.Q
import android.service.quicksettings.Tile.STATE_ACTIVE
import android.service.quicksettings.Tile.STATE_INACTIVE
import android.service.quicksettings.TileService
import com.d4rk.musicsleeptimer.plus.R
import com.d4rk.musicsleeptimer.plus.notifications.SleepNotification.find
import com.d4rk.musicsleeptimer.plus.notifications.SleepNotification.handle
import com.d4rk.musicsleeptimer.plus.notifications.SleepNotification.toggle
import java.text.DateFormat.SHORT
import java.text.DateFormat.getTimeInstance
import java.util.Date
class SleepTileService : TileService() {
    companion object {
        fun Context.requestTileUpdate() = requestListeningState(this, ComponentName(this, SleepTileService::class.java))
    }
    override fun onStartListening() = refreshTile()
    override fun onClick() = toggle().also { refreshTile() }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        handle(intent)
        requestTileUpdate()
        stopSelfResult(startId)
        return START_NOT_STICKY
    }
    private fun refreshTile() = qsTile?.run {
        when (val notification = find()) {
            null -> {
                state = STATE_INACTIVE
                if (SDK_INT >= Q) subtitle = resources.getText(R.string.tile_subtitle)
            }
            else -> {
                state = STATE_ACTIVE
                if (SDK_INT >= Q) subtitle = getTimeInstance(SHORT).format(Date(notification.`when`))
            }
        }
        updateTile()
    } ?: Unit
}