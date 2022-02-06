package com.example.instagramclone

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.Toast
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView
import com.theartofdev.edmodo.cropper.CropImage
import java.lang.Exception
import java.util.*
import kotlin.collections.HashMap
import kotlin.coroutines.Continuation

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

                    val hashTagRef = FirebaseDatabase.getInstance().reference.child("Hashtags")
                    val hashtags: List<String> = description.hashtags
                    if (hashtags.isNotEmpty()) {
                        for (tag in hashtags) {
                            map.clear()
                            map["tag"] = tag.lowercase()
                            map["postId"] = postId.toString()
                            hashTagRef.child(tag.lowercase()).setValue(map)
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

    private fun getFileExtension(uri: Uri): String? {
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(this.contentResolver.getType(uri))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

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