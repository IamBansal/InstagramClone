package com.example.instagramclone.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.instagramclone.R
import com.example.instagramclone.StoryPostActivity
import com.example.instagramclone.adapter.PostAdapter
import com.example.instagramclone.adapter.StoryAdapter
import com.example.instagramclone.model.Post
import com.example.instagramclone.model.Story
import com.example.instagramclone.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
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

    private var recyclerViewPosts: RecyclerView? = null
    private var postsList: ArrayList<Post>? = null
    private var followingList: ArrayList<String>? = null
    private var postAdapter: PostAdapter? = null

    private var recyclerViewStories: RecyclerView? = null
    private var storyList: ArrayList<Story>? = null
    private var storyAdapter: StoryAdapter? = null

    private var addStory: ImageView? = null
    private var profileStory: CircleImageView? = null
    private var relativeLayout: RelativeLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val layout = inflater.inflate(R.layout.fragment_home, container, false)

        addStory = layout.findViewById(R.id.addStory)
        profileStory = layout.findViewById(R.id.ciStoryHome)
        relativeLayout = layout.findViewById(R.id.relativeLayout)

        //to show profile
        FirebaseDatabase.getInstance().reference.child("Users")
            .child(FirebaseAuth.getInstance().currentUser!!.uid).addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userHere : User? = snapshot.getValue(User::class.java)
                    if(userHere?.imageUrl.equals("default")){
                        profileStory?.setImageResource(R.drawable.ic_baseline_person_24)
                    } else {
                        Picasso.get().load(userHere?.imageUrl).placeholder(R.drawable.ic_baseline_person_24).into(profileStory)
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

        //to check whether to show that dummy story item or not.
        FirebaseDatabase.getInstance().reference.child("Story")
            .child(FirebaseAuth.getInstance().currentUser!!.uid).addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.exists()){
                        relativeLayout?.visibility = View.VISIBLE
                    } else {
                        relativeLayout?.visibility = View.GONE
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })


        recyclerViewPosts = layout.findViewById(R.id.recyclerViewPosts)
        recyclerViewPosts?.setHasFixedSize(true)
        val llLayout = LinearLayoutManager(requireContext())

        //To get the latest entry on top.
        llLayout.stackFromEnd = true
        llLayout.reverseLayout = true
        recyclerViewPosts?.layoutManager = llLayout

        postsList = ArrayList()
        followingList = ArrayList()
        postAdapter = PostAdapter(requireContext(), postsList!!)
        recyclerViewPosts?.adapter = postAdapter

        checkFollowingUsers()

        recyclerViewStories = layout.findViewById(R.id.recyclerViewStories)
        recyclerViewStories?.setHasFixedSize(true)
        recyclerViewStories?.layoutManager =
            StaggeredGridLayoutManager(1, LinearLayoutManager.HORIZONTAL)
        storyList = ArrayList()
        storyAdapter = StoryAdapter(requireContext(), storyList!!)
        recyclerViewStories?.adapter = storyAdapter

        addStory?.setOnClickListener {
//            checkIfOnlyStory()
            startActivity(Intent(context, StoryPostActivity::class.java))
        }

        return layout
    }

    //To add story.
    private fun checkIfOnlyStory() {
        FirebaseDatabase.getInstance().reference.child("Story").child(FirebaseAuth.getInstance().currentUser!!.uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.hasChildren()) {
                    startActivity(Intent(context, StoryPostActivity::class.java))
                } else {
                    Toast.makeText(context, "Already Added One.\nWill implement more than one story post later.", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    //For story reading
    private fun readStory() {
        FirebaseDatabase.getInstance().reference.child("Story")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    storyList?.clear()
                    for (dataSnapshot in snapshot.children) {
                        var count = 0;
                        for (dataSnapshots in dataSnapshot.children) {
                            //This while loop is for showing story only once in home.
                            while (count < 1) {
                                val story: Story? = dataSnapshots.getValue(Story::class.java)
                                for (id in followingList!!) {
                                    if (story?.publisher!! == id) {
                                        if (story.publisher == FirebaseAuth.getInstance().currentUser!!.uid) {
                                            storyList?.add(0, story)
                                        } else {
                                            storyList?.add(story)
                                        }
                                    }
                                }
                                count++
                            }
                        }
                    }
                    storyAdapter?.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    //For checking those whom the current user is following.
    private fun checkFollowingUsers() {
        FirebaseDatabase.getInstance().reference.child("Follow")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("Following").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    followingList?.clear()
                    //This next line for showing own posts
                    followingList?.add(FirebaseAuth.getInstance().currentUser!!.uid)
                    for (dataSnapshot in snapshot.children) {
                        followingList?.add(dataSnapshot.key.toString())
                    }
                    readPost()
                    readStory()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    //To read the posts.
    private fun readPost() {
        FirebaseDatabase.getInstance().reference.child("Posts")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    postsList?.clear()
                    for (dataSnapshot in snapshot.children) {
                        val post: Post? = dataSnapshot.getValue(Post::class.java)
                        for (id in followingList!!) {
                            if (post?.publisher.equals(id)) {
                                postsList?.add(post!!)
                            }
                        }
                    }
                    postAdapter?.notifyDataSetChanged()
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
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}