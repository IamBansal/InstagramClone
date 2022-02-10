package com.example.instagramclone

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.hendraanggrian.appcompat.socialview.Hashtag
import com.hendraanggrian.appcompat.widget.HashtagArrayAdapter
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView
import com.theartofdev.edmodo.cropper.CropImage
import kotlin.collections.HashMap

class PostActivity : AppCompatActivity() {

    private lateinit var imageUri: Uri
    private lateinit var imageUrl: String
    private lateinit var close: ImageView
    private lateinit var post: ImageView
    private lateinit var imageAdded: ImageView
    private lateinit var description: SocialAutoCompleteTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        close = findViewById(R.id.ivClose)
        post = findViewById(R.id.ivPost)
        imageAdded = findViewById(R.id.ivImageAdded)
        description = findViewById(R.id.description)

        close.setOnClickListener {
            startActivity(Intent(this, HomePageActivity::class.java))
        }

        //For crop image library we're using.
        CropImage.activity().start(this)

        post.setOnClickListener {
            upload()
        }

    }

    //For getting hashtags suggestions when adding the description for the post.
    override fun onStart() {
        super.onStart()
        val hashTagAdapter = HashtagArrayAdapter<Hashtag>(this)
        FirebaseDatabase.getInstance().reference.child("Hashtags").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot in snapshot.children) {
                    hashTagAdapter.add(Hashtag(dataSnapshot.key.toString(), dataSnapshot.childrenCount.toInt()))
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        description.hashtagAdapter = hashTagAdapter
    }

    //For uploading the post.
    private fun upload() {

        val pd = ProgressDialog(this)
        pd.setMessage("Uploading...")
        pd.show()

        if (imageUri != null) {
            val filePath = FirebaseStorage.getInstance().getReference("Posts")
                .child(System.currentTimeMillis().toString() + "." + getFileExtension(imageUri))
            val uploadTask = filePath.putFile(imageUri)
            uploadTask.continueWithTask {
                filePath.downloadUrl
            }
                .addOnCompleteListener { task ->
                    imageUrl = task.result.toString()

                    val ref = FirebaseDatabase.getInstance().getReference("Posts")
                    val postId = ref.push().key

                    val map = HashMap<String, String>()
                    map["postId"] = postId.toString()
                    map["imageUrl"] = imageUrl
                    map["description"] = description.text.toString()
                    map["publisher"] = FirebaseAuth.getInstance().currentUser!!.uid

                    ref.child(postId.toString()).setValue(map)

                    //This is for adding hashtags to another child in database.
                    val hashTagRef = FirebaseDatabase.getInstance().reference.child("Hashtags")
                    val hashtags: List<String> = description.hashtags
                    if (hashtags.isNotEmpty()) {
                        for (tag in hashtags) {
                            map.clear()
                            map["tag"] = tag.lowercase()
                            map["postId"] = postId.toString()
                            hashTagRef.child(tag.lowercase()).child(postId!!).setValue(map)
                        }
                    }
                    pd.dismiss()
                    Toast.makeText(
                        this,
                        "Image Uploaded\nDownload link : $imageUrl",
                        Toast.LENGTH_SHORT
                    ).show()
                    startActivity(Intent(this, HomePageActivity::class.java))
                }
        } else {
            Toast.makeText(this, "No image selected.", Toast.LENGTH_SHORT).show()
        }

    }

    //To get the file extension which is used in image's name.
    private fun getFileExtension(uri: Uri): String? {
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(this.contentResolver.getType(uri))
    }

    //To select and get the image in post.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

//        TODO("Can't get image from camera only.")

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                imageUri = result.uri
                imageAdded.setImageURI(imageUri)
            } else {
                Toast.makeText(
                    this,
                    "Can't help with camera.\nUpload already stored or captured image.",
                    Toast.LENGTH_SHORT
                ).show()
                startActivity(Intent(this, HomePageActivity::class.java))
            }
        } else {
            Toast.makeText(this, "Try Again", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, HomePageActivity::class.java))
        }

    }

}