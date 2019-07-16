package com.kanhasoft.firedb.core.registration

import android.app.Activity
import android.net.Uri
import com.kanhasoft.firedb.model.UserData

class RegistrationPresenter(mRegisterView: RegisterContract.View) : RegisterContract.Presenter,
    RegisterContract.onRegisterListener {

    var mRegisterView: RegisterContract.View
    var mRegisterInteractor: RegistrationInteractor

    init {
        this.mRegisterView = mRegisterView
        mRegisterInteractor = RegistrationInteractor(this)
    }

    override fun register(activity: Activity, userData: UserData, password: String, filePath: Uri) {
        mRegisterInteractor.performFirebaseRegistration(activity, userData, password, filePath)
    }

    override fun onSuccess() {
        mRegisterView.onRegistrationSuccess()
    }

    override fun onFailure(message: String) {
        mRegisterView.onRegistrationFailure(message)
    }
}