package com.example.instagramclone.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramclone.R
import com.example.instagramclone.adapter.TagAdapter
import com.example.instagramclone.adapter.UserAdapter
import com.example.instagramclone.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchFragment : Fragment() {
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

    private var recyclerView : RecyclerView? = null
    private var mUsers : ArrayList<User>? = null
    private var userAdapter : UserAdapter? = null
    private var searchBar : SocialAutoCompleteTextView? = null


    private var recyclerViewTags : RecyclerView? = null
    private var mHashTags : ArrayList<String>? = null
    private var mHashTagsCount : ArrayList<String>? = null
    private var tagAdapter : TagAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val layout = inflater.inflate(R.layout.fragment_search, container, false)
        recyclerView = layout.findViewById(R.id.recyclerViewUsers)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(activity)

        mUsers = ArrayList()
        userAdapter = UserAdapter(requireContext(), mUsers!!, true)
        recyclerView!!.adapter = userAdapter

        recyclerViewTags = layout.findViewById(R.id.recyclerViewTags)
        recyclerViewTags!!.setHasFixedSize(true)
        recyclerViewTags!!.layoutManager = LinearLayoutManager(activity)

        mHashTags = ArrayList()
        mHashTagsCount = ArrayList()
        tagAdapter = TagAdapter(requireContext(), mHashTags!!, mHashTagsCount!!)
        recyclerViewTags!!.adapter = tagAdapter

        searchBar = layout.findViewById(R.id.search_bar)
        readUsers()
        readTags()

        searchBar?.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                TODO("Not yet implemented")
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                searchUser(p0.toString())
            }

            override fun afterTextChanged(p0: Editable?) {
                filter(p0.toString())
            }

        })

        return layout
    }

    private fun readTags() {
        FirebaseDatabase.getInstance().reference.child("Hashtags").addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                mHashTagsCount?.clear()
                mHashTags?.clear()
                for (dataSnapshot in snapshot.children) {
                    mHashTags?.add(dataSnapshot.key.toString())
                    mHashTagsCount?.add(dataSnapshot.childrenCount.toString())
                }
                tagAdapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    //For searching the user..
    private fun searchUser(s: String) {
        val query = FirebaseDatabase.getInstance().reference.child("Users").
                orderByChild("Username").startAt(s).endAt(s + "\uf8ff")
        query.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                mUsers!!.clear()
                for (dataSnapshot in snapshot.children) {
                    val user = dataSnapshot.getValue(User::class.java)
                    mUsers?.add(user!!)
                }
                userAdapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    //for reading the user..
    private fun readUsers() {

        val ref = FirebaseDatabase.getInstance().reference.child("Users")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                if (TextUtils.isEmpty(searchBar?.text.toString())) {
                    mUsers?.clear()
                    for (dataSnapshot in snapshot.children) {
                        val user = dataSnapshot.getValue(User::class.java)
                        mUsers?.add(user!!)
                    }
                    userAdapter?.notifyDataSetChanged()
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    private fun filter(text : String) {
        val mSearchTags = ArrayList<String>()
        val mSearchTagsCount = ArrayList<String>()

        for (s in mHashTags!!){
            if (s.toLowerCase().contains(text.toLowerCase())){
                mSearchTags.add(s)
                mSearchTagsCount.add(mHashTagsCount!![mHashTags!!.indexOf(s)])
            }
        }
        tagAdapter?.filter(mSearchTags, mSearchTagsCount)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SearchFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SearchFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}