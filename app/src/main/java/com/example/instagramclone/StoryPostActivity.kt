package com.example.instagramclone

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.theartofdev.edmodo.cropper.CropImage

class StoryPostActivity : AppCompatActivity() {

    private lateinit var imageUri: Uri
    private lateinit var imageUrl: String
    private lateinit var close: ImageView
    private lateinit var post: ImageView
    private lateinit var imageAdded: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story_post)

        close = findViewById(R.id.ivCloseS)
        post = findViewById(R.id.ivPostS)
        imageAdded = findViewById(R.id.ivImageAddedS)

        close.setOnClickListener {
            startActivity(Intent(this, HomePageActivity::class.java))
        }

        //For crop image library we're using.
        CropImage.activity().start(this)

        post.setOnClickListener {
            upload()
        }

    }

    //For uploading the post.
    private fun upload() {

        val pd = ProgressDialog(this)
        pd.setMessage("Uploading...")
        pd.show()

        if (imageUri != null) {
            val filePath = FirebaseStorage.getInstance().getReference("Story")
                .child(System.currentTimeMillis().toString() + "." + getFileExtension(imageUri))
            val uploadTask = filePath.putFile(imageUri)
            uploadTask.continueWithTask {
                filePath.downloadUrl
            }
                .addOnCompleteListener { task ->
                    imageUrl = task.result.toString()

                    val ref = FirebaseDatabase.getInstance().getReference("Story").child(FirebaseAuth.getInstance().currentUser!!.uid)
                    val storyId = ref.push().key

                    val map = HashMap<String, String>()
                    map["storyId"] = storyId.toString()
                    map["imageUrl"] = imageUrl
                    map["publisher"] = FirebaseAuth.getInstance().currentUser!!.uid

                    ref.child(storyId.toString()).setValue(map)
                    pd.dismiss()
                    Toast.makeText(
                        this,
                        "Story Uploaded\nDownload link : $imageUrl",
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
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Toast.makeText(
                    this,
                    "Can't help with camera.\nUpload already stored or captured image.\n${result.error}",
                    Toast.LENGTH_SHORT
                ).show()
                startActivity(Intent(this, HomePageActivity::class.java))
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