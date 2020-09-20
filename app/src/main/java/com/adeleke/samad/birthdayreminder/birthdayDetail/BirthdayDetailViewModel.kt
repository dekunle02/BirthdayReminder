package com.adeleke.samad.birthdayreminder.birthdayDetail

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.adeleke.samad.birthdayreminder.util.convertToEasyDate
import com.adeleke.samad.birthdayreminder.model.Birthday
import com.adeleke.samad.birthdayreminder.network.FirebaseUtil
import com.adeleke.samad.birthdayreminder.util.getSimpleDate

class BirthdayDetailViewModel(var oldBirthdayId: String, application: Application) : AndroidViewModel(application) {
    private val TAG = javaClass.simpleName
    private val context = application.applicationContext

    private lateinit var oldBirthday: Birthday


    // Two way observables
    val name = MutableLiveData<String>()
    val dateOfBirth = MutableLiveData<String>()
    val phoneNumber = MutableLiveData<String>()
    val message = MutableLiveData<String>()

    // Observables
    private val _snackMessage = MutableLiveData<String?>()
    val snackMessage: LiveData<String?>
        get() = _snackMessage

    init {
        initBirthdayWithId()
    }

    fun addOrUpdateBirthday() {
        val newBirthday = makeBirthdayObject()
        FirebaseUtil.getInstance(context).addBirthday(newBirthday!!)
    }


    private fun makeBirthdayObject(): Birthday? {
        if (name.value!!.isEmpty()) {
            _snackMessage.value = "Name cannot be left blank!"
            return null
        }
        if (dateOfBirth.value!!.isEmpty()) {
            _snackMessage.value = "Date of birth cannot be blank"
            return null
        }
        if (phoneNumber.value!!.isEmpty()) {
            _snackMessage.value = "Leave a phone number"
            return null
        }
        if (message.value!!.isEmpty()) {
            _snackMessage.value = "Leave a text message!"
            return null
        }
        val rawDate = dateOfBirth.value!!
        val dateMap = convertToEasyDate(rawDate)

        val birthday = Birthday(
            id = oldBirthdayId,
            name = name.value!!,
            dayOfBirth = dateMap.get("day")!!,
            monthOfBirth = dateMap.get("month")!!,
            yearOfBirth = dateMap.get("year")!!,
            phoneNumber = phoneNumber.value!!,
            textMessage = message.value!!
        )
        Log.d(TAG, "birthday object -> $birthday")
        return birthday
    }

    private fun initBirthdayWithId() {
        if (oldBirthdayId == "-1") {
            oldBirthday = Birthday()
            oldBirthdayId = oldBirthday.id
        } else {
            oldBirthday = FirebaseUtil.getInstance(context).getBirthdayWithId(oldBirthdayId)!!
            name.value = oldBirthday.name
            dateOfBirth.value = oldBirthday.getSimpleDate()
            phoneNumber.value = oldBirthday.phoneNumber
            message.value = oldBirthday.textMessage
        }

    }

    class BirthdayDetailViewModelFactory(
        private val oldBirthdayId: String,
        private val application: Application
    ) : ViewModelProvider.Factory{

        @Suppress("unchecked_cast")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(BirthdayDetailViewModel::class.java)) {
                return BirthdayDetailViewModel(oldBirthdayId, application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }

    }

}