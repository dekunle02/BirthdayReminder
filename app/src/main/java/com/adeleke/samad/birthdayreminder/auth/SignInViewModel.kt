package com.adeleke.samad.birthdayreminder.auth

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.adeleke.samad.birthdayreminder.isEmailFormatted
import com.adeleke.samad.birthdayreminder.isPasswordFormatted
import com.adeleke.samad.birthdayreminder.network.FirebaseUtil

class SignInViewModel(application: Application): AndroidViewModel(application) {
    private val TAG = javaClass.simpleName
    private val context = application.applicationContext

    // Two way bindings
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    // Observables
    private val _snackMessage = MutableLiveData<String?>()
    val snackMessage: LiveData<String?>
        get() = _snackMessage

    private val _canNavigateToMain = MutableLiveData<Boolean?>()
    val canNavigateToMain: LiveData<Boolean?>
        get() = _canNavigateToMain


    private val firebaseUtil: FirebaseUtil = FirebaseUtil.getInstance(context)

    fun signIn() {
        Log.d(TAG, "signIn() called with email-> ${email.value}, password-> ${password.value}")
        if (!(email.value!!.isEmailFormatted() && password.value!!.isPasswordFormatted())) {
            Log.d(TAG, "IncorrectFormat")
            _snackMessage.value = "Incorrect Format"
            return
        }
        firebaseUtil.mAuth.signInWithEmailAndPassword(email.value!!, password.value!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signIn is Successful")
                    _canNavigateToMain.value = true
                } else {
                    Log.d(TAG, "signIn is Failed! -> ${task.exception}")
                }
            }
    }

    fun doneNavigateToMain() {
        _canNavigateToMain.value = false
    }


}