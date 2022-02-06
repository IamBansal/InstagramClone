package com.example.instagramclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var logo : ImageView
    private lateinit var linearLayout: LinearLayout
    private lateinit var login : Button
    private lateinit var register : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        logo = findViewById(R.id.ivLogo)
        linearLayout = findViewById(R.id.linearLayoutMain)
        login = findViewById(R.id.btnLogin)
        register = findViewById(R.id.btnRegister)

        linearLayout.animate().alpha(0f).duration = 1

        //For Setting welcome animation.
        val animation = TranslateAnimation(0f, 0f, 0f , -1250f)
        animation.duration = 1500
        animation.fillAfter = false
        animation.setAnimationListener(object : Animation.AnimationListener{
            override fun onAnimationStart(p0: Animation?) {}
            override fun onAnimationEnd(p0: Animation?) {
                logo.clearAnimation()
                logo.visibility = View.INVISIBLE
                linearLayout.animate().alpha(1f).duration = 1000
            }
            override fun onAnimationRepeat(p0: Animation?) {}

        })
        logo.animation = animation

        login.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
        }

        register.setOnClickListener {
            startActivity(Intent(this, Signup::class.java))
        }

    }

    override fun onStart() {
        super.onStart()
        if (FirebaseAuth.getInstance().currentUser != null) {
            startActivity(Intent(this, HomePageActivity::class.java))
        }
    }

}