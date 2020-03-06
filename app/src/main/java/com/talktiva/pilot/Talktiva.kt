package com.talktiva.pilot

import android.app.Application

class Talktiva : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        TAG = resources.getString(R.string.app_name)
    }

    companion object {
        var TAG: String? = null
        var instance: Talktiva? = null
            private set
    }
}
