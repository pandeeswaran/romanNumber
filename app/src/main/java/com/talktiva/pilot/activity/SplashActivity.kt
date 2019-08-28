package com.talktiva.pilot.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.talktiva.pilot.helper.AppConstant
import com.talktiva.pilot.helper.Utility

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Utility.getPreference(AppConstant.PREF_A_TOKEN) != null) {
            if (Utility.getPreference(AppConstant.PREF_PASS_FLAG) != null) {
                if (Utility.getPreference(AppConstant.PREF_PASS_FLAG).equals("true", true)) {
                    startActivity(Intent(this@SplashActivity, ChangePasswordActivity::class.java))
                } else {
                    startActivity(Intent(this@SplashActivity, DashBoardActivity::class.java))
                }
            } else {
                startActivity(Intent(this@SplashActivity, DashBoardActivity::class.java))
            }
        } else {
            startActivity(Intent(this@SplashActivity, WelcomeActivity::class.java))
        }
        finish()
    }
}
