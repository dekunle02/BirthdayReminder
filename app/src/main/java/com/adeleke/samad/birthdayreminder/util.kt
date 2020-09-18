package com.adeleke.samad.birthdayreminder

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.adeleke.samad.birthdayreminder.views.main.MainActivity
import com.google.android.material.snackbar.Snackbar

fun String.isEmailFormatted(): Boolean {
    val text = this.trim()
    return !(text.isNullOrEmpty() || !Patterns.EMAIL_ADDRESS.matcher(text).matches())
}

fun String.isPasswordFormatted(): Boolean {
    val text = this.trim()
    return !(text.isNullOrEmpty() || text.length < 6)

}

fun hideSoftKeyboard(view: View) {
    val imm =
        view.context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun View.makeSimpleSnack(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_SHORT).show()
}


fun Activity.navigateToMain() {
    val intent = Intent(this, MainActivity::class.java)
    startActivity(intent)
    this.finish()
}