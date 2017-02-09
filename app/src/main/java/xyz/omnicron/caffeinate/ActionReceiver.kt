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

        if("xyz.omnicron.caffeinate.STOP_ACTION".equals(action)) {
            (ctx?.applicationContext as Caffeine).caffeinationService?.releaseWakelock()
        }
    }

}
