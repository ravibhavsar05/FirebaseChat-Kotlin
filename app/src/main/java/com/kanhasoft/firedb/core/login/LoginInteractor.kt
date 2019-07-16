package com.kanhasoft.firedb.core.login

import android.app.Activity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.kanhasoft.firedb.common.Constant
import com.kanhasoft.firedb.common.SharedPrefUtil

class LoginInteractor(onLoginListener: LoginContract.onLoginListener) : LoginContract.Interactor {

    val TAG: String = LoginInteractor::class.java.simpleName
    var sharedPreferencesUtils: SharedPrefUtil? = null
    private lateinit var auth: FirebaseAuth
    val database = FirebaseDatabase.getInstance().reference

    private var mOnLoginListener: LoginContract.onLoginListener? = null

    init {
        this.mOnLoginListener = onLoginListener

    }

    override fun performFirebaseLogin(activity: Activity, email: String, password: String, deviceToken: String) {
        sharedPreferencesUtils = SharedPrefUtil(activity)


        auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(activity, OnCompleteListener<AuthResult> { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    mOnLoginListener!!.onSuccess(task)
                    updateFirebaseToken(
                        task.result!!.user.uid, deviceToken
                    )
                    sharedPreferencesUtils!!.saveString(Constant.ARG_UID, task.result!!.user.uid.toString())
                } else {
                    // If sign in fails, display a Message to the user.
                    mOnLoginListener!!.onFailure(message = task.exception.toString())
                }
            })

    }

    private fun updateFirebaseToken(uid: String, deviceToken: String) {
        database
            .child(Constant.ARG_USERS)
            .child(uid).child("token").setValue(deviceToken)
    }
}
