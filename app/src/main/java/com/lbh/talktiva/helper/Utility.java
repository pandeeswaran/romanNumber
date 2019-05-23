package com.lbh.talktiva.helper;

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
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.lbh.talktiva.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class Utility {

    private ProgressDialog progressDialog;
    private final Activity context;
    private Dialog dialogInternet;

    public Utility(Activity context) {
        this.context = context;
    }

    public Typeface getFont() {
        return Typeface.createFromAsset(context.getApplicationContext().getAssets(), "merriweather_regular.ttf");
    }

    public void setTitleFont(Toolbar toolbar) {
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View v = toolbar.getChildAt(i);
            if (v instanceof TextView && ((TextView) v).getText() == context.getTitle()) {
                ((TextView) v).setTypeface(getFont());
                ((TextView) v).setGravity(Gravity.CENTER);
                Toolbar.LayoutParams layoutParams = (Toolbar.LayoutParams) v.getLayoutParams();
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                toolbar.requestLayout();
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
        dialogInternet = showAlert("Network Alert", context.getResources().getString(R.string.internet_msg), false, "Setting", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                context.startActivity(new Intent(Settings.ACTION_DATA_USAGE_SETTINGS));
            }
        }, null, null);
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
    public Dialog showAlert(String title, String msg, boolean bool, String positiveTitle, DialogInterface.OnClickListener positiveClickListener, String negativeTitle, DialogInterface.OnClickListener negativeClickListener) {
        return new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(msg)
                .setCancelable(bool)
                .setPositiveButton(positiveTitle, positiveClickListener)
                .setNegativeButton(negativeTitle, negativeClickListener)
                .create();
    }
    //endregion

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
        String s = "";
        try {
            FileInputStream fileIn = context.openFileInput(fileName);
            InputStreamReader InputRead = new InputStreamReader(fileIn);
            char[] inputBuffer = new char[Character.MAX_VALUE];
            int charRead;
            while ((charRead = InputRead.read(inputBuffer)) > 0) {
                String readstring = String.copyValueOf(inputBuffer, 0, charRead);
                s += readstring;
            }
            InputRead.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }
    //endregion

    //region Progress Dialog
    public ProgressDialog getProgress() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        return progressDialog;
    }
    //endregion
}
