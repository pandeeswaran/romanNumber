package com.talktiva.pilot.helper

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo

@Suppress("DEPRECATION")
class NetworkChangeReceiver : BroadcastReceiver() {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifi: NetworkInfo? = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val mobile: NetworkInfo? = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
        if ((wifi != null && wifi.isAvailable) || (mobile != null && mobile.isAvailable)) {
            if (!wifi!!.isConnected && !mobile!!.isConnected) {
                Utility.requestInternet(context)
            } else {
                Utility.dismissRequestInternet()
            }
        }
    }
}
