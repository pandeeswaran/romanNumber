package com.talktiva.pilot.helper

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager

@Suppress("DEPRECATION")
class NetworkChangeReceiver : BroadcastReceiver() {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
        if (wifi.isAvailable || mobile.isAvailable) {
            if (!wifi.isConnected && !mobile.isConnected) {
                Utility.requestInternet(context)
            } else {
                Utility.dismissRequestInternet()
            }
        }
    }
}
