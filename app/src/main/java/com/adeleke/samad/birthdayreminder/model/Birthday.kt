package com.adeleke.samad.birthdayreminder.model

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.text.format.DateUtils
import android.util.Log
import com.adeleke.samad.birthdayreminder.notification.NotificationReceiver
import com.adeleke.samad.birthdayreminder.util.*
import com.google.gson.Gson
import java.util.*


// Birthday data class that contains all information stored in the database
data class Birthday(
    var id: String? = generateID(),
    var name: String? = "Name",
    var dayOfBirth: String? = "1",
    var monthOfBirth: String? = "1",
    var yearOfBirth: String? = "2000",
    var phoneNumber: String? = "",
    var textMessage: String? = "Happy Birthday!!!",
    var notes: String? = "Buy gift\n",
    var notificationId: Int = generateRandomNumber()
) {

    companion object {
        private const val prefix = "birthday- "
        private fun generateID(): String {
            return prefix + UUID.randomUUID().toString()
        }

        private fun generateRandomNumber(): Int {
            val random = Random()
            return random.nextInt(5000)
        }
    }

}


fun Birthday.getSimpleDate(): String {
    return this.monthOfBirth + " " + this.dayOfBirth + ", " + this.yearOfBirth
}

// Converting to JSON strings for use as intent extras for notifications
fun Birthday.toJSONString(): String {
    val gson = Gson()
    return gson.toJson(this)
}
fun makeBirthdayFromJSON(json: String): Birthday? {
    val gson = Gson()
    return gson.fromJson(json, Birthday::class.java)
}

// This returns the next alarm time for the birthday extended by the function
fun Birthday.getNextBirthdayMillis(): Long {
    val cal = Calendar.getInstance()
    cal.set(Calendar.MILLISECOND, 0)
    // Set the calendar time with the time from the birthday
    cal.set(Calendar.MONTH, monthSortMap[this.monthOfBirth]!! - 1)
    cal.set(Calendar.DAY_OF_MONTH, this.dayOfBirth!!.toInt())
    cal.set(Calendar.AM_PM, 0)
    cal.set(Calendar.HOUR, 8)
    cal.set(Calendar.MINUTE, 0)

    val now = Calendar.getInstance()
    // Date has not yet happened this year
    if (cal.timeInMillis < now.timeInMillis) {
        cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + 1)
    }
    return cal.timeInMillis
}

// This sets and cancels alarm for the birthday using a context and an alarm Manager
fun Birthday.setAlarm(context: Context, alarmManager: AlarmManager) {
    val repeatInterval = DateUtils.YEAR_IN_MILLIS
    val triggerTime = this.getNextBirthdayMillis()

    val birthdayJson = this.toJSONString()
    val notificationIntent: Intent = Intent(context, NotificationReceiver::class.java)
    notificationIntent.action = BIRTHDAY_NOTIFICATION_ACTION
    notificationIntent.putExtra(BIRTHDAY_JSON_INTENT_TAG, birthdayJson)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        this.notificationId,
        notificationIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    // Cancel any previous alarms set with this particular birthday object and cancel it
    alarmManager.cancel(pendingIntent)
    alarmManager.setExactAndAllowWhileIdle(
        AlarmManager.RTC_WAKEUP,
        triggerTime,
        pendingIntent
    )
}
fun Birthday.cancelAlarm(context: Context, alarmManager: AlarmManager) {
    val birthdayJson = this.toJSONString()
    val notificationIntent: Intent = Intent(context, NotificationReceiver::class.java)
    notificationIntent.action = BIRTHDAY_NOTIFICATION_ACTION
    notificationIntent.putExtra(BIRTHDAY_JSON_INTENT_TAG, birthdayJson)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        this.notificationId,
        notificationIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )
    // Cancel any previous alarms set with this particular birthday object and cancel it
    alarmManager.cancel(pendingIntent)
}

// This makes a string for notifications taking a list of birthdays that will occur in that month
fun makeMonthNotificationString(birthdayList: List<Birthday>): String {
    val sb = StringBuilder()
    for (birthday in birthdayList) {
        sb.append("${birthday.name} on ${birthday.monthOfBirth} ${birthday.dayOfBirth}. ")
    }
    return sb.toString()
}

// This sets the month alarm using a context and an alarmManager
fun setMonthAlarm(context: Context, alarmManager: AlarmManager) {
    Log.d("BIRTHDAY", "setMonthAlarm:  called")
    val cal = Calendar.getInstance()
    // Reset the time
    cal.set(Calendar.MILLISECOND, 0)
    // Set the calendar time with the time from the birthday
    cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 1)
    cal.set(Calendar.AM_PM, 0)
    cal.set(Calendar.DAY_OF_MONTH, 1)
    cal.set(Calendar.HOUR, 8)
    cal.set(Calendar.MINUTE, 0)

    val triggerTime = cal.timeInMillis
    val notificationIntent: Intent = Intent(context, NotificationReceiver::class.java)
    notificationIntent.action = MONTH_NOTIFICATION_ACTION
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        MONTH_NOTIFICATION_ID,
        notificationIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    alarmManager.setExactAndAllowWhileIdle(
        AlarmManager.RTC_WAKEUP,
        triggerTime,
        pendingIntent
    )
}


// Testing the notification Structure and alarm settings
//    alarmManager.setRepeating(
//        AlarmManager.ELAPSED_REALTIME_WAKEUP,
//        SystemClock.elapsedRealtime(),
//        AlarmManager.INTERVAL_HALF_HOUR,
//        pendingIntent
//    )

//    Log.d("BirthdayClass", "Birthday Alarm has been set!")
//    val calendar = Calendar.getInstance()
//    calendar.timeInMillis = this.getNextBirthdayMillis()
//    Log.d("BirthdayClass", "Set for year -  ${calendar.get(Calendar.YEAR)}")
//    Log.d("BirthdayClass", "Set for month -  ${calendar.get(Calendar.MONTH)}")
//    Log.d("BirthdayClass", "Set for day - ${calendar.get(Calendar.DAY_OF_MONTH)}")
//    Log.d("BirthdayClass", "Set for hour - ${calendar.get(Calendar.HOUR)}")
//    Log.d("BirthdayClass", "Set for minute - ${calendar.get(Calendar.MINUTE)}")


//    alarmManager.setRepeating(
//        AlarmManager.RTC_WAKEUP,
//        triggerTime,
//        repeatInterval,
//        pendingIntent
//    )