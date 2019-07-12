@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package com.talktiva.pilot.helper

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Environment
import android.os.Parcelable
import android.provider.Settings
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.Constraints
import com.airbnb.lottie.LottieAnimationView
import com.talktiva.pilot.R
import com.talktiva.pilot.Talktiva
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


object Utility {

    var imageFilePath: String? = null

    private var dialogInternet: Dialog? = null
    private var preferences: SharedPreferences? = null

//    private const val STRING_LENGTH = 10
//    private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

    //region Get Fonts
    val fontRegular: Typeface
        get() = Typeface.createFromAsset(Talktiva.instance!!.assets, "merriweather_regular.ttf")

    val fontBold: Typeface
        get() = Typeface.createFromAsset(Talktiva.instance!!.assets, "merriweather_bold.ttf")
    //endregion

    //region Device Id and Device Name
    @SuppressLint("HardwareIds")
    fun getDeviceId(): String {
        return Settings.Secure.getString(Talktiva.instance?.contentResolver, Settings.Secure.ANDROID_ID)
    }

//    fun getDeviceName(): String {
//        val fields = Build.VERSION_CODES::class.java.fields
//        val versionName = fields[Build.VERSION.SDK_INT].name
//        return Build.MANUFACTURER.plus(" ").plus(Build.MODEL).plus(" ").plus(Build.VERSION.RELEASE).plus(" ").plus(versionName)
//    }
    //endregion

    //region Connection
    private val isNetworkAvailable: Boolean
        get() = (Talktiva.instance!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo != null

    val isConnectingToInternet: Boolean
        get() {
            if (isNetworkAvailable) {
                val info = (Talktiva.instance!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo
                @Suppress("DEPRECATION")
                if (info != null)
                    return info.state == NetworkInfo.State.CONNECTED
            }
            return false
        }

    fun setTitleText(toolbar: Toolbar, id: Int?, title: Int?) {
        val textView = toolbar.findViewById<TextView>(id!!)
        textView.typeface = fontRegular
        textView.setText(title!!)
    }

    @SuppressLint("InlinedApi")
    internal fun requestInternet(context: Context) {
        dialogInternet = showAlert(context, R.string.internet_msg, false, VISIBLE, R.string.dd_setting, View.OnClickListener {
            dialogInternet!!.dismiss()
            Talktiva.instance!!.startActivity(Intent(Settings.ACTION_DATA_USAGE_SETTINGS))
        }, GONE, null, null)
        dialogInternet!!.show()
    }

    internal fun dismissRequestInternet() {
        if (dialogInternet != null) {
            if (dialogInternet!!.isShowing) {
                dialogInternet!!.dismiss()
            }
        }
    }
    //endregion

    //region AlertDialog
    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    fun showAlert(context: Context, msg: Int?, bool: Boolean?, positiveVisibility: Int?, positiveTitle: Int?, positiveClickListener: View.OnClickListener, negativeVisibility: Int?, negativeTitle: Int?, negativeClickListener: View.OnClickListener?): Dialog {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dd_layout)
        dialog.setCancelable(bool!!)

        (dialog.findViewById<View>(R.id.dialog_msg) as TextView).typeface = fontRegular
        (dialog.findViewById<View>(R.id.dialog_msg) as TextView).setText(msg!!)

        (dialog.findViewById<View>(R.id.dialog_positive) as Button).typeface = fontRegular
        (dialog.findViewById<View>(R.id.dialog_positive) as Button).setText(positiveTitle!!)
        dialog.findViewById<View>(R.id.dialog_positive).visibility = positiveVisibility!!
        dialog.findViewById<View>(R.id.dialog_positive).setOnClickListener(positiveClickListener)

        dialog.findViewById<View>(R.id.dialog_negative).visibility = negativeVisibility!!

        if (negativeVisibility != GONE) {
            (dialog.findViewById<View>(R.id.dialog_negative) as Button).typeface = fontRegular
            (dialog.findViewById<View>(R.id.dialog_negative) as Button).setText(negativeTitle!!)
            dialog.findViewById<View>(R.id.dialog_negative).setOnClickListener(negativeClickListener)
        }

        dialog.window.setLayout(Constraints.LayoutParams.MATCH_PARENT, Constraints.LayoutParams.WRAP_CONTENT)
        dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
        return dialog
    }

    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    fun showAlert(context: Context, msg: String?, bool: Boolean?, positiveVisibility: Int?, positiveTitle: Int?, positiveClickListener: View.OnClickListener, negativeVisibility: Int?, negativeTitle: Int?, negativeClickListener: View.OnClickListener?): Dialog {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dd_layout)
        dialog.setCancelable(bool!!)

        (dialog.findViewById<View>(R.id.dialog_msg) as TextView).typeface = fontRegular
        (dialog.findViewById<View>(R.id.dialog_msg) as TextView).text = msg!!

        (dialog.findViewById<View>(R.id.dialog_positive) as Button).typeface = fontRegular
        (dialog.findViewById<View>(R.id.dialog_positive) as Button).setText(positiveTitle!!)
        dialog.findViewById<View>(R.id.dialog_positive).visibility = positiveVisibility!!
        dialog.findViewById<View>(R.id.dialog_positive).setOnClickListener(positiveClickListener)

        dialog.findViewById<View>(R.id.dialog_negative).visibility = negativeVisibility!!

        if (negativeVisibility != GONE) {
            (dialog.findViewById<View>(R.id.dialog_negative) as Button).typeface = fontRegular
            (dialog.findViewById<View>(R.id.dialog_negative) as Button).setText(negativeTitle!!)
            dialog.findViewById<View>(R.id.dialog_negative).setOnClickListener(negativeClickListener)
        }

        dialog.window.setLayout(Constraints.LayoutParams.MATCH_PARENT, Constraints.LayoutParams.WRAP_CONTENT)
        dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
        return dialog
    }

    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    fun showAlert(context: Context, color: Int?, msg: Int?, bool: Boolean?, positiveVisibility: Int?, positiveTitle: Int?, positiveClickListener: View.OnClickListener?, negativeVisibility: Int?, negativeTitle: Int?, negativeClickListener: View.OnClickListener?): Dialog {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dd_layout)
        dialog.setCancelable(bool!!)

