package com.jesta.status

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jesta.R
import com.jesta.askJesta.AskJestaActivity
import com.jesta.doJesta.DoJestaActivity
import com.jesta.settings.SettingsActivity
import kotlinx.android.synthetic.main.frame_bottom_navigation_view.*

class StatusActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_status)

        jesta_bottom_navigation.selectedItemId = R.id.nav_status
        jesta_bottom_navigation.setOnNavigationItemSelectedListener {

            val intent = when (it.itemId) {
                R.id.nav_do_jesta -> {
                    Intent(this@StatusActivity, DoJestaActivity::class.java)
                }
                R.id.nav_ask_jesta -> {
                    Intent(this@StatusActivity, AskJestaActivity::class.java)
                }
                // Settings Activity
                else -> {
                    Intent(this@StatusActivity, SettingsActivity::class.java)
                }
            }
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            finish()
            startActivity(intent)
            true
        }
    }
}
