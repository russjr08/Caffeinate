package xyz.omnicron.caffeinate

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import com.google.firebase.remoteconfig.FirebaseRemoteConfig

class ABTestingDebugActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_abtesting_debug)

        val textview = findViewById(R.id.editText) as TextView

        textview.text = "persistent_notification_for_tileservice: " + FirebaseRemoteConfig.getInstance().getBoolean("persistent_notification_for_tileservice").toString()

    }
}
