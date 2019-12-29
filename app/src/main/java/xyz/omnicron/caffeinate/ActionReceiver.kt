package xyz.omnicron.caffeinate

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * @author russjr08
 */

class ActionReceiver: BroadcastReceiver() {
    override fun onReceive(ctx: Context?, intent: Intent) {
        val action = intent.action

        val caffeine = ctx?.applicationContext as Caffeine

        if("xyz.omnicron.caffeinate.STOP_ACTION".equals(action)) {
            caffeine.caffeinationService?.releaseWakelock("cancel_notification")
        } else if("xyz.omnicron.caffeinate.TIMER_SET_INFINITE".equals(action)) {
            caffeine.caffeinationService?.increaseTimer(1000000000)
        } else if("xyz.omnicron.caffeinate.TIMER_RESET".equals(action)) {
            // Kills existing timer

            caffeine.caffeinationService?.resetState()
            caffeine.caffeinationService?.onCreate()



        }
    }



}
