package xyz.omnicron.caffeinate

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.analytics.FirebaseAnalytics

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FirebaseAnalytics.getInstance(this) // Init analytics
        setupNotificationTestDefaults()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val inflater = menuInflater

        inflater.inflate(R.menu.main_activity, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        var intent: Intent

        when(item?.itemId) {
            R.id.menu_entry_settings -> {
                intent = Intent(applicationContext, SettingsActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_entry_credits -> {
                intent = Intent(applicationContext, CreditsActivity::class.java)
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun setupNotificationTestDefaults() {
        (application as Caffeine).updateFirebaseRemoteConfigs()

    }
}
