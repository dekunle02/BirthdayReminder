package com.adeleke.samad.birthdayreminder.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.adeleke.samad.birthdayreminder.R
import com.adeleke.samad.birthdayreminder.util.navigateToMain
import com.adeleke.samad.birthdayreminder.network.FirebaseUtil
import com.adeleke.samad.birthdayreminder.util.KEY_PREF_NIGHT_SWITCH

class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        val nightPref: Boolean = sharedPref.getBoolean(KEY_PREF_NIGHT_SWITCH, false)
        if (nightPref) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)


        val user =  FirebaseUtil.getInstance(this).mUser
        if (user != null) {
            navigateToMain()
        }
    }
}