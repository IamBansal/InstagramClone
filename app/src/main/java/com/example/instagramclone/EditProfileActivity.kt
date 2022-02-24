package com.example.instagramclone

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.instagramclone.fragments.ProfileFragment
import com.example.instagramclone.model.User
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import de.hdodenhof.circleimageview.CircleImageView

class EditProfileActivity : AppCompatActivity() {

    private var close : ImageView? = null
    private var save : ImageView? = null
    private var imageProfile : CircleImageView? = null
    private var changeProfile : TextView? = null
    private var removeProfile : TextView? = null
    private var username : TextInputEditText? = null
    private var fullName : TextInputEditText? = null
    private var bio : TextInputEditText? = null
    private var firebaseUser : FirebaseUser? = null
    private var imageUri : Uri? = null
    private var storageRef : StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        close = findViewById(R.id.ivCloseEditProfile)
        save = findViewById(R.id.ivSaveProfile)
        imageProfile = findViewById(R.id.civProfileImage)
        changeProfile = findViewById(R.id.tvChangePhoto)
        removeProfile = findViewById(R.id.tvRemovePhoto)
        username = findViewById(R.id.etUsernameEProfile)
        fullName = findViewById(R.id.etEFullName)
        bio = findViewById(R.id.etEBio)

        storageRef = FirebaseStorage.getInstance().reference.child("Uploads")

        FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                username?.setText(user?.Username.toString().trim())
                fullName?.setText(user?.Name.toString().trim())
                bio?.setText(user?.Bio.toString().trim())

                if (user?.imageUrl.equals("default")){
                    imageProfile?.setImageResource(R.drawable.ic_baseline_person_24)
                } else {
                    Picasso.get().load(user?.imageUrl).into(imageProfile)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        close?.setOnClickListener {
            finish()
        }

        changeProfile?.setOnClickListener {
            CropImage.activity().setCropShape(CropImageView.CropShape.RECTANGLE).start(this)
        }

        removeProfile?.setOnClickListener {
            val alert = AlertDialog.Builder(this)
            alert.setTitle("Remove Profile Requested!!")
                .setMessage("You sure you want to remove your profile photo?\nYou can again change your profile photo anytime again.")
                .setPositiveButton("Remove!"){_,_->
                    val map = HashMap<String, Any>()
                    map["imageUrl"] = "default"
                    FirebaseDatabase.getInstance().reference.child("Users").child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .updateChildren(map).addOnCompleteListener { task ->
                            if (task.isSuccessful){
                                Toast.makeText(this, "Profile photo removed.", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                }
                .setNegativeButton("No"){_,_->}
                .create()
                .show()
        }

        imageProfile?.setOnClickListener {
            CropImage.activity().setCropShape(CropImageView.CropShape.RECTANGLE).start(this)
        }

        save?.setOnClickListener {
            updateProfile()
        }

    }

    //For updating the profile.
    private fun updateProfile() {
        val pd = ProgressDialog(this)
        pd.setMessage("Updating your profile...")
        pd.show()

        val map = HashMap<String, String>()
        map["Username"] = username?.text.toString()
        map["Name"] = fullName?.text.toString()
        map["Bio"] = bio?.text.toString()
        FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid).updateChildren(map as Map<String, Any>)
        pd.dismiss()
        finish()
    }

    //For getting the crop image result.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

//        TODO("Can't get image from camera only.")

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            uploadImage()
        } else {
            Toast.makeText(this, "Something went wrong!!", Toast.LENGTH_SHORT).show()
        }
    }

    //For uploading the image.
    private fun uploadImage() {
        val pd = ProgressDialog(this)
        pd.setMessage("Uploading...")
        pd.show()

        if(imageUri != null){

            val ref = storageRef!!.child("${System.currentTimeMillis()}.jpeg")
            val uploadTask = ref.putFile(imageUri!!)
            uploadTask.continueWithTask{
                ref.downloadUrl
            }
                .addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        val url = task.result.toString()
                        FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid).child("imageUrl").setValue(url)
                        pd.dismiss()
                    } else {
                        pd.dismiss()
                        Toast.makeText(this, "Can't update profile photo.", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(this, "No image selected.", Toast.LENGTH_SHORT).show()
        }

    }

}