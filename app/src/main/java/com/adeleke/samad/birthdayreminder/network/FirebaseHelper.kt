package com.adeleke.samad.birthdayreminder.network

import androidx.fragment.app.Fragment
import com.adeleke.samad.birthdayreminder.util.GOOGLE_RC_SIGN_IN


fun Fragment.googleSignIn() {
    val firebaseUtil = FirebaseUtil.getInstance(context!!)
    val signInIntent = firebaseUtil.mGoogleSignInClient.signInIntent
    startActivityForResult(signInIntent, GOOGLE_RC_SIGN_IN)
}



