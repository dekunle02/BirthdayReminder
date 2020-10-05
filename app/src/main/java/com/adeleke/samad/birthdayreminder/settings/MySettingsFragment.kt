package com.adeleke.samad.birthdayreminder.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.adeleke.samad.birthdayreminder.R
import com.adeleke.samad.birthdayreminder.util.KEY_PREF_DISPLAY_NAME
import com.adeleke.samad.birthdayreminder.util.KEY_PREF_NIGHT_SWITCH


class MySettingsFragment : PreferenceFragmentCompat() {

    private var mPreferences: SharedPreferences? = null
    private val sharedPrefFile = "com.adeleke.samad.birthdayreminder"

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        mPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val namePref = findPreference<Preference>(KEY_PREF_DISPLAY_NAME)
        namePref!!.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { preference, newValue ->
                Toast.makeText(this.context, getString(R.string.name_changed), Toast.LENGTH_SHORT).show()
                true
            }
        val nightPref = findPreference<Preference>(KEY_PREF_NIGHT_SWITCH)
        nightPref!!.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { preference, newValue ->
                if (newValue as Boolean) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

                }
                true
            }
    }
}