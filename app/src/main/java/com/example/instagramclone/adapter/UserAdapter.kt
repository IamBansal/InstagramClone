package com.example.instagramclone.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.loader.R
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

class UserAdapter(private var context: Context, private var mUsers : ArrayList<User>, private var isFollowed : Boolean)
    : RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    var firebaseUser : FirebaseUser? = null

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        val imageProfile : CircleImageView = itemView.findViewById(com.example.instagramclone.R.id.image_profile)
        val username : TextView = itemView.findViewById(com.example.instagramclone.R.id.usernameItem)
        val nameItem : TextView = itemView.findViewById(com.example.instagramclone.R.id.nameItem)
        val btnFollow : Button = itemView.findViewById(com.example.instagramclone.R.id.btnFollow)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout = LayoutInflater.from(context).inflate(com.example.instagramclone.R.layout.user_item, parent, false)
        return ViewHolder(layout)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        val user : User = mUsers[position]
        holder.btnFollow.visibility = View.VISIBLE
        holder.username.text = user.Username
        holder.nameItem.text = user.Name

        Picasso.get().load(user.imageUrl).placeholder(com.example.instagramclone.R.mipmap.ic_launcher).into(holder.imageProfile)

        isFollowed(user.id, holder.btnFollow)

        if (user.id.equals(firebaseUser!!.uid)) {
            holder.btnFollow.visibility = View.GONE
        }

        holder.btnFollow.setOnClickListener {
            if (holder.btnFollow.text.toString() == "Follow") {
                FirebaseDatabase.getInstance().reference.child("Follow").child(firebaseUser!!.uid)
                    .child("Following").child(user.id!!).setValue(true)

                FirebaseDatabase.getInstance().reference.child("Follow").child(user.id!!)
                    .child("Followers").child(firebaseUser!!.uid).setValue(true)
            } else {
                FirebaseDatabase.getInstance().reference.child("Follow").child(firebaseUser!!.uid)
                    .child("Following").child(user.id!!).removeValue()

                FirebaseDatabase.getInstance().reference.child("Follow").child(user.id!!)
                    .child("Followers").child(firebaseUser!!.uid).removeValue()
            }
        }

    }

    private fun isFollowed(id: String?, btnFollow: Button) {

        val ref = FirebaseDatabase.getInstance().reference.child("Follow").child(firebaseUser!!.uid).child("Following")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {
                    btnFollow.text = "Following"
                } else {
                    btnFollow.text = "Follow"
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    override fun getItemCount(): Int {
        return mUsers.size
    }
}