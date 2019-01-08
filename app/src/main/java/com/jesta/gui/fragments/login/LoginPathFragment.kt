package com.jesta.gui.fragments.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FacebookAuthProvider
import com.jesta.R
import com.jesta.gui.activities.MainActivity
import com.jesta.utils.db.SysManager
import kotlinx.android.synthetic.main.fragment_login_path.view.*
import kotlinx.android.synthetic.main.jesta_main_activity.*
import java.util.*

class LoginPathFragment : Fragment() {

    val sysManager = SysManager(MainActivity.instance)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_login_path, container, false)
        val instance = MainActivity.instance

        instance.hideBottomNavigation()
        instance.jesta_main_progress_bar.visibility = View.INVISIBLE
        instance.jesta_main_container.visibility = View.VISIBLE

        // Email Registration
        view.jesta_login_email_button.setOnClickListener {
            instance.fragNavController.pushFragment(LoginEmailFragment());
        }

        // Google Registration
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val mGoogleSignInClient = GoogleSignIn.getClient(instance, gso)
        view.jesta_login_google_button.setOnClickListener {
            startActivityForResult(mGoogleSignInClient.signInIntent, 101)
        }

        // Facebook
        val fbCallbackManager = CallbackManager.Factory.create()

        LoginManager.getInstance().registerCallback(fbCallbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    handleFacebookToken(loginResult.accessToken)
                }

                override fun onCancel() {}

                override fun onError(exception: FacebookException) {
                    instance.alertError(exception.message)
                    Log.e(LoginPathFragment::class.java.simpleName, exception.message)
                    return
                }
            })

        view.jesta_login_facebook_button.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(instance, Arrays.asList("email", "public_profile"))
        }

        return view
    }

    fun handleFacebookToken(accessToken: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(accessToken.token)
        sysManager.firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener {fbLoginTask ->
                if (!fbLoginTask.isSuccessful) {
                    MainActivity.instance.alertError(fbLoginTask.exception!!.message)
                    Log.e(LoginPathFragment::class.java.simpleName, fbLoginTask.exception!!.message)
                    return@addOnCompleteListener
                }
                MainActivity.instance.fragNavController.clearStack()
                MainActivity.instance.restart()
            }
    }
}