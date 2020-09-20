package com.adeleke.samad.birthdayreminder

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.adeleke.samad.birthdayreminder.databinding.ActivityMainBinding
import com.adeleke.samad.birthdayreminder.network.FirebaseUtil
import com.adeleke.samad.birthdayreminder.auth.AuthActivity
import com.adeleke.samad.birthdayreminder.util.makeSimpleSnack
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private val TAG = javaClass.simpleName
    private val DRAWER_STATE_KEY: String = "navigationDrawerState"

    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var navView: NavigationView
    private lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setUpToolbar()
        setUpDrawerLayout()
        setUpNav()

        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(DRAWER_STATE_KEY)) drawerLayout.openDrawer(
                GravityCompat.START
            ) else drawerLayout.closeDrawer(GravityCompat.START)
        }
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        outState.putBoolean(DRAWER_STATE_KEY, drawerLayout.isDrawerOpen(GravityCompat.START))
        super.onSaveInstanceState(outState, outPersistentState)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_main) as NavHostFragment
        val navController = navHostFragment.navController
        return NavigationUI.navigateUp(navController, drawerLayout) || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search -> coordinatorHost.makeSimpleSnack("Search clicked!")
            R.id.action_sign_out -> {
                coordinatorHost.makeSimpleSnack("Log out clicked!")
                FirebaseUtil.getInstance(this).signOut()
                FirebaseUtil.getInstance(this).mUser = null
                val intent = Intent(this, AuthActivity::class.java)
                startActivity(intent)
                finish()
            }
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun setUpToolbar() {
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
    }

    private fun setUpDrawerLayout() {
        drawerLayout = binding.drawerLayout
//        val toggle = ActionBarDrawerToggle(
//            this, drawerLayout, toolbar, 0, 0
//        )
//        drawerLayout.addDrawerListener(toggle)
//
//        toggle.syncState()
    }

    private fun setUpNav() {
        navView = binding.navView
        navView.setNavigationItemSelectedListener(this)

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_main) as NavHostFragment
        navController = navHostFragment.navController
        navView.setupWithNavController(navController)
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
    }
}