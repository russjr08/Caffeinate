package xyz.omnicron.caffeinate.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.preference.PreferenceManager
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import xyz.omnicron.caffeinate.Caffeine
import xyz.omnicron.caffeinate.R

/**
* @author russjr08
*/
class CaffeinateTileService : TileService() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return Service.START_STICKY
    }

    override fun onClick() {
        super.onClick()
        val service = Intent(this, CaffeinationService::class.java)
        val caffeine = application as Caffeine

        if(qsTile?.state == Tile.STATE_INACTIVE) {
            caffeine.tile = qsTile
            qsTile?.icon = Icon.createWithResource(baseContext, R.drawable.ic_tile_icon_24dp)
            startService(service)
            caffeine.initializeServiceConnection()
            applicationContext.bindService(service, caffeine.connection, Context.BIND_AUTO_CREATE)
            qsTile.state = Tile.STATE_ACTIVE
            qsTile.updateTile()

            val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)

            if(sharedPrefs.getBoolean("caffeine_instant_infinite_toggle", false)) {
                caffeine.caffeinationService?.increaseTimer(100000000)
            }
        } else if(qsTile?.state == Tile.STATE_ACTIVE) {
            caffeine.tile = qsTile
            if(caffeine.caffeinationService == null) {
                qsTile?.state = Tile.STATE_INACTIVE
                qsTile?.updateTile()
            }

            if(caffeine.caffeinationService != null) {
                if (caffeine.caffeinationService?.infiniteMode!!) {
                    caffeine.caffeinationService?.releaseWakelock("user_cancelled")
                } else {
                    val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)
                    var incrementByPrefValue = sharedPrefs.getString("caffeine_timer_increment_val",
                            "300").toLong() * 1000
                    if(incrementByPrefValue <= 0) {
                        incrementByPrefValue = 300000
                    }
                    caffeine.caffeinationService?.increaseTimer(incrementByPrefValue)
                }
            }
        }

        qsTile?.updateTile()
    }

    override fun onTileAdded() {
        super.onTileAdded()
        qsTile?.state = Tile.STATE_INACTIVE
        qsTile?.label = resources.getString(R.string.caffeinate_tile_label)
        qsTile?.updateTile()
    }

    override fun onTileRemoved() {
        super.onTileRemoved()
        val caffeine = application as Caffeine
        if(caffeine.caffeinationService != null) {
            caffeine.caffeinationService?.releaseWakelock("user_destroyed_tile")
            caffeine.caffeinationService?.resetState()
        }
    }


}