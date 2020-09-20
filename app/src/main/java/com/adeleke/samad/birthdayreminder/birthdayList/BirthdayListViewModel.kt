package com.adeleke.samad.birthdayreminder.birthdayList

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.adeleke.samad.birthdayreminder.network.FirebaseUtil

class BirthdayListViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG: String = javaClass.simpleName
    private val context: Context = application.applicationContext

    // Observables
    private val _snackMessage = MutableLiveData<String?>()
    val snackMessage: LiveData<String?>
        get() = _snackMessage

    private val _recyclerAdapter = MutableLiveData<BirthdayListAdapter?>()
    val recyclerAdapter: LiveData<BirthdayListAdapter?>
        get() = _recyclerAdapter


    init {

    }

//    fun populateRecyclerView () {
//        Log.d(TAG, "BirthdayList View model init: called")
//        val allBirthdays = FirebaseUtil.getInstance(context).getAllBirthdays().toList()
//        val adapter = BirthdayListAdapter()
//        adapter.data = allBirthdays
//        _recyclerAdapter.value = adapter
//    }



}