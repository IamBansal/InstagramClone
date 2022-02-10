package com.example.instagramclone.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramclone.CommentActivity
import com.example.instagramclone.R
import com.example.instagramclone.fragments.ProfileFragment
import com.example.instagramclone.model.Post
import com.example.instagramclone.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hendraanggrian.appcompat.widget.SocialTextView
import com.squareup.picasso.Picasso
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class PostAdapter(private var context: Context, private var mPosts: ArrayList<Post>) :
    RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    private var firebaseUser: FirebaseUser? = null

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val profilePost: ImageView = itemView.findViewById(R.id.profile_image_post)
        val postImage: ImageView = itemView.findViewById(R.id.post_image)
        val like: ImageView = itemView.findViewById(R.id.like)
        val comment: ImageView = itemView.findViewById(R.id.comment)
        val more: ImageView = itemView.findViewById(R.id.more)
        val save: ImageView = itemView.findViewById(R.id.save)
        val share: ImageView = itemView.findViewById(R.id.share)
        val usernamePost: TextView = itemView.findViewById(R.id.username_post)
        val noOfLikes: TextView = itemView.findViewById(R.id.no_of_likes)
        val author: TextView = itemView.findViewById(R.id.author)
        val noOfComments: TextView = itemView.findViewById(R.id.no_of_comments)
        val descriptionPost: SocialTextView = itemView.findViewById(R.id.description_post)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.post_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        firebaseUser = FirebaseAuth.getInstance().currentUser
        val post: Post = mPosts[position]
        Picasso.get().load(post.imageUrl).into(holder.postImage)

        holder.descriptionPost.text = post.description

        FirebaseDatabase.getInstance().reference.child("Users").child(post.publisher!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)

                    if (user?.imageUrl.equals("default")) {
                        holder.profilePost.setImageResource(R.drawable.ic_baseline_person_24)
                    } else {
                        Picasso.get().load(user?.imageUrl).into(holder.profilePost)
                    }
                    holder.usernamePost.text = user?.Username
                    holder.author.text = user?.Name
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

        isLiked(post.postId.toString(), holder.like)
        noOfLikes(post.postId.toString(), holder.noOfLikes)
        getNumberOfComments(post.postId.toString(), holder.noOfComments)
        isSaved(post.postId.toString(), holder.save)

        //To like or unlike a post.
        holder.like.setOnClickListener {
            if (holder.like.tag.equals("Like")) {
                FirebaseDatabase.getInstance().reference.child("Likes")
                    .child(post.postId.toString()).child(firebaseUser!!.uid).setValue(true)
                addNotification(post.postId.toString(), post.publisher.toString())
            } else {
                FirebaseDatabase.getInstance().reference.child("Likes")
                    .child(post.postId.toString()).child(firebaseUser!!.uid).removeValue()
            }
        }

        holder.comment.setOnClickListener {
            val intent = Intent(context, CommentActivity::class.java)
            intent.putExtra("postId", post.postId)
            intent.putExtra("authorId", post.publisher)
            context.startActivity(intent)
        }

        holder.noOfComments.setOnClickListener {
            val intent = Intent(context, CommentActivity::class.java)
            intent.putExtra("postId", post.postId)
            intent.putExtra("authorId", post.publisher)
            context.startActivity(intent)
        }

        //to save or un-save a post.
        holder.save.setOnClickListener {
            if (holder.save.tag == "Save"){
                FirebaseDatabase.getInstance().reference.child("Saves").child(firebaseUser!!.uid).
                    child(post.postId.toString()).setValue(true)
            }else {
                FirebaseDatabase.getInstance().reference.child("Saves").child(firebaseUser!!.uid).
                child(post.postId.toString()).removeValue()
            }
        }

        holder.profilePost.setOnClickListener {
            context.getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit().putString("profileID", post.publisher).apply()
            (context as FragmentActivity).supportFragmentManager.beginTransaction().replace(R.id.fragment_container, ProfileFragment()).commit()
        }

        holder.usernamePost.setOnClickListener {
            context.getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit().putString("profileID", post.publisher).apply()
            (context as FragmentActivity).supportFragmentManager.beginTransaction().replace(R.id.fragment_container, ProfileFragment()).commit()
        }

        holder.author.setOnClickListener {
            context.getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit().putString("profileID", post.publisher).apply()
            (context as FragmentActivity).supportFragmentManager.beginTransaction().replace(R.id.fragment_container, ProfileFragment()).commit()
        }

    }

    //To add notification on the liking post.
    private fun addNotification(postId: String, publisher: String) {
        val map = HashMap<String, Any>()
        map["isPost"] = true
        map["postId"] = postId
        map["text"] = "Liked your post."
        map["userId"] = publisher
        FirebaseDatabase.getInstance().reference.child("Notifications").child(firebaseUser!!.uid).push().setValue(map)
    }

    //To check if post is saved or not.
    private fun isSaved(postID: String, save: ImageView) {
        FirebaseDatabase.getInstance().reference.child("Saves").child(firebaseUser!!.uid).addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.child(postID).exists()){
                    save.setImageResource(R.drawable.ic_baseline_bookmark_24)
                    save.tag = "Saved"
                } else {
                    save.setImageResource(R.drawable.ic_baseline_turned_in_not_24)
                    save.tag = "Save"
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    //To get the number of comments.
    private fun getNumberOfComments(postId: String, text: TextView) {
        FirebaseDatabase.getInstance().reference.child("Comments").child(postId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    text.text = "View all ${snapshot.childrenCount} comments"
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    //for checking if post is liked or not by the user.
    private fun isLiked(postId: String, image: ImageView) {
        FirebaseDatabase.getInstance().reference.child("Likes").child(postId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.child(firebaseUser!!.uid).exists()) {
                        image.setImageResource(R.drawable.ic_liked)
                        image.tag = "Liked"
                    } else {
                        image.setImageResource(R.drawable.ic_baseline_favorite_border_24)
                        image.tag = "Like"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    //For displaying number of likes of post.
    private fun noOfLikes(postId: String, text: TextView) {
        FirebaseDatabase.getInstance().reference.child("Likes").child(postId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    text.text = "${snapshot.childrenCount} likes"
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    override fun getItemCount(): Int {
        return mPosts.size
    }


}