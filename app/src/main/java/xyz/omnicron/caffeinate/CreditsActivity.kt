package xyz.omnicron.caffeinate

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore

import kotlinx.android.synthetic.main.activity_credits.*
import kotlinx.android.synthetic.main.content_credits.*
import xyz.omnicron.caffeinate.adapters.CreditsAdapter
import xyz.omnicron.caffeinate.models.Language

class CreditsActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private var languages = mutableListOf<Language>()
    private var adapter = CreditsAdapter(languages)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credits)
        setSupportActionBar(toolbar)

        refresh_holder.isEnabled = false
        refresh_holder.isRefreshing = true

        credits_contrib_list.layoutManager = LinearLayoutManager(this)
        credits_contrib_list.adapter = adapter

        db.collection("contrib_languages").get().addOnCompleteListener { it ->
            it.addOnCompleteListener {
                refresh_holder.isRefreshing = false
                if(it.isSuccessful) {
                    for(doc in it.result) {
                        System.out.println("${doc.id} => ${doc.data}")
                        val language = Language(doc.id, doc.get("lang_name") as String, doc.get("contributors") as List<String>)
                        languages.add(language)
                        adapter.notifyDataSetChanged()

                    }
                }
            }
        }


    }

}
