package com.adeleke.samad.birthdayreminder.birthdayDetail

import android.app.Application
import android.util.Log
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.lifecycle.*
import com.adeleke.samad.birthdayreminder.util.convertToEasyDate
import com.adeleke.samad.birthdayreminder.model.Birthday
import com.adeleke.samad.birthdayreminder.network.FirebaseUtil
import com.adeleke.samad.birthdayreminder.util.getSimpleDate
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class BirthdayDetailViewModel(var oldBirthdayId: String, application: Application) : AndroidViewModel(application) {
    private val TAG = javaClass.simpleName
    private val context = application.applicationContext

    private lateinit var oldBirthday: Birthday
    private var firebaseUtil: FirebaseUtil


    // Two way observables
    val name = MutableLiveData<String>()
    val dateOfBirth = MutableLiveData<String>()
    val phoneNumber = MutableLiveData<String>()
    val message = MutableLiveData<String>()


    private val _fieldName = MutableLiveData<String>()
    val fieldName: LiveData<String>
        get() = _fieldName

      private val _fieldDate = MutableLiveData<String>()
    val fieldDate: LiveData<String>
        get() = _fieldDate
      private val _fieldPhoneNumber = MutableLiveData<String>()
    val fieldPhoneNumber: LiveData<String>
        get() = _fieldPhoneNumber
      private val _fieldMessage = MutableLiveData<String>()
    val fieldMessage: LiveData<String>
        get() = _fieldMessage


    // Observables
    private val _snackMessage = MutableLiveData<String?>()
    val snackMessage: LiveData<String?>
        get() = _snackMessage

    init {
        firebaseUtil = FirebaseUtil.getInstance(context)
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
            getBirthdayWithId(oldBirthdayId)
        }
    }

    private fun getBirthdayWithId(id: String) {
        Log.d(TAG, "getBirthdayWithId: called")
        val query = firebaseUtil.birthdayReference.child(firebaseUtil.mAuth.currentUser!!.uid).orderByKey()
            .equalTo(id)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (singleSnapshot in snapshot.children) {
                    oldBirthday = singleSnapshot.getValue(Birthday::class.java)!!
                }
                _fieldName.value = oldBirthday.name
                _fieldDate.value = oldBirthday.getSimpleDate()
                _fieldPhoneNumber.value = oldBirthday.phoneNumber
                _fieldMessage.value = oldBirthday.textMessage
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "onCancelled: called")
            }
        })
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