package com.adeleke.samad.birthdayreminder.birthdayDetail

import android.app.Application
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log
import androidx.lifecycle.*
import com.adeleke.samad.birthdayreminder.R
import com.adeleke.samad.birthdayreminder.model.*
import com.adeleke.samad.birthdayreminder.network.FirebaseUtil
import com.adeleke.samad.birthdayreminder.util.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class BirthdayDetailViewModel(var passedBirthdayId: String, application: Application) :
    AndroidViewModel(application) {
    private val TAG = javaClass.simpleName
    private val context = application.applicationContext

    private lateinit var currentBirthday: Birthday
    private var firebaseUtil: FirebaseUtil


    // Two way observables for the fields
    val name = MutableLiveData<String>()
    val dateOfBirth = MutableLiveData<String>()
    val phoneNumber = MutableLiveData<String>()
    val message = MutableLiveData<String>()
    val note = MutableLiveData<String>()

    // Observables
    private val _snackMessage = MutableLiveData<String?>()
    val snackMessage: LiveData<String?>
        get() = _snackMessage

    init {
        firebaseUtil = FirebaseUtil.getInstance(context)
        initBirthdayWithId()
    }

    // Function to prepopulate the fields with the passed birthday Info
    private fun initBirthdayWithId() {
        if (passedBirthdayId == NEW_BIRTHDAY_ID) {
            currentBirthday = Birthday()
            name.value = ""
            dateOfBirth.value = ""
            phoneNumber.value = ""
            message.value = currentBirthday.textMessage
            note.value = currentBirthday.notes
        } else {
            getBirthdayWithId(passedBirthdayId)
        }
    }

    // Function called when save is clicked from toolbar
    fun addOrUpdateBirthday() {
        val newBirthday = makeBirthdayObjectFromField()
        FirebaseUtil.getInstance(context).addBirthdayToBirthdays(newBirthday!!)
        _snackMessage.value = context.getString(R.string.birthday_saved)
    }

    private fun makeBirthdayObjectFromField(): Birthday? {
        val rawDate = dateOfBirth.value!!
        val dateMap = convertToEasyDate(rawDate)

        return Birthday(
            id = currentBirthday.id,
            name = name.value!!.capitalize(),
            dayOfBirth = dateMap["day"],
            monthOfBirth = dateMap["month"],
            yearOfBirth = dateMap["year"],
            phoneNumber = phoneNumber.value,
            textMessage = message.value!!,
            notes = note.value!!
        )
    }

    private fun getBirthdayWithId(id: String) {
        val query =
            firebaseUtil.birthdayReference.child(firebaseUtil.mAuth.currentUser!!.uid).orderByKey()
                .equalTo(id)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (singleSnapshot in snapshot.children) {
                    currentBirthday = singleSnapshot.getValue(Birthday::class.java)!!
                }
                name.value = currentBirthday.name
                dateOfBirth.value = currentBirthday.getSimpleDate()
                phoneNumber.value = currentBirthday.phoneNumber
                message.value = currentBirthday.textMessage
                note.value = currentBirthday.notes
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
            val contactName = cursor.getString(nameIndex)
            Log.d("Number, Name", "$number->$contactName")

            name.value = contactName
            phoneNumber.value = number
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