package com.talktiva.pilot.helper;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.Constraints;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.snackbar.Snackbar;
import com.talktiva.pilot.R;
import com.talktiva.pilot.Talktiva;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Objects;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class Utility {

    private static Dialog dialogInternet;

    public static Typeface getFontRegular() {
        return Typeface.createFromAsset(Talktiva.getInstance().getAssets(), "Merriweather_regular.ttf");
    }

    public static Typeface getFontBold() {
        return Typeface.createFromAsset(Talktiva.getInstance().getApplicationContext().getAssets(), "Merriweather_bold.ttf");
    }

    public static void setTitleText(Toolbar toolbar, Integer id, Integer title) {
        TextView textView = toolbar.findViewById(id);
        textView.setTypeface(getFontRegular());
        textView.setText(title);
    }

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
    private static boolean isNetworkAvailable() {
        return ((ConnectivityManager) Talktiva.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
    }

    public static boolean isConnectingToInternet() {
        if (isNetworkAvailable()) {
            NetworkInfo info = ((ConnectivityManager) Talktiva.getInstance().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
            if (info != null)
                return info.getState() == NetworkInfo.State.CONNECTED;
        }
        return false;
    }

    @SuppressLint("InlinedApi")
    static void requestInternet(Context context) {
        dialogInternet = showAlert(context, R.string.internet_msg, false, VISIBLE, R.string.dd_setting, v -> {
            dialogInternet.dismiss();
            Talktiva.getInstance().startActivity(new Intent(Settings.ACTION_DATA_USAGE_SETTINGS));
        }, GONE, null, null);
        dialogInternet.show();
    }

    static void dismissRequestInternet() {
        if (dialogInternet != null) {
            if (dialogInternet.isShowing()) {
                dialogInternet.dismiss();
            }
        }
    }
    //endregion

    //region AlertDialog
    public static Dialog showAlert(Context context, Integer msg, Boolean bool, Integer positiveVisibility, Integer positiveTitle, View.OnClickListener positiveClickListener, Integer negativeVisibility, Integer negativeTitle, View.OnClickListener negativeClickListener) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dd_layout);
        dialog.setCancelable(bool);

        ((TextView) dialog.findViewById(R.id.dialog_msg)).setTypeface(getFontRegular());
        ((TextView) dialog.findViewById(R.id.dialog_msg)).setText(msg);

        ((Button) dialog.findViewById(R.id.dialog_positive)).setTypeface(getFontRegular());
        ((Button) dialog.findViewById(R.id.dialog_positive)).setText(positiveTitle);
        dialog.findViewById(R.id.dialog_positive).setVisibility(positiveVisibility);
        dialog.findViewById(R.id.dialog_positive).setOnClickListener(positiveClickListener);

        dialog.findViewById(R.id.dialog_negative).setVisibility(negativeVisibility);

        if (negativeVisibility != GONE) {
            ((Button) dialog.findViewById(R.id.dialog_negative)).setTypeface(getFontRegular());
            ((Button) dialog.findViewById(R.id.dialog_negative)).setText(negativeTitle);
            dialog.findViewById(R.id.dialog_negative).setOnClickListener(negativeClickListener);
        }

        Objects.requireNonNull(dialog.getWindow()).setLayout(Constraints.LayoutParams.MATCH_PARENT, Constraints.LayoutParams.WRAP_CONTENT);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        return dialog;
    }
    //endregion

    //region Progress Dialog
    public static Dialog showProgress(Context context) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dd_loader);
        dialog.setCancelable(false);
        ((LottieAnimationView) dialog.findViewById(R.id.progress)).setAnimation("loader.json");
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        return dialog;
    }

    public static void dismissDialog(Dialog dialog) {
        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }
    //endregion

    //region Error Dialog
    public static Dialog showError(Context context, Integer msg, Integer action, View.OnClickListener clickListener) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dd_error);
        dialog.setCancelable(false);

        ((TextView) dialog.findViewById(R.id.de_tv)).setTypeface(getFontRegular());
        ((TextView) dialog.findViewById(R.id.de_tv)).setText(msg);

        ((Button) dialog.findViewById(R.id.de_btn)).setTypeface(getFontRegular());
        ((Button) dialog.findViewById(R.id.de_btn)).setText(action);
        dialog.findViewById(R.id.de_btn).setOnClickListener(clickListener);

        Objects.requireNonNull(dialog.getWindow()).setLayout(Constraints.LayoutParams.MATCH_PARENT, Constraints.LayoutParams.MATCH_PARENT);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        return dialog;
    }
    //endregion

    //region Toast Msg Show
    public static void showMsg(Integer id) {
        Toast.makeText(Talktiva.getInstance(), Talktiva.getInstance().getResources().getString(id), Toast.LENGTH_SHORT).show();
    }

    public static void showMsg(String string) {
        Toast.makeText(Talktiva.getInstance(), string, Toast.LENGTH_SHORT).show();
    }
    //endregion

    //region Snack Bar Message
    public static void showMsgSnack(View view, String message, String action, View.OnClickListener listener) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).setAction(action, listener).show();
    }
    //endregion

    //region Store data to system local storage privately
    public static void storeData(String fileName, String data) {
        File file = new File(Talktiva.getInstance().getFilesDir().getAbsolutePath() + "/" + fileName);
        if (file.exists()) {
            if (file.delete()) {
                try {
                    FileOutputStream fileOut = Talktiva.getInstance().openFileOutput(fileName, Context.MODE_PRIVATE);
                    OutputStreamWriter outputWriter = new OutputStreamWriter(fileOut);
                    outputWriter.write(data);
                    outputWriter.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                FileOutputStream fileOut = Talktiva.getInstance().openFileOutput(fileName, Context.MODE_PRIVATE);
                OutputStreamWriter outputWriter = new OutputStreamWriter(fileOut);
                outputWriter.write(data);
                outputWriter.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //endregion

    //region Get data from system local storage privately
    public static String getData(String fileName) {
        StringBuilder s = new StringBuilder();
        try {
            FileInputStream fileIn = Talktiva.getInstance().openFileInput(fileName);
            InputStreamReader InputRead = new InputStreamReader(fileIn);
            char[] inputBuffer = new char[Character.MAX_VALUE];
            int charRead;
            while ((charRead = InputRead.read(inputBuffer)) > 0) {
                String readstring = String.copyValueOf(inputBuffer, 0, charRead);
                s.append(readstring);
            }
            InputRead.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s.toString();
    }
    //endregion
}
