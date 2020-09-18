package com.adeleke.samad.birthdayreminder.views.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.adeleke.samad.birthdayreminder.R
import com.adeleke.samad.birthdayreminder.navigateToMain
import com.adeleke.samad.birthdayreminder.network.FirebaseUtil
import com.adeleke.samad.birthdayreminder.views.main.MainActivity

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