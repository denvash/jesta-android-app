package com.jesta.gui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
import com.jesta.R
import com.jesta.data.*
import com.jesta.gui.fragments.AskJestaFragment
import com.jesta.gui.fragments.DoJestaFragment
import com.jesta.gui.fragments.SettingsFragment
import com.jesta.gui.fragments.StatusFragment
import com.jesta.gui.fragments.login.LoginPathFragment
import com.jesta.utils.db.SysManager
import com.ncapdevi.fragnav.FragNavController
import com.ncapdevi.fragnav.FragNavLogger
import com.tapadoo.alerter.Alerter
import kotlinx.android.synthetic.main.jesta_main_activity.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent


class MainActivity : AppCompatActivity(), FragNavController.RootFragmentListener,
    FragNavController.TransactionListener {

    val fragNavController: FragNavController = FragNavController(supportFragmentManager, R.id.jesta_main_container)
    lateinit var sysManager: SysManager
    private lateinit var fbCallbackManager: CallbackManager

    companion object {
        lateinit var instance: MainActivity
            private set
    }

    fun getInstance(): MainActivity {
        return instance
    }

    override val numberOfRootFragments: Int = 4

    override fun getRootFragment(index: Int): Fragment {
        when (index) {
            INDEX_DO_JESTA -> return DoJestaFragment()
            INDEX_ASK_JESTA -> return AskJestaFragment()
            INDEX_STATUS -> return StatusFragment()
            INDEX_SETTINGS -> return SettingsFragment()
        }
        throw IllegalStateException("Need to send an index that we know")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.jesta_main_activity)

        instance = this

        sysManager = SysManager(instance)

        KeyboardVisibilityEvent.setEventListener(this@MainActivity) { isKeyboardOpen ->
            if (isKeyboardOpen) hideBottomNavigation() else showBottomNavigation()
        }

        fragNavController.apply {
            transactionListener = this@MainActivity
            rootFragmentListener = this@MainActivity
            createEager = true
            fragNavLogger = object : FragNavLogger {
                override fun error(message: String, throwable: Throwable) {
                    alertError(message)
                }
            }

            fragmentHideStrategy = FragNavController.DETACH_ON_NAVIGATE_HIDE_ON_SWITCH


            // Facebook
            fbCallbackManager = CallbackManager.Factory.create()

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

        }

        sysManager.createDBTask(SysManager.DBTask.RELOAD_USERS).addOnCompleteListener {

            fragNavController.initialize(INDEX_DO_JESTA, savedInstanceState)

            val user = sysManager.currentUserFromDB
            if (user == null) {
                if (!fragNavController.isRootFragment && fragNavController.size != 0) {
                    fragNavController.clearStack()
                }
                fragNavController.pushFragment(LoginPathFragment())
            } else {

                jesta_main_progress_bar.visibility = View.INVISIBLE
                jesta_main_container.visibility = View.VISIBLE
                jesta_main_line_view.visibility = View.VISIBLE
                jesta_bottom_navigation.visibility = View.VISIBLE
                // Add random avatar for empty ones
                if (user.photoUrl == USER_EMPTY_PHOTO) {
                    user.photoUrl = avatarList.random()
                    sysManager.setUserOnDB(user)
                }
                sysManager.listenForIncomingInboxMessages(instance)
                sysManager.listenForChatAndNotify(instance)
                fragNavController.pushFragment(DoJestaFragment())
            }

        }

        jesta_bottom_navigation.setOnNavigationItemSelectedListener {
            if (!fragNavController.isRootFragment) {
                fragNavController.popFragment()
            }

            when (it.itemId) {
                R.id.nav_do_jesta -> {
                    fragNavController.switchTab(INDEX_DO_JESTA)
                }
                R.id.nav_ask_jesta -> {
                    fragNavController.switchTab(INDEX_ASK_JESTA)
                }
                R.id.nav_status -> {
                    fragNavController.switchTab(INDEX_STATUS)
                }
                R.id.nav_settings -> {
                    fragNavController.switchTab(INDEX_SETTINGS)
                }
            }
            true
        }

        jesta_bottom_navigation.setOnNavigationItemReselectedListener {
            fragNavController.clearStack()
        }


    }

    fun showBottomNavigation() {
        val containerParams = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        containerParams.addRule(RelativeLayout.ABOVE, R.id.jesta_main_line_view)
        setBottomNavigationVisible(true)
        jesta_main_container.layoutParams = containerParams
    }

    private fun setBottomNavigationVisible(isVisible: Boolean) {
        jesta_bottom_navigation.visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
        jesta_main_line_view.visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
    }

    fun hideBottomNavigation() {
        val containerParams = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        setBottomNavigationVisible(false)
        jesta_main_container.layoutParams = containerParams
    }

    override fun onBackPressed() {
        if (fragNavController.currentFrag?.tag?.contains(LoginPathFragment::class.java.name) != null) {
            return
        }
        if (fragNavController.isRootFragment) return
        if (fragNavController.popFragment().not()) {
            super.onBackPressed()
        }
    }


    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        fragNavController.onSaveInstanceState(outState!!)

    }

    override fun onTabTransaction(fragment: Fragment?, index: Int) {
//        supportActionBar?.setDisplayHomeAsUpEnabled(fragNavController.isRootFragment.not())
    }


    override fun onFragmentTransaction(fragment: Fragment?, transactionType: FragNavController.TransactionType) {
//        supportActionBar?.setDisplayHomeAsUpEnabled(fragNavController.isRootFragment.not())
    }

    fun alertError(message: String? = "") {
        Alerter.create(MainActivity.instance)
            .setTitle("Oops! Something got wrong \uD83D\uDE35")
            .setText(if (message == null) "Sorry \uD83D\uDE4F, we got an internal error, please try again" else "Error: $message")
            .setBackgroundColorRes(R.color.colorPrimary)
            .setIcon(R.drawable.ic_jesta_diamond_normal)
            .show()
    }

    fun restart() {
        finish()
        startActivity(intent)
    }

    inner class OnTaskCompletion : OnCompleteListener<AuthResult> {
        override fun onComplete(task: Task<AuthResult>) {
            if (!task.isSuccessful) {
                MainActivity.instance.alertError(task.exception!!.message)
                Log.e(LoginPathFragment::class.java.simpleName, task.exception!!.message)
                return
            }
            sysManager.signInUser(task, applicationContext)

            MainActivity.instance.fragNavController.clearStack()
            MainActivity.instance.restart()
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        sysManager.firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this, OnTaskCompletion())
    }

    private fun handleFacebookToken(accessToken: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(accessToken.token)
        sysManager.firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this, OnTaskCompletion())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 101) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)

                val credential = GoogleAuthProvider.getCredential(account?.idToken, null)

                sysManager.firebaseAuth.signInWithCredential(credential)
                    .addOnCompleteListener { googleLoginTask ->
                        if (!googleLoginTask.isSuccessful) {
                            MainActivity.instance.alertError(googleLoginTask.exception?.message)
                            Log.e(LoginPathFragment::class.java.simpleName, googleLoginTask.exception?.message)
                            return@addOnCompleteListener
                        }
                        // Google Sign In was successful, authenticate with Firebase
                        val googleSignInAccount = task.getResult(ApiException::class.java)
                        if (googleSignInAccount != null) {
                            firebaseAuthWithGoogle(googleSignInAccount)
                        }
                        MainActivity.instance.fragNavController.clearStack()
                        MainActivity.instance.restart()
                    }
            } catch (e: ApiException) {
                alertError(e.message)
            }

        } else if (requestCode == 64206) { // facebook
            fbCallbackManager.onActivityResult(requestCode, resultCode, data)
        }
    }
}