        (dialog.findViewById<TextView>(R.id.dialog_msg)).typeface = fontRegular
        (dialog.findViewById<TextView>(R.id.dialog_msg)).setText(msg!!)
        (dialog.findViewById<TextView>(R.id.dialog_msg)).setTextColor(context.resources.getColor(color!!))
        (dialog.findViewById<TextView>(R.id.dialog_msg)).gravity = Gravity.START
        (dialog.findViewById<TextView>(R.id.dialog_msg)).setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)

        dialog.findViewById<View>(R.id.dialog_positive).visibility = positiveVisibility!!

        if (positiveVisibility != GONE) {
            (dialog.findViewById<View>(R.id.dialog_positive) as Button).typeface = fontRegular
            (dialog.findViewById<View>(R.id.dialog_positive) as Button).setText(positiveTitle!!)
            dialog.findViewById<View>(R.id.dialog_positive).setOnClickListener(positiveClickListener)
        }

        dialog.findViewById<View>(R.id.dialog_negative).visibility = negativeVisibility!!

        if (negativeVisibility != GONE) {
            (dialog.findViewById<View>(R.id.dialog_negative) as Button).typeface = fontRegular
            (dialog.findViewById<View>(R.id.dialog_negative) as Button).setText(negativeTitle!!)
            dialog.findViewById<View>(R.id.dialog_negative).setOnClickListener(negativeClickListener)
        }

        dialog.window.setLayout(Constraints.LayoutParams.MATCH_PARENT, Constraints.LayoutParams.WRAP_CONTENT)
        dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
        return dialog
    }

    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    fun showAlert(context: Context, color: Int?, msg: Int?, closeClickListener: View.OnClickListener): Dialog {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dd_dialog_dash)
        dialog.setCancelable(false)

        (dialog.findViewById<View>(R.id.dd_tv_msg) as TextView).typeface = fontRegular
        (dialog.findViewById<View>(R.id.dd_tv_msg) as TextView).setText(msg!!)
        (dialog.findViewById<View>(R.id.dd_tv_msg) as TextView).setTextColor(context.resources.getColor(color!!))
        (dialog.findViewById<View>(R.id.dd_tv_msg) as TextView).gravity = Gravity.START
        (dialog.findViewById<TextView>(R.id.dd_tv_msg)).setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)

        dialog.findViewById<View>(R.id.dd_btn_positive).visibility = GONE

        dialog.findViewById<View>(R.id.dd_btn_negative).visibility = GONE

        dialog.findViewById<ImageView>(R.id.dd_iv_close).setOnClickListener(closeClickListener)

        dialog.window.setLayout(Constraints.LayoutParams.MATCH_PARENT, Constraints.LayoutParams.WRAP_CONTENT)
        dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
        return dialog
    }

    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    fun showAlert(context: Context, msg: Int?, positiveVisibility: Int?, positiveTitle: Int?, positiveClickListener: View.OnClickListener?, closeClickListener: View.OnClickListener): Dialog {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dd_dialog_dash)
        dialog.setCancelable(false)

        (dialog.findViewById<TextView>(R.id.dd_tv_msg)).typeface = fontBold
        (dialog.findViewById<TextView>(R.id.dd_tv_msg)).setText(msg!!)

        dialog.findViewById<Button>(R.id.dd_btn_positive).visibility = positiveVisibility!!

        if (positiveVisibility != GONE) {
            (dialog.findViewById<Button>(R.id.dd_btn_positive)).typeface = fontRegular
            (dialog.findViewById<Button>(R.id.dd_btn_positive)).setText(positiveTitle!!)
            dialog.findViewById<Button>(R.id.dd_btn_positive).setOnClickListener(positiveClickListener)
        }

        dialog.findViewById<Button>(R.id.dd_btn_negative).visibility = GONE

        dialog.findViewById<ImageView>(R.id.dd_iv_close).setOnClickListener(closeClickListener)

        dialog.window.setLayout(Constraints.LayoutParams.MATCH_PARENT, Constraints.LayoutParams.WRAP_CONTENT)
        dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
        return dialog
    }


    //endregion

    //region Progress Dialog
    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    fun showProgress(context: Context): Dialog {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dd_loader)
        dialog.setCancelable(false)
        (dialog.findViewById<View>(R.id.progress) as LottieAnimationView).setAnimation("loader.json")
        dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
        return dialog
    }

    fun dismissDialog(dialog: Dialog?) {
        if (dialog != null) {
            if (dialog.isShowing) {
                dialog.dismiss()
            }
        }
    }
    //endregion

    //region Error Dialog
    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    fun showError(context: Context, msg: Int?, action: Int?, clickListener: View.OnClickListener): Dialog {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dd_error)
        dialog.setCancelable(false)

        (dialog.findViewById<View>(R.id.de_tv) as TextView).typeface = fontRegular
        (dialog.findViewById<View>(R.id.de_tv) as TextView).setText(msg!!)

        (dialog.findViewById<View>(R.id.de_btn) as Button).typeface = fontRegular
        (dialog.findViewById<View>(R.id.de_btn) as Button).setText(action!!)
        dialog.findViewById<View>(R.id.de_btn).setOnClickListener(clickListener)

        dialog.window.setLayout(Constraints.LayoutParams.MATCH_PARENT, Constraints.LayoutParams.MATCH_PARENT)
        dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
        return dialog
    }

    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    fun showError(context: Context, msg: String?, action: Int?, clickListener: View.OnClickListener): Dialog {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dd_error)
        dialog.setCancelable(false)

        (dialog.findViewById<View>(R.id.de_tv) as TextView).typeface = fontRegular
        (dialog.findViewById<View>(R.id.de_tv) as TextView).text = msg!!

        (dialog.findViewById<View>(R.id.de_btn) as Button).typeface = fontRegular
        (dialog.findViewById<View>(R.id.de_btn) as Button).setText(action!!)
        dialog.findViewById<View>(R.id.de_btn).setOnClickListener(clickListener)

        dialog.window.setLayout(Constraints.LayoutParams.MATCH_PARENT, Constraints.LayoutParams.MATCH_PARENT)
        dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
        return dialog
    }
    //endregion

    //region Toast Msg Show
    fun showMsg(id: Int?) {
        Toast.makeText(Talktiva.instance, Talktiva.instance!!.resources!!.getString(id!!), Toast.LENGTH_SHORT).show()
    }
    //endregion

    //region Shared Preference
    fun setPreference(key: String?, value: String?): Boolean? {
        preferences = Talktiva.instance!!.getSharedPreferences(Talktiva.TAG, Context.MODE_PRIVATE)
        val editor = preferences!!.edit()
        editor.putString(key!!, value!!)
        return editor.commit()
    }

    fun getPreference(key: String?): String? {
        preferences = Talktiva.instance!!.getSharedPreferences(Talktiva.TAG, Context.MODE_PRIVATE)
        return preferences?.getString(key!!, null)
    }

    fun blankPreference(key: String?): Boolean? {
        preferences = Talktiva.instance!!.getSharedPreferences(Talktiva.TAG, Context.MODE_PRIVATE)
        val editor = preferences!!.edit()
        editor.putString(key!!, null)
        return editor.commit()
    }
    //endregion

    //region Store data to system local storage privately
    fun storeData(fileName: String, data: String) {
        val file = File(Talktiva.instance?.filesDir?.absolutePath + "/" + fileName)
        file.createNewFile()
        file.setWritable(true)
        if (file.exists()) {
            if (file.delete()) {
                file.writeText(data, Charsets.UTF_8)
            }
        } else {
            file.writeText(data, Charsets.UTF_8)
        }
    }
    //endregion

    //region Get data from system local storage privately
    fun getData(fileName: String): String? {
        val file = File(Talktiva.instance?.filesDir?.absolutePath + "/" + fileName)
        file.setReadable(true)
        return file.readText(Charsets.UTF_8)
    }
    //endregion

    //region Create image file in pictures folder
    fun createImageFile(): File? {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss_", Locale.getDefault()).format(Date())
        val imageFileName = "IMG_".plus(timestamp)
        val storageFile = Talktiva.instance?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = createTempFile(imageFileName, ".jpeg", storageFile)
        imageFilePath = image.absolutePath
        return image
    }
    //endregion

    //region Image Popup
    fun getPickImageChooserForGallery(context: Context): Intent {
        val allIntents = ArrayList<Intent>()
        val packageManager = context.packageManager
        var intent1: Intent

        //region Add gallery apps from installed apps
        val galleryIntent = Intent()
        galleryIntent.action = Intent.ACTION_GET_CONTENT
        galleryIntent.type = "*/*"
        val info = packageManager.queryIntentActivities(galleryIntent, 0)
        for (resolveInfo in info) {
            intent1 = Intent(galleryIntent)
            intent1.component = ComponentName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name)
            intent1.setPackage(resolveInfo.activityInfo.packageName)
            allIntents.add(intent1)
        }
        //endregion

        //region Remove document app from intent list
        var mainIntent = allIntents[allIntents.size - 1]
        for (intent in allIntents) {
            if (intent.component!!.className == "com.android.documentsui.DocumentsActivity") {
                mainIntent = intent
                break
            }
        }
        allIntents.remove(mainIntent)
        //endregion

        //region Create chooser for photo
        val chooserIntent = Intent.createChooser(mainIntent, "Select source")
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toTypedArray<Parcelable>())
        //endregion

        return chooserIntent
    }
    //endregion

}
