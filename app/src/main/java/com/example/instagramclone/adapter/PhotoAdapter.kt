package com.example.instagramclone.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramclone.R
import com.example.instagramclone.fragments.PostDetailFragment
import com.example.instagramclone.model.Post
import com.squareup.picasso.Picasso

class PhotoAdapter(private var context: Context, private var mPhoto : ArrayList<Post>) : RecyclerView.Adapter<PhotoAdapter.ViewHolder>() {

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        var postImage : ImageView = itemView.findViewById(R.id.post)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.photo_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = mPhoto[position]
        Picasso.get().load(post.imageUrl).placeholder(R.mipmap.ic_launcher).into(holder.postImage)

        holder.postImage.setOnClickListener {
            context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit().putString("postId", post.postId).apply()
            (context as FragmentActivity).supportFragmentManager.beginTransaction().replace(R.id.fragment_container, PostDetailFragment()).commit()
        }

    }

    override fun getItemCount(): Int {
        return mPhoto.size
    }
}