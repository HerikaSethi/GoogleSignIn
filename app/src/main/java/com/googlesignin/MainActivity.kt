package com.googlesignin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.googlesignin.utils.HelperConstants
import com.googlesignin.utils.HelperConstants.EXTRA_NAME
import com.googlesignin.utils.HelperConstants.PHOTO_URL
import com.googlesignin.utils.HelperConstants.RC_SIGN_IN


class MainActivity : AppCompatActivity() {
    lateinit var googleSignInClient: GoogleSignInClient
    lateinit var auth: FirebaseAuth
    lateinit var signInBtn:SignInButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        signInBtn = findViewById<View>(R.id.btn_googleSignIn) as SignInButton

        auth = Firebase.auth
        createRequest()
    }

    private fun createRequest() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(HelperConstants.serverClientId)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        signInBtn.setOnClickListener {
            signInWithGoogle()
        }
    }

    private fun signInWithGoogle() {
        val intent = googleSignInClient.signInIntent
        startActivityForResult(intent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                //Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                //Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    val us = Firebase.auth.currentUser

                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                   // Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            //manage users info
            // Name, email address, and profile photo Url
            val name = user.displayName
            val email = user.email
            val photoUrl = user.photoUrl
            Log.d("usersinfo", "updateUI: name is :: $name email is :: $email  photo url is :: $photoUrl ")

            // Check if user's email is verified
            val emailVerified = user.isEmailVerified
            Log.d("usersinfo", "updateUI: isemailVerified?? $emailVerified")
            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
           // val uid = user.uid

            
            //move to next activity
            val intent = Intent(applicationContext, SignedInUserActivity::class.java)
            intent.putExtra(EXTRA_NAME, user.displayName)
            intent.putExtra(PHOTO_URL, user.photoUrl.toString())

            startActivity(intent)
        }
    }

}