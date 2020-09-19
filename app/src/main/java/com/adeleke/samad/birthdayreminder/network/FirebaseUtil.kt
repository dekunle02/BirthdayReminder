package com.adeleke.samad.birthdayreminder.network

import android.content.Context
import android.util.Log
import com.adeleke.samad.birthdayreminder.FIREBASE_BIRTHDAY_NODE
import com.adeleke.samad.birthdayreminder.OAUTH_CLIENT_ID
import com.adeleke.samad.birthdayreminder.model.Birthday
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
    private var gso: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(OAUTH_CLIENT_ID)
        .requestEmail()
        .build()
    var mGoogleSignInClient: GoogleSignInClient
    private var birthdayReference: DatabaseReference


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
        mGoogleSignInClient = GoogleSignIn.getClient(context, gso)
        mUser = mAuth.currentUser
        val database = FirebaseDatabase.getInstance()
        if (mUser == null) {
            Log.d(TAG, "Unable to get data reference because mUSer is null!")
        }
        birthdayReference = database.getReference(FIREBASE_BIRTHDAY_NODE)
    }


    fun addBirthday(birthday: Birthday) {
        birthdayReference.child(mUser!!.uid).child(birthday.id).setValue(birthday).addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d(TAG, "Birthday inserted successfully!")
            } else if (it.isCanceled) {
                Log.d(TAG, "Birthday insertion cancelled!")
            }
        }
    }


    fun getBirthdayWithId(id: String): Birthday? {
        Log.d(TAG, "getBirthdayWithId: called")
        var birthday: Birthday? = null
        val query = birthdayReference.child(mUser!!.uid).orderByKey()
            .equalTo(id)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (singleSnapshot in snapshot.children) {
                    birthday = singleSnapshot.getValue(Birthday::class.java)!!
                    Log.d(TAG, "getBirthday: $birthday")
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "getBirthdayRequest: cancelled")
            }

        })

        return birthday
    }

    fun getAllBirthdays(): MutableList<Birthday> {
        Log.d(TAG, "getAllBirthdays: called")
        val birthdayList = mutableListOf<Birthday>()
        val query = birthdayReference.child(mUser!!.uid).orderByKey()

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (singleSnapshot in snapshot.children) {
                    val birthday = singleSnapshot.getValue(Birthday::class.java)!!
                    birthdayList.add(birthday)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "getBirthdayListRequest: cancelled")
            }
        })
        return birthdayList
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