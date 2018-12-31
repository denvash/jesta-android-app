package com.jesta.settings

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jesta.login.LoginMainActivity
import com.jesta.R
import com.jesta.askJesta.AskJestaActivity
import com.jesta.doJesta.DoJestaActivity
import com.jesta.settings.profile.ProfileActivity
import com.jesta.status.StatusActivity
import com.jesta.util.SysManager
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.frame_bottom_navigation_view.*

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

        jesta_settings_button_log_out.setOnClickListener {
            sysManager.signOutUser(this@SettingsActivity)
            val intent = Intent(this, LoginMainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        jesta_bottom_navigation.selectedItemId = R.id.nav_settings
        jesta_bottom_navigation.setOnNavigationItemSelectedListener {

            if (it.itemId == jesta_bottom_navigation.selectedItemId) return@setOnNavigationItemSelectedListener true

            val intent = when (it.itemId) {
                R.id.nav_do_jesta -> {
                    Intent(this@SettingsActivity, DoJestaActivity::class.java)
                }
                R.id.nav_status -> {
                    Intent(this@SettingsActivity, StatusActivity::class.java)
                }
                else -> {
                    Intent(this@SettingsActivity, AskJestaActivity::class.java)
                }
            }
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            finish()
            startActivity(intent)
            true
        }
    }
}