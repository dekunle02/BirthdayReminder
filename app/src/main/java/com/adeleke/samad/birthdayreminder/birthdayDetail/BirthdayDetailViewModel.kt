package com.adeleke.samad.birthdayreminder.birthdayDetail

import android.app.Application
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log
import androidx.lifecycle.*
import com.adeleke.samad.birthdayreminder.model.Birthday
import com.adeleke.samad.birthdayreminder.network.FirebaseUtil
import com.adeleke.samad.birthdayreminder.util.NEW_BIRTHDAY_ID
import com.adeleke.samad.birthdayreminder.util.convertToEasyDate
import com.adeleke.samad.birthdayreminder.util.getSimpleDate
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class BirthdayDetailViewModel(var oldBirthdayId: String, application: Application) :
    AndroidViewModel(application) {
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

    // Function called when save is clicked from toolbar
    fun addOrUpdateBirthday() {
        val newBirthday = makeBirthdayObjectFromField()
        FirebaseUtil.getInstance(context).addBirthdayToBirthdays(newBirthday!!)
    }


    private fun makeBirthdayObjectFromField(): Birthday? {
        val rawDate = dateOfBirth.value!!
        val dateMap = convertToEasyDate(rawDate)

        val birthday = Birthday(
            id = oldBirthdayId,
            name = name.value!!.capitalize(),
            dayOfBirth = dateMap["day"]!!,
            monthOfBirth = dateMap["month"]!!,
            yearOfBirth = dateMap["year"]!!,
            phoneNumber = phoneNumber.value!!,
            textMessage = message.value!!
        )
        Log.d(TAG, "birthday object made -> $birthday")
        return birthday
    }

    private fun initBirthdayWithId() {
        if (oldBirthdayId == NEW_BIRTHDAY_ID) {
            oldBirthday = Birthday()
            oldBirthdayId = oldBirthday.id
            _fieldMessage.value = oldBirthday.textMessage
            Log.d(TAG, "textMessage value: ${oldBirthday.textMessage}")
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