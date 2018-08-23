package xyz.omnicron.caffeinate.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.lang_contrib_item.view.*
import xyz.omnicron.caffeinate.R
import xyz.omnicron.caffeinate.models.Language

class CreditsAdapter(private val data: MutableList<Language>): RecyclerView.Adapter<CreditsAdapter.CreditsViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreditsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.lang_contrib_item, parent,false) as View

        return CreditsViewHolder(itemView)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: CreditsViewHolder, position: Int) {
        holder.textLang.text = data[position].name
        for (contrib in data[position].contributors) {
            var contribs = holder.contribList.text
            if(contribs.isBlank()) {
                holder.contribList.text = "\u2022 ${contrib}"
            } else {
                holder.contribList.text = "${contribs}\n \u2022 ${contrib}"
            }
        }
    }

    class CreditsViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        val textLang = item.lang_name
        val contribList = item.text_contribs
    }

}