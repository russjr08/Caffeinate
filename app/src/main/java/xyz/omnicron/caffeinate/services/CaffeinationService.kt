package xyz.omnicron.caffeinate.services

import android.app.Notification
import android.app.Service
import android.content.*
import android.os.*
import android.preference.PreferenceManager
import android.service.quicksettings.Tile
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import xyz.omnicron.caffeinate.Caffeine
import xyz.omnicron.caffeinate.R

/**
 * @author russjr08
 */
class CaffeinationService: Service() {

    lateinit var wakeLock: PowerManager.WakeLock
    var timer: CountDownTimer? = null
    lateinit var notification: Notification
    val config: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
    lateinit var sharedPrefs: SharedPreferences

    lateinit var tile: Tile

    var mBinder = LocalBinder()
    var receiver: BroadcastReceiver? = null
    var timeLeft: Long = 0L


    val WL_TAG = "Caffeinate"

    inner class LocalBinder: Binder() {
        fun getService(): CaffeinationService {
            return this@CaffeinationService
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return mBinder
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("Caffeine", "Caffeination Service is now running.")
        setupNotificationTestDefaults()

        tile = (application as Caffeine).tile

        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)

        setupNotificationTestDefaults()

        val intentFilter = IntentFilter(Intent.ACTION_SCREEN_OFF)

        receiver = object: BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                releaseWakelock()
            }

        }
        registerReceiver(receiver, intentFilter)

        if(sharedPrefs.getBoolean("caffeine_screen_dimming", false)) {
            wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, WL_TAG)
        } else {
            wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, WL_TAG)
        }


        notification = Notification.Builder(applicationContext)
                .setContentTitle("Caffeinating...")
                .setContentText(getString(R.string.caffeination_in_progress))
                .setSmallIcon(R.drawable.ic_tile_icon_24dp)
                .setPriority(Notification.PRIORITY_LOW)
                .build()

        startTimer()

    }

    fun increaseTimer(increaseBy: Long) {
        timer?.cancel()
        startTimer(timeLeft + increaseBy)
    }

    fun startTimer(time: Long = sharedPrefs.getString("caffeine_time_limit", "300000").toLong()) {
        Log.d("Caffeine", "Starting caffeination timer for " + time)
        timer?.cancel()
        timer = object: CountDownTimer(time, 1000) {
            override fun onFinish() {
                releaseWakelock()
            }

            override fun onTick(remains: Long) {
                tile?.label = timeConversion(remains)
                timeLeft = remains;
                tile?.updateTile()
            }

        }

        timer?.start()

        startForeground(50, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        tile?.state = Tile.STATE_ACTIVE
        tile?.updateTile()
        createWakelock()
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        println("We're being destroyed :(")
        unregisterReceiver(receiver)
//        logDestructionEvent()
        timer?.cancel()
        releaseWakelock()

        tile?.state = Tile.STATE_INACTIVE
        tile?.updateTile()

//        if((application as Caffeine).connection != null) {
//            unbindService((application as Caffeine).connection)
//        }
        (application as Caffeine).initializeServiceConnection()
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


    fun logDestructionEvent() {
        val bundle = Bundle()
        bundle.putString("notification_enabled",
                config.getBoolean("persistent_notification_for_tileservice").toString())
        FirebaseAnalytics.getInstance(this).logEvent("service_destruction", bundle)
    }


    fun createWakelock() {
        if(config.getBoolean("persistent_notification_for_tileservice") ||
                sharedPrefs.getBoolean("opt_into_notification_test", false)) {
            startForeground(101, notification)
        }

        wakeLock.acquire()

        startTimer()
    }

    fun releaseWakelock() {
        tile?.state = Tile.STATE_INACTIVE
        tile?.label = getString(R.string.caffeinate_tile_label)
        tile?.updateTile()

        if(wakeLock.isHeld) {
            wakeLock.release()
        }

        timer?.cancel()

        if(config.getBoolean("persistent_notification_for_tileservice") ||
                sharedPrefs.getBoolean("opt_into_notification_test", false)) {
        }
        stopForeground(Service.STOP_FOREGROUND_REMOVE)
        stopSelf()

    }

    private fun setupNotificationTestDefaults() {
        (application as Caffeine).updateFirebaseRemoteConfigs()
    }


}