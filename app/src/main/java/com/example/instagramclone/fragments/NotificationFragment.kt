package com.example.instagramclone.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramclone.R
import com.example.instagramclone.adapter.NotificationAdapter
import com.example.instagramclone.model.Notification
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [NotificationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NotificationFragment : Fragment() {
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

    private var recyclerViewNotification : RecyclerView? = null
    private var adapterNotification : NotificationAdapter? = null
    private var notificationList : ArrayList<Notification>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout =  inflater.inflate(R.layout.fragment_notification, container, false)

        recyclerViewNotification = layout.findViewById(R.id.recyclerViewNotifications)
        notificationList = ArrayList()
        adapterNotification = NotificationAdapter(requireContext(), notificationList!!)
        recyclerViewNotification?.setHasFixedSize(true)
        recyclerViewNotification?.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewNotification?.adapter = adapterNotification

        readNotification()

        return layout
    }

    //To read the notifications user got.
    private fun readNotification() {
        FirebaseDatabase.getInstance().reference.child("Notifications").child(FirebaseAuth.getInstance().currentUser!!.uid)
            .addValueEventListener(object :ValueEventListener{
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (dataSnaps in snapshot.children){
                        notificationList?.add(dataSnaps.getValue(Notification::class.java)!!)
                    }
                    notificationList?.reverse()
                    adapterNotification?.notifyDataSetChanged()
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
         * @return A new instance of fragment NotificationFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NotificationFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}