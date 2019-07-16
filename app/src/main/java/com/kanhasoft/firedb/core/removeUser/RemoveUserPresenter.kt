package com.kanhasoft.firedb.core.removeUser

import android.app.Activity
import com.kanhasoft.firedb.model.UserData

class RemoveUserPresenter(removeView: RemoveUserContract.View) : RemoveUserContract.Presenter,
    RemoveUserContract.onRemoveUserListener {

    var removeView: RemoveUserContract.View = removeView
    var removeUserInteractor: RemoveUserInteractor

    init {
        removeUserInteractor = RemoveUserInteractor(this)
    }

    override fun remove(activity: Activity, userData: UserData) {
        removeUserInteractor.performFirebaseRemove(activity, userData)
    }

    override fun onRemoveUserSuccess(userData: UserData) {
        removeView.onDeleteUserSuccess(userData)
    }

    override fun onRemoveUserFailure(message: String) {
        removeView.onDeleteUserFailure(message)
    }
}