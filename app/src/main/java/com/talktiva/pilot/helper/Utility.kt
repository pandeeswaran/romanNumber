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
import android.net.Uri
import android.os.Build
import android.os.Parcelable
import android.provider.MediaStore
import android.provider.Settings
import android.util.TypedValue
import android.view.Gravity
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
import java.io.File
import java.util.*


object Utility {

    private var dialogInternet: Dialog? = null
    private var preferences: SharedPreferences? = null

    private const val STRING_LENGTH = 10
    private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

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

    fun getDeviceName(): String {
        val fields = Build.VERSION_CODES::class.java.fields
        val versionName = fields[Build.VERSION.SDK_INT].name
        return Build.MANUFACTURER.plus(" ").plus(Build.MODEL).plus(" ").plus(Build.VERSION.RELEASE).plus(" ").plus(versionName)
    }
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

        (dialog.findViewById<TextView>(R.id.dialog_msg)).typeface = fontBold
        (dialog.findViewById<TextView>(R.id.dialog_msg)).setText(msg!!)
        (dialog.findViewById<TextView>(R.id.dialog_msg)).setTextColor(context.resources.getColor(color!!))
        (dialog.findViewById<TextView>(R.id.dialog_msg)).gravity = Gravity.START
        (dialog.findViewById<TextView>(R.id.dialog_msg)).setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)

        dialog.findViewById<Button>(R.id.dialog_positive).visibility = positiveVisibility!!

        if (positiveVisibility != GONE) {
            (dialog.findViewById<Button>(R.id.dialog_positive)).typeface = fontRegular
            (dialog.findViewById<Button>(R.id.dialog_positive)).setText(positiveTitle!!)
            dialog.findViewById<Button>(R.id.dialog_positive).setOnClickListener(positiveClickListener)
        }

        dialog.findViewById<Button>(R.id.dialog_negative).visibility = negativeVisibility!!

        if (negativeVisibility != GONE) {
            (dialog.findViewById<Button>(R.id.dialog_negative)).typeface = fontRegular
            (dialog.findViewById<Button>(R.id.dialog_negative)).setText(negativeTitle!!)
            dialog.findViewById<Button>(R.id.dialog_negative).setOnClickListener(negativeClickListener)
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

//    fun showMsg(string: String) {
//        Toast.makeText(Talktiva.instance, string, Toast.LENGTH_SHORT).show()
//    }
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

    //region Snack Bar Message
//    fun showMsgSnack(view: View, message: String, action: String, listener: View.OnClickListener) {
//        Snackbar.make(view, message, Snackbar.LENGTH_LONG).setAction(action, listener).show()
//    }
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

    //region Get image uri captured by camera
    fun getCaptureImageOutputUri(): Uri? {
        val file = File(Talktiva.instance?.filesDir?.absolutePath + "/" + random().plus(".png"))
        if (file.exists()) {
            if (file.delete()) {
                file.createNewFile()
            }
        } else {
            file.createNewFile()
        }
        return Uri.fromFile(file)
    }
    //endregion

    //region Image Popup
    fun getPickImageChooserForCamera(context: Context): Intent {
        val outputFileUri = getCaptureImageOutputUri()
        val allIntents = ArrayList<Intent>()
        val packageManager = context.packageManager
        var intent1: Intent

        //region Add camera apps from installed apps
        val captureIntent = Intent()
        captureIntent.action = MediaStore.ACTION_IMAGE_CAPTURE
        val infos = packageManager.queryIntentActivities(captureIntent, 0)
        for (resolveInfo in infos) {
            intent1 = Intent(captureIntent)
            intent1.component = ComponentName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name)
            intent1.setPackage(resolveInfo.activityInfo.packageName)
            if (outputFileUri != null) {
                intent1.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri)
            }
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

    //region Image Popup
    fun getPickImageChooserForGallery(context: Context): Intent {
        val allIntents = ArrayList<Intent>()
        val packageManager = context.packageManager
        var intent1: Intent

        //region Add gallery apps from installed apps
        val galleryIntent = Intent()
        galleryIntent.action = Intent.ACTION_GET_CONTENT
        galleryIntent.type = "image/*"
        val infos = packageManager.queryIntentActivities(galleryIntent, 0)
        for (resolveInfo in infos) {
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

    private fun random(): String {
        return (1..STRING_LENGTH).map { kotlin.random.Random.nextInt(0, charPool.size) }.map(charPool::get).joinToString("")
    }

    //region Get pick image result uri
//    fun isGallery(data: Intent?): Boolean {
//        var isGallery = false
//        if (data != null) {
//            isGallery = data.data != null
//        }
//        return isGallery
//    }
    //endregion

    //region Rotate image if required
//    @Throws(IOException::class)
//    fun rotateImageIfRequired(context: Activity, img: Bitmap, selectedImage: Uri): Bitmap {
//        val path = ImagePath(context, context.contentResolver).getUriRealPathAboveKitkat(selectedImage)
//        val ei = ExifInterface(path)
//        return when (ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
//            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(img, 90)
//            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(img, 180)
//            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(img, 270)
//            else -> img
//        }
//    }
    //endregion

    //region Rotate image
//    private fun rotateImage(img: Bitmap, degree: Int): Bitmap {
//        val matrix = Matrix()
//        matrix.postRotate(degree.toFloat())
//        val rotatedImg = Bitmap.createBitmap(img, 0, 0, img.width, img.height, matrix, true)
//        img.recycle()
//        return rotatedImg
//    }
    //endregion

    //region Resized bitmap
//    fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap {
//        var width = image.width
//        var height = image.height
//
//        val bitmapRatio = width.toFloat() / height.toFloat()
//        if (bitmapRatio > 0) {
//            width = maxSize
//            height = (width / bitmapRatio).toInt()
//        } else {
//            height = maxSize
//            width = (height * bitmapRatio).toInt()
//        }
//
//        return Bitmap.createScaledBitmap(image, width, height, true)
//    }
    //endregion

    //region Convert Bitmap to Base64 String
//    fun convertBitmapToBase64String(bitmap: Bitmap): String {
//        val baos = ByteArrayOutputStream()
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos)
//        val b = baos.toByteArray()
//        return Base64.encodeToString(b, Base64.DEFAULT)
//    }
    //endregion

    //region Convert Bitmap64 from image path
//    private fun convertBase64(path: String): String {
//        val encoded: String
//        var fis: FileInputStream? = null
//        try {
//            fis = FileInputStream(File(path))
//        } catch (e: FileNotFoundException) {
//            e.printStackTrace()
//        }
//
//        val bm = BitmapFactory.decodeStream(fis)
//        val baos = ByteArrayOutputStream()
//        bm.compress(Bitmap.CompressFormat.JPEG, 60, baos)
//        val b = baos.toByteArray()
//        encoded = Base64.encodeToString(b, Base64.DEFAULT)
//
//        return encoded
//    }
    //endregion
}
