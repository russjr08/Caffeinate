package xyz.omnicron.caffeinate

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkAndWarnIfNoNotifications()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val inflater = menuInflater

        inflater.inflate(R.menu.main_activity, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val intent: Intent

        when(item.itemId) {
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

    fun checkAndWarnIfNoNotifications() {

        val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            isGranted: Boolean ->
                if(!isGranted) {
                    // TODO: Explain to the user that their experience may be degraded without a status notification
                }
        }

        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED -> {
                // Notification permissions have already been granted, we're all set!
            }

            // TODO: Build/Implement Android's "Rationale/Reason for permission request" recommendation

            else -> {
                val mainActivityView = findViewById<LinearLayout>(R.id.main_activity_root)
                Snackbar.make(this, mainActivityView,
                    getText(R.string.notification_request_permission_warning),
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.notification_request_permission_option_allow) {
                        // Build permission request and display to the user
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            // :( - Uh oh, we shouldn't encounter this section!
                        }
                    }
                    .show()
            }
        }
    }

}
