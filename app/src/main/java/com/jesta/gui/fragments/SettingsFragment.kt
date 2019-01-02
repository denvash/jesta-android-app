package com.jesta.gui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.jesta.R
import com.jesta.data.USER_EMPTY_DIAMONDS
import com.jesta.data.USER_EMPTY_PHOTO
import com.jesta.gui.activities.MainActivity
import com.jesta.gui.activities.login.LoginMainActivity
import com.jesta.utils.db.SysManager
import kotlinx.android.synthetic.main.fragment_settings.view.*
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper
import kotlin.random.Random

class SettingsFragment : Fragment() {

    private val sysManager = SysManager(this)

    companion object {
        private val TAG = SettingsFragment::class.java.simpleName
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        sysManager.createDBTask(SysManager.DBTask.RELOAD_USERS).addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                // todo error
            } else {
                updateUserLayout(view)
            }

            view.jesta_settings_profile_layout.visibility = View.VISIBLE
        }

        view.jesta_settings_tab_account.setOnClickListener {
            val userChangeAccount = sysManager.currentUserFromDB
            userChangeAccount.displayName =
                    "RandomAcc-"+Random.nextInt(500, 30000).toString()

            sysManager.setUserOnDB(userChangeAccount)
            sysManager.createDBTask(SysManager.DBTask.RELOAD_USERS).addOnCompleteListener {
                Toast.makeText(context, "User Last Mission updated", Toast.LENGTH_LONG).show()
                updateUserLayout(view)
            }
        }

        view.jesta_settings_button_log_out.setOnClickListener {
            sysManager.signOutUser(MainActivity.instance)
            MainActivity.instance.fragNavController.clearStack()
            MainActivity.instance.finish()

            val intent = Intent(context, LoginMainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        OverScrollDecoratorHelper.setUpOverScroll(view.jesta_settings_scroll_view)
        return view
    }

    private fun updateUserLayout(view: View) {
        val currentUser = sysManager.currentUserFromDB
        view.jesta_settings_profile_full_name.text = currentUser.displayName
        view.jesta_settings_profile_phone_number.text = currentUser.email
        view.jesta_settings_profile_full_name_progress.hide()

        if (currentUser.photoUrl != USER_EMPTY_PHOTO) {
            view.jesta_settings_avatar.setImageURI(Uri.parse(currentUser.photoUrl))
        }
        if (currentUser.diamonds == USER_EMPTY_DIAMONDS) {
            view.jesta_settings_diamond_amount.text = Random.nextInt(500, 30000).toString()
        }
    }
}