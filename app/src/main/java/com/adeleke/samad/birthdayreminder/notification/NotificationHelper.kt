package com.adeleke.samad.birthdayreminder.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.util.Log
import androidx.core.app.NotificationCompat
import com.adeleke.samad.birthdayreminder.MainActivity
import com.adeleke.samad.birthdayreminder.R
import com.adeleke.samad.birthdayreminder.birthdayDetail.BirthdayDetailActivity
import com.adeleke.samad.birthdayreminder.model.Birthday
import com.adeleke.samad.birthdayreminder.model.makeMonthNotificationString
import com.adeleke.samad.birthdayreminder.network.FirebaseUtil
import com.adeleke.samad.birthdayreminder.util.ITEM_DETAIL_TAG
import com.adeleke.samad.birthdayreminder.util.MONTH_NOTIFICATION_ID
import com.adeleke.samad.birthdayreminder.util.NOTIFICATION_BROADCAST_REQUEST_CODE
import com.adeleke.samad.birthdayreminder.util.PRIMARY_CHANNEL_ID
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class NotificationHelper private constructor(val context: Context) {
    private val TAG = javaClass.simpleName
    private lateinit var mNotifyManager: NotificationManager

    init {
        createNotificationChannel()
    }

    companion object {
        private var instance: NotificationHelper? = null
        fun getInstance(context: Context): NotificationHelper {
            if (instance == null) {
                instance = NotificationHelper(context)
            }
            return instance as NotificationHelper
        }
    }


    fun cancelNotification(notificationId: Int) {
        mNotifyManager.cancel(notificationId)
    }


    fun sendNotification(birthday: Birthday) {
        val notifyBuilder = getNotificationBuilder(birthday)
        mNotifyManager.notify(birthday.notificationId, notifyBuilder!!.build())
    }
//
//    fun sendMonthNotification(month: String) {
//        val notifyBuilder = getMonthlyNotificationBuilder(month)
//        mNotifyManager.notify(MONTH_NOTIFICATION_ID, notifyBuilder!!.build())
//    }


    // Builder responsible for constructing notification UI and the pending intents associated with it,
    // unique for each birthday, so that birthdays don't overwrite each other
    private fun getNotificationBuilder(birthday: Birthday): NotificationCompat.Builder? {
//        val mainPendingIntent = getMainPendingIntent(birthday)
        val smsPendingIntent = getSendTextPendingIntent(birthday)

        return NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID)
            .setContentTitle("It's ${birthday.name}'s birthday!")
            .setContentText(context.getString(R.string.send))
            .setSmallIcon(R.drawable.ic_birthday)
            .setContentIntent(smsPendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
//            .addAction(R.drawable.ic_message, context.getString(R.string.send), smsPendingIntent)
    }

    fun sendMonthNotification(month: String){
        val pendingIntent = getBirthdayListPendingIntent()
        val firebaseUtil = FirebaseUtil.getInstance(context)

        val query = firebaseUtil.birthdayReference.child(firebaseUtil.mAuth.currentUser!!.uid)
            .orderByChild("monthOfBirth").equalTo(month)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val birthdayList = mutableListOf<Birthday>()
                for (singleSnapshot in snapshot.children) {
                    val birthday = singleSnapshot.getValue(Birthday::class.java)!!
                    birthdayList.add(birthday)
                }
                val notifyString = makeMonthNotificationString(birthdayList)
                val builder = NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID)
                    .setContentTitle("Birthdays this month!")
                    .setContentText(notifyString)
                    .setSmallIcon(R.drawable.ic_birthday)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                mNotifyManager.notify(MONTH_NOTIFICATION_ID, builder.build())
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }


    // Intent that opens the Message App with the birthday data
    private fun getSendTextPendingIntent(birthday: Birthday): PendingIntent? {
        Log.d(TAG, "getSendTextPendingIntent: called to make the pending request to start sms app")
        val smsNumber = "smsto:" + birthday.phoneNumber
        val smsMessage = birthday.textMessage
        val smsIntent = Intent(Intent.ACTION_SENDTO)
        smsIntent.data = Uri.parse(smsNumber)
        smsIntent.putExtra("sms_body", smsMessage)

        // If package resolves (target app installed), send intent.
        if (smsIntent.resolveActivity(context.packageManager) != null) {

            return PendingIntent.getActivity(
                context,
                NOTIFICATION_BROADCAST_REQUEST_CODE,
                smsIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        } else {
            Log.d(TAG, "Can't resolve app for ACTION_SENDTO Intent.")
        }
        Log.d(TAG, "getSendTextPendingIntent: Returning NULL")
        return null
    }


    // Pending intent that opens the detail page
    private fun getMainPendingIntent(birthday: Birthday): PendingIntent? {
        val intent = Intent(context, BirthdayDetailActivity::class.java)
        intent.putExtra(ITEM_DETAIL_TAG, birthday.id)
        return PendingIntent.getActivity(
            context,
            birthday.notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun getBirthdayListPendingIntent(): PendingIntent {
        val i = Intent(context, MainActivity::class.java)
        return PendingIntent.getActivity(
            context,
            MONTH_NOTIFICATION_ID,
            i,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
    private fun createNotificationChannel() {
        mNotifyManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                PRIMARY_CHANNEL_ID,
                "BirthdayReminder Notification", NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description =
                context.getString(R.string.notification_channel_description)
            mNotifyManager.createNotificationChannel(notificationChannel)
        }
    }




//     Function to sent texts completely in the background. Not good practice!

//    fun sendText(smsNumber: String, smsMessage: String) {
//        val smsNumber = birthday.phoneNumber
//        val smsMessage = birthday.textMessage
//
//        val mySmsManager = SmsManager.getDefault()
//        mySmsManager.sendTextMessage(smsNumber,null, smsMessage, null, null)
//    }


}