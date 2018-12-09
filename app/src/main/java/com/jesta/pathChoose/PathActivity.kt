package com.jesta.pathChoose

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.jesta.MainActivity
import com.jesta.askJesta.AskJestaActivity
import com.jesta.R
import com.jesta.doJesta.DoJestaActivity
import com.jesta.util.SysManager
import kotlinx.android.synthetic.main.activity_path.*

class PathActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_path)

        // TODO: use current logged in user like this:
//        val sysManager = SysManager(this)
//        val currentUser = sysManager.currentUserFromDB()

        ask_jesta_button.setOnClickListener {
            val intent = Intent(this, AskJestaActivity::class.java)
            startActivity(intent)
        }

        do_jesta_button.setOnClickListener {
            val intent = Intent(this, DoJestaActivity::class.java)
            startActivity(intent)
        }

        logout_button.setOnClickListener {
            val sysManager = SysManager(this)
            sysManager.signOutUser(applicationContext)
            val intent = Intent(this, MainActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }

    }
}
