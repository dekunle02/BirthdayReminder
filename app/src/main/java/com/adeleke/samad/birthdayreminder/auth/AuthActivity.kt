package com.adeleke.samad.birthdayreminder.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.adeleke.samad.birthdayreminder.R
import com.adeleke.samad.birthdayreminder.util.navigateToMain
import com.adeleke.samad.birthdayreminder.network.FirebaseUtil

class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        val user =  FirebaseUtil.getInstance(this).mUser
        if (user != null) {
            navigateToMain()
        }
    }
}