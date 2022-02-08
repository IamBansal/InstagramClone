package com.example.instagramclone

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramclone.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*
import kotlin.collections.HashMap

class CommentActivity : AppCompatActivity() {

    private var addComment : EditText? = null
    private var postComment : TextView? = null
    private var profileComment : CircleImageView? = null
    private var recyclerViewComments : RecyclerView? = null
    private var postId : String? = null
    private var author : String? = null
    private var firebaseUser : FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbarComment)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Comments"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        addComment = findViewById(R.id.AddComment)
        postComment = findViewById(R.id.PostComment)
        profileComment = findViewById(R.id.profileComment)
        recyclerViewComments = findViewById(R.id.recyclerViewComments)
        firebaseUser = FirebaseAuth.getInstance().currentUser

        val intent = intent
        postId = intent.getStringExtra("postId")
        author = intent.getStringExtra("authorId")

        getUserImage()

        postComment?.setOnClickListener {
            if (TextUtils.isEmpty(addComment?.text)) {
                Toast.makeText(this, "No comment added.", Toast.LENGTH_SHORT).show()
            } else {
                putComment()
            }
        }

    }

    //To upload the comment.
    private fun putComment() {

        val pd = ProgressDialog(this)
        pd.setMessage("Uploading the comment...")
        pd.show()

        val map = HashMap<String, String>()
        map["Comment"] = addComment?.text.toString()
        map["publisher"] = firebaseUser!!.uid

        FirebaseDatabase.getInstance().reference.child("Comments").child(postId.toString())
            .push().setValue(map).addOnCompleteListener { task ->
                pd.dismiss()
                if (task.isSuccessful) {
                    Toast.makeText(this, "Comment Added.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }

    }

    private fun getUserImage() {

        FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                if (user?.imageUrl.equals("default")){
                    profileComment?.setImageResource(R.drawable.ic_baseline_person_24)
                } else {
                    Picasso.get().load(user?.imageUrl).into(profileComment)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }
}