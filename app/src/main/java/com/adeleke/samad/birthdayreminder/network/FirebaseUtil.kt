package com.adeleke.samad.birthdayreminder.network

import android.content.Context
import android.content.Intent
import android.util.Log
import com.adeleke.samad.birthdayreminder.OAUTH_CLIENT_ID
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class FirebaseUtil private constructor(context: Context) {
    private val TAG: String = FirebaseUtil::class.java.simpleName

    var mAuth: FirebaseAuth
    var mUser: FirebaseUser? = null
    var gso: GoogleSignInOptions
    var mGoogleSignInClient: GoogleSignInClient


    companion object {
        private var instance: FirebaseUtil? = null
        fun getInstance(context: Context): FirebaseUtil {
            if (instance == null) {
                instance = FirebaseUtil(context)
            }
            return instance as FirebaseUtil
        }
    }

    init {
        mAuth = FirebaseAuth.getInstance()
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(OAUTH_CLIENT_ID)
            .requestEmail()
            .build()
        mUser = mAuth.currentUser
        mGoogleSignInClient = GoogleSignIn.getClient(context, gso)
    }


    fun signUp(email: String, password: String) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "createUser is Successful")
                    mUser = mAuth.currentUser
                } else {
                    Log.d(TAG, "createUser Failed! -> ${task.exception}")
                }
            }
    }

    fun signIn(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signIn is Successful")
                    mUser = mAuth.currentUser
                } else {
                    Log.d(TAG, "signIn is Failed! -> ${task.exception}")
                }
            }
    }

    // This will receive the intent data from on Activity result in the activity calling googleSignIN
    fun receiveGoogleActivity(data: Intent?) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            // Google Sign In was successful, authenticate with Firebase
            val account = task.getResult(ApiException::class.java)!!
            Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            Log.d(TAG, "Google sign in failed", e)
        }
    }



    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    mUser = mAuth.currentUser
                } else {
                    Log.d(TAG, "signInWithCredential:failure", task.exception)
                }
            }
    }

     fun signOut() {
        // Firebase sign out
        mAuth.signOut()

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener() {
            Log.d(TAG, "GoogleSigned OUT")
        }

        Log.d(TAG, "Signed out")
    }



}