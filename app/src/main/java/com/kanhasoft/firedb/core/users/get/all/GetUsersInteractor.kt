package com.kanhasoft.firedb.core.users.get.all

import android.text.TextUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.kanhasoft.firedb.common.Constant
import com.kanhasoft.firedb.model.UserData
import java.util.*

class GetUsersInteractor(onGetAllUsersListener: GetUsersContract.OnGetAllUsersListener) : GetUsersContract.Interactor {

    private var mOnGetAllUsersListener: GetUsersContract.OnGetAllUsersListener? = null

    init {
        this.mOnGetAllUsersListener = onGetAllUsersListener
    }

    override fun getAllUsersFromFirebase() {

        FirebaseDatabase.getInstance().reference.child(Constant.ARG_USERS)
            .addChildEventListener(object : ChildEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onChildMoved(p0: DataSnapshot, p1: String?) {

                }

                override fun onChildChanged(p0: DataSnapshot, p1: String?) {

                }

                override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                    val users = ArrayList<UserData>()

                    val user = dataSnapshot.getValue(UserData::class.java)
                    if (!TextUtils.equals(user!!.uid, FirebaseAuth.getInstance().currentUser!!.uid)) {
                        users.add(user!!)
                    }
                    mOnGetAllUsersListener!!.onGetAllUsersSuccess(users)
                }

                override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                    val user = dataSnapshot.getValue(UserData::class.java)
                    mOnGetAllUsersListener!!.onGetRemoveUsersSuccess(user!!)
                }

            })
    }

    override fun getChatUsersFromFirebase() {

    }
}