package com.example.instagramclone.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramclone.HomePageActivity
import com.example.instagramclone.R
import com.example.instagramclone.model.Comment
import com.example.instagramclone.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class CommentAdapter(private var context: Context, private var mComments : ArrayList<Comment>, private var postId : String) : RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    var firebaseUser : FirebaseUser? = null

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val usernameComment : TextView = itemView.findViewById(R.id.username_comment)
        val commentText : TextView = itemView.findViewById(R.id.comment_text)
        val profileComment : CircleImageView = itemView.findViewById(R.id.profileCommentItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.comment_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val comment = mComments[position]
        firebaseUser = FirebaseAuth.getInstance().currentUser

        holder.commentText.text = comment.Comment

        FirebaseDatabase.getInstance().reference.child("Users").
        child(comment.publisher.toString()).addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                holder.usernameComment.text = user?.Username
                if (user?.imageUrl.equals("default")) {
                    holder.profileComment.setImageResource(R.drawable.ic_baseline_person_24)
                } else {
                    Picasso.get().load(user?.imageUrl).into(holder.profileComment)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        //To delete the comment.
        holder.itemView.setOnLongClickListener{

            if (comment.publisher!!.endsWith(firebaseUser!!.uid)){
                val alertDialog = AlertDialog.Builder(context)
                    .setTitle("Delete Comment!!")
                    .setMessage("You want to delete it??")
                    .setPositiveButton("Yes"){_,_,->
                        FirebaseDatabase.getInstance().reference.child("Comments").child(postId)
                            .child(comment.id.toString()).removeValue().addOnCompleteListener { task ->
                            if (task.isSuccessful){
                                Toast.makeText(context, "Comment deleted.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    .setNegativeButton("No"){_,_-> }
                    .create()
                    .show()
            }

            return@setOnLongClickListener true
        }

        holder.commentText.setOnClickListener {
            val intent = Intent(context, HomePageActivity::class.java)
            intent.putExtra("publisherId", comment.publisher)
            context.startActivity(intent)
        }

        holder.profileComment.setOnClickListener {
            val intent = Intent(context, HomePageActivity::class.java)
            intent.putExtra("publisherId", comment.publisher)
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return mComments.size
    }

}