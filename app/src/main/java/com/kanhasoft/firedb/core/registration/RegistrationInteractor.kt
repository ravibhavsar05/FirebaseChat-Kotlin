package com.kanhasoft.firedb.core.registration

import android.app.Activity
import android.net.Uri
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.kanhasoft.firedb.common.Constant
import com.kanhasoft.firedb.common.SharedPrefUtil
import com.kanhasoft.firedb.model.UserData

class RegistrationInteractor(onRegisterListener: RegisterContract.onRegisterListener) : RegisterContract.Interactor {

    val TAG: String = RegistrationInteractor::class.java.simpleName
    var sharedPreferencesUtils: SharedPrefUtil? = null
    private lateinit var auth: FirebaseAuth
    var database = FirebaseDatabase.getInstance().reference
    private var storageReference: StorageReference? = null

    private var mOnRegisterClickListener: RegisterContract.onRegisterListener? = null

    init {
        this.mOnRegisterClickListener = onRegisterListener
    }

    override fun performFirebaseRegistration(activity: Activity, userData: UserData, password: String, filePath: Uri) {
        sharedPreferencesUtils = SharedPrefUtil(activity)

        auth = FirebaseAuth.getInstance()
        if (userData!!.uid != null) {
            updateFirebaseToken(userData!!.uid!!, userData, activity)
            uploadFile(filePath, activity, userData.uid.toString())
        } else {
            auth.createUserWithEmailAndPassword(userData!!.email!!, password)
                .addOnCompleteListener(activity, OnCompleteListener<AuthResult> { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        userData.uid = task.result!!.user.uid
                        updateFirebaseToken(task.result!!.user.uid, userData, activity)
                        /* sharedPreferencesUtils!!.saveString(Constant.ARG_UID, task.result!!.user.uid.toString())
                         sharedPreferencesUtils!!.saveString(Constant.ARG_NAME, userData.display_name.toString())*/
                        uploadFile(filePath, activity, task.result!!.user.uid)
                    } else {
                        // If sign in fails, display a Message to the user.
                        mOnRegisterClickListener!!.onFailure(message = task.exception.toString())
                    }
                })
        }

    }

    private fun updateFirebaseToken(uid: String, userData: UserData, activity: Activity) {

        userData.profile_pic = userData.uid + ".jpg"
        database
            .child(Constant.ARG_USERS)
            .child(uid)
            .setValue(userData)
            .addOnCompleteListener(activity, OnCompleteListener { task ->

            })

    }

    private fun uploadFile(filePath: Uri, activity: Activity, uid: String) {
        storageReference = FirebaseStorage.getInstance().getReference();
        //database = FirebaseDatabase.getInstance().getReference(Constant.DATABASE_PATH_UPLOADS);

        //checking if file is available
        if (filePath != null) {
            //displaying progress dialog while image is uploading

            //getting the storage reference
            val sRef = storageReference!!.child(
                Constant.STORAGE_PATH_UPLOADS + uid + ".jpg"
            )

            //adding the file to reference
            sRef.putFile(filePath)
                .addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->
                    //dismissing the progress dialog

                    //creating the upload object to store uploaded image details

                    //adding an upload to firebase database
                    /*          val uploadId = database.push().getKey()
                              database.child(uploadId.toString()).setValue(filePath)*/

                    mOnRegisterClickListener!!.onSuccess()

                })
                .addOnFailureListener(OnFailureListener { exception ->
                    Toast.makeText(activity, exception.message, Toast.LENGTH_LONG).show()
                    mOnRegisterClickListener!!.onFailure(exception.message.toString())
                })
                .addOnProgressListener(OnProgressListener<UploadTask.TaskSnapshot> { taskSnapshot ->
                    //displaying the upload progress
                    val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
                })
        } else {
            //display an error if no file is selected
        }
    }
}
