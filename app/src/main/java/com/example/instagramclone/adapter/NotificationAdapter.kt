package com.example.instagramclone.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramclone.R
import com.example.instagramclone.fragments.PostDetailFragment
import com.example.instagramclone.fragments.ProfileUserFragment
import com.example.instagramclone.model.Notification
import com.example.instagramclone.model.Post
import com.example.instagramclone.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class NotificationAdapter(private var context: Context, private var mNotifications : ArrayList<Notification>)
    : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val profileN : CircleImageView = itemView.findViewById(R.id.ProfileNotification)
        val usernameN : TextView = itemView.findViewById(R.id.usernameNotification)
        val commentN : TextView = itemView.findViewById(R.id.commentNotification)
        val postN : ImageView = itemView.findViewById(R.id.ivPostNotification)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.notification_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notification = mNotifications[position]

        getUser(holder.profileN, holder.usernameN, notification.userId.toString())
        holder.commentN.text = notification.text

        //For checking if item is related to a post ot not.
        if (!notification.postId.equals("")){
            holder.postN.visibility = View.VISIBLE
            getPostImage(holder.postN, notification.postId)
        } else {
            holder.postN.visibility = View.GONE
        }

        //when user click on the notification
        holder.itemView.setOnClickListener {
            //If notification is about a post, then redirected to post detail.
            if (!notification.postId.equals("")){
                context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit().putString("postId", notification.postId).apply()
                (context as FragmentActivity).supportFragmentManager.beginTransaction().replace(R.id.fragment_container, PostDetailFragment()).commit()
            }
            //If notification is not about a post, then redirected to profile of the publisher.
            else {
                context.getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit().putString("profileID", notification.userId).apply()
                (context as FragmentActivity).supportFragmentManager.beginTransaction().replace(R.id.fragment_container, ProfileUserFragment()).commit()
            }
        }

    }

    //For getting the post in notification.
    private fun getPostImage(postN: ImageView, postId: String?) {
        FirebaseDatabase.getInstance().reference.child("Posts").child(postId!!).addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val post = snapshot.getValue(Post::class.java)
                Picasso.get().load(post?.imageUrl).placeholder(R.drawable.ic_baseline_post_add_24).into(postN)
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    //For getting the publisher in notification.
    private fun getUser(profile: CircleImageView, username: TextView, userId : String) {
        FirebaseDatabase.getInstance().reference.child("Users").child(userId).addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                if (user?.imageUrl.equals("default")){
                    profile.setImageResource(R.drawable.ic_baseline_person_24)
                } else {
                    Picasso.get().load(user?.imageUrl).into(profile)
                }
                username.text = user?.Username
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    override fun getItemCount(): Int {
        return mNotifications.size
    }
}