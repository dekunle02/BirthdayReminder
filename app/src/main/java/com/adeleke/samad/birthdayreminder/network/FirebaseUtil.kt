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

    var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    var mUser: FirebaseUser?
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
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(OAUTH_CLIENT_ID)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(context, gso)
        mUser = mAuth.currentUser
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