package com.jesta.gui.activities

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.gson.GsonBuilder
import com.jesta.R
import com.jesta.data.INDEX_ASK_JESTA
import com.jesta.data.INDEX_DO_JESTA
import com.jesta.data.INDEX_SETTINGS
import com.jesta.data.INDEX_STATUS
import com.jesta.gui.fragments.AskJestaFragment
import com.jesta.gui.fragments.DoJestaFragment
import com.jesta.gui.fragments.SettingsFragment
import com.jesta.gui.fragments.StatusFragment
import com.ncapdevi.fragnav.FragNavController
import com.ncapdevi.fragnav.FragNavLogger
import kotlinx.android.synthetic.main.jesta_main_activity.*



class MainActivity : AppCompatActivity(), FragNavController.RootFragmentListener,
    FragNavController.TransactionListener {

    companion object {
        private val TAG = MainActivity::class.java.simpleName

        val gson = GsonBuilder().setPrettyPrinting().create()!!
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

    val fragNavController: FragNavController = FragNavController(supportFragmentManager, R.id.container)

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

            // TODO: normal animations
//            defaultTransactionOptions = FragNavTransactionOptions.newBuilder().customAnimations(
//                R.anim.slide_in_from_right,
//                R.anim.slide_out_to_left,
//                R.anim.slide_in_from_left,
//                R.anim.slide_out_to_right
//            ).build()

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
        //do fragmentary stuff. Maybe change title, I'm not going to tell you how to live your life
        // If we have a backstack, show the back button
//        supportActionBar?.setDisplayHomeAsUpEnabled(fragNavController.isRootFragment.not())
    }
}
