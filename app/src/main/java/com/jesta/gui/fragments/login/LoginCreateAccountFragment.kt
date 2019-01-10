package com.jesta.gui.fragments.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.jesta.R
import com.jesta.data.User
import com.jesta.gui.activities.MainActivity
import com.jesta.utils.db.SysManager
import com.tapadoo.alerter.Alerter
import kotlinx.android.synthetic.main.fragment_login_create_account.view.*

class LoginCreateAccountFragment : Fragment() {

    private val sysManager = SysManager(MainActivity.instance)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_login_create_account, container, false)
        val instance = MainActivity.instance

        view.jesta_login_create_button_back.setOnClickListener {
            instance.fragNavController.popFragment()
        }

        view.jesta_login_create_account_button.setOnClickListener {
            val emailView = view.jesta_login_create_email.text
            val passwordView = view.jesta_login_create_password.text
            val displayNameView = view.jesta_login_create_display_name.text

            if (passwordView.toString().length < 6 || displayNameView.isNullOrEmpty() || emailView.isNullOrEmpty() || passwordView.isNullOrEmpty()) {
                Alerter.create(MainActivity.instance)
                    .setTitle("Please fill all fields \uD83D\uDE35")
                    .setText("Password should be at least 6 characters")
                    .setBackgroundColorRes(R.color.colorPrimary)
                    .setIcon(R.drawable.ic_jesta_status_delete)
                    .show()
                return@setOnClickListener
            }

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(emailView.toString(), passwordView.toString())
                .addOnCompleteListener { task ->

                    if (!task.isSuccessful) {
                        MainActivity.instance.alertError(task.exception?.message)
                        Log.e(LoginPathFragment::class.java.simpleName, task.exception?.message)
                        return@addOnCompleteListener
                    }

                    val user = task.result?.user

                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(displayNameView.toString()).build()

                    user?.updateProfile(profileUpdates)

                    // user created in firebase only
                    Toast.makeText(instance, "User created successfully", Toast.LENGTH_SHORT)
                        .show()

                    sysManager.firebaseAuth.signInWithEmailAndPassword(emailView.toString(), passwordView.toString())
                        .addOnCompleteListener { userLoginTask ->
                            if (!userLoginTask.isSuccessful) {
                                MainActivity.instance.alertError(task.exception!!.message)
                                Log.e(LoginPathFragment::class.java.simpleName, userLoginTask.exception!!.message)
                                @Suppress("LABEL_NAME_CLASH")
                                return@addOnCompleteListener
                            }

                            sysManager.setUserOnDB(User(user!!.uid,displayNameView.toString(),email = user.email!!))

                            MainActivity.instance.fragNavController.clearStack()
                            MainActivity.instance.restart()
                        }
                }
        }
        return view
    }
}