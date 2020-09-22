package com.adeleke.samad.birthdayreminder.archiveList

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.adeleke.samad.birthdayreminder.model.Birthday
import com.adeleke.samad.birthdayreminder.network.FirebaseUtil
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class ArchiveViewModel(application: Application): AndroidViewModel(application) {
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
            firebaseUtil.archiveReference.child(firebaseUtil.mAuth.currentUser!!.uid).orderByKey()

        query.addValueEventListener(object : ValueEventListener {
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
                }

            }
            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "load: cancelled")
            }
        })
    }
}