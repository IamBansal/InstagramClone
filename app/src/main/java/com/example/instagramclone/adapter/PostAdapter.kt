package com.example.instagramclone.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramclone.R
import com.example.instagramclone.model.Post
import com.example.instagramclone.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hendraanggrian.appcompat.widget.SocialTextView
import com.squareup.picasso.Picasso

class PostAdapter(private var context: Context, private var mPosts: ArrayList<Post>) :
    RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    private var firebaseUser: FirebaseUser? = null

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val profilePost: ImageView = itemView.findViewById(R.id.profile_image_post)
        val postImage: ImageView = itemView.findViewById(R.id.post_image)
        val like: ImageView = itemView.findViewById(R.id.like)
        val comment: ImageView = itemView.findViewById(R.id.comment)
        val more: ImageView = itemView.findViewById(R.id.more)
        val save: ImageView = itemView.findViewById(R.id.save)
        val share: ImageView = itemView.findViewById(R.id.share)
        val usernamePost: TextView = itemView.findViewById(R.id.username_post)
        val noOfLikes: TextView = itemView.findViewById(R.id.no_of_likes)
        val author: TextView = itemView.findViewById(R.id.author)
        val noOfComments: TextView = itemView.findViewById(R.id.no_of_comments)
        val descriptionPost: SocialTextView = itemView.findViewById(R.id.description_post)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.post_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        firebaseUser = FirebaseAuth.getInstance().currentUser
        val post: Post = mPosts[position]
        Picasso.get().load(post.imageUrl).into(holder.postImage)

        holder.descriptionPost.text = post.description

        FirebaseDatabase.getInstance().reference.child("Users").child(post.publisher!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)

                    if (user?.imageUrl.equals("default")) {
                        holder.profilePost.setImageResource(R.drawable.ic_baseline_person_24)
                    } else {
                        Picasso.get().load(user?.imageUrl).into(holder.profilePost)
                    }
                    holder.usernamePost.text = user?.Username
                    holder.author.text = user?.Name
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

    }

    override fun getItemCount(): Int {
        return mPosts.size
    }

}