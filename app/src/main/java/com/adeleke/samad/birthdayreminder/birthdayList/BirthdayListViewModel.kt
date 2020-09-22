package com.adeleke.samad.birthdayreminder.birthdayList

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.adeleke.samad.birthdayreminder.model.Birthday
import com.adeleke.samad.birthdayreminder.network.FirebaseUtil
import com.adeleke.samad.birthdayreminder.util.monthSortMap
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class BirthdayListViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG: String = javaClass.simpleName
    private val context: Context = application.applicationContext
    private val firebaseUtil = FirebaseUtil.getInstance(context)


    // Observables
    private val _snackMessage = MutableLiveData<String?>()
    val snackMessage: LiveData<String?>
        get() = _snackMessage

    private val _recyclerData = MutableLiveData<MutableList<Birthday>>()
    val recyclerData: LiveData<MutableList<Birthday>>
        get() = _recyclerData



    fun populateRecyclerData() {
        Log.d(TAG, "populateRecyclerData: called")

        val query =
            firebaseUtil.birthdayReference.child(firebaseUtil.mAuth.currentUser!!.uid).orderByKey()

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val myList = mutableListOf<Birthday>()
                if (snapshot == null) {
                    Log.d(TAG, "onDataChange: Snapshot is null. New USER")
                    _recyclerData.value = myList
                } else {
                    for (singleSnapshot in snapshot.children) {
                        val birthday = singleSnapshot.getValue(Birthday::class.java)!!
                        myList.add(birthday)
                    }
                    Log.d(TAG, "onDataChange: $myList")
                    _recyclerData.value = myList
                    filterByMonth()
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "getBirthdayListRequest: cancelled")
            }

        })
    }

    fun insertBirthday(pos: Int, birthday: Birthday) {
        firebaseUtil.restoreBirthdayFromArchive(birthday)
        _recyclerData.value!!.add(pos, birthday)
    }

    fun filterByMonth() {
        val sortedList =
            _recyclerData.value!!.sortedWith(compareBy { monthSortMap[it.monthOfBirth] })
        _recyclerData.value = sortedList.toMutableList()
    }

    fun filterByAlphabetically() {
        val sortedList = _recyclerData.value!!.sortedWith(compareBy { it.name })
        _recyclerData.value = sortedList.toMutableList()
    }

    fun archiveBirthdayAtPosition(position: Int) {
        Log.d(TAG, "archiveBirthdayAtPosition: ${_recyclerData.value}")
        val birthdayAtPosition = _recyclerData.value!![position]
        firebaseUtil.archiveBirthday(birthdayAtPosition)
    }



}