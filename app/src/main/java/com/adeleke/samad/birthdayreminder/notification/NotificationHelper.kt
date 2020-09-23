package com.adeleke.samad.birthdayreminder.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.telephony.SmsManager
import android.util.Log
import android.view.View
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.startActivity
import com.adeleke.samad.birthdayreminder.MainActivity
import com.adeleke.samad.birthdayreminder.R
import com.adeleke.samad.birthdayreminder.model.Birthday
import com.adeleke.samad.birthdayreminder.util.PRIMARY_CHANNEL_ID

class NotificationHelper private constructor(val context: Context) {
    private lateinit var mNotifyManager: NotificationManager

    init {
        createNotificationChannel()
    }

    companion object{
        private var instance: NotificationHelper? = null
        fun getInstance(context: Context): NotificationHelper {
            if (instance == null) {
                instance = NotificationHelper(context)
            }
            return instance as NotificationHelper
        }
    }

    private fun getNotificationBuilder(birthday: Birthday): NotificationCompat.Builder? {
        val notificationId = birthday.notificationId
        val notificationIntent = Intent(context, MainActivity::class.java)

        val notificationPendingIntent = PendingIntent.getActivity(
            context,
            notificationId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        val extraActionPendingIntent = PendingIntent.getActivity(
            context,
            notificationId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID)
            .setContentTitle("It's ${birthday.name}'s birthday!")
            .setContentText("Send your composed message.")
            .setSmallIcon(R.drawable.ic_birthday)
            .setContentIntent(notificationPendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .addAction(R.drawable.ic_message, "EDIT", extraActionPendingIntent)
    }

    fun cancelNotification(notificationId: Int) {
        mNotifyManager!!.cancel(notificationId)
    }

    fun sendNotification(birthday: Birthday) {
        val notifyBuilder = getNotificationBuilder(birthday)
        mNotifyManager!!.notify(birthday.notificationId, notifyBuilder!!.build())
    }

    private fun createNotificationChannel() {
        mNotifyManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                PRIMARY_CHANNEL_ID,
                "BirthdayReminder Notification", NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Notification from BirthdayReminder"
            mNotifyManager!!.createNotificationChannel(notificationChannel)
        }
    }

    fun editSendText(birthday: Birthday) {
        val smsNumber = "smsto:" + birthday.phoneNumber
        val sms = birthday.textMessage
        // Create the intent.
        val smsIntent = Intent(Intent.ACTION_SENDTO)
        // Set the data for the intent as the phone number.
        smsIntent.data = Uri.parse(smsNumber)
        // Add the message (sms) with the key ("sms_body").
        smsIntent.putExtra("sms_body", sms)
        // If package resolves (target app installed), send intent.
        if (smsIntent.resolveActivity(context.packageManager) != null) {
            startActivity(context, smsIntent, null)
        } else {
            Log.e("TAG", "Can't resolve app for ACTION_SENDTO Intent.")
        }
    }

    fun sendText(birthday: Birthday) {
        val smsNumber = birthday.phoneNumber
        val smsMessage = birthday.textMessage

        val mySmsManager = SmsManager.getDefault()
        mySmsManager.sendTextMessage(smsNumber,null, smsMessage, null, null)
    }


}