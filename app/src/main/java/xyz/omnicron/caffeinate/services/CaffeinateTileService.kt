package xyz.omnicron.caffeinate.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import xyz.omnicron.caffeinate.Caffeine

/**
* @author russjr08
*/
class CaffeinateTileService : TileService() {

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return Service.START_STICKY
    }

    override fun onClick() {
        super.onClick()
        val service = Intent(this, CaffeinationService::class.java)
        val caffeine = application as Caffeine

        if(qsTile?.state == Tile.STATE_INACTIVE) {
            caffeine.tile = qsTile
            startService(service)
            bindService(service, caffeine.connection, Context.BIND_AUTO_CREATE)
            qsTile.state = Tile.STATE_ACTIVE
            qsTile.updateTile()
        } else {
//            qsTile.state = Tile.STATE_INACTIVE
//            unbindService(caffeine.connection)
            if(caffeine.caffeinationService == null) {
                qsTile.state = Tile.STATE_INACTIVE
                qsTile.updateTile()
            }
            caffeine.caffeinationService?.increaseTimer(300000)
        }

        qsTile.updateTile()
    }




}