package com.example.instagramclone

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {

    private var email : TextInputEditText? = null
    private var password : TextInputEditText? = null
    private var login : Button? = null
    private var signuptext : TextView? = null
    private var firebaseAuth : FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        email = findViewById(R.id.emailLog)
        password = findViewById(R.id.passLog)
        login = findViewById(R.id.btnLog)
        signuptext = findViewById(R.id.signupText)
        firebaseAuth = FirebaseAuth.getInstance()

        login?.setOnClickListener {
            loginUser()
        }

        signuptext?.setOnClickListener {
            startActivity(Intent(this, Signup::class.java))
        }

    }

    //For remembering the user.
    override fun onStart() {
        super.onStart()
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            startActivity(Intent(this, HomePageActivity::class.java))
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, MainActivity::class.java))
    }

    //To sign in the user.(Without verification for now.)
    private fun loginUser() {
        val emailText = email?.text.toString().trim()
        val passwordText = password?.text.toString().trim()

        if (TextUtils.isEmpty(emailText) || TextUtils.isEmpty(passwordText)) {
            val alert = AlertDialog.Builder(this)
            alert.setTitle("Login failed!!")
                .setMessage("Fill all credentials first.")
                .setPositiveButton("Okay"){_,_-> }
                .create()
                .show()
        } else {
            val progressBar = ProgressDialog(this)
            progressBar.setMessage("Logging in..")
            progressBar.show()

            firebaseAuth?.signInWithEmailAndPassword(emailText, passwordText)?.addOnCompleteListener { task ->
                progressBar.dismiss()
                if(task.isSuccessful) {
                    Toast.makeText(this, "Logged in successfully.", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, HomePageActivity::class.java))
                } else {
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

    }


}