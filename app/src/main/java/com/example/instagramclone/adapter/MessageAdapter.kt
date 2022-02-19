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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MessageAdapter(private var context: Context, private var message : ArrayList<Inbox>) : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val messageText : TextView = itemView.findViewById(R.id.messageChat)
        val sentByText : TextView = itemView.findViewById(R.id.sentByChat)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.message_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val msg = message[position]
        holder.messageText.text = msg.textMessage.toString()
        getUser(holder.sentByText, msg.byUserId.toString())
    }

    private fun getUser(sentByText: TextView, byUserId: String) {
        FirebaseDatabase.getInstance().reference.child("Users").child(byUserId).addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user : User? = snapshot.getValue(User::class.java)
                if (byUserId == FirebaseAuth.getInstance().currentUser!!.uid){
                    sentByText.text = "by you"
                } else {
                    sentByText.text = "by ${user?.Username}"
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    override fun getItemCount(): Int {
        return message.size
    }

}