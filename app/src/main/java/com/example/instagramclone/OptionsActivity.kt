package com.example.instagramclone

import android.app.UiModeManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentActivity
import com.example.instagramclone.fragments.ProfileFragment
import com.google.firebase.auth.FirebaseAuth

class OptionsActivity : AppCompatActivity() {

    private var settings: TextView? = null
    private var logOut: TextView? = null
    private var deleteAccount: TextView? = null
    private var aboutDeveloper: TextView? = null
    private var aboutDeveloperDesc: TextView? = null
    private var toolbar: Toolbar? = null
    private var switchButton: SwitchCompat? = null
    private var linearLayout: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_options)

        settings = findViewById(R.id.settings)
        logOut = findViewById(R.id.logout)
        deleteAccount = findViewById(R.id.deleteAccount)
        aboutDeveloper = findViewById(R.id.aboutD)
        aboutDeveloperDesc = findViewById(R.id.aboutDesc)
        toolbar = findViewById(R.id.toolbarOptions)
        switchButton = findViewById(R.id.switchBtn)
        linearLayout = findViewById(R.id.llOptions)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Options"
        toolbar?.setNavigationOnClickListener {
            finish()
        }

        //Settings part - not yet implemented
        settings?.setOnClickListener {
            Toast.makeText(this, "Not yet implemented!!\nSorry :|", Toast.LENGTH_SHORT).show()
            if (linearLayout?.tag!! == "Visible") {
                linearLayout?.visibility = View.GONE
                linearLayout?.tag = "Gone"
            } else {
                linearLayout?.visibility = View.VISIBLE
                linearLayout?.tag = "Visible"
            }
        }

        /*
        For switching themes
        switchButton?.isChecked = true
        val manager: UiModeManager = this.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
        switchButton?.setOnCheckedChangeListener { _, isChecked ->

            if (switchButton?.isChecked == true) {
                manager.nightMode = UiModeManager.MODE_NIGHT_YES
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//            delegate.applyDayNight()
                switchButton?.isChecked = true
            } else {
                manager.nightMode = UiModeManager.MODE_NIGHT_NO
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//            delegate.applyDayNight()
                switchButton?.isChecked = false
            }
        }

         */

        //To logout the user.
        logOut?.setOnClickListener {
            val alert = AlertDialog.Builder(this)
            alert.setTitle("Logout Requested!!")
                .setMessage("You sure you want to logout?")
                .setPositiveButton("Yes, Logout!!") { _, _ ->
                    FirebaseAuth.getInstance().signOut()
                    startActivity(
                        Intent(
                            this,
                            Login::class.java
                        ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    )
                }
                .setNegativeButton("No") { _, _ -> }
                .create()
                .show()
        }

        //To delete the account.
        deleteAccount?.setOnClickListener {
            val alert = AlertDialog.Builder(this)
            alert.setTitle("Delete Account Requested!!")
                .setMessage("You sure you want to delete your account?\nYou will not be able to recover your data once you delete account.")
                .setPositiveButton("Yes, Delete!!") { _, _ ->
                    FirebaseAuth.getInstance().currentUser?.delete()
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this, "Account deleted.", Toast.LENGTH_SHORT).show()
                                startActivity(
                                    Intent(
                                        this,
                                        Login::class.java
                                    ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                )
                            } else {
                                Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                }
                .setNegativeButton("No") { _, _ -> }
                .create()
                .show()
        }

        //To show about developer.
        aboutDeveloper?.setOnClickListener {
            //If description is visible then to hide it else show it.
            if (aboutDeveloperDesc?.tag!! == "Visible") {
                aboutDeveloperDesc?.visibility = View.GONE
                aboutDeveloperDesc?.tag = "Gone"
            } else {
                aboutDeveloperDesc?.visibility = View.VISIBLE
                aboutDeveloperDesc?.tag = "Visible"
            }
        }

    }
}