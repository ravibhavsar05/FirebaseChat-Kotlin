package com.kanhasoft.firedb.core.users.get.all

import com.kanhasoft.firedb.model.UserData

interface GetUsersContract {
    interface View {
        fun onGetAllUsersSuccess(users: List<UserData>)
        fun onGetRemoveUsersSuccess(users: UserData)

        fun onGetAllUsersFailure(message: String)
    }

    interface Presenter {
        fun getAllUsers()
    }

    interface Interactor {
        fun getAllUsersFromFirebase()

        fun getChatUsersFromFirebase()
    }

    interface OnGetAllUsersListener {
        fun onGetAllUsersSuccess(users: List<UserData>)
        fun onGetRemoveUsersSuccess(users: UserData)

        fun onGetAllUsersFailure(message: String)
    }
}