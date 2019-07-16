package com.kanhasoft.firedb.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.kanhasoft.firedb.R
import com.kanhasoft.firedb.adapter.FormDataAdapter
import com.kanhasoft.firedb.adapterListener.FormDataClickListener
import com.kanhasoft.firedb.common.Constant
import com.kanhasoft.firedb.common.SharedPrefUtil
import com.kanhasoft.firedb.core.removeUser.RemoveUserContract
import com.kanhasoft.firedb.core.removeUser.RemoveUserPresenter
import com.kanhasoft.firedb.core.users.devicetoken.UpdateDeviceTokenContract
import com.kanhasoft.firedb.core.users.devicetoken.UpdateDeviceTokenPresenter
import com.kanhasoft.firedb.core.users.get.all.GetUsersContract
import com.kanhasoft.firedb.core.users.get.all.GetUsersPresenter
import com.kanhasoft.firedb.model.UserData
import com.labters.lottiealertdialoglibrary.ClickListener
import com.labters.lottiealertdialoglibrary.DialogTypes
import com.labters.lottiealertdialoglibrary.LottieAlertDialog
import kotlinx.android.synthetic.main.activity_list.*

class ListActivity : AppCompatActivity(), FormDataClickListener, GetUsersContract.View,
    RemoveUserContract.View, UpdateDeviceTokenContract.View {

    internal var data: ArrayList<UserData> = ArrayList<UserData>()
    internal var originalList: ArrayList<UserData> = ArrayList<UserData>()
    internal lateinit var formDataAdapter: FormDataAdapter
    private lateinit var getUsersPresenter: GetUsersPresenter
    private lateinit var removeUserPresenter: RemoveUserPresenter
    private lateinit var updateDeviceToken: UpdateDeviceTokenPresenter
    var sharedPreferencesUtils: SharedPrefUtil? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        sharedPreferencesUtils = SharedPrefUtil(this@ListActivity)
        getUsersPresenter = GetUsersPresenter(this)
        removeUserPresenter = RemoveUserPresenter(this)
        updateDeviceToken = UpdateDeviceTokenPresenter(this)

        formDataAdapter = FormDataAdapter(this, data, this)
        recyclerData.setHasFixedSize(true)
        val mLayoutManager = LinearLayoutManager(applicationContext)
        recyclerData.layoutManager = mLayoutManager
        recyclerData.itemAnimator = DefaultItemAnimator()
        recyclerData.adapter = formDataAdapter

        getUsersPresenter.getAllUsers()

        ivAdd.setOnClickListener(clickListener)
        ivBack.setOnClickListener(clickListener)
        ivLogout.setOnClickListener(clickListener)

        searchData()
    }

    private fun searchData() {
        etSearch.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                hideSoftKeyBoard(this@ListActivity, etSearch)
                val text = etSearch.text.toString().trim { it <= ' ' }
                if (text.trim().isNotEmpty()) {
                    filter(text)
                } else {
                    data.clear()
                    formDataAdapter.notifyDataSetChanged()
                    data.addAll(originalList)
                    formDataAdapter.notifyDataSetChanged()

                }

                return@OnEditorActionListener true
            }
            false
        })
    }

    private fun filter(text: String) {
        //new array list that will hold the filtered data
        var filterdNames: List<UserData> = ArrayList<UserData>()

        filterdNames = originalList.filter {
            it.display_name.toString().toLowerCase().contains(text.toLowerCase()) ||
                    it.email.toString().toLowerCase().contains(text.toLowerCase())
        }
        //calling a method of the adapter class and passing the filtered list
        formDataAdapter.filterList(filterdNames as MutableList<UserData>)
    }

    fun hideSoftKeyBoard(context: Context, view: View) {
        try {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        } catch (e: Exception) {
            // TODO: handle exception
            e.printStackTrace()
        }

    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }

    private val clickListener: View.OnClickListener = View.OnClickListener { view ->
        if (view.id == ivBack.id) {
            onBackPressed()
        }
        if (view.id == ivAdd.id)
            redirectCreateDisplayScreen(Constant.ADD, "")
        if (view.id == ivLogout.id) {
            showLogoutDialog()
        }
    }

    private fun redirectCreateDisplayScreen(action: String, position: String) {
        val intent = Intent(this, CreateDisplayActivity::class.java)
        intent.putExtra(Constant.ACTION, action)
        intent.putExtra(Constant.DATA, position)
        startActivityForResult(intent, 101)
    }

    private fun showDeleteDialog(position: Int) {

        var alertDialog: LottieAlertDialog? = null
        alertDialog = LottieAlertDialog.Builder(this@ListActivity, DialogTypes.TYPE_DELETE)
            .setTitle(getString(R.string.text_delete))
            .setDescription(getString(R.string.msg_delete))
            .setPositiveText(getString(R.string.text_yes))
            .setNegativeText(getString(R.string.text_no))
            .setPositiveListener(object : ClickListener {
                override fun onClick(dialog: LottieAlertDialog) {
                    alertDialog!!.hide()
                    removeUserPresenter.remove(this@ListActivity, data.get(position))
                }
            })
            .setNegativeListener(object : ClickListener {
                override fun onClick(dialog: LottieAlertDialog) {
                    alertDialog!!.hide()
                }
            })
            .build()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun showLogoutDialog() {

        var alertDialog: LottieAlertDialog? = null
        alertDialog = LottieAlertDialog.Builder(this@ListActivity, DialogTypes.TYPE_LOGOUT)
            .setTitle(getString(R.string.text_logout))
            .setDescription(getString(R.string.msg_logout))
            .setPositiveText(getString(R.string.text_yes))
            .setNegativeText(getString(R.string.text_no))
            .setPositiveListener(object : ClickListener {
                override fun onClick(dialog: LottieAlertDialog) {
                    alertDialog!!.hide()
                    updateDeviceToken.updateDeviceToken(
                        sharedPreferencesUtils!!.getString(Constant.ARG_UID).toString(),
                        ""
                    )
                }
            })
            .setNegativeListener(object : ClickListener {
                override fun onClick(dialog: LottieAlertDialog) {
                    alertDialog!!.hide()
                }

            })
            .build()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    override
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 101) {
                getUsersPresenter.getAllUsers()
            }
        }
    }

    override
    fun onEditFormClickListener(position: Int) {
        redirectCreateDisplayScreen(Constant.EDIT, data[position].user_id!!)
    }

    override
    fun onDeleteFormClickListener(position: Int) {
        showDeleteDialog(position)
    }

    override
    fun onDetailFormClickListener(position: Int) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra(Constant.DATA, data[position].user_id!!)
        startActivity(intent)
    }

    override fun onGetAllUsersSuccess(users: List<UserData>) {
        data.addAll(users)
        originalList.addAll(users)

        if (data!!.size > 0) {
            recyclerData.visibility = VISIBLE
            relativeNoData.visibility = GONE
            etSearch.visibility = VISIBLE
        } else {
            recyclerData.visibility = GONE
            relativeNoData.visibility = VISIBLE
            etSearch.visibility = GONE
        }
        formDataAdapter.notifyDataSetChanged()
    }

    override fun onGetAllUsersFailure(message: String) {

    }

    override fun onGetRemoveUsersSuccess(users: UserData) {
        deleteUser(users)
    }

    override fun onDeleteUserSuccess(users: UserData) {
        deleteUser(users)
    }

    private fun deleteUser(users: UserData) {
        val dumy = data

        for (i in dumy.iterator()) {

            if (i.uid.toString().equals(users.uid.toString())) {
                data.remove(i)
            }
        }

        originalList.clear()
        originalList.addAll(data)
        formDataAdapter.notifyDataSetChanged()

        if (data!!.size > 0) {
            recyclerData.visibility = VISIBLE
            relativeNoData.visibility = GONE
            etSearch.visibility = VISIBLE
        } else {
            recyclerData.visibility = GONE
            relativeNoData.visibility = VISIBLE
            etSearch.visibility = GONE
        }
    }

    override fun onDeleteUserFailure(message: String) {

    }

    override fun updateDeviceTokenSuccess() {
        sharedPreferencesUtils!!.clear()
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(
            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                    Intent.FLAG_ACTIVITY_NEW_TASK
        )
        startActivity(intent)
    }

    override fun updateDeviceTokenFailure(message: String) {

    }
}
