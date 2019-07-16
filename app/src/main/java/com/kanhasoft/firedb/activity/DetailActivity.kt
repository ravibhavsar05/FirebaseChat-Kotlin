package com.kanhasoft.firedb.activity

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.storage.FirebaseStorage
import com.kanhasoft.firedb.R
import com.kanhasoft.firedb.common.Constant
import com.kanhasoft.firedb.common.Utils
import com.kanhasoft.firedb.core.users.get.all.GetUsersContract
import com.kanhasoft.firedb.core.users.get.all.GetUsersPresenter
import com.kanhasoft.firedb.model.UserData
import com.labters.lottiealertdialoglibrary.DialogTypes
import com.labters.lottiealertdialoglibrary.LottieAlertDialog
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity(), GetUsersContract.View {

    override fun onGetRemoveUsersSuccess(users: UserData) {

    }

    private lateinit var getAllPresenter: GetUsersPresenter
    var data: UserData? = null
    var alertDialog: LottieAlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val intent: Intent = this.getIntent()
        getAllPresenter = GetUsersPresenter(this)
        if (intent.hasExtra(Constant.DATA)) {
            getAllPresenter.getAllUsers()
        }
        ivBack.setOnClickListener(clickListener)
        ivChat.setOnClickListener(clickListener)

        alertDialog = LottieAlertDialog.Builder(this@DetailActivity, DialogTypes.TYPE_LOADING)
            .setTitle(getString(R.string.loading))
            .setDescription(getString(R.string.msg_please_wait))
            .build()

        alertDialog!!.setCancelable(false)
        alertDialog!!.show()
        ivChat.visibility = GONE
        linearDetail.visibility = GONE
    }

    private val clickListener: View.OnClickListener = View.OnClickListener { view ->
        if (view.id == ivBack.id) {
            onBackPressed()
        }
        if (view.id == ivChat.id) {
            if (data != null) {
                val intent = Intent(this@DetailActivity, ChatActivity::class.java)
                intent.putExtra(Constant.ARG_RECEIVER, data!!.display_name)
                intent.putExtra(Constant.ARG_RECEIVER_UID, data!!.uid)
                intent.putExtra(Constant.ARG_FIREBASE_TOKEN, data!!.token)
                startActivity(intent)
            }
        }
    }

    override fun onGetAllUsersSuccess(users: List<UserData>) {

        if (users.isNotEmpty()) {
            for (user in users)
                if (user.user_id.equals(intent.getStringExtra(Constant.DATA))) {
                    data = user
                    break
                }
        }

        if (data != null) {
            ivChat.visibility = VISIBLE

            tvUserName.text = data!!.display_name
            tvEmail.text = data!!.email
            tvPhone.text = data!!.phone
            tvGender.text = data!!.sex
            tvAddress.text = data!!.address
            tvAboutme.text = data!!.about
            tvState.text = Utils.states.get(data!!.state!!).stateName
            tvCity.text = Utils.cities.get(data!!.city!!).cityName
            tvZip.text = data!!.zip
            tvBirthdate.text = data!!.birthdate

            val bdate = data!!.birthdate.toString().split("/")
            tvAge.text =
                Utils.getAge(
                    Integer.parseInt(bdate[2]),
                    Integer.parseInt(bdate[1]),
                    Integer.parseInt(bdate[0])
                ) + " years"

            val referenseLcl = FirebaseStorage.getInstance().reference
            val islandRefLcl = referenseLcl.child(Constant.DATABASE_PATH_UPLOADS).child(data!!.uid.toString() + ".jpg")
            val ONE_MEGABYTE = (512 * 512).toLong()
            islandRefLcl.getBytes(ONE_MEGABYTE).addOnSuccessListener { bytesPrm ->
                val bmp = BitmapFactory.decodeByteArray(bytesPrm, 0, bytesPrm.size)
                ivProfileImage.setImageBitmap(bmp)
            }.addOnFailureListener { ivProfileImage.setImageResource(R.drawable.ic_placeholder) }

            linearDetail.visibility = VISIBLE
        }
        alertDialog!!.hide()
    }

    override fun onGetAllUsersFailure(message: String) {
        Toast.makeText(this@DetailActivity, message, LENGTH_SHORT).show()
        alertDialog!!.hide()
    }
}
