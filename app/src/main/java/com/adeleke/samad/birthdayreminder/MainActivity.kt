package com.adeleke.samad.birthdayreminder

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.*
import android.widget.EditText
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.preference.PreferenceManager
import com.adeleke.samad.birthdayreminder.archiveList.ArchiveListFragment
import com.adeleke.samad.birthdayreminder.auth.AuthActivity
import com.adeleke.samad.birthdayreminder.birthdayList.BirthdayListFragment
import com.adeleke.samad.birthdayreminder.databinding.ActivityMainBinding
import com.adeleke.samad.birthdayreminder.network.FirebaseUtil
import com.adeleke.samad.birthdayreminder.settings.MySettingsActivity
import com.adeleke.samad.birthdayreminder.util.KEY_PREF_NIGHT_SWITCH
import com.adeleke.samad.birthdayreminder.util.makeSimpleSnack
import com.google.android.material.button.MaterialButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private val TAG = javaClass.simpleName
    private val DRAWER_STATE_KEY: String = "navigationDrawerState"

    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var navView: NavigationView
    private var user: FirebaseUser? = null
    private lateinit var firebaseUtil: FirebaseUtil

//    private lateinit var navController: NavController
//    private lateinit var navHostFragment: NavHostFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        firebaseUtil = FirebaseUtil.getInstance(this)
        user = FirebaseAuth.getInstance().currentUser!!
        if (user == null) {
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
            finish()
        }

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
        if (nightSwitch) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES) else AppCompatDelegate.setDefaultNightMode(
            AppCompatDelegate.MODE_NIGHT_NO
        )

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
            R.id.action_delete_account -> {
                showDeleteAccountPopUp()
            }
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showDeleteAccountPopUp() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setIcon(R.drawable.ic_warning)
            .setCancelable(true)
            .setTitle(getString(R.string.delete))
            .setMessage(getString(R.string.delete_account_warning))
            .setPositiveButton(
                getString(R.string.yes)
            ) { p0, p1 ->
                Log.d(TAG, "showDeleteAccountPopUp: OK pressed ")
                deleteAccount()
            }
            .setNegativeButton(
                getString(R.string.no)
            ) { dialogInterface, p1 ->
                dialogInterface!!.cancel()
            }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun deleteAccount() {
        val uid = user!!.uid
        user!!.delete()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    firebaseUtil.deleteUserInformation(uid)
                    val intent = Intent(this, AuthActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    try {
                        throw task.exception!!
                    } catch (e: FirebaseAuthRecentLoginRequiredException) {
                        showConfirmPasswordPopUp()
                    }
                }
            }
    }

    private fun showConfirmPasswordPopUp() {
        val builder = AlertDialog.Builder(this)
        val viewGroup = findViewById<ViewGroup>(android.R.id.content)
        val dialogView: View =
            LayoutInflater.from(toolbar.context)
                .inflate(R.layout.dialog_confirm_password, viewGroup, false)
        builder.setView(dialogView)
        val alertDialog = builder.create()
        alertDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        alertDialog.show()
        val editTextPassword: EditText =
            dialogView.findViewById(R.id.confirm_password)
        editTextPassword.requestFocus()
        val btnConfirmSubmit: MaterialButton =
            dialogView.findViewById(R.id.btn_confirm_password_ok)

        btnConfirmSubmit.setOnClickListener {
            val credential = EmailAuthProvider.getCredential(
                user!!.email!!,
                editTextPassword.text.toString()
            )
            user!!.reauthenticate(credential)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d(TAG, "showConfirmPasswordPopUp: Re-Authentication Complete")
                        toolbar.makeSimpleSnack("Re-Authentication Complete.")
                    } else {
                        toolbar.makeSimpleSnack(it.result.toString())
                    }
                    alertDialog.dismiss()
                    deleteAccount()
                }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val fragmentManager = supportFragmentManager;
        when (item.itemId) {
            R.id.nav_settings -> {
                val intent = Intent(this, MySettingsActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_contact -> contactMe()

            R.id.nav_privacy -> showPrivacyPopUp()

            R.id.birthdayListFragment -> {
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

    private fun showPrivacyPopUp() {
        val builder = AlertDialog.Builder(this)
        val viewGroup = findViewById<ViewGroup>(android.R.id.content)
        val dialogView: View =
            LayoutInflater.from(toolbar.context).inflate(R.layout.dialog_privacy, viewGroup, false)
        builder.setView(dialogView)
        val alertDialog = builder.create()
        alertDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        alertDialog.show()
        val btnConfirmSubmit: MaterialButton = dialogView.findViewById(R.id.btn_privacy_ok)
        btnConfirmSubmit.setOnClickListener { alertDialog.dismiss() }
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
        navView = binding.navView
        navView.setNavigationItemSelectedListener(this)
        binding.navView.setCheckedItem(R.id.birthdayListFragment);
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
}
