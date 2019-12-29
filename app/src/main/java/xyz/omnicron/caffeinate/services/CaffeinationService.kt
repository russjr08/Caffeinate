package xyz.omnicron.caffeinate.services

import android.app.*
import android.content.*
import android.graphics.drawable.Icon
import android.os.*
import android.preference.PreferenceManager
import android.service.quicksettings.Tile
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import xyz.omnicron.caffeinate.ActionReceiver
import xyz.omnicron.caffeinate.Caffeine
import xyz.omnicron.caffeinate.R
import xyz.omnicron.caffeinate.SettingsActivity
import java.util.concurrent.TimeUnit

/**
 * @author russjr08
 */
class CaffeinationService: Service() {

    private lateinit var wakeLock: PowerManager.WakeLock
    private var timer: CountDownTimer? = null
    private lateinit var notification: Notification
    private val config: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
    private lateinit var sharedPrefs: SharedPreferences

    private val stopActionReceiver = ActionReceiver()
    private val stopIntent = Intent()


    var tile: Tile? = null

    private var mBinder = LocalBinder()
    private var receiver: BroadcastReceiver? = null
    var timeLeft: Long = 0L

    var infiniteMode = false


    val WL_TAG = "Caffeinate:WAKELOCK"

    private val NOTIFICATION_CHANNEL_ID = "caffeination_in_progress"
    private val NOTIFICATION_IN_PROGRESS_ID = 200

    inner class LocalBinder: Binder() {
        fun getService(): CaffeinationService {
            return this@CaffeinationService
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return mBinder
    }

    fun buildNotification() {

        val infiniteIntent = Intent(baseContext, ActionReceiver::class.java)
        val timerResetIntent = Intent(baseContext, ActionReceiver::class.java)


        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val launcherIntent = Intent(this, SettingsActivity::class.java)
        val stopPendingIntent = PendingIntent.getBroadcast(this, 1, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        infiniteIntent.action = "xyz.omnicron.caffeinate.TIMER_SET_INFINITE"
        timerResetIntent.action = "xyz.omnicron.caffeinate.TIMER_RESET"

        val infinitePendingIntent = PendingIntent.getBroadcast(this, 2, infiniteIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val timerResetPendingIntent = PendingIntent.getBroadcast(this, 1, timerResetIntent, PendingIntent.FLAG_UPDATE_CURRENT)


        val builder: Notification.Builder

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID, getString(R.string.notification_channel_caffeination_title), NotificationManager.IMPORTANCE_DEFAULT)
            notificationChannel.description = getString(R.string.notification_channel_caffeination_description)
            notificationChannel.setSound(null, null)
            notificationChannel.enableVibration(false)
            notificationChannel.setShowBadge(false)
            notificationChannel.enableLights(false)
            notificationManager.createNotificationChannel(notificationChannel)

            builder = Notification.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
                    .setContentTitle(getString(R.string.notification_caffeinate_title))
                    .setSmallIcon(if(!infiniteMode) R.drawable.ic_tile_icon_24dp else R.drawable.ic_infinity_black_24dp)
                    .setContentIntent(PendingIntent.getActivity(this, 1, launcherIntent, PendingIntent.FLAG_UPDATE_CURRENT))
                    .setPriority(Notification.PRIORITY_MAX)
                    .setStyle(Notification.BigTextStyle().bigText(if(infiniteMode) getString(R.string.caffeination_in_progress_infinite) else getString(R.string.caffeination_in_progress, timeConversion(timeLeft))))
                    .addAction(R.drawable.ic_stop, getString(R.string.notification_caffeinate_action_cancel), stopPendingIntent)
                    .setChannelId(NOTIFICATION_CHANNEL_ID)

            if(!infiniteMode) {
                builder.addAction(R.drawable.ic_infinity_white_18dp, getString(R.string.notification_caffeinate_action_infinite), infinitePendingIntent)
            } else {
                builder.addAction(R.drawable.ic_hourglass, getString(R.string.notification_caffeinate_action_timer_reset), timerResetPendingIntent)

            }

            notification = builder.build()

        } else {

            builder = Notification.Builder(applicationContext)
                    .setContentTitle(getString(R.string.notification_caffeinate_title))
                    .setSmallIcon(if(!infiniteMode) R.drawable.ic_tile_icon_24dp else R.drawable.ic_infinity_black_24dp)
                    .setContentIntent(PendingIntent.getActivity(this, 1, launcherIntent, PendingIntent.FLAG_UPDATE_CURRENT))
                    .setPriority(Notification.PRIORITY_MAX)
                    .setStyle(Notification.BigTextStyle().bigText(if(infiniteMode) getString(R.string.caffeination_in_progress_infinite) else getString(R.string.caffeination_in_progress, timeConversion(timeLeft))))
                    .addAction(R.drawable.ic_stop, getString(R.string.notification_caffeinate_action_cancel), stopPendingIntent)

            if(!infiniteMode) {
                builder.addAction(R.drawable.ic_infinity_white_18dp, getString(R.string.notification_caffeinate_action_infinite), infinitePendingIntent)
            } else {
                builder.addAction(R.drawable.ic_hourglass, getString(R.string.notification_caffeinate_action_timer_reset), timerResetPendingIntent)
            }

            notification = builder.build()

        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("Caffeine", "Caffeination Service is now running.")
        setupNotificationTestDefaults()

        stopIntent.action = "xyz.omnicron.caffeinate.STOP_ACTION"

        tile = (application as Caffeine).tile

        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)


        val intentFilter = IntentFilter(Intent.ACTION_SCREEN_OFF)

        receiver = object: BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                releaseWakelock("screen_off")
            }

        }
        registerReceiver(receiver, intentFilter)

