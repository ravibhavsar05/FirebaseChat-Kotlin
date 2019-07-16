package com.kanhasoft.firedb.core.users.get.all

import com.kanhasoft.firedb.model.UserData

class GetUsersPresenter(view: GetUsersContract.View) : GetUsersContract.Presenter,
    GetUsersContract.OnGetAllUsersListener {
    var mGetUsersInteractor: GetUsersInteractor? = null
    var view: GetUsersContract.View? = null

    init {
        this.view = view
        mGetUsersInteractor = GetUsersInteractor(this)
    }

    override fun getAllUsers() {
        mGetUsersInteractor!!.getAllUsersFromFirebase()
    }

    override fun onGetAllUsersSuccess(users: List<UserData>) {
        view!!.onGetAllUsersSuccess(users)
    }

    override fun onGetAllUsersFailure(message: String) {
        view!!.onGetAllUsersFailure(message)
    }

    override fun onGetRemoveUsersSuccess(users: UserData) {
        view!!.onGetRemoveUsersSuccess(users)
    }
}