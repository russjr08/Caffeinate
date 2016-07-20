package xyz.omnicron.caffeinate.services

import android.app.Service
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
        var service = Intent(this, CaffeinationService::class.java)

        if(qsTile.state == Tile.STATE_INACTIVE) {
            (application as Caffeine).tile = qsTile
            startService(service)
        } else {
            qsTile.state = Tile.STATE_INACTIVE
            stopService(service)
        }

        qsTile.updateTile()
    }

}