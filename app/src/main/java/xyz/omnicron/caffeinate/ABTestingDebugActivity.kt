package xyz.omnicron.caffeinate

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.remoteconfig.FirebaseRemoteConfig

class ABTestingDebugActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_abtesting_debug)

        val textview = findViewById(R.id.editText) as TextView

        (application as Caffeine).updateFirebaseRemoteConfigs {
            Toast.makeText(this@ABTestingDebugActivity, "Config values updated successfully", Toast.LENGTH_LONG).show()
            runOnUiThread { textview.text = "persistent_notification_for_tileservice: " + FirebaseRemoteConfig.getInstance().getBoolean("persistent_notification_for_tileservice").toString() }
        }

    }
}