        if(sharedPrefs.getBoolean("caffeine_screen_dimming", false)) {
            wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, WL_TAG)
        } else {
            wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, WL_TAG)
        }




        registerReceiver(stopActionReceiver, IntentFilter(stopIntent.action))

        buildNotification()

        if(sharedPrefs.getBoolean("caffeine_instant_infinite_toggle", false)) {
            setToInfinite()
        } else {
            startTimer()
        }

    }

    fun increaseTimer(increaseBy: Long) {
        timer?.cancel()
        val newTime = timeLeft + increaseBy

        if(newTime > 3600000) { // 1 hour
            setToInfinite()
        } else {
            tile?.label = timeConversion(newTime)
            tile?.icon = Icon.createWithResource(baseContext, R.drawable.ic_tile_icon_24dp)
            tile?.updateTile()
            buildNotification()
            startTimer(newTime)
        }
    }

    fun setToInfinite() {
        timer?.cancel()
        infiniteMode = true
        tile?.label = "∞"
        tile?.state = Tile.STATE_INACTIVE
        tile?.updateTile()
        tile?.icon = Icon.createWithResource(applicationContext, R.drawable.infinity)
        tile?.state = Tile.STATE_ACTIVE
        tile?.updateTile()
        buildNotification()
        startForeground(NOTIFICATION_IN_PROGRESS_ID, notification)
    }

    fun startTimer(time: Long = sharedPrefs.getString("caffeine_time_limit", "300000").toLong()) {
        Log.d("Caffeine", "Starting caffeination timer for " + time)
        timer?.cancel()
        timer = object: CountDownTimer(time, 1000) {
            override fun onFinish() {
                releaseWakelock("timer_expired")
            }

            override fun onTick(remains: Long) {
                tile?.label = timeConversion(remains)
                tile?.state = Tile.STATE_ACTIVE
                timeLeft = remains
                buildNotification()
                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(NOTIFICATION_IN_PROGRESS_ID, notification)
                tile?.updateTile()
            }

        }

        timer?.start()

        val launcherIconUserProp = if(sharedPrefs.getBoolean("show_launcher_icon", true)) {
            "No"
        } else {
            "Yes"
        }
        val analytics = FirebaseAnalytics.getInstance(this.applicationContext)
        analytics.setUserProperty("hides_launcher_icon", launcherIconUserProp)
        analytics.setUserProperty("screen_timeout_option", sharedPrefs.getString("caffeine_time_limit", "300000"))
        startForeground(NOTIFICATION_IN_PROGRESS_ID, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        tile?.state = Tile.STATE_ACTIVE
        tile?.updateTile()
        createWakelock()
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        unregisterReceiver(receiver)
        unregisterReceiver(stopActionReceiver)
        logDestructionEvent()
        timer?.cancel()
        releaseWakelock()

        resetState()

        if((application as Caffeine).bound && (application as Caffeine).connection != null) {
            applicationContext.unbindService((application as Caffeine).connection)
            (application as Caffeine).bound = false
        }
        (application as Caffeine).initializeServiceConnection()
    }

    fun resetState() {
        timer?.cancel()
        tile?.state = Tile.STATE_INACTIVE
        tile?.label = resources.getString(R.string.caffeinate_tile_label)
        tile?.icon = Icon.createWithResource(baseContext, R.drawable.ic_tile_icon_24dp)
        infiniteMode = false
        timeLeft = 0
        tile?.updateTile()

        if((application as Caffeine).bound && (application as Caffeine).connection != null) {
            applicationContext.unbindService((application as Caffeine).connection)
            (application as Caffeine).bound = false
        }
        (application as Caffeine).initializeServiceConnection()

    }

    private fun timeConversion(remains: Long): String {
        var timeRemaining = remains

        val days = TimeUnit.MILLISECONDS
                .toDays(remains)
        timeRemaining -= TimeUnit.DAYS.toMillis(days)

        val hours = TimeUnit.MILLISECONDS
                .toHours(timeRemaining)
        timeRemaining -= TimeUnit.HOURS.toMillis(hours)

        val minutes = TimeUnit.MILLISECONDS
                .toMinutes(timeRemaining)
        timeRemaining -= TimeUnit.MINUTES.toMillis(minutes)

        val seconds = TimeUnit.MILLISECONDS
                .toSeconds(timeRemaining)

        var strSeconds = seconds.toString()
        if(seconds < 10) {
            strSeconds = String.format("0%s", strSeconds)
        }
        return String.format("%s:%s", minutes, strSeconds)
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
            startForeground(NOTIFICATION_IN_PROGRESS_ID, notification)
        }

        wakeLock.acquire()
    }

    fun releaseWakelock(reason: String = "unknown") {
        if(wakeLock.isHeld) {
            wakeLock.release()
        }

        resetState()

        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "stop_caffeination")
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Caffeination End")
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, reason)
        FirebaseAnalytics.getInstance(this).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)

        stopForeground(Service.STOP_FOREGROUND_REMOVE)
        stopSelf()

    }

    private fun setupNotificationTestDefaults() {
        (application as Caffeine).updateFirebaseRemoteConfigs()
    }


}