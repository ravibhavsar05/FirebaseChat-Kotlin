package com.kanhasoft.firedb.core.registration

import android.app.Activity
import android.net.Uri
import com.kanhasoft.firedb.model.UserData

interface RegisterContract {
    interface Presenter {
        fun register(
            activity: Activity, userData: UserData, password: String,
            filePath: Uri
        )
    }

    interface Interactor {
        fun performFirebaseRegistration(
            activity: Activity,
            userData: UserData, password: String,
            filePath: Uri
        )
    }

    interface onRegisterListener {
        fun onSuccess()
        fun onFailure(message: String)
    }

    interface View {
        fun onRegistrationSuccess()
        fun onRegistrationFailure(message: String)
    }
}