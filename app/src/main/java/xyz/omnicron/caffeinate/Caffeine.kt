package xyz.omnicron.caffeinate

import android.app.Application
import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import android.service.quicksettings.Tile
import com.google.firebase.FirebaseApp
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import xyz.omnicron.caffeinate.services.CaffeinationService
import java.util.*

/**
 * @author russjr08
 */
class Caffeine: Application() {

    lateinit var tile: Tile
    lateinit var config: FirebaseRemoteConfig

    var caffeinationService: CaffeinationService? = null

    override fun onCreate() {
        if (!FirebaseApp.getApps(this).isEmpty()) {
            config = FirebaseRemoteConfig.getInstance()
        }
    }

    fun updateFirebaseRemoteConfigs() {
        updateFirebaseRemoteConfigs({})
    }

    fun updateFirebaseRemoteConfigs(callback: () -> Unit?) {

        val defaults = HashMap<String, Any>()
        defaults.put("persistent_notification_for_tileservice", false)

        config.setDefaults(defaults)

        val debugging = BuildConfig.DEBUG
        var cacheTime = 0L

        if(!debugging) {
            cacheTime = 2000L
        }

        config.setConfigSettings(FirebaseRemoteConfigSettings.Builder().setDeveloperModeEnabled(debugging).build())


        config.fetch(cacheTime).addOnCompleteListener { task ->
            run {
                if (task.isSuccessful) {
                    config.activateFetched()
                    callback()
                }
            }
        }
    }

    var connection: ServiceConnection? = object: ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {

        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            var binder = service as CaffeinationService.LocalBinder
            caffeinationService = binder.getService()
        }

    }

    fun initializeServiceConnection() {
        connection = object: ServiceConnection {
            override fun onServiceDisconnected(name: ComponentName?) {

            }

            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                var binder = service as CaffeinationService.LocalBinder
                caffeinationService = binder.getService()
            }

        }
    }

}