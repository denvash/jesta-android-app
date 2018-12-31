package com.jesta.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jesta.MainActivity
import com.jesta.R
import com.jesta.util.SysManager

class SettingsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        jesta_settings_profile_full_name.text = currentUser.displayName
//        jesta_settings_profile_phone_number.text = currentUser.phoneNumber

        //        profile_constraint_layout.setOnClickListener {
//            val intent = Intent(this@SettingsFragment, ProfileActivity::class.java)
//            startActivity(intent)
//        }

//        jesta_settings_button_log_out.setOnClickListener {
            //            sysManager.signOutUser(this@SettingsFragment)
//            val intent = Intent(this, LoginMainActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//            startActivity(intent)
//        }

    }



    //    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_settings)
//
//        val sysManager = SysManager(this@SettingsFragment)
//        val currentUser = sysManager.currentUserFromDB
//
//        jesta_settings_profile_full_name.text = currentUser.displayName
//        jesta_settings_profile_phone_number.text = currentUser.phoneNumber
//
//        profile_constraint_layout.setOnClickListener {
//            val intent = Intent(this@SettingsFragment, ProfileActivity::class.java)
//            startActivity(intent)
//        }
//
//        jesta_settings_button_log_out.setOnClickListener {
//            sysManager.signOutUser(this@SettingsFragment)
//            val intent = Intent(this, LoginMainActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//            startActivity(intent)
//        }
//
//        jesta_bottom_navigation.selectedItemId = R.id.nav_settings
//        jesta_bottom_navigation.setOnNavigationItemSelectedListener {
//
//            if (it.itemId == jesta_bottom_navigation.selectedItemId) return@setOnNavigationItemSelectedListener true
//
//            val intent = when (it.itemId) {
//                R.id.nav_do_jesta -> {
//                    Intent(this@SettingsFragment, DoJestaFragment::class.java)
//                }
//                R.id.nav_status -> {
//                    Intent(this@SettingsFragment, StatusFragment::class.java)
//                }
//                else -> {
//                    Intent(this@SettingsFragment, AskJestaFragment::class.java)
//                }
//            }
//            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//            finish()
//            startActivity(intent)
//            true
//        }
//    }
}