package com.kanhasoft.firedb.core.users.devicetoken

class UpdateDeviceTokenPresenter(view: UpdateDeviceTokenContract.View) : UpdateDeviceTokenContract.Presenter,
    UpdateDeviceTokenContract.OnUpdateDeviceTokenListener {

    var mUpdateDeviceTokenInteractor: UpdateDeviceTokenInteractor
    var view: UpdateDeviceTokenContract.View

    init {
        this.view = view
        mUpdateDeviceTokenInteractor = UpdateDeviceTokenInteractor(this)
    }


    override fun updateDeviceToken(uid: String, deviceToken: String) {
        mUpdateDeviceTokenInteractor.updateDeviceTokenFromFirebase(uid, deviceToken)
    }

    override fun updateDeviceTokenSuccess() {
        view.updateDeviceTokenSuccess()
    }

    override fun updateDeviceTokenFailure(message: String) {
        view.updateDeviceTokenFailure(message)
    }
}