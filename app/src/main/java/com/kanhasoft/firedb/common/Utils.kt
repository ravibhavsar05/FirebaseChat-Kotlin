package com.kanhasoft.firedb.common

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import com.kanhasoft.firedb.model.city.City
import com.kanhasoft.firedb.model.state.State
import java.io.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

object Utils {

    var states = ArrayList<State>()
    var cities = ArrayList<City>()

    val fileList: Array<File>?
        get() {
            var files: Array<File>? = null
            val path = Environment.getExternalStorageDirectory().path + "/.Images/"
            Log.d("Files", "Path: $path")
            val directory = File(path)
            if (directory != null) {
                files = directory.listFiles()
            }
            return files
        }

    fun getBytes(file: File): ByteArray {
        var bytes: ByteArray? = null
        val size = file.length().toInt()
        bytes = ByteArray(size)
        try {
            val buf = BufferedInputStream(FileInputStream(file))
            buf.read(bytes, 0, bytes.size)
            buf.close()
        } catch (e: FileNotFoundException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }

        return bytes

    }

    fun getAge(year: Int, month: Int, day: Int): String {
        val dob = Calendar.getInstance()
        val today = Calendar.getInstance()

        dob.set(year, month, day)

        var age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--
        }

        val ageInt = age

        return ageInt.toString()
    }

    fun closeKeyboard(activity: Context) {
        try {
            val inputManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager?.hideSoftInputFromWindow(
                (activity as Activity).currentFocus!!.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun isKeyBoardShow(activity: Context) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow((activity as Activity).currentFocus!!.windowToken, 0)
        /* if (imm.isActive()) {
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0); // hide
        } else {
            imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY); // show
        }*/
    }

    fun showKeyboard(activity: Context) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow((activity as Activity).currentFocus!!.windowToken, InputMethodManager.SHOW_FORCED)
        /* if (imm.isActive()) {
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0); // hide
        } else {
            imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY); // show
        }*/
    }

    fun isKeyBoardVisible(activity: Context): Boolean {
        var visible = false
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        visible = imm.isActive

        return visible
    }

    //    public static ProgressDialog showProgressBar(Context mContext) {
    //        ProgressDialog of = ProgressDialog.show(mContext, "", "Vennligst vent...");
    //        of.setCanceledOnTouchOutside(true);
    //        return of;
    //    }


    fun getValue(text: String?): String {
        return if (text != null && !text.equals("-1", ignoreCase = true)) text else ""
    }

    fun getAmount(amount: String): String {
        if (amount.contains(".0")) {
            return amount.replace(".0", "")
        } else {
            val f = DecimalFormat("#0.00")
            return if (amount.equals("", ignoreCase = true)) {
                "0"
            } else if (amount.equals("0.0", ignoreCase = true)) {
                "0"
            } else if (!amount.contains(".")) {
                amount
            } else {
                f.format(java.lang.Double.parseDouble(amount)).toString()
            }
        }
    }

    fun expand(v: View) {
        val matchParentMeasureSpec =
            View.MeasureSpec.makeMeasureSpec((v.parent as View).width, View.MeasureSpec.EXACTLY)
        val wrapContentMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        v.measure(matchParentMeasureSpec, wrapContentMeasureSpec)
        val targetHeight = v.measuredHeight

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.layoutParams.height = 1
        v.visibility = View.VISIBLE
        val a = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                v.layoutParams.height = if (interpolatedTime == 1f)
                    LinearLayout.LayoutParams.WRAP_CONTENT
                else
                    (targetHeight * interpolatedTime).toInt()
                v.requestLayout()
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        // 1dp/ms
        a.duration = (targetHeight / v.context.resources.displayMetrics.density).toInt().toLong()
        v.startAnimation(a)
    }

    fun collapse(v: View) {
        val initialHeight = v.measuredHeight

        val a = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                if (interpolatedTime == 1f) {
                    v.visibility = View.GONE
                } else {
                    v.layoutParams.height = initialHeight - (initialHeight * interpolatedTime).toInt()
                    v.requestLayout()
                }
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        // 1dp/ms
        a.duration = (initialHeight / v.context.resources.displayMetrics.density).toInt().toLong()
        v.startAnimation(a)
    }

    fun isFileExists(fileName: String): Boolean {
        val flag = false
        val files = fileList
        if (files != null)
            if (files.size > 0) {
                for (i in files.indices) {
                    Log.d("Files", "FileName:" + files[i].name)
                    if (fileName.equals(files[i].name, ignoreCase = true)) {
                        return true
                    }
                }
            }
        return flag
    }


    fun getPathNameFile(fileName: String): String {
        val flag = false
        val files = fileList
        if (files != null)
            if (files.size > 0) {
                for (i in files.indices) {
                    Log.d("Files", "FileName:" + files[i].name)
                    if (fileName.equals(files[i].name, ignoreCase = true)) {
                        return files[i].absolutePath
                    }
                }
            }
        return ""
    }


    fun createLists() {

        val state0 = State(0, "Choose a State")
        val state1 = State(1, "Gujarat")
        val state2 = State(2, "Kerala")
        val state3 = State(3, "Madhya Pradesh")
        val state4 = State(4, "Punjab")

        states.add(state0)
        states.add(state1)
        states.add(state2)
        states.add(state3)
        states.add(state4)

        cities.add(City(0, state0, "Choose a City"))
        cities.add(City(1, state1, "Ahmedabad"))
        cities.add(City(2, state1, "Gandhinagar"))
        cities.add(City(3, state1, "Patan"))
        cities.add(City(4, state1, "Rajkot"))
        cities.add(City(5, state1, "Surat"))
        cities.add(City(6, state2, "Kannur"))
        cities.add(City(7, state2, "Wayanad"))
        cities.add(City(8, state3, "Bhopal"))
        cities.add(City(9, state3, "Dewas"))
        cities.add(City(10, state3, "Indore"))
        cities.add(City(11, state3, "Panna"))
        cities.add(City(12, state3, "Sidhi"))
        cities.add(City(13, state3, "Ujjain"))
        cities.add(City(14, state4, "Amritsar"))
        cities.add(City(15, state4, "Firozpur"))
        cities.add(City(16, state4, "Rupnagar"))
        cities.add(City(17, state4, "Ludhiana"))
        cities.add(City(18, state4, "Barnala"))
    }

    fun saveFile(imageView: ImageView, fileName: String) {
        val drawable = imageView.drawable as BitmapDrawable
        val bitmap = drawable.bitmap
        val sdCardDirectory = File(Environment.getExternalStorageDirectory().path + "/.Images")
        if (!sdCardDirectory.exists())
            sdCardDirectory.mkdir()
        val image = File(sdCardDirectory, fileName)
        val outStream: FileOutputStream
        try {

            outStream = FileOutputStream(image)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream)
            /* 100 keep full quality image */

            outStream.flush()
            outStream.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun dateFormat(calendar: Calendar): String {
        val myFormat = "dd/MM/yyyy" //In which you need put here
        val sdf = SimpleDateFormat(myFormat, Locale.US)

        return sdf.format(calendar.time)
    }

    fun hideAllChildFromLayout(layout: LinearLayout, boolean: Boolean) {
        for (i in 0 until layout.childCount) {
            val child = layout.getChildAt(i)
            child.isEnabled = boolean
        }
    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    fun appInForeground(context: Context): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningAppProcesses = activityManager.runningAppProcesses ?: return false
        return runningAppProcesses.any { it.processName == context.packageName && it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND }
    }
}
