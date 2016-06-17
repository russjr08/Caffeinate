package xyz.omnicron.caffeinate.services

import android.app.Notification
import android.content.Context
import android.os.CountDownTimer
import android.os.PowerManager
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import xyz.omnicron.caffeinate.R

/**
* @author russjr08
*/
class CaffeinateTileService : TileService() {


    lateinit var wakeLock: PowerManager.WakeLock
    lateinit var timer: CountDownTimer
    lateinit var notification: Notification

    val WL_TAG = "Caffeinate"

    override fun onCreate() {
        super.onCreate()

        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager

        wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, WL_TAG)

        timer = object: CountDownTimer(300000, 1000) {
            override fun onFinish() {
                releaseWakelock()
            }

            override fun onTick(remains: Long) {
                qsTile.label = timeConversion(remains)
                qsTile.updateTile()
            }

        }

        notification = Notification.Builder(applicationContext)
                        .setContentTitle("Caffeinating...")
                        .setContentText(getString(R.string.caffeination_in_progress))
                        .setSmallIcon(R.drawable.ic_tile_icon_24dp)
                        .setPriority(Notification.PRIORITY_LOW)
                        .build()

    }

    override fun onClick() {
        super.onClick()

        if(qsTile.state == Tile.STATE_INACTIVE) {
            qsTile.state = Tile.STATE_ACTIVE
            createWakelock()

        } else {
            qsTile.state = Tile.STATE_INACTIVE
            releaseWakelock()
            if(timer != null) {
                timer.cancel()
            }
        }

        qsTile.updateTile()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
        releaseWakelock()
    }

    fun createWakelock() {
        startForeground(1, notification)

        wakeLock.acquire()

        timer.start()

    }

    fun releaseWakelock() {
        qsTile.state = Tile.STATE_INACTIVE
        qsTile.label = getString(R.string.caffeinate_tile_label)
        qsTile.updateTile()

        if(wakeLock.isHeld) {
            wakeLock.release()
        }

        stopForeground(1)
    }

    private fun timeConversion(remains: Long): String {
        val MINUTES_IN_AN_HOUR = 60
        val SECONDS_IN_A_MINUTE = 60

        var seconds = remains / 1000
        var minutes = seconds / SECONDS_IN_A_MINUTE

        seconds -= minutes * SECONDS_IN_A_MINUTE
        val hours = minutes / MINUTES_IN_AN_HOUR
        minutes -= hours * MINUTES_IN_AN_HOUR

        return String.format("%s:%s", minutes, seconds)
    }


}