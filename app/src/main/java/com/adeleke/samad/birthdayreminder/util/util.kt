package com.adeleke.samad.birthdayreminder.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.adeleke.samad.birthdayreminder.MainActivity
import com.adeleke.samad.birthdayreminder.model.Birthday
import com.google.android.material.snackbar.Snackbar
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

// String extension functions that help check proper EditText user inputs
fun String.isEmailFormatted(): Boolean {
    val text = this.trim()
    return (!text.isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(text).matches())
}

fun String.isPasswordFormatted(): Boolean {
    val text = this.trim()
    return (!text.isNullOrEmpty() && text.length >= 6)
}

fun String.isEmpty(): Boolean {
    val text = this.trim()
    return (text.isNullOrEmpty())
}

// Function to hide keyboard used everywhere user enters sth into an editText
fun hideSoftKeyboard(view: View) {
    val imm =
        view.context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

// Function to show a simple snackbar message, used all over the app to communicate with user
fun View.makeSimpleSnack(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG).show()
}


// Function to navigate to the actual app, from the 2 log in fragments and the auth activity
fun Activity.navigateToMain() {
    val intent = Intent(this, MainActivity::class.java)
    startActivity(intent)
    this.finish()
}


// Function to convert the date given by the Material Date Picker dialog
fun convertToEasyDate(dateString: String): Map<String, String> {
    val df: DateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
    val date = LocalDate.parse(dateString, df)

    return mapOf(
        "day" to "${date.dayOfMonth}",
        "month" to reversedMonthSort[date.monthValue]!!,
        "year" to "${date.year}"
        )
}

