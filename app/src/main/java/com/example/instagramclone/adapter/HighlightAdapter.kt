package com.example.instagramclone.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramclone.HIghlightShowActivity
import com.example.instagramclone.R
import com.example.instagramclone.model.Highlight
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class HighlightAdapter(private var context: Context, private var highlights : ArrayList<Highlight>) : RecyclerView.Adapter<HighlightAdapter.ViewHolder>() {
    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val image : CircleImageView = itemView.findViewById(R.id.ciStory)
        val usernameS : TextView = itemView.findViewById(R.id.textStory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.story_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val highlight = highlights[position]
        Picasso.get().load(highlight.imageUrl).placeholder(R.drawable.ic_baseline_cloud_download_24).into(holder.image)
        holder.usernameS.text = highlight.highlightText

        holder.itemView.setOnClickListener{
            val intent = Intent(context, HIghlightShowActivity::class.java)
            intent.putExtra("userH", highlight.publisher.toString())
            intent.putExtra("highlightId", highlight.highlightId.toString())
            intent.putExtra("highlightText", highlight.highlightText.toString())
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return highlights.size
    }
}