package com.example.instagramclone.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramclone.CommentActivity
import com.example.instagramclone.FollowersActivity
import com.example.instagramclone.R
import com.example.instagramclone.fragments.ProfileUserFragment
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
        val llMore: LinearLayout = itemView.findViewById(R.id.llMore)
        val whyPost: TextView = itemView.findViewById(R.id.whyPost)
        val unfollowMore: TextView = itemView.findViewById(R.id.unfollowMore)

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
                        Picasso.get().load(user?.imageUrl)
                            .placeholder(R.drawable.ic_baseline_person_24).into(holder.profilePost)
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

        holder.share.setOnClickListener {
            Toast.makeText(context, "Share not yet implemented buddy!\nWorking on it :)", Toast.LENGTH_SHORT).show()
        }

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

        //For showing options on clicking more icon.
        holder.more.setOnClickListener {
            if (holder.llMore.tag == "Visible") {
                holder.llMore.visibility = View.GONE
                holder.postImage.visibility = View.VISIBLE
                holder.llMore.tag = "Gone"
            } else {
                holder.llMore.visibility = View.VISIBLE
                if (post.publisher == firebaseUser!!.uid) {
                    holder.unfollowMore.visibility = View.INVISIBLE
                }
                holder.postImage.visibility = View.INVISIBLE
                holder.llMore.tag = "Visible"
            }
        }

        //To unfollow the user from post.
        holder.unfollowMore.setOnClickListener {
            FirebaseDatabase.getInstance().reference.child("Follow").child(firebaseUser!!.uid)
                .child("Following").child(post.publisher.toString()).removeValue()

            FirebaseDatabase.getInstance().reference.child("Follow")
                .child(post.publisher.toString())
                .child("Followers").child(firebaseUser!!.uid).removeValue()
        }

        //To show why current user is seeing the post
        holder.whyPost.setOnClickListener {
            val alert = AlertDialog.Builder(context)
            alert.setTitle("Why you are seeing this post?")
            if (post.publisher == firebaseUser!!.uid) {
                alert.setMessage(
                    "You are seeing this post as you are the publisher of the post."
                )
            } else {
                alert.setMessage(
                    "You are seeing this post as you must be following the publisher.\n" +
                            "Or you are viewing that user."
                )
            }
            alert.setPositiveButton("Ok, Cool!!") { _, _ ->
                holder.llMore.visibility = View.GONE
                holder.postImage.visibility = View.VISIBLE
            }
                .create()
                .show()
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
            if (holder.save.tag == "Save") {
                FirebaseDatabase.getInstance().reference.child("Saves").child(firebaseUser!!.uid)
                    .child(post.postId.toString()).setValue(true)
            } else {
                FirebaseDatabase.getInstance().reference.child("Saves").child(firebaseUser!!.uid)
                    .child(post.postId.toString()).removeValue()
            }
        }

        holder.profilePost.setOnClickListener {
            context.getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit()
                .putString("profileID", post.publisher).apply()
            (context as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ProfileUserFragment()).commit()
        }

        holder.usernamePost.setOnClickListener {
            context.getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit()
                .putString("profileID", post.publisher).apply()
            (context as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ProfileUserFragment()).commit()
        }

        holder.author.setOnClickListener {
            context.getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit()
                .putString("profileID", post.publisher).apply()
            (context as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ProfileUserFragment()).commit()
        }

        holder.noOfLikes.setOnClickListener {
            val intent = Intent(context, FollowersActivity::class.java)
            intent.putExtra("id", post.publisher)
            intent.putExtra("postId", post.postId)
            intent.putExtra("title", "Likes")
            context.startActivity(intent)
        }

    }

    //To add notification on liking the post.
    private fun addNotification(postId: String, publisher: String) {
        if (publisher != firebaseUser!!.uid) {
            val map = HashMap<String, String>()
            map["postId"] = postId
            map["text"] = "Liked your post."
            map["userId"] = firebaseUser!!.uid
            FirebaseDatabase.getInstance().reference.child("Notifications").child(publisher).push()
                .setValue(map)
        }
    }

    //To check if post is saved or not.
    private fun isSaved(postID: String, save: ImageView) {
        FirebaseDatabase.getInstance().reference.child("Saves").child(firebaseUser!!.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.child(postID).exists()) {
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
                        image.setImageResource(R.drawable.unlike)
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