package com.jesta.doJesta

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.jesta.R
import kotlinx.android.synthetic.main.activity_do_jesta.*


class DoJestaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_do_jesta)

        setSupportActionBar(bottom_app_bar_do_jesta)

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> {
                val bottomNavDrawerFragment = BottomNavigationDrawerFragment()
                bottomNavDrawerFragment.show(supportFragmentManager, bottomNavDrawerFragment.tag)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}