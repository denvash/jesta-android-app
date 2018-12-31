package com.jesta

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.jesta.askJesta.AskJestaFragment
import com.jesta.doJesta.DoJestaFragment
import com.jesta.settings.SettingsFragment
import com.jesta.status.StatusFragment
import com.jesta.util.SysManager
import com.ncapdevi.fragnav.FragNavController
import com.ncapdevi.fragnav.FragNavLogger
import com.ncapdevi.fragnav.FragNavTransactionOptions
import kotlinx.android.synthetic.main.jesta_main_activity.*

const val INDEX_DO_JESTA = FragNavController.TAB1
const val INDEX_ASK_JESTA = FragNavController.TAB2
const val INDEX_STATUS = FragNavController.TAB3
const val INDEX_SETTINGS = FragNavController.TAB4

class MainActivity : AppCompatActivity(), FragNavController.RootFragmentListener,
    FragNavController.TransactionListener {

    companion object {
        private val TAG = MainActivity::class.java.simpleName

        lateinit var instance: MainActivity
            private set
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

    private val fragNavController: FragNavController = FragNavController(supportFragmentManager, R.id.container)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.jesta_main_activity)

        instance = this

        fragNavController.apply {
            transactionListener = this@MainActivity
            rootFragmentListener = this@MainActivity
            createEager = true
            fragNavLogger = object : FragNavLogger {
                override fun error(message: String, throwable: Throwable) {
                    Log.e(TAG, message, throwable)
                }
            }

            defaultTransactionOptions = FragNavTransactionOptions.newBuilder().customAnimations(
                R.anim.slide_in_from_right,
                R.anim.slide_out_to_left,
                R.anim.slide_in_from_left,
                R.anim.slide_out_to_right
            ).build()

            fragmentHideStrategy = FragNavController.DETACH_ON_NAVIGATE_HIDE_ON_SWITCH
        }

        fragNavController.initialize(INDEX_DO_JESTA, savedInstanceState)

        jesta_bottom_navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_do_jesta -> fragNavController.switchTab(INDEX_DO_JESTA)
                R.id.nav_ask_jesta -> fragNavController.switchTab(INDEX_ASK_JESTA)
                R.id.nav_status -> fragNavController.switchTab(INDEX_STATUS)
                R.id.nav_settings -> fragNavController.switchTab(INDEX_SETTINGS)
            }
            true
        }

        jesta_bottom_navigation.setOnNavigationItemReselectedListener {
            fragNavController.clearStack()
        }
    }

    override fun onBackPressed() {
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
        //do fragmentary stuff. Maybe change title, I'm not going to tell you how to live your life
        // If we have a backstack, show the back button
//        supportActionBar?.setDisplayHomeAsUpEnabled(fragNavController.isRootFragment.not())
    }
}
