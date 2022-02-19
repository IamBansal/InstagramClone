package com.example.instagramclone.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramclone.AddHighlightActivity
import com.example.instagramclone.R
import com.example.instagramclone.model.Story
import com.example.instagramclone.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import com.example.instagramclone.HomePageActivity

class StoryShowAdapter(private var context: Context, private var storyUser: ArrayList<Story>) :
    RecyclerView.Adapter<StoryShowAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val postImage: ImageView = itemView.findViewById(R.id.ivStoryItem)
        val usernameSI: TextView = itemView.findViewById(R.id.usernameStoryItem)
        val profileS: CircleImageView = itemView.findViewById(R.id.profileStoryItem)
        val pause: Button = itemView.findViewById(R.id.pauseStoryItem)
        val more: ImageView = itemView.findViewById(R.id.StoryItemOptions)
        val deleteImage: ImageView = itemView.findViewById(R.id.StoryItemOptionsDelete)
        val delete: TextView = itemView.findViewById(R.id.deleteStory)
        val highlight: ImageView = itemView.findViewById(R.id.addHighlight)
        val unfollow: TextView = itemView.findViewById(R.id.unfollowMoreStory)
        val ll: LinearLayout = itemView.findViewById(R.id.llMoreStory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.story_show_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val story = storyUser[position]
        getUser(holder.profileS, holder.usernameSI, story.publisher.toString())

        //To get story image.
        FirebaseDatabase.getInstance().reference.child("Story").child(story.publisher.toString())
            .child(story.storyId.toString()).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val item = snapshot.getValue(Story::class.java)
                    if (item?.imageUrl.equals("")) {
                        holder.postImage.setImageResource(R.drawable.d)
                    } else {
                        Picasso.get().load(item?.imageUrl)
                            .placeholder(R.drawable.ic_baseline_cloud_download_24)
                            .into(holder.postImage)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

        //To show options on clicking on more.
        holder.more.setOnClickListener {
            if (holder.ll.tag.equals("Gone")) {
                holder.ll.visibility = View.VISIBLE
                holder.ll.tag = "Visible"
                holder.unfollow.visibility = View.VISIBLE
            } else {
                holder.ll.visibility = View.INVISIBLE
                holder.ll.tag = "Gone"
            }
        }

        //To decide whether to show highlight option or not.
        if (story.publisher.equals(FirebaseAuth.getInstance().currentUser!!.uid)) {
            holder.highlight.visibility = View.VISIBLE
            holder.more.visibility = View.GONE
            holder.deleteImage.visibility = View.VISIBLE
        } else {
            holder.highlight.visibility = View.GONE
            holder.more.visibility = View.VISIBLE
            holder.deleteImage.visibility = View.GONE
        }

        holder.highlight.setOnClickListener {
            val intent = Intent(context, AddHighlightActivity::class.java)
            intent.putExtra("storyIdH", story.storyId)
            intent.putExtra("storyImageUrl", story.imageUrl)
            context.startActivity(intent)
        }

        //To delete the story
        holder.deleteImage.setOnClickListener {
            val alert = AlertDialog.Builder(context)
            alert.setTitle("Story Delete Requested!!")
                .setMessage("You sure you want to delete the story?")
                .setPositiveButton("Yes, Delete") { _, _ ->
                    FirebaseDatabase.getInstance().reference.child("Story")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .child(story.storyId.toString()).removeValue()
                    holder.delete.visibility = View.INVISIBLE
                    Toast.makeText(
                        context,
                        "Story Deleted\nYou will see the story until you finish viewing it.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .setNegativeButton("No") { _, _ -> holder.delete.visibility = View.INVISIBLE }
                .create()
                .show()
        }

        //To unfollow the user (story publisher)
        holder.unfollow.setOnClickListener {
            FirebaseDatabase.getInstance().reference.child("Follow")
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                .child("Following").child(story.publisher.toString()).removeValue()

            FirebaseDatabase.getInstance().reference.child("Follow")
                .child(story.publisher.toString())
                .child("Followers").child(FirebaseAuth.getInstance().currentUser!!.uid)
                .removeValue()

            Toast.makeText(
                context,
                "User Unfollowed\nYou will see the story until you finish viewing it.",
                Toast.LENGTH_SHORT
            ).show()
            holder.unfollow.visibility = View.GONE

        }

        //To pause/unpause the story.
        holder.pause.visibility = View.INVISIBLE
        holder.pause.setOnClickListener {
//            if (holder.pause.tag!! == "NotPaused"){
//                holder.pause.tag = "Paused"
            Toast.makeText(
                context,
                "Not yet implemented!\nSorry :(\nDrag to switch story.",
                Toast.LENGTH_SHORT
            ).show()
//            } else {
//                Handler().postDelayed({
//
//                }, 5000)
//                Toast.makeText(context, "UnPaused", Toast.LENGTH_SHORT).show()
//                holder.pause.tag = "NotPaused"
//            }
        }

        //To finish the activity on clicking on story.
        holder.postImage.setOnClickListener {
            context.startActivity(Intent(context, HomePageActivity::class.java))
        }

        //To navigate to user's profile.
        holder.profileS.setOnClickListener {
            val intent = Intent(context, HomePageActivity::class.java)
            intent.putExtra("publisherId", story.publisher)
            context.startActivity(intent)
        }

        //To navigate to user's profile.
        holder.usernameSI.setOnClickListener {
            val intent = Intent(context, HomePageActivity::class.java)
            intent.putExtra("publisherId", story.publisher)
            context.startActivity(intent)
        }

    }

    //To get the user's information in story.
    private fun getUser(profile: CircleImageView, username: TextView, userId: String) {
        FirebaseDatabase.getInstance().reference.child("Users").child(userId)
            .addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)
                    if (user?.imageUrl.equals("default")) {
                        profile.setImageResource(R.drawable.ic_baseline_person_24)
                    } else {
                        Picasso.get().load(user?.imageUrl)
                            .placeholder(R.drawable.ic_baseline_person_24).into(profile)
                    }
                    username.text = user?.Username
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    override fun getItemCount(): Int {
        return storyUser.size
    }

}