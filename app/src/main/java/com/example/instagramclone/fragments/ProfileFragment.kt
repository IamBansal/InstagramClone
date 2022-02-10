package com.example.instagramclone.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramclone.EditProfileActivity
import com.example.instagramclone.R
import com.example.instagramclone.adapter.PhotoAdapter
import com.example.instagramclone.model.Post
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
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    //Widgets used in fragment
    private var usernameProfile : TextView? = null
    private var nameProfile : TextView? = null
    private var bioProfile : TextView? = null
    private var imagePProfile : CircleImageView? = null
    private var postsProfile : TextView? = null
    private var followersProfile : TextView? = null
    private var followingsProfile : TextView? = null
    private var options : ImageView? = null
    private var myPictures : ImageButton? = null
    private var savedPictures : ImageButton? = null
    private var editProfile : Button? = null
    private var recyclerViewMyPictures : RecyclerView? = null
    private var photoAdapter : PhotoAdapter? = null
    private var myPhotoList : ArrayList<Post>? = null
    private var recyclerViewSavedPics : RecyclerView? = null
    private var photoAdapterSaves : PhotoAdapter? = null
    private var mySavedPhotoList : ArrayList<Post>? = null
    private var firebaseUser : FirebaseUser? = null
    private var profileId : String? = null

    override fun onDestroyView() {
        super.onDestroyView()
        firebaseUser = FirebaseAuth.getInstance().currentUser
        profileId = firebaseUser!!.uid
    }

    override fun onDetach() {
        super.onDetach()
        firebaseUser = FirebaseAuth.getInstance().currentUser
        profileId = firebaseUser!!.uid
    }

    override fun onDestroy() {
        super.onDestroy()
        firebaseUser = FirebaseAuth.getInstance().currentUser
        profileId = firebaseUser!!.uid
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val layout =  inflater.inflate(R.layout.fragment_profile, container, false)

        //on again getting to profile it is not showing the profile for me.
        val data = context?.getSharedPreferences("PROFILE", Context.MODE_PRIVATE)!!.getString("profileID", "none")

        onDetach()

        firebaseUser = FirebaseAuth.getInstance().currentUser
        profileId = if (data.equals("none")){
            firebaseUser!!.uid
        } else {
            data
        }

        usernameProfile = layout.findViewById(R.id.username_profile)
        nameProfile = layout.findViewById(R.id.nameProfile)
        bioProfile = layout.findViewById(R.id.bioProfile)
        imagePProfile = layout.findViewById(R.id.imageOnProfile)
        postsProfile = layout.findViewById(R.id.postsProfile)
        followersProfile = layout.findViewById(R.id.followers)
        followingsProfile = layout.findViewById(R.id.followings)
        options = layout.findViewById(R.id.optionsProfile)
        myPictures = layout.findViewById(R.id.ibMyPics)
        savedPictures = layout.findViewById(R.id.ibSaved)
        editProfile = layout.findViewById(R.id.btnEditProfile)
        recyclerViewMyPictures = layout.findViewById(R.id.recyclerviewMyPics)
        recyclerViewSavedPics = layout.findViewById(R.id.recyclerviewSaved)

        myPhotoList = ArrayList()
        photoAdapter = PhotoAdapter(requireContext(), myPhotoList!!)
        recyclerViewMyPictures?.setHasFixedSize(true)
        recyclerViewMyPictures?.layoutManager = GridLayoutManager(requireContext(), 3)
        recyclerViewMyPictures?.adapter = photoAdapter

        mySavedPhotoList = ArrayList()
        photoAdapterSaves = PhotoAdapter(requireContext(), mySavedPhotoList!!)
        recyclerViewSavedPics?.setHasFixedSize(true)
        recyclerViewSavedPics?.layoutManager = GridLayoutManager(requireContext(), 3)
        recyclerViewSavedPics?.adapter = photoAdapterSaves

        userInfo()
        getFollowersAndFollowings()
        getPostCount()
        myPhotos()
        getSavedPosts()

        if (profileId.equals(firebaseUser?.uid)) {
            editProfile?.text = "Edit Profile"
        } else {
            checkFollowingStatus()
        }

        editProfile?.setOnClickListener {
            val btnText = editProfile?.text.toString()
            if (btnText == "Edit Profile"){
                startActivity(Intent(context, EditProfileActivity::class.java))
            } else {
                //To follow if it shows follow.
                if (btnText == "Follow"){
                    FirebaseDatabase.getInstance().reference.child("Follow").child(firebaseUser!!.uid)
                        .child("Following").child(profileId.toString()).setValue(true)
                    FirebaseDatabase.getInstance().reference.child("Follow").child(profileId.toString())
                        .child("Followers").child(firebaseUser!!.uid).setValue(true)
                }
                //to unfollow if it shows following
                else {
                    FirebaseDatabase.getInstance().reference.child("Follow").child(firebaseUser!!.uid)
                        .child("Following").child(profileId.toString()).removeValue()
                    FirebaseDatabase.getInstance().reference.child("Follow").child(profileId.toString())
                        .child("Followers").child(firebaseUser!!.uid).removeValue()
                }
            }
        }

        recyclerViewMyPictures?.visibility = View.VISIBLE
        recyclerViewSavedPics?.visibility = View.GONE

        myPictures?.setOnClickListener {
            recyclerViewMyPictures?.visibility = View.VISIBLE
            recyclerViewSavedPics?.visibility = View.GONE
        }
        savedPictures?.setOnClickListener {
            recyclerViewMyPictures?.visibility = View.GONE
            recyclerViewSavedPics?.visibility = View.VISIBLE
        }

        return layout
    }

    //to get the saved posts
    private fun getSavedPosts() {
        val savedIds = ArrayList<String>()
        FirebaseDatabase.getInstance().reference.child("Saves").child(firebaseUser!!.uid).addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                //To get all posts in saved.
                for (dataSnaps in snapshot.children){
                    savedIds.add(dataSnaps.key.toString())
                }

                //To check which post to show now.
                FirebaseDatabase.getInstance().reference.child("Posts").addValueEventListener(object :ValueEventListener{
                    override fun onDataChange(snapshot1: DataSnapshot) {
                        mySavedPhotoList?.clear()
                        for (snap in snapshot1.children){
                            val post = snap.getValue(Post::class.java)
                            for (id in savedIds){
                                if (post?.postId.equals(id)){
                                    mySavedPhotoList?.add(post!!)
                                }
                            }
                        }
                        photoAdapterSaves?.notifyDataSetChanged()
                    }
                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    //To get current user's posts.
    private fun myPhotos() {
        FirebaseDatabase.getInstance().reference.child("Posts").addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                myPhotoList?.clear()
                for (dataSnapshot in snapshot.children){
                    val post = dataSnapshot.getValue(Post::class.java)
                    if(post?.publisher.equals(profileId)){
                        myPhotoList?.add(post!!)
                    }
                }
                Collections.reverse(myPhotoList)
                photoAdapter?.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    //to check if current user is following the user or not.
    private fun checkFollowingStatus() {
        FirebaseDatabase.getInstance().reference.child("Follow").child(firebaseUser!!.uid)
            .child("Following").addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.child(profileId.toString()).exists()){
                        editProfile?.text = "Following"
                    } else {
                        editProfile?.text = "Follow"
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

    }

    //To get post count
    private fun getPostCount() {
        FirebaseDatabase.getInstance().reference.child("Posts").addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var count = 0
                for (dataSnapshot in snapshot.children) {
                    val post = dataSnapshot.getValue(Post::class.java)
                    if (post?.publisher.equals(profileId)){
                        count++
                    }
                }
                postsProfile?.text = count.toString()
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    //to get number of followers and followings.
    private fun getFollowersAndFollowings() {
        val ref = FirebaseDatabase.getInstance().reference.child("Follow").child(profileId!!)
            //To get number of followers.
            ref.child("Followers").addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    followersProfile?.text = snapshot.childrenCount.toString()
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

            //To get number of followings.
            ref.child("Following").addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    followingsProfile?.text = snapshot.childrenCount.toString()
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })


    }

    //To get userinfo
    private fun userInfo() {

        FirebaseDatabase.getInstance().reference.child("Users").child(profileId!!).addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                if (user?.imageUrl.equals("default")) {
                    imagePProfile?.setImageResource(R.drawable.ic_baseline_person_24)
                } else {
                    Picasso.get().load(user?.imageUrl).into(imagePProfile)
                }
                usernameProfile?.text = user?.Username
                nameProfile?.text = user?.Name
                bioProfile?.text = user?.Bio
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}