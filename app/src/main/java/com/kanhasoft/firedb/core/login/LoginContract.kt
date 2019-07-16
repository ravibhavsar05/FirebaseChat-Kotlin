package com.kanhasoft.firedb.core.login

import android.app.Activity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult

interface LoginContract {
    interface Presenter {
        fun login(activity: Activity, email: String, password: String, token: String)
    }

    interface Interactor {
        fun performFirebaseLogin(activity: Activity, email: String, password: String, token: String)
    }

    interface onLoginListener {
        fun onSuccess(message: Task<AuthResult>)
        fun onFailure(message: String)
    }

    interface View {
        fun onLoginSuccess(message: Task<AuthResult>)
        fun onLoginFailure(message: String)
    }
}
