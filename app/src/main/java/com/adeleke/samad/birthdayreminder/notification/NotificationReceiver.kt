package com.adeleke.samad.birthdayreminder.notification

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.adeleke.samad.birthdayreminder.model.Birthday
import com.adeleke.samad.birthdayreminder.model.makeBirthdayFromJSON
import com.adeleke.samad.birthdayreminder.model.setAlarm
import com.adeleke.samad.birthdayreminder.model.setMonthAlarm
import com.adeleke.samad.birthdayreminder.util.BIRTHDAY_JSON_INTENT_TAG
import com.adeleke.samad.birthdayreminder.util.BIRTHDAY_NOTIFICATION_ACTION
import com.adeleke.samad.birthdayreminder.util.MONTH_NOTIFICATION_ACTION
import com.adeleke.samad.birthdayreminder.util.reversedMonthSort
import java.util.*

class NotificationReceiver : BroadcastReceiver() {
    private val TAG = javaClass.simpleName

    override fun onReceive(context: Context?, intent: Intent?) {

        val notificationHelper = NotificationHelper.getInstance(context!!)
        val alarmManager: AlarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intentAction = intent!!.action
        when (intentAction!!) {
            BIRTHDAY_NOTIFICATION_ACTION -> {
                val birthdayJson: String? = intent.getStringExtra(BIRTHDAY_JSON_INTENT_TAG)
                val birthday: Birthday = makeBirthdayFromJSON(birthdayJson!!)!!
                notificationHelper.sendNotification(birthday)
                birthday.setAlarm(context, alarmManager)
            }
            MONTH_NOTIFICATION_ACTION -> {
                val calendar = Calendar.getInstance()
                val monthIndex = calendar.get(Calendar.MONTH) + 1
                val month = reversedMonthSort[monthIndex]
                notificationHelper.sendMonthNotification(month!!)
                setMonthAlarm(context, alarmManager)
            }
        }

//        val intentAction = intent!!.action
//        if (intentAction != null) {
//            Log.d(TAG, "onReceive: intent is not null")
//            when (intentAction) {
//                CUSTOM_BROADCAST_INTENT_ACTION -> {
//                    Log.d(TAG, "onReceive: Intent Action matches")
//                    val birthday: Birthday? = intent.getParcelableExtra(BIRTHDAY_INTENT_TAG)
//                    val notificationHelper = NotificationHelper.getInstance(context!!)
//                    Log.d(TAG, "onReceive: notification Helper built")
//                    notificationHelper.sendNotification(birthday!!)
//                }
//
//            }
//
//        }
    }

}