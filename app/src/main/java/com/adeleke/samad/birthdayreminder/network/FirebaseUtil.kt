package com.adeleke.samad.birthdayreminder.network

import android.content.Context
import android.util.Log
import com.adeleke.samad.birthdayreminder.model.Birthday
import com.adeleke.samad.birthdayreminder.util.FIREBASE_ARCHIVE_NODE
import com.adeleke.samad.birthdayreminder.util.FIREBASE_BIRTHDAY_NODE
import com.adeleke.samad.birthdayreminder.util.OAUTH_CLIENT_ID
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class FirebaseUtil private constructor(context: Context) {
    private val TAG: String = FirebaseUtil::class.java.simpleName

    var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    var mUser: FirebaseUser?
    private var gso: GoogleSignInOptions =
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(OAUTH_CLIENT_ID)
            .requestEmail()
            .build()
    var mGoogleSignInClient: GoogleSignInClient
    var birthdayReference: DatabaseReference
    var archiveReference: DatabaseReference


    companion object {
        private var instance: FirebaseUtil? = null
        fun getInstance(context: Context): FirebaseUtil {
            if (instance == null) {
                instance = FirebaseUtil(context)
            }
            return instance as FirebaseUtil
        }
    }

    init {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        if (mAuth == null) {
            Log.d(TAG, "mAuth: is null")
        }
        mGoogleSignInClient = GoogleSignIn.getClient(context, gso)
        mUser = mAuth.currentUser

        val database = FirebaseDatabase.getInstance()
        if (mUser == null) {
            Log.d(TAG, "Unable to get data reference because mUSer is null!")
        }
        birthdayReference = database.getReference(FIREBASE_BIRTHDAY_NODE)
        archiveReference = database.getReference(FIREBASE_ARCHIVE_NODE)
    }

    private fun getBirthdayByMonth() {
        Log.d(TAG, "getBirthdayByMonth: CALLED")
        val query = birthdayReference.child(mAuth.currentUser!!.uid).orderByChild("monthOfBirth").equalTo("Oct")
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val myList = mutableListOf<Birthday>()
                if (snapshot == null) {
                    Log.d(TAG, "onDataChange: snapShot is NULL")
                } else {
                    for (singleSnapshot in snapshot.children) {
                        val birthday = singleSnapshot.getValue(Birthday::class.java)!!
                        Log.d(TAG, "onDataChange: $birthday")
                        myList.add(birthday)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }


    fun addBirthdayToBirthdays(birthday: Birthday) {
        birthdayReference.child(mAuth.currentUser!!.uid).child(birthday.id!!).setValue(birthday)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d(TAG, "Birthday inserted successfully!")
                } else if (it.isCanceled) {
                    Log.d(TAG, "Birthday insertion cancelled!")
                }
            }
    }

    private fun deleteBirthdayFromBirthdays(birthdayId: String) {
        birthdayReference.child(mAuth.currentUser!!.uid).child(birthdayId!!).removeValue()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d(TAG, "Birthday deleted successfully!")
                } else if (it.isCanceled) {
                    Log.d(TAG, "Birthday deletion failed cancelled!")
                }
            }
    }

    fun deleteUserInformation(userId: String) {
        birthdayReference.child(userId).removeValue()
        archiveReference.child(userId).removeValue()
    }


    private fun addBirthdayToArchive(birthday: Birthday) {
        archiveReference.child(mAuth.currentUser!!.uid).child(birthday.id!!).setValue(birthday)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d(TAG, "Birthday inserted successfully!")
                } else if (it.isCanceled) {
                    Log.d(TAG, "Birthday insertion cancelled!")
                }
            }
    }

    fun deleteBirthdayFromArchive(birthdayId: String) {
        archiveReference.child(mAuth.currentUser!!.uid).child(birthdayId!!).removeValue()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d(TAG, "Birthday deleted successfully!")
                } else if (it.isCanceled) {
                    Log.d(TAG, "Birthday deletion failed cancelled!")
                }
            }
    }

    fun sendVerificationEmail() {
        mAuth.currentUser!!.sendEmailVerification()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Email successfully sent!")
                }
            }
    }

    fun sendPasswordResetPassword(emailAddress: String) {
        mAuth.sendPasswordResetEmail(emailAddress)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Email sent!")
                }
            }
    }

    fun archiveBirthday(birthday: Birthday) {
        deleteBirthdayFromBirthdays(birthday.id!!)
        addBirthdayToArchive(birthday)
    }

    fun restoreBirthdayFromArchive(birthday: Birthday) {
        deleteBirthdayFromArchive(birthday.id!!)
        addBirthdayToBirthdays(birthday)
    }

    fun deleteAll() {
        archiveReference.child(mAuth.currentUser!!.uid).removeValue()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d(TAG, "All birthdays deleted successfully!")
                } else if (it.isCanceled) {
                    Log.d(TAG, "Birthday deletion failed cancelled!")
                }
            }
    }

    fun signOut() {
        // Firebase sign out
        mAuth.signOut()

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener() {
            Log.d(TAG, "GoogleSigned OUT")
        }

        Log.d(TAG, "Signed out")
    }

}