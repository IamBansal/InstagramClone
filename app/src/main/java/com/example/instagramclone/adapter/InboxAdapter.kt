package com.example.instagramclone.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramclone.R
import com.example.instagramclone.model.Inbox
import com.example.instagramclone.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class InboxAdapter(private var context: Context, private var inbox : ArrayList<Inbox>) : RecyclerView.Adapter<InboxAdapter.ViewHolder>() {

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val profileInbox : CircleImageView = itemView.findViewById(R.id.profileInbox)
        val usernameInbox : TextView = itemView.findViewById(R.id.nameInbox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.inbox_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val inboxChat = inbox[position]
        getToUser(holder.profileInbox, holder.usernameInbox, inboxChat.toUserId.toString())
    }

    private fun getToUser(profile :CircleImageView, username: TextView, toUserId: String) {
        FirebaseDatabase.getInstance().reference.child("Users").child(toUserId).addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user : User? = snapshot.getValue(User::class.java)
                if (user?.imageUrl.equals("default")){
                    profile.setImageResource(R.drawable.ic_baseline_person_24)
                } else {
                    Picasso.get().load(user?.imageUrl).placeholder(R.drawable.ic_baseline_person_24).into(profile)
                }
                username.text = user?.Name
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    override fun getItemCount(): Int {
        return inbox.size
    }
}