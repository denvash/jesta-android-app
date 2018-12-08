package com.jesta.settings

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jesta.R
import com.jesta.settings.profile.ProfileActivity
import com.jesta.util.SysManager
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val sysManager = SysManager(this@SettingsActivity)
        val currentUser = sysManager.currentUserFromDB

        jesta_settings_profile_full_name.text = currentUser.displayName
        jesta_settings_profile_phone_number.text = currentUser.phoneNumber

        profile_constraint_layout.setOnClickListener {
            val intent = Intent(this@SettingsActivity, ProfileActivity::class.java)
            startActivity(intent)
        }
    }
}