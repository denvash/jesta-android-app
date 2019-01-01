package com.jesta.gui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jesta.R
import com.jesta.gui.activities.MainActivity
import com.jesta.gui.activities.login.LoginMainActivity
import com.jesta.utils.db.SysManager
import kotlinx.android.synthetic.main.fragment_settings.view.*
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper

class SettingsFragment : Fragment() {

    private val sysManager = SysManager(this)

    companion object {
        private val TAG = SettingsFragment::class.java.simpleName
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        // TODO: User null
//        val currentUser = sysManager.currentUserFromDB
//        view.jesta_settings_profile_full_name.text = currentUser.displayName
//        view.jesta_settings_profile_phone_number.text = currentUser.phoneNumber

//                Toast.makeText(
//                    this@AskJestaFragment,
//                    "Quota has been exceeded for this project.",
//                    Toast.LENGTH_LONG
//                ).show()

        view.jesta_settings_button_log_out.setOnClickListener {
            sysManager.signOutUser(context)
            val intent = Intent(context, LoginMainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            MainActivity.instance.fragNavController.clearStack()
            startActivity(intent)
        }

        OverScrollDecoratorHelper.setUpOverScroll(view.jesta_settings_scroll_view)

        return view
    }
}