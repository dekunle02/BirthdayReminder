package com.adeleke.samad.birthdayreminder.auth

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.adeleke.samad.birthdayreminder.R
import com.adeleke.samad.birthdayreminder.util.isEmailFormatted
import com.adeleke.samad.birthdayreminder.util.isPasswordFormatted
import com.adeleke.samad.birthdayreminder.network.FirebaseUtil
import com.google.firebase.auth.GoogleAuthProvider

class SignInViewModel(application: Application): AndroidViewModel(application) {
    private val TAG = javaClass.simpleName
    private val context = application.applicationContext

    // Two way bindings
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    // Observables
    private val _showProgressBar = MutableLiveData<Boolean>()
    val showProgressBar: LiveData<Boolean?>
        get() = _showProgressBar

    private val _snackMessage = MutableLiveData<String?>()
    val snackMessage: LiveData<String?>
        get() = _snackMessage

    private val _canNavigateToMain = MutableLiveData<Boolean?>()
    val canNavigateToMain: LiveData<Boolean?>
        get() = _canNavigateToMain


    private val firebaseUtil: FirebaseUtil = FirebaseUtil.getInstance(context)

    fun signIn() {
        _showProgressBar.value = true
        firebaseUtil.mAuth.signInWithEmailAndPassword(email.value!!, password.value!!)
            .addOnCompleteListener { task ->
                _showProgressBar.value = false
                if (task.isSuccessful) {
                    _canNavigateToMain.value = true
                } else {
                    _snackMessage.value = context.getString(R.string.error) + task.exception!!.message
                    Log.d(TAG, "signIn Failed! -> ${task.exception}")
                }
            }

    }

    fun firebaseAuthWithGoogle(idToken: String) {
        _showProgressBar.value = true
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseUtil.mAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                _showProgressBar.value = false
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    _canNavigateToMain.value = true
                } else {
                    Log.d(TAG, "signInWithCredential:failure", task.exception)
                    _snackMessage.value = "Sign in Failed. ${task.exception.toString()}"
                }
            }
    }

    fun doneNavigateToMain() {
        _canNavigateToMain.value = false
    }

    fun sendForgotPasswordEmail() {
        firebaseUtil.sendPasswordResetPassword(email.value!!)
        _snackMessage.value = context.getString(R.string.email_sent)
    }


}