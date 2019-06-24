package com.talktiva.pilot.helper

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.provider.Settings
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.Constraints
import com.airbnb.lottie.LottieAnimationView
import com.talktiva.pilot.R
import com.talktiva.pilot.Talktiva


object Utility {

    private var dialogInternet: Dialog? = null
    private var preferences: SharedPreferences? = null

    val fontRegular: Typeface
        get() = Typeface.createFromAsset(Talktiva.instance!!.assets, "Merriweather_regular.ttf")

    val fontBold: Typeface
        get() = Typeface.createFromAsset(Talktiva.instance!!.assets, "Merriweather_bold.ttf")

    //region Device Id and Device Name
    //    @SuppressLint("HardwareIds")
    //    public String getDeviceId() {
    //        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    //    }
    //
    //    public String getDeviceName() {
    //        return Build.MANUFACTURER
    //                + " " + Build.MODEL + " " + Build.VERSION.RELEASE
    //                + " " + Build.VERSION_CODES.class.getFields()[android.os.Build.VERSION.SDK_INT].getName();
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
    //endregion

    //region Toast Msg Show
    fun showMsg(id: Int?) {
        Toast.makeText(Talktiva.instance, Talktiva.instance!!.resources!!.getString(id!!), Toast.LENGTH_SHORT).show()
    }

    fun showMsg(string: String) {
        Toast.makeText(Talktiva.instance, string, Toast.LENGTH_SHORT).show()
    }
    //endregion

    fun setPrefrance(key: String?, value: String?): Boolean? {
        preferences = Talktiva.instance!!.getSharedPreferences(Talktiva.TAG, Context.MODE_PRIVATE)
        val editor = preferences!!.edit()
        editor.putString(key!!, value!!)
        return editor.commit()
    }

    fun getPrefrance(key: String?): String? {
        preferences = Talktiva.instance!!.getSharedPreferences(Talktiva.TAG, Context.MODE_PRIVATE)
        return preferences?.getString(key!!, null)
    }

    fun blankPrefrance(key: String?): Boolean? {
        preferences = Talktiva.instance!!.getSharedPreferences(Talktiva.TAG, Context.MODE_PRIVATE)
        val editor = preferences!!.edit()
        editor.putString(key!!, null)
        return editor.commit()
    }

    //region Snack Bar Message
//    fun showMsgSnack(view: View, message: String, action: String, listener: View.OnClickListener) {
//        Snackbar.make(view, message, Snackbar.LENGTH_LONG).setAction(action, listener).show()
//    }
    //endregion

    //region Store data to system local storage privately
//    fun storeData(fileName: String, data: String) {
//        val file = File(Talktiva.instance?.filesDir?.absolutePath + "/" + fileName)
//        if (file.exists()) {
//            if (file.delete()) {
//                try {
//                    val fileOut = Talktiva.instance?.openFileOutput(fileName, Context.MODE_PRIVATE)
//                    val outputWriter = OutputStreamWriter(fileOut)
//                    outputWriter.write(data)
//                    outputWriter.close()
//                } catch (e: FileNotFoundException) {
//                    e.printStackTrace()
//                } catch (e: IOException) {
//                    e.printStackTrace()
//                }
//
//            }
//        } else {
//            try {
//                val fileOut = Talktiva.instance?.openFileOutput(fileName, Context.MODE_PRIVATE)
//                val outputWriter = OutputStreamWriter(fileOut)
//                outputWriter.write(data)
//                outputWriter.close()
//            } catch (e: FileNotFoundException) {
//                e.printStackTrace()
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
//
//        }
//    }
    //endregion

    //region Get data from system local storage privately
//    fun getData(fileName: String): String {
//        val s = StringBuilder()
//        try {
//            val fileIn = Talktiva.instance?.openFileInput(fileName)
//            val inputRead = InputStreamReader(fileIn)
//            val inputBuffer = CharArray(Character.MAX_VALUE.toInt())
//            val charRead = inputRead.read(inputBuffer)
//            while (charRead > 0) {
//                val readstring = String(inputBuffer, 0, charRead)
//                s.append(readstring)
//            }
//            inputRead.close()
//        } catch (e: FileNotFoundException) {
//            e.printStackTrace()
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//
//        return s.toString()
//    }
    //endregion
}
