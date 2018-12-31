package com.jesta.settings

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.jesta.R
import com.jesta.doJesta.DoJestaFragment
import com.jesta.util.SysManager
import kotlinx.android.synthetic.main.fragment_settings.view.*

class SettingsFragment : Fragment() {

    private val sysManager = SysManager(this)

    companion object {
        private val TAG = SettingsFragment::class.java.simpleName
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.jesta_settings_button_log_out.setOnClickListener {
            Log.d("setup","HELLO")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        // TODO: User null
//        val currentUser = sysManager.currentUserFromDB
//        view.jesta_settings_profile_full_name.text = currentUser.displayName
//        view.jesta_settings_profile_phone_number.text = currentUser.phoneNumber

        view.profile_layout.setOnClickListener {
//            Log.i(TAG,"Profile Clicked")
//            val intent = Intent(context, ProfileActivity::class.java)
//            startActivity(intent)
        }

        view.jesta_settings_button_log_out.setOnClickListener {
            Log.d("setup","HELLO")
//            val intent = Intent(this@AskJestaFragment, DoJestaFragment::class.java)

//            sysManager.signOutUser(context)
//            val intent = Intent(context, LoginMainActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//            startActivity(intent)
        }

        return view
    }
}