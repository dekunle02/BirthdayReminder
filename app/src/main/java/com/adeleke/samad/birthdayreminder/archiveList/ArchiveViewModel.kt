package com.adeleke.samad.birthdayreminder.archiveList

import android.app.AlarmManager
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.adeleke.samad.birthdayreminder.model.Birthday
import com.adeleke.samad.birthdayreminder.model.cancelAlarm
import com.adeleke.samad.birthdayreminder.model.setAlarm
import com.adeleke.samad.birthdayreminder.network.FirebaseUtil
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class ArchiveViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG: String = javaClass.simpleName
    private val context: Context = application.applicationContext
    private val firebaseUtil = FirebaseUtil.getInstance(context)
    private val alarmManager: AlarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager


    // Observables
    private val _snackMessage = MutableLiveData<String?>()
    val snackMessage: LiveData<String?>
        get() = _snackMessage

    private val _recyclerData = MutableLiveData<MutableList<Birthday>>()
    val recyclerData: LiveData<MutableList<Birthday>>
        get() = _recyclerData


    // Populates archive RecyclerView and removes each birthday's alarm
    fun populateRecyclerData() {
        val query =
            firebaseUtil.archiveReference.child(firebaseUtil.mAuth.currentUser!!.uid).orderByKey()
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val myList = mutableListOf<Birthday>()
                for (singleSnapshot in snapshot.children) {
                    val birthday = singleSnapshot.getValue(Birthday::class.java)!!
                    myList.add(birthday)
                }
                _recyclerData.value = myList
                for (birthday in myList) {
                    birthday.cancelAlarm(context, alarmManager)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "load: cancelled")
            }
        })
    }

    // Functions for adding or removing item from archive
    fun restoreBirthdayFromArchive(birthdaySwiped: Birthday) {
        firebaseUtil.restoreBirthdayFromArchive(birthdaySwiped)
    }

    fun finalDeleteBirthday(birthdaySwiped: Birthday) {
        firebaseUtil.deleteBirthdayFromArchive(birthdaySwiped.id!!)
    }

    fun deleteAll() {
        firebaseUtil.deleteAll()
    }


}