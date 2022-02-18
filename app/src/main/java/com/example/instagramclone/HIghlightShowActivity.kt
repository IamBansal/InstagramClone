package com.example.instagramclone

import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.instagramclone.adapter.HIghlightShowAdapter
import com.example.instagramclone.model.Highlight
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HIghlightShowActivity : AppCompatActivity() {

    private var recyclerView: RecyclerView? = null
    private var highlightShowAdapter: HIghlightShowAdapter? = null
    private var list: ArrayList<Highlight>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_highlight_show)

        recyclerView = findViewById(R.id.rvHighlight)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = StaggeredGridLayoutManager(1, LinearLayout.HORIZONTAL)
        list = ArrayList()
        highlightShowAdapter = HIghlightShowAdapter(this, list!!)
        recyclerView?.adapter = highlightShowAdapter

        val userIdFromIntent = intent.getStringExtra("userH")
        val highlightText = intent.getStringExtra("highlightText")
        readHighlight(userIdFromIntent.toString(), highlightText.toString())

    }

    //For highlight reading
    private fun readHighlight(idUser: String, textHighlight: String) {
        FirebaseDatabase.getInstance().reference.child("Highlight").child(idUser)
            .child(textHighlight).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    list?.clear()
                    for (snaps in snapshot.children) {
                        val highlight: Highlight? = snaps.getValue(Highlight::class.java)
                        list?.add(highlight!!)
                    }
                    highlightShowAdapter?.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

}