package com.adeleke.samad.birthdayreminder.util

import com.adeleke.samad.birthdayreminder.R

// Client id needed in google sign in constructor
//const val OAUTH_CLIENT_ID = "519923531269-85jam4b2rh4g7nfm7jaopnmi0aed5usj.apps.googleusercontent.com"
const val OAUTH_CLIENT_ID =
    "519923531269-84796tme8imf5735chjt0u48itpknmf8.apps.googleusercontent.com"
const val GOOGLE_RC_SIGN_IN = 1

const val FIREBASE_BIRTHDAY_NODE = "Birthdays"
const val FIREBASE_ANNIVERSARY_NODE = "Anniversaries"

val colorMap = mapOf(
    "Jan" to R.color.salmon,
    "Feb" to R.color.pink,
    "Mar" to R.color.lilac,
    "May" to R.color.purple,
    "Jun" to R.color.blue,
    "Jul" to R.color.bluegreen,
    "Aug" to R.color.green,
    "Sep" to R.color.lemon,
    "Oct" to R.color.yellow,
    "Nov" to R.color.gold,
    "Dec" to R.color.tangerine,
    "default" to R.color.colorAccent
    )