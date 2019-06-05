package com.talktiva.pilot.helper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.support.constraint.Constraints;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.talktiva.pilot.R;

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

    private final Activity context;
    private ProgressDialog progressDialog;
    private Dialog dialogInternet;

    public Utility(Activity context) {
        this.context = context;
    }

    public Typeface getFont() {
        return Typeface.createFromAsset(context.getApplicationContext().getAssets(), "merriweather_regular.ttf");
    }

    public void setTitleText(Toolbar toolbar, int id, String title) {
        TextView textView = toolbar.findViewById(id);
        textView.setTypeface(getFont());
        textView.setText(title);
    }

    public void setTitleFont(Toolbar toolbar) {
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View v = toolbar.getChildAt(i);
            if (v instanceof TextView && ((TextView) v).getText() == context.getTitle()) {
                ((TextView) v).setTypeface(getFont());
            }
        }
    }

    //region Device Id and Device Name
    @SuppressLint("HardwareIds")
    public String getDeviceId() {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public String getDeviceName() {
        return Build.MANUFACTURER
                + " " + Build.MODEL + " " + Build.VERSION.RELEASE
                + " " + Build.VERSION_CODES.class.getFields()[android.os.Build.VERSION.SDK_INT].getName();
    }
    //endregion

    //region Check GPS And Dialog
    public boolean checkGPS() {
        LocationManager mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public void showGpsAlert() {
        new AlertDialog.Builder(context)
                .setTitle(context.getResources().getString(R.string.app_name))
                .setMessage(R.string.gps_msg)
                .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
    //endregion

    //region Connection
    private boolean isNetworkAvailable() {
        return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
    }

    public boolean isConnectingToInternet() {
        if (isNetworkAvailable()) {
            NetworkInfo info = ((ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
            if (info != null)
                return info.getState() == NetworkInfo.State.CONNECTED;
        }
        return false;
    }

    void requestInternet() {
        dialogInternet = showAlert(context.getResources().getString(R.string.internet_msg), false, VISIBLE, context.getResources().getString(R.string.dd_setting), new View.OnClickListener() {
            @SuppressLint("InlinedApi")
            @Override
            public void onClick(View v) {
                dialogInternet.dismiss();
                context.startActivity(new Intent(Settings.ACTION_DATA_USAGE_SETTINGS));
            }
        }, GONE, null, null);
        dialogInternet.show();
    }

    void dismissRequestInternet() {
        if (dialogInternet != null) {
            if (dialogInternet.isShowing()) {
                dialogInternet.dismiss();
            }
        }
    }
    //endregion

    //region AlertDialog
    public Dialog showAlert(String msg, boolean bool, int positiveVisibility, String positiveTitle, View.OnClickListener positiveClickListener, int negativeVisibility, String negativeTitle, View.OnClickListener negativeClickListener) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_layout);
        dialog.setCancelable(bool);
        ((TextView) dialog.findViewById(R.id.dialog_msg)).setTypeface(getFont());
        ((TextView) dialog.findViewById(R.id.dialog_msg)).setText(msg);
        ((Button) dialog.findViewById(R.id.dialog_positive)).setTypeface(getFont());
        ((Button) dialog.findViewById(R.id.dialog_positive)).setText(positiveTitle);
        dialog.findViewById(R.id.dialog_positive).setVisibility(positiveVisibility);
        dialog.findViewById(R.id.dialog_positive).setOnClickListener(positiveClickListener);
        ((Button) dialog.findViewById(R.id.dialog_negative)).setTypeface(getFont());
        ((Button) dialog.findViewById(R.id.dialog_negative)).setText(negativeTitle);
        dialog.findViewById(R.id.dialog_negative).setVisibility(negativeVisibility);
        dialog.findViewById(R.id.dialog_negative).setOnClickListener(negativeClickListener);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        return dialog;
    }
    //endregion

    //region Progress Dialog
    public Dialog showProgress() {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.loader_layout);
        dialog.setCancelable(false);
        ((LottieAnimationView) dialog.findViewById(R.id.progress)).setAnimation("loader.json");
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        return dialog;
    }

    public void dismissDialog(Dialog progressDialog) {
        if (progressDialog != null) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }
    //endregion

    public Dialog showError(String msg, String action, View.OnClickListener clickListener) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_error);
        dialog.setCancelable(false);
        ((TextView) dialog.findViewById(R.id.de_tv)).setTypeface(getFont());
        ((TextView) dialog.findViewById(R.id.de_tv)).setText(msg);
        ((Button) dialog.findViewById(R.id.de_btn)).setTypeface(getFont());
        ((Button) dialog.findViewById(R.id.de_btn)).setText(action);
        dialog.findViewById(R.id.de_btn).setOnClickListener(clickListener);
        Objects.requireNonNull(dialog.getWindow()).setLayout(Constraints.LayoutParams.MATCH_PARENT, Constraints.LayoutParams.MATCH_PARENT);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        return dialog;
    }

    //region Toast Msg Show
    public void showMsg(int id) {
        Toast.makeText(context.getApplicationContext(), context.getResources().getString(id), Toast.LENGTH_SHORT).show();
    }

    public void showMsg(String string) {
        Toast.makeText(context.getApplicationContext(), string, Toast.LENGTH_SHORT).show();
    }
    //endregion

    //region Snack Bar Message
    public void showMsgSnack(View view, String message, String action, View.OnClickListener listener) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).setAction(action, listener).show();
    }
    //endregion

    //region Store data to system local storage privately
    public void storeData(String fileName, String data) {
        File file = new File(context.getFilesDir().getAbsolutePath() + "/" + fileName);
        if (file.exists()) {
            if (file.delete()) {
                try {
                    FileOutputStream fileOut = context.openFileOutput(fileName, Context.MODE_PRIVATE);
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
                FileOutputStream fileOut = context.openFileOutput(fileName, Context.MODE_PRIVATE);
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
    public String getData(String fileName) {
        StringBuilder s = new StringBuilder();
        try {
            FileInputStream fileIn = context.openFileInput(fileName);
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
