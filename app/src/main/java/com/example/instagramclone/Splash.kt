package com.example.instagramclone

import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ImageView

class Splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val image = findViewById<ImageView>(R.id.logoSplash)
        val imageD = findViewById<ImageView>(R.id.logoSplashDark)

        when (this.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                imageD.visibility = View.VISIBLE
                image.visibility = View.GONE
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                image.visibility = View.VISIBLE
                imageD.visibility = View.GONE
            }
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {
            }
        }

        Handler().postDelayed({ startActivity(Intent(this, Login::class.java))}, 2500)

    }
}