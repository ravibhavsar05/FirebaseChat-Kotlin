package com.kanhasoft.firedb.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.iid.FirebaseInstanceId
import com.kanhasoft.firedb.R
import com.kanhasoft.firedb.common.Constant
import com.kanhasoft.firedb.common.SharedPrefUtil
import com.kanhasoft.firedb.core.users.devicetoken.UpdateDeviceTokenContract
import com.kanhasoft.firedb.core.users.devicetoken.UpdateDeviceTokenPresenter

class SplashActivity : AppCompatActivity(), UpdateDeviceTokenContract.View {

    var sharedPreferencesUtils: SharedPrefUtil? = null
    lateinit var updateDeviceTokenPresenter: UpdateDeviceTokenPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        sharedPreferencesUtils = SharedPrefUtil(this@SplashActivity)
        updateDeviceTokenPresenter = UpdateDeviceTokenPresenter(this)

        sharedPreferencesUtils!!.saveString(
            Constant.ARG_FIREBASE_TOKEN,
            FirebaseInstanceId.getInstance().getToken().toString()
        )

        Handler().postDelayed(Runnable {
            if (sharedPreferencesUtils!!.getString(Constant.ARG_UID).equals(null)) {

                val intent = Intent(this, LoginActivity::class.java)
                intent.addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or
                            Intent.FLAG_ACTIVITY_NEW_TASK
                )
                startActivity(intent)
            } else {

                //Logic of update device token

                updateDeviceTokenPresenter.updateDeviceToken(
                    sharedPreferencesUtils!!.getString(Constant.ARG_UID).toString(),
                    FirebaseInstanceId.getInstance().getToken().toString()
                )
            }
        }, 1500)
    }

    override fun updateDeviceTokenSuccess() {
        val intent = Intent(this, ListActivity::class.java)
        intent.addFlags(
            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                    Intent.FLAG_ACTIVITY_NEW_TASK
        )
        startActivity(intent)
    }

    override fun updateDeviceTokenFailure(message: String) {
        Toast.makeText(applicationContext, message, LENGTH_SHORT).show()
    }
}
