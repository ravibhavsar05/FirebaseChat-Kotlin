package com.kanhasoft.firedb.core.users.devicetoken

interface UpdateDeviceTokenContract {

    interface View {
        fun updateDeviceTokenSuccess()

        fun updateDeviceTokenFailure(message: String)
    }

    interface Presenter {
        fun updateDeviceToken(uid: String, deviceToken: String)
    }

    interface Interactor {
        fun updateDeviceTokenFromFirebase(uid: String, deviceToken: String)
    }

    interface OnUpdateDeviceTokenListener {
        fun updateDeviceTokenSuccess()

        fun updateDeviceTokenFailure(message: String)
    }
}