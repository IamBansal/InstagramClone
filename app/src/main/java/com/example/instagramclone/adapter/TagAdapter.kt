package com.example.instagramclone.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramclone.R

class TagAdapter(private var context: Context, private var mTags : ArrayList<String>, private var mTagsCount : ArrayList<String>)
    : RecyclerView.Adapter<TagAdapter.ViewHolder>(){


        class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
            val tag : TextView = itemView.findViewById(R.id.hashtag)
            val noOfPosts : TextView = itemView.findViewById(R.id.no_of_posts)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.tag_item, parent, false))

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.tag.text = "#${mTags[position]}"
        holder.noOfPosts.text = "${mTagsCount[position]} posts"

    }

    override fun getItemCount(): Int {
        return mTags.size
    }

    fun filter(filterTags : ArrayList<String>, filterTagsCount : ArrayList<String>) {
        this.mTags = filterTags
        this.mTagsCount = filterTagsCount
    }

}