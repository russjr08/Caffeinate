package xyz.omnicron.caffeinate

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import android.text.method.LinkMovementMethod
import kotlinx.android.synthetic.main.content_credits.*
import xyz.omnicron.caffeinate.adapters.CreditsAdapter
import xyz.omnicron.caffeinate.models.Language

class CreditsActivity : AppCompatActivity() {

    private var languages = mutableListOf<Language>()
    private var adapter = CreditsAdapter(languages)

    // TODO: Reimplement credits back into Application
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credits)
        setSupportActionBar(toolbar)

        text_contrib_invitation.movementMethod = LinkMovementMethod.getInstance()

        refresh_holder.isEnabled = false
        refresh_holder.isRefreshing = false

        credits_contrib_list.layoutManager =
            LinearLayoutManager(this)
        credits_contrib_list.adapter = adapter


    }

}
