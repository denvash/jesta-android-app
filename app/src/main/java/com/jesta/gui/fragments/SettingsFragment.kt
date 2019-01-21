package com.jesta.gui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jesta.R
import com.jesta.data.JESTA_WEBSITE
import com.jesta.data.USER_EMPTY_DIAMONDS
import com.jesta.data.User
import com.jesta.gui.activities.MainActivity
import com.jesta.gui.fragments.login.LoginPathFragment
import com.jesta.utils.db.SysManager
import com.squareup.picasso.Picasso
import com.tapadoo.alerter.Alerter
import kotlinx.android.synthetic.main.fragment_settings.view.*
import kotlinx.android.synthetic.main.jesta_about.view.*
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper
import kotlin.random.Random

class SettingsFragment : Fragment() {

    private val sysManager = MainActivity.instance.sysManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        if (sysManager.currentUserFromDB == null) return view

        updateUserLayout(view)
        view.jesta_settings_profile_layout.visibility = View.VISIBLE

        view.jesta_settings_button_log_out.setOnClickListener {
            sysManager.signOutUser(MainActivity.instance)
            val fragNavController = MainActivity.instance.fragNavController
            fragNavController.clearStack()
            fragNavController.pushFragment(LoginPathFragment())
        }

        OverScrollDecoratorHelper.setUpOverScroll(view.jesta_settings_scroll_view)

        view.tab_account_edit_display_name.setText(sysManager.currentUserFromDB.displayName)
        view.tab_account_edit_email.setText(sysManager.currentUserFromDB.email)

        view.jesta_settings_button_accept_changes.setOnClickListener {
            val currentUser = sysManager.currentUserFromDB
            val changedUser = currentUser.copy()

            val displayNameEditable = view.tab_account_edit_display_name.text
            val emailEditable = view.tab_account_edit_email.text

            if (!displayNameEditable.isNullOrBlank() && !displayNameEditable.isNullOrEmpty() &&
                !emailEditable.isNullOrBlank() && !emailEditable.isNullOrEmpty()
            ) {
                changedUser.displayName = displayNameEditable.toString()
                changedUser.email = emailEditable.toString()
            }

            if (areSameFields(changedUser, currentUser) == false) {
                view.jesta_settings_profile_layout.visibility = View.INVISIBLE
                sysManager.setUserOnDB(changedUser)
                sysManager.createDBTask(SysManager.DBTask.RELOAD_USERS).addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        return@addOnCompleteListener
                    } else {
                        updateUserLayout(view)
                    }
                    view.jesta_settings_profile_layout.visibility = View.VISIBLE
                }
            } else {
                Alerter.create(MainActivity.instance)
                    .setTitle("Nothing to change!")
                    .setText("Change display name or email \uD83D\uDD8A")
                    .setBackgroundColorRes(R.color.colorPrimary)
                    .setIcon(R.drawable.ic_jesta_diamond_normal)
                    .show()
//                Toast.makeText(MainActivity.instance, "Nothing to change!", Toast.LENGTH_LONG).show()
            }
        }

        view.jesta_settings_send_bug.setOnClickListener {

            val bugEditable = view.tab_bug_edit.text
            if (bugEditable.isNullOrBlank() || bugEditable.isNullOrEmpty()) {
                Alerter.create(MainActivity.instance)
                    .setTitle("You didn't type anything!")
                    .setText("Don't leave it empty! \uD83D\uDE05")
                    .setBackgroundColorRes(R.color.colorPrimary)
                    .setIcon(R.drawable.ic_jesta_diamond_normal)
                    .show()
                return@setOnClickListener
//                Toast.makeText(it.context, "Please fill your bug report", Toast.LENGTH_LONG).show()
            }

            val to = "jestaa80@gmail.com"
            val emailIntent = Intent(Intent.ACTION_SENDTO)
            emailIntent.data = Uri.parse("mailto:$to")
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "My feedback for Jesta-App")
            emailIntent.putExtra(Intent.EXTRA_TEXT, view.tab_bug_edit.text)
            startActivity(emailIntent)
        }

        view.jesta_about_github.setOnClickListener {
            val urlIntent = Intent(Intent.ACTION_VIEW)
            urlIntent.data = Uri.parse(JESTA_WEBSITE)
            MainActivity.instance.startActivity(urlIntent)
        }

        return view
    }

    private fun areSameFields(changed: User, current: User): Any {
        return changed.displayName == current.displayName && changed.email == current.email
    }

    private fun updateUserLayout(view: View) {
        val currentUser = sysManager.currentUserFromDB
        view.jesta_settings_profile_full_name.text = currentUser.displayName
        view.jesta_settings_profile_phone_number.text = currentUser.email
        view.jesta_settings_profile_full_name_progress.hide()

        Picasso.get().load(currentUser.photoUrl).noFade().into(view.jesta_settings_avatar)

        if (currentUser.diamonds == USER_EMPTY_DIAMONDS) {
            view.jesta_settings_diamond_amount.text = Random.nextInt(500, 30000).toString()
        }
    }
}