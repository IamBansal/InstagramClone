package com.example.instagramclone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramclone.adapter.InboxAdapter
import com.example.instagramclone.model.Inbox
import com.example.instagramclone.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class InboxActivity : AppCompatActivity() {

    private var recyclerViewInbox: RecyclerView? = null
    private var inboxAdapter: InboxAdapter? = null
    private var inboxList: ArrayList<Inbox>? = null
    private var firebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inbox)

        getToolbar()

        firebaseUser = FirebaseAuth.getInstance().currentUser

        recyclerViewInbox = findViewById(R.id.recyclerViewInbox)
        inboxList = ArrayList()
        inboxAdapter = InboxAdapter(this, inboxList!!)
        recyclerViewInbox?.setHasFixedSize(true)
        recyclerViewInbox?.layoutManager = LinearLayoutManager(this)
        recyclerViewInbox?.adapter = inboxAdapter

        getChats()

    }

    private fun getChats() {
        FirebaseDatabase.getInstance().reference.child("Chats").child(firebaseUser!!.uid).addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                inboxList?.clear()
                for (snaps in snapshot.children){
                    val item : Inbox? = snaps.getValue(Inbox::class.java)
                    inboxList?.add(item!!)
                }
                inboxAdapter?.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun getToolbar() {
        FirebaseDatabase.getInstance().reference.child("Users").child(FirebaseAuth.getInstance().currentUser!!.uid)
            .addValueEventListener(object  :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user : User? = snapshot.getValue(User::class.java)
                val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbarInbox)
                setSupportActionBar(toolbar)
                supportActionBar?.title = user?.Username
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