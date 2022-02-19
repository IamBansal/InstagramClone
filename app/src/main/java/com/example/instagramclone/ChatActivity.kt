package com.example.instagramclone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramclone.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatActivity : AppCompatActivity() {

    private var recyclerViewChats : RecyclerView? = null
    private var messageEt : EditText? = null
    private var send : TextView? = null
    private var firebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val toUserId = intent.getStringExtra("toUserId")
        getToolbar(toUserId.toString())

        recyclerViewChats = findViewById(R.id.recyclerViewChat)
        messageEt = findViewById(R.id.etMessage)
        send = findViewById(R.id.sendMsg)
        firebaseUser = FirebaseAuth.getInstance().currentUser

        send?.setOnClickListener {
            val msgText = messageEt?.text.toString()
            if (TextUtils.isEmpty(msgText)){
                Toast.makeText(this, "No message added.\nWrite a message first to send something.", Toast.LENGTH_SHORT).show()
            } else {
                val map = HashMap<String, Any>()
                map["toUserId"] = toUserId.toString()
                map["byUserId"] = firebaseUser!!.uid
                map["textMessage"] = msgText
                FirebaseDatabase.getInstance().reference.child("Chats").child(firebaseUser!!.uid).child(toUserId!!).setValue(map).addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        messageEt?.text?.clear()
                    } else {
                        Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }

    private fun getToolbar(toUserId: String) {
        FirebaseDatabase.getInstance().reference.child("Users").child(toUserId)
            .addValueEventListener(object  : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user : User? = snapshot.getValue(User::class.java)
                    val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbarChat)
                    setSupportActionBar(toolbar)
                    supportActionBar?.title = user?.Name
                    supportActionBar?.setDisplayHomeAsUpEnabled(true)
                    toolbar.setNavigationOnClickListener {
                        finish()
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

}