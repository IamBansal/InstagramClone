package com.example.instagramclone

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramclone.adapter.UserAdapter
import com.example.instagramclone.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FollowersActivity : AppCompatActivity() {

    private var id : String? = null
    private var postId : String? = null
    private var title : String? = null
    private var idList : ArrayList<String>? = null
    private var recyclerView : RecyclerView? = null
    private var userAdapter : UserAdapter? = null
    private var mUsers : ArrayList<User>? = null
    private var toolbar : Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_followers)

        postId = intent.getStringExtra("postId")
        id = intent.getStringExtra("id")
        title = intent.getStringExtra("title")

        toolbar = findViewById(R.id.toolbarF)
        recyclerView = findViewById(R.id.recyclerViewF)
        mUsers = ArrayList()
        idList = ArrayList()
        userAdapter = UserAdapter(this, mUsers!!, false)

        setSupportActionBar(toolbar)
        supportActionBar?.title = title
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar?.setNavigationOnClickListener {
            finish()
        }

        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(this)
        recyclerView?.adapter = userAdapter

        when(title){
            "Followers" -> {
                getFollowers()
            }
            "Followings" -> {
                getFollowings()
            }
            "Likes" -> {
                getLikes()
            }
        }

    }

    //To get list of followers of the user
    private fun getFollowers() {
        FirebaseDatabase.getInstance().reference.child("Follow").child(id!!).child("Followers").addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                idList?.clear()
                for (dataSnaps in snapshot.children){
                    idList?.add(dataSnaps.key.toString())
                }
                showUsers()
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    //To get list of users whom the user is following.
    private fun getFollowings() {
        FirebaseDatabase.getInstance().reference.child("Follow").child(id!!).child("Following").addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                idList?.clear()
                for (dataSnaps in snapshot.children){
                    idList?.add(dataSnaps.key.toString())
                }
                showUsers()
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    //To show the users whether Followers, followings or likers.
    private fun showUsers() {
        FirebaseDatabase.getInstance().reference.child("Users").addValueEventListener(object :ValueEventListener{
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                mUsers?.clear()
                for (dataSnaps in snapshot.children){
                    val user = dataSnaps.getValue(User::class.java)
                    for (id in idList!!){
                        if (user?.id.equals(id)){
                            mUsers?.add(user!!)
                        }
                    }
                }
                userAdapter?.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    //For getting list of the users who liked the post of the user.
    private fun getLikes() {
        FirebaseDatabase.getInstance().reference.child("Likes").child(postId!!).addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                idList?.clear()
                for (dataSnaps in snapshot.children){
                    idList?.add(dataSnaps.key.toString())
                }
                showUsers()
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}