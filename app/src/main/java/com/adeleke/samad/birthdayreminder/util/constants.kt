package com.adeleke.samad.birthdayreminder.util

import com.adeleke.samad.birthdayreminder.BuildConfig
import com.adeleke.samad.birthdayreminder.R

// Client id needed in google sign in constructor
//const val OAUTH_CLIENT_ID = "519923531269-85jam4b2rh4g7nfm7jaopnmi0aed5usj.apps.googleusercontent.com"
const val OAUTH_CLIENT_ID =
    "519923531269-84796tme8imf5735chjt0u48itpknmf8.apps.googleusercontent.com"
const val GOOGLE_RC_SIGN_IN = 1

const val FIREBASE_BIRTHDAY_NODE = "Birthdays"
const val FIREBASE_ARCHIVE_NODE = "Archived"

val KEY_PREF_NIGHT_SWITCH = "night_switch"
val KEY_PREF_DISPLAY_NAME = "display_name"


val colorMap = mapOf(
    "Jan" to R.color.salmon,
    "Feb" to R.color.pink,
    "Mar" to R.color.lilac,
    "Apr" to R.color.colorAccent,
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
val monthSortMap = mapOf(
    "Jan" to 1,
    "Feb" to 2,
    "Mar" to 3,
    "Apr" to 4,
    "May" to 5,
    "Jun" to 6,
    "Jul" to 7,
    "Aug" to 8,
    "Sep" to 9,
    "Oct" to 10,
    "Nov" to 11,
    "Dec" to 12
)
val reversedMonthSort = monthSortMap.entries.associate{(k,v)-> v to k}

const val NEW_BIRTHDAY_ID = "-1"
const val ITEM_DETAIL_TAG = "detailTag"
const val CONTACT_REQUEST_CODE = 100
const val NOTIFICATION_BROADCAST_REQUEST_CODE = 200
const val PRIMARY_CHANNEL_ID = "birthdayReminder_notification_channel"

const val MONTH_NOTIFICATION_ID = 200
const val MONTH_NOTIFICATION_ACTION = "month"
const val BIRTHDAY_NOTIFICATION_ACTION = "birthday"
const val BIRTHDAY_JSON_INTENT_TAG = "birthday"

const val ACTION_CUSTOM_BROADCAST =
    BuildConfig.APPLICATION_ID.toString() + ".notification.NotificationReceiver"
