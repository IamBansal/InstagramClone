package com.example.instagramclone.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramclone.ChatActivity
import com.example.instagramclone.R
import com.example.instagramclone.fragments.ProfileUserFragment
import com.example.instagramclone.model.Inbox
import com.example.instagramclone.model.User
import com.google.firebase.auth.FirebaseAuth
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

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("toUserId", inboxChat.toUserId.toString())
            context.startActivity(intent)
        }

//        holder.profileInbox.setOnClickListener {
//            context.getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit()
//                .putString("profileID", inboxChat.toUserId).apply()
//            (context as FragmentActivity).supportFragmentManager.beginTransaction()
//                .replace(R.id.fragment_container, ProfileUserFragment()).commit()
//        }

        holder.itemView.setOnLongClickListener {
        val alert = AlertDialog.Builder(context)
        alert.setTitle("Delete Chat Requested!!")
            .setMessage("You sure you want to delete chats with the user?\nIt will delete the chats for the user too.")
            .setPositiveButton("Yes, Delete!!"){_,_->
                FirebaseDatabase.getInstance().reference.child("Chats").child(FirebaseAuth.getInstance().currentUser!!.uid).child(inboxChat.toUserId!!).removeValue()
            }
            .setNegativeButton("No"){_,_->}
            .create()
            .show()
            return@setOnLongClickListener true
        }

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