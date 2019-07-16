package com.kanhasoft.firedb.core.users.devicetoken

import android.text.TextUtils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kanhasoft.firedb.common.Constant
import com.kanhasoft.firedb.model.UserData
import java.util.*

class UpdateDeviceTokenInteractor(onUpdateDeviceTokenListener: UpdateDeviceTokenContract.OnUpdateDeviceTokenListener) :
    UpdateDeviceTokenContract.Interactor {

    val onUpdateDeviceTokenListener: UpdateDeviceTokenContract.OnUpdateDeviceTokenListener

    init {
        this.onUpdateDeviceTokenListener = onUpdateDeviceTokenListener
    }

    override fun updateDeviceTokenFromFirebase(uid: String, deviceToken: String) {
        val databaseReference = FirebaseDatabase.getInstance().reference

        databaseReference.child(Constant.ARG_USERS).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onCancelled(exception: DatabaseError) {
                onUpdateDeviceTokenListener.updateDeviceTokenFailure("Unable to send message: " + exception.message)
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val dataSnapshots = dataSnapshot.children.iterator()
                val users = ArrayList<UserData>()
                while (dataSnapshots.hasNext()) {
                    val dataSnapshotChild = dataSnapshots.next()
                    val user = dataSnapshotChild.getValue(UserData::class.java)

                    if (TextUtils.equals(user!!.uid, uid)) {
                        onUpdateDeviceTokenListener.updateDeviceTokenSuccess()
                    }

                }
            }
        })
    }

}