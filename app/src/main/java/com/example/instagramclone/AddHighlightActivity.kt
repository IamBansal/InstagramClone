package com.example.instagramclone

import android.app.AlertDialog
import android.app.ProgressDialog
import android.os.Bundle
import android.text.TextUtils
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso

class AddHighlightActivity : AppCompatActivity() {

    private var close : ImageView? = null
    private var add : ImageView? = null
    private var image : ImageView? = null
    private var name : EditText? = null
    private var firebaseUser : FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_highlight)

        close = findViewById(R.id.ivCloseH)
        add = findViewById(R.id.ivPostH)
        image = findViewById(R.id.ivImageAddedH)
        name = findViewById(R.id.etHighlightName)
        firebaseUser = FirebaseAuth.getInstance().currentUser

        val storyId = intent.getStringExtra("storyIdH")
        val storyImageUrl = intent.getStringExtra("storyImageUrl")

        if (storyImageUrl == null){
            add?.isEnabled = false
            val alert = AlertDialog.Builder(this)
            alert.setTitle("Add Highlight!!")
                .setMessage("Add story first if not added.\n" +
                        "And then add highlight from story.")
                .setPositiveButton("Okay"){_,_->finish()
                    add?.isEnabled = true}
                .create()
                .show()
        } else {
            add?.isEnabled = true
        }

        Picasso.get().load(storyImageUrl).placeholder(R.drawable.ic_baseline_cloud_download_24).into(image)

        //To add highlight
        add?.setOnClickListener {
            val nameText = name?.text.toString()
            if (TextUtils.isEmpty(nameText)){
                Toast.makeText(this, "Enter a name for the highlight first.", Toast.LENGTH_SHORT).show()
            } else {
                add?.isEnabled = true
                val pd = ProgressDialog(this)
                pd.setMessage("Adding Highlight...")
                pd.show()

                val ref = FirebaseDatabase.getInstance().reference.child("Highlight").child(firebaseUser!!.uid).child(nameText)
                val highlightId = ref.push().key
                val map = HashMap<String, Any>()
                map["highlightId"] = highlightId.toString()
                map["highlightText"] = nameText
                map["imageUrl"] = storyImageUrl.toString()
                map["publisher"] = firebaseUser!!.uid
                ref.child(highlightId.toString()).setValue(map).addOnCompleteListener { task ->
                    pd.dismiss()
                    if (task.isSuccessful){
                        finish()
                        Toast.makeText(this, "Highlight Added", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        close?.setOnClickListener {
            finish()
        }

    }
}