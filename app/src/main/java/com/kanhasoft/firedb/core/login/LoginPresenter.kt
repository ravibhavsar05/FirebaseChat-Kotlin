package com.kanhasoft.firedb.core.login

import android.app.Activity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult

class LoginPresenter(mLoginView: LoginContract.View) : LoginContract.Presenter, LoginContract.onLoginListener {

    lateinit var mLoginView: LoginContract.View
    lateinit var mLoginInteractor: LoginInteractor

    init {
        this.mLoginView = mLoginView;
        mLoginInteractor = LoginInteractor(this)
    }

    override fun login(activity: Activity, email: String, password: String, deviceToken: String) {
        mLoginInteractor.performFirebaseLogin(activity, email, password, deviceToken)
    }

    override fun onSuccess(message: Task<AuthResult>) {
        mLoginView.onLoginSuccess(message)
    }

    override fun onFailure(message: String) {
        mLoginView.onLoginFailure(message)
    }

}