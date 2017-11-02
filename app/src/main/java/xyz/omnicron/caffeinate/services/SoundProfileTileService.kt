package xyz.omnicron.caffeinate.services

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.media.AudioManager
import android.provider.Settings
import android.service.quicksettings.TileService
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import xyz.omnicron.caffeinate.R

/**
* @author russjr08
*/
class SoundProfileTileService : TileService() {

    override fun onStartListening() {
        super.onStartListening()

        val audio: AudioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        if(audio.ringerMode == AudioManager.RINGER_MODE_NORMAL) {
            qsTile?.label = getString(R.string.soundprofile_tile_label_ringer)
            qsTile.icon = Icon.createWithResource(this, R.drawable.ic_notifications_active_white_24dp)
        } else {
            qsTile?.label = getString(R.string.soundprofile_tile_label_vibrate)
            qsTile.icon = Icon.createWithResource(this, R.drawable.ic_vibration_white_24dp)
        }

        qsTile?.updateTile()
    }

    override fun onClick() {
        super.onClick()

        val audio: AudioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        if(!isPhoneInDoNotDisturb()) {
            if (audio.ringerMode == AudioManager.RINGER_MODE_NORMAL) {
                audio.ringerMode = AudioManager.RINGER_MODE_VIBRATE
                qsTile.label = getString(R.string.soundprofile_tile_label_vibrate)
                qsTile.icon = Icon.createWithResource(this, R.drawable.ic_vibration_white_24dp)
            } else {
                audio.ringerMode = AudioManager.RINGER_MODE_NORMAL
                qsTile.label = getString(R.string.soundprofile_tile_label_ringer)
                qsTile.icon = Icon.createWithResource(this, R.drawable.ic_notifications_active_white_24dp)

            }
        } else {
            val dialog = MaterialDialog.Builder(applicationContext)
                    .title(R.string.sound_dialog_dnd_error_title)
                    .content(R.string.sound_dialog_dnd_error_message)
                    .positiveText("Grant Permission")
                    .neutralText("Cancel")
                    .onPositive(MaterialDialog.SingleButtonCallback { _: MaterialDialog, _: DialogAction ->
                        startActivity(Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS))
                    })
                    .build()

            showDialog(dialog)
        }

        qsTile.updateTile()

    }

    // https://stackoverflow.com/questions/31387137/android-detect-do-not-disturb-status
    fun isPhoneInDoNotDisturb() : Boolean {
        val notificationManager: NotificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Settings.Global.getInt(contentResolver, "zen_mode") == 0) { // 0 is normal mode
            return false
        } else {
            return !notificationManager.isNotificationPolicyAccessGranted
        }
    }

}