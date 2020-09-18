package com.adeleke.samad.birthdayreminder.views.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.adeleke.samad.birthdayreminder.R
import com.adeleke.samad.birthdayreminder.makeSimpleSnack
import com.adeleke.samad.birthdayreminder.network.FirebaseUtil
import com.adeleke.samad.birthdayreminder.views.auth.AuthActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val TAG = javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search -> mainRootLayout.makeSimpleSnack("Search clicked!")
            R.id.action_sign_out -> {
                mainRootLayout.makeSimpleSnack("Log out clicked!")
                FirebaseUtil.getInstance(this).signOut()
                FirebaseUtil.getInstance(this).mUser = null
                val intent = Intent(this, AuthActivity::class.java)
                startActivity(intent)
                finish()
            }
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)    }
}