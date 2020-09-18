package com.adeleke.samad.birthdayreminder.network

import android.app.Activity
import com.adeleke.samad.birthdayreminder.GOOGLE_RC_SIGN_IN


fun Activity.googleSignIn() {
    val firebaseUtil = FirebaseUtil.getInstance(this)
    val signInIntent = firebaseUtil.mGoogleSignInClient.signInIntent
    startActivityForResult(signInIntent, GOOGLE_RC_SIGN_IN)
}

