package com.kanhasoft.firedb.activity

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.iid.FirebaseInstanceId
import com.kanhasoft.firedb.R
import com.kanhasoft.firedb.common.Constant
import com.kanhasoft.firedb.common.MessageBar
import com.kanhasoft.firedb.core.login.LoginContract
import com.kanhasoft.firedb.core.login.LoginPresenter
import com.labters.lottiealertdialoglibrary.DialogTypes
import com.labters.lottiealertdialoglibrary.LottieAlertDialog
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity(), LoginContract.View {

    private var mMassagebar: MessageBar? = null
    private lateinit var loginPresenter: LoginPresenter
    var alertDialog: LottieAlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginPresenter = LoginPresenter(this)

        alertDialog = LottieAlertDialog.Builder(this@LoginActivity, DialogTypes.TYPE_LOADING)
            .setTitle(getString(R.string.loading))
            .setDescription(getString(R.string.msg_please_wait))
            .build()
        alertDialog!!.setCancelable(true)

        ivBack.setOnClickListener(clickListener)
        btnLogin.setOnClickListener(clickListener)
        tvRegister.setOnClickListener(clickListener)

    }

    private val clickListener: View.OnClickListener = View.OnClickListener { view ->
        if (view.id == ivBack.id) {
            onBackPressed()
        }
        if (view.id == btnLogin.id) {
            if (validate()) {
                alertDialog!!.show()
                val email = etEmail.text.toString().trim { it <= ' ' }
                loginPresenter.login(
                    this@LoginActivity, email.toString(), "Admin@123",
                    FirebaseInstanceId.getInstance().getToken().toString()
                )
            }
        }
        if (view.id == tvRegister.id) {
            val intent = Intent(this, CreateDisplayActivity::class.java)
            intent.putExtra(Constant.ACTION, Constant.ADD)
            startActivity(intent)
        }
    }

    private fun validate(): Boolean {
        val email = etEmail.text.toString().trim { it <= ' ' }
        if (email.length == 0) {
            displayMessage(resources.getString(R.string.err_msg_email))
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            displayMessage(resources.getString(R.string.err_msg_valid_email))
            return false
        }
        return true
    }

    private fun displayMessage(msg: String) {
        mMassagebar!!.showWithColor(msg, resources.getString(R.string.msg_color_warning_color))
    }

    override fun onLoginSuccess(message: Task<AuthResult>) {
        alertDialog!!.hide()
        val intent = Intent(this, ListActivity::class.java)
        intent.addFlags(
            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                    Intent.FLAG_ACTIVITY_NEW_TASK
        )
        startActivity(intent)
    }

    override fun onLoginFailure(message: String) {
        alertDialog!!.hide()
        Toast.makeText(this@LoginActivity, " No user found ", LENGTH_SHORT).show()
    }
}
