package com.adeleke.samad.birthdayreminder

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.adeleke.samad.birthdayreminder.archiveList.ArchiveListFragment
import com.adeleke.samad.birthdayreminder.auth.AuthActivity
import com.adeleke.samad.birthdayreminder.birthdayList.BirthdayListFragment
import com.adeleke.samad.birthdayreminder.databinding.ActivityMainBinding
import com.adeleke.samad.birthdayreminder.network.FirebaseUtil
import com.adeleke.samad.birthdayreminder.settings.MySettingsActivity
import com.adeleke.samad.birthdayreminder.util.KEY_PREF_DISPLAY_NAME
import com.adeleke.samad.birthdayreminder.util.KEY_PREF_NIGHT_SWITCH
import com.adeleke.samad.birthdayreminder.util.makeSimpleSnack
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header_main.*
import org.w3c.dom.Text

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private val TAG = javaClass.simpleName
    private  val DRAWER_STATE_KEY: String = "navigationDrawerState"

    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var navView: NavigationView

//    private lateinit var navController: NavController
//    private lateinit var navHostFragment: NavHostFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setUpToolbar()
        setUpDrawerLayout()
        setUpNav()

        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(DRAWER_STATE_KEY)) binding.drawerLayout.openDrawer(
                GravityCompat.START
            ) else binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        outState.putBoolean(DRAWER_STATE_KEY, drawerLayout.isDrawerOpen(GravityCompat.START))
        super.onSaveInstanceState(outState, outPersistentState)
    }

    override fun onResume() {
        super.onResume()
        // This sets the default values, the last parameter should be false so it doesnt set it every time user opens app
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        val nightSwitch = sharedPref.getBoolean(KEY_PREF_NIGHT_SWITCH, false)
        if (nightSwitch) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES) else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//        val displayName = sharedPref.getString(KEY_PREF_DISPLAY_NAME, "Nobody")
//        val hView = binding.navView.getHeaderView(0)
//        val navUser = hView.findViewById<TextView>(R.id.navTitle)
//        navUser.text = displayName
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

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
        val fragmentManager = supportFragmentManager;
        when(item.itemId ) {
            R.id.nav_settings -> {
                val intent = Intent(this, MySettingsActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_contact -> contactMe()
            R.id.birthdayListFragment-> {
                val fragment = BirthdayListFragment();
                val fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_main, fragment);
                fragmentTransaction.commit();
               binding.navView.setCheckedItem(R.id.birthdayListFragment);
            }
            R.id.archiveListFragment -> {
                val fragment = ArchiveListFragment();
                val fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_main, fragment);
                fragmentTransaction.commit();
                binding.navView.setCheckedItem(R.id.archiveListFragment);
            }

        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun setUpToolbar() {
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
    }


    private fun setUpDrawerLayout() {
        drawerLayout = binding.drawerLayout
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, 0, 0
        )
        drawerLayout.addDrawerListener(toggle)

        toggle.syncState()
    }

    private fun setUpNav() {
//        navHostFragment =
//            supportFragmentManager.findFragmentById(R.id.nav_host_main) as NavHostFragment
//        navController = navHostFragment.navController
//        NavigationUI.setupActionBarWithNavController(this, navController, binding.drawerLayout)
//        binding.navView.setCheckedItem(R.id.birthdayListFragment)
        navView = binding.navView
        navView.setNavigationItemSelectedListener(this)
        binding.navView.setCheckedItem(R.id.birthdayListFragment);
//        navHostFragment =
//            supportFragmentManager.findFragmentById(R.id.nav_host_main) as NavHostFragment
//        navController = navHostFragment.navController
//        navView.setupWithNavController(navController)
//        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)


    }

    private fun contactMe() {
        val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + "dekunle02@gmail.com"))
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        val androidVersion = Build.VERSION.SDK_INT
        emailIntent.putExtra(
            Intent.EXTRA_SUBJECT,
            "Feedback from BirthdayReminder App\nManufacturer: $manufacturer \nmodel: $model \nOs: $androidVersion"
        )
        // emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        //emailIntent.putExtra(Intent.EXTRA_TEXT, text);
        // emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        //emailIntent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(emailIntent, "Feedback from BirthdayReminder App"))
    }


//    override fun onSupportNavigateUp(): Boolean {
//        val navHostFragment =
//            supportFragmentManager.findFragmentById(R.id.nav_host_main) as NavHostFragment
//        val navController = navHostFragment.navController
//        return NavigationUI.navigateUp(navController, drawerLayout) || super.onSupportNavigateUp()
//    }

}

//AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)