package com.kanhasoft.firedb.activity

import android.content.Intent
import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.kanhasoft.firedb.R
import com.kanhasoft.firedb.common.Constant
import com.labters.lottiealertdialoglibrary.ClickListener
import com.labters.lottiealertdialoglibrary.DialogTypes
import com.labters.lottiealertdialoglibrary.LottieAlertDialog

class MyAlertDialogActivity : AppCompatActivity() {
    var alertDialog: LottieAlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE); //hide activity title

        val receiverUid = intent.getExtras()!!.getString(
            Constant.ARG_RECEIVER_UID
        )
        val receiver = intent.getExtras()!!.getString(Constant.ARG_RECEIVER)
        val receiverToken = intent.getExtras()!!.getString(Constant.ARG_FIREBASE_TOKEN)
        val sender = intent.getExtras()!!.getString(Constant.ARG_NAME)
        val message = intent.getExtras()!!.getString(Constant.MESSAGE)

        alertDialog = LottieAlertDialog.Builder(this@MyAlertDialogActivity, DialogTypes.TYPE_MESSAGE)
            .setTitle(getString(R.string.text_message))
            .setDescription(sender + getString(R.string.msg_sent_text))
            .setPositiveText(getString(R.string.text_yes))
            .setNegativeText(getString(R.string.text_no))
            .setPositiveListener(object : ClickListener {
                override fun onClick(dialog: LottieAlertDialog) {
                    alertDialog!!.hide()
                    finishAffinity()
                    val intentActivity = Intent(this@MyAlertDialogActivity, ChatActivity::class.java)
                    intentActivity.putExtra(Constant.ARG_RECEIVER, receiver)
                    intentActivity.putExtra(Constant.ARG_RECEIVER_UID, receiverUid)
                    intentActivity.putExtra(Constant.ARG_FIREBASE_TOKEN, receiverToken)
                    startActivity(intentActivity)
                }
            })
            .setNegativeListener(object : ClickListener {
                override fun onClick(dialog: LottieAlertDialog) {
                    alertDialog!!.hide()
                    finishAffinity()
                }
            })
            .build()
        alertDialog!!.setCancelable(false)
        alertDialog!!.show()

    }
}
