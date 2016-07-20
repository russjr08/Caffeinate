package xyz.omnicron.caffeinate

import android.app.Application
import android.service.quicksettings.Tile
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import java.util.*

/**
 * @author russjr08
 */
class Caffeine: Application() {

    lateinit var tile: Tile
    val config = FirebaseRemoteConfig.getInstance()

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

}