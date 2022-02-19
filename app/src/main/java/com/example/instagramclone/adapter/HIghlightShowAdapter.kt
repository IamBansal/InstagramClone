package com.example.instagramclone.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramclone.HomePageActivity
import com.example.instagramclone.R
import com.example.instagramclone.model.Highlight
import com.example.instagramclone.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class HIghlightShowAdapter(
    private var context: Context,
    private var highlights: ArrayList<Highlight>
) : RecyclerView.Adapter<HIghlightShowAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val postImage: ImageView = itemView.findViewById(R.id.ivhItem)
        val usernameSI: TextView = itemView.findViewById(R.id.usernameHItem)
        val profileS: CircleImageView = itemView.findViewById(R.id.profileHItem)
        val pause: Button = itemView.findViewById(R.id.pauseHItem)
        val more: ImageView = itemView.findViewById(R.id.HItemOptions)
        val deleteImage: ImageView = itemView.findViewById(R.id.HItemOptionsDelete)
        val delete: TextView = itemView.findViewById(R.id.deleteHighlight)
        val unfollow: TextView = itemView.findViewById(R.id.unfollowMoreH)
        val ll: LinearLayout = itemView.findViewById(R.id.llMoreH)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.highlight_show_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val highlight = highlights[position]
        holder.usernameSI.text = highlight.highlightText
        getUser(holder.profileS, highlight.publisher.toString())

        //To get highlight image.
        FirebaseDatabase.getInstance().reference.child("Highlight")
            .child(highlight.publisher.toString())
            .child(highlight.highlightText.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (snaps in snapshot.children) {
                        val item = snaps.getValue(Highlight::class.java)
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

        if (highlight.publisher.equals(FirebaseAuth.getInstance().currentUser!!.uid)) {
            holder.more.visibility = View.GONE
            holder.deleteImage.visibility = View.VISIBLE
        } else {
            holder.more.visibility = View.VISIBLE
            holder.deleteImage.visibility = View.GONE
        }

        //To delete the highlight
        holder.deleteImage.setOnClickListener {
            val alert = AlertDialog.Builder(context)
            alert.setTitle("Highlight Delete Requested!!")
                .setMessage("You sure you want to delete the highlight?")
                .setPositiveButton("Yes, Delete") { _, _ ->
                    FirebaseDatabase.getInstance().reference.child("Highlight")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .child(highlight.highlightText.toString())
                        .child(highlight.highlightId.toString()).removeValue()
                    holder.delete.visibility = View.INVISIBLE
                    Toast.makeText(
                        context,
                        "Highlight Deleted\nYou will see the highlight until you finish viewing it.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .setNegativeButton("No") { _, _ -> holder.delete.visibility = View.INVISIBLE }
                .create()
                .show()
        }

        //To unfollow the user (highlight publisher)
        holder.unfollow.setOnClickListener {
            FirebaseDatabase.getInstance().reference.child("Follow")
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                .child("Following").child(highlight.publisher.toString()).removeValue()

            FirebaseDatabase.getInstance().reference.child("Follow")
                .child(highlight.publisher.toString())
                .child("Followers").child(FirebaseAuth.getInstance().currentUser!!.uid)
                .removeValue()

            Toast.makeText(
                context,
                "User Unfollowed\nYou will see the highlight until you finish viewing it.",
                Toast.LENGTH_SHORT
            ).show()
            holder.unfollow.visibility = View.GONE

        }

        //To pause/unpause the highlight.
        holder.pause.visibility = View.INVISIBLE
        holder.pause.setOnClickListener {
//            if (holder.pause.tag!! == "NotPaused"){
//                holder.pause.tag = "Paused"
            Toast.makeText(
                context,
                "Not yet implemented!\nSorry :(\nDrag to switch highlight.",
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

        //To finish the activity on clicking on highlight.
        holder.postImage.setOnClickListener {
            val intent = Intent(context, HomePageActivity::class.java)
            intent.putExtra("publisherId", highlight.publisher)
            context.startActivity(intent)
        }

        //To navigate to user's profile.
        holder.profileS.setOnClickListener {
            val intent = Intent(context, HomePageActivity::class.java)
            intent.putExtra("publisherId", highlight.publisher)
            context.startActivity(intent)
        }

        //To navigate to user's profile.
        holder.usernameSI.setOnClickListener {
            val intent = Intent(context, HomePageActivity::class.java)
            intent.putExtra("publisherId", highlight.publisher)
            context.startActivity(intent)
        }

    }

    //To get the user's information in highlight.
    private fun getUser(profile: CircleImageView, userId: String) {
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
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    override fun getItemCount(): Int {
        return highlights.size
    }

}