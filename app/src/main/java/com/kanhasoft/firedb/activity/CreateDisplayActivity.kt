package com.kanhasoft.firedb.activity

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AppCompatActivity
import com.baoyz.actionsheet.ActionSheet
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.storage.FirebaseStorage
import com.kanhasoft.firedb.R
import com.kanhasoft.firedb.common.Constant
import com.kanhasoft.firedb.common.MessageBar
import com.kanhasoft.firedb.common.SharedPrefUtil
import com.kanhasoft.firedb.common.Utils
import com.kanhasoft.firedb.core.registration.RegisterContract
import com.kanhasoft.firedb.core.registration.RegistrationPresenter
import com.kanhasoft.firedb.core.users.get.all.GetUsersContract
import com.kanhasoft.firedb.core.users.get.all.GetUsersPresenter
import com.kanhasoft.firedb.model.UserData
import com.kanhasoft.firedb.model.city.City
import com.kanhasoft.firedb.model.state.State
import com.labters.lottiealertdialoglibrary.DialogTypes
import com.labters.lottiealertdialoglibrary.LottieAlertDialog
import com.myhexaville.smartimagepicker.ImagePicker
import kotlinx.android.synthetic.main.activity_create_display.*
import java.util.*

class CreateDisplayActivity : AppCompatActivity(), RegisterContract.View,
    GetUsersContract.View {

    private var mMassagebar: MessageBar? = null
    internal lateinit var myCalendar: Calendar
    private var stateArrayAdapter: ArrayAdapter<State>? = null
    private var cityArrayAdapter: ArrayAdapter<City>? = null

    internal var action = Constant.ADD
    internal var dataEdit: UserData? = null
    private var imagePicker: ImagePicker? = null
    val database = FirebaseDatabase.getInstance().reference
    private lateinit var registerPresenter: RegistrationPresenter
    private lateinit var getAllPresenter: GetUsersPresenter
    private var filePath: Uri? = null
    var sharedPreferencesUtils: SharedPrefUtil? = null
    var alertDialog: LottieAlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_display)

        alertDialog = LottieAlertDialog.Builder(this@CreateDisplayActivity, DialogTypes.TYPE_LOADING)
            .setTitle("Loading")
            .setDescription("Please Wait")
            .build()
        alertDialog!!.setCancelable(true)


        sharedPreferencesUtils = SharedPrefUtil(this@CreateDisplayActivity)
        val intent: Intent = this.getIntent()

        initialData()

        registerPresenter = RegistrationPresenter(this)
        getAllPresenter = GetUsersPresenter(this)
        mMassagebar = MessageBar(this@CreateDisplayActivity)
        myCalendar = Calendar.getInstance()
        if (intent.hasExtra(Constant.ACTION)) {
            action = intent.getStringExtra(Constant.ACTION)
            if (action == Constant.EDIT) {
                if (intent.hasExtra(Constant.DATA)) {
                    if (alertDialog != null)
                        alertDialog!!.show()

                    getAllPresenter.getAllUsers()
                }
            }
        }

        ivBack.setOnClickListener(clickListener)
        btnSubmit.setOnClickListener(clickListener)
        ivProfileImage.setOnClickListener(clickListener)
        tvBirthdate.setOnClickListener(clickListener)
    }

    private val clickListener: View.OnClickListener = View.OnClickListener { view ->
        if (view.id == ivBack.id) {
            onBackPressed()
        }
        if (view.id == btnSubmit.id) {
            if (validate()) {
                alertDialog!!.show()
                submitData()
            }
        }
        if (view.id == ivProfileImage.id) {
            openImageChooserActionSheet()
        }
        if (view.id == tvBirthdate.id)
            chooseBirthDate()
    }

    private fun initialData() {
        stateArrayAdapter =
            object : ArrayAdapter<State>(applicationContext, R.layout.simple_spinner_item, Utils.states) {
                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val v = super.getView(position, convertView, parent)
                    (v.findViewById<View>(android.R.id.text1) as TextView).setTextColor(resources.getColor(R.color.colorPrimary))
                    return v
                }
            }

        spinnerState.setAdapter(stateArrayAdapter)

        cityArrayAdapter = object : ArrayAdapter<City>(applicationContext, R.layout.simple_spinner_item, Utils.cities) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getView(position, convertView, parent)
                (v.findViewById<View>(android.R.id.text1) as TextView).setTextColor(resources.getColor(R.color.colorPrimary))

                return v
            }
        }

        spinnerCity.setAdapter(cityArrayAdapter)

        spinnerState.setOnItemSelectedListener(state_listener)
        spinnerState.setSelection(0)
        spinnerCity.setSelection(0)
    }

    private val state_listener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
            if (position > 0) {
                val state = spinnerState.getItemAtPosition(position) as State
                val tempCities = ArrayList<City>()

                val firstState = State(0, resources.getString(R.string.choose_state))
                tempCities.add(City(0, firstState, resources.getString(R.string.choose_city)))

                for (singleCity in Utils.cities) {
                    if (singleCity.state.stateID === state.stateID) {
                        tempCities.add(singleCity)
                    }
                }

                cityArrayAdapter =
                    object : ArrayAdapter<City>(applicationContext, R.layout.simple_spinner_item, tempCities) {
                        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                            val v = super.getView(position, convertView, parent)
                            (v.findViewById<View>(android.R.id.text1) as TextView).setTextColor(resources.getColor(R.color.colorPrimary))

                            return v
                        }
                    }

                spinnerCity.adapter = cityArrayAdapter
                if (action.equals(Constant.EDIT, ignoreCase = true)) {
                    if (state.stateID === dataEdit!!.state)
                        spinnerCity.setSelection(Utils.cities[dataEdit!!.city!!].cityID)
                }
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>) {
        }
    }

    private fun displayMessage(msg: String) {
        mMassagebar!!.showWithColor(msg, resources.getString(R.string.msg_color_warning_color))
    }

    override
    fun onResume() {
        super.onResume()

    }

    override
    fun onBackPressed() {
        dataEdit = null
        val intent = Intent()
        setResult(Activity.RESULT_CANCELED, intent)
        finish()
    }

    private fun submitData() {
        val selectedId = rbGender.checkedRadioButtonId
        val selectedRadioButton = findViewById<View>(selectedId) as RadioButton
        val radioButtonText = selectedRadioButton.text.toString()

        val firstName = etFirstname.text.toString().trim { it <= ' ' }
        val email = etEmail.text.toString().trim { it <= ' ' }
        val phone = etPhone.text.toString().trim { it <= ' ' }
        val birthDate = tvBirthdate.text.toString().trim { it <= ' ' }
        val aboutMe = etAboutMe.text.toString().trim { it <= ' ' }
        val address = etAddress.text.toString().trim { it <= ' ' }
        val zip = etZip.text.toString().trim { it <= ' ' }

        val data = UserData()
        data.display_name = firstName
        data.email = email
        data.sex = radioButtonText
        data.phone = phone
        data.birthdate = birthDate
        data.about = aboutMe
        data.address = address
        data.state = Utils.states[spinnerState.selectedItemPosition].stateID
        data.city = Utils.cities[spinnerCity.selectedItemPosition].cityID
        data.zip = zip
        data.profile_pic = filePath.toString()
        data.token = FirebaseInstanceId.getInstance().getToken().toString()

        if (action.equals(Constant.ADD, ignoreCase = true))
            data.user_id = database.child(Constant.FIREBASE_DATABASE).push().key
        if (action.equals(Constant.EDIT, ignoreCase = true)) {
            data.user_id = dataEdit!!.user_id
            data.uid = dataEdit!!.uid
        }

        registerPresenter.register(this@CreateDisplayActivity, data, "Admin@123", this!!.filePath!!)
    }

    private fun chooseBirthDate() {
        val datePickerDialog = DatePickerDialog(
            this@CreateDisplayActivity, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)
        )
        val calendar = Calendar.getInstance()//get the current day
        datePickerDialog.datePicker.maxDate = calendar.timeInMillis
        datePickerDialog.show()
    }

    internal var date: DatePickerDialog.OnDateSetListener =
        DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            tvBirthdate.text = Utils.dateFormat(myCalendar)
            tvAge.text = Utils.getAge(year, monthOfYear, dayOfMonth) + " years"
        }

    private fun validate(): Boolean {
        val firstName = etFirstname.text.toString().trim { it <= ' ' }
        val email = etEmail.text.toString().trim { it <= ' ' }
        val phone = etPhone.text.toString().trim { it <= ' ' }
        val birthDate = tvBirthdate.text.toString().trim { it <= ' ' }
        val aboutMe = etAboutMe.text.toString().trim { it <= ' ' }
        val address = etAddress.text.toString().trim { it <= ' ' }
        val zip = etZip.text.toString().trim { it <= ' ' }

        if (firstName.length == 0) {
            displayMessage(resources.getString(R.string.err_msg_firstname))
            return false
        } else if (email.length == 0) {
            displayMessage(resources.getString(R.string.err_msg_email))
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            displayMessage(resources.getString(R.string.err_msg_valid_email))
            return false
        } else if (phone.length == 0) {
            displayMessage(resources.getString(R.string.err_msg_email))
            return false
        } else if (phone.length < 10) {
            displayMessage(resources.getString(R.string.err_msg_phone_digit))
            return false
        } else if (rbGender.checkedRadioButtonId == -1) {
            displayMessage(resources.getString(R.string.err_msg_gender))
            return false
        } else if (birthDate.length == 0) {
            displayMessage(resources.getString(R.string.err_msg_birthdate))
            return false
        } else if (aboutMe.length == 0) {
            displayMessage(resources.getString(R.string.err_msg_aboutme))
            return false
        } else if (address.length == 0) {
            displayMessage(resources.getString(R.string.err_msg_address))
            return false
        } else if (spinnerState.selectedItemPosition == 0) {
            displayMessage(resources.getString(R.string.err_msg_state))
            return false
        } else if (spinnerCity.selectedItemPosition == 0) {
            displayMessage(resources.getString(R.string.err_msg_city))
            return false
        } else if (zip.length == 0) {
            displayMessage(resources.getString(R.string.err_msg_zip))
            return false
        } else if (filePath == null) {
            displayMessage(getString(R.string.err_msg_profile_image))
            return false
        }
        return true
    }

    private fun openImageChooserActionSheet() {
        setTheme(R.style.ActionSheetStyleiOS7)
        ActionSheet.createBuilder(this@CreateDisplayActivity, supportFragmentManager).setCancelButtonTitle("Cancel")
            .setOtherButtonTitles("Camera", "Gallery").setCancelableOnTouchOutside(true).setListener(object :
                ActionSheet.ActionSheetListener {
                override fun onDismiss(actionSheet: ActionSheet, isCancel: Boolean) {}

                override fun onOtherButtonClick(actionSheet: ActionSheet, index: Int) {
                    if (index == 0) {
                        openCamera()
                    } else {
                        chooseFromGallery()
                    }
                }
            }).show()
    }

    fun chooseFromGallery() {
        refreshImagePicker()
        imagePicker!!.choosePicture(false)
    }

    fun openCamera() {
        refreshImagePicker()
        imagePicker!!.openCamera()
    }

    private fun refreshImagePicker() {

        imagePicker = ImagePicker(this@CreateDisplayActivity, null) { imageUri ->
            filePath = imageUri;
            ivProfileImage.setImageURI(imageUri)
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        imagePicker!!.handleActivityResult(resultCode, requestCode, data)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        imagePicker!!.handlePermission(requestCode, grantResults)
    }

    override fun onGetAllUsersSuccess(users: List<UserData>) {
        alertDialog!!.hide()
        if (users.size > 0) {

            for (user in users)
                if (user.user_id.equals(intent.getStringExtra(Constant.DATA))) {
                    dataEdit = user
                    break
                }
        }

        if (dataEdit != null) {
            etFirstname.setText(dataEdit!!.display_name)
            etEmail.setText(dataEdit!!.email)
            etPhone.setText(dataEdit!!.phone)
            if (dataEdit!!.sex.toString().toLowerCase().equals(Constant.MALE.toString().toLowerCase()))
                rbGender.check(rbMale!!.getId())
            else if (dataEdit!!.sex.toString().toLowerCase().equals(Constant.FEMALE.toString().toLowerCase()))
                rbGender.check(rbFemale!!.getId())
            tvBirthdate.setText(dataEdit!!.birthdate.toString())
            try {
                val bdate = dataEdit!!.birthdate.toString().split("/")
                tvAge.setText(
                    Utils.getAge(
                        Integer.parseInt(bdate[2]),
                        Integer.parseInt(bdate[1]),
                        Integer.parseInt(bdate[0])
                    ) + " years"
                )
            } catch (e: Exception) {

            }

            etAboutMe!!.setText(dataEdit!!.about)
            etAddress!!.setText(dataEdit!!.address)

            etZip.setText(dataEdit!!.zip)
            val referenseLcl = FirebaseStorage.getInstance().reference
            val islandRefLcl =
                referenseLcl.child(Constant.DATABASE_PATH_UPLOADS).child(dataEdit!!.uid.toString() + ".jpg")
            val ONE_MEGABYTE = (512 * 512).toLong()
            islandRefLcl.getBytes(ONE_MEGABYTE).addOnSuccessListener { bytesPrm ->

                val bmp = BitmapFactory.decodeByteArray(bytesPrm, 0, bytesPrm.size)
                ivProfileImage.setImageBitmap(bmp)
                try {
                    filePath = Utils.getImageUri(this@CreateDisplayActivity, bmp)
                } catch (e: Exception) {
                }

            }.addOnFailureListener { ivProfileImage.setImageResource(R.drawable.ic_placeholder) }

            if (action.equals(Constant.EDIT, ignoreCase = true))
                spinnerState.setSelection(Utils.states.get(dataEdit!!.state!!).stateID)
        }
    }

    override fun onGetAllUsersFailure(message: String) {
        alertDialog!!.hide()
        Log.e("TAG", "fail")

    }

    override fun onRegistrationSuccess() {
        alertDialog!!.hide()
        Toast.makeText(applicationContext, getString(R.string.msg_register_successful), LENGTH_SHORT).show()
        val intent = Intent()
        intent.putExtra(Constant.DATA, getIntent().getIntExtra(Constant.DATA, 0))
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onRegistrationFailure(message: String) {
        alertDialog!!.hide()
        Toast.makeText(applicationContext, getString(R.string.err_register_user), LENGTH_SHORT).show()
        val intent = Intent()
        intent.putExtra(Constant.DATA, getIntent().getIntExtra(Constant.DATA, 0))
        setResult(Activity.RESULT_CANCELED, intent)
        finish()
    }

    override fun onGetRemoveUsersSuccess(users: UserData) {

    }
}
