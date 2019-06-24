package com.talktiva.pilot.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
//        if (Utility.getPrefrance(AppConstant.TOKEN) != null) startActivity(Intent(this@SplashActivity, DashBoardActivity::class.java))
//        else startActivity(Intent(this@SplashActivity, WelcomeActivity::class.java))
        finish()
    }
}
