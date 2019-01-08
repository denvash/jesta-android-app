package com.jesta.gui.fragments.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jesta.R
import com.jesta.gui.activities.MainActivity
import com.jesta.utils.db.SysManager
import com.tapadoo.alerter.Alerter
import kotlinx.android.synthetic.main.fragment_login_email.view.*

class LoginEmailFragment : Fragment() {

    val sysManager = SysManager(MainActivity.instance)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_login_email, container, false)
        val instance = MainActivity.instance

        view.jesta_login_create_account.setOnClickListener {
            instance.fragNavController.pushFragment(LoginCreateAccountFragment())
        }

        view.jesta_login_email_button_back.setOnClickListener {
            instance.fragNavController.popFragment()
        }

        view.jesta_login_email_button_login.setOnClickListener {
            val emailView = view.jesta_login_email_value.text
            val passwordView = view.jesta_login_email_password.text
            if (emailView.isNullOrEmpty() || passwordView.isNullOrEmpty()) {
                Alerter.create(MainActivity.instance)
                    .setTitle("Please fill all fields \uD83D\uDE35")
                    .setBackgroundColorRes(R.color.colorPrimary)
                    .setIcon(R.drawable.ic_jesta_status_delete)
                    .show()
                return@setOnClickListener
            }

            sysManager.firebaseAuth.signInWithEmailAndPassword(emailView.toString(), passwordView.toString())
                .addOnCompleteListener{ emailLoginTask ->
                    if (!emailLoginTask.isSuccessful) {
                        MainActivity.instance.alertError(emailLoginTask.exception!!.message)
                        Log.e(LoginPathFragment::class.java.simpleName, emailLoginTask.exception!!.message)
                        return@addOnCompleteListener
                    }
                    MainActivity.instance.fragNavController.clearStack()
                    MainActivity.instance.restart()
                }
        }
        return view
    }
}