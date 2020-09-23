package com.adeleke.samad.birthdayreminder.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class NotificationReceiver : BroadcastReceiver() {
    private val TAG = javaClass.simpleName

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "onReceive: broadcastReceived")
        val intentAction = intent!!.action
        if (intentAction != null) {
            when (intentAction) {
                "ACTION_CUSTOM_NOTIFY"-> {
                    val notificationHelper = NotificationHelper.getInstance(context!!)
                    val smsNumber = intent!!.getStringExtra("NUMBER")
                    val smsMessage = intent!!.getStringExtra("MESSAGE")
                    notificationHelper.sendText(smsNumber!!, smsMessage!!)

                }

            }

        }
    }

}