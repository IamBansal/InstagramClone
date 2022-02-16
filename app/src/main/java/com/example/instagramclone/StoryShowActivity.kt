package com.example.instagramclone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.example.instagramclone.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class StoryShowActivity : AppCompatActivity() {

    private var post : ImageView? = null
    private var profile : CircleImageView? = null
    private var username : TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story_show)

        post = findViewById(R.id.ivStory)
        profile = findViewById(R.id.profileStory)
        username = findViewById(R.id.usernameStory)

        val userIdFromIntent = intent.getStringExtra("user")

        Handler().postDelayed({finish()}, 5000)

        post?.setOnClickListener {
            finish()
        }

        getUser(userIdFromIntent)

    }

    //To get the user
    private fun getUser(id : String?) {
        FirebaseDatabase.getInstance().reference.child("Users").child(id!!).addValueEventListener(object  : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                if (user?.imageUrl.equals("default")){
                    Picasso.get().load(R.drawable.ic_baseline_person_24).placeholder(R.drawable.ic_baseline_person_24).into(profile)
                } else {
                    Picasso.get().load(user?.imageUrl).placeholder(R.drawable.ic_baseline_person_24).into(profile)
                }
                username?.text = user?.Username
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}