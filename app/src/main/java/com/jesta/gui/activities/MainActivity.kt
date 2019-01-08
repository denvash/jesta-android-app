package com.jesta.gui.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.jesta.R
import com.jesta.data.*
import com.jesta.data.chat.ChatManager
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

    companion object {
        private val TAG = MainActivity::class.java.simpleName

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

    val fragNavController: FragNavController = FragNavController(supportFragmentManager, R.id.jesta_main_container)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.jesta_main_activity)

        instance = this

        val sysManager = SysManager(instance)

        KeyboardVisibilityEvent.setEventListener(this@MainActivity) { isKeyboardOpen ->
            if (isKeyboardOpen) hideBottomNavigation() else showBottomNavigation()
        }

                fragNavController.apply {
                    transactionListener = this@MainActivity
                    rootFragmentListener = this@MainActivity
                    createEager = true
                    fragNavLogger = object : FragNavLogger {
                        override fun error(message: String, throwable: Throwable) {
                            Log.e(TAG, message, throwable)
                        }
                    }

            // Animation example
//            defaultTransactionOptions = FragNavTransactionOptions.newBuilder().customAnimations(
//                R.anim.slide_in_from_right,
//                R.anim.slide_out_to_left,
//                R.anim.slide_in_from_left,
//                R.anim.slide_out_to_right
//            ).build()

            fragmentHideStrategy = FragNavController.DETACH_ON_NAVIGATE_HIDE_ON_SWITCH
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
        jesta_bottom_navigation.visibility = if(isVisible) View.VISIBLE else View.INVISIBLE
        jesta_main_line_view.visibility = if(isVisible) View.VISIBLE else View.INVISIBLE
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
        // If we have a backstack, show the back button
//        supportActionBar?.setDisplayHomeAsUpEnabled(fragNavController.isRootFragment.not())
    }


    override fun onFragmentTransaction(fragment: Fragment?, transactionType: FragNavController.TransactionType) {
        //do fragmentary stuff. Maybe change statusTitle, I'm not going to tell you how to live your life
        // If we have a backstack, show the back button
//        supportActionBar?.setDisplayHomeAsUpEnabled(fragNavController.isRootFragment.not())
    }
}
