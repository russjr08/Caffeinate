package xyz.omnicron.caffeinate

import android.app.Application
import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import android.service.quicksettings.Tile
import xyz.omnicron.caffeinate.services.CaffeinationService

/**
 * @author russjr08
 */
class Caffeine: Application() {

    lateinit var tile: Tile

    var caffeinationService: CaffeinationService? = null
    var bound = false

    var connection: ServiceConnection? = object: ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            bound = false
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as CaffeinationService.LocalBinder
            caffeinationService = binder.getService()
            bound = true
        }

    }

    fun initializeServiceConnection() {
        connection = object: ServiceConnection {
            override fun onServiceDisconnected(name: ComponentName?) {
                bound = false
            }

            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val binder = service as CaffeinationService.LocalBinder
                caffeinationService = binder.getService()
                bound = true
            }

        }
    }

}