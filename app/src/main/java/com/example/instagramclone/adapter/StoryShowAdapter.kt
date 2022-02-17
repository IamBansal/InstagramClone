package com.example.instagramclone.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
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
import com.example.instagramclone.HomePageActivity

class StoryShowAdapter(private var context: Context, private var storyUser : ArrayList<Story>) : RecyclerView.Adapter<StoryShowAdapter.ViewHolder>() {

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val postImage : ImageView = itemView.findViewById(R.id.ivStoryItem)
        val usernameSI : TextView = itemView.findViewById(R.id.usernameStoryItem)
        val profileS : CircleImageView = itemView.findViewById(R.id.profileStoryItem)
        val pause : Button = itemView.findViewById(R.id.pauseStoryItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.story_show_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val story = storyUser[position]
        getUser(holder.profileS, holder.usernameSI, story.publisher.toString())
        holder.pause.setOnClickListener {
//            if (holder.pause.tag!! == "NotPaused"){
//                holder.pause.tag = "Paused"
//                Toast.makeText(context, "Paused", Toast.LENGTH_SHORT).show()
//            } else {
//                Handler().postDelayed({
//
//                }, 5000)
//                Toast.makeText(context, "UnPaused", Toast.LENGTH_SHORT).show()
//                holder.pause.tag = "NotPaused"
//            }
        }

        holder.postImage.setOnClickListener {
            context.startActivity(Intent(context, HomePageActivity::class.java))
        }

    }

    private fun getUser(profile: CircleImageView, username: TextView, userId : String) {
        FirebaseDatabase.getInstance().reference.child("Users").child(userId).addValueEventListener(object :
            ValueEventListener {
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
        return storyUser.size
    }

}