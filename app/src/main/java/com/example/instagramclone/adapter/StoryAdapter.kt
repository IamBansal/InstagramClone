package com.example.instagramclone.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramclone.R
import com.example.instagramclone.model.Story
import com.example.instagramclone.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class StoryAdapter(private var context: Context, private var mStories : ArrayList<Story>) : RecyclerView.Adapter<StoryAdapter.ViewHolder>() {

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val image : CircleImageView = itemView.findViewById(R.id.ciStory)
        val usernameS : TextView = itemView.findViewById(R.id.textStory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.story_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val story = mStories[position]
        getUser(holder.image, holder.usernameS, story.userId.toString())
    }

    //For getting the publisher in notification.
    private fun getUser(profile: CircleImageView, username: TextView, userId : String) {
        FirebaseDatabase.getInstance().reference.child("Users").child(userId).addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                if (user?.imageUrl.equals("default")){
                    profile.setImageResource(R.drawable.ic_baseline_person_24)
                } else {
                    Picasso.get().load(user?.imageUrl).placeholder(R.drawable.ic_baseline_person_24).into(profile)
                }
                if (user?.id.equals(FirebaseAuth.getInstance().currentUser!!.uid)){
                    username.text = "Your story"
                } else {
                    username.text = user?.Username
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }


    override fun getItemCount(): Int {
        return mStories.size
    }

}