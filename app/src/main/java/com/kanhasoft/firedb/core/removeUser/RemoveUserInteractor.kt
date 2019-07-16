package com.kanhasoft.firedb.core.removeUser

import android.app.Activity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.kanhasoft.firedb.common.Constant
import com.kanhasoft.firedb.core.registration.RegistrationInteractor
import com.kanhasoft.firedb.model.UserData

class RemoveUserInteractor(onRemoveUserListener: RemoveUserContract.onRemoveUserListener) :
    RemoveUserContract.Interactor {

    val TAG: String = RegistrationInteractor::class.java.simpleName
    private lateinit var auth: FirebaseAuth
    var database = FirebaseDatabase.getInstance().reference
    private var storageReference: StorageReference? = null

    private var onRemoveUserListener: RemoveUserContract.onRemoveUserListener? = null

    init {
        this.onRemoveUserListener = onRemoveUserListener
    }

    override fun performFirebaseRemove(activity: Activity, userData: UserData) {
        auth = FirebaseAuth.getInstance()
        database
            .child(Constant.ARG_USERS)
            .child(userData.uid.toString())
            .removeValue()
            .addOnCompleteListener(activity, OnCompleteListener { task ->
                storageReference = FirebaseStorage.getInstance().getReference()
                val sRef = storageReference!!.child(
                    Constant.STORAGE_PATH_UPLOADS + userData.uid + ".jpg"
                )

                sRef.delete().addOnSuccessListener(OnSuccessListener {
                    onRemoveUserListener!!.onRemoveUserSuccess(userData)
                })
                    .addOnFailureListener(OnFailureListener {
                        onRemoveUserListener!!.onRemoveUserFailure("Error while removing user")
                    })
            })
            .addOnFailureListener(activity, OnFailureListener { exception: Exception ->
                onRemoveUserListener!!.onRemoveUserFailure(exception.message.toString())
            })
    }
}