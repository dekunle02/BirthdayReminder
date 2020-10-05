package com.adeleke.samad.birthdayreminder.birthdayDetail

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context.ALARM_SERVICE
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log
import androidx.lifecycle.*
import com.adeleke.samad.birthdayreminder.model.*
import com.adeleke.samad.birthdayreminder.network.FirebaseUtil
import com.adeleke.samad.birthdayreminder.util.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class BirthdayDetailViewModel(var oldBirthdayId: String, application: Application) :
    AndroidViewModel(application) {
    private val TAG = javaClass.simpleName
    private val context = application.applicationContext

    private lateinit var oldBirthday: Birthday
    private var firebaseUtil: FirebaseUtil
    private val alarmManager: AlarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager


    // Two way observables
    val name = MutableLiveData<String>()
    val dateOfBirth = MutableLiveData<String>()
    val phoneNumber = MutableLiveData<String>()
    val message = MutableLiveData<String>()
    val note = MutableLiveData<String>()


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
    private val _fieldNote = MutableLiveData<String>()
    val fieldNote: LiveData<String>
        get() = _fieldNote


    // Observables
    private val _snackMessage = MutableLiveData<String?>()
    val snackMessage: LiveData<String?>
        get() = _snackMessage

    init {
        firebaseUtil = FirebaseUtil.getInstance(context)
        _fieldName.value = ""
        _fieldDate.value = ""
        _fieldPhoneNumber.value = ""
        _fieldMessage.value = ""
        _fieldNote.value = ""
        initBirthdayWithId()
    }

    // Function called when save is clicked from toolbar
    fun addOrUpdateBirthday() {
        val newBirthday = makeBirthdayObjectFromField()
        FirebaseUtil.getInstance(context).addBirthdayToBirthdays(newBirthday!!)
        newBirthday.setAlarm(context, alarmManager)
    }


    private fun makeBirthdayObjectFromField(): Birthday? {
        val rawDate = dateOfBirth.value!!
        val dateMap = convertToEasyDate(rawDate)

        return Birthday(
            id = oldBirthdayId,
            name = name.value!!.capitalize(),
            dayOfBirth = dateMap["day"],
            monthOfBirth = dateMap["month"],
            yearOfBirth = dateMap["year"],
            phoneNumber = phoneNumber.value,
            textMessage = message.value!!,
            notes = note.value!!
        )
    }

    private fun initBirthdayWithId() {
        if (oldBirthdayId == NEW_BIRTHDAY_ID) {
            oldBirthday = Birthday()
            oldBirthdayId = oldBirthday.id!!
            _fieldMessage.value = oldBirthday.textMessage
        } else {
            getBirthdayWithId(oldBirthdayId)
        }
    }

    private fun getBirthdayWithId(id: String) {
        Log.d(TAG, "getBirthdayWithId: called")
        val query =
            firebaseUtil.birthdayReference.child(firebaseUtil.mAuth.currentUser!!.uid).orderByKey()
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
                _fieldNote.value = oldBirthday.notes
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "onCancelled: called")
            }
        })
    }

    fun loadFieldsWithContact(contactUri: Uri?) {
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.Contacts.DISPLAY_NAME
        )
        val cursor: Cursor? =
            context.contentResolver.query(contactUri!!, projection, null, null, null)
        if (cursor != null && cursor.moveToFirst()) {
            Log.d("Cursor not null ->", "not null")
            val numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
            val number = cursor.getString(numberIndex)
            val name = cursor.getString(nameIndex)
            Log.d("Number, Name", "$number->$name")
            _fieldName.value = name
            _fieldPhoneNumber.value = number
        }
        cursor!!.close()
    }


    // Factory class to pass id into viewmodel object
    class BirthdayDetailViewModelFactory(
        private val oldBirthdayId: String,
        private val application: Application
    ) : ViewModelProvider.Factory {

        @Suppress("unchecked_cast")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(BirthdayDetailViewModel::class.java)) {
                return BirthdayDetailViewModel(oldBirthdayId, application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}