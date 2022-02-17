package com.example.instagramclone

import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.instagramclone.adapter.StoryShowAdapter
import com.example.instagramclone.model.Story
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class StoryShowActivity : AppCompatActivity() {

    private var recyclerView: RecyclerView? = null
    private var storyShowAdapter: StoryShowAdapter? = null
    private var list: ArrayList<Story>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story_show)

        recyclerView = findViewById(R.id.rvStory)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = StaggeredGridLayoutManager(1, LinearLayout.HORIZONTAL)
        list = ArrayList()
        storyShowAdapter = StoryShowAdapter(this, list!!)
        recyclerView?.adapter = storyShowAdapter

        val userIdFromIntent = intent.getStringExtra("user")

        readStory(userIdFromIntent.toString())

    }

    //For story reading
    private fun readStory(idUser: String) {
        FirebaseDatabase.getInstance().reference.child("Story")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    list?.clear()
                    for (dataSnapshot in snapshot.children) {
                        for (dataSnapshots in dataSnapshot.children) {
                            val story: Story? = dataSnapshots.getValue(Story::class.java)
                            if (story?.publisher == idUser) {
                                list?.add(story)
                            }
                        }
                        storyShowAdapter?.notifyDataSetChanged()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

}