package xyz.omnicron.caffeinate.services

import android.content.Context
import android.graphics.drawable.Icon
import android.media.AudioManager
import android.service.quicksettings.TileService
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
        } else {
            qsTile?.label = getString(R.string.soundprofile_tile_label_vibrate)
        }

        qsTile?.updateTile()
    }

    override fun onClick() {
        super.onClick()

        val audio: AudioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        if(qsTile.label == getString(R.string.soundprofile_tile_label_ringer)) {
            audio.ringerMode = AudioManager.RINGER_MODE_VIBRATE
            qsTile.label = getString(R.string.soundprofile_tile_label_vibrate)
            qsTile.icon = Icon.createWithResource(this, R.drawable.ic_vibration_white_24dp)
        } else {
            audio.ringerMode = AudioManager.RINGER_MODE_NORMAL
            qsTile.label = getString(R.string.soundprofile_tile_label_ringer)
            qsTile.icon = Icon.createWithResource(this, R.drawable.ic_notifications_active_white_24dp)

        }

        qsTile.updateTile()

    }

}