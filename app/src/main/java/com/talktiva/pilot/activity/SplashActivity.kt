package com.talktiva.pilot.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.talktiva.pilot.helper.AppConstant
import com.talktiva.pilot.helper.Utility

class SplashActivity : AppCompatActivity() {

//    var entityId: String? = null
//    var action: String? = null
//    var body: String? = null
//    var type: String? = null
//    var title: String? = null
//    var notificationId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        if (intent.extras != null) {
//            entityId = intent.extras.getString("entityId")
//            action = intent.extras.getString("action")
//            body = intent.extras.getString("body")
//            type = intent.extras.getString("type")
//            title = intent.extras.getString("title")
//            notificationId = intent.extras.getString("notificationId")
//            startActivity(Intent(this@SplashActivity, DashBoardActivity::class.java))
//        } else {
//
//        }
        if (Utility.getPreference(AppConstant.PREF_A_TOKEN) != null) {
            startActivity(Intent(this@SplashActivity, DashBoardActivity::class.java))
        } else {
            startActivity(Intent(this@SplashActivity, WelcomeActivity::class.java))
        }
        finish()
    }
}
