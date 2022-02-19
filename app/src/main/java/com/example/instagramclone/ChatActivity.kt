package com.example.instagramclone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.instagramclone.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val toUserId = intent.getStringExtra("toUserId")

        getToolbar(toUserId.toString())

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