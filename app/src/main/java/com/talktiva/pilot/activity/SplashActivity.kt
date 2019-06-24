package com.talktiva.pilot.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.talktiva.pilot.helper.AppConstant
import com.talktiva.pilot.helper.Utility

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
        if (Utility.getPrefrance(AppConstant.ACCESS_TOKEN) != null) startActivity(Intent(this@SplashActivity, DashBoardActivity::class.java))
        else startActivity(Intent(this@SplashActivity, WelcomeActivity::class.java))
        finish()
    }
}
