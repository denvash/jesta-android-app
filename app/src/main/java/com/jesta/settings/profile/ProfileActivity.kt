package com.jesta.settings.profile

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jesta.MainActivity
import com.jesta.R
import com.jesta.util.SysManager
import kotlinx.android.synthetic.main.activity_path.*
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        jesta_profile_log_out_button.setOnClickListener {
            val sysManager = SysManager(this)
            sysManager.signOutUser(this@ProfileActivity)
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }


}