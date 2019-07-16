package com.kanhasoft.firedb.core.removeUser

import android.app.Activity
import com.kanhasoft.firedb.model.UserData

interface RemoveUserContract {
    interface Presenter {
        fun remove(activity: Activity, userData: UserData)
    }

    interface Interactor {
        fun performFirebaseRemove(activity: Activity, userData: UserData)
    }

    interface onRemoveUserListener {
        fun onRemoveUserSuccess(userData: UserData)
        fun onRemoveUserFailure(message: String)
    }


    interface View {
        fun onDeleteUserSuccess(userData: UserData)
        fun onDeleteUserFailure(message: String)
    }


}