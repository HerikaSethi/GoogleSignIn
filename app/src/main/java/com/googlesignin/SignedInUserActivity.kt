package com.googlesignin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.Auth
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.googlesignin.utils.HelperConstants.EXTRA_NAME
import com.googlesignin.utils.HelperConstants.PHOTO_URL
import kotlin.math.log

class SignedInUserActivity : AppCompatActivity() {
    lateinit var userText:TextView
    lateinit var logoutBtn:Button
    lateinit var profileImage:ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signed_in_user)
        userText = findViewById(R.id.userText)
        logoutBtn = findViewById(R.id.logoutBtn)
        profileImage = findViewById(R.id.profileImg)

        //receiving data from main activity (user_name of signedIn user) and setting it to textView
        userText.text = intent.getStringExtra(EXTRA_NAME)

        //loading url to imageview
        val url = intent.getStringExtra(PHOTO_URL)
        Log.d("url", "onCreate: $url")
        Glide.with(applicationContext).load(url).into(profileImage)


        logoutBtn.setOnClickListener {
           // Firebase.auth.signOut()
            FirebaseAuth.getInstance().signOut()

            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()

        }
    }

}